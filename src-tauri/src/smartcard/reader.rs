//! PC/SC reader enumeration (Refs #98).

use super::error::{map_pcsc, SmartcardError};

/// A detected smartcard reader.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReaderInfo {
    /// PC/SC reader name, e.g. `"ACS ACR39U ICC Reader 00 00"`.
    pub name: String,
}

/// List connected readers. Empty hardware → [`SmartcardError::NoReader`];
/// dead `pcscd` → [`SmartcardError::PcscUnavailable`].
pub fn list_readers() -> Result<Vec<ReaderInfo>, SmartcardError> {
    let ctx = pcsc::Context::establish(pcsc::Scope::User).map_err(|e| {
        map_pcsc(e).unwrap_or_else(|| SmartcardError::Transmission(format!("{e:?}")))
    })?;
    let mut buf = vec![0u8; 2048];
    let names = ctx.list_readers(&mut buf).map_err(|e| {
        map_pcsc(e).unwrap_or_else(|| SmartcardError::Transmission(format!("{e:?}")))
    })?;
    let readers = names
        .map(|n| ReaderInfo {
            name: n.to_string_lossy().into_owned(),
        })
        .collect::<Vec<_>>();
    if readers.is_empty() {
        return Err(SmartcardError::NoReader);
    }
    Ok(readers)
}

#[cfg(test)]
mod tests {
    use super::*;

    /// No assertion on hardware: on a reader-less box this must be a typed
    /// `NoReader`/`PcscUnavailable`, never a panic or a raw code.
    #[test]
    fn empty_box_yields_typed_error() {
        match list_readers() {
            Ok(readers) => assert!(
                readers.iter().all(|r| !r.name.is_empty()),
                "reader names must be non-empty"
            ),
            Err(SmartcardError::NoReader) | Err(SmartcardError::PcscUnavailable) => {}
            Err(other) => panic!("untyped error leaked: {other}"),
        }
    }
}
