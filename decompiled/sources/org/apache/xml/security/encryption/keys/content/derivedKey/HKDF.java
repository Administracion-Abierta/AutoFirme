package org.apache.xml.security.encryption.keys.content.derivedKey;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.params.HKDFParams;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/content/derivedKey/HKDF.class */
public class HKDF implements DerivationAlgorithm<HKDFParams> {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HKDF.class);

    @Override // org.apache.xml.security.encryption.keys.content.derivedKey.DerivationAlgorithm
    public byte[] deriveKey(byte[] secret, HKDFParams params) throws XMLSecurityException {
        if (params == null) {
            throw new IllegalArgumentException(I18n.translate("KeyDerivation.MissingParameters"));
        }
        String jceAlgorithmName = JCEMapper.translateURItoJCEID(params.getHmacHashAlgorithm());
        if (jceAlgorithmName == null) {
            throw new XMLSecurityException("KeyDerivation.NotSupportedParameter", new Object[]{params.getHmacHashAlgorithm()});
        }
        byte[] prk = extractKey(jceAlgorithmName, params.getSalt(), secret);
        return expandKey(jceAlgorithmName, prk, params.getInfo(), params.getKeyLength());
    }

    public byte[] extractKey(String jceAlgorithmName, byte[] salt, byte[] secret) throws XMLSecurityException {
        Mac hMac = initHMac(jceAlgorithmName, salt, true);
        hMac.reset();
        return hMac.doFinal(secret);
    }

    public byte[] expandKey(String jceHmacAlgorithmName, byte[] prk, byte[] info, long keyLength) throws XMLSecurityException {
        Mac hMac = initHMac(jceHmacAlgorithmName, prk, false);
        int iMacLength = hMac.getMacLength();
        int toGenerateSize = (int) keyLength;
        ByteBuffer result = ByteBuffer.allocate(toGenerateSize);
        byte[] prevResult = new byte[0];
        short s = 1;
        while (true) {
            short counter = s;
            if (toGenerateSize > 0) {
                hMac.reset();
                hMac.update(prevResult);
                if (info != null && info.length > 0) {
                    hMac.update(info);
                }
                hMac.update((byte) counter);
                prevResult = hMac.doFinal();
                result.put(prevResult, 0, Math.min(toGenerateSize, iMacLength));
                toGenerateSize -= iMacLength;
                s = (short) (counter + 1);
            } else {
                return result.array();
            }
        }
    }

    private Mac initHMac(String jceAlgorithmName, byte[] initSecret, boolean initPRK) throws XMLSecurityException {
        try {
            LOG.debug("Init Mac with hash algorithm: [{}]", jceAlgorithmName);
            Mac mac = Mac.getInstance(jceAlgorithmName);
            if (initPRK && (initSecret == null || initSecret.length == 0)) {
                LOG.debug("Init Mac with hmac algorithm [{}] and empty salt!", jceAlgorithmName);
                initSecret = new byte[mac.getMacLength()];
            }
            SecretKeySpec secretKey = new SecretKeySpec(initSecret, jceAlgorithmName);
            try {
                mac.init(secretKey);
                return mac;
            } catch (InvalidKeyException e) {
                throw new XMLSecurityException(e);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new XMLSecurityException(e2, "KeyDerivation.NotSupportedParameter", new Object[]{jceAlgorithmName});
        }
    }
}
