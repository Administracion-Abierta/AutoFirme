package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JceETSIDataEncryptor.class */
public class JceETSIDataEncryptor implements ETSIDataEncryptor {
    private final SecureRandom random;
    private final JcaJceHelper helper;
    private byte[] nonce;
    private byte[] key;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JceETSIDataEncryptor$Builder.class */
    public static class Builder {
        private SecureRandom random;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setRandom(SecureRandom secureRandom) {
            this.random = secureRandom;
            return this;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String str) {
            this.helper = new NamedJcaJceHelper(str);
            return this;
        }

        public JceETSIDataEncryptor build() {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            return new JceETSIDataEncryptor(this.random, this.helper);
        }
    }

    private JceETSIDataEncryptor(SecureRandom secureRandom, JcaJceHelper jcaJceHelper) {
        this.random = secureRandom;
        this.helper = jcaJceHelper;
    }

    @Override // org.bouncycastle.its.operator.ETSIDataEncryptor
    public byte[] encrypt(byte[] bArr) {
        this.key = new byte[16];
        this.random.nextBytes(this.key);
        this.nonce = new byte[12];
        this.random.nextBytes(this.nonce);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.key, "AES");
            Cipher cipherCreateCipher = this.helper.createCipher("CCM");
            cipherCreateCipher.init(1, secretKeySpec, ClassUtil.getGCMSpec(this.nonce, 128));
            return cipherCreateCipher.doFinal(bArr);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.its.operator.ETSIDataEncryptor
    public byte[] getKey() {
        return this.key;
    }

    @Override // org.bouncycastle.its.operator.ETSIDataEncryptor
    public byte[] getNonce() {
        return this.nonce;
    }
}
