//! Protocol 3 (legacy socket) frame handling (P2.2a, Refs #104).
//!
//! Reference (read-only): `AfirmaWebSocketServer` / `ProtocolInvocationLauncher` /
//! `UrlParameters` on tag `reference/decompile-1.9`. The wire contract reproduced here:
//! one WebSocket text frame in, one text frame out. `echo=<anything>` answers `OK`;
//! anything else must be an `afirma://<op>?<params>` URL whose query carries `v`
//! (protocol version), `ports` (comma-separated, absolute ints, validated not used —
//! this server is already bound) and the op params (`dat` xor `fileid` for sign ops).
//! Errors are `SAF_XX: <detail>` like `getErrorMessage` (`code + ": " + text`).
//!
//! Deliberate deviations from 1.9 (clean-room, not a port):
//! - No `idsession` enforcement — that is the v4 addition (`AfirmaWebSocketServerV4`);
//!   v3 has no address/session check. Binding to 127.0.0.1 is the access control.
//! - `v` present and != 3 is rejected (`SAF_21`) instead of silently served: the
//!   sibling protocol-4 issue owns v4, and a silent fallback is failure mode #5.
//! - Success is the literal `OK` (transport accepted the frame). Crypto dispatch
//!   (real signatures) lands with the KeyProvider-side work, not here.

/// Default loopback port (`DEFAULT_WEBSOCKET_PORT` in the reference).
pub const DEFAULT_WEBSOCKET_PORT: u16 = 63117;
/// The protocol version this module speaks.
pub const PROTOCOL_VERSION: u32 = 3;

const ECHO_PREFIX: &str = "echo=";
const SCHEME: &str = "afirma://";
const OK: &str = "OK";

/// Handle one text frame, returning the exact response bytes.
/// Raw frame logging exists in debug builds only (issue scope).
pub fn handle_frame(frame: &str) -> String {
    #[cfg(debug_assertions)]
    eprintln!("[proto3] RX {} bytes: {}", frame.len(), preview(frame));
    let response = dispatch(frame);
    #[cfg(debug_assertions)]
    eprintln!("[proto3] TX {} bytes: {}", response.len(), preview(&response));
    response
}

#[cfg(debug_assertions)]
fn preview(frame: &str) -> String {
    const MAX: usize = 160;
    let mut text: String = frame.chars().take(MAX).collect();
    if frame.len() > MAX {
        text.push('…');
    }
    text
}

fn err(code: &str, detail: &str) -> String {
    format!("{code}: {detail}")
}

fn dispatch(frame: &str) -> String {
    if frame.starts_with(ECHO_PREFIX) {
        return OK.to_owned();
    }
    if frame.is_empty() {
        return err("SAF_01", "empty request");
    }
    if !frame.starts_with(SCHEME) {
        return err("SAF_02", "invocation URL must start with 'afirma://'");
    }
    let rest = &frame[SCHEME.len()..];
    let (op, query) = match rest.find('?') {
        Some(index) => (&rest[..index], &rest[index + 1..]),
        None => (rest, ""),
    };
    let op = op.trim_end_matches('/');
    let params = parse_query(query);
    if let Some(version) = params.get("v") {
        if version_number(version) != Some(PROTOCOL_VERSION) {
            return err(
                "SAF_21",
                &format!("unsupported protocol version '{version}'; this server speaks 3"),
            );
        }
    }
    if let Some(ports) = params.get("ports") {
        if parse_ports(ports).is_none() {
            return err("SAF_03", "ports must be a comma-separated integer list");
        }
    }
    match op {
        "sign" | "cosign" | "countersign" | "signandsave" => {
            if !params.contains_key("dat") && !params.contains_key("fileid") {
                return err("SAF_03", "sign operations need 'dat' or 'fileid'");
            }
            OK.to_owned()
        }
        "selectcert" | "save" | "load" | "batch" | "websocket" => OK.to_owned(),
        _ => err("SAF_04", &format!("unsupported operation '{op}'")),
    }
}

/// Split `a=1&b=2` with `URLDecoder` semantics (`+` is a space, `%XX` escapes).
fn parse_query(query: &str) -> std::collections::HashMap<String, String> {
    let mut params = std::collections::HashMap::new();
    for pair in query.split('&') {
        let Some(eq) = pair.find('=') else { continue };
        if eq == 0 {
            continue;
        }
        params.insert(decode(&pair[..eq]), decode(&pair[eq + 1..]));
    }
    params
}

