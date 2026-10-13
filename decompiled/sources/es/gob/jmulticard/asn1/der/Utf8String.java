package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/Utf8String.class */
public final class Utf8String extends DecoderObject {
    private static final byte TAG_UTF8STRING = 12;
    private static final byte TAG_PRINTABLESTRING = 19;
    private static final byte TAG_T61STRING = 20;
    private String stringValue = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 12;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (12 != tag && 19 != tag && 20 != tag) {
            throw new Asn1Exception("Se esperaba un tipo " + HexUtils.hexify(new byte[]{19}, false) + ", " + HexUtils.hexify(new byte[]{20}, false) + " o " + HexUtils.hexify(new byte[]{12}, false) + " (" + getClass().getName() + ") pero se encontro un tipo " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        Tlv tlv = new Tlv(getBytes());
        checkTag(tlv.getTag());
        this.stringValue = new String(tlv.getValue(), StandardCharsets.UTF_8);
    }

    public String toString() {
        return this.stringValue;
    }
}
