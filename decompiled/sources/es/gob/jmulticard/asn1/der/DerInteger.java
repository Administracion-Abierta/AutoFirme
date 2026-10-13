package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/DerInteger.class */
public class DerInteger extends DecoderObject {
    private static final byte TAG_INTEGER = 2;
    private BigInteger value = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException {
        this.value = new BigInteger(new Tlv(getBytes()).getValue());
    }

    public BigInteger getIntegerValue() {
        if (this.value == null) {
            throw new IllegalStateException("El valor del objeto aun no esta establecido");
        }
        return this.value;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 2;
    }

    public String toString() {
        return getIntegerValue().toString();
    }
}
