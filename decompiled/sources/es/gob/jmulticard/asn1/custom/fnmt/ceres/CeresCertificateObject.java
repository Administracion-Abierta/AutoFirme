package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.asn1.der.pkcs15.CertificateObject;
import es.gob.jmulticard.asn1.der.pkcs15.CommonCertificateAttributes;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresCertificateObject.class */
public final class CeresCertificateObject extends CertificateObject {
    public CeresCertificateObject() {
        super(CommonCertificateAttributes.class, null, CeresX509CertificateAttributesContextSpecific.class);
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.CertificateObject
    public String getPath() {
        return ((CeresX509CertificateAttributesContextSpecific) getTypeAttributes()).getPath();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.CertificateObject
    public byte[] getIdentifier() {
        return ((CommonCertificateAttributes) getClassAttributes()).getId();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.CertificateObject
    public String toString() {
        return getTypeAttributes().toString() + "\nRuta del certificado: " + getPath();
    }
}
