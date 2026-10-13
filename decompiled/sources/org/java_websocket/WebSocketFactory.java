package org.java_websocket;

import java.util.List;
import org.java_websocket.drafts.Draft;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/WebSocketFactory.class */
public interface WebSocketFactory {
    WebSocket createWebSocket(WebSocketAdapter webSocketAdapter, Draft draft);

    WebSocket createWebSocket(WebSocketAdapter webSocketAdapter, List<Draft> list);
}
