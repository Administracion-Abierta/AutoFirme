//! Restart policy for the bundled `autofirme-mcp` sidecar (Refs #107).
//!
//! Pure decision logic, no Tauri dependency: given an exit event, decide whether the
//! sidecar restarts now, backs off, or stays down. The Tauri glue in `super` drives the
//! actual spawn; the tests below pin the only behavior the acceptance criteria need:
//! a crash restarts, an intentional stop stays stopped, a poison binary gives up.

/// Sidecar program name as declared in `tauri.conf.json > bundle > externalBin`.
/// Tauri appends `-<target-triple>` (plus `.exe` on Windows) and resolves it from
/// `src-tauri/binaries/` at dev time, from the bundle resources dir at runtime.
pub const SIDECAR_PROGRAM: &str = "autofirme-mcp";

/// How the supervised child exited.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ChildExit {
    /// Unexpected crash (nonzero code or signal). Restart.
    Crashed,
    /// App is quitting / operator disabled MCP. Stay down.
    Stopped,
}

/// What the supervisor must do after an exit event.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum SupervisorAction {
    Restart,
    StayDown,
}

/// Bounded crash loop guard: after this many consecutive crashes inside
/// [`RESTART_WINDOW_SECS`] the sidecar stays down and logs, instead of hot-looping.
pub const MAX_FAST_RESTARTS: u32 = 5;
/// Seconds the fast-restart counter covers.
pub const RESTART_WINDOW_SECS: u64 = 60;
/// Backoff between restarts, seconds. Linear: attempt N waits N * this.
pub const RESTART_BACKOFF_SECS: u64 = 1;

/// Decide the next action. `consecutive_crashes` counts crashes since the last clean
/// run or window expiry; the caller resets it. Returns the action plus seconds to wait.
pub fn decide(exit: ChildExit, consecutive_crashes: u32) -> (SupervisorAction, u64) {
    match exit {
        ChildExit::Stopped => (SupervisorAction::StayDown, 0),
        ChildExit::Crashed if consecutive_crashes >= MAX_FAST_RESTARTS => {
            (SupervisorAction::StayDown, 0)
        }
        ChildExit::Crashed => (
            SupervisorAction::Restart,
            u64::from(consecutive_crashes.saturating_add(1)) * RESTART_BACKOFF_SECS,
        ),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn crash_restarts_with_linear_backoff() {
        assert_eq!(
            decide(ChildExit::Crashed, 0),
            (SupervisorAction::Restart, 1)
        );
        assert_eq!(
            decide(ChildExit::Crashed, 2),
            (SupervisorAction::Restart, 3)
        );
    }

    #[test]
    fn poison_binary_stays_down_after_five_fast_crashes() {
        assert_eq!(
            decide(ChildExit::Crashed, MAX_FAST_RESTARTS),
            (SupervisorAction::StayDown, 0)
        );
    }

    #[test]
    fn intentional_stop_never_restarts() {
        assert_eq!(
            decide(ChildExit::Stopped, 0),
            (SupervisorAction::StayDown, 0)
        );
        assert_eq!(
            decide(ChildExit::Stopped, MAX_FAST_RESTARTS),
            (SupervisorAction::StayDown, 0)
        );
    }
}
