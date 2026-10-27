//! OpenSC PKCS#11 module autodetect (Refs #98).
//!
//! Probe result on this box (2026-10-21, EL9): OpenSC **0.23.0** via
//! `dnf` (`opensc-0.23.0-6.el9`, pcsc-lite 1.9.4) — not the 0.25 the issue
//! hoped for. Module live at `/usr/lib64/opensc-pkcs11.so`, cryptoki 3.0,
//! but `pkcs11-tool -L` reports **no slots** (no reader/hardware).
//! Cheapest-path verdict stays open until real DNIe hardware signs.

use std::path::PathBuf;

use super::error::SmartcardError;

/// Well-known OpenSC module locations, distro order first.
pub const OPENSC_CANDIDATES: &[&str] = &[
    "/usr/lib64/pkcs11/opensc-pkcs11.so", // RHEL/Fedora p11-kit dir
    "/usr/lib64/opensc-pkcs11.so",        // RHEL/Fedora x86_64
    "/usr/lib/x86_64-linux-gnu/opensc-pkcs11.so", // Debian/Ubuntu
    "/usr/lib/pkcs11/opensc-pkcs11.so",   // Debian p11-kit dir
    "/usr/lib/opensc-pkcs11.so",          // fallback prefix
    "/usr/local/lib/opensc-pkcs11.so",    // source builds
    "/opt/homebrew/lib/opensc-pkcs11.so", // macOS arm64
    "/Library/OpenSC/lib/opensc-pkcs11.so", // macOS installer
];

/// First existing candidate wins; none → [`SmartcardError::OpenscModuleNotFound`].
pub fn find_opensc_module() -> Result<PathBuf, SmartcardError> {
    find_in(OPENSC_CANDIDATES)
}

fn find_in(candidates: &[&str]) -> Result<PathBuf, SmartcardError> {
    candidates
        .iter()
        .map(PathBuf::from)
        .find(|p| p.is_file())
        .ok_or(SmartcardError::OpenscModuleNotFound)
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Write;

    #[test]
    fn finds_first_existing_candidate() {
        let dir = std::env::temp_dir().join("autofirme-opensc-probe");
        std::fs::create_dir_all(&dir).unwrap();
        let fake = dir.join("opensc-pkcs11.so");
        std::fs::File::create(&fake)
            .unwrap()
            .write_all(b"fake")
            .unwrap();
        let missing = dir.join("missing.so");
        let paths = [
            missing.to_str().unwrap().to_string(),
            fake.to_str().unwrap().to_string(),
        ];
        let refs: Vec<&str> = paths.iter().map(String::as_str).collect();
        assert_eq!(find_in(&refs).unwrap(), fake);
        std::fs::remove_dir_all(&dir).ok();
    }

    #[test]
    fn none_existing_is_typed() {
        assert_eq!(
            find_in(&["/nonexistent-a-xyz.so", "/nonexistent-b-xyz.so"]),
            Err(SmartcardError::OpenscModuleNotFound)
        );
    }
}
