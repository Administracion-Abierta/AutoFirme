package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OidDictionary;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/ObjectIdentifier.class */
public final class ObjectIdentifier extends DecoderObject {
    private static final byte TAG_OBJECTID = 6;
    private static final byte RELATIVE_OID = 13;
    private byte[] rawValue = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (6 != tag && 13 != tag) {
            throw new Asn1Exception("Se esperaba un tipo " + HexUtils.hexify(new byte[]{6}, false) + " o " + HexUtils.hexify(new byte[]{13}, false) + " (" + getClass().getName() + ") pero se encontro un tipo " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException {
        this.rawValue = new Tlv(getBytes()).getValue();
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 6;
    }

    public String toString() {
        if (this.rawValue == null) {
            throw new IllegalStateException("El valor del OID no esta establecido");
        }
        return OidDictionary.getOidDescription(this.rawValue);
    }
}
