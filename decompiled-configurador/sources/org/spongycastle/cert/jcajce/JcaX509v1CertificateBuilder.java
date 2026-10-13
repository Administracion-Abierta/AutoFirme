package org.spongycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509v1CertificateBuilder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509v1CertificateBuilder.class */
public class JcaX509v1CertificateBuilder extends X509v1CertificateBuilder {
    public JcaX509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, PublicKey publicKey) {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public JcaX509v1CertificateBuilder(X500Principal issuer, BigInteger serial, Date notBefore, Date notAfter, X500Principal subject, PublicKey publicKey) {
        super(X500Name.getInstance(issuer.getEncoded()), serial, notBefore, notAfter, X500Name.getInstance(subject.getEncoded()), SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }
}
