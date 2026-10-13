package org.spongycastle.cert.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/bc/BcX509v3CertificateBuilder.class */
public class BcX509v3CertificateBuilder extends X509v3CertificateBuilder {
    public BcX509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, AsymmetricKeyParameter publicKey) throws IOException {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }

    public BcX509v3CertificateBuilder(X509CertificateHolder issuerCert, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, AsymmetricKeyParameter publicKey) throws IOException {
        super(issuerCert.getSubject(), serial, notBefore, notAfter, subject, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }
}
