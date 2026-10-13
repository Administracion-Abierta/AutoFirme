package es.gob.jmulticard.card.iso7816four;

import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816four/OffsetOutsideEfException.class */
public final class OffsetOutsideEfException extends Iso7816FourCardException {
    private static final long serialVersionUID = 1900557624644154869L;

    OffsetOutsideEfException(StatusWord retCode, Apdu origin) {
        super(retCode, origin);
    }
}
