package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.asn1.Tlv;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/DO97.class */
final class DO97 {
    private static final byte TAG = -105;
    private final Tlv tlv;

    DO97(int le) {
        this.tlv = new Tlv((byte) -105, new byte[]{(byte) le});
    }

    byte[] getEncoded() {
        return this.tlv.getBytes();
    }
}
