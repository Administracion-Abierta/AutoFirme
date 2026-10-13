package es.gob.jmulticard.asn1.custom.fnmt.ceressc;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.pkcs15.CommonPrivateKeyAttributes;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceressc/CeresScCommonPrivateKeyAttributesContextSpecific.class */
public final class CeresScCommonPrivateKeyAttributesContextSpecific extends ContextSpecific {
    private static final byte TAG = -96;

    public CeresScCommonPrivateKeyAttributesContextSpecific() {
        super(EmptyCommonPrivateKeyAttributes.class);
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (TAG != tag) {
            throw new Asn1Exception("CommonPrivateKeyAttributesContextSpecific esperaba una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{TAG}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    public String toString() {
        return getObject().toString();
    }

    public X500Principal getKeyPrincipal() {
        return ((CommonPrivateKeyAttributes) getObject()).getKeyPrincipal();
    }
}
