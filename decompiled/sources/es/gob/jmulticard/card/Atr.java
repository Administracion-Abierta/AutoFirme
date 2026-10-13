package es.gob.jmulticard.card;

import es.gob.jmulticard.HexUtils;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/Atr.class */
public class Atr implements Serializable {
    private static final long serialVersionUID = 1;
    protected final byte[] atrBytes;
    private final byte[] mask;

    public Atr(byte[] cardAtr, byte[] atrMask) {
        if (cardAtr == null || atrMask == null) {
            throw new IllegalArgumentException("El ATR y su mascara no pueden ser nulos");
        }
        this.atrBytes = new byte[cardAtr.length];
        System.arraycopy(cardAtr, 0, this.atrBytes, 0, cardAtr.length);
        this.mask = new byte[atrMask.length];
        System.arraycopy(atrMask, 0, this.mask, 0, atrMask.length);
    }

    public String toString() {
        return HexUtils.hexify(getBytes(), false);
    }

    public byte[] getMask() {
        return (byte[]) this.mask.clone();
    }

    public byte[] getBytes() {
        byte[] tmp = new byte[this.atrBytes.length];
        System.arraycopy(this.atrBytes, 0, tmp, 0, this.atrBytes.length);
        return tmp;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Atr)) {
            return false;
        }
        byte[] tmpAtrBytes = ((Atr) o).getBytes();
        if (this.atrBytes.length < tmpAtrBytes.length) {
            return false;
        }
        int offset = this.atrBytes.length - tmpAtrBytes.length;
        for (int i = tmpAtrBytes.length - 1; i >= 0; i--) {
            if ((this.atrBytes[i + offset] & this.mask[i + offset]) != (tmpAtrBytes[i] & this.mask[i + offset])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return hashCode(this.atrBytes) + hashCode(this.mask);
    }

    private static int hashCode(byte[] a) {
        if (a == null) {
            return 0;
        }
        int result = 1;
        for (byte element : a) {
            result = (31 * result) + element;
        }
        return result;
    }
}
