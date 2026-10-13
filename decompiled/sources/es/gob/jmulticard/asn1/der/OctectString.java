package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/OctectString.class */
public class OctectString extends DecoderObject {
    private static final byte TAG_OCTECTSTRING = 4;
    private byte[] value = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException {
        Tlv tlv = new Tlv(getBytes());
        if (4 != tlv.getTag()) {
            throw new TlvException("Se esperaba un TLV de tipo OctectString pero se ha encontrado uno de tipo " + HexUtils.hexify(new byte[]{tlv.getTag()}, false));
        }
        this.value = tlv.getValue();
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 4;
    }

    public byte[] getOctectStringByteValue() {
        byte[] out = new byte[this.value.length];
        System.arraycopy(this.value, 0, out, 0, this.value.length);
        return out;
    }
}
