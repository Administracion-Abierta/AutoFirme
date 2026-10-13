package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/DerBoolean.class */
public class DerBoolean extends DecoderObject {
    private static final byte TAG_BOOLEAN = 1;
    private Boolean booleanValue = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        Tlv tmpTlv = new Tlv(getBytes());
        checkTag(tmpTlv.getTag());
        this.booleanValue = Boolean.valueOf(tmpTlv.getValue()[0] == 0);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 1;
    }

    public boolean getBooleanValue() {
        if (this.booleanValue == null) {
            throw new IllegalStateException("El valor del objeto boolean no esta establecido");
        }
        return this.booleanValue.booleanValue();
    }
}
