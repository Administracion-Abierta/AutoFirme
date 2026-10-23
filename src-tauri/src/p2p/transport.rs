// QUIC container transport, one stream per document (Refs #110).
//
// Security: self-signed cert per install, peer pins the [`Fingerprint`] via
// the discovery TXT record and confirms words out-of-band (TOFU, #111).
// Integrity of the *document* comes from the signature inside the container,
// not from this channel.
//
// Guarantees:
// - send: one bidirectional stream per document; 8-byte big-endian length
//   prefix then raw bytes; explicit timeout; `Cancel` aborts the stream.
// - receive: streams into `<dest>.part-<rand>` then atomically renames.
//   Timeout, peer cancel, or caller cancel deletes the partial file — a
//   cancelled transfer leaves no partial file behind.

use std::net::SocketAddr;
use std::path::{Path, PathBuf};
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::Arc;
use std::time::Duration;

use quinn::{ClientConfig, Endpoint, ServerConfig};
use rustls::client::danger::{HandshakeSignatureValid, ServerCertVerified, ServerCertVerifier};
use rustls::crypto::WebPkiSupportedAlgorithms;
use rustls::pki_types::{CertificateDer, PrivateKeyDer, PrivatePkcs8KeyDer, UnixTime};
use rustls::{DigitallySignedStruct, DistinguishedName, Error as TlsError, SignatureScheme};

use super::fingerprint::Fingerprint;

const ALPN: &[u8] = b"autofirme-p2p/1";
/// Long enough for LAN/Tailscale 100 MB moves, short enough to fail loudly.
pub const DEFAULT_TIMEOUT: Duration = Duration::from_secs(300);
const CHUNK: usize = 64 * 1024;

#[derive(Debug)]
pub enum TransportError {
    Timeout,
    Cancelled,
    Connection(String),
    Tls(String),
    Io(String),
    PartialRemoved(PathBuf),
}

impl std::fmt::Display for TransportError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Timeout => write!(f, "transfer timed out"),
            Self::Cancelled => write!(f, "transfer cancelled"),
            Self::Connection(reason) => write!(f, "connection failed: {reason}"),
            Self::Tls(reason) => write!(f, "TLS setup failed: {reason}"),
            Self::Io(reason) => write!(f, "I/O failed: {reason}"),
            Self::PartialRemoved(path) => {
                write!(f, "transfer incomplete, partial removed: {}", path.display())
            }
        }
    }
}

impl std::error::Error for TransportError {}

/// Pin `ring` as the process-wide rustls provider. Feature unification
/// (`rcgen` pulls `aws-lc-rs`, `quinn` defaults to `ring`) leaves rustls
/// unable to pick one automatically; this makes the choice explicit.
/// Idempotent: later calls are no-ops once a provider is installed.
fn ensure_provider() {
    let _ = rustls::crypto::ring::default_provider().install_default();
}

fn map_quinn(error: quinn::ConnectionError) -> TransportError {
    TransportError::Connection(error.to_string())
}

fn map_write(error: quinn::WriteError) -> TransportError {
    TransportError::Connection(error.to_string())
}

fn map_closed(_: quinn::ClosedStream) -> TransportError {
    TransportError::Connection("stream closed".to_owned())
}

fn map_read(error: quinn::ReadExactError) -> TransportError {
    TransportError::Connection(error.to_string())
}

/// Self-signed identity for this install. Keep the DER + key in the OS
/// keyring / app data (wiring is #111's TOFU store); this constructor only
/// mints fresh ones.
pub struct Identity {
    pub cert_der: Vec<u8>,
    pub key_der: Vec<u8>,
    pub fingerprint: Fingerprint,
}

impl Identity {
    pub fn generate(peer_name: &str) -> Result<Self, TransportError> {
        let certified = rcgen::generate_simple_self_signed(vec![peer_name.to_owned()])
            .map_err(|error| TransportError::Tls(error.to_string()))?;
        let cert_der = certified.cert.der().to_vec();
        let key_der = certified.signing_key.serialize_der();
        let fingerprint = Fingerprint::of_der(&cert_der);
        Ok(Self {
            cert_der,
            key_der,
            fingerprint,
        })
    }
}

