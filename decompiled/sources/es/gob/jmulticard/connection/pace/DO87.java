package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.asn1.Tlv;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/DO87.class */
final class DO87 {
    private static final byte TAG = -121;
    private Tlv tlv;

    DO87() {
        this.tlv = null;
    }

    private static byte[] addOne(byte[] data) {
        byte[] ret = new byte[data.length + 1];
        System.arraycopy(data, 0, ret, 1, data.length);
        ret[0] = 1;
        return ret;
    }

    private static byte[] removeOne(byte[] value) {
        byte[] ret = new byte[value.length - 1];
        System.arraycopy(value, 1, ret, 0, ret.length);
        return ret;
    }

    DO87(byte[] encodedOrValue) {
        Tlv tmpTlv;
        this.tlv = null;
        if (encodedOrValue == null) {
            throw new IllegalArgumentException("Los datos para construir el DO8E no pueden ser nulos");
        }
        try {
            tmpTlv = new Tlv(encodedOrValue);
        } catch (Exception e) {
            tmpTlv = null;
        }
        if (tmpTlv != null && (TAG != tmpTlv.getTag() || tmpTlv.getValue()[0] != 1)) {
            tmpTlv = null;
        }
        this.tlv = tmpTlv == null ? new Tlv((byte) -121, addOne(encodedOrValue)) : tmpTlv;
    }

    byte[] getEncoded() {
        return this.tlv.getBytes();
    }

    byte[] getData() {
        return removeOne(this.tlv.getValue());
    }
}
