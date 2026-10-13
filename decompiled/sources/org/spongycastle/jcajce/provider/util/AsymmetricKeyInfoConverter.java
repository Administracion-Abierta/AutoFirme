package org.spongycastle.jcajce.provider.util;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/util/AsymmetricKeyInfoConverter.class */
public interface AsymmetricKeyInfoConverter {
    PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException;

    PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException;
}
