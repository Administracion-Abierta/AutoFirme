package org.apache.xml.security.encryption;

import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.keys.content.derivedKey.ConcatKDFParamsImpl;
import org.apache.xml.security.encryption.keys.content.derivedKey.HKDFParamsImpl;
import org.apache.xml.security.encryption.keys.content.derivedKey.KDFParams;
import org.apache.xml.security.encryption.params.ConcatKDFParams;
import org.apache.xml.security.encryption.params.HKDFParams;
import org.apache.xml.security.encryption.params.KeyAgreementParameters;
import org.apache.xml.security.encryption.params.KeyDerivationParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/XMLCipherUtil.class */
public final class XMLCipherUtil {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLCipherUtil.class);
    private static final boolean gcmUseIvParameterSpec = ((Boolean) AccessController.doPrivileged(() -> {
        return Boolean.valueOf(Boolean.getBoolean("org.apache.xml.security.cipher.gcm.useIvParameterSpec"));
    })).booleanValue();

    public static AlgorithmParameterSpec constructBlockCipherParameters(String algorithm, byte[] iv) {
        if ("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(algorithm) || "http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(algorithm) || "http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(algorithm)) {
            return constructBlockCipherParametersForGCMAlgorithm(algorithm, iv);
        }
        LOG.debug("Saw non-AES-GCM mode block cipher, returning IvParameterSpec: {}", algorithm);
        return new IvParameterSpec(iv);
    }

    public static AlgorithmParameterSpec constructBlockCipherParameters(boolean gcmAlgorithm, byte[] iv) {
        if (gcmAlgorithm) {
            return constructBlockCipherParametersForGCMAlgorithm("AES/GCM/NoPadding", iv);
        }
        LOG.debug("Saw non-AES-GCM mode block cipher, returning IvParameterSpec");
        return new IvParameterSpec(iv);
    }

    private static AlgorithmParameterSpec constructBlockCipherParametersForGCMAlgorithm(String algorithm, byte[] iv) {
        if (gcmUseIvParameterSpec) {
            LOG.debug("Saw AES-GCM block cipher, using IvParameterSpec due to system property override: {}", algorithm);
            return new IvParameterSpec(iv);
        }
        LOG.debug("Saw AES-GCM block cipher, attempting to create GCMParameterSpec: {}", algorithm);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        LOG.debug("Successfully created GCMParameterSpec");
        return gcmSpec;
    }

    public static OAEPParameterSpec constructOAEPParameters(String encryptionAlgorithmURI, String digestAlgorithmURI, String mgfAlgorithmURI, byte[] oaepParams) {
        if ("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionAlgorithmURI) || "http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionAlgorithmURI)) {
            String jceDigestAlgorithm = "SHA-1";
            if (digestAlgorithmURI != null) {
                jceDigestAlgorithm = JCEMapper.translateURItoJCEID(digestAlgorithmURI);
            }
            PSource.PSpecified pSource = oaepParams == null ? PSource.PSpecified.DEFAULT : new PSource.PSpecified(oaepParams);
            MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
            if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionAlgorithmURI)) {
                mgfParameterSpec = constructMGF1Parameter(mgfAlgorithmURI);
            }
            return new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
        }
        return null;
    }

    public static MGF1ParameterSpec constructMGF1Parameter(String mgh1AlgorithmURI) {
        LOG.debug("Creating MGF1ParameterSpec for [{0}]", mgh1AlgorithmURI);
        if (mgh1AlgorithmURI == null || mgh1AlgorithmURI.isEmpty()) {
            LOG.warn("MGF1 algorithm URI is null or empty. Using SHA-1 as default.");
            return new MGF1ParameterSpec("SHA-1");
        }
        switch (mgh1AlgorithmURI) {
            case "http://www.w3.org/2009/xmlenc11#mgf1sha1":
                return new MGF1ParameterSpec("SHA-1");
            case "http://www.w3.org/2009/xmlenc11#mgf1sha224":
                return new MGF1ParameterSpec("SHA-224");
            case "http://www.w3.org/2009/xmlenc11#mgf1sha256":
                return new MGF1ParameterSpec("SHA-256");
            case "http://www.w3.org/2009/xmlenc11#mgf1sha384":
                return new MGF1ParameterSpec("SHA-384");
            case "http://www.w3.org/2009/xmlenc11#mgf1sha512":
                return new MGF1ParameterSpec("SHA-512");
            default:
                LOG.warn("Unsupported MGF algorithm: [{0}] Using SHA-1 as default.", mgh1AlgorithmURI);
                return new MGF1ParameterSpec("SHA-1");
        }
    }

    public static String getMgf1URIForParameter(MGF1ParameterSpec parameterSpec) {
        String digestAlgorithm = parameterSpec.getDigestAlgorithm();
        LOG.debug("Get MGF1 URI for digest algorithm [{0}]", digestAlgorithm);
        switch (digestAlgorithm) {
            case "SHA-1":
                return "http://www.w3.org/2009/xmlenc11#mgf1sha1";
            case "SHA-224":
                return "http://www.w3.org/2009/xmlenc11#mgf1sha224";
            case "SHA-256":
                return "http://www.w3.org/2009/xmlenc11#mgf1sha256";
            case "SHA-384":
                return "http://www.w3.org/2009/xmlenc11#mgf1sha384";
            case "SHA-512":
                return "http://www.w3.org/2009/xmlenc11#mgf1sha512";
            default:
                LOG.warn("Unknown hash algorithm: [{0}]  for MGF1", digestAlgorithm);
                return "http://www.w3.org/2009/xmlenc11#mgf1sha1";
        }
    }

    public static KeyAgreementParameters constructRecipientKeyAgreementParameters(String keyWrapAlgoURI, AgreementMethod agreementMethod, PrivateKey keyAgreementPrivateKey) throws XMLSecurityException {
        String agreementAlgorithmURI = agreementMethod.getAlgorithm();
        int keyLength = KeyUtils.getAESKeyBitSizeForWrapAlgorithm(keyWrapAlgoURI);
        KeyDerivationMethod keyDerivationMethod = agreementMethod.getKeyDerivationMethod();
        if (keyDerivationMethod == null) {
            throw new XMLEncryptionException("Key Derivation Algorithm is not specified");
        }
        KeyDerivationParameters kdp = constructKeyDerivationParameter(keyDerivationMethod, keyLength);
        return constructAgreementParameters(agreementAlgorithmURI, KeyAgreementParameters.ActorType.RECIPIENT, kdp, keyAgreementPrivateKey, agreementMethod.getOriginatorKeyInfo().getPublicKey());
    }

    public static KeyAgreementParameters constructAgreementParameters(String agreementAlgorithmURI, KeyAgreementParameters.ActorType actorType, KeyDerivationParameters keyDerivationParameter, PrivateKey keyAgreementPrivateKey, PublicKey keyAgreementPublicKey) {
        KeyAgreementParameters ecdhKeyAgreementParameters = new KeyAgreementParameters(actorType, agreementAlgorithmURI, keyDerivationParameter);
        if (actorType == KeyAgreementParameters.ActorType.RECIPIENT) {
            ecdhKeyAgreementParameters.setRecipientPrivateKey(keyAgreementPrivateKey);
            ecdhKeyAgreementParameters.setOriginatorPublicKey(keyAgreementPublicKey);
        } else {
            ecdhKeyAgreementParameters.setOriginatorPrivateKey(keyAgreementPrivateKey);
            ecdhKeyAgreementParameters.setRecipientPublicKey(keyAgreementPublicKey);
        }
        return ecdhKeyAgreementParameters;
    }

    public static KeyDerivationParameters constructKeyDerivationParameter(KeyDerivationMethod keyDerivationMethod, int keyBitLength) throws XMLEncryptionException {
        String keyDerivationAlgorithm = keyDerivationMethod.getAlgorithm();
        try {
            KDFParams kdfParams = keyDerivationMethod.getKDFParams();
            if (EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF.equals(keyDerivationAlgorithm)) {
                if (!(kdfParams instanceof ConcatKDFParamsImpl)) {
                    throw new XMLEncryptionException("KeyDerivation.InvalidParametersType", keyDerivationAlgorithm, ConcatKDFParamsImpl.class.getName());
                }
                ConcatKDFParamsImpl concatKDFParams = (ConcatKDFParamsImpl) kdfParams;
                return ConcatKDFParams.createBuilder(keyBitLength, concatKDFParams.getDigestMethod()).algorithmID(concatKDFParams.getAlgorithmId()).partyUInfo(concatKDFParams.getPartyUInfo()).partyVInfo(concatKDFParams.getPartyVInfo()).suppPubInfo(concatKDFParams.getSuppPubInfo()).suppPrivInfo(concatKDFParams.getSuppPrivInfo()).build();
            }
            if (EncryptionConstants.ALGO_ID_KEYDERIVATION_HKDF.equals(keyDerivationAlgorithm)) {
                if (!(kdfParams instanceof HKDFParamsImpl)) {
                    throw new XMLEncryptionException("KeyDerivation.InvalidParametersType", keyDerivationAlgorithm, HKDFParamsImpl.class.getName());
                }
                HKDFParamsImpl hKDFParams = (HKDFParamsImpl) kdfParams;
                return HKDFParams.createBuilder(keyBitLength, hKDFParams.getPRFAlgorithm()).salt(hKDFParams.getSalt() != null ? Base64.getDecoder().decode(hKDFParams.getSalt()) : null).info(hKDFParams.getInfo() != null ? Base64.getDecoder().decode(hKDFParams.getInfo()) : null).build();
            }
            throw new XMLEncryptionException("unknownAlgorithm", keyDerivationAlgorithm);
        } catch (XMLSecurityException e) {
            throw new XMLEncryptionException(e);
        }
    }

    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null) {
            return null;
        }
        if (hexString.isEmpty()) {
            return new byte[0];
        }
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
