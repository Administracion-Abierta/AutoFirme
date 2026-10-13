package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/jcajce/JcaSelectorConverter.class */
public class JcaSelectorConverter {
    public X509CertificateHolderSelector getCertificateHolderSelector(X509CertSelector x509CertSelector) {
        try {
            X500Name x500Name = X500Name.getInstance(x509CertSelector.getIssuerAsBytes());
            BigInteger serialNumber = x509CertSelector.getSerialNumber();
            byte[] octets = null;
            byte[] subjectKeyIdentifier = x509CertSelector.getSubjectKeyIdentifier();
            if (subjectKeyIdentifier != null) {
                octets = ASN1OctetString.getInstance(subjectKeyIdentifier).getOctets();
            }
            return new X509CertificateHolderSelector(x500Name, serialNumber, octets);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to convert issuer: " + e.getMessage());
        }
    }
}
