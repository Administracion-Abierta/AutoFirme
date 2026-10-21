//! RAII PC/SC session guard (Refs #98).
//!
//! Owns one connected card and does raw APDU round-trips. Digest signing goes
//! through [`super::pkcs11`] (PKCS#11 via OpenSC), not through a hand-rolled
//! APDU sequence — that is the whole point of this issue.
//!
//! Dropping the session releases the card. Callers holding bytes after
//! [`SmartcardError::CardRemovedMidSession`] must discard them; this layer
//! never writes files, so "no partial file" holds by construction.

use super::error::{map_pcsc, SmartcardError};

/// Connected card session. Drop = disconnect.
pub struct SmartcardSession {
    card: pcsc::Card,
}

impl SmartcardSession {
    /// Connect to `reader` in shared mode. Absent card →
    /// [`SmartcardError::CardAbsent`]; pulled mid-connect →
    /// [`SmartcardError::CardRemovedMidSession`].
    pub fn connect(reader: &str) -> Result<Self, SmartcardError> {
        let reader = std::ffi::CString::new(reader)
            .map_err(|_| SmartcardError::Transmission("reader name contains NUL".into()))?;
        let ctx = pcsc::Context::establish(pcsc::Scope::User)
            .map_err(|e| map_pcsc(e).unwrap_or(SmartcardError::Transmission(format!("{e:?}"))))?;
        let card = ctx
            .connect(&reader, pcsc::ShareMode::Shared, pcsc::Protocols::ANY)
            .map_err(|e| map_pcsc(e).unwrap_or(SmartcardError::Transmission(format!("{e:?}"))))?;
        Ok(Self { card })
    }

    /// Raw APDU round-trip. Removal mid-transmit →
    /// [`SmartcardError::CardRemovedMidSession`].
    pub fn transmit(&self, apdu: &[u8]) -> Result<Vec<u8>, SmartcardError> {
        if apdu.is_empty() {
            return Err(SmartcardError::Transmission("empty APDU".into()));
        }
        let mut rapdu_buf = [0u8; pcsc::MAX_BUFFER_SIZE];
        let rapdu = self
            .card
            .transmit(apdu, &mut rapdu_buf)
            .map_err(|e| map_pcsc(e).unwrap_or(SmartcardError::Transmission(format!("{e:?}"))))?;
        Ok(rapdu.to_vec())
    }
}
