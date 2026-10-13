package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/DO99.class */
final class DO99 {
    private static final byte TAG = -103;
    private final Tlv tlv;

    DO99(byte[] encodedData) throws SecureMessagingException {
        try {
            this.tlv = new Tlv(encodedData);
            if (TAG != this.tlv.getTag()) {
                throw new SecureMessagingException("Los datos proporcionados para el DO99 conforman un TLV con una etiqueta desconocida: " + HexUtils.hexify(new byte[]{this.tlv.getTag()}, false));
            }
        } catch (TlvException e) {
            throw new SecureMessagingException("Los datos proporcionados para el DO99 no conforman un TLV valido", e);
        }
    }

    byte[] getEncoded() {
        return this.tlv.getBytes();
    }

    byte[] getData() {
        return this.tlv.getValue();
    }
}
