package org.spongycastle.cert.selector.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/selector/jcajce/JcaX509CertificateHolderSelector.class */
public class JcaX509CertificateHolderSelector extends X509CertificateHolderSelector {
    public JcaX509CertificateHolderSelector(X509Certificate certificate) {
        super(convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), getSubjectKeyId(certificate));
    }

    public JcaX509CertificateHolderSelector(X500Principal issuer, BigInteger serialNumber) {
        super(convertPrincipal(issuer), serialNumber);
    }

    public JcaX509CertificateHolderSelector(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance(issuer.getEncoded());
    }

    private static byte[] getSubjectKeyId(X509Certificate cert) {
        byte[] ext = cert.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (ext != null) {
            return ASN1OctetString.getInstance(ASN1OctetString.getInstance(ext).getOctets()).getOctets();
        }
        return null;
    }
}
