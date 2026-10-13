package es.gob.jmulticard.apdu;

import es.gob.jmulticard.HexUtils;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/StatusWord.class */
public final class StatusWord implements Serializable {
    private static final long serialVersionUID = -735824987343408119L;
    private byte msb;
    private byte lsb;

    public StatusWord(byte mostSigniticativeByte, byte lessSignificativeByte) {
        this.msb = (byte) 0;
        this.lsb = (byte) 0;
        this.msb = mostSigniticativeByte;
        this.lsb = lessSignificativeByte;
    }

    public byte getMsb() {
        return this.msb;
    }

    public byte getLsb() {
        return this.lsb;
    }

    public byte[] getBytes() {
        return new byte[]{this.msb, this.lsb};
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StatusWord)) {
            return false;
        }
        StatusWord other = (StatusWord) obj;
        return this.lsb == other.lsb && this.msb == other.msb;
    }

    public int hashCode() {
        return HexUtils.getShort(new byte[]{this.msb, this.lsb}, 0);
    }

    public String toString() {
        return StandardErrorCodes.getErrorDescription(this);
    }

    public boolean isOk() {
        return this.msb == -112 && this.lsb == 0;
    }
}
