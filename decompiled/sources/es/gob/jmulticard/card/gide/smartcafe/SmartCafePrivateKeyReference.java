package es.gob.jmulticard.card.gide.smartcafe;

import es.gob.jmulticard.card.PrivateKeyReference;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/gide/smartcafe/SmartCafePrivateKeyReference.class */
public final class SmartCafePrivateKeyReference implements PrivateKeyReference {
    private final int keyOrdinal;

    public SmartCafePrivateKeyReference(Integer ordinal) {
        if (ordinal == null) {
            throw new IllegalArgumentException("El ordinal de la clave no puede ser nulo");
        }
        this.keyOrdinal = ordinal.intValue();
    }

    public int getKeyOrdinal() {
        return this.keyOrdinal;
    }

    public String toString() {
        return "Clave privada para G&D SmartCafe (con Applet PKCS#15) con ordinal " + this.keyOrdinal;
    }
}
