//! `afirma://` deep-link surface (Refs #103).
//!
//! Flow: the OS launches a second instance (Windows/Linux) or fires a
//! deep-link event (macOS) with the portal URL. The single-instance plugin
//! routes that into the running instance; we parse + sanitise (`parse`),
//! gate on the per-domain allow-list (`allowlist`), and only then emit a
//! `deeplink://request` frontend event and show the native confirmation.
//!
//! Never-stolen scheme: [`scheme_status`] reports whether another app owns
//! `afirma://`. When owned elsewhere the app offers extension-only mode and
//! never calls `register`.

pub mod allowlist;
pub mod parse;

use allowlist::{AllowError, AllowList};
use parse::{origin_of_url, parse_afirma_url, AfirmaRequest, Op, ParseError};
use std::path::PathBuf;
use std::sync::Mutex;
use tauri::{AppHandle, Emitter, Manager, Runtime};

/// Frontend event carrying a gated portal request.
pub const REQUEST_EVENT: &str = "deeplink://request";

/// Who owns the `afirma://` scheme on this machine.
#[derive(Debug, Clone, PartialEq, Eq, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum SchemeStatus {
    /// We are the registered handler.
    Ours,
    /// Another app (e.g. real AutoFirma) owns it — extension-only mode.
    Other {
        /// Best-effort handler description (`xdg-mime` desktop id, …).
        handler: Option<String>,
    },
    /// No handler registered — the user should register us (opt-in).
    None,
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct GatedRequest {
    pub op: String,
    pub origin: Option<String>,
    pub allowed: bool,
    pub params: Vec<(String, String)>,
}

fn op_name(op: Op) -> &'static str {
    match op {
        Op::Sign => "sign",
        Op::Cosign => "cosign",
        Op::Countersign => "countersign",
        Op::SelectCert => "selectcert",
        Op::Load => "load",
        Op::Save => "save",
        Op::SignAndSave => "signandsave",
        Op::Batch => "batch",
    }
}

fn request_to_gated(req: &AfirmaRequest, allowed: bool) -> GatedRequest {
    GatedRequest {
        op: op_name(req.op).to_string(),
        origin: req.origin.clone(),
        allowed,
        params: req.params.clone(),
    }
}

/// Query the OS for the current `afirma://` handler (Linux `xdg-mime` first;
/// other platforms: best-effort stub returning `None` = "unknown").
pub fn current_handler() -> Option<String> {
    #[cfg(target_os = "linux")]
    {
        std::process::Command::new("xdg-mime")
            .args(["query", "default", "x-scheme-handler/afirma"])
            .output()
            .ok()
            .filter(|o| o.status.success())
            .map(|o| String::from_utf8_lossy(&o.stdout).trim().to_string())
            .filter(|s| !s.is_empty())
    }
    #[cfg(not(target_os = "linux"))]
    {
        // ponytail: Windows (registry) + macOS (LaunchServices) probes need
        // platform APIs unavailable in CI; unknown → ask the user, never assume.
        None
    }
}

/// Resolve [`SchemeStatus`] from our bundle id and the OS-reported handler.
pub fn resolve_status(ours: &str, handler: Option<&str>) -> SchemeStatus {
    match handler {
        None => SchemeStatus::None,
        Some(h) if h.trim().is_empty() => SchemeStatus::None,
        Some(h) if h.contains(ours) => SchemeStatus::Ours,
        Some(h) => SchemeStatus::Other {
            handler: Some(h.to_string()),
        },
    }
}

fn allowlist_path<R: Runtime>(app: &AppHandle<R>) -> Result<PathBuf, String> {
    app.path()
        .app_data_dir()
        .map(|d| d.join("deeplink-allowlist.txt"))
        .map_err(|e| e.to_string())
}

fn load_allowlist<R: Runtime>(app: &AppHandle<R>) -> Result<AllowList, String> {
    let path = allowlist_path(app)?;
    match std::fs::read_to_string(&path) {
        Ok(text) => AllowList::from_text(&text).map_err(|e| e.to_string()),
        Err(e) if e.kind() == std::io::ErrorKind::NotFound => Ok(AllowList::new()),
        Err(e) => Err(e.to_string()),
    }
}

fn save_allowlist<R: Runtime>(app: &AppHandle<R>, list: &AllowList) -> Result<(), String> {
    let path = allowlist_path(app)?;
    if let Some(dir) = path.parent() {
        std::fs::create_dir_all(dir).map_err(|e| e.to_string())?;
    }
    crate::atomic_write::write_atomic(&path, list.to_text().as_bytes()).map_err(|e| e.to_string())
}

struct AllowState(Mutex<AllowList>);

/// Gate one raw URL: parse, check the allow-list, emit the frontend event.
/// Returns the gated request; the confirmation dialog happens in `confirm_flow`
/// (native, never webview-observable).
pub fn gate_url<R: Runtime>(app: &AppHandle<R>, raw: &str) -> Result<GatedRequest, String> {
    let req = parse_afirma_url(raw).map_err(|e: ParseError| e.to_string())?;
    let allowed = app
        .try_state::<AllowState>()
        .map(|s| {
            s.0.lock()
                .map(|l| l.contains(req.origin.as_deref()))
                .unwrap_or(false)
        })
        .unwrap_or(false);
    let gated = request_to_gated(&req, allowed);
    let _ = app.emit(REQUEST_EVENT, &gated);
    Ok(gated)
}

