package org.spongycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.bc.BCObjectIdentifiers;
import org.spongycastle.asn1.pkcs.EncryptionScheme;
import org.spongycastle.asn1.pkcs.KeyDerivationFunc;
import org.spongycastle.asn1.pkcs.PBES2Parameters;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.jcajce.PKCS12KeyWithParameters;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.operator.SecretKeySizeProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/jcajce/JcePKCSPBEOutputEncryptorBuilder.class */
public class JcePKCSPBEOutputEncryptorBuilder {
    private ASN1ObjectIdentifier algorithm;
    private ASN1ObjectIdentifier keyEncAlgorithm;
    private SecureRandom random;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
    private int iterationCount = 1024;
    private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);

    public JcePKCSPBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm) {
        if (isPKCS12(algorithm)) {
            this.algorithm = algorithm;
            this.keyEncAlgorithm = algorithm;
        } else {
            this.algorithm = PKCSObjectIdentifiers.id_PBES2;
            this.keyEncAlgorithm = algorithm;
        }
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setPRF(AlgorithmIdentifier prf) {
        this.prf = prf;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public OutputEncryptor build(final char[] password) throws OperatorCreationException {
        Cipher cipher;
        AlgorithmIdentifier encryptionAlg;
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            if (isPKCS12(this.algorithm)) {
                byte[] salt = new byte[20];
                this.random.nextBytes(salt);
                cipher = this.helper.createCipher(this.algorithm.getId());
                cipher.init(1, new PKCS12KeyWithParameters(password, salt, this.iterationCount));
                encryptionAlg = new AlgorithmIdentifier(this.algorithm, new PKCS12PBEParams(salt, this.iterationCount));
            } else if (this.algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                byte[] salt2 = new byte[JceUtils.getSaltSize(this.prf.getAlgorithm())];
                this.random.nextBytes(salt2);
                SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(this.prf.getAlgorithm()));
                SecretKey key = keyFact.generateSecret(new PBEKeySpec(password, salt2, this.iterationCount, this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                cipher.init(1, key, this.random);
                PBES2Parameters algParams = new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(salt2, this.iterationCount, this.prf)), new EncryptionScheme(this.keyEncAlgorithm, ASN1Primitive.fromByteArray(cipher.getParameters().getEncoded())));
                encryptionAlg = new AlgorithmIdentifier(this.algorithm, algParams);
            } else {
                throw new OperatorCreationException("unrecognised algorithm");
            }
            final AlgorithmIdentifier algorithmIdentifier = encryptionAlg;
            final Cipher cipher2 = cipher;
            return new OutputEncryptor() { // from class: org.spongycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder.1
                @Override // org.spongycastle.operator.OutputEncryptor
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return algorithmIdentifier;
                }

                @Override // org.spongycastle.operator.OutputEncryptor
                public OutputStream getOutputStream(OutputStream out) {
                    return new CipherOutputStream(out, cipher2);
                }

                @Override // org.spongycastle.operator.OutputEncryptor
                public GenericKey getKey() {
                    if (JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(algorithmIdentifier.getAlgorithm())) {
                        return new GenericKey(algorithmIdentifier, JcePKCSPBEOutputEncryptorBuilder.PKCS12PasswordToBytes(password));
                    }
                    return new GenericKey(algorithmIdentifier, JcePKCSPBEOutputEncryptorBuilder.PKCS5PasswordToBytes(password));
                }
            };
        } catch (Exception e) {
            throw new OperatorCreationException("unable to create OutputEncryptor: " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPKCS12(ASN1ObjectIdentifier algorithm) {
        return algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds) || algorithm.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12) || algorithm.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] PKCS5PasswordToBytes(char[] password) {
        if (password != null) {
            byte[] bytes = new byte[password.length];
            for (int i = 0; i != bytes.length; i++) {
                bytes[i] = (byte) password[i];
            }
            return bytes;
        }
        return new byte[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] PKCS12PasswordToBytes(char[] password) {
        if (password != null && password.length > 0) {
            byte[] bytes = new byte[(password.length + 1) * 2];
            for (int i = 0; i != password.length; i++) {
                bytes[i * 2] = (byte) (password[i] >>> '\b');
                bytes[(i * 2) + 1] = (byte) password[i];
            }
            return bytes;
        }
        return new byte[0];
    }
}
