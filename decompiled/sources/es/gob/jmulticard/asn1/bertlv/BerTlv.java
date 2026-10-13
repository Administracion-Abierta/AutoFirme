package es.gob.jmulticard.asn1.bertlv;

import es.gob.jmulticard.HexUtils;
import java.io.ByteArrayInputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/bertlv/BerTlv.class */
public final class BerTlv {
    private BerTlvIdentifier tag;
    private int length;
    private byte[] value;

    public byte getTag() {
        return (byte) this.tag.getTagValue();
    }

    public byte[] getValue() {
        if (this.value == null) {
            return null;
        }
        byte[] out = new byte[this.value.length];
        System.arraycopy(this.value, 0, out, 0, this.value.length);
        return out;
    }

    public int getLength() {
        return this.length;
    }

    public static BerTlv createInstance(byte[] stream) {
        BerTlv tlv = new BerTlv();
        tlv.decode(new ByteArrayInputStream(stream));
        return tlv;
    }

    public static BerTlv createInstance(ByteArrayInputStream stream) {
        BerTlv tlv = new BerTlv();
        tlv.decode(stream);
        return tlv;
    }

    private void decode(ByteArrayInputStream stream) {
        this.tag = new BerTlvIdentifier();
        this.tag.decode(stream);
        int tmpLength = stream.read();
        if (tmpLength > 127 && tmpLength != 128) {
            int numberOfLengthOctets = tmpLength & 127;
            tmpLength = 0;
            for (int i = 0; i < numberOfLengthOctets; i++) {
                int nextLengthOctet = stream.read();
                tmpLength = (tmpLength << 8) | nextLengthOctet;
            }
        }
        this.length = tmpLength;
        if (this.length == 128) {
            stream.mark(0);
            int prevOctet = 1;
            int len = 0;
            while (true) {
                len++;
                int curOctet = stream.read();
                if (prevOctet == 0 && curOctet == 0) {
                    break;
                } else {
                    prevOctet = curOctet;
                }
            }
            int len2 = len - 2;
            this.value = new byte[len2];
            stream.reset();
            if (len2 != stream.read(this.value, 0, len2)) {
                throw new IndexOutOfBoundsException("La longitud de los datos leidos no coincide con el parametro indicado");
            }
            this.length = len2;
            return;
        }
        this.value = new byte[this.length];
        if (this.length != stream.read(this.value, 0, this.length)) {
            throw new IndexOutOfBoundsException("La longitud de los datos leidos no coincide con el parametro indicado");
        }
    }

    public String toString() {
        return "[TLV: T=" + this.tag + "; L=" + this.length + "d; V=" + (this.value == null ? "null" : HexUtils.hexify(this.value, false)) + "]";
    }
}
