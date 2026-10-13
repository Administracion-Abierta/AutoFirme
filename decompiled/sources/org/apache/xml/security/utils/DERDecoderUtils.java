package org.apache.xml.security.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import org.apache.xml.security.exceptions.DERDecodingException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/DERDecoderUtils.class */
public class DERDecoderUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DERDecoderUtils.class.getName());
    public static final byte TYPE_BIT_STRING = 3;
    public static final byte TYPE_OCTET_STRING = 4;
    public static final byte TYPE_SEQUENCE = 48;
    public static final byte TYPE_OBJECT_IDENTIFIER = 6;

    public static byte[] getAlgorithmIdBytes(InputStream derEncodedIS) throws IOException, DERDecodingException {
        if (derEncodedIS == null || derEncodedIS.available() <= 0) {
            throw new DERDecodingException("DER decoding error: Null data");
        }
        validateType(derEncodedIS.read(), (byte) 48);
        readLength(derEncodedIS);
        validateType(derEncodedIS.read(), (byte) 48);
        readLength(derEncodedIS);
        return readObjectIdentifier(derEncodedIS);
    }

    public static byte[] readObjectIdentifier(InputStream derEncodedIS) throws DERDecodingException {
        try {
            validateType(derEncodedIS.read(), (byte) 6);
            int length = readLength(derEncodedIS);
            LOG.debug("DER decoding algorithm id bytes");
            byte[] oidBytes = new byte[length];
            derEncodedIS.read(oidBytes);
            return oidBytes;
        } catch (IOException ex) {
            throw new DERDecodingException("Error occurred while reading the input stream.", ex);
        }
    }

    public static String getAlgorithmIdFromPublicKey(PublicKey publicKey) throws DERDecodingException {
        String keyFormat = publicKey.getFormat();
        if (!XMLX509Certificate.JCA_CERT_ID.equalsIgnoreCase(keyFormat) && !"X509".equalsIgnoreCase(keyFormat)) {
            throw new DERDecodingException("Unknown key format [" + keyFormat + "]! Support for X.509-encoded public keys only!");
        }
        try {
            InputStream inputStream = new ByteArrayInputStream(publicKey.getEncoded());
            Throwable th = null;
            try {
                try {
                    byte[] keyAlgOidBytes = getAlgorithmIdBytes(inputStream);
                    String alg = decodeOID(keyAlgOidBytes);
                    if (alg.equals(KeyUtils.KeyAlgorithmType.EC.getOid())) {
                        byte[] keyAlgOidBytes2 = readObjectIdentifier(inputStream);
                        alg = decodeOID(keyAlgOidBytes2);
                    }
                    String str = alg;
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        inputStream.close();
                    }
                    return str;
                } catch (Throwable th3) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        inputStream.close();
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (IOException ex) {
            throw new DERDecodingException("Error reading public key", ex);
        }
    }

    private static void validateType(int iType, byte expectedType) throws DERDecodingException {
        validateType((byte) (iType & 255), expectedType);
    }

    private static void validateType(byte type, byte expectedType) throws DERDecodingException {
        if (type != expectedType) {
            throw new DERDecodingException("DER decoding error: Expected type [" + ((int) expectedType) + "] but got [" + ((int) type) + "]");
        }
    }

    public static int readLength(InputStream derEncodedIs) throws IOException, DERDecodingException {
        if (derEncodedIs.available() <= 0) {
            throw new DERDecodingException("Invalid DER format");
        }
        int value = derEncodedIs.read();
        if ((value & 128) == 0) {
            return value;
        }
        int byteCount = value & 127;
        if (byteCount == 0) {
            return -1;
        }
        if (byteCount > 4) {
            throw new DERDecodingException("Data length byte size: [" + byteCount + "] is incorrect/too big");
        }
        byte[] intSizeBytes = new byte[byteCount];
        derEncodedIs.read(intSizeBytes);
        return new BigInteger(1, intSizeBytes).intValue();
    }

    public static String decodeOID(byte[] oidBytes) {
        int length = oidBytes.length;
        StringBuilder sb = new StringBuilder(length * 4);
        int fromPos = 0;
        for (int i = 0; i < length; i++) {
            if ((oidBytes[i] & 128) == 0) {
                long decodedValue = decodeBytes(oidBytes, fromPos, (i - fromPos) + 1);
                if (fromPos == 0) {
                    if (decodedValue < 80) {
                        sb.append(decodedValue / 40);
                        decodedValue %= 40;
                    } else {
                        sb.append('2');
                        decodedValue -= 80;
                    }
                }
                sb.append('.');
                sb.append(decodedValue);
                fromPos = i + 1;
            }
        }
        return sb.toString();
    }

    private static long decodeBytes(byte[] inBytes, int iOffset, int iLength) {
        if (iLength > 8) {
            throw new IllegalArgumentException("OID segment too long to parse: [" + iLength + "]");
        }
        if (iLength > 1) {
            int iSteps = iLength - 1;
            return (((long) (inBytes[iOffset] & 127)) << (7 * iSteps)) + decodeBytes(inBytes, iOffset + 1, iSteps);
        }
        return inBytes[iOffset] & 127;
    }
}
