package es.gob.jmulticard.asn1.custom.fnmt.ceressc;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.pkcs15.CommonKeyAttributes;
import es.gob.jmulticard.asn1.der.pkcs15.CommonPrivateKeyAttributesContextSpecific;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Object;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceressc/CeresScPrivateKeyObject.class */
public final class CeresScPrivateKeyObject extends Pkcs15Object {
    public CeresScPrivateKeyObject(Class<? extends DecoderObject> classAttributes, Class<? extends ContextSpecific> subclassAttributes, Class<? extends ContextSpecific> typeAttributes) {
        super(classAttributes, subclassAttributes, typeAttributes);
    }

    public CeresScPrivateKeyObject() {
        super(CommonKeyAttributes.class, CeresScCommonPrivateKeyAttributesContextSpecific.class, CeresScPrivateRsaKeyAttributesContextSpecific.class);
    }

    public byte[] getKeyIdentifier() {
        return ((CommonKeyAttributes) getClassAttributes()).getIdentifier();
    }

    String getKeyName() {
        return getCommonObjectAttributes().getLabel();
    }

    public String getKeyPath() {
        return ((CeresScPrivateRsaKeyAttributesContextSpecific) getTypeAttributes()).getPath();
    }

    int getKeyLength() {
        return ((CeresScPrivateRsaKeyAttributesContextSpecific) getTypeAttributes()).getKeyLength();
    }

    X500Principal getKeyPrincipal() {
        Object o = getSubclassAttributes();
        if (o instanceof CommonPrivateKeyAttributesContextSpecific) {
            return ((CommonPrivateKeyAttributesContextSpecific) o).getKeyPrincipal();
        }
        return null;
    }

    public String toString() {
        return "Nombre de la clave privada: " + getCommonObjectAttributes().getLabel() + (getSubclassAttributes() != null ? "\n" + ((CommonPrivateKeyAttributesContextSpecific) getSubclassAttributes()).getKeyPrincipal() : "");
    }

    public byte getKeyReference() {
        return ((CommonKeyAttributes) getClassAttributes()).getReference().getIntegerValue().byteValue();
    }
}
