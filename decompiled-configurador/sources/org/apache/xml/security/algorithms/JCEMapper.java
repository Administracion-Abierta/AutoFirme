package org.apache.xml.security.algorithms;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/JCEMapper.class */
public class JCEMapper {
    private static String globalProviderName;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) JCEMapper.class);
    private static Map<String, Algorithm> algorithmsMap = new ConcurrentHashMap();
    private static final ThreadLocal<String> threadSpecificProviderName = new ThreadLocal<>();

    public static void register(String id, Algorithm algorithm) {
        JavaUtils.checkRegisterPermission();
        algorithmsMap.put(id, algorithm);
    }

    public static void registerDefaultAlgorithms() {
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5, new Algorithm("", MessageDigestAlgorithms.MD5, "MessageDigest"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#ripemd160", new Algorithm("", "RIPEMD160", "MessageDigest"));
        algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#sha1", new Algorithm("", "SHA-1", "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA224, new Algorithm("", "SHA-224", "MessageDigest"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha256", new Algorithm("", "SHA-256", "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, new Algorithm("", "SHA-384", "MessageDigest"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha512", new Algorithm("", "SHA-512", "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_WHIRLPOOL, new Algorithm("", "WHIRLPOOL", "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_224, new Algorithm("", MessageDigestAlgorithms.SHA3_224, "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_256, new Algorithm("", "SHA3-256", "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_384, new Algorithm("", MessageDigestAlgorithms.SHA3_384, "MessageDigest"));
        algorithmsMap.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_512, new Algorithm("", MessageDigestAlgorithms.SHA3_512, "MessageDigest"));
        algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", new Algorithm("DSA", AOSignConstants.SIGN_ALGORITHM_SHA1WITHDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256, new Algorithm("DSA", "SHA256withDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, new Algorithm("RSA", "MD5withRSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160, new Algorithm("RSA", "RIPEMD160withRSA", "Signature"));
        algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", new Algorithm("RSA", AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224, new Algorithm("RSA", "SHA224withRSA", "Signature"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", new Algorithm("RSA", AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA, "Signature"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", new Algorithm("RSA", AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA, "Signature"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", new Algorithm("RSA", "SHA512withRSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1_MGF1, new Algorithm("RSA", "SHA1withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224_MGF1, new Algorithm("RSA", "SHA224withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1, new Algorithm("RSA", "SHA256withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1, new Algorithm("RSA", "SHA384withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512_MGF1, new Algorithm("RSA", "SHA512withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1, new Algorithm("RSA", "SHA3-224withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1, new Algorithm("RSA", "SHA3-256withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1, new Algorithm("RSA", "SHA3-384withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1, new Algorithm("RSA", "SHA3-512withRSAandMGF1", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS, new Algorithm("RSA", "RSASSA-PSS", "Signature"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", new Algorithm("EC", AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA224, new Algorithm("EC", AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, new Algorithm("EC", AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, new Algorithm("EC", AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, new Algorithm("EC", AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA, "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_224, new Algorithm("EC", "SHA3-224withECDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_256, new Algorithm("EC", "SHA3-256withECDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_384, new Algorithm("EC", "SHA3-384withECDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_512, new Algorithm("EC", "SHA3-512withECDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_RIPEMD160, new Algorithm("EC", "RIPEMD160withECDSA", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED25519, new Algorithm("Ed25519", "Ed25519", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED448, new Algorithm("Ed448", "Ed448", "Signature"));
        algorithmsMap.put(XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5, new Algorithm("", "HmacMD5", "Mac", 0, 0));
        algorithmsMap.put(XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, new Algorithm("", "HMACRIPEMD160", "Mac", 0, 0));
        algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", new Algorithm("", "HmacSHA1", "Mac", 0, 0));
        algorithmsMap.put(XMLSignature.ALGO_ID_MAC_HMAC_SHA224, new Algorithm("", "HmacSHA224", "Mac", 0, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", new Algorithm("", "HmacSHA256", "Mac", 0, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", new Algorithm("", "HmacSHA384", "Mac", 0, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", new Algorithm("", "HmacSHA512", "Mac", 0, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", new Algorithm("DESede", "DESede/CBC/ISO10126Padding", "BlockEncryption", 192, 64));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes128-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 128, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes192-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 192, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#aes256-cbc", new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 256, 128));
        algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes128-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 128, 96));
        algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes192-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 192, 96));
        algorithmsMap.put("http://www.w3.org/2009/xmlenc11#aes256-gcm", new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 256, 96));
        algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#seed128-cbc", new Algorithm("SEED", "SEED/CBC/ISO10126Padding", "BlockEncryption", 128, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc", new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 128, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc", new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 192, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc", new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 256, 128));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-1_5", new Algorithm("RSA", "RSA/ECB/PKCS1Padding", "KeyTransport"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
        algorithmsMap.put("http://www.w3.org/2009/xmlenc11#rsa-oaep", new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#dh", new Algorithm("", "", "KeyAgreement"));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-tripledes", new Algorithm("DESede", "DESedeWrap", "SymmetricKeyWrap", 192, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes128", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 128, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes192", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 192, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#kw-aes256", new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 256, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128", new Algorithm("Camellia", "CamelliaWrap", "SymmetricKeyWrap", 128, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192", new Algorithm("Camellia", "CamelliaWrap", "SymmetricKeyWrap", 192, 0));
        algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256", new Algorithm("Camellia", "CamelliaWrap", "SymmetricKeyWrap", 256, 0));
        algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#kw-seed128", new Algorithm("SEED", "SEEDWrap", "SymmetricKeyWrap", 128, 0));
    }

    public static String translateURItoJCEID(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.jceName;
        }
        return null;
    }

    public static String getAlgorithmClassFromURI(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.algorithmClass;
        }
        return null;
    }

    public static int getKeyLengthFromURI(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.keyLength;
        }
        return 0;
    }

    public static int getIVLengthFromURI(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.ivLength;
        }
        return 0;
    }

    public static String getJCEKeyAlgorithmFromURI(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.requiredKey;
        }
        return null;
    }

    public static String getJCEProviderFromURI(String algorithmURI) {
        Algorithm algorithm = getAlgorithm(algorithmURI);
        if (algorithm != null) {
            return algorithm.jceProvider;
        }
        return null;
    }

    private static Algorithm getAlgorithm(String algorithmURI) {
        LOG.debug("Request for URI {}", algorithmURI);
        if (algorithmURI != null) {
            return algorithmsMap.get(algorithmURI);
        }
        return null;
    }

    public static String getProviderId() {
        if (threadSpecificProviderName.get() != null) {
            return threadSpecificProviderName.get();
        }
        return globalProviderName;
    }

    public static void setProviderId(String provider) {
        JavaUtils.checkRegisterPermission();
        globalProviderName = provider;
    }

    public static void setThreadSpecificProviderName(String threadSpecificProviderName2) {
        JavaUtils.checkRegisterPermission();
        threadSpecificProviderName.set(threadSpecificProviderName2);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/JCEMapper$Algorithm.class */
    public static class Algorithm {
        final String requiredKey;
        final String jceName;
        final String algorithmClass;
        final int keyLength;
        final int ivLength;
        final String jceProvider;

        public Algorithm(Element el) {
            this.requiredKey = el.getAttributeNS(null, "RequiredKey");
            this.jceName = el.getAttributeNS(null, "JCEName");
            this.algorithmClass = el.getAttributeNS(null, "AlgorithmClass");
            this.jceProvider = el.getAttributeNS(null, "JCEProvider");
            if (el.hasAttribute(EncryptionConstants._TAG_KEYLENGTH)) {
                this.keyLength = Integer.parseInt(el.getAttributeNS(null, EncryptionConstants._TAG_KEYLENGTH));
            } else {
                this.keyLength = 0;
            }
            if (el.hasAttribute("IVLength")) {
                this.ivLength = Integer.parseInt(el.getAttributeNS(null, "IVLength"));
            } else {
                this.ivLength = 0;
            }
        }

        public Algorithm(String requiredKey, String jceName) {
            this(requiredKey, jceName, null, 0, 0);
        }

        public Algorithm(String requiredKey, String jceName, String algorithmClass) {
            this(requiredKey, jceName, algorithmClass, 0, 0);
        }

        public Algorithm(String requiredKey, String jceName, int keyLength) {
            this(requiredKey, jceName, null, keyLength, 0);
        }

        public Algorithm(String requiredKey, String jceName, String algorithmClass, int keyLength, int ivLength) {
            this(requiredKey, jceName, algorithmClass, keyLength, ivLength, null);
        }

        public Algorithm(String requiredKey, String jceName, String algorithmClass, int keyLength, int ivLength, String jceProvider) {
            this.requiredKey = requiredKey;
            this.jceName = jceName;
            this.algorithmClass = algorithmClass;
            this.keyLength = keyLength;
            this.ivLength = ivLength;
            this.jceProvider = jceProvider;
        }
    }
}
