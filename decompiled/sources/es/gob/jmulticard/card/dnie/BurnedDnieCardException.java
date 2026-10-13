package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.card.CardException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/BurnedDnieCardException.class */
public final class BurnedDnieCardException extends CardException {
    private static final long serialVersionUID = -3337211407421513080L;

    BurnedDnieCardException(Atr atr) {
        super(atr != null ? "Se encontro un DNIe, pero con la memoria volatil borrada (codigo 6581): " + HexUtils.hexify(atr.getBytes(), true) : "Se encontro un DNIe, pero con la memoria volatil borrada (codigo 6581)");
    }
}
