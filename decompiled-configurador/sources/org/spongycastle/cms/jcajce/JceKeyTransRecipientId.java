package org.spongycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cms.KeyTransRecipientId;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKeyTransRecipientId.class */
public class JceKeyTransRecipientId extends KeyTransRecipientId {
    public JceKeyTransRecipientId(X509Certificate certificate) {
        super(convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(certificate));
    }

    public JceKeyTransRecipientId(X500Principal issuer, BigInteger serialNumber) {
        super(convertPrincipal(issuer), serialNumber);
    }

    public JceKeyTransRecipientId(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance(issuer.getEncoded());
    }
}
