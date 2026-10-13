package org.apache.xml.security.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.algorithms.implementations.ECDSAUtils;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.encryption.keys.content.derivedKey.ConcatKDF;
import org.apache.xml.security.encryption.keys.content.derivedKey.HKDF;
import org.apache.xml.security.encryption.params.ConcatKDFParams;
import org.apache.xml.security.encryption.params.HKDFParams;
import org.apache.xml.security.encryption.params.KeyAgreementParameters;
import org.apache.xml.security.encryption.params.KeyDerivationParameters;
import org.apache.xml.security.exceptions.DERDecodingException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/KeyUtils.class */
public class KeyUtils {
    private static final Logger LOG = LoggerFactory.getLogger(KeyUtils.class.getName());

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/KeyUtils$KeyAlgorithmType.class */
    public enum KeyAlgorithmType {
        EC("EC", "1.2.840.10045.2.1"),
        DSA("DSA", "1.2.840.10040.4.1"),
        RSA("RSA", "1.2.840.113549.1.1.1"),
        RSASSA_PSS("RSASSA-PSS", "1.2.840.113549.1.1.10"),
        DH("DiffieHellman", "1.2.840.113549.1.3.1"),
        XDH("XDH", null),
        EdDSA("EdDSA", null);

        private final String jceName;
        private final String oid;

        KeyAlgorithmType(String jceName, String oid) {
            this.jceName = jceName;
            this.oid = oid;
        }

        public String getJceName() {
            return this.jceName;
        }

