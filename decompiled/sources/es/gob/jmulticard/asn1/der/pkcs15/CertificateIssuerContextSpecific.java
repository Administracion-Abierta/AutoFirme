package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.x509.RdnSequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CertificateIssuerContextSpecific.class */
public final class CertificateIssuerContextSpecific extends ContextSpecific {
    public CertificateIssuerContextSpecific() {
        super(RdnSequence.class);
    }

    public String toString() {
        return getObject().toString();
    }
}
