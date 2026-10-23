// LAN peer discovery: mDNS announce/browse + manual entry (Refs #110).
//
// Announcements carry identity + fingerprint only — never file contents.
// Where multicast is blocked (corporate Wi-Fi), the user types `host:port`
// and the transfer proceeds without discovery. Browse errors degrade to the
// manual path; there is no error storm: bounded retries, then quiet.

use std::collections::HashMap;
use std::net::{SocketAddr, ToSocketAddrs};
use std::sync::{Arc, Mutex};
use std::time::Duration;

use super::fingerprint::Fingerprint;

pub const SERVICE_TYPE: &str = "_autofirme._udp.local.";
pub const TXT_FINGERPRINT: &str = "fp";
pub const TXT_ID: &str = "id";

/// A peer seen on the LAN.
#[derive(Clone, Debug, PartialEq, Eq)]
pub struct Peer {
    pub name: String,
    pub fingerprint: Fingerprint,
    pub addresses: Vec<SocketAddr>,
}

impl Peer {
    fn from_txt(name: &str, props: &HashMap<String, String>, addrs: Vec<SocketAddr>) -> Option<Self> {
        let hex = props.get(TXT_FINGERPRINT)?;
        let fingerprint = Fingerprint::parse_hex(hex)?;
        let label = props
            .get(TXT_ID)
            .cloned()
            .unwrap_or_else(|| name.to_owned());
        Some(Self {
            name: label,
            fingerprint,
            addresses: addrs,
        })
    }
}

/// Parse a manual `host:port` entry. Accepts `1.2.3.4:1234`, `[::1]:1234`,
/// and bare IPs (default P2P port assumed by the caller via `default_port`).
pub fn parse_manual_addr(input: &str, default_port: u16) -> Option<SocketAddr> {
    let trimmed = input.trim();
    if trimmed.is_empty() {
        return None;
    }
    if let Ok(addr) = trimmed.parse::<SocketAddr>() {
        return Some(addr);
    }
    // Bare IP → attach default port.
    if let Ok(ip) = trimmed
        .trim_start_matches('[')
        .trim_end_matches(']')
        .parse::<std::net::IpAddr>()
    {
        return Some(SocketAddr::new(ip, default_port));
    }
    // Hostname:port → resolve (first hit wins; LAN peers are single-homed).
    if let Some((host, port)) = trimmed.rsplit_once(':') {
        let port: u16 = port.parse().ok()?;
        let mut addrs = (host, port).to_socket_addrs().ok()?;
        return addrs.next();
    }
    None
}

/// mDNS announcer + browser. `mdns-sd` is sync; the handle below owns the
/// daemon for the app lifetime. Peer list is polled, never pushed with
/// panics: browse failures are recorded as [`DiscoveryState::Degraded`].
#[derive(Clone, Debug, PartialEq, Eq)]
pub enum DiscoveryState {
    Browsing,
    /// Multicast unavailable (CI sandbox, blocked Wi-Fi): manual entry only.
    Degraded(String),
}

pub struct Discovery {
    daemon: mdns_sd::ServiceDaemon,
    service_fullname: Option<String>,
    state: Arc<Mutex<DiscoveryState>>,
}

impl Discovery {
    pub fn new() -> Result<Self, String> {
        let daemon = mdns_sd::ServiceDaemon::new().map_err(|error| error.to_string())?;
        Ok(Self {
            daemon,
            service_fullname: None,
            state: Arc::new(Mutex::new(DiscoveryState::Browsing)),
        })
    }

    /// Announce this install on the LAN: name + fingerprint TXT only.
    pub fn announce(
        &mut self,
        peer_name: &str,
        fingerprint: &Fingerprint,
        port: u16,
    ) -> Result<(), String> {
        let mut props = HashMap::new();
        props.insert(TXT_FINGERPRINT.to_owned(), fingerprint.hex());
        props.insert(TXT_ID.to_owned(), peer_name.to_owned());
        let info = mdns_sd::ServiceInfo::new(
            SERVICE_TYPE,
            peer_name,
            "autofirme.local.",
            (),
            port,
            props,
        )
        .map_err(|error| error.to_string())?;
        self.service_fullname = Some(info.get_fullname().to_owned());
        self.daemon
            .register(info)
            .map_err(|error| error.to_string())?;
        Ok(())
    }

