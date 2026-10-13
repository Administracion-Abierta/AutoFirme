package es.gob.jmulticard.card.iso7816four;

import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816four/RequiredSecurityStateNotSatisfiedException.class */
public final class RequiredSecurityStateNotSatisfiedException extends Iso7816FourCardException {
    private static final long serialVersionUID = -5145858128531217344L;

    RequiredSecurityStateNotSatisfiedException(StatusWord retCode) {
        super("Condicion de seguridad no satisfecha", retCode);
    }
}
