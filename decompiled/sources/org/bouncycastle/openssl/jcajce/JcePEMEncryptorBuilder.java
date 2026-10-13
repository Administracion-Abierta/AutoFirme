package org.bouncycastle.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/jcajce/JcePEMEncryptorBuilder.class */
public class JcePEMEncryptorBuilder {
    private final String algorithm;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private SecureRandom random;

    public JcePEMEncryptorBuilder(String str) {
        this.algorithm = str;
    }

    public JcePEMEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePEMEncryptorBuilder setProvider(String str) {
        this.helper = new NamedJcaJceHelper(str);
        return this;
    }

    public JcePEMEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public PEMEncryptor build(final char[] cArr) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final byte[] bArr = new byte[this.algorithm.startsWith("AES-") ? 16 : 8];
        this.random.nextBytes(bArr);
        return new PEMEncryptor() { // from class: org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder.1
            @Override // org.bouncycastle.openssl.PEMEncryptor
            public String getAlgorithm() {
                return JcePEMEncryptorBuilder.this.algorithm;
            }

            @Override // org.bouncycastle.openssl.PEMEncryptor
            public byte[] getIV() {
                return bArr;
            }

            @Override // org.bouncycastle.openssl.PEMEncryptor
            public byte[] encrypt(byte[] bArr2) throws PEMException {
                return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, bArr2, cArr, JcePEMEncryptorBuilder.this.algorithm, bArr);
            }
        };
    }
}
