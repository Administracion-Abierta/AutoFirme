package org.java_websocket.exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/IncompleteException.class */
public class IncompleteException extends Exception {
    private static final long serialVersionUID = 7330519489840500997L;
    private final int preferredSize;

    public IncompleteException(int preferredSize) {
        this.preferredSize = preferredSize;
    }

    public int getPreferredSize() {
        return this.preferredSize;
    }
}
