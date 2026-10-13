package org.java_websocket;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.java_websocket.drafts.Draft;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/WebSocketServerFactory.class */
public interface WebSocketServerFactory extends WebSocketFactory {
    @Override // org.java_websocket.WebSocketFactory
    WebSocketImpl createWebSocket(WebSocketAdapter webSocketAdapter, Draft draft);

    @Override // org.java_websocket.WebSocketFactory
    WebSocketImpl createWebSocket(WebSocketAdapter webSocketAdapter, List<Draft> list);

    ByteChannel wrapChannel(SocketChannel socketChannel, SelectionKey selectionKey) throws IOException;

    void close();

    @Override // org.java_websocket.WebSocketFactory
    /* bridge */ /* synthetic */ default WebSocket createWebSocket(WebSocketAdapter webSocketAdapter, List list) {
        return createWebSocket(webSocketAdapter, (List<Draft>) list);
    }
}
