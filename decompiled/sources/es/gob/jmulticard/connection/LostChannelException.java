package es.gob.jmulticard.connection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/LostChannelException.class */
public final class LostChannelException extends ApduConnectionException {
    private static final long serialVersionUID = -4881940145750512085L;

    public LostChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
