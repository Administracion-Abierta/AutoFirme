package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.Null;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresCommonPrivateKeyAttributesContextSpecific.class */
public final class CeresCommonPrivateKeyAttributesContextSpecific extends ContextSpecific {
    private static final byte TAG = -96;

    public CeresCommonPrivateKeyAttributesContextSpecific() {
        super(Null.class);
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (TAG != tag) {
            throw new Asn1Exception("CeresCommonPrivateKeyAttributesContextSpecific espera una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{TAG}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    public String toString() {
        return "Contexto especifico vacio";
    }
}
