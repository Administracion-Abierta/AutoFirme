package es.gob.jmulticard.card.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.PrivateKeyReference;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/fnmt/ceres/CeresPrivateKeyReference.class */
public final class CeresPrivateKeyReference implements PrivateKeyReference {
    private final byte reference;
    private final int keySize;

    public CeresPrivateKeyReference(byte r, int kSize) {
        this.reference = r;
        this.keySize = kSize;
    }

    public byte getKeyReference() {
        return this.reference;
    }

    public int getKeyBitSize() {
        return this.keySize;
    }

    public String toString() {
        return "Clave privada de tarjeta CERES de " + this.keySize + " bits con referencia: 0x" + HexUtils.hexify(new byte[]{this.reference}, false);
    }
}
