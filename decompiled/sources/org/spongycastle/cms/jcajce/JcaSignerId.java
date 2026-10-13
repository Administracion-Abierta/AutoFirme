package org.spongycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cms.SignerId;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSignerId.class */
public class JcaSignerId extends SignerId {
    public JcaSignerId(X509Certificate certificate) {
        super(convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(certificate));
    }

    public JcaSignerId(X500Principal issuer, BigInteger serialNumber) {
        super(convertPrincipal(issuer), serialNumber);
    }

    public JcaSignerId(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance(issuer.getEncoded());
    }
}
