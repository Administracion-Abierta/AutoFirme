//! `afirma://` URL parsing and sanitising (Refs #103).
//!
//! Dependency-free on purpose: the parser runs on untrusted OS input (second
//! instance CLI args) and is unit-tested with bare `rustc --test`.
//!
//! Security contract: parsed values NEVER reach a shell (no `Command` with
//! them) and NEVER touch the filesystem (no path construction). Validation
//! below is defence-in-depth plus early rejection of malformed portal links.

/// URL scheme claimed via `tauri.conf.json`.
pub const SCHEME: &str = "afirma";
/// Raw URL length cap: portals pass base64 inline; 64 KiB is plenty.
pub const MAX_URL_LEN: usize = 64 * 1024;
/// Individual query value cap.
pub const MAX_VALUE_LEN: usize = 56 * 1024;

/// Operations from `ProtocolInvocationLauncher` (AutoFirma 1.9). `Service`
/// and `Websocket` are the local-socket bridge: owned by the sibling sign
/// server (#104), rejected here with a reason instead of silent fallback.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Op {
    Sign,
    Cosign,
    Countersign,
    SelectCert,
    Load,
    Save,
    SignAndSave,
    Batch,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ParseError {
    TooLong,
    BadScheme,
    /// Control/whitespace characters in the raw URL.
    BadChars,
    /// Non-alphanumeric operation host, or `..`/separator tricks.
    BadOp,
    /// `service` / `websocket`: the socket bridge, not this surface.
    ForbiddenOp(&'static str),
    /// Duplicate query key (parameter smuggling).
    DuplicateParam(String),
    /// Value fails charset/length checks after percent-decoding.
    BadValue(String),
    /// Return URL is not `http(s)://`.
    BadReturnUrl(String),
}

impl std::fmt::Display for ParseError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::TooLong => write!(f, "URL exceeds {MAX_URL_LEN} bytes"),
            Self::BadScheme => write!(f, "not an {SCHEME}:// URL"),
            Self::BadChars => write!(f, "control characters in URL"),
            Self::BadOp => write!(f, "unknown or malformed operation"),
            Self::ForbiddenOp(op) => write!(f, "{op} needs the local sign server (#104)"),
            Self::DuplicateParam(k) => write!(f, "duplicate parameter {k:?}"),
            Self::BadValue(k) => write!(f, "parameter {k:?} failed validation"),
            Self::BadReturnUrl(k) => write!(f, "parameter {k:?} is not an http(s) URL"),
        }
    }
}

/// A sanitised portal request. `origin` is the portal host taken from the
/// return-URL parameter (`storageServlet` / `servlet` / `rtservlet` / `url`);
/// `None` means no provable origin — the caller must refuse and show it.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct AfirmaRequest {
    pub op: Op,
    pub origin: Option<String>,
    /// Decoded `(key, value)` pairs, keys lowercased.
    pub params: Vec<(String, String)>,
}

impl AfirmaRequest {
    pub fn get(&self, key: &str) -> Option<&str> {
        self.params
            .iter()
            .find(|(k, _)| k == key)
            .map(|(_, v)| v.as_str())
    }
}

/// Query parameters that carry the portal return URL (1.9 `UrlParameters`
/// family: `STORAGE_SERVLET_PARAM`, `SERVLET`, `rtservlet`, plain `url`).
const ORIGIN_PARAMS: &[&str] = &["storageservlet", "servlet", "rtservlet", "serverurl", "url"];
fn parse_op(host: &str) -> Result<Op, ParseError> {
    match host.to_ascii_lowercase().as_str() {
        "sign" => Ok(Op::Sign),
        "cosign" | "co-sign" => Ok(Op::Cosign),
        "countersign" | "counter-sign" => Ok(Op::Countersign),
        "selectcert" => Ok(Op::SelectCert),
        "load" => Ok(Op::Load),
        "save" => Ok(Op::Save),
        "signandsave" => Ok(Op::SignAndSave),
        "batch" => Ok(Op::Batch),
        "service" | "websocket" => Err(ParseError::ForbiddenOp(
            if host.eq_ignore_ascii_case("service") {
                "service"
            } else {
                "websocket"
            },
        )),
        _ => Err(ParseError::BadOp),
    }
}

