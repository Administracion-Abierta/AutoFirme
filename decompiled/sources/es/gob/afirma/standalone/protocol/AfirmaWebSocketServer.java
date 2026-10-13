package es.gob.afirma.standalone.protocol;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/AfirmaWebSocketServer.class */
public class AfirmaWebSocketServer extends WebSocketServer {
    private static final String ECHO_REQUEST_PREFIX = "echo=";
    private static final String ECHO_OK_RESPONSE = "OK";
    private static final String HEADER_BATCH_1 = "afirma://batch?";
    private static final String HEADER_BATCH_2 = "afirma://batch/?";
    protected String sessionId;
    private WebSocket wsClient;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static int protocolVersion = -1;

    public AfirmaWebSocketServer(int i, String str) {
        super(new InetSocketAddress(i));
        this.wsClient = null;
        setReuseAddr(true);
        this.sessionId = str;
        Runtime.getRuntime().addShutdownHook(new Thread() { // from class: es.gob.afirma.standalone.protocol.AfirmaWebSocketServer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    AfirmaWebSocketServer.this.stop();
                } catch (Exception e) {
                    AfirmaWebSocketServer.LOGGER.log(Level.SEVERE, "No se pudo detener la escucha en el puerto", (Throwable) e);
                }
            }
        });
    }

    @Override // org.java_websocket.server.WebSocketServer
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LOGGER.info("Apertura del socket del puerto " + getAddress().getPort());
        if (this.wsClient == null) {
            this.wsClient = webSocket;
        }
    }

    @Override // org.java_websocket.server.WebSocketServer
    public void onClose(WebSocket webSocket, int i, String str, boolean z) {
        LOGGER.info("Se ha cerrado la comunicacion con el socket del puerto " + getAddress().getPort() + " . Codigo: " + i + ": " + str);
        if (this.wsClient == null || this.wsClient.equals(webSocket)) {
            LOGGER.info("Cerramos la aplicacion");
            Runtime.getRuntime().halt(0);
        }
    }

    @Override // org.java_websocket.server.WebSocketServer
    public void onStart() {
        LOGGER.fine("Inicio del socket");
    }

    @Override // org.java_websocket.server.WebSocketServer
    public void onMessage(WebSocket webSocket, String str) {
        LOGGER.info("Recibimos una peticion en el socket del puerto: " + getAddress().getPort());
        if (str.startsWith(ECHO_REQUEST_PREFIX)) {
            broadcast(ECHO_OK_RESPONSE, Collections.singletonList(webSocket));
        } else {
            setConnectionLostTimeout(str.startsWith(HEADER_BATCH_1) || str.startsWith(HEADER_BATCH_2) ? 240 : 60);
            broadcast(ProtocolInvocationLauncher.launch(str, protocolVersion, true), Collections.singletonList(webSocket));
        }
    }

    @Override // org.java_websocket.server.WebSocketServer
    public void onError(WebSocket webSocket, Exception exc) {
        LOGGER.log(Level.SEVERE, "Error en el socket del puerto " + getAddress().getPort(), (Throwable) exc);
    }
}
