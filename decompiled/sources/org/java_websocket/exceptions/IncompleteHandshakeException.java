package org.java_websocket.exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/IncompleteHandshakeException.class */
public class IncompleteHandshakeException extends RuntimeException {
    private static final long serialVersionUID = 7906596804233893092L;
    private final int preferredSize;

    public IncompleteHandshakeException(int preferredSize) {
        this.preferredSize = preferredSize;
    }

    public IncompleteHandshakeException() {
        this.preferredSize = 0;
    }

    public int getPreferredSize() {
        return this.preferredSize;
    }
}
