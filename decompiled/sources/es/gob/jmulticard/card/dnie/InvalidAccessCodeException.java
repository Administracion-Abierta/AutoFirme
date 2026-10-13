package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.connection.ApduConnectionException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/InvalidAccessCodeException.class */
public class InvalidAccessCodeException extends ApduConnectionException {
    private static final long serialVersionUID = 5878922643296036682L;

    public InvalidAccessCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAccessCodeException(String message) {
        super(message);
    }

    public InvalidAccessCodeException(Throwable cause) {
        super(cause);
    }
}
