package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AlgorithmNameFinder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/jcajce/JcePKCSPBEOutputEncryptorBuilder.class */
public class JcePKCSPBEOutputEncryptorBuilder {
    private final PBKDFConfig pbkdf;
    private JcaJceHelper helper;
    private ASN1ObjectIdentifier algorithm;
    private ASN1ObjectIdentifier keyEncAlgorithm;
    private SecureRandom random;
    private SecretKeySizeProvider keySizeProvider;
    private AlgorithmNameFinder algorithmNameFinder;
    private int iterationCount;
    private PBKDF2Config.Builder pbkdfBuilder;

    public JcePKCSPBEOutputEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.helper = new DefaultJcaJceHelper();
        this.keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
        this.algorithmNameFinder = new DefaultAlgorithmNameFinder();
        this.iterationCount = 1024;
        this.pbkdfBuilder = new PBKDF2Config.Builder();
        this.pbkdf = null;
        if (isPKCS12(aSN1ObjectIdentifier)) {
            this.algorithm = aSN1ObjectIdentifier;
            this.keyEncAlgorithm = aSN1ObjectIdentifier;
        } else {
            this.algorithm = PKCSObjectIdentifiers.id_PBES2;
            this.keyEncAlgorithm = aSN1ObjectIdentifier;
        }
    }

    public JcePKCSPBEOutputEncryptorBuilder(PBKDFConfig pBKDFConfig, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.helper = new DefaultJcaJceHelper();
        this.keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
        this.algorithmNameFinder = new DefaultAlgorithmNameFinder();
        this.iterationCount = 1024;
        this.pbkdfBuilder = new PBKDF2Config.Builder();
        this.algorithm = PKCSObjectIdentifiers.id_PBES2;
        this.pbkdf = pBKDFConfig;
        this.keyEncAlgorithm = aSN1ObjectIdentifier;
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(String str) {
        this.helper = new NamedJcaJceHelper(str);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(SecretKeySizeProvider secretKeySizeProvider) {
        this.keySizeProvider = secretKeySizeProvider;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setPRF(AlgorithmIdentifier algorithmIdentifier) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set PRF count using PBKDFDef");
        }
        this.pbkdfBuilder.withPRF(algorithmIdentifier);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setIterationCount(int i) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set iteration count using PBKDFDef");
        }
        this.iterationCount = i;
        this.pbkdfBuilder.withIterationCount(i);
        return this;
    }

    public OutputEncryptor build(final char[] cArr) throws OperatorCreationException {
        Cipher cipherCreateCipher;
        AlgorithmIdentifier algorithmIdentifier;
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            if (isPKCS12(this.algorithm)) {
                byte[] bArr = new byte[20];
                this.random.nextBytes(bArr);
                cipherCreateCipher = this.helper.createCipher(this.algorithm.getId());
                cipherCreateCipher.init(1, new PKCS12KeyWithParameters(cArr, bArr, this.iterationCount));
                algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, new PKCS12PBEParams(bArr, this.iterationCount));
            } else {
                if (!this.algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
                    throw new OperatorCreationException("unrecognised algorithm");
                }
                PBKDFConfig pBKDFConfigBuild = this.pbkdf == null ? this.pbkdfBuilder.build() : this.pbkdf;
                if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) pBKDFConfigBuild.getAlgorithm())) {
                    ScryptConfig scryptConfig = (ScryptConfig) pBKDFConfigBuild;
                    byte[] bArr2 = new byte[scryptConfig.getSaltLength()];
                    this.random.nextBytes(bArr2);
                    ScryptParams scryptParams = new ScryptParams(bArr2, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter());
                    SecretKey secretKeyGenerateSecret = this.helper.createSecretKeyFactory("SCRYPT").generateSecret(new ScryptKeySpec(cArr, bArr2, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipherCreateCipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipherCreateCipher.init(1, simplifyPbeKey(secretKeyGenerateSecret), this.random);
                    algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, cipherCreateCipher.getParameters() != null ? new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, scryptParams), new EncryptionScheme(this.keyEncAlgorithm, ASN1Primitive.fromByteArray(cipherCreateCipher.getParameters().getEncoded()))) : new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, scryptParams), new EncryptionScheme(this.keyEncAlgorithm)));
                } else {
                    PBKDF2Config pBKDF2Config = (PBKDF2Config) pBKDFConfigBuild;
                    byte[] bArr3 = new byte[pBKDF2Config.getSaltLength()];
                    this.random.nextBytes(bArr3);
                    SecretKey secretKeyGenerateSecret2 = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(pBKDF2Config.getPRF().getAlgorithm())).generateSecret(new PBEKeySpec(cArr, bArr3, pBKDF2Config.getIterationCount(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipherCreateCipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipherCreateCipher.init(1, simplifyPbeKey(secretKeyGenerateSecret2), this.random);
                    algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, cipherCreateCipher.getParameters() != null ? new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr3, pBKDF2Config.getIterationCount(), pBKDF2Config.getPRF())), new EncryptionScheme(this.keyEncAlgorithm, ASN1Primitive.fromByteArray(cipherCreateCipher.getParameters().getEncoded()))) : new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr3, pBKDF2Config.getIterationCount(), pBKDF2Config.getPRF())), new EncryptionScheme(this.keyEncAlgorithm)));
                }
            }
            final AlgorithmIdentifier algorithmIdentifier2 = algorithmIdentifier;
            final Cipher cipher = cipherCreateCipher;
            return new OutputEncryptor() { // from class: org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder.1
                @Override // org.bouncycastle.operator.OutputEncryptor
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return algorithmIdentifier2;
                }

                @Override // org.bouncycastle.operator.OutputEncryptor
                public OutputStream getOutputStream(OutputStream outputStream) {
                    return new CipherOutputStream(outputStream, cipher);
                }

                @Override // org.bouncycastle.operator.OutputEncryptor
                public GenericKey getKey() {
                    return JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(algorithmIdentifier2.getAlgorithm()) ? new GenericKey(algorithmIdentifier2, JcePKCSPBEOutputEncryptorBuilder.PKCS12PasswordToBytes(cArr)) : new GenericKey(algorithmIdentifier2, JcePKCSPBEOutputEncryptorBuilder.PKCS5PasswordToBytes(cArr));
                }
            };
        } catch (Exception e) {
            throw new OperatorCreationException("unable to create OutputEncryptor: " + e.getMessage(), e);
        }
    }

    private SecretKey simplifyPbeKey(SecretKey secretKey) {
        if (this.algorithmNameFinder.hasAlgorithmName(this.keyEncAlgorithm) && this.algorithmNameFinder.getAlgorithmName(this.keyEncAlgorithm).indexOf("AES") >= 0) {
            secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
        }
        return secretKey;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPKCS12(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.on(PKCSObjectIdentifiers.pkcs_12PbeIds) || aSN1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12) || aSN1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] PKCS5PasswordToBytes(char[] cArr) {
        if (cArr == null) {
            return new byte[0];
        }
        byte[] bArr = new byte[cArr.length];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) cArr[i];
        }
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] PKCS12PasswordToBytes(char[] cArr) {
        if (cArr == null || cArr.length <= 0) {
            return new byte[0];
        }
        byte[] bArr = new byte[(cArr.length + 1) * 2];
        for (int i = 0; i != cArr.length; i++) {
            bArr[i * 2] = (byte) (cArr[i] >>> '\b');
            bArr[(i * 2) + 1] = (byte) cArr[i];
        }
        return bArr;
    }
}
