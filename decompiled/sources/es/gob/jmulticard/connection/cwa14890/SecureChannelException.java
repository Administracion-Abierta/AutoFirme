package es.gob.jmulticard.connection.cwa14890;

import es.gob.jmulticard.connection.ApduConnectionException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/SecureChannelException.class */
public final class SecureChannelException extends ApduConnectionException {
    private static final long serialVersionUID = 3618976402641614649L;

    public SecureChannelException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SecureChannelException(String msg) {
        super(msg);
    }
}
