package org.spongycastle.pkix.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.pkix.PKIXIdentity;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkix/jcajce/JcaPKIXIdentity.class */
public class JcaPKIXIdentity extends PKIXIdentity {
    private final PrivateKey privKey;
    private final X509Certificate[] certs;

    private static PrivateKeyInfo getPrivateKeyInfo(PrivateKey privateKey) {
        try {
            return PrivateKeyInfo.getInstance(privateKey.getEncoded());
        } catch (Exception e) {
            return null;
        }
    }

    private static X509CertificateHolder[] getCertificates(X509Certificate[] certs) {
        X509CertificateHolder[] certHldrs = new X509CertificateHolder[certs.length];
        for (int i = 0; i != certHldrs.length; i++) {
            try {
                certHldrs[i] = new JcaX509CertificateHolder(certs[i]);
            } catch (CertificateEncodingException e) {
                throw new IllegalArgumentException("Unable to process certificates: " + e.getMessage());
            }
        }
        return certHldrs;
    }

    public JcaPKIXIdentity(PrivateKey privKey, X509Certificate[] certs) {
        super(getPrivateKeyInfo(privKey), getCertificates(certs));
        this.privKey = privKey;
        this.certs = new X509Certificate[certs.length];
        System.arraycopy(certs, 0, this.certs, 0, certs.length);
    }

    public PrivateKey getPrivateKey() {
        return this.privKey;
    }

    public X509Certificate getX509Certificate() {
        return this.certs[0];
    }
}
