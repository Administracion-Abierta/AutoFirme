//! Revocation transport: fetch OCSP/CRL bytes outside the browser (Refs #136).
//!
//! CORS makes OCSP/CRL unreachable from a page, so the desktop shell fetches
//! through the OS network stack via `curl` (present on every supported target;
//! no new crates). Only the URL the certificate advertises is fetched; the
//! request body is the caller's OCSP bytes (serial + issuer hashes) or empty
//! for a CRL download. No user-agent spoofing: the default `curl` agent.
//! Cap: 64 KiB headers + 1 MiB body, 20 s timeout. Offline/unreachable surfaces
//! as `Err`, which the TS side reports as `unknown` — never `good`.

use std::process::Command;

/// Maximum accepted response body (OCSP responses are ~1 KiB, CRLs rarely > 100 KiB).
const MAX_BODY_BYTES: usize = 1024 * 1024;
/// Network deadline for the whole fetch.
const TIMEOUT_SECS: &str = "20";

/// Fetch revocation bytes: POST `body` for OCSP, GET for CRL (`body` null).
#[tauri::command]
pub async fn fetch_revocation_bytes(
    url: String,
    body: Option<Vec<u8>>,
    content_type: Option<String>,
) -> Result<Vec<u8>, String> {
    let checked = check_url(&url)?;
    let request_body = body.unwrap_or_default();
    if request_body.len() > MAX_BODY_BYTES {
        return Err("request body exceeds 1 MiB".to_string());
    }
    tauri::async_runtime::spawn_blocking(move || {
        fetch_blocking(&checked, &request_body, content_type.as_deref())
    })
    .await
    .map_err(|error| error.to_string())?
}

/// Only http(s) URLs the certificate advertised; refuse file/data/javascript schemes.
fn check_url(url: &str) -> Result<String, String> {
    if url.len() > 2048 {
        return Err("URL exceeds 2048 characters".to_string());
    }
    let lower = url.to_ascii_lowercase();
    if !(lower.starts_with("http://") || lower.starts_with("https://")) {
        return Err("refused non-http(s) URL".to_string());
    }
    if url.contains([' ', '\t', '\n', '\r']) {
        return Err("URL contains whitespace".to_string());
    }
    Ok(url.to_string())
}

fn fetch_blocking(url: &str, body: &[u8], content_type: Option<&str>) -> Result<Vec<u8>, String> {
    let mut command = Command::new("curl");
    command
        .args([
            "--silent",
            "--show-error",
            "--fail",
            "--location",
            "--max-redirs",
            "3",
        ])
        .args([
            "--max-time",
            TIMEOUT_SECS,
            "--max-filesize",
            &MAX_BODY_BYTES.to_string(),
        ])
        .args(["--proto", "=http,https"]);
    if body.is_empty() {
        command.arg("--get");
    } else {
        command.args(["--request", "POST", "--data-binary", "@-"]);
        command.arg("--header");
        command.arg(format!(
            "Content-Type: {}",
            content_type.unwrap_or("application/ocsp-request")
        ));
    }
    command.arg("--").arg(url);
    let mut child = command
        .stdin(std::process::Stdio::piped())
        .stdout(std::process::Stdio::piped())
        .stderr(std::process::Stdio::piped())
        .spawn()
        .map_err(|error| format!("fetch failed: {error}"))?;
    if !body.is_empty() {
        use std::io::Write;
        child
            .stdin
            .take()
            .ok_or_else(|| "fetch failed: stdin unavailable".to_string())?
            .write_all(body)
            .map_err(|error| format!("fetch failed: {error}"))?;
    }
    let output = child
        .wait_with_output()
        .map_err(|error| format!("fetch failed: {error}"))?;
    if !output.status.success() {
        let detail = String::from_utf8_lossy(&output.stderr);
        return Err(format!("fetch failed: {}", detail.trim()));
    }
    if output.stdout.len() > MAX_BODY_BYTES {
        return Err("response exceeds 1 MiB".to_string());
    }
    Ok(output.stdout)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn refuses_non_http_urls() {
        for bad in [
            "file:///etc/passwd",
            "data:text/plain,hi",
            "javascript:alert(1)",
            "ftp://example.com/crl.pem",
            "https://example.com/has space",
        ] {
            assert!(check_url(bad).is_err(), "{bad}");
        }
        assert!(check_url("https://ocsp.example.com/").is_ok());
        assert!(check_url("http://crl.example.com/x.crl").is_ok());
    }

    #[test]
    fn unreachable_host_is_an_error_not_good() {
        // Reserved TEST-NET-1: unroutable, so this exercises the offline path
        // without touching any real responder.
        let result = fetch_blocking("https://192.0.2.1/ocsp", &[], None);
        assert!(result.is_err());
    }

    #[test]
    fn oversize_request_body_rejected() {
        let big = vec![0u8; MAX_BODY_BYTES + 1];
        let result = tauri::async_runtime::block_on(fetch_revocation_bytes(
            "https://example.com/".to_string(),
            Some(big),
            None,
        ));
        assert!(result.is_err());
    }
}
