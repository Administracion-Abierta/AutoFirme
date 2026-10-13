package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.card.icao.IcaoException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/PaceException.class */
public class PaceException extends IcaoException {
    private static final long serialVersionUID = 6633945897491338530L;

    public PaceException(StatusWord retCode, Apdu origin, String description) {
        super(retCode, origin, description);
    }

    public PaceException(String description, Throwable e) {
        super(description, e);
    }

    public PaceException(String description) {
        super(description);
    }
}