fn server_config(cert_der: &[u8], key_der: &[u8]) -> Result<ServerConfig, TransportError> {
    ensure_provider();
    let cert = CertificateDer::from(cert_der.to_vec());
    let key = PrivateKeyDer::Pkcs8(PrivatePkcs8KeyDer::from(key_der.to_vec()));
    let mut tls = rustls::ServerConfig::builder()
        .with_no_client_auth()
        .with_single_cert(vec![cert], key)
        .map_err(|error| TransportError::Tls(error.to_string()))?;
    tls.alpn_protocols = vec![ALPN.to_vec()];
    Ok(ServerConfig::with_crypto(Arc::new(
        quinn::crypto::rustls::QuicServerConfig::try_from(tls)
            .map_err(|error| TransportError::Tls(error.to_string()))?,
    )))
}

#[derive(Debug)]
struct PinVerifier {
    expected: Fingerprint,
    /// Signature schemes the pin will accept; same set used to verify the
    /// handshake signature below.
    supported: WebPkiSupportedAlgorithms,
}

impl ServerCertVerifier for PinVerifier {
    fn verify_server_cert(
        &self,
        end_entity: &CertificateDer<'_>,
        _intermediates: &[CertificateDer<'_>],
        _server_name: &rustls::pki_types::ServerName<'_>,
        _ocsp_response: &[u8],
        _now: UnixTime,
    ) -> Result<ServerCertVerified, TlsError> {
        if Fingerprint::of_der(end_entity.as_ref()) == self.expected {
            Ok(ServerCertVerified::assertion())
        } else {
            Err(TlsError::General("peer fingerprint mismatch".to_owned()))
        }
    }

    fn verify_tls12_signature(
        &self,
        _message: &[u8],
        _cert: &CertificateDer<'_>,
        _dss: &DigitallySignedStruct,
    ) -> Result<HandshakeSignatureValid, TlsError> {
        Err(TlsError::General("TLS 1.2 not accepted".to_owned()))
    }

    fn verify_tls13_signature(
        &self,
        message: &[u8],
        cert: &CertificateDer<'_>,
        dss: &DigitallySignedStruct,
    ) -> Result<HandshakeSignatureValid, TlsError> {
        // Trust in *who* we talk to comes from the pin above (sha256 of the
        // DER). The handshake signature is still verified: without it, an
        // attacker that serves the victim's cert DER with a key of their own
        // (`CertifiedKey::new` + `with_cert_resolver` skips rustls'
        // `InconsistentKeys` check) would pass the pin and read the payload.
        rustls::crypto::verify_tls13_signature(message, cert, dss, &self.supported)
    }

    fn supported_verify_schemes(&self) -> Vec<SignatureScheme> {
        self.supported.supported_schemes()
    }

    fn root_hint_subjects(&self) -> Option<&[DistinguishedName]> {
        None
    }
}

fn client_config(expected: Fingerprint) -> Result<ClientConfig, TransportError> {
    ensure_provider();
    let supported = rustls::crypto::ring::default_provider().signature_verification_algorithms;
    let mut tls = rustls::ClientConfig::builder()
        .dangerous()
        .with_custom_certificate_verifier(Arc::new(PinVerifier {
            expected,
            supported,
        }))
        .with_no_client_auth();
    tls.alpn_protocols = vec![ALPN.to_vec()];
    Ok(ClientConfig::new(Arc::new(
        quinn::crypto::rustls::QuicClientConfig::try_from(tls)
            .map_err(|error| TransportError::Tls(error.to_string()))?,
    )))
}

/// Cancellation flag shared between UI/task and the running transfer.
#[derive(Clone, Debug, Default)]
pub struct Cancel(Arc<AtomicBool>);

impl Cancel {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn cancel(&self) {
        self.0.store(true, Ordering::SeqCst);
    }

    fn is_cancelled(&self) -> bool {
        self.0.load(Ordering::SeqCst)
    }
}

/// A bound QUIC endpoint that can send documents and accept one stream each.
pub struct Transport {
    endpoint: Endpoint,
}

impl Transport {
    /// Bind on `bind` (port 0 = OS-assigned) with this install's identity.
    pub fn bind(
        bind: SocketAddr,
        identity: &Identity,
    ) -> Result<Self, TransportError> {
        let config = server_config(&identity.cert_der, &identity.key_der)?;
        let endpoint = Endpoint::server(config, bind)
            .map_err(|error| TransportError::Io(error.to_string()))?;
        Ok(Self { endpoint })
    }

    pub fn local_addr(&self) -> Result<SocketAddr, TransportError> {
        self.endpoint
            .local_addr()
            .map_err(|error| TransportError::Io(error.to_string()))
    }

