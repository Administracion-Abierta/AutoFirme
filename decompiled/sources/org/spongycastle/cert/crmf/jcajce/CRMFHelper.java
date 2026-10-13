package org.spongycastle.cert.crmf.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.iana.IANAObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cert.crmf.CRMFException;
import org.spongycastle.cms.CMSAlgorithm;
import org.spongycastle.jcajce.util.AlgorithmParametersUtils;
import org.spongycastle.jcajce.util.JcaJceHelper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/jcajce/CRMFHelper.class */
class CRMFHelper {
    protected static final Map BASE_CIPHER_NAMES = new HashMap();
    protected static final Map CIPHER_ALG_NAMES = new HashMap();
    protected static final Map DIGEST_ALG_NAMES = new HashMap();
    protected static final Map KEY_ALG_NAMES = new HashMap();
    protected static final Map MAC_ALG_NAMES = new HashMap();
    private JcaJceHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/jcajce/CRMFHelper$JCECallback.class */
    interface JCECallback {
        Object doInJCE() throws CRMFException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException;
    }

    static {
        BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
        BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
        CIPHER_ALG_NAMES.put(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()), "RSA/ECB/PKCS1Padding");
        DIGEST_ALG_NAMES.put(OIWObjectIdentifiers.idSHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1);
        DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha384, AOSignConstants.DIGEST_ALGORITHM_SHA384);
        DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        MAC_ALG_NAMES.put(IANAObjectIdentifiers.hmacSHA1, "HMACSHA1");
        MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "HMACSHA1");
        MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "HMACSHA224");
        MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "HMACSHA256");
        MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "HMACSHA384");
        MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "HMACSHA512");
        KEY_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        KEY_ALG_NAMES.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }

    CRMFHelper(JcaJceHelper helper) {
        this.helper = helper;
    }

    PublicKey toPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        try {
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
            AlgorithmIdentifier keyAlg = subjectPublicKeyInfo.getAlgorithm();
            return createKeyFactory(keyAlg.getAlgorithm()).generatePublic(xspec);
        } catch (Exception e) {
            throw new CRMFException("invalid key: " + e.getMessage(), e);
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier algorithm) throws CRMFException {
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
            throw new CRMFException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier algorithm) throws CRMFException {
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
            throw new CRMFException("cannot create key generator: " + e2.getMessage(), e2);
        }
    }

    Cipher createContentCipher(final Key sKey, final AlgorithmIdentifier encryptionAlgID) throws CRMFException {
        return (Cipher) execute(new JCECallback() { // from class: org.spongycastle.cert.crmf.jcajce.CRMFHelper.1
            @Override // org.spongycastle.cert.crmf.jcajce.CRMFHelper.JCECallback
            public Object doInJCE() throws CRMFException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
                Cipher cipher = CRMFHelper.this.createCipher(encryptionAlgID.getAlgorithm());
                ASN1Primitive sParams = (ASN1Primitive) encryptionAlgID.getParameters();
                ASN1ObjectIdentifier encAlg = encryptionAlgID.getAlgorithm();
                if (sParams != null && !(sParams instanceof ASN1Null)) {
                    try {
                        AlgorithmParameters params = CRMFHelper.this.createAlgorithmParameters(encryptionAlgID.getAlgorithm());
                        try {
                            AlgorithmParametersUtils.loadParameters(params, sParams);
                            cipher.init(2, sKey, params);
                        } catch (IOException e) {
                            throw new CRMFException("error decoding algorithm parameters.", e);
                        }
                    } catch (NoSuchAlgorithmException e2) {
                        if (encAlg.equals(CMSAlgorithm.DES_EDE3_CBC) || encAlg.equals(CMSAlgorithm.IDEA_CBC) || encAlg.equals(CMSAlgorithm.AES128_CBC) || encAlg.equals(CMSAlgorithm.AES192_CBC) || encAlg.equals(CMSAlgorithm.AES256_CBC)) {
                            cipher.init(2, sKey, new IvParameterSpec(ASN1OctetString.getInstance(sParams).getOctets()));
                        } else {
                            throw e2;
                        }
                    }
                } else if (encAlg.equals(CMSAlgorithm.DES_EDE3_CBC) || encAlg.equals(CMSAlgorithm.IDEA_CBC) || encAlg.equals(CMSAlgorithm.CAST5_CBC)) {
                    cipher.init(2, sKey, new IvParameterSpec(new byte[8]));
                } else {
                    cipher.init(2, sKey);
                }
                return cipher;
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

    KeyFactory createKeyFactory(ASN1ObjectIdentifier algorithm) throws CRMFException {
        try {
            String algName = (String) KEY_ALG_NAMES.get(algorithm);
            if (algName != null) {
                try {
                    return this.helper.createKeyFactory(algName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyFactory(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CRMFException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    MessageDigest createDigest(ASN1ObjectIdentifier algorithm) throws CRMFException {
        try {
            String digestName = (String) DIGEST_ALG_NAMES.get(algorithm);
            if (digestName != null) {
                try {
                    return this.helper.createDigest(digestName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createDigest(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CRMFException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    Mac createMac(ASN1ObjectIdentifier algorithm) throws CRMFException {
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
            throw new CRMFException("cannot create mac: " + e2.getMessage(), e2);
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

    AlgorithmParameters generateParameters(ASN1ObjectIdentifier encryptionOID, SecretKey encKey, SecureRandom rand) throws CRMFException {
        try {
            AlgorithmParameterGenerator pGen = createAlgorithmParameterGenerator(encryptionOID);
            if (encryptionOID.equals(CMSAlgorithm.RC2_CBC)) {
                byte[] iv = new byte[8];
                rand.nextBytes(iv);
                try {
                    pGen.init(new RC2ParameterSpec(encKey.getEncoded().length * 8, iv), rand);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new CRMFException("parameters generation error: " + e, e);
                }
            }
            return pGen.generateParameters();
        } catch (NoSuchAlgorithmException e2) {
            return null;
        } catch (GeneralSecurityException e3) {
            throw new CRMFException("exception creating algorithm parameter generator: " + e3, e3);
        }
    }

    AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier encryptionOID, AlgorithmParameters params) throws CRMFException {
        ASN1Encodable asn1Params;
        if (params != null) {
            try {
                asn1Params = AlgorithmParametersUtils.extractParameters(params);
            } catch (IOException e) {
                throw new CRMFException("cannot encode parameters: " + e.getMessage(), e);
            }
        } else {
            asn1Params = DERNull.INSTANCE;
        }
        return new AlgorithmIdentifier(encryptionOID, asn1Params);
    }

    static Object execute(JCECallback callback) throws CRMFException {
        try {
            return callback.doInJCE();
        } catch (InvalidAlgorithmParameterException e) {
            throw new CRMFException("algorithm parameters invalid.", e);
        } catch (InvalidKeyException e2) {
            throw new CRMFException("key invalid in message.", e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new CRMFException("can't find algorithm.", e3);
        } catch (NoSuchProviderException e4) {
            throw new CRMFException("can't find provider.", e4);
        } catch (InvalidParameterSpecException e5) {
            throw new CRMFException("MAC algorithm parameter spec invalid.", e5);
        } catch (NoSuchPaddingException e6) {
            throw new CRMFException("required padding not supported.", e6);
        }
    }
}
