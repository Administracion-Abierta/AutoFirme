package es.gob.jmulticard.asn1.bertlv;

import es.gob.jmulticard.HexUtils;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/bertlv/BerTlvIdentifier.class */
final class BerTlvIdentifier {
    private byte[] value;

    BerTlvIdentifier() {
    }

    int getTagValue() {
        if (this.value == null) {
            return 0;
        }
        if (this.value.length == 1) {
            return this.value[0];
        }
        byte[] tagBytes = new byte[this.value.length - 1];
        System.arraycopy(this.value, 1, tagBytes, 0, this.value.length - 1);
        for (int i = 0; i < tagBytes.length - 1; i++) {
            tagBytes[i] = (byte) BitManipulationHelper.setBitValue(tagBytes[i], 8, false);
        }
        return new BigInteger(tagBytes).intValue();
    }

    void decode(ByteArrayInputStream stream) {
        boolean lastOctet;
        int tlvIdFirstOctet = stream.read();
        this.value = new byte[]{(byte) tlvIdFirstOctet};
        if ((tlvIdFirstOctet & 31) == 31) {
            do {
                int tlvIdNextOctet = stream.read();
                lastOctet = false;
                if (!BitManipulationHelper.getBitValue(tlvIdNextOctet, 8)) {
                    lastOctet = true;
                }
                this.value = BitManipulationHelper.mergeArrays(this.value, new byte[]{(byte) tlvIdNextOctet});
            } while (!lastOctet);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BerTlvIdentifier) {
            return HexUtils.arrayEquals(this.value, ((BerTlvIdentifier) obj).value);
        }
        return false;
    }

    public int hashCode() {
        return new BigInteger(this.value).intValue();
    }

    public String toString() {
        if (this.value == null) {
            return "NULL";
        }
        StringBuilder buf = new StringBuilder("[");
        for (byte element : this.value) {
            buf.append("0x").append(Integer.toHexString(element)).append(' ');
        }
        buf.append(']');
        return buf.toString();
    }
}
