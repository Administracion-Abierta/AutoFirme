mod atomic_write;
pub mod deeplink;

// P1.4a PC/SC + OpenSC PKCS#11 layer (Refs #98).
pub mod smartcard;

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
        // Single-instance FIRST: its callback sees every second launch's argv
        // and hands `afirma://` args to the running instance. Registered before
        // deep-link so its `deep-link` feature forwards even when we shut down
        // on purpose (e.g. scheme owned by real AutoFirma) — handoff still lands.
        .plugin(tauri_plugin_single_instance::init(|app, argv, _cwd| {
            for raw in deeplink::parse::extract_afirma_args(&argv) {
                deeplink::handle_raw_url(app, raw);
            }
        }))
        .plugin(tauri_plugin_deep_link::init())
        .plugin(tauri_plugin_dialog::init())
        .invoke_handler(tauri::generate_handler![
            save_file,
            deeplink::deeplink_status,
            deeplink::deeplink_list,
            deeplink::deeplink_remove,
            deeplink::deeplink_confirm
        ])
        .setup(|app| {
            use tauri_plugin_deep_link::DeepLinkExt;
            deeplink::init_state(app.handle());
            // Startup args: OS launched us with `afirma://…`.
            let args: Vec<String> = std::env::args().collect();
            for raw in deeplink::parse::extract_afirma_args(&args) {
                deeplink::handle_raw_url(app.handle(), raw);
            }
            // Cold-start links already delivered (Linux/Windows) plus macOS
            // open-URL events arriving while running.
            if let Ok(Some(urls)) = app.deep_link().get_current() {
                for url in urls {
                    deeplink::handle_raw_url(app.handle(), &url.to_string());
                }
            }
            let handle = app.handle().clone();
            app.deep_link().on_open_url(move |event| {
                for url in event.urls() {
                    deeplink::handle_raw_url(&handle, &url.to_string());
                }
            });
            Ok(())
        })
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
        .expect("error while running AutoFirma");
}
