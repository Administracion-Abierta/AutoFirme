package org.java_websocket.exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/NotSendableException.class */
public class NotSendableException extends RuntimeException {
    private static final long serialVersionUID = -6468967874576651628L;

    public NotSendableException(String s) {
        super(s);
    }

    public NotSendableException(Throwable t) {
        super(t);
    }

    public NotSendableException(String s, Throwable t) {
        super(s, t);
    }
}
