package org.spongycastle.openssl.jcajce;

import java.security.PrivateKey;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.openssl.PKCS8Generator;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.util.io.pem.PemGenerationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/jcajce/JcaPKCS8Generator.class */
public class JcaPKCS8Generator extends PKCS8Generator {
    public JcaPKCS8Generator(PrivateKey key, OutputEncryptor encryptor) throws PemGenerationException {
        super(PrivateKeyInfo.getInstance(key.getEncoded()), encryptor);
    }
}
