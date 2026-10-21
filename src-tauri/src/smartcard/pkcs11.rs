//! OpenSC PKCS#11 sign path (Refs #98).
//!
//! Digest in, raw signature out. This is the cheap path the issue exists to
//! prove: if `opensc-pkcs11.so` signs a DNIe digest, CWA-14890 secure
//! messaging is unnecessary. No files, no documents, no PIN storage — the
//! caller owns the PIN and the output bytes.

use std::path::Path;

use cryptoki::context::{CInitializeArgs, CInitializeFlags, Pkcs11};
use cryptoki::error::{Error as CkError, RvError};
use cryptoki::mechanism::Mechanism;
use cryptoki::object::{Attribute, AttributeType, ObjectClass};
use cryptoki::session::{Session, UserType};
use cryptoki::slot::Slot;
use cryptoki::types::AuthPin;

use super::error::SmartcardError;
use super::opensc::find_opensc_module;

/// Loaded PKCS#11 module, ready to open token sessions.
pub struct Pkcs11Signer {
    ctx: Pkcs11,
}

impl Pkcs11Signer {
    /// Load the OpenSC module, autodetected unless `module` is given.
    pub fn open(module: Option<&Path>) -> Result<Self, SmartcardError> {
        let path = match module {
            Some(p) => p.to_path_buf(),
            None => find_opensc_module()?,
        };
        let ctx = Pkcs11::new(&path).map_err(|e| map_pkcs11(&e))?;
        match ctx.initialize(CInitializeArgs::new(CInitializeFlags::OS_LOCKING_OK)) {
            Ok(()) => {}
            // A second handle to the same library is already usable.
            Err(CkError::Pkcs11(RvError::CryptokiAlreadyInitialized, _)) => {}
            Err(e) => return Err(map_pkcs11(&e)),
        }
        Ok(Self { ctx })
    }

    /// Slots holding a card. Empty → [`SmartcardError::CardAbsent`].
    pub fn token_slots(&self) -> Result<Vec<Slot>, SmartcardError> {
        let slots = self.ctx.get_slots_with_token().map_err(|e| map_pkcs11(&e))?;
        if slots.is_empty() {
            return Err(SmartcardError::CardAbsent);
        }
        Ok(slots)
    }

    /// Labels of the private keys the card exposes. Lets the caller show
    /// "which key am I signing with" without guessing labels.
    pub fn signing_key_labels(
        &self,
        slot: Slot,
        pin: &AuthPin,
    ) -> Result<Vec<String>, SmartcardError> {
        let session = self.open_logged_in(slot, pin)?;
        let keys = session
            .find_objects(&[Attribute::Class(ObjectClass::PRIVATE_KEY)])
            .map_err(|e| map_pkcs11(&e))?;
        let mut labels = Vec::with_capacity(keys.len());
        for key in keys {
            if let Ok(attrs) = session.get_attributes(key, &[AttributeType::Label]) {
                if let Some(Attribute::Label(bytes)) = attrs.into_iter().next() {
                    labels.push(String::from_utf8_lossy(&bytes).trim().to_string());
                }
            }
        }
        Ok(labels)
    }

    /// Sign `digest` on `slot`'s card. `key_label` selects the private key;
    /// `None` takes the card's first one.
    ///
    /// The returned bytes are the raw signature. A card pulled mid-sign yields
    /// [`SmartcardError::CardRemovedMidSession`] and no bytes — nothing is
    /// written to disk anywhere in this layer.
    pub fn sign_digest(
        &self,
        slot: Slot,
        key_label: Option<&str>,
        digest: &[u8],
        pin: &AuthPin,
    ) -> Result<Vec<u8>, SmartcardError> {
        if digest.is_empty() {
            return Err(SmartcardError::Transmission("empty digest".into()));
        }
        let mechanism = mechanism_for_digest(digest.len())?;
        let session = self.open_logged_in(slot, pin)?;

        let mut template = vec![Attribute::Class(ObjectClass::PRIVATE_KEY)];
        if let Some(label) = key_label {
            template.push(Attribute::Label(label.as_bytes().to_vec()));
        }
        let keys = session.find_objects(&template).map_err(|e| map_pkcs11(&e))?;
        let key = *keys.first().ok_or_else(|| {
            // An empty list is ambiguous: no such key, or the card is gone.
            // Ask the resource layer which one it is before blaming the key.
            if self.token_gone(slot) {
                return SmartcardError::CardRemovedMidSession;
            }
            SmartcardError::UnsupportedCard(format!(
                "card exposes no private key{}",
                match key_label {
                    Some(l) => format!(" labelled {l:?}"),
                    None => String::new(),
                }
            ))
        })?;

        session
            .sign(&mechanism, key, digest)
            .map_err(|e| map_pkcs11(&e))
    }

