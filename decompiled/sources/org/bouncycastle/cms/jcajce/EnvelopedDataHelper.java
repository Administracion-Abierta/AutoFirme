package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/EnvelopedDataHelper.class */
public class EnvelopedDataHelper {
    protected static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private static final byte[] hkdfSalt = Strings.toByteArray("The Cryptographic Message Syntax");
    private static final Set authEnvelopedAlgorithms = new HashSet();
    protected static final Map BASE_CIPHER_NAMES = new HashMap();
    protected static final Map CIPHER_ALG_NAMES = new HashMap();
    protected static final Map MAC_ALG_NAMES = new HashMap();
    private static final Map PBKDF2_ALG_NAMES = new HashMap();
    private static final short[] rc2Table;
    private static final short[] rc2Ekb;
    private JcaJceExtHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/EnvelopedDataHelper$JCECallback.class */
    interface JCECallback {
        Object doInJCE() throws NoSuchPaddingException, CMSException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException;
    }

    EnvelopedDataHelper(JcaJceExtHelper jcaJceExtHelper) {
        this.helper = jcaJceExtHelper;
    }

    String getBaseCipherName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
        return str == null ? aSN1ObjectIdentifier.getId() : str;
    }

    Key getJceKey(GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key) genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) genericKey.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    public Key getJceKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key) genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) genericKey.getRepresentation(), getBaseCipherName(aSN1ObjectIdentifier));
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    public Key getJceKey(AlgorithmIdentifier algorithmIdentifier, GenericKey genericKey) throws CMSException {
        if (!algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
            return getJceKey(algorithmIdentifier.getAlgorithm(), genericKey);
        }
        byte[] encoded = null;
        if (genericKey.getRepresentation() instanceof Key) {
            encoded = ((Key) genericKey.getRepresentation()).getEncoded();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            encoded = (byte[]) genericKey.getRepresentation();
        }
        AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters());
        HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        try {
            hKDFBytesGenerator.init(new HKDFParameters(encoded, hkdfSalt, algorithmIdentifier2.getEncoded("DER")));
            hKDFBytesGenerator.generateBytes(encoded, 0, encoded.length);
            return new SecretKeySpec(encoded, getBaseCipherName(algorithmIdentifier2.getAlgorithm()));
        } catch (IOException e) {
            throw new CMSException("unable to encode enc algorithm parameters", e);
        }
    }

    public void keySizeCheck(AlgorithmIdentifier algorithmIdentifier, Key key) throws CMSException {
        int keySize = KEY_SIZE_PROVIDER.getKeySize(algorithmIdentifier);
        if (keySize > 0) {
            byte[] encoded = null;
            try {
                encoded = key.getEncoded();
            } catch (Exception e) {
            }
            if (encoded != null && encoded.length * 8 != keySize) {
                throw new CMSException("Expected key size for algorithm OID not found in recipient.");
            }
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) CIPHER_ALG_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    Mac createMac(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) MAC_ALG_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createMac(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createMac(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create mac: " + e2.getMessage(), e2);
        }
    }

    Cipher createRFC3211Wrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
        if (str == null) {
            throw new CMSException("no name for " + aSN1ObjectIdentifier);
        }
        try {
            return this.helper.createCipher(str + "RFC3211Wrap");
        } catch (GeneralSecurityException e) {
            throw new CMSException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createKeyAgreement(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyAgreement(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key agreement: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameterGenerator createAlgorithmParameterGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws GeneralSecurityException {
        String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
        if (str != null) {
            try {
                return this.helper.createAlgorithmParameterGenerator(str);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return this.helper.createAlgorithmParameterGenerator(aSN1ObjectIdentifier.getId());
    }

    public Cipher createContentCipher(final Key key, final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        return (Cipher) execute(new JCECallback() { // from class: org.bouncycastle.cms.jcajce.EnvelopedDataHelper.1
            @Override // org.bouncycastle.cms.jcajce.EnvelopedDataHelper.JCECallback
            public Object doInJCE() throws NoSuchPaddingException, CMSException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
                AlgorithmIdentifier algorithmIdentifier2 = algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) CMSObjectIdentifiers.id_alg_cek_hkdf_sha256) ? AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters()) : algorithmIdentifier;
                Cipher cipherCreateCipher = EnvelopedDataHelper.this.createCipher(algorithmIdentifier2.getAlgorithm());
                ASN1Encodable parameters = algorithmIdentifier2.getParameters();
                String id = algorithmIdentifier2.getAlgorithm().getId();
                if (parameters != null && !(parameters instanceof ASN1Null)) {
                    try {
                        AlgorithmParameters algorithmParametersCreateAlgorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(algorithmIdentifier2.getAlgorithm());
                        CMSUtils.loadParameters(algorithmParametersCreateAlgorithmParameters, parameters);
                        cipherCreateCipher.init(2, key, algorithmParametersCreateAlgorithmParameters);
                    } catch (NoSuchAlgorithmException e) {
                        if (!id.equals(CMSAlgorithm.DES_CBC.getId()) && !id.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) && !id.equals(CMSEnvelopedDataGenerator.IDEA_CBC) && !id.equals(CMSEnvelopedDataGenerator.AES128_CBC) && !id.equals(CMSEnvelopedDataGenerator.AES192_CBC) && !id.equals(CMSEnvelopedDataGenerator.AES256_CBC)) {
                            throw e;
                        }
                        cipherCreateCipher.init(2, key, new IvParameterSpec(ASN1OctetString.getInstance(parameters).getOctets()));
                    }
                } else if (id.equals(CMSAlgorithm.DES_CBC.getId()) || id.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || id.equals(CMSEnvelopedDataGenerator.IDEA_CBC) || id.equals(CMSEnvelopedDataGenerator.CAST5_CBC)) {
                    cipherCreateCipher.init(2, key, new IvParameterSpec(new byte[8]));
                } else {
                    cipherCreateCipher.init(2, key);
                }
                return cipherCreateCipher;
            }
        });
    }

    Mac createContentMac(final Key key, final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        return (Mac) execute(new JCECallback() { // from class: org.bouncycastle.cms.jcajce.EnvelopedDataHelper.2
            @Override // org.bouncycastle.cms.jcajce.EnvelopedDataHelper.JCECallback
            public Object doInJCE() throws NoSuchPaddingException, CMSException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
                Mac macCreateMac = EnvelopedDataHelper.this.createMac(algorithmIdentifier.getAlgorithm());
                ASN1Encodable parameters = algorithmIdentifier.getParameters();
                algorithmIdentifier.getAlgorithm().getId();
                if (parameters == null || (parameters instanceof ASN1Null)) {
                    macCreateMac.init(key);
                } else {
                    try {
                        AlgorithmParameters algorithmParametersCreateAlgorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
                        CMSUtils.loadParameters(algorithmParametersCreateAlgorithmParameters, parameters);
                        macCreateMac.init(key, algorithmParametersCreateAlgorithmParameters.getParameterSpec(AlgorithmParameterSpec.class));
                    } catch (NoSuchAlgorithmException e) {
                        throw e;
                    }
                }
                return macCreateMac;
            }
        });
    }

    AlgorithmParameters createAlgorithmParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
        if (str != null) {
            try {
                return this.helper.createAlgorithmParameters(str);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return this.helper.createAlgorithmParameters(aSN1ObjectIdentifier.getId());
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createKeyPairGenerator(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyPairGenerator(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key pair generator: " + e2.getMessage(), e2);
        }
    }

    public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createKeyGenerator(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyGenerator(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key generator: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameters generateParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier, SecretKey secretKey, SecureRandom secureRandom) throws CMSException {
        try {
            AlgorithmParameterGenerator algorithmParameterGeneratorCreateAlgorithmParameterGenerator = createAlgorithmParameterGenerator(aSN1ObjectIdentifier);
            if (aSN1ObjectIdentifier.equals((ASN1Primitive) CMSAlgorithm.RC2_CBC)) {
                byte[] bArr = new byte[8];
                secureRandom.nextBytes(bArr);
                try {
                    algorithmParameterGeneratorCreateAlgorithmParameterGenerator.init(new RC2ParameterSpec(secretKey.getEncoded().length * 8, bArr), secureRandom);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new CMSException("parameters generation error: " + e, e);
                }
            }
            return algorithmParameterGeneratorCreateAlgorithmParameterGenerator.generateParameters();
        } catch (NoSuchAlgorithmException e2) {
            return null;
        } catch (GeneralSecurityException e3) {
            throw new CMSException("exception creating algorithm parameter generator: " + e3, e3);
        }
    }

    AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameters algorithmParameters) throws CMSException {
        return new AlgorithmIdentifier(aSN1ObjectIdentifier, algorithmParameters != null ? CMSUtils.extractParameters(algorithmParameters) : DERNull.INSTANCE);
    }

    static Object execute(JCECallback jCECallback) throws CMSException {
        try {
            return jCECallback.doInJCE();
        } catch (InvalidAlgorithmParameterException e) {
            throw new CMSException("algorithm parameters invalid.", e);
        } catch (InvalidKeyException e2) {
            throw new CMSException("key invalid in message.", e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new CMSException("can't find algorithm.", e3);
        } catch (NoSuchProviderException e4) {
            throw new CMSException("can't find provider.", e4);
        } catch (InvalidParameterSpecException e5) {
            throw new CMSException("MAC algorithm parameter spec invalid.", e5);
        } catch (NoSuchPaddingException e6) {
            throw new CMSException("required padding not supported.", e6);
        }
    }

    public KeyFactory createKeyFactory(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String str = (String) BASE_CIPHER_NAMES.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createKeyFactory(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyFactory(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key factory: " + e2.getMessage(), e2);
        }
    }

    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey) {
        return this.helper.createAsymmetricUnwrapper(algorithmIdentifier, CMSUtils.cleanPrivateKey(privateKey));
    }

    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey, byte[] bArr, byte[] bArr2) {
        return this.helper.createAsymmetricUnwrapper(algorithmIdentifier, CMSUtils.cleanPrivateKey(privateKey), bArr, bArr2);
    }

    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, SecretKey secretKey) {
        return this.helper.createSymmetricUnwrapper(algorithmIdentifier, secretKey);
    }

    public AsymmetricKeyUnwrapper createKEMUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey) {
        return this.helper.createKEMUnwrapper(algorithmIdentifier, CMSUtils.cleanPrivateKey(privateKey));
    }

    public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof IvParameterSpec) {
            return new AlgorithmIdentifier(aSN1ObjectIdentifier, new DEROctetString(((IvParameterSpec) algorithmParameterSpec).getIV()));
        }
        if (!(algorithmParameterSpec instanceof RC2ParameterSpec)) {
            throw new IllegalStateException("unknown parameter spec: " + algorithmParameterSpec);
        }
        RC2ParameterSpec rC2ParameterSpec = (RC2ParameterSpec) algorithmParameterSpec;
        int effectiveKeyBits = ((RC2ParameterSpec) algorithmParameterSpec).getEffectiveKeyBits();
        if (effectiveKeyBits != -1) {
            return new AlgorithmIdentifier(aSN1ObjectIdentifier, new RC2CBCParameter(effectiveKeyBits < 256 ? rc2Table[effectiveKeyBits] : effectiveKeyBits, rC2ParameterSpec.getIV()));
        }
        return new AlgorithmIdentifier(aSN1ObjectIdentifier, new RC2CBCParameter(rC2ParameterSpec.getIV()));
    }

    SecretKeyFactory createSecretKeyFactory(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createSecretKeyFactory(str);
    }

    byte[] calculateDerivedKey(int i, char[] cArr, AlgorithmIdentifier algorithmIdentifier, int i2) throws CMSException {
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(algorithmIdentifier.getParameters());
        try {
            return (i == 0 ? this.helper.createSecretKeyFactory("PBKDF2with8BIT") : this.helper.createSecretKeyFactory((String) PBKDF2_ALG_NAMES.get(pBKDF2Params.getPrf()))).generateSecret(new PBEKeySpec(cArr, pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue(), i2)).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new CMSException("Unable to calculate derived key from password: " + e.getMessage(), e);
        }
    }

    boolean isAuthEnveloped(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return authEnvelopedAlgorithms.contains(aSN1ObjectIdentifier);
    }

    static {
        BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_CBC, "DES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED");
        BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
        BASE_CIPHER_NAMES.put(CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_CBC, "DES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
        MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
        PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA1.getAlgorithmID(), "PBKDF2WITHHMACSHA1");
        PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA224.getAlgorithmID(), "PBKDF2WITHHMACSHA224");
        PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA256.getAlgorithmID(), "PBKDF2WITHHMACSHA256");
        PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA384.getAlgorithmID(), "PBKDF2WITHHMACSHA384");
        PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA512.getAlgorithmID(), "PBKDF2WITHHMACSHA512");
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_CCM);
        rc2Table = new short[]{189, 86, 234, 242, 162, 241, 172, 42, 176, 147, 209, 156, 27, 51, 253, 208, 48, 4, 182, 220, 125, 223, 50, 75, 247, 203, 69, 155, 49, 187, 33, 90, 65, 159, 225, 217, 74, 77, 158, 218, 160, 104, 44, 195, 39, 95, 128, 54, 62, 238, 251, 149, 26, 254, 206, 168, 52, 169, 19, 240, 166, 63, 216, 12, 120, 36, 175, 35, 82, 193, 103, 23, 245, 102, 144, 231, 232, 7, 184, 96, 72, 230, 30, 83, 243, 146, 164, 114, 140, 8, 21, 110, 134, 0, 132, 250, 244, 127, 138, 66, 25, 246, 219, 205, 20, 141, 80, 18, 186, 60, 6, 78, 236, 179, 53, 17, 161, 136, 142, 43, 148, 153, 183, 113, 116, 211, 228, 191, 58, 222, 150, 14, 188, 10, 237, 119, 252, 55, 107, 3, 121, 137, 98, 198, 215, 192, 210, 124, 106, 139, 34, 163, 91, 5, 93, 2, 117, 213, 97, 227, 24, 143, 85, 81, 173, 31, 11, 94, 133, 229, 194, 87, 99, 202, 61, 108, 180, 197, 204, 112, 178, 145, 89, 13, 71, 32, 200, 79, 88, 224, 1, 226, 22, 56, 196, 111, 59, 15, 101, 70, 190, 126, 45, 123, 130, 249, 64, 181, 29, 115, 248, 235, 38, 199, 135, 151, 37, 84, 177, 40, 170, 152, 157, 165, 100, 109, 122, 212, 16, 129, 68, 239, 73, 214, 174, 46, 221, 118, 92, 47, 167, 28, 201, 9, 105, 154, 131, 207, 41, 57, 185, 233, 76, 255, 67, 171};
        rc2Ekb = new short[]{93, 190, 155, 139, 17, 153, 110, 77, 89, 243, 133, 166, 63, 183, 131, 197, 228, 115, 107, 58, 104, 90, 192, 71, 160, 100, 52, 12, 241, 208, 82, 165, 185, 30, 150, 67, 65, 216, 212, 44, 219, 248, 7, 119, 42, 202, 235, 239, 16, 28, 22, 13, 56, 114, 47, 137, 193, 249, 128, 196, 109, 174, 48, 61, 206, 32, 99, 254, 230, 26, 199, 184, 80, 232, 36, 23, 252, 37, 111, 187, 106, 163, 68, 83, 217, 162, 1, 171, 188, 182, 31, 152, 238, 154, 167, 45, 79, 158, 142, 172, 224, 198, 73, 70, 41, 244, 148, 138, 175, 225, 91, 195, 179, 123, 87, 209, 124, 156, 237, 135, 64, 140, 226, 203, 147, 20, 201, 97, 46, 229, 204, 246, 94, 168, 92, 214, 117, 141, 98, 149, 88, 105, 118, 161, 74, 181, 85, 9, 120, 51, 130, 215, 221, 121, 245, 27, 11, 222, 38, 33, 40, 116, 4, 151, 86, 223, 60, 240, 55, 57, 220, 255, 6, 164, 234, 66, 8, 218, 180, 113, 176, 207, 18, 122, 78, 250, 108, 29, 132, 0, 200, 127, 145, 69, 170, 43, 194, 177, 143, 213, 186, 242, 173, 25, 178, 103, 54, 247, 15, 10, 146, 125, 227, 157, 233, 144, 62, 35, 39, 102, 19, 236, 129, 21, 189, 34, 191, 159, 126, 169, 81, 75, 76, 251, 2, 211, 112, 134, 49, 231, 59, 5, 3, 84, 96, 72, 101, 24, 210, 205, 95, 50, 136, 14, 53, 253};
    }
}
