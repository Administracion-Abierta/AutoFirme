package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PrivateKeyObject.class */
public class PrivateKeyObject extends Pkcs15Object {
    protected PrivateKeyObject(Class<? extends DecoderObject> classAttributes, Class<? extends ContextSpecific> subclassAttributes, Class<? extends ContextSpecific> typeAttributes) {
        super(classAttributes, subclassAttributes, typeAttributes);
    }

    public PrivateKeyObject() {
        super(CommonKeyAttributes.class, CommonPrivateKeyAttributesContextSpecific.class, PrivateRsaKeyAttributesContextSpecific.class);
    }

    public byte[] getKeyIdentifier() {
        return ((CommonKeyAttributes) getClassAttributes()).getIdentifier();
    }

    String getKeyName() {
        return getCommonObjectAttributes().getLabel();
    }

    public String getKeyPath() {
        return ((PrivateRsaKeyAttributesContextSpecific) getTypeAttributes()).getPath();
    }

    int getKeyLength() {
        return ((PrivateRsaKeyAttributesContextSpecific) getTypeAttributes()).getKeyLength();
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
        Reference ref = ((CommonKeyAttributes) getClassAttributes()).getReference();
        byte[] referenceRawBytes = ref.getBytes();
        return referenceRawBytes[referenceRawBytes.length - 1];
    }
}
