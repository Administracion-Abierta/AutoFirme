//! Loopback sign server: `axum` WebSocket on 127.0.0.1, TLS only (P2.2a, Refs #104).
//!
//! Reference (read-only): `AfirmaWebSocketServer` + `AfirmaWebSocketServerManager`
//! on tag `reference/decompile-1.9` — a WSS listener on `DEFAULT_WEBSOCKET_PORT`
//! speaking one-text-frame-in / one-text-frame-out (`echo=` → `OK`, otherwise an
//! `afirma://…` URL). Reproduced: bind address, port, framing contract, TLS-only
//! transport. Not reproduced: the v3 lack of origin checks stays (binding to
//! loopback IS the access control), but plaintext fallback can never exist here —
//! there is no plaintext listener, only `bind_rustls`.

pub mod protocol;

use std::net::SocketAddr;
use std::path::Path;
use std::sync::Arc;

use axum::extract::ws::{Message, WebSocketUpgrade};
use axum::response::Response;
use axum::routing::get;
use axum::Router;

/// Production bind: loopback only, never 0.0.0.0 (the access control).
pub fn loopback_addr() -> SocketAddr {
    SocketAddr::from(([127, 0, 0, 1], protocol::DEFAULT_WEBSOCKET_PORT))
}

/// The single-route router (`/` upgrades to the protocol-3 WebSocket loop).
pub fn router() -> Router {
    Router::new().route("/", get(ws_handler))
}

async fn ws_handler(ws: WebSocketUpgrade) -> Response {
    ws.on_upgrade(|mut socket| async move {
        while let Some(message) = socket.recv().await {
            match message {
                Ok(Message::Text(text)) => {
                    let reply = protocol::handle_frame(text.as_str());
                    if socket.send(Message::Text(reply.into())).await.is_err() {
                        break;
                    }
                }
                Ok(Message::Close(_)) | Err(_) => break,
                _ => {}
            }
        }
    })
}

/// TLS config for the loopback listener, from an install identity.
pub fn rustls_config(
    identity: &crate::tls::TlsIdentity,
) -> std::io::Result<axum_server::tls_rustls::RustlsConfig> {
    let mut config = crate::tls::server_config(identity)
        .map_err(|detail| std::io::Error::new(std::io::ErrorKind::Other, detail))?;
    config.alpn_protocols = vec![b"http/1.1".to_vec()];
    Ok(axum_server::tls_rustls::RustlsConfig::from_config(Arc::new(config)))
}

/// Serve forever on 127.0.0.1:63117 with the identity from `cert_dir`.
/// There is no plaintext path: the only listener is `bind_rustls`.
pub async fn run(cert_dir: &Path) -> std::io::Result<()> {
    let identity = crate::tls::load_or_generate(cert_dir)?;
    let config = rustls_config(&identity)?;
    axum_server::bind_rustls(loopback_addr(), config)
        .serve(router().into_make_service())
        .await
}

#[cfg(test)]
mod tests {
    use super::*;
    use futures_util::{SinkExt, StreamExt};

    type WsMessage = tokio_tungstenite::tungstenite::Message;

    /// Spawn the router on an ephemeral loopback port; returns the port.
    /// Production always uses 63117 ([`loopback_addr`]); tests take any free
    /// port so parallel runs never collide.
    async fn spawn_ephemeral(cert_dir: &Path) -> u16 {
        let identity = crate::tls::load_or_generate(cert_dir).expect("identity");
        let config = rustls_config(&identity).expect("server config");
        let listener = std::net::TcpListener::bind("127.0.0.1:0").expect("bind");
        listener.set_nonblocking(true).expect("nonblocking");
        let port = listener.local_addr().expect("addr").port();
        let server = axum_server::from_tcp_rustls(listener, config).expect("tls server");
        tokio::spawn(async move {
            let _ = server.serve(router().into_make_service()).await;
        });
        // Poke until the acceptor answers, so the client never races the bind.
        for _ in 0..100 {
            if tokio::net::TcpStream::connect(("127.0.0.1", port)).await.is_ok() {
                break;
            }
            tokio::time::sleep(std::time::Duration::from_millis(10)).await;
        }
        port
    }

    fn test_client(cert_der: &[u8]) -> tokio_tungstenite::Connector {
        let mut roots = rustls::RootCertStore::empty();
        roots
            .add(rustls::pki_types::CertificateDer::from(cert_der.to_vec()))
            .expect("root");
        let client = rustls::ClientConfig::builder()
            .with_root_certificates(roots)
            .with_no_client_auth();
        tokio_tungstenite::Connector::Rustls(Arc::new(client))
    }

    fn scratch_dir(name: &str) -> std::path::PathBuf {
        let dir = std::env::temp_dir().join(format!("autofirme-{name}-{}", std::process::id()));
        let _ = std::fs::remove_dir_all(&dir);
        dir
    }

    #[tokio::test]
    async fn tls_round_trip_echo_and_sign() {
        let dir = scratch_dir("srv");
        let identity = crate::tls::load_or_generate(&dir).expect("identity");
        let port = spawn_ephemeral(&dir).await;
        let url = format!("wss://127.0.0.1:{port}/");
        let (mut socket, _) = tokio_tungstenite::connect_async_tls_with_config(
            url,
            None,
            false,
            Some(test_client(&identity.cert_der)),
        )
        .await
        .expect("tls connect");
        socket
            .send(WsMessage::Text("echo=probe".into()))
            .await
            .expect("send");
        let reply = socket.next().await.expect("reply").expect("message");
        assert_eq!(reply.into_text().expect("text").as_str(), "OK");
        socket
            .send(WsMessage::Text("afirma://sign?dat=AA==&format=CAdES".into()))
            .await
            .expect("send");
        let reply = socket.next().await.expect("reply").expect("message");
        assert_eq!(reply.into_text().expect("text").as_str(), "OK");
        let _ = std::fs::remove_dir_all(&dir);
    }

    #[tokio::test]
    async fn plaintext_client_gets_no_http() {
        let dir = scratch_dir("plain");
        let port = spawn_ephemeral(&dir).await;
        let mut stream = tokio::net::TcpStream::connect(("127.0.0.1", port))
            .await
            .expect("tcp");
        use tokio::io::{AsyncReadExt, AsyncWriteExt};
        stream.write_all(b"GET / HTTP/1.0\r\n\r\n").await.expect("write");
        let mut buf = [0u8; 5];
        let outcome =
            tokio::time::timeout(std::time::Duration::from_secs(5), stream.read_exact(&mut buf)).await;
        match outcome {
            Ok(Ok(_)) => assert_ne!(
                &buf[..5],
                b"HTTP/",
                "server spoke plaintext HTTP to a plaintext client"
            ),
            Ok(Err(_)) | Err(_) => {} // closed / timed out: also a refusal
        }
        let _ = std::fs::remove_dir_all(&dir);
    }
}
