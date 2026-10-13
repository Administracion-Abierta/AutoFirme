package org.spongycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.operator.SecretKeySizeProvider;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceCMSContentEncryptorBuilder.class */
public class JceCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    private AlgorithmParameters algorithmParameters;

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, KEY_SIZE_PROVIDER.getKeySize(encryptionOID));
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.encryptionOID = encryptionOID;
        int fixedSize = KEY_SIZE_PROVIDER.getKeySize(encryptionOID);
        if (encryptionOID.equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
            if (keySize != 168 && keySize != fixedSize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256;
            return;
        }
        if (encryptionOID.equals(OIWObjectIdentifiers.desCBC)) {
            if (keySize != 56 && keySize != fixedSize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 56;
            return;
        }
        if (fixedSize > 0 && fixedSize != keySize) {
            throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
        }
        this.keySize = keySize;
    }

    public JceCMSContentEncryptorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceCMSContentEncryptorBuilder$CMSOutputEncryptor.class */
    private class CMSOutputEncryptor implements OutputEncryptor {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;

        CMSOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, AlgorithmParameters params, SecureRandom random) throws CMSException {
            KeyGenerator keyGen = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID);
            random = random == null ? new SecureRandom() : random;
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(encryptionOID);
            this.encKey = keyGen.generateKey();
            params = params == null ? JceCMSContentEncryptorBuilder.this.helper.generateParameters(encryptionOID, this.encKey, random) : params;
            try {
                this.cipher.init(1, this.encKey, params, random);
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(encryptionOID, params == null ? this.cipher.getParameters() : params);
            } catch (GeneralSecurityException e) {
                throw new CMSException("unable to initialize cipher: " + e.getMessage(), e);
            }
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public OutputStream getOutputStream(OutputStream dOut) {
            return new CipherOutputStream(dOut, this.cipher);
        }

        @Override // org.spongycastle.operator.OutputEncryptor
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}