/// Minimal percent-decoder (`+` stays literal: portal data is not form data).
fn percent_decode(input: &str) -> Option<String> {
    let bytes = input.as_bytes();
    let mut out = Vec::with_capacity(bytes.len());
    let mut i = 0;
    while i < bytes.len() {
        match bytes[i] {
            b'%' => {
                if i + 2 >= bytes.len() {
                    return None;
                }
                let hex = |c: u8| (c as char).to_digit(16).map(|d| d as u8);
                let (hi, lo) = (hex(bytes[i + 1])?, hex(bytes[i + 2])?);
                out.push(hi << 4 | lo);
                i += 3;
            }
            b => {
                out.push(b);
                i += 1;
            }
        }
    }
    String::from_utf8(out).ok()
}

fn valid_key(key: &str) -> bool {
    !key.is_empty()
        && key.len() <= 64
        && key
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b == b'.' || b == b'_' || b == b'-')
}

/// Decoded values: printable ASCII (plus TAB), no shell metachars that could
/// surprise a downstream consumer, no backslashes. Base64/URL payloads pass.
fn valid_value(value: &str) -> bool {
    value.len() <= MAX_VALUE_LEN
        && value.chars().all(|c| {
            c == '\t' || (!c.is_control() && !matches!(c, '"' | '\'' | '`' | '\\' | '<' | '>'))
        })
}

/// Extract the host from an `http(s)://` URL. Returns `None` for anything else
/// (`file:`, `jar:`, schemeless, userinfo, …).
pub fn origin_of_url(url: &str) -> Option<String> {
    let rest = url
        .strip_prefix("http://")
        .or_else(|| url.strip_prefix("https://"))?;
    // Strip userinfo (last `@`), then a single trailing `:port`.
    let authority = rest.split(['/', '?', '#']).next().unwrap_or_default();
    let authority = authority.rsplit('@').next().unwrap_or_default();
    let host = match authority.rsplit_once(':') {
        // Single colon + numeric tail = port. Anything else (IPv6, junk) is rejected
        // by the charset check below.
        Some((h, port)) if !port.is_empty() && port.bytes().all(|b| b.is_ascii_digit()) => h,
        _ => authority,
    };
    let host = host.strip_suffix('.').unwrap_or(host).to_ascii_lowercase();
    if host.is_empty()
        || host.len() > 253
        || host.contains('*')
        || !host
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b == b'.' || b == b'-')
    {
        return None;
    }
    Some(host)
}

/// Parse and sanitise one raw `afirma://` URL (already stripped of OS quoting:
/// Tauri hands us `argv` entries verbatim, no shell involved).
pub fn parse_afirma_url(raw: &str) -> Result<AfirmaRequest, ParseError> {
    if raw.len() > MAX_URL_LEN {
        return Err(ParseError::TooLong);
    }
    if raw
        .bytes()
        .any(|b| b < 0x20 || b == 0x7f || b == b' ' && false)
    {
        return Err(ParseError::BadChars);
    }
    // Spaces must be percent-encoded; a literal space means a broken or
    // hand-typed URL, never a real portal link.
    if raw.as_bytes().contains(&b' ') {
        return Err(ParseError::BadChars);
    }
    let rest = raw
        .strip_prefix(&format!("{SCHEME}://"))
        .ok_or(ParseError::BadScheme)?;
    if rest.contains('#') {
        return Err(ParseError::BadChars);
    }
    let (host, query) = match rest.find('?') {
        Some(i) => (&rest[..i], &rest[i + 1..]),
        None => (rest, ""),
    };
    if host.is_empty()
        || host.len() > 32
        || !host.bytes().all(|b| b.is_ascii_alphanumeric() || b == b'-')
    {
        return Err(ParseError::BadOp);
    }
    let op = parse_op(host)?;

    let mut params = Vec::new();
    if !query.is_empty() {
        for pair in query.split('&') {
            if pair.is_empty() {
                continue;
            }
            let (k, v) = match pair.find('=') {
                Some(i) => (&pair[..i], &pair[i + 1..]),
                None => (pair, ""),
            };
            let key = percent_decode(k).unwrap_or_default().to_ascii_lowercase();
            if !valid_key(&key) || params.iter().any(|(ek, _): &(String, String)| ek == &key) {
                return Err(ParseError::DuplicateParam(key));
            }
            let value = percent_decode(v).ok_or_else(|| ParseError::BadValue(key.clone()))?;
            if !valid_value(&value) {
                return Err(ParseError::BadValue(key.clone()));
            }
            // Any param that looks like a return URL must be http(s).
            if ORIGIN_PARAMS.contains(&key.as_str()) && !value.is_empty() {
                let lower = value.to_ascii_lowercase();
                if !(lower.starts_with("http://") || lower.starts_with("https://")) {
                    return Err(ParseError::BadReturnUrl(key.clone()));
                }
            }
            params.push((key, value));
        }
    }

    let origin = ORIGIN_PARAMS
        .iter()
        .find_map(|k| params.iter().find(|(ek, _)| ek == *k))
        .and_then(|(_, v)| origin_of_url(v));

    Ok(AfirmaRequest { op, origin, params })
}

