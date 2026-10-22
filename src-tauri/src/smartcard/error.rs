//! Typed smartcard errors with plain-language messages (Refs #98).
//!
//! Every failure the UI can act on gets its own variant — no stringly-typed
//! PC/SC codes leak past this module. Mid-flow card removal maps to
//! [`SmartcardError::CardRemovedMidSession`]; callers must drop any partial
//! output when they see it (this layer never writes files itself).

use std::fmt;

/// Errors from the PC/SC session layer.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum SmartcardError {
    /// No smartcard reader detected on the system.
    NoReader,
    /// Reader present but no card inserted.
    CardAbsent,
    /// Card pulled mid-operation; any partial signature bytes are unusable.
    CardRemovedMidSession,
    /// Card present but not supported by the installed OpenSC build.
    UnsupportedCard(String),
    /// PC/SC service (`pcscd`) unreachable.
    PcscUnavailable,
    /// No OpenSC PKCS#11 module found at any known install path.
    OpenscModuleNotFound,
    /// Transport-level failure carrying the underlying detail.
    Transmission(String),
    /// PIN entry cancelled by the user (owned by `pin.rs`, sibling #99).
    PinCancelled,
    /// Card blocked after PIN retry exhaustion (owned by `pin.rs`, #99).
    PinBlocked,
    /// PIN attempts remaining before the card blocks (owned by `pin.rs`, #99).
    PinAttemptsRemaining(u8),
}

impl fmt::Display for SmartcardError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::NoReader => write!(f, "no smartcard reader detected"),
            Self::CardAbsent => write!(f, "no card in reader"),
            Self::CardRemovedMidSession => {
                write!(f, "card removed during operation; partial output discarded")
            }
            Self::UnsupportedCard(detail) => {
                write!(f, "card not supported by installed OpenSC ({detail})")
            }
            Self::PcscUnavailable => write!(f, "smartcard service (pcscd) unavailable"),
            Self::OpenscModuleNotFound => write!(f, "OpenSC PKCS#11 module not found"),
            Self::Transmission(detail) => write!(f, "card transmission failed ({detail})"),
            Self::PinCancelled => write!(f, "PIN entry cancelled"),
            Self::PinBlocked => write!(f, "card blocked: PIN retries exhausted, PUK required"),
            Self::PinAttemptsRemaining(n) => {
                write!(f, "wrong PIN: {n} attempt(s) remaining before card blocks")
            }
        }
    }
}

impl std::error::Error for SmartcardError {}

/// Map a raw `pcsc::Error` to the typed surface. `None` means the code has no
/// clean mapping and the caller must wrap it as [`SmartcardError::Transmission`].
pub fn map_pcsc(code: pcsc::Error) -> Option<SmartcardError> {
    use pcsc::Error as P;
    match code {
        P::NoReadersAvailable => Some(SmartcardError::NoReader),
        // A reader *name* that does not exist (`SCardConnect` with a stale or
        // bogus name) is "no such reader", not a transport failure: the issue
        // names that state explicitly and the UI must not render a raw code.
        P::UnknownReader => Some(SmartcardError::NoReader),
        P::NoSmartcard => Some(SmartcardError::CardAbsent),
        // Card inserted but its type is unknown to PC/SC/OpenSC.
        P::UnknownCard => Some(SmartcardError::UnsupportedCard(format!("{code:?}"))),
        P::RemovedCard => Some(SmartcardError::CardRemovedMidSession),
        // `SCARD_F_COMM_ERROR` is the client<->resource-manager channel, i.e.
        // pcscd itself (down, or refusing this client); card-side removal
        // arrives as `RemovedCard`.
        P::NoService | P::ServiceStopped | P::CommError => Some(SmartcardError::PcscUnavailable),
        P::NotTransacted | P::ReaderUnavailable => {
            Some(SmartcardError::Transmission(format!("{code:?}")))
        }
        _ => None,
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn messages_are_plain_language() {
        assert_eq!(
            SmartcardError::NoReader.to_string(),
            "no smartcard reader detected"
        );
        assert_eq!(
            SmartcardError::CardRemovedMidSession.to_string(),
            "card removed during operation; partial output discarded"
        );
        assert!(SmartcardError::PinBlocked.to_string().contains("PUK"));
        assert!(SmartcardError::PinAttemptsRemaining(2)
            .to_string()
            .contains('2'));
    }

    /// Live-verified on this box: `SmartcardSession::connect` with a bogus
    /// reader name made PC/SC answer `SCARD_E_UNKNOWN_READER`, which used to
    /// leak as `Transmission("UnknownReader")`. The issue requires the
    /// "no reader" state to be typed.
    #[test]
    fn unknown_reader_and_card_codes_are_typed() {
        assert_eq!(
            map_pcsc(pcsc::Error::UnknownReader),
            Some(SmartcardError::NoReader)
        );
        assert!(matches!(
            map_pcsc(pcsc::Error::UnknownCard),
            Some(SmartcardError::UnsupportedCard(_))
        ));
    }

    #[test]
    fn pcsc_codes_map_to_typed_errors() {
        assert_eq!(
            map_pcsc(pcsc::Error::NoReadersAvailable),
            Some(SmartcardError::NoReader)
        );
        assert_eq!(
            map_pcsc(pcsc::Error::NoSmartcard),
            Some(SmartcardError::CardAbsent)
        );
        assert_eq!(
            map_pcsc(pcsc::Error::RemovedCard),
            Some(SmartcardError::CardRemovedMidSession)
        );
        assert_eq!(
            map_pcsc(pcsc::Error::NoService),
            Some(SmartcardError::PcscUnavailable)
        );
        assert_eq!(
            map_pcsc(pcsc::Error::CommError),
            Some(SmartcardError::PcscUnavailable)
        );
        assert!(matches!(
            map_pcsc(pcsc::Error::ReaderUnavailable),
            Some(SmartcardError::Transmission(_))
        ));
    }
}
