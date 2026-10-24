//! Per-install TLS identity for the loopback sign server (P2.2a, Refs #104).
//!
//! Reference (read-only): `SecureSocketUtils` on tag `reference/decompile-1.9`
//! loads `autofirma.pfx` (PKCS#12, shared password) and builds a `TLSv1`
//! context. Intent reproduced, primitives not: a fresh self-signed cert is
//! generated per install with `rcgen` (SANs `127.0.0.1` + `localhost`), stored
//! as PEM (`<dir>/autofirme-loopback.crt`) + key (`<dir>/autofirme-loopback.key`,
//! mode 0600), and served through rustls with TLS 1.2+ only. The bundled-PKCS#12
//! with a well-known password pattern is deliberately not reproduced.
//!
//! Trust-store install (needs admin, like real AutoFirma) is a documented
//! command, not code — run at install time against [`CERT_FILE_NAME`]:
//! `sudo trust anchor --store <dir>/autofirme-loopback.crt` (Fedora/RHEL) or
//! `sudo cp <crt> /usr/local/share/ca-certificates/ && sudo update-ca-certificates`
//! (Debian/Ubuntu).

use std::fs;
use std::io;
use std::path::{Path, PathBuf};

/// PEM certificate file name inside the install data dir.
pub const CERT_FILE_NAME: &str = "autofirme-loopback.crt";
/// PEM private-key file name inside the install data dir.
pub const KEY_FILE_NAME: &str = "autofirme-loopback.key";

/// A loaded server identity: DER bytes, ready for rustls.
pub struct TlsIdentity {
    /// DER-encoded self-signed certificate (SANs: 127.0.0.1, localhost).
    pub cert_der: Vec<u8>,
    /// DER-encoded PKCS#8 private key.
    pub key_der: Vec<u8>,
}

/// Directory layout for one install's identity.
pub fn paths(dir: &Path) -> (PathBuf, PathBuf) {
    (dir.join(CERT_FILE_NAME), dir.join(KEY_FILE_NAME))
}

/// Load the identity from `dir`, generating and persisting it on first run.
/// Same directory on the next start yields byte-identical DER (reload path).
pub fn load_or_generate(dir: &Path) -> io::Result<TlsIdentity> {
    let (cert_path, key_path) = paths(dir);
    if cert_path.exists() && key_path.exists() {
        let cert_pem = fs::read(&cert_path)?;
        let key_pem = fs::read(&key_path)?;
        return from_pem(&cert_pem, &key_pem)
            .map_err(|detail| io::Error::new(io::ErrorKind::InvalidData, detail));
    }
    fs::create_dir_all(dir)?;
    let certified =
        rcgen::generate_simple_self_signed(vec!["127.0.0.1".to_owned(), "localhost".to_owned()])
            .map_err(|error| io::Error::new(io::ErrorKind::Other, error.to_string()))?;
    let cert_pem = certified.cert.pem();
    let key_pem = certified.signing_key.serialize_pem();
    fs::write(&cert_path, cert_pem.as_bytes())?;
    fs::write(&key_path, key_pem.as_bytes())?;
    #[cfg(unix)]
    {
        use std::os::unix::fs::PermissionsExt;
        fs::set_permissions(&key_path, fs::Permissions::from_mode(0o600))?;
    }
    from_pem(cert_pem.as_bytes(), key_pem.as_bytes())
        .map_err(|detail| io::Error::new(io::ErrorKind::InvalidData, detail))
}

fn pem_block(pem: &[u8], label: &str) -> Result<Vec<u8>, String> {
    let text = std::str::from_utf8(pem).map_err(|_| format!("non-UTF8 {label} PEM"))?;
    let begin = format!("-----BEGIN {label}-----");
    let end = format!("-----END {label}-----");
    let start = text
        .find(&begin)
        .ok_or_else(|| format!("missing {label} block"))?
        + begin.len();
    let stop = text.find(&end).ok_or_else(|| format!("missing {label} tail"))?;
    // Minimal base64 (PEM alphabet only); std-only so no new dependency.
    let mut bits = 0u32;
    let mut collected = 0u32;
    let mut out = Vec::new();
    for byte in text[start..stop].bytes() {
        let digit = match byte {
            b'A'..=b'Z' => byte - b'A',
            b'a'..=b'z' => byte - b'a' + 26,
            b'0'..=b'9' => byte - b'0' + 52,
            b'+' => 62,
            b'/' => 63,
            _ => continue, // whitespace/newlines skipped
        };
        bits = (bits << 6) | u32::from(digit);
        collected += 6;
        if collected >= 8 {
            collected -= 8;
            out.push((bits >> collected) as u8);
            bits &= (1 << collected) - 1;
        }
    }
    Ok(out)
}

fn from_pem(cert_pem: &[u8], key_pem: &[u8]) -> Result<TlsIdentity, String> {
    Ok(TlsIdentity {
        cert_der: pem_block(cert_pem, "CERTIFICATE")?,
        key_der: pem_block(key_pem, "PRIVATE KEY")?,
    })
}

/// Build the rustls server config: TLS 1.2+ only (no TLS 1.0/1.1 like 1.9).
pub fn server_config(identity: &TlsIdentity) -> Result<rustls::ServerConfig, String> {
    let cert = rustls::pki_types::CertificateDer::from(identity.cert_der.clone());
    // rcgen's `serialize_der` is PKCS#8, so this variant always fits.
    let key = rustls::pki_types::PrivatePkcs8KeyDer::from(identity.key_der.clone());
    rustls::ServerConfig::builder()
        .with_no_client_auth()
        .with_single_cert(vec![cert], key.into())
        .map_err(|error| error.to_string())
}

#[cfg(test)]
mod tests {
    use super::*;

    fn scratch_dir(name: &str) -> PathBuf {
        let dir = std::env::temp_dir().join(format!("autofirme-tls-test-{name}-{}", std::process::id()));
        let _ = fs::remove_dir_all(&dir);
        dir
    }

    #[test]
    fn generate_then_reload_is_byte_identical() {
        let dir = scratch_dir("reload");
        let first = load_or_generate(&dir).expect("generate");
        let second = load_or_generate(&dir).expect("reload");
        assert_eq!(first.cert_der, second.cert_der);
        assert_eq!(first.key_der, second.key_der);
        assert!(!first.cert_der.is_empty() && !first.key_der.is_empty());
        let _ = fs::remove_dir_all(&dir);
    }

    #[test]
    fn server_config_builds_from_generated_identity() {
        let dir = scratch_dir("config");
        let identity = load_or_generate(&dir).expect("generate");
        server_config(&identity).expect("rustls server config");
        let _ = fs::remove_dir_all(&dir);
    }
}
