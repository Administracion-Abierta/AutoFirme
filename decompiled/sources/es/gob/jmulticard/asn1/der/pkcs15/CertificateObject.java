package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CertificateObject.class */
public class CertificateObject extends Pkcs15Object {
    public CertificateObject() {
        super(CommonCertificateAttributes.class, null, X509CertificateAttributesContextSpecific.class);
    }

    protected CertificateObject(Class<? extends DecoderObject> classAttributes, Class<? extends ContextSpecific> subclassAttributes, Class<? extends ContextSpecific> typeAttributes) {
        super(classAttributes, subclassAttributes, typeAttributes);
    }

    String getIssuer() {
        return ((X509CertificateAttributesContextSpecific) getTypeAttributes()).getIssuer();
    }

    String getSubject() {
        return ((X509CertificateAttributesContextSpecific) getTypeAttributes()).getSubject();
    }

    public String getPath() {
        return ((X509CertificateAttributesContextSpecific) getTypeAttributes()).getPath();
    }

    public byte[] getPathBytes() {
        return ((X509CertificateAttributesContextSpecific) getTypeAttributes()).getPathBytes();
    }

    BigInteger getSerialNumber() {
        return ((X509CertificateAttributesContextSpecific) getTypeAttributes()).getSerialNumber();
    }

    public byte[] getIdentifier() {
        return ((CommonCertificateAttributes) getClassAttributes()).getId();
    }

    public String getAlias() {
        return getCommonObjectAttributes().getLabel();
    }

    public String toString() {
        return getTypeAttributes().toString() + "\nAlias del certificado: " + getCommonObjectAttributes().getLabel() + "\nIdentificador del certificado: " + getClassAttributes().toString();
    }
}
