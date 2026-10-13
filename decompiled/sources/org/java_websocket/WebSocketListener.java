package org.java_websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/WebSocketListener.class */
public interface WebSocketListener {
    ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket webSocket, Draft draft, ClientHandshake clientHandshake) throws InvalidDataException;

    void onWebsocketHandshakeReceivedAsClient(WebSocket webSocket, ClientHandshake clientHandshake, ServerHandshake serverHandshake) throws InvalidDataException;

    void onWebsocketHandshakeSentAsClient(WebSocket webSocket, ClientHandshake clientHandshake) throws InvalidDataException;

    void onWebsocketMessage(WebSocket webSocket, String str);

    void onWebsocketMessage(WebSocket webSocket, ByteBuffer byteBuffer);

    void onWebsocketOpen(WebSocket webSocket, Handshakedata handshakedata);

    void onWebsocketClose(WebSocket webSocket, int i, String str, boolean z);

    void onWebsocketClosing(WebSocket webSocket, int i, String str, boolean z);

    void onWebsocketCloseInitiated(WebSocket webSocket, int i, String str);

    void onWebsocketError(WebSocket webSocket, Exception exc);

    void onWebsocketPing(WebSocket webSocket, Framedata framedata);

    PingFrame onPreparePing(WebSocket webSocket);

    void onWebsocketPong(WebSocket webSocket, Framedata framedata);

    void onWriteDemand(WebSocket webSocket);

    InetSocketAddress getLocalSocketAddress(WebSocket webSocket);

    InetSocketAddress getRemoteSocketAddress(WebSocket webSocket);
}