/// Filter `argv` (second-instance handoff) down to candidate `afirma://`
/// entries. Pure, so the handoff stays testable without Tauri.
pub fn extract_afirma_args(argv: &[String]) -> Vec<&str> {
    argv.iter()
        .filter(|a| a.len() <= MAX_URL_LEN)
        .map(String::as_str)
        .filter(|a| {
            a.len() > SCHEME.len() + 3
                && a.as_bytes()[SCHEME.len()] == b':'
                && a[..SCHEME.len()].eq_ignore_ascii_case(SCHEME)
        })
        .collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn sign_with_servlet_origin() {
        let r = parse_afirma_url(
            "afirma://sign?dat=aGVsbG8%3D&format=CAdES&algorithm=SHA256withRSA&storageServlet=https%3A%2F%2Fsede.example.es%2Fsave",
        )
        .unwrap();
        assert_eq!(r.op, Op::Sign);
        assert_eq!(r.origin.as_deref(), Some("sede.example.es"));
        assert_eq!(r.get("format"), Some("CAdES"));
    }

    #[test]
    fn forbidden_bridge_ops_have_reason() {
        assert!(matches!(
            parse_afirma_url("afirma://service?x=1"),
            Err(ParseError::ForbiddenOp("service"))
        ));
        assert!(matches!(
            parse_afirma_url("afirma://websocket?x=1"),
            Err(ParseError::ForbiddenOp("websocket"))
        ));
    }

    #[test]
    fn no_provable_origin_without_servlet() {
        let r = parse_afirma_url("afirma://sign?dat=QUJD&format=XAdES").unwrap();
        assert_eq!(r.origin, None);
    }

    #[test]
    fn rejects_wrong_scheme_and_bad_ops() {
        assert_eq!(
            parse_afirma_url("https://sede.example.es/sign"),
            Err(ParseError::BadScheme)
        );
        assert_eq!(
            parse_afirma_url("afirma://../etc?x=1"),
            Err(ParseError::BadOp)
        );
        assert_eq!(
            parse_afirma_url("afirma://sig;n?x=1"),
            Err(ParseError::BadOp)
        );
    }

    #[test]
    fn rejects_file_return_url_and_controls() {
        assert!(matches!(
            parse_afirma_url("afirma://sign?servlet=file%3A%2F%2F%2Fetc%2Fpasswd"),
            Err(ParseError::BadReturnUrl(_))
        ));
        assert_eq!(
            parse_afirma_url("afirma://sign?dat=a\nb"),
            Err(ParseError::BadChars)
        );
        assert!(matches!(
            parse_afirma_url("afirma://sign?dat=%3Cscript%3E"),
            Err(ParseError::BadValue(_))
        ));
    }

    #[test]
    fn rejects_duplicates_and_fragments() {
        assert!(matches!(
            parse_afirma_url("afirma://sign?dat=QQ%3D%3D&dat=Qg%3D%3D"),
            Err(ParseError::DuplicateParam(_))
        ));
        assert_eq!(
            parse_afirma_url("afirma://sign?dat=QQ#frag"),
            Err(ParseError::BadChars)
        );
    }

    #[test]
    fn argv_filter_keeps_only_scheme() {
        let argv = vec![
            "/usr/bin/autofirme".to_string(),
            "afirma://sign?dat=QQ".to_string(),
            "--flag".to_string(),
            "AFIRMA://selectcert".to_string(),
        ];
        let got = extract_afirma_args(&argv);
        assert_eq!(got, vec!["afirma://sign?dat=QQ", "AFIRMA://selectcert"]);
    }

    #[test]
    fn origin_host_edge_cases() {
        assert_eq!(
            origin_of_url("https://Sede.Example.ES:8443/path?q=1").as_deref(),
            Some("sede.example.es")
        );
        assert_eq!(origin_of_url("file:///etc/passwd"), None);
        assert_eq!(
            origin_of_url("https://user@evil.example/x").as_deref(),
            Some("evil.example")
        );
        assert_eq!(origin_of_url("https://*.example.es/x"), None);
    }
}
