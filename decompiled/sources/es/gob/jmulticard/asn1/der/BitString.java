package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/BitString.class */
public abstract class BitString extends DecoderObject {
    private static final byte TAG_BITSTRING = 3;
    private Tlv tlv = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        this.tlv = new Tlv(getBytes());
        if (3 != this.tlv.getTag()) {
            throw new Asn1Exception("Se esperaba un TLV de tipo BitString pero se ha encontrado uno de tipo " + HexUtils.hexify(new byte[]{this.tlv.getTag()}, false));
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 3;
    }

    public final byte[] getValue() {
        if (this.tlv != null) {
            return this.tlv.getValue();
        }
        return null;
    }
}
