mod atomic_write;

use tauri_plugin_dialog::DialogExt;

/// Save bytes from the webview to a user-chosen path. The dialog runs off the async
/// runtime; the write itself is crash-safe (temp file + rename).
#[tauri::command]
async fn save_file(
    app: tauri::AppHandle,
    suggested_name: String,
    bytes: Vec<u8>,
) -> Result<Option<String>, String> {
    let dialog = app.clone();
    let picked = tauri::async_runtime::spawn_blocking(move || {
        dialog
            .dialog()
            .file()
            .set_file_name(&suggested_name)
            .blocking_save_file()
    })
    .await
    .map_err(|error| error.to_string())?;
    let Some(path) = picked else {
        return Ok(None);
    };
    let path = path.into_path().map_err(|error| error.to_string())?;
    atomic_write::write_atomic(&path, &bytes).map_err(|error| error.to_string())?;
    Ok(Some(path.display().to_string()))
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_dialog::init())
        .invoke_handler(tauri::generate_handler![save_file])
        // P0.6 smoke (#19): with AUTOFIRME_SMOKE set, print the page the webview finished
        // loading and exit. The CI job runs the production build under `xvfb-run` and
        // asserts on the marker, so a webview that cannot boot fails the build. Silent
        // (the hook stays registered but does nothing) in every normal run.
        .on_page_load(|_webview, payload| {
            if std::env::var_os("AUTOFIRME_SMOKE").is_none() {
                return;
            }
            println!("SHELL_SMOKE {:?} {}", payload.event(), payload.url());
            if matches!(payload.event(), tauri::webview::PageLoadEvent::Finished) {
                std::process::exit(0);
            }
        })
        .run(tauri::generate_context!())
        .expect("error while running AutoFirme");
}
