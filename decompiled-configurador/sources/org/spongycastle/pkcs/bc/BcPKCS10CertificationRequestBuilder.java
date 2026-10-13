package org.spongycastle.pkcs.bc;

import java.io.IOException;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.spongycastle.pkcs.PKCS10CertificationRequestBuilder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/bc/BcPKCS10CertificationRequestBuilder.class */
public class BcPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder {
    public BcPKCS10CertificationRequestBuilder(X500Name subject, AsymmetricKeyParameter publicKey) throws IOException {
        super(subject, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey));
    }
}
