package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/DO8E.class */
final class DO8E {
    private static final byte TAG = -114;
    private final Tlv tlv;

    DO8E(byte[] checksumOrEncoded) throws SecureMessagingException {
        if (checksumOrEncoded == null) {
            throw new IllegalArgumentException("El checksum para el DO8E no puede ser nulo");
        }
        if (checksumOrEncoded.length == 10) {
            try {
                this.tlv = new Tlv(checksumOrEncoded);
                if (TAG != this.tlv.getTag()) {
                    throw new SecureMessagingException("Los datos proporcionados para el DO8E conforman un TLV con una etiqueta desconocida: " + HexUtils.hexify(new byte[]{this.tlv.getTag()}, false));
                }
                return;
            } catch (TlvException e) {
                throw new SecureMessagingException("Los datos proporcionados no son un TLV valido", e);
            }
        }
        this.tlv = new Tlv((byte) -114, checksumOrEncoded);
    }

    byte[] getEncoded() {
        return this.tlv.getBytes();
    }

    byte[] getData() {
        return this.tlv.getValue();
    }
}
