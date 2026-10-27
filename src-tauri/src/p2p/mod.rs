// P6.2 QUIC transport with mDNS discovery (Refs #110).
//
// Two machines on the same LAN / Tailscale find each other and move a signed
// container without any relay. The signature is the security; the transport
// is not (DESKTOP-ROADMAP §5.5).
//
// Layout:
// - `fingerprint`: peer identity = sha256 of the QUIC TLS cert, hex + words.
// - `transport`: `quinn` QUIC, one stream per document, timeout + cancel.
//   Receives land via temp file + atomic rename; a cancelled transfer leaves
//   no partial file.
// - `discovery`: `mdns-sd` announce/browse plus manual `host:port` entry.
//   Announcements carry identity + fingerprint only, never file contents.

pub mod discovery;
pub mod fingerprint;
pub mod transport;
