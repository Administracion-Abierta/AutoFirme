package org.bouncycastle.pkcs.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/jcajce/JcaPKCS10CertificationRequestBuilder.class */
public class JcaPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder {
    public JcaPKCS10CertificationRequestBuilder(X500Name x500Name, PublicKey publicKey) {
        super(x500Name, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public JcaPKCS10CertificationRequestBuilder(X500Principal x500Principal, PublicKey publicKey) {
        super(X500Name.getInstance(x500Principal.getEncoded()), SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public PKCS10CertificationRequest build(ContentSigner contentSigner, PublicKey publicKey, ContentSigner contentSigner2) {
        return super.build(contentSigner, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()), contentSigner2);
    }
}
