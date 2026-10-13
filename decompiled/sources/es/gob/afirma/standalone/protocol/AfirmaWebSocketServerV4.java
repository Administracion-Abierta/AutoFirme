package es.gob.afirma.standalone.protocol;

import java.net.InetAddress;
import java.util.Collections;
import org.java_websocket.WebSocket;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/AfirmaWebSocketServerV4.class */
public final class AfirmaWebSocketServerV4 extends AfirmaWebSocketServer {
    private static final int PROTOCOL_VERSION = 4;
    private static final String ECHO_REQUEST_PREFIX = "echo=";
    private static final String ECHO_REQUEST_SUFFIX = "@EOF";
    private static final String IDSESSION_PARAM_PREFIX = "idsession=";
    private static final String ECHO_OK_RESPONSE = "OK";
    private static final String LOCALHOST_ADDRESS = "127.0.0.1";
    private static final String HEADER_BATCH_1 = "afirma://batch?";
    private static final String HEADER_BATCH_2 = "afirma://batch/?";

    public AfirmaWebSocketServerV4(int i, String str) {
        super(i, str);
    }

    @Override // es.gob.afirma.standalone.protocol.AfirmaWebSocketServer, org.java_websocket.server.WebSocketServer
    public void onMessage(WebSocket webSocket, String str) {
        LOGGER.info("Recibimos una peticion en el socket del puerto " + getAddress().getPort());
        InetAddress address = webSocket.getRemoteSocketAddress().getAddress();
        if (address == null || !isLocalAddress(address)) {
            LOGGER.warning("Peticion al socket desde IP externa o sin identificar: " + address);
            broadcast(ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_47"), Collections.singletonList(webSocket));
        } else if (this.sessionId != null && !this.sessionId.equals(getSessionId(str))) {
            LOGGER.warning("La peticion no incluia el id de sesion correcto");
            broadcast(ProtocolInvocationLauncherErrorManager.getErrorMessage("SAF_46"), Collections.singletonList(webSocket));
        } else if (str.startsWith(ECHO_REQUEST_PREFIX)) {
            broadcast(ECHO_OK_RESPONSE, Collections.singletonList(webSocket));
        } else {
            setConnectionLostTimeout(str.startsWith(HEADER_BATCH_1) || str.startsWith(HEADER_BATCH_2) ? 240 : 60);
            broadcast(ProtocolInvocationLauncher.launch(str, 4, true), Collections.singletonList(webSocket));
        }
    }

    private static boolean isLocalAddress(InetAddress inetAddress) {
        return inetAddress != null && LOCALHOST_ADDRESS.equals(inetAddress.getHostAddress());
    }

    private static String getSessionId(String str) {
        String strSubstring = null;
        int iIndexOf = str.indexOf(IDSESSION_PARAM_PREFIX);
        if (iIndexOf > -1) {
            int length = iIndexOf + IDSESSION_PARAM_PREFIX.length();
            int iIndexOf2 = str.indexOf("&", length);
            if (iIndexOf2 > -1) {
                strSubstring = str.substring(length, iIndexOf2);
            } else {
                strSubstring = str.substring(length);
                if (strSubstring.endsWith(ECHO_REQUEST_SUFFIX)) {
                    strSubstring = strSubstring.substring(0, strSubstring.length() - ECHO_REQUEST_SUFFIX.length());
                }
            }
        }
        return strSubstring;
    }
}