    /// True when the resource manager no longer reports a token in `slot`,
    /// i.e. the card was pulled rather than the key being missing.
    fn token_gone(&self, slot: Slot) -> bool {
        match self.ctx.get_slot_info(slot) {
            Ok(info) => !info.token_present(),
            Err(_) => true,
        }
    }

    fn open_logged_in(&self, slot: Slot, pin: &AuthPin) -> Result<Session, SmartcardError> {
        let session = self.ctx.open_rw_session(slot).map_err(|e| map_pkcs11(&e))?;
        match session.login(UserType::User, Some(pin)) {
            Ok(()) => {}
            // Another desktop session (or our own earlier login) already holds
            // the token's single user login; the session is usable as-is.
            Err(CkError::Pkcs11(RvError::UserAlreadyLoggedIn, _)) => {}
            Err(e) => return Err(map_pkcs11(&e)),
        }
        Ok(session)
    }
}

/// DNIe 3.x signing keys are RSA, so the digest length picks the mechanism.
/// OpenSC wraps the digest into DigestInfo for the `*_RSA_PKCS` variants.
fn mechanism_for_digest(len: usize) -> Result<Mechanism<'static>, SmartcardError> {
    match len {
        32 => Ok(Mechanism::Sha256RsaPkcs),
        48 => Ok(Mechanism::Sha384RsaPkcs),
        64 => Ok(Mechanism::Sha512RsaPkcs),
        other => Err(SmartcardError::UnsupportedCard(format!(
            "digest length {other} not supported (expected 32/48/64)"
        ))),
    }
}

/// Map a raw PKCS#11 return value onto the typed surface.
pub fn map_rv(rv: &RvError) -> SmartcardError {
    match rv {
        RvError::TokenNotRecognized => {
            SmartcardError::UnsupportedCard("OpenSC did not recognise the card".into())
        }
        RvError::TokenNotPresent | RvError::SlotIdInvalid | RvError::DeviceError => {
            SmartcardError::CardAbsent
        }
        RvError::DeviceRemoved | RvError::SessionHandleInvalid | RvError::SessionClosed => {
            SmartcardError::CardRemovedMidSession
        }
        RvError::PinLocked => SmartcardError::PinBlocked,
        RvError::PinIncorrect => {
            SmartcardError::Transmission("PIN incorrect (retry count belongs to #99)".into())
        }
        RvError::MechanismInvalid | RvError::KeyHandleInvalid | RvError::KeyTypeInconsistent => {
            SmartcardError::UnsupportedCard(format!("{rv:?}"))
        }
        other => SmartcardError::Transmission(format!("{other:?}")),
    }
}

/// Map a cryptoki failure onto the typed surface.
pub fn map_pkcs11(err: &CkError) -> SmartcardError {
    match err {
        CkError::LibraryLoading(_) => SmartcardError::OpenscModuleNotFound,
        CkError::Pkcs11(rv, _) => map_rv(rv),
        other => SmartcardError::Transmission(format!("{other:?}")),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use cryptoki::mechanism::MechanismType;

    #[test]
    fn digest_length_selects_mechanism() {
        let ty = |len| mechanism_for_digest(len).unwrap().mechanism_type();
        assert_eq!(ty(32), MechanismType::SHA256_RSA_PKCS);
        assert_eq!(ty(48), MechanismType::SHA384_RSA_PKCS);
        assert_eq!(ty(64), MechanismType::SHA512_RSA_PKCS);
        assert!(matches!(
            mechanism_for_digest(20),
            Err(SmartcardError::UnsupportedCard(_))
        ));
    }

    #[test]
    fn pkcs11_codes_map_to_typed_errors() {
        assert_eq!(
            map_rv(&RvError::DeviceRemoved),
            SmartcardError::CardRemovedMidSession
        );
        assert_eq!(map_rv(&RvError::TokenNotPresent), SmartcardError::CardAbsent);
        assert_eq!(map_rv(&RvError::PinLocked), SmartcardError::PinBlocked);
        assert!(matches!(
            map_rv(&RvError::TokenNotRecognized),
            SmartcardError::UnsupportedCard(_)
        ));
    }

    /// No card on this box: the typed error must be `CardAbsent`, not a panic
    /// and not a raw cryptoki code. On a carded box the slots must be real.
    #[test]
    fn empty_box_yields_typed_error() {
        let Ok(signer) = Pkcs11Signer::open(None) else {
            // No OpenSC module installed at all is also a typed outcome.
            return;
        };
        match signer.token_slots() {
            Ok(slots) => assert!(!slots.is_empty()),
            Err(SmartcardError::CardAbsent) => {}
            Err(other) => panic!("untyped error leaked: {other}"),
        }
    }

    #[test]
    fn missing_module_is_typed() {
        match Pkcs11Signer::open(Some(Path::new("/nonexistent-opensc-xyz.so"))) {
            Err(SmartcardError::OpenscModuleNotFound) => {}
            Err(other) => panic!("wrong typed error: {other}"),
            Ok(_) => panic!("loading a nonexistent module must fail"),
        }
    }
}
