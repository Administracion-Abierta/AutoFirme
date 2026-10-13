package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.pkcs15.Path;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresPrivateRsaKeyAttributesContextSpecific.class */
public final class CeresPrivateRsaKeyAttributesContextSpecific extends ContextSpecific {
    private static final byte TAG = -95;

    public CeresPrivateRsaKeyAttributesContextSpecific() {
        super(Path.class);
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (TAG != tag) {
            throw new Asn1Exception("CeresPrivateRsaKeyAttributesContextSpecific espera una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{TAG}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    public String toString() {
        return getObject().toString();
    }

    String getPath() {
        return new String(((Path) getObject()).getPathBytes());
    }
}
