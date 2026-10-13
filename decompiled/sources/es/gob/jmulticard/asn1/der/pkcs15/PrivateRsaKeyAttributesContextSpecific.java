package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.der.ContextSpecific;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PrivateRsaKeyAttributesContextSpecific.class */
public final class PrivateRsaKeyAttributesContextSpecific extends ContextSpecific {
    private static final byte TAG = -95;

    public PrivateRsaKeyAttributesContextSpecific() {
        super(PrivateRsaKeyAttributes.class);
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (TAG != tag) {
            throw new Asn1Exception("PrivateRsaKeyAttributesContextSpecific esperaba una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{TAG}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    public String toString() {
        return getObject().toString();
    }

    String getPath() {
        return ((PrivateRsaKeyAttributes) getObject()).getPath();
    }

    int getKeyLength() {
        return ((PrivateRsaKeyAttributes) getObject()).getKeyLength();
    }
}
