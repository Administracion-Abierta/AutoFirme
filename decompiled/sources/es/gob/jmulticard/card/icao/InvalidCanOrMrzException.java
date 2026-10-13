package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/InvalidCanOrMrzException.class */
public final class InvalidCanOrMrzException extends IcaoException {
    private static final long serialVersionUID = 8254462304692038281L;

    public InvalidCanOrMrzException(String description) {
        super(description);
    }

    public InvalidCanOrMrzException(StatusWord retCode, Apdu origin, String description) {
        super(retCode, origin, description);
    }
}