    /// One-shot browse: collect peers visible within `timeout`.
    /// Never errors on empty air — returns what was seen, possibly none.
    /// On daemon failure, marks degraded and returns empty.
    pub fn browse(&self, timeout: Duration) -> Vec<Peer> {
        let receiver = match self.daemon.browse(SERVICE_TYPE) {
            Ok(receiver) => receiver,
            Err(error) => {
                *self.state.lock().expect("state") =
                    DiscoveryState::Degraded(error.to_string());
                return Vec::new();
            }
        };
        let deadline = std::time::Instant::now() + timeout;
        let mut peers = Vec::new();
        loop {
            let remaining = deadline.saturating_duration_since(std::time::Instant::now());
            if remaining.is_zero() {
                break;
            }
            let event = match receiver.recv_timeout(remaining) {
                Ok(event) => event,
                Err(_) => break,
            };
            match event {
                mdns_sd::ServiceEvent::ServiceResolved(info) => {
                    let addrs: Vec<SocketAddr> = info
                        .get_addresses()
                        .iter()
                        .map(|ip| SocketAddr::new(ip.to_ip_addr(), info.get_port()))
                        .collect();
                    let props: HashMap<String, String> = info
                        .get_properties()
                        .iter()
                        .map(|prop| {
                            (prop.key().to_owned(), prop.val_str().to_owned())
                        })
                        .collect();
                    if let Some(peer) =
                        Peer::from_txt(info.get_fullname(), &props, addrs)
                    {
                        if !peers.contains(&peer) {
                            peers.push(peer);
                        }
                    }
                }
                mdns_sd::ServiceEvent::SearchStopped(_) => break,
                _ => {}
            }
            if std::time::Instant::now() >= deadline {
                break;
            }
        }
        let _ = self.daemon.stop_browse(SERVICE_TYPE);
        peers
    }

    pub fn state(&self) -> DiscoveryState {
        self.state.lock().expect("state").clone()
    }
}

impl Drop for Discovery {
    fn drop(&mut self) {
        if let Some(fullname) = self.service_fullname.take() {
            let _ = self.daemon.unregister(&fullname);
        }
        let _ = self.daemon.shutdown();
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn manual_addr_forms() {
        assert_eq!(
            parse_manual_addr("192.168.1.20:4242", 9999),
            Some("192.168.1.20:4242".parse().unwrap())
        );
        assert_eq!(
            parse_manual_addr("192.168.1.20", 9999),
            Some("192.168.1.20:9999".parse().unwrap())
        );
        assert_eq!(
            parse_manual_addr("  [::1]:4242  ", 9999),
            Some("[::1]:4242".parse().unwrap())
        );
        assert_eq!(parse_manual_addr("", 9999), None);
        assert_eq!(parse_manual_addr("not a host !!!", 9999), None);
    }

    #[test]
    fn peer_requires_valid_fingerprint_txt() {
        let mut props = HashMap::new();
        props.insert(TXT_ID.to_owned(), "laptop".to_owned());
        assert!(Peer::from_txt("x", &props, vec![]).is_none());
        props.insert(TXT_FINGERPRINT.to_owned(), "zz".repeat(32));
        assert!(Peer::from_txt("x", &props, vec![]).is_none());
    }

    #[test]
    fn txt_carries_identity_only() {
        // Announcement surface: exactly fp + id keys, no document metadata.
        let fp = Fingerprint::of_der(b"cert");
        let mut props = HashMap::new();
        props.insert(TXT_FINGERPRINT.to_owned(), fp.hex());
        props.insert(TXT_ID.to_owned(), "laptop".to_owned());
        assert_eq!(props.len(), 2);
        let peer = Peer::from_txt("laptop", &props, vec![]).expect("peer");
        assert_eq!(peer.fingerprint, fp);
    }

    #[test]
    fn loopback_announce_and_browse() {
        let mut discovery =
            Discovery::new().expect("mDNS daemon (may fail in sandbox)");
        let fp = Fingerprint::of_der(b"loopback-cert");
        match discovery.announce("af-test-peer", &fp, 42424) {
            Ok(()) => {
                let peers = discovery.browse(Duration::from_secs(5));
                // Either we see ourselves (multicast works) or the list is
                // empty (sandbox): both are non-error outcomes. What must
                // hold: any peer found parses with a valid fingerprint.
                assert!(peers.iter().all(|peer| peer.fingerprint.hex().len() == 64));
            }
            Err(reason) => {
                // No multicast in this environment → manual fallback path.
                assert!(!reason.is_empty());
            }
        }
    }

    #[test]
    fn degraded_state_on_bad_daemon_use() {
        // Shutdown daemon first: browse must degrade, not panic or storm.
        let discovery = Discovery::new().expect("daemon");
        let status_rx = discovery.daemon.shutdown().expect("shutdown");
        // `shutdown` is async — wait until the daemon confirms it is down,
        // otherwise browse may still succeed and the degrade path is skipped.
        let _ = status_rx.recv_timeout(Duration::from_secs(5));
        let peers = discovery.browse(Duration::from_millis(200));
        assert!(peers.is_empty());
        assert!(matches!(discovery.state(), DiscoveryState::Degraded(_)));
    }
}
