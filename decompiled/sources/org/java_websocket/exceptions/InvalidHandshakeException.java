package org.java_websocket.exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/InvalidHandshakeException.class */
public class InvalidHandshakeException extends InvalidDataException {
    private static final long serialVersionUID = -1426533877490484964L;

    public InvalidHandshakeException() {
        super(1002);
    }

    public InvalidHandshakeException(String s, Throwable t) {
        super(1002, s, t);
    }

    public InvalidHandshakeException(String s) {
        super(1002, s);
    }

    public InvalidHandshakeException(Throwable t) {
        super(1002, t);
    }
}
