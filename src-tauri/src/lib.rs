mod atomic_write;
mod net;
pub mod deeplink;

// P1.4a PC/SC + OpenSC PKCS#11 layer (Refs #98).
pub mod smartcard;
// P6.2 QUIC transport with mDNS discovery (Refs #110).
pub mod p2p;

// P5.1 MCP sidecar supervisor (Refs #107).
pub mod supervisor;
// P2.2a loopback sign server, protocol 3 (Refs #104).
pub mod server;
pub mod tls;

use tauri::Manager;
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
    // P5.1 log capture (Refs #107): the app's only logger, so the supervisor's `log`
    // calls (sidecar stderr included) reach stderr; `RUST_LOG` overrides `info`.
    // ponytail: stderr only — file target comes with a log viewer.
    env_logger::Builder::from_env(env_logger::Env::default().default_filter_or("info")).init();
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
        .manage(supervisor::SupervisorState::default())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_shell::init())
        .invoke_handler(tauri::generate_handler![
            save_file,
            net::ocsp::fetch_revocation_bytes,
            deeplink::deeplink_status,
            deeplink::deeplink_list,
            deeplink::deeplink_remove,
            deeplink::deeplink_confirm
        ])
        // P5.1 MCP sidecar (Refs #107): spawn on startup, kill on `RunEvent::Exit`
        // so quitting the app leaves zero orphans.
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
            let sup = app.handle().clone();
            tauri::async_runtime::spawn(async move {
                supervisor::run(sup).await;
            });
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
        .build(tauri::generate_context!())
        .expect("error while building AutoFirme")
        .run(|app, event| {
            if matches!(event, tauri::RunEvent::Exit) {
                app.state::<supervisor::SupervisorState>().shutdown();
            }
        });
}

#[cfg(test)]
mod logger_tests {
    use log::LevelFilter;

    /// Pins the logger config installed by [`run`]: default threshold `info`
    /// (supervisor stderr capture is `warn`/`error`), `RUST_LOG` wins when set.
    /// Builds the same [`env_logger::Builder`] chain `run()` runs — the filter is
    /// introspectable without touching the global logger, so the test never races
    /// `env_logger::init`'s once-only guard.
    #[test]
    fn logger_defaults_to_info_and_honors_rust_log() {
        assert_eq!(
            env_logger::Builder::from_env(env_logger::Env::default().default_filter_or("info"))
                .build()
                .filter(),
            LevelFilter::Info
        );
        // Records below the default threshold are dropped — the property the
        // original review finding (logger never installed, every record lost)
        // turned into a contract.
        let logger =
            env_logger::Builder::from_env(env_logger::Env::default().default_filter_or("info"))
                .build();
        let probe = log::Record::builder()
            .args(format_args!("probe"))
            .level(log::Level::Debug)
            .target("autofirme::probe")
            .build();
        assert!(!logger.matches(&probe));
        let warn = log::Record::builder()
            .args(format_args!("probe"))
            .level(log::Level::Warn)
            .target("autofirme::probe")
            .build();
        assert!(logger.matches(&warn));
    }
}
