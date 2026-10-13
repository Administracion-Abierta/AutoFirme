package org.java_websocket.handshake;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/handshake/ServerHandshake.class */
public interface ServerHandshake extends Handshakedata {
    short getHttpStatus();

    String getHttpStatusMessage();
}
