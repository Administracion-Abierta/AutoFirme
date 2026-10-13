package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceCMSContentEncryptorBuilder.class */
public class JceCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private static final byte[] hkdfSalt = Strings.toByteArray("The Cryptographic Message Syntax");
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    private AlgorithmIdentifier algorithmIdentifier;
    private AlgorithmParameters algorithmParameters;
    private ASN1ObjectIdentifier kdfAlgorithm;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceCMSContentEncryptorBuilder$CMSAuthOutputEncryptor.class */
    private class CMSAuthOutputEncryptor extends CMSOutEncryptor implements OutputAEADEncryptor {
        private MacCaptureStream macOut;

        CMSAuthOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, int i, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            super();
            init(aSN1ObjectIdentifier, aSN1ObjectIdentifier2, i, algorithmParameters, secureRandom);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public OutputStream getOutputStream(OutputStream outputStream) {
            this.macOut = new MacCaptureStream(outputStream, GCMParameters.getInstance((JceCMSContentEncryptorBuilder.this.kdfAlgorithm != null ? AlgorithmIdentifier.getInstance(this.algorithmIdentifier.getParameters()) : this.algorithmIdentifier).getParameters()).getIcvLen());
            return new CipherOutputStream(this.macOut, this.cipher);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }

        @Override // org.bouncycastle.operator.AADProcessor
        public OutputStream getAADStream() {
            if (JceCMSContentEncryptorBuilder.checkForAEAD()) {
                return new JceAADStream(this.cipher);
            }
            return null;
        }

        @Override // org.bouncycastle.operator.AADProcessor
        public byte[] getMAC() {
            return this.macOut.getMac();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceCMSContentEncryptorBuilder$CMSOutEncryptor.class */
    private class CMSOutEncryptor {
        protected SecretKey encKey;
        protected AlgorithmIdentifier algorithmIdentifier;
        protected Cipher cipher;

        private CMSOutEncryptor() {
        }

        private void applyKdf(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
            byte[] encoded = this.encKey.getEncoded();
            try {
                hKDFBytesGenerator.init(new HKDFParameters(encoded, JceCMSContentEncryptorBuilder.hkdfSalt, this.algorithmIdentifier.getEncoded("DER")));
                hKDFBytesGenerator.generateBytes(encoded, 0, encoded.length);
                try {
                    this.cipher.init(1, new SecretKeySpec(encoded, this.encKey.getAlgorithm()), algorithmParameters, secureRandom);
                    this.algorithmIdentifier = new AlgorithmIdentifier(aSN1ObjectIdentifier, this.algorithmIdentifier);
                } catch (GeneralSecurityException e) {
                    throw new CMSException("unable to initialize cipher: " + e.getMessage(), e);
                }
            } catch (IOException e2) {
                throw new CMSException("unable to encode enc algorithm parameters", e2);
            }
        }

        protected void init(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, int i, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            KeyGenerator keyGeneratorCreateKeyGenerator = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(aSN1ObjectIdentifier2);
            SecureRandom secureRandom2 = CryptoServicesRegistrar.getSecureRandom(secureRandom);
            if (i < 0) {
                keyGeneratorCreateKeyGenerator.init(secureRandom2);
            } else {
                keyGeneratorCreateKeyGenerator.init(i, secureRandom2);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(aSN1ObjectIdentifier2);
            this.encKey = keyGeneratorCreateKeyGenerator.generateKey();
            if (algorithmParameters == null) {
                algorithmParameters = JceCMSContentEncryptorBuilder.this.helper.generateParameters(aSN1ObjectIdentifier2, this.encKey, secureRandom2);
            }
            if (algorithmParameters != null) {
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(aSN1ObjectIdentifier2, algorithmParameters);
                if (aSN1ObjectIdentifier != null) {
                    applyKdf(aSN1ObjectIdentifier, algorithmParameters, secureRandom2);
                    return;
                }
                try {
                    this.cipher.init(1, this.encKey, algorithmParameters, secureRandom2);
                    return;
                } catch (GeneralSecurityException e) {
                    throw new CMSException("unable to initialize cipher: " + e.getMessage(), e);
                }
            }
            try {
                this.cipher.init(1, this.encKey, algorithmParameters, secureRandom2);
                AlgorithmParameters parameters = this.cipher.getParameters();
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(aSN1ObjectIdentifier2, parameters);
                if (aSN1ObjectIdentifier != null) {
                    applyKdf(aSN1ObjectIdentifier, parameters, secureRandom2);
                }
            } catch (GeneralSecurityException e2) {
                throw new CMSException("unable to initialize cipher: " + e2.getMessage(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceCMSContentEncryptorBuilder$CMSOutputEncryptor.class */
    private class CMSOutputEncryptor extends CMSOutEncryptor implements OutputEncryptor {
        CMSOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, int i, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            super();
            init(aSN1ObjectIdentifier, aSN1ObjectIdentifier2, i, algorithmParameters, secureRandom);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public OutputStream getOutputStream(OutputStream outputStream) {
            return new CipherOutputStream(outputStream, this.cipher);
        }

        @Override // org.bouncycastle.operator.OutputEncryptor
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier));
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.encryptionOID = aSN1ObjectIdentifier;
        int keySize = KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier);
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.des_EDE3_CBC)) {
            if (i != 168 && i != keySize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 168;
            return;
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) OIWObjectIdentifiers.desCBC)) {
            if (i != 56 && i != keySize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 56;
            return;
        }
        if (keySize > 0 && keySize != i) {
            throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
        }
        this.keySize = i;
    }

    public JceCMSContentEncryptorBuilder(AlgorithmIdentifier algorithmIdentifier) {
        this(algorithmIdentifier.getAlgorithm(), KEY_SIZE_PROVIDER.getKeySize(algorithmIdentifier.getAlgorithm()));
        this.algorithmIdentifier = algorithmIdentifier;
    }

    public JceCMSContentEncryptorBuilder setEnableSha256HKdf(boolean z) {
        if (z) {
            this.kdfAlgorithm = CMSObjectIdentifiers.id_alg_cek_hkdf_sha256;
        } else if (this.kdfAlgorithm != null) {
            if (!this.kdfAlgorithm.equals((ASN1Primitive) CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
                throw new IllegalStateException("SHA256 HKDF not enabled");
            }
            this.kdfAlgorithm = null;
        }
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        ASN1Encodable parameters;
        if (this.algorithmParameters != null) {
            return this.helper.isAuthEnveloped(this.encryptionOID) ? new CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, this.keySize, this.algorithmParameters, this.random) : new CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
        }
        if (this.algorithmIdentifier != null && (parameters = this.algorithmIdentifier.getParameters()) != null && !parameters.equals(DERNull.INSTANCE)) {
            try {
                this.algorithmParameters = this.helper.createAlgorithmParameters(this.algorithmIdentifier.getAlgorithm());
                this.algorithmParameters.init(parameters.toASN1Primitive().getEncoded());
            } catch (Exception e) {
                throw new CMSException("unable to process provided algorithmIdentifier: " + e.toString(), e);
            }
        }
        return this.helper.isAuthEnveloped(this.encryptionOID) ? new CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, this.keySize, this.algorithmParameters, this.random) : new CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean checkForAEAD() {
        return ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    return Boolean.valueOf(Cipher.class.getMethod("updateAAD", byte[].class) != null);
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
            }
        })).booleanValue();
    }
}
