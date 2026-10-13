package org.java_websocket.handshake;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/handshake/HandshakeImpl1Server.class */
public class HandshakeImpl1Server extends HandshakedataImpl1 implements ServerHandshakeBuilder {
    private short httpstatus;
    private String httpstatusmessage;

    @Override // org.java_websocket.handshake.ServerHandshake
    public String getHttpStatusMessage() {
        return this.httpstatusmessage;
    }

    @Override // org.java_websocket.handshake.ServerHandshake
    public short getHttpStatus() {
        return this.httpstatus;
    }

    @Override // org.java_websocket.handshake.ServerHandshakeBuilder
    public void setHttpStatusMessage(String message) {
        this.httpstatusmessage = message;
    }

    @Override // org.java_websocket.handshake.ServerHandshakeBuilder
    public void setHttpStatus(short status) {
        this.httpstatus = status;
    }
}
