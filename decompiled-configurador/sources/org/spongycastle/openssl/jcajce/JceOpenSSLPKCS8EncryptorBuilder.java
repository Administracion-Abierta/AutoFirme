package org.spongycastle.openssl.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
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
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/jcajce/JceOpenSSLPKCS8EncryptorBuilder.class */
public class JceOpenSSLPKCS8EncryptorBuilder {
    public static final String AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC.getId();
    public static final String AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC.getId();
    public static final String AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC.getId();
    public static final String DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC.getId();
    public static final String PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId();
    public static final String PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId();
    public static final String PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId();
    public static final String PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId();
    public static final String PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId();
    public static final String PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC.getId();
    private AlgorithmParameters params;
    private ASN1ObjectIdentifier algOID;
    byte[] salt;
    private Cipher cipher;
    private SecureRandom random;
    private AlgorithmParameterGenerator paramGen;
    private char[] password;
    private SecretKey key;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
    int iterationCount = 2048;

    public JceOpenSSLPKCS8EncryptorBuilder(ASN1ObjectIdentifier algorithm) {
        this.algOID = algorithm;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setPasssword(char[] password) {
        this.password = password;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setPRF(AlgorithmIdentifier prf) {
        this.prf = prf;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public OutputEncryptor build() throws OperatorCreationException {
        AlgorithmIdentifier algID;
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            this.cipher = this.helper.createCipher(this.algOID.getId());
            if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
                this.paramGen = this.helper.createAlgorithmParameterGenerator(this.algOID.getId());
            }
            if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
                this.salt = new byte[PEMUtilities.getSaltSize(this.prf.getAlgorithm())];
                this.random.nextBytes(this.salt);
                this.params = this.paramGen.generateParameters();
                try {
                    EncryptionScheme scheme = new EncryptionScheme(this.algOID, ASN1Primitive.fromByteArray(this.params.getEncoded()));
                    KeyDerivationFunc func = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(this.salt, this.iterationCount, this.prf));
                    ASN1EncodableVector v = new ASN1EncodableVector();
                    v.add(func);
                    v.add(scheme);
                    algID = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, PBES2Parameters.getInstance(new DERSequence(v)));
                    try {
                        if (PEMUtilities.isHmacSHA1(this.prf)) {
                            this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount);
                        } else {
                            this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount, this.prf);
                        }
                        this.cipher.init(1, this.key, this.params);
                    } catch (GeneralSecurityException e) {
                        throw new OperatorCreationException(e.getMessage(), e);
                    }
                } catch (IOException e2) {
                    throw new OperatorCreationException(e2.getMessage(), e2);
                }
            } else if (PEMUtilities.isPKCS12(this.algOID)) {
                ASN1EncodableVector v2 = new ASN1EncodableVector();
                this.salt = new byte[20];
                this.random.nextBytes(this.salt);
                v2.add(new DEROctetString(this.salt));
                v2.add(new ASN1Integer(this.iterationCount));
                algID = new AlgorithmIdentifier(this.algOID, PKCS12PBEParams.getInstance(new DERSequence(v2)));
                try {
                    this.cipher.init(1, new PKCS12KeyWithParameters(this.password, this.salt, this.iterationCount));
                } catch (GeneralSecurityException e3) {
                    throw new OperatorCreationException(e3.getMessage(), e3);
                }
            } else {
                throw new OperatorCreationException("unknown algorithm: " + this.algOID, null);
            }
            final AlgorithmIdentifier algorithmIdentifier = algID;
            return new OutputEncryptor() { // from class: org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder.1
                @Override // org.spongycastle.operator.OutputEncryptor
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return algorithmIdentifier;
                }

                @Override // org.spongycastle.operator.OutputEncryptor
                public OutputStream getOutputStream(OutputStream encOut) {
                    return new CipherOutputStream(encOut, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
                }

                @Override // org.spongycastle.operator.OutputEncryptor
                public GenericKey getKey() {
                    return new JceGenericKey(algorithmIdentifier, JceOpenSSLPKCS8EncryptorBuilder.this.key);
                }
            };
        } catch (GeneralSecurityException e4) {
            throw new OperatorCreationException(this.algOID + " not available: " + e4.getMessage(), e4);
        }
    }
}
