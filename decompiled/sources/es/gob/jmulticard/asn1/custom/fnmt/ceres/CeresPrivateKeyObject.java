package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.der.pkcs15.CommonKeyAttributes;
import es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresPrivateKeyObject.class */
public final class CeresPrivateKeyObject extends PrivateKeyObject {
    public CeresPrivateKeyObject() {
        super(CommonKeyAttributes.class, CeresCommonPrivateKeyAttributesContextSpecific.class, CeresPrivateRsaKeyAttributesContextSpecific.class);
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject
    public byte[] getKeyIdentifier() {
        return ((CommonKeyAttributes) getClassAttributes()).getIdentifier();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject
    public byte getKeyReference() {
        return ((CommonKeyAttributes) getClassAttributes()).getReference().getIntegerValue().byteValue();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject
    public String getKeyPath() {
        return ((CeresPrivateRsaKeyAttributesContextSpecific) getTypeAttributes()).getPath();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject
    public String toString() {
        return "Clave privada con ruta '" + getKeyPath() + "', identificador '" + HexUtils.hexify(getKeyIdentifier(), true) + "' y referencia '0x" + HexUtils.hexify(new byte[]{getKeyReference()}, false);
    }
}