fn decode(field: &str) -> String {
    let mut out = String::with_capacity(field.len());
    let bytes = field.as_bytes();
    let mut index = 0;
    while index < bytes.len() {
        match bytes[index] {
            b'+' => {
                out.push(' ');
                index += 1;
            }
            b'%' if index + 2 < bytes.len() => match std::str::from_utf8(&bytes[index + 1..index + 3])
                .ok()
                .and_then(|hex| u8::from_str_radix(hex, 16).ok()) {
                Some(byte) => {
                    out.push(byte as char);
                    index += 3;
                }
                None => {
                    out.push('%');
                    index += 1;
                }
            },
            _ => {
                let next = field[index..].chars().next().unwrap_or('?');
                out.push(next);
                index += next.len_utf8();
            }
        }
    }
    out
}

fn version_number(value: &str) -> Option<u32> {
    value.trim().parse().ok()
}

/// Validate the `ports` param (`getChannelInfo`: comma list, absolute ints
/// that fit in a port; the values are checked, not used — we are bound).
fn parse_ports(value: &str) -> Option<Vec<u16>> {
    value
        .split(',')
        .map(|port| {
            let parsed: i32 = port.trim().parse().ok()?;
            u16::try_from(parsed.unsigned_abs()).ok()
        })
        .collect()
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;

    #[test]
    fn echo_answers_ok() {
        assert_eq!(handle_frame("echo=anything"), "OK");
    }

    #[test]
    fn empty_frame_is_saf_01() {
        assert!(handle_frame("").starts_with("SAF_01:"));
    }

    #[test]
    fn non_afirma_scheme_is_saf_02() {
        assert!(handle_frame("https://example.invalid/sign").starts_with("SAF_02:"));
    }

    #[test]
    fn unknown_op_is_saf_04() {
        assert!(handle_frame("afirma://frobnicate?dat=AA==").starts_with("SAF_04:"));
    }

    #[test]
    fn sign_without_data_is_saf_03() {
        assert!(handle_frame("afirma://sign?format=CAdES").starts_with("SAF_03:"));
    }

    #[test]
    fn version_gate_rejects_v4() {
        // Never silently serve another protocol version (failure mode #5).
        let response = handle_frame("afirma://sign?dat=AA==&v=4");
        assert!(response.starts_with("SAF_21:"), "got: {response}");
    }

    #[test]
    fn version_gate_accepts_v3_and_absent() {
        assert_eq!(handle_frame("afirma://sign?dat=AA==&v=3"), "OK");
        assert_eq!(handle_frame("afirma://sign?dat=AA=="), "OK");
    }

    #[test]
    fn bad_ports_is_saf_03() {
        assert!(handle_frame("afirma://sign?dat=AA==&ports=abc").starts_with("SAF_03:"));
    }

    #[test]
    fn fixtures_replay_byte_identically() {
        let dir = format!("{}/../fixtures/protocol3", env!("CARGO_MANIFEST_DIR"));
        let mut entries: Vec<_> = fs::read_dir(&dir)
            .unwrap_or_else(|error| panic!("missing fixture dir {dir}: {error}"))
            .map(|entry| entry.expect("fixture entry").path())
            .filter(|path| path.extension().is_some_and(|ext| ext == "json"))
            .collect();
        entries.sort();
        assert!(!entries.is_empty(), "no fixtures in {dir}");
        for path in &entries {
            let raw = fs::read_to_string(path).expect("read fixture");
            let fixture: serde_json::Value = serde_json::from_str(&raw).expect("parse fixture");
            let request = fixture["request"].as_str().expect("request field");
            let expect = fixture["expect"].as_str().expect("expect field");
            let got = handle_frame(request);
            assert_eq!(
                got,
                expect,
                "fixture {} mismatch",
                path.file_name().unwrap().to_string_lossy()
            );
        }
        assert!(entries.len() >= 4, "expected at least 4 fixtures");
    }

    #[test]
    fn malformed_percent_frames_never_panic() {
        // Regression for commit 11db9df0: the old `&field[index + 1..index + 3]`
        // str-slice panicked when it ended inside a multibyte char (`%` + one
        // ASCII byte + `é`); short/trailing `%` must stay panic-free too.
        for frame in [
            "afirma://sign?dat=%aé",
            "afirma://sign?dat=%",
            "afirma://sign?dat=%zz&v=3",
            "afirma://sign?dat=é%",
        ] {
            let response = handle_frame(frame);
            assert_eq!(response, "OK", "frame {frame:?} gave {response:?}");
        }
    }
}
