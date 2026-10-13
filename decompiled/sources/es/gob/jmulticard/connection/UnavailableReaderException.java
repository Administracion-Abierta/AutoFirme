package es.gob.jmulticard.connection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/UnavailableReaderException.class */
public final class UnavailableReaderException extends ApduConnectionException {
    private static final long serialVersionUID = 4033742751748929273L;

    public UnavailableReaderException(String message) {
        super(message);
    }

    public UnavailableReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