    /// Send `data` to `peer` (whose cert pins `expected_fp`) on one stream:
    /// 8-byte big-endian length, then bytes. Timeout covers the whole send.
    pub async fn send(
        &self,
        peer: SocketAddr,
        expected_fp: Fingerprint,
        data: &[u8],
        timeout: Duration,
        cancel: &Cancel,
    ) -> Result<u64, TransportError> {
        let config = client_config(expected_fp)?;
        let connecting = self
            .endpoint
            .connect_with(config, peer, "autofirme")
            .map_err(|error| TransportError::Connection(error.to_string()))?;
        let conn = tokio::time::timeout(timeout, connecting)
            .await
            .map_err(|_| TransportError::Timeout)?
            .map_err(map_quinn)?;
        let (mut send_stream, _recv_stream) =
            tokio::time::timeout(timeout, conn.open_bi())
                .await
                .map_err(|_| TransportError::Timeout)?
                .map_err(map_quinn)?;
        let total = data.len() as u64;
        let send = async {
            send_stream
                .write_all(&total.to_be_bytes())
                .await
                .map_err(map_write)?;
            for chunk in data.chunks(CHUNK) {
                if cancel.is_cancelled() {
                    let _ = send_stream.reset(0u32.into());
                    return Err::<(), TransportError>(TransportError::Cancelled);
                }
                send_stream.write_all(chunk).await.map_err(map_write)?;
            }
            send_stream.finish().map_err(map_closed)?;
            // Wait for the peer's ack so a dropped receiver is not silent.
            let _ = send_stream.stopped().await;
            Ok::<(), TransportError>(())
        };
        tokio::time::timeout(timeout, send)
            .await
            .map_err(|_| TransportError::Timeout)??;
        conn.close(0u32.into(), b"done");
        Ok(total)
    }

    /// Accept one document stream and store it at `dest` atomically.
    /// Returns the byte count. Any failure (timeout, cancel, reset) removes
    /// the partial file before returning the error.
    pub async fn recv_one(
        &self,
        dest: &Path,
        timeout: Duration,
        cancel: &Cancel,
    ) -> Result<u64, TransportError> {
        let accept = async {
            let conn = self
                .endpoint
                .accept()
                .await
                .ok_or_else(|| TransportError::Connection("endpoint closed".to_owned()))?
                .await
                .map_err(map_quinn)?;
            let (_send_stream, recv_stream) = conn
                .accept_bi()
                .await
                .map_err(map_quinn)?;
            Ok::<_, TransportError>((conn, recv_stream))
        };
        let (conn, mut recv_stream) = tokio::time::timeout(timeout, accept)
            .await
            .map_err(|_| TransportError::Timeout)??;

        let partial = partial_path(dest);
        let recv = async {
            let mut len_buf = [0u8; 8];
            recv_stream
                .read_exact(&mut len_buf)
                .await
                .map_err(map_read)?;
            let total = u64::from_be_bytes(len_buf);
            // 1 GiB sanity cap: containers are ~100 MB; anything larger is a
            // corrupt length, not a document. Resumable multi-GB is out of
            // scope for #110.
            if total > 1024 * 1024 * 1024 {
                return Err::<u64, TransportError>(TransportError::Connection(
                    "declared length over 1 GiB".to_owned(),
                ));
            }
            let mut file = tokio::fs::File::create(&partial)
                .await
                .map_err(|error| TransportError::Io(error.to_string()))?;
            let mut remaining = total;
            // One buffer reused across chunks: no per-64 KiB allocation.
            let mut buf = vec![0u8; CHUNK];
            use tokio::io::AsyncWriteExt;
            while remaining > 0 {
                if cancel.is_cancelled() {
                    return Err::<u64, TransportError>(TransportError::Cancelled);
                }
                let want = remaining.min(CHUNK as u64) as usize;
                recv_stream
                    .read_exact(&mut buf[..want])
                    .await
                    .map_err(map_read)?;
                file.write_all(&buf[..want])
                    .await
                    .map_err(|error| TransportError::Io(error.to_string()))?;
                remaining -= want as u64;
            }
            file.flush()
                .await
                .map_err(|error| TransportError::Io(error.to_string()))?;
            drop(file);
            tokio::fs::rename(&partial, dest)
                .await
                .map_err(|error| TransportError::Io(error.to_string()))?;
            // Ack so the sender's `stopped()` resolves.
            let _ = recv_stream.stop(0u32.into());
            conn.close(0u32.into(), b"done");
            Ok::<u64, TransportError>(total)
        };
        // Close the connection on every failure path: dropping `conn` only
        // implicit-closes when no other handle holds it, so an explicit
        // close is what promptly tells the peer we are gone (review #110).
        match tokio::time::timeout(timeout, recv).await {
            Ok(Ok(total)) => Ok(total),
            Ok(Err(recv_error)) => {
                conn.close(0u32.into(), b"error");
                let _ = tokio::fs::remove_file(&partial).await;
                Err(match recv_error {
                    TransportError::Cancelled => {
                        TransportError::PartialRemoved(partial.clone())
                    }
                    other => other,
                })
            }
            Err(_) => {
                conn.close(0u32.into(), b"timeout");
                let _ = tokio::fs::remove_file(&partial).await;
                Err(TransportError::Timeout)
            }
        }
    }
}

fn partial_path(dest: &Path) -> PathBuf {
    let mut name = dest
        .file_name()
        .map(|name| name.to_owned())
        .unwrap_or_default();
    name.push(format!(".part-{}", std::process::id()));
    dest.with_file_name(name)
}

#[cfg(test)]
mod tests {
    use super::*;

