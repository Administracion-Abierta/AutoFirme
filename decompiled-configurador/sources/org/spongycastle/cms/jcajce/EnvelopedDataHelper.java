package org.spongycastle.cms.jcajce;

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
import java.util.Map;
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
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.RC2CBCParameter;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSAlgorithm;
import org.spongycastle.cms.CMSEnvelopedDataGenerator;
import org.spongycastle.cms.CMSEnvelopedGenerator;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.PasswordRecipient;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.SecretKeySizeProvider;
import org.spongycastle.operator.SymmetricKeyUnwrapper;
import org.spongycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.spongycastle.operator.jcajce.JceKTSKeyUnwrapper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/EnvelopedDataHelper.class */
public class EnvelopedDataHelper {
    protected static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    protected static final Map BASE_CIPHER_NAMES = new HashMap();
    protected static final Map CIPHER_ALG_NAMES = new HashMap();
    protected static final Map MAC_ALG_NAMES = new HashMap();
    private static final Map PBKDF2_ALG_NAMES = new HashMap();
    private static final short[] rc2Table;
    private static final short[] rc2Ekb;
    private JcaJceExtHelper helper;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/EnvelopedDataHelper$JCECallback.class */
    interface JCECallback {
        Object doInJCE() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, CMSException, InvalidAlgorithmParameterException;
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
        rc2Table = new short[]{189, 86, 234, 242, 162, 241, 172, 42, 176, 147, 209, 156, 27, 51, 253, 208, 48, 4, 182, 220, 125, 223, 50, 75, 247, 203, 69, 155, 49, 187, 33, 90, 65, 159, 225, 217, 74, 77, 158, 218, 160, 104, 44, 195, 39, 95, 128, 54, 62, 238, 251, 149, 26, 254, 206, 168, 52, 169, 19, 240, 166, 63, 216, 12, 120, 36, 175, 35, 82, 193, 103, 23, 245, 102, 144, 231, 232, 7, 184, 96, 72, 230, 30, 83, 243, 146, 164, 114, 140, 8, 21, 110, 134, 0, 132, 250, 244, 127, 138, 66, 25, 246, 219, 205, 20, 141, 80, 18, 186, 60, 6, 78, 236, 179, 53, 17, 161, 136, 142, 43, 148, 153, 183, 113, 116, 211, 228, 191, 58, 222, 150, 14, 188, 10, 237, 119, 252, 55, 107, 3, 121, 137, 98, 198, 215, 192, 210, 124, 106, 139, 34, 163, 91, 5, 93, 2, 117, 213, 97, 227, 24, 143, 85, 81, 173, 31, 11, 94, 133, 229, 194, 87, 99, 202, 61, 108, 180, 197, 204, 112, 178, 145, 89, 13, 71, 32, 200, 79, 88, 224, 1, 226, 22, 56, 196, 111, 59, 15, 101, 70, 190, 126, 45, 123, 130, 249, 64, 181, 29, 115, 248, 235, 38, 199, 135, 151, 37, 84, 177, 40, 170, 152, 157, 165, 100, 109, 122, 212, 16, 129, 68, 239, 73, 214, 174, 46, 221, 118, 92, 47, 167, 28, 201, 9, 105, 154, 131, 207, 41, 57, 185, 233, 76, 255, 67, 171};
        rc2Ekb = new short[]{93, 190, 155, 139, 17, 153, 110, 77, 89, 243, 133, 166, 63, 183, 131, 197, 228, 115, 107, 58, 104, 90, 192, 71, 160, 100, 52, 12, 241, 208, 82, 165, 185, 30, 150, 67, 65, 216, 212, 44, 219, 248, 7, 119, 42, 202, 235, 239, 16, 28, 22, 13, 56, 114, 47, 137, 193, 249, 128, 196, 109, 174, 48, 61, 206, 32, 99, 254, 230, 26, 199, 184, 80, 232, 36, 23, 252, 37, 111, 187, 106, 163, 68, 83, 217, 162, 1, 171, 188, 182, 31, 152, 238, 154, 167, 45, 79, 158, 142, 172, 224, 198, 73, 70, 41, 244, 148, 138, 175, 225, 91, 195, 179, 123, 87, 209, 124, 156, 237, 135, 64, 140, 226, 203, 147, 20, 201, 97, 46, 229, 204, 246, 94, 168, 92, 214, 117, 141, 98, 149, 88, 105, 118, 161, 74, 181, 85, 9, 120, 51, 130, 215, 221, 121, 245, 27, 11, 222, 38, 33, 40, 116, 4, 151, 86, 223, 60, 240, 55, 57, 220, 255, 6, 164, 234, 66, 8, 218, 180, 113, 176, 207, 18, 122, 78, 250, 108, 29, 132, 0, 200, 127, 145, 69, 170, 43, 194, 177, 143, 213, 186, 242, 173, 25, 178, 103, 54, 247, 15, 10, 146, 125, 227, 157, 233, 144, 62, 35, 39, 102, 19, 236, 129, 21, 189, 34, 191, 159, 126, 169, 81, 75, 76, 251, 2, 211, 112, 134, 49, 231, 59, 5, 3, 84, 96, 72, 101, 24, 210, 205, 95, 50, 136, 14, 53, 253};
    }

