package org.spongycastle.cert.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.X509v1CertificateBuilder;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/bc/BcX509v1CertificateBuilder.class */
public class BcX509v1CertificateBuilder extends X509v1CertificateBuilder {
    public BcX509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, AsymmetricKeyParameter publicKey) throws IOException {
        super(issuer, serial, notBefore, notAfter, subject, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }
}
