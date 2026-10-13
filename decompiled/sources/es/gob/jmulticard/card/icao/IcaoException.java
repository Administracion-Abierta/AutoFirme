package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/IcaoException.class */
public class IcaoException extends Iso7816FourCardException {
    private static final long serialVersionUID = -4567446645471517825L;

    public IcaoException(StatusWord retCode, Apdu origin, String description) {
        super(retCode, origin, description);
    }

    public IcaoException(String description, Throwable e) {
        super(description, e);
    }

    public IcaoException(String description) {
        super(description);
    }
}