    EnvelopedDataHelper(JcaJceExtHelper helper) {
        this.helper = helper;
    }

    String getBaseCipherName(ASN1ObjectIdentifier algorithm) {
        String name = (String) BASE_CIPHER_NAMES.get(algorithm);
        if (name == null) {
            return algorithm.getId();
        }
        return name;
    }

    Key getJceKey(GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return (Key) key.getRepresentation();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) key.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    public Key getJceKey(ASN1ObjectIdentifier algorithm, GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return (Key) key.getRepresentation();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) key.getRepresentation(), getBaseCipherName(algorithm));
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    public void keySizeCheck(AlgorithmIdentifier keyAlgorithm, Key key) throws CMSException {
        int expectedKeySize = KEY_SIZE_PROVIDER.getKeySize(keyAlgorithm);
        if (expectedKeySize > 0) {
            byte[] keyEnc = null;
            try {
                keyEnc = key.getEncoded();
            } catch (Exception e) {
            }
            if (keyEnc != null && keyEnc.length * 8 != expectedKeySize) {
                throw new CMSException("Expected key size for algorithm OID not found in recipient.");
            }
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String cipherName = (String) CIPHER_ALG_NAMES.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createCipher(cipherName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createCipher(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    Mac createMac(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String macName = (String) MAC_ALG_NAMES.get(algorithm);
            if (macName != null) {
                try {
                    return this.helper.createMac(macName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createMac(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create mac: " + e2.getMessage(), e2);
        }
    }

    Cipher createRFC3211Wrapper(ASN1ObjectIdentifier algorithm) throws CMSException {
        String cipherName = (String) BASE_CIPHER_NAMES.get(algorithm);
        if (cipherName == null) {
            throw new CMSException("no name for " + algorithm);
        }
        try {
            return this.helper.createCipher(cipherName + "RFC3211Wrap");
        } catch (GeneralSecurityException e) {
            throw new CMSException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String agreementName = (String) BASE_CIPHER_NAMES.get(algorithm);
            if (agreementName != null) {
                try {
                    return this.helper.createKeyAgreement(agreementName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyAgreement(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key agreement: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameterGenerator createAlgorithmParameterGenerator(ASN1ObjectIdentifier algorithm) throws GeneralSecurityException {
        String algorithmName = (String) BASE_CIPHER_NAMES.get(algorithm);
        if (algorithmName != null) {
            try {
                return this.helper.createAlgorithmParameterGenerator(algorithmName);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return this.helper.createAlgorithmParameterGenerator(algorithm.getId());
    }

    public Cipher createContentCipher(final Key sKey, final AlgorithmIdentifier encryptionAlgID) throws CMSException {
        return (Cipher) execute(new JCECallback() { // from class: org.spongycastle.cms.jcajce.EnvelopedDataHelper.1
            @Override // org.spongycastle.cms.jcajce.EnvelopedDataHelper.JCECallback
            public Object doInJCE() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, CMSException, InvalidAlgorithmParameterException {
                Cipher cipher = EnvelopedDataHelper.this.createCipher(encryptionAlgID.getAlgorithm());
                ASN1Encodable sParams = encryptionAlgID.getParameters();
                String encAlg = encryptionAlgID.getAlgorithm().getId();
                if (sParams != null && !(sParams instanceof ASN1Null)) {
                    try {
                        AlgorithmParameters params = EnvelopedDataHelper.this.createAlgorithmParameters(encryptionAlgID.getAlgorithm());
                        CMSUtils.loadParameters(params, sParams);
                        cipher.init(2, sKey, params);
                    } catch (NoSuchAlgorithmException e) {
                        if (encAlg.equals(CMSAlgorithm.DES_CBC.getId()) || encAlg.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || encAlg.equals(CMSEnvelopedGenerator.IDEA_CBC) || encAlg.equals(CMSEnvelopedDataGenerator.AES128_CBC) || encAlg.equals(CMSEnvelopedDataGenerator.AES192_CBC) || encAlg.equals(CMSEnvelopedDataGenerator.AES256_CBC)) {
                            cipher.init(2, sKey, new IvParameterSpec(ASN1OctetString.getInstance(sParams).getOctets()));
                        } else {
                            throw e;
                        }
                    }
                } else if (encAlg.equals(CMSAlgorithm.DES_CBC.getId()) || encAlg.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || encAlg.equals(CMSEnvelopedGenerator.IDEA_CBC) || encAlg.equals(CMSEnvelopedGenerator.CAST5_CBC)) {
                    cipher.init(2, sKey, new IvParameterSpec(new byte[8]));
                } else {
                    cipher.init(2, sKey);
                }
                return cipher;
            }
        });
    }

    Mac createContentMac(final Key sKey, final AlgorithmIdentifier macAlgId) throws CMSException {
        return (Mac) execute(new JCECallback() { // from class: org.spongycastle.cms.jcajce.EnvelopedDataHelper.2
            @Override // org.spongycastle.cms.jcajce.EnvelopedDataHelper.JCECallback
            public Object doInJCE() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, CMSException, InvalidAlgorithmParameterException {
                Mac mac = EnvelopedDataHelper.this.createMac(macAlgId.getAlgorithm());
                ASN1Encodable sParams = macAlgId.getParameters();
                macAlgId.getAlgorithm().getId();
                if (sParams != null && !(sParams instanceof ASN1Null)) {
                    try {
                        AlgorithmParameters params = EnvelopedDataHelper.this.createAlgorithmParameters(macAlgId.getAlgorithm());
                        CMSUtils.loadParameters(params, sParams);
                        mac.init(sKey, params.getParameterSpec(AlgorithmParameterSpec.class));
                    } catch (NoSuchAlgorithmException e) {
                        throw e;
                    }
                } else {
                    mac.init(sKey);
                }
                return mac;
            }
        });
    }

    AlgorithmParameters createAlgorithmParameters(ASN1ObjectIdentifier algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        String algorithmName = (String) BASE_CIPHER_NAMES.get(algorithm);
        if (algorithmName != null) {
            try {
                return this.helper.createAlgorithmParameters(algorithmName);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return this.helper.createAlgorithmParameters(algorithm.getId());
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String cipherName = (String) BASE_CIPHER_NAMES.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createKeyPairGenerator(cipherName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyPairGenerator(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key pair generator: " + e2.getMessage(), e2);
        }
    }

    public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String cipherName = (String) BASE_CIPHER_NAMES.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createKeyGenerator(cipherName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyGenerator(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key generator: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameters generateParameters(ASN1ObjectIdentifier encryptionOID, SecretKey encKey, SecureRandom rand) throws CMSException {
        try {
            AlgorithmParameterGenerator pGen = createAlgorithmParameterGenerator(encryptionOID);
            if (encryptionOID.equals(CMSAlgorithm.RC2_CBC)) {
                byte[] iv = new byte[8];
                rand.nextBytes(iv);
                try {
                    pGen.init(new RC2ParameterSpec(encKey.getEncoded().length * 8, iv), rand);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new CMSException("parameters generation error: " + e, e);
                }
            }
            return pGen.generateParameters();
        } catch (NoSuchAlgorithmException e2) {
            return null;
        } catch (GeneralSecurityException e3) {
            throw new CMSException("exception creating algorithm parameter generator: " + e3, e3);
        }
    }

    AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier encryptionOID, AlgorithmParameters params) throws CMSException {
        ASN1Encodable asn1Params;
        if (params != null) {
            asn1Params = CMSUtils.extractParameters(params);
        } else {
            asn1Params = DERNull.INSTANCE;
        }
        return new AlgorithmIdentifier(encryptionOID, asn1Params);
    }

    static Object execute(JCECallback callback) throws CMSException {
        try {
            return callback.doInJCE();
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

    public KeyFactory createKeyFactory(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String cipherName = (String) BASE_CIPHER_NAMES.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createKeyFactory(cipherName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyFactory(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key factory: " + e2.getMessage(), e2);
        }
    }

    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey) {
        return this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey);
    }

    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey, byte[] partyUInfo, byte[] partyVInfo) {
        return this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey, partyUInfo, partyVInfo);
    }

    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, SecretKey keyEncryptionKey) {
        return this.helper.createSymmetricUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey);
    }

    public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier macOID, AlgorithmParameterSpec paramSpec) {
        int parameterVersion;
        if (paramSpec instanceof IvParameterSpec) {
            return new AlgorithmIdentifier(macOID, new DEROctetString(((IvParameterSpec) paramSpec).getIV()));
        }
        if (paramSpec instanceof RC2ParameterSpec) {
            RC2ParameterSpec rc2Spec = (RC2ParameterSpec) paramSpec;
            int effKeyBits = ((RC2ParameterSpec) paramSpec).getEffectiveKeyBits();
            if (effKeyBits != -1) {
                if (effKeyBits < 256) {
                    parameterVersion = rc2Table[effKeyBits];
                } else {
                    parameterVersion = effKeyBits;
                }
                return new AlgorithmIdentifier(macOID, new RC2CBCParameter(parameterVersion, rc2Spec.getIV()));
            }
            return new AlgorithmIdentifier(macOID, new RC2CBCParameter(rc2Spec.getIV()));
        }
        throw new IllegalStateException("unknown parameter spec: " + paramSpec);
    }

    SecretKeyFactory createSecretKeyFactory(String keyFactoryAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createSecretKeyFactory(keyFactoryAlgorithm);
    }

    byte[] calculateDerivedKey(int schemeID, char[] password, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
        SecretKeyFactory keyFact;
        PBKDF2Params params = PBKDF2Params.getInstance(derivationAlgorithm.getParameters());
        try {
            if (schemeID == 0) {
                keyFact = this.helper.createSecretKeyFactory("PBKDF2with8BIT");
            } else {
                keyFact = this.helper.createSecretKeyFactory((String) PBKDF2_ALG_NAMES.get(params.getPrf()));
            }
            SecretKey key = keyFact.generateSecret(new PBEKeySpec(password, params.getSalt(), params.getIterationCount().intValue(), keySize));
            return key.getEncoded();
        } catch (GeneralSecurityException e) {
            throw new CMSException("Unable to calculate derived key from password: " + e.getMessage(), e);
        }
    }
}
