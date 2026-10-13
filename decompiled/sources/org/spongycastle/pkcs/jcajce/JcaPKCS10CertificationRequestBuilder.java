package org.spongycastle.pkcs.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.pkcs.PKCS10CertificationRequestBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkcs/jcajce/JcaPKCS10CertificationRequestBuilder.class */
public class JcaPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder {
    public JcaPKCS10CertificationRequestBuilder(X500Name subject, PublicKey publicKey) {
        super(subject, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public JcaPKCS10CertificationRequestBuilder(X500Principal subject, PublicKey publicKey) {
        super(X500Name.getInstance(subject.getEncoded()), SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }
}