    fn test_runtime() -> tokio::runtime::Runtime {
        tokio::runtime::Builder::new_current_thread()
            .enable_all()
            .build()
            .expect("test runtime")
    }

    fn loopback_pair() -> (Transport, Transport, Fingerprint, Fingerprint) {
        let id_a = Identity::generate("peer-a").expect("identity a");
        let id_b = Identity::generate("peer-b").expect("identity b");
        let fp_a = id_a.fingerprint;
        let fp_b = id_b.fingerprint;
        let bind: SocketAddr = "127.0.0.1:0".parse().expect("loopback");
        let transport_a = Transport::bind(bind, &id_a).expect("bind a");
        let transport_b = Transport::bind(bind, &id_b).expect("bind b");
        (transport_a, transport_b, fp_a, fp_b)
    }

    #[test]
    fn loopback_send_recv_roundtrip() {
        test_runtime().block_on(async {
            let (sender, receiver, _fp_a, fp_b) = loopback_pair();
            let peer = receiver.local_addr().expect("receiver addr");
            let data: Vec<u8> = (0..256u32).map(|i| (i % 251) as u8).collect();
            let data = data.repeat(8 * 1024); // ~2 MB through the same code path as 100 MB
            let dir = std::env::temp_dir().join(format!("af-p2p-{}", std::process::id()));
            std::fs::create_dir_all(&dir).expect("temp dir");
            let dest = dir.join("doc.asics");
            let _ = std::fs::remove_file(&dest);

            let recv = tokio::spawn(async move {
                receiver
                    .recv_one(&dest, DEFAULT_TIMEOUT, &Cancel::new())
                    .await
            });
            let sent = sender
                .send(peer, fp_b, &data, DEFAULT_TIMEOUT, &Cancel::new())
                .await
                .expect("send ok");
            let received = recv.await.expect("join").expect("recv ok");
            assert_eq!(sent, data.len() as u64);
            assert_eq!(received, data.len() as u64);

            let back = std::fs::read(dir.join("doc.asics")).expect("read back");
            assert_eq!(back, data);
            std::fs::remove_dir_all(&dir).ok();
        });
    }

    #[test]
    fn cancel_leaves_no_partial_file() {
        test_runtime().block_on(async {
            let (sender, receiver, _fp_a, fp_b) = loopback_pair();
            let peer = receiver.local_addr().expect("receiver addr");
            let data = vec![0xABu8; 4 * 1024 * 1024];
            let dir = std::env::temp_dir().join(format!("af-p2p-cancel-{}", std::process::id()));
            std::fs::create_dir_all(&dir).expect("temp dir");
            let dest = dir.join("cancelled.asics");

            let cancel = Cancel::new();
            let sender_cancel = cancel.clone();
            // Cancel mid-flight from another task.
            let canceller = tokio::spawn(async move {
                tokio::time::sleep(Duration::from_millis(50)).await;
                sender_cancel.cancel();
            });
            let recv = tokio::spawn(async move {
                receiver
                    .recv_one(&dest, DEFAULT_TIMEOUT, &Cancel::new())
                    .await
            });
            let result = sender
                .send(peer, fp_b, &data, DEFAULT_TIMEOUT, &cancel)
                .await;
            let _ = canceller.await;
            let _ = recv.await;
            // Either the send was cancelled (partial removed on recv side) or
            // it raced through before the flag — in neither case may dest or a
            // .part file linger from an incomplete transfer.
            if result.is_err() {
                let entries: Vec<_> = std::fs::read_dir(&dir)
                    .expect("read dir")
                    .filter_map(|entry| entry.ok())
                    .collect();
                assert!(
                    entries.is_empty(),
                    "cancel must leave no partial file: {entries:?}"
                );
            }
            std::fs::remove_dir_all(&dir).ok();
        });
    }

