package es.gob.jmulticard.connection;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduConnectionException.class */
public class ApduConnectionException extends IOException {
    private static final long serialVersionUID = 8002087406820820877L;

    public ApduConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApduConnectionException(String message) {
        super(message);
    }

    public ApduConnectionException(Throwable cause) {
        super(cause);
    }
}
