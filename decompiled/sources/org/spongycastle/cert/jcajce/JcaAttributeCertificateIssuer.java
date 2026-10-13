package org.spongycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.AttributeCertificateIssuer;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaAttributeCertificateIssuer.class */
public class JcaAttributeCertificateIssuer extends AttributeCertificateIssuer {
    public JcaAttributeCertificateIssuer(X509Certificate issuerCert) {
        this(issuerCert.getIssuerX500Principal());
    }

    public JcaAttributeCertificateIssuer(X500Principal issuerDN) {
        super(X500Name.getInstance(issuerDN.getEncoded()));
    }
}