#[tauri::command]
pub async fn deeplink_status(app: AppHandle) -> Result<SchemeStatus, String> {
    let ours = app.package_info().name.clone();
    Ok(resolve_status(&ours, current_handler().as_deref()))
}

#[tauri::command]
pub async fn deeplink_list(app: AppHandle) -> Result<Vec<String>, String> {
    let list = app
        .try_state::<AllowState>()
        .ok_or_else(|| "allow-list not initialised".to_string())?;
    let guard = list.0.lock().map_err(|_| "allow-list locked".to_string())?;
    Ok(guard.domains().map(str::to_string).collect())
}

#[tauri::command]
pub async fn deeplink_remove(app: AppHandle, domain: String) -> Result<bool, String> {
    let list = app
        .try_state::<AllowState>()
        .ok_or_else(|| "allow-list not initialised".to_string())?;
    let removed = list
        .0
        .lock()
        .map(|mut l| l.remove(&domain))
        .map_err(|_| "allow-list locked".to_string())?;
    if removed {
        let text = list
            .0
            .lock()
            .map(|l| l.to_text())
            .map_err(|_| "allow-list locked".to_string())?;
        let path = allowlist_path(&app)?;
        if let Some(dir) = path.parent() {
            std::fs::create_dir_all(dir).map_err(|e| e.to_string())?;
        }
        crate::atomic_write::write_atomic(&path, text.as_bytes()).map_err(|e| e.to_string())?;
    }
    Ok(removed)
}

/// Per-request confirmation: the native dialog shows op + origin + params and,
/// ONLY on explicit user action, adds the origin to the allow-list.
/// `remember = true` means "also allow this domain next time" — never "all domains".
#[tauri::command]
pub async fn deeplink_confirm(
    app: AppHandle,
    gated: GatedRequest,
    allow: bool,
    remember: bool,
) -> Result<bool, String> {
    use tauri_plugin_dialog::{DialogExt, MessageDialogButtons};
    if !allow {
        return Ok(false);
    }
    // No provable origin → nothing to allow-list; the caller must refuse + show.
    let Some(origin) = gated.origin.as_deref() else {
        return Ok(false);
    };
    // Re-check the origin shape at confirm time (defence in depth: the
    // frontend passes this struct back verbatim).
    if origin_of_url(&format!("https://{origin}/")).as_deref() != Some(origin) {
        return Err(AllowError::BadDomain.to_string());
    }
    let dialog_app = app.clone();
    let title = format!("Portal {} wants to {}", origin, gated.op);
    let body = format!(
        "{}\n\nOrigin: {}\n{}",
        title,
        origin,
        gated
            .params
            .iter()
            .map(|(k, v)| format!("{k} = {}", truncate(v, 120)))
            .collect::<Vec<_>>()
            .join("\n")
    );
    let confirmed = tauri::async_runtime::spawn_blocking(move || {
        dialog_app
            .dialog()
            .message(body)
            .title(title)
            .buttons(MessageDialogButtons::OkCancelCustom(
                "Sign".to_string(),
                "Refuse".to_string(),
            ))
            .blocking_show()
    })
    .await
    .map_err(|e| e.to_string())?;
    if !confirmed {
        return Ok(false);
    }
    if remember {
        let list = app
            .try_state::<AllowState>()
            .ok_or_else(|| "allow-list not initialised".to_string())?;
        let mut guard = list.0.lock().map_err(|_| "allow-list locked".to_string())?;
        guard.add(origin).map_err(|e: AllowError| e.to_string())?;
        save_allowlist(&app, &guard.clone())?;
    }
    Ok(true)
}

fn truncate(s: &str, max: usize) -> String {
    if s.len() <= max {
        return s.to_string();
    }
    // Byte slicing can split a char boundary; walk back to one.
    let mut end = max;
    while end > 0 && !s.is_char_boundary(end) {
        end -= 1;
    }
    format!("{}…", &s[..end])
}

/// Handle one raw URL from any entry point (startup args, second-instance
/// handoff, macOS deep-link event). Public for the plugin callbacks in `lib.rs`.
pub fn handle_raw_url<R: Runtime>(app: &AppHandle<R>, raw: &str) {
    // Focus the window first: an invisible handoff is a spoofing risk.
    if let Some(window) = app.get_webview_window("main") {
        let _ = window.set_focus();
        let _ = window.unminimize();
    }
    if let Err(e) = gate_url(app, raw) {
        // Parse failures still surface: unlisted/refused must be SHOWN per acceptance.
        #[derive(serde::Serialize, Clone)]
        struct Refused<'a> {
            raw: &'a str,
            reason: String,
        }
        let _ = app.emit(
            REQUEST_EVENT,
            Refused {
                raw: &truncate(raw, 200),
                reason: e,
            },
        );
    }
}

/// Initialise allow-list state from disk. Call once in `.setup()`.
pub fn init_state<R: Runtime>(app: &AppHandle<R>) {
    let list = load_allowlist(app).unwrap_or_default();
    app.manage(AllowState(Mutex::new(list)));
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn status_never_steals() {
        assert_eq!(resolve_status("autofirme", None), SchemeStatus::None);
        assert_eq!(resolve_status("autofirme", Some("")), SchemeStatus::None);
        assert_eq!(
            resolve_status("autofirme", Some("autofirme.desktop")),
            SchemeStatus::Ours
        );
        assert_eq!(
            resolve_status("autofirme", Some("AutoFirma.desktop")),
            SchemeStatus::Other {
                handler: Some("AutoFirma.desktop".to_string())
            }
        );
    }
}