        public String getOid() {
            return this.oid;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/KeyUtils$KeyType.class */
    public enum KeyType {
        DSA("DSA", "RFC 8017", KeyAlgorithmType.DSA, "1.2.840.10040.4.1"),
        RSA("RSA", "RFC 8017", KeyAlgorithmType.RSA, "1.2.840.113549.1.1.1"),
        RSASSA_PSS("RSASSA-PSS", "RFC 3447", KeyAlgorithmType.RSASSA_PSS, "1.2.840.113549.1.1.10"),
        SECT163K1("sect163k1", "NIST K-163", KeyAlgorithmType.EC, "1.3.132.0.1"),
        SECT163R1("sect163r1", "", KeyAlgorithmType.EC, "1.3.132.0.2"),
        SECT163R2("sect163r2", "NIST B-163", KeyAlgorithmType.EC, "1.3.132.0.15"),
        SECT193R1("sect193r1", "", KeyAlgorithmType.EC, "1.3.132.0.24"),
        SECT193R2("sect193r2", "", KeyAlgorithmType.EC, "1.3.132.0.25"),
        SECT233K1("sect233k1", "NIST K-233", KeyAlgorithmType.EC, "1.3.132.0.26"),
        SECT233R1("sect233r1", "NIST B-233", KeyAlgorithmType.EC, "1.3.132.0.27"),
        SECT239K1("sect239k1", "", KeyAlgorithmType.EC, "1.3.132.0.3"),
        SECT283K1("sect283k1", "NIST K-283", KeyAlgorithmType.EC, "1.3.132.0.16"),
        SECT283R1("sect283r1", "", KeyAlgorithmType.EC, "1.3.132.0.17"),
        SECT409K1("sect409k1", "NIST K-409", KeyAlgorithmType.EC, "1.3.132.0.36"),
        SECT409R1("sect409r1", "NIST B-409", KeyAlgorithmType.EC, "1.3.132.0.37"),
        SECT571K1("sect571k1", "NIST K-571", KeyAlgorithmType.EC, "1.3.132.0.38"),
        SECT571R1("sect571r1", "NIST B-571", KeyAlgorithmType.EC, "1.3.132.0.39"),
        SECP160K1("secp160k1", "", KeyAlgorithmType.EC, "1.3.132.0.9"),
        SECP160R1("secp160r1", "", KeyAlgorithmType.EC, "1.3.132.0.8"),
        SECP160R2("secp160r2", "", KeyAlgorithmType.EC, "1.3.132.0.30"),
        SECP192K1("secp192k1", "", KeyAlgorithmType.EC, "1.3.132.0.31"),
        SECP192R1("secp192r1", "NIST P-192,X9.62 prime192v1", KeyAlgorithmType.EC, "1.2.840.10045.3.1.1"),
        SECP224K1("secp224k1", "", KeyAlgorithmType.EC, "1.3.132.0.32"),
        SECP224R1("secp224r1", "NIST P-224", KeyAlgorithmType.EC, "1.3.132.0.33"),
        SECP256K1("secp256k1", "", KeyAlgorithmType.EC, "1.3.132.0.10"),
        SECP256R1("secp256r1", "NIST P-256,X9.62 prime256v1", KeyAlgorithmType.EC, "1.2.840.10045.3.1.7"),
        SECP384R1("secp384r1", "NIST P-384", KeyAlgorithmType.EC, "1.3.132.0.34"),
        SECP521R1("secp521r1", "NIST P-521", KeyAlgorithmType.EC, "1.3.132.0.35"),
        BRAINPOOLP256R1("brainpoolP256r1", "RFC 5639", KeyAlgorithmType.EC, "1.3.36.3.3.2.8.1.1.7"),
        BRAINPOOLP384R1("brainpoolP384r1", "RFC 5639", KeyAlgorithmType.EC, "1.3.36.3.3.2.8.1.1.11"),
        BRAINPOOLP512R1("brainpoolP512r1", "RFC 5639", KeyAlgorithmType.EC, "1.3.36.3.3.2.8.1.1.13"),
        X25519("x25519", "RFC 7748", KeyAlgorithmType.XDH, "1.3.101.110"),
        X448("x448", "RFC 7748", KeyAlgorithmType.XDH, "1.3.101.111"),
        ED25519("ed25519", "RFC 8032", KeyAlgorithmType.EdDSA, "1.3.101.112"),
        ED448("ed448", "RFC 8032", KeyAlgorithmType.EdDSA, "1.3.101.113");

        private final String name;
        private final String origin;
        private final KeyAlgorithmType algorithm;
        private final String oid;

        KeyType(String name, String origin, KeyAlgorithmType algorithm, String oid) {
            this.name = name;
            this.origin = origin;
            this.algorithm = algorithm;
            this.oid = oid;
        }

        public String getName() {
            return this.name;
        }

        public KeyAlgorithmType getAlgorithm() {
            return this.algorithm;
        }

        public String getOid() {
            return this.oid;
        }

        public String getOrigin() {
            return this.origin;
        }

        public static KeyType getByOid(String oid) {
            return (KeyType) Arrays.stream(values()).filter(keyType -> {
                return keyType.getOid().equals(oid);
            }).findFirst().orElse(null);
        }
    }

    public static KeyPair generateEphemeralDHKeyPair(PublicKey recipientPublicKey, Provider provider) throws XMLEncryptionException {
        KeyPairGenerator keyPairGenerator;
        String algorithm = recipientPublicKey.getAlgorithm();
        try {
            if (recipientPublicKey instanceof ECPublicKey) {
                keyPairGenerator = createKeyPairGenerator(algorithm, provider);
                ECPublicKey exchangePublicKey = (ECPublicKey) recipientPublicKey;
                String keyOId = ECDSAUtils.getOIDFromPublicKey(exchangePublicKey);
                if (keyOId == null) {
                    keyOId = DERDecoderUtils.getAlgorithmIdFromPublicKey(recipientPublicKey);
                }
                ECGenParameterSpec kpgparams = new ECGenParameterSpec(keyOId);
                keyPairGenerator.initialize(kpgparams);
            } else {
                String keyOId2 = DERDecoderUtils.getAlgorithmIdFromPublicKey(recipientPublicKey);
                KeyType keyType = KeyType.getByOid(keyOId2);
                keyPairGenerator = createKeyPairGenerator(keyType == null ? keyOId2 : keyType.getName(), provider);
            }
            return keyPairGenerator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | DERDecodingException e) {
            throw new XMLEncryptionException(e);
        }
    }

    public static KeyPairGenerator createKeyPairGenerator(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        return provider == null ? KeyPairGenerator.getInstance(algorithm) : KeyPairGenerator.getInstance(algorithm, provider);
    }

    public static SecretKey aesWrapKeyWithDHGeneratedKey(KeyAgreementParameters parameterSpec) throws XMLEncryptionException {
        try {
            PublicKey publicKey = parameterSpec.getAgreementPublicKey();
            PrivateKey privateKey = parameterSpec.getAgreementPrivateKey();
            String algorithm = publicKey.getAlgorithm();
            if ("EC".equalsIgnoreCase(algorithm)) {
                LOG.warn("EC keys are detected for key agreement algorithm! Cryptographic algorithm may not be secure, consider using a different algorithm (and keys).");
            }
            KeyAgreement keyAgreement = KeyAgreement.getInstance(algorithm + (algorithm.equalsIgnoreCase("EC") ? "DH" : ""));
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            byte[] secret = keyAgreement.generateSecret();
            byte[] kek = deriveKeyEncryptionKey(secret, parameterSpec.getKeyDerivationParameter());
            return new SecretKeySpec(kek, "AES");
        } catch (InvalidKeyException | NoSuchAlgorithmException | XMLSecurityException e) {
            throw new XMLEncryptionException(e);
        }
    }

    public static int getAESKeyBitSizeForWrapAlgorithm(String keyWrapAlg) throws XMLEncryptionException {
        switch (keyWrapAlg) {
            case "http://www.w3.org/2001/04/xmlenc#kw-aes128":
                return 128;
            case "http://www.w3.org/2001/04/xmlenc#kw-aes192":
                return 192;
            case "http://www.w3.org/2001/04/xmlenc#kw-aes256":
                return 256;
            default:
                throw new XMLEncryptionException("Unsupported KeyWrap Algorithm");
        }
    }

    public static byte[] deriveKeyEncryptionKey(byte[] sharedSecret, KeyDerivationParameters keyDerivationParameter) throws XMLSecurityException {
        if (keyDerivationParameter == null) {
            throw new IllegalArgumentException(I18n.translate("KeyDerivation.MissingParameters"));
        }
        String keyDerivationAlgorithm = keyDerivationParameter.getAlgorithm();
        if (keyDerivationParameter instanceof HKDFParams) {
            return deriveKeyWithHKDF(sharedSecret, (HKDFParams) keyDerivationParameter);
        }
        if (keyDerivationParameter instanceof ConcatKDFParams) {
            return deriveKeyWithConcatKDF(sharedSecret, (ConcatKDFParams) keyDerivationParameter);
        }
        throw new XMLEncryptionException("KeyDerivation.UnsupportedAlgorithm", keyDerivationAlgorithm, keyDerivationParameter.getClass().getName());
    }

    public static byte[] deriveKeyWithHKDF(byte[] sharedSecret, HKDFParams hkdfParameter) throws XMLSecurityException {
        if (!EncryptionConstants.ALGO_ID_KEYDERIVATION_HKDF.equals(hkdfParameter.getAlgorithm())) {
            throw new XMLEncryptionException("KeyDerivation.UnsupportedAlgorithm", hkdfParameter.getAlgorithm(), HKDFParams.class.getName());
        }
        HKDF kdf = new HKDF();
        return kdf.deriveKey(sharedSecret, hkdfParameter);
    }

    public static byte[] deriveKeyWithConcatKDF(byte[] sharedSecret, ConcatKDFParams ckdfParameter) throws XMLSecurityException {
        if (!EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF.equals(ckdfParameter.getAlgorithm())) {
            throw new XMLEncryptionException("KeyDerivation.UnsupportedAlgorithm", ckdfParameter.getAlgorithm(), HKDFParams.class.getName());
        }
        ConcatKDF concatKDF = new ConcatKDF();
        return concatKDF.deriveKey(sharedSecret, ckdfParameter);
    }
}
