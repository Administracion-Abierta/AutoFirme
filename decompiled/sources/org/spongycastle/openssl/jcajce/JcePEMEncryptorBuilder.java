package org.spongycastle.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.openssl.PEMEncryptor;
import org.spongycastle.openssl.PEMException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/jcajce/JcePEMEncryptorBuilder.class */
public class JcePEMEncryptorBuilder {
    private final String algorithm;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private SecureRandom random;

    public JcePEMEncryptorBuilder(String algorithm) {
        this.algorithm = algorithm;
    }

    public JcePEMEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePEMEncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePEMEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public PEMEncryptor build(final char[] password) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        int ivLength = this.algorithm.startsWith("AES-") ? 16 : 8;
        final byte[] iv = new byte[ivLength];
        this.random.nextBytes(iv);
        return new PEMEncryptor() { // from class: org.spongycastle.openssl.jcajce.JcePEMEncryptorBuilder.1
            @Override // org.spongycastle.openssl.PEMEncryptor
            public String getAlgorithm() {
                return JcePEMEncryptorBuilder.this.algorithm;
            }

            @Override // org.spongycastle.openssl.PEMEncryptor
            public byte[] getIV() {
                return iv;
            }

            @Override // org.spongycastle.openssl.PEMEncryptor
            public byte[] encrypt(byte[] encoding) throws PEMException {
                return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, encoding, password, JcePEMEncryptorBuilder.this.algorithm, iv);
            }
        };
    }
}