    #[test]
    fn wrong_fingerprint_rejected() {
        test_runtime().block_on(async {
            let (sender, receiver, _fp_a, _fp_b) = loopback_pair();
            let peer = receiver.local_addr().expect("receiver addr");
            let wrong = Fingerprint([0u8; 32]);
            // Spawn the receiver so the handshake has a peer; it will time
            // out waiting for a stream the rejected client never opens.
            let _recv = tokio::spawn(async move {
                let dir = std::env::temp_dir();
                let dest = dir.join("unreachable.asics");
                receiver
                    .recv_one(&dest, Duration::from_millis(300), &Cancel::new())
                    .await
            });
            let result = sender
                .send(
                    peer,
                    wrong,
                    b"hello",
                    Duration::from_secs(5),
                    &Cancel::new(),
                )
                .await;
            assert!(result.is_err(), "pin mismatch must fail");
        });
    }

    #[test]
    fn odd_size_roundtrip_exact_bytes() {
        // Non-multiple-of-CHUNK payload: the reused buffer must send/receive
        // exactly `len` bytes via sliced reads/writes (review #110).
        test_runtime().block_on(async {
            let (sender, receiver, _fp_a, fp_b) = loopback_pair();
            let peer = receiver.local_addr().expect("receiver addr");
            let data: Vec<u8> = (0..CHUNK as u64 * 2 + 12345)
                .map(|i| (i % 251) as u8)
                .collect();
            let dir = std::env::temp_dir().join(format!("af-p2p-odd-{}", std::process::id()));
            std::fs::create_dir_all(&dir).expect("temp dir");
            let dest = dir.join("odd.asics");
            let _ = std::fs::remove_file(&dest);
            let recv = tokio::spawn(async move {
                receiver
                    .recv_one(&dest, DEFAULT_TIMEOUT, &Cancel::new())
                    .await
            });
            let sent = sender
                .send(peer, fp_b, &data, DEFAULT_TIMEOUT, &Cancel::new())
                .await
                .expect("send ok");
            let received = recv.await.expect("join").expect("recv ok");
            assert_eq!(sent, data.len() as u64);
            assert_eq!(received, data.len() as u64);
            assert_eq!(std::fs::read(dir.join("odd.asics")).expect("read back"), data);
            std::fs::remove_dir_all(&dir).ok();
        });
    }

    #[test]
    fn timeout_closes_conn_and_removes_partial() {
        // Sender stalls mid-stream: recv must time out, delete the partial,
        // and close the connection so the peer observes it (review #110).
        test_runtime().block_on(async {
            let id_b = Identity::generate("recv-timeout").expect("identity b");
            let bind: SocketAddr = "127.0.0.1:0".parse().expect("loopback");
            let receiver = Transport::bind(bind, &id_b).expect("bind");
            let peer = receiver.local_addr().expect("receiver addr");
            let dir = std::env::temp_dir().join(format!("af-p2p-timeout-{}", std::process::id()));
            std::fs::create_dir_all(&dir).expect("temp dir");
            let dest = dir.join("stalled.asics");
            // Spawn recv first so the handshake has a peer, then connect raw:
            // open a stream, declare 1 MiB, send no body.
            let recv = tokio::spawn(async move {
                receiver
                    .recv_one(&dest, Duration::from_millis(1500), &Cancel::new())
                    .await
            });
            let client_id = Identity::generate("staller").expect("identity a");
            let client = Transport::bind(bind, &client_id).expect("client bind");
            let config = client_config(id_b.fingerprint).expect("client config");
            let connecting = client
                .endpoint
                .connect_with(config, peer, "autofirme")
                .expect("connect");
            let conn = connecting.await.expect("handshake");
            let mut send = conn.open_bi().await.expect("open stream").0;
            send.write_all(&(1024u64 * 1024).to_be_bytes())
                .await
                .expect("write len");
            // Hold the stream open without the body; recv must time out.
            let result = recv.await.expect("join");
            assert!(matches!(result, Err(TransportError::Timeout)), "must time out: {result:?}");
            // Peer sees the close instead of hanging on an idle connection.
            let closed = tokio::time::timeout(Duration::from_secs(5), send.stopped()).await;
            assert!(closed.is_ok(), "sender never observed the recv-side close");
            let entries: Vec<_> = std::fs::read_dir(&dir)
                .expect("read dir")
                .filter_map(|entry| entry.ok())
                .collect();
            assert!(entries.is_empty(), "timeout must leave no partial: {entries:?}");
            std::fs::remove_dir_all(&dir).ok();
        });
    }

