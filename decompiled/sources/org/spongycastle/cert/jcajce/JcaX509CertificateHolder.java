package org.spongycastle.cert.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaX509CertificateHolder.class */
public class JcaX509CertificateHolder extends X509CertificateHolder {
    public JcaX509CertificateHolder(X509Certificate cert) throws CertificateEncodingException {
        super(Certificate.getInstance(cert.getEncoded()));
    }
}
