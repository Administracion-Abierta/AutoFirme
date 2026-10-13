package org.apache.xml.security.encryption.keys.content.derivedKey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.encryption.XMLCipherUtil;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.encryption.params.ConcatKDFParams;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/content/derivedKey/ConcatKDF.class */
public class ConcatKDF implements DerivationAlgorithm<ConcatKDFParams> {
    private static final Logger LOG = LoggerFactory.getLogger(ConcatKDF.class.getName());

    @Override // org.apache.xml.security.encryption.keys.content.derivedKey.DerivationAlgorithm
    public byte[] deriveKey(byte[] sharedSecret, ConcatKDFParams concatKDFParams) throws XMLSecurityException {
        if (concatKDFParams == null) {
            throw new IllegalArgumentException(I18n.translate("KeyDerivation.MissingParameters"));
        }
        byte[] otherInfo = concatParameters(concatKDFParams.getAlgorithmID(), concatKDFParams.getPartyUInfo(), concatKDFParams.getPartyVInfo(), concatKDFParams.getSuppPubInfo(), concatKDFParams.getSuppPrivInfo());
        MessageDigest digest = MessageDigestAlgorithm.getDigestInstance(concatKDFParams.getDigestAlgorithm());
        int genKeyLength = concatKDFParams.getKeyLength();
        int iDigestLength = digest.getDigestLength();
        if (((long) genKeyLength) / ((long) iDigestLength) > 2147483647L) {
            throw new XMLSecurityException("KeyDerivation.InvalidParameter", new Object[]{"key length"});
        }
        int toGenerateSize = genKeyLength;
        digest.reset();
        ByteBuffer indexBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        ByteBuffer result = ByteBuffer.allocate(toGenerateSize);
        int counter = 1;
        while (toGenerateSize > 0) {
            indexBuffer.position(0);
            int i = counter;
            counter++;
            indexBuffer.putInt(i);
            indexBuffer.position(0);
            digest.update(indexBuffer);
            digest.update(sharedSecret);
            if (otherInfo != null && otherInfo.length > 0) {
                digest.update(otherInfo);
            }
            result.put(digest.digest(), 0, Math.min(toGenerateSize, iDigestLength));
            toGenerateSize -= iDigestLength;
        }
        return result.array();
    }

    private static byte[] concatParameters(String... parameters) throws XMLEncryptionException {
        List<byte[]> byteParams = new ArrayList<>();
        for (String parameter : parameters) {
            byte[] bytes = parseBitString(parameter);
            byteParams.add(bytes);
        }
        int iSize = ((Integer) byteParams.stream().map(ConcatKDF::getSize).reduce(0, (v0, v1) -> {
            return Integer.sum(v0, v1);
        })).intValue();
        ByteBuffer buffer = ByteBuffer.allocate(iSize);
        Objects.requireNonNull(buffer);
        byteParams.forEach(buffer::put);
        return buffer.array();
    }

    private static byte[] parseBitString(String kdfParameter) throws XMLEncryptionException {
        if (kdfParameter == null || kdfParameter.isEmpty()) {
            return new byte[0];
        }
        String kdfP = kdfParameter.trim();
        int paramLen = kdfP.length();
        if (paramLen < 4) {
            LOG.error("ConcatKDF parameter is to short");
            throw new XMLEncryptionException("KeyDerivation.TooShortParameter", kdfParameter);
        }
        if (paramLen % 2 != 0) {
            LOG.error("Invalid length of ConcatKDF parameter [{0}]!", kdfP);
            throw new XMLEncryptionException("KeyDerivation.InvalidParameter", kdfParameter);
        }
        String strPadding = kdfP.substring(0, 2);
        try {
            int iPadding = Integer.parseInt(strPadding, 16);
            if (iPadding != 0) {
                LOG.error("Padded ConcatKDF parameters are not supported");
                throw new XMLEncryptionException("KeyDerivation.NotSupportedParameter", kdfParameter);
            }
            return XMLCipherUtil.hexStringToByteArray(kdfP.substring(2));
        } catch (NumberFormatException e) {
            LOG.error("Invalid padding number: [{0}]! Number is not Hexadecimal!", strPadding);
            throw new XMLEncryptionException(e, "KeyDerivation.InvalidParameter", new Object[]{kdfParameter});
        }
    }

    private static int getSize(byte[] array) {
        if (array == null) {
            return 0;
        }
        return array.length;
    }
}