    #[test]
    fn mitm_without_key_fails_at_server_build() {
        // Attacker serves the victim's cert DER with their own key through
        // the ordinary `with_single_cert` path: rustls rejects the mismatched
        // pair before any handshake with `InconsistentKeys(KeyMismatch)`.
        // The resolver path that bypasses this check is covered by
        // `cert_resolver_impersonation_rejected_at_handshake` (review #110).
        let victim = Identity::generate("victim").expect("victim");
        let attacker = Identity::generate("attacker").expect("attacker");
        let err = server_config(&victim.cert_der, &attacker.key_der)
            .expect_err("mismatched key must fail");
        assert!(matches!(err, TransportError::Tls(_)), "wrong error kind: {err:?}");
    }

    #[test]
    fn cert_resolver_impersonation_rejected_at_handshake() {
        // `CertifiedKey::new` + `with_cert_resolver` skips rustls'
        // `InconsistentKeys` check, so an attacker can serve the victim's
        // cert DER with a key of their own, and the sha256 pin accepts it.
        // The TLS 1.3 handshake-signature check is what stops the
        // impersonation (review #110).
        #[derive(Debug)]
        struct Evil(Arc<rustls::sign::CertifiedKey>);
        impl rustls::server::ResolvesServerCert for Evil {
            fn resolve(
                &self,
                _hello: rustls::server::ClientHello<'_>,
            ) -> Option<Arc<rustls::sign::CertifiedKey>> {
                Some(self.0.clone())
            }
        }

        test_runtime().block_on(async {
            let victim = Identity::generate("victim").expect("victim");
            let attacker = Identity::generate("attacker").expect("attacker");
            let victim_fp = victim.fingerprint;

            let provider = rustls::crypto::ring::default_provider();
            let _ = provider.clone().install_default();
            let cert = CertificateDer::from(victim.cert_der.clone());
            let key = PrivateKeyDer::Pkcs8(PrivatePkcs8KeyDer::from(attacker.key_der.clone()));
            let signing_key = provider
                .key_provider
                .load_private_key(key)
                .expect("attacker key");
            let certified = rustls::sign::CertifiedKey::new(vec![cert], signing_key);
            let mut tls = rustls::ServerConfig::builder_with_provider(Arc::new(provider))
                .with_safe_default_protocol_versions()
                .expect("protocol versions")
                .with_no_client_auth()
                .with_cert_resolver(Arc::new(Evil(Arc::new(certified))));
            tls.alpn_protocols = vec![ALPN.to_vec()];
            let attacker_config = ServerConfig::with_crypto(Arc::new(
                quinn::crypto::rustls::QuicServerConfig::try_from(tls)
                    .expect("quic server config"),
            ));
            let bind: SocketAddr = "127.0.0.1:0".parse().expect("loopback");
            let endpoint = Endpoint::server(attacker_config, bind).expect("attacker endpoint");
            let attacker_addr = endpoint.local_addr().expect("addr");
            let attacker_task = tokio::spawn(async move {
                let connecting = endpoint.accept().await.expect("accept");
                let _ = connecting.await;
            });

            let client = Transport::bind(bind, &victim).expect("client bind");
            let sent = client
                .send(
                    attacker_addr,
                    victim_fp,
                    b"SECRET",
                    Duration::from_secs(10),
                    &Cancel::new(),
                )
                .await;
            assert!(
                sent.is_err(),
                "impersonation must be rejected, got {sent:?}"
            );
            let _ = attacker_task.await;
        });
    }
}
