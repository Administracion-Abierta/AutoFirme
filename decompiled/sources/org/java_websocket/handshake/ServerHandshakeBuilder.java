package org.java_websocket.handshake;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/handshake/ServerHandshakeBuilder.class */
public interface ServerHandshakeBuilder extends HandshakeBuilder, ServerHandshake {
    void setHttpStatus(short s);

    void setHttpStatusMessage(String str);
}
