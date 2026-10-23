//! Supervised `autofirme-mcp` sidecar (Refs #107).
//!
//! The TypeScript MCP server ships as a self-contained binary built by `mcp/build.mjs`
//! into `src-tauri/binaries/` and declared in `tauri.conf.json > bundle > externalBin`.
//! [`run`] spawns it as a Tauri sidecar at startup and restarts it on unexpected exit
//! ([`policy`] bounds the crash loop); every stderr line goes to the app log, stdout is
//! the MCP channel and is never logged.
//!
//! Shutdown: call [`SupervisorState::shutdown`] (wired to `RunEvent::Exit` in `lib.rs`).
//! It flips the stop flag and kills the child synchronously, so quitting the app leaves
//! zero orphans. The pump loop only ever observes the death afterwards and exits.

pub mod policy;

use std::sync::atomic::{AtomicBool, Ordering};
use std::time::Duration;

use parking_lot::Mutex;
use tauri::{AppHandle, Manager, Runtime};
use tauri_plugin_shell::process::{CommandChild, CommandEvent};
use tauri_plugin_shell::ShellExt;

pub use policy::{decide, ChildExit, SupervisorAction, SIDECAR_PROGRAM};

/// Shared supervisor state, stored with `app.manage()`.
#[derive(Debug, Default)]
pub struct SupervisorState {
    stopped: AtomicBool,
    child: Mutex<Option<CommandChild>>,
}

impl SupervisorState {
    /// Signal shutdown and synchronously kill the sidecar if alive. Idempotent:
    /// second call finds an empty slot and only re-sets the flag.
    pub fn shutdown(&self) {
        self.stopped.store(true, Ordering::SeqCst);
        if let Some(child) = self.child.lock().take() {
            if let Err(error) = child.kill() {
                log::error!("[mcp-supervisor] kill on shutdown failed: {error}");
            }
        }
    }

    fn is_stopped(&self) -> bool {
        self.stopped.load(Ordering::SeqCst)
    }
}

/// Spawn the sidecar and supervise it until [`SupervisorState::shutdown`] runs.
///
/// Restarts on crash with linear backoff, capped at [`policy::MAX_FAST_RESTARTS`] fast
/// crashes — after that the sidecar stays down and the failure is logged. The only
/// [`CommandChild`] owner is this loop plus the shutdown hook (via the state slot),
/// so a quit always reaches a kill.
pub async fn run<R: Runtime>(app: AppHandle<R>) {
    let state: tauri::State<'_, SupervisorState> = app.state();
    let mut consecutive_crashes: u32 = 0;
    let mut window_start = std::time::Instant::now();

    loop {
        if state.is_stopped() {
            return;
        }
        let command = match app.shell().sidecar(SIDECAR_PROGRAM) {
            Ok(command) => command,
            Err(error) => {
                log::error!("[mcp-supervisor] sidecar not configured: {error}");
                return;
            }
        };
        let (events, child) = match command.spawn() {
            Ok(pair) => pair,
            Err(error) => {
                log::error!("[mcp-supervisor] spawn failed: {error}");
                let (action, wait_secs) = decide(ChildExit::Crashed, consecutive_crashes);
                if action == SupervisorAction::StayDown {
                    log::error!(
                        "[mcp-supervisor] giving up after {consecutive_crashes} spawn failures"
                    );
                    return;
                }
                consecutive_crashes += 1;
                sleep_while_alive(wait_secs, &state).await;
                continue;
            }
        };
        log::info!("[mcp-supervisor] sidecar spawned (pid {})", child.pid());
        *state.child.lock() = Some(child);

        let exit = pump_events(events, &state).await;
        // Whoever killed the child (crash or shutdown hook) left the slot filled;
        // take it so a stale handle never lingers. On the shutdown path the hook may
        // have fired before this spawn, so a live child can still be here: kill it,
        // otherwise quitting during a backoff would orphan the fresh child.
        let taken = state.child.lock().take();
        if exit == ChildExit::Stopped {
            if let Some(child) = taken {
                let _ = child.kill();
            }
        }

        if window_start.elapsed().as_secs() > policy::RESTART_WINDOW_SECS {
            consecutive_crashes = 0;
            window_start = std::time::Instant::now();
        }
        let (action, wait_secs) = match exit {
            ChildExit::Stopped => (SupervisorAction::StayDown, 0),
            ChildExit::Crashed => {
                let decision = decide(ChildExit::Crashed, consecutive_crashes);
                consecutive_crashes += 1;
                decision
            }
        };
        match action {
            SupervisorAction::StayDown => {
                if exit == ChildExit::Crashed {
                    log::error!(
                        "[mcp-supervisor] sidecar down after {consecutive_crashes} fast crashes; manual restart required"
                    );
                }
                return;
            }
            SupervisorAction::Restart => {
                log::warn!("[mcp-supervisor] restarting sidecar in {wait_secs}s");
                sleep_while_alive(wait_secs, &state).await;
            }
        }
    }
}

/// Pump one child lifetime. Returns how it ended: shutdown flag set means the exit hook
/// owns the kill; any other end is a crash.
async fn pump_events(
    mut events: tauri::async_runtime::Receiver<CommandEvent>,
    state: &SupervisorState,
) -> ChildExit {
    loop {
        if state.is_stopped() {
            return ChildExit::Stopped;
        }
        match events.try_recv() {
            Ok(CommandEvent::Stderr(line)) => {
                log::warn!("[mcp-sidecar] {}", String::from_utf8_lossy(&line));
            }
            Ok(CommandEvent::Stdout(_)) => {
                // MCP channel traffic; never logged (may carry request contents).
            }
            Ok(CommandEvent::Error(message)) => {
                log::error!("[mcp-supervisor] event error: {message}");
                return ChildExit::Crashed;
            }
            Ok(CommandEvent::Terminated(payload)) => {
                let code = payload.code.unwrap_or(-1);
                log::warn!("[mcp-supervisor] sidecar exited (code {code})");
                // The MCP server runs until killed: any exit is a crash
                // unless shutdown was requested.
                return if state.is_stopped() {
                    ChildExit::Stopped
                } else {
                    ChildExit::Crashed
                };
            }
            Err(_) => {
                // Empty or disconnected: disconnected means the reader tasks died
                // with the child, so treat a closed channel as a crash.
                tokio::time::sleep(Duration::from_millis(50)).await;
                if events.is_closed() {
                    return ChildExit::Crashed;
                }
            }
        }
    }
}

/// Sleep, but wake early on shutdown so quit never waits out a backoff.
async fn sleep_while_alive(secs: u64, state: &SupervisorState) {
    let deadline = std::time::Instant::now() + Duration::from_secs(secs);
    while std::time::Instant::now() < deadline {
        if state.is_stopped() {
            return;
        }
        tokio::time::sleep(Duration::from_millis(100)).await;
    }
}
