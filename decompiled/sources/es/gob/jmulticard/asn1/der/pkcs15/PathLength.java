package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.Record;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PathLength.class */
public final class PathLength extends ContextSpecific {
    private static final byte TAG = -128;
    private Integer value;

    int getIntValue() {
        return this.value.intValue();
    }

    public PathLength() {
        super(Record.class);
        this.value = null;
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException {
        this.value = Integer.valueOf(String.valueOf(HexUtils.getUnsignedInt(new Tlv(getBytes()).getValue(), 0)));
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (-128 != tag) {
            throw new Asn1Exception("PathLength esperaba una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{-128}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }
}
