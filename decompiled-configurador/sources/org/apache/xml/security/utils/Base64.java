package org.apache.xml.security.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/Base64.class */
@Deprecated
public final class Base64 {
    public static final int BASE64DEFAULTLENGTH = 76;
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final char PAD = '=';
    private static final byte[] base64Alphabet = new byte[255];
    private static final char[] lookUpBase64Alphabet = new char[64];

    static {
        for (int i = 0; i < 255; i++) {
            base64Alphabet[i] = -1;
        }
        for (int i2 = 90; i2 >= 65; i2--) {
            base64Alphabet[i2] = (byte) (i2 - 65);
        }
        for (int i3 = 122; i3 >= 97; i3--) {
            base64Alphabet[i3] = (byte) ((i3 - 97) + 26);
        }
        for (int i4 = 57; i4 >= 48; i4--) {
            base64Alphabet[i4] = (byte) ((i4 - 48) + 52);
        }
        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;
        for (int i5 = 0; i5 <= 25; i5++) {
            lookUpBase64Alphabet[i5] = (char) (65 + i5);
        }
        int i6 = 26;
        int j = 0;
        while (i6 <= 51) {
            lookUpBase64Alphabet[i6] = (char) (97 + j);
            i6++;
            j++;
        }
        int i7 = 52;
        int j2 = 0;
        while (i7 <= 61) {
            lookUpBase64Alphabet[i7] = (char) (48 + j2);
            i7++;
            j2++;
        }
        lookUpBase64Alphabet[62] = '+';
        lookUpBase64Alphabet[63] = '/';
    }

    private Base64() {
    }

    static byte[] getBytes(BigInteger big, int bitlen) {
        int bitlen2 = ((bitlen + 7) >> 3) << 3;
        if (bitlen2 < big.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        byte[] bigBytes = big.toByteArray();
        if (big.bitLength() % 8 != 0 && (big.bitLength() / 8) + 1 == bitlen2 / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int bigLen = bigBytes.length;
        if (big.bitLength() % 8 == 0) {
            startSrc = 1;
            bigLen--;
        }
        int startDst = (bitlen2 / 8) - bigLen;
        byte[] resizedBytes = new byte[bitlen2 / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);
        return resizedBytes;
    }

    public static String encode(BigInteger big) {
        byte[] bytes = XMLUtils.getBytes(big, big.bitLength());
        return XMLUtils.encodeToString(bytes);
    }

    public static byte[] encode(BigInteger big, int bitlen) {
        int bitlen2 = ((bitlen + 7) >> 3) << 3;
        if (bitlen2 < big.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        byte[] bigBytes = big.toByteArray();
        if (big.bitLength() % 8 != 0 && (big.bitLength() / 8) + 1 == bitlen2 / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int bigLen = bigBytes.length;
        if (big.bitLength() % 8 == 0) {
            startSrc = 1;
            bigLen--;
        }
        int startDst = (bitlen2 / 8) - bigLen;
        byte[] resizedBytes = new byte[bitlen2 / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);
        return resizedBytes;
    }

    public static BigInteger decodeBigIntegerFromElement(Element element) throws Base64DecodingException {
        return new BigInteger(1, decode(element));
    }

    public static BigInteger decodeBigIntegerFromText(Text text) throws Base64DecodingException {
        return new BigInteger(1, decode(text.getData()));
    }

    public static void fillElementWithBigInteger(Element element, BigInteger biginteger) {
        String encodedInt = encode(biginteger);
        if (!XMLUtils.ignoreLineBreaks() && encodedInt.length() > 76) {
            encodedInt = "\n" + encodedInt + "\n";
        }
        Document doc = element.getOwnerDocument();
        Text text = doc.createTextNode(encodedInt);
        element.appendChild(text);
    }

    public static byte[] decode(Element element) throws Base64DecodingException {
        StringBuilder sb = new StringBuilder();
        for (Node sibling = element.getFirstChild(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 3) {
                Text t = (Text) sibling;
                sb.append(t.getData());
            }
        }
        return decode(sb.toString());
    }

    public static Element encodeToElement(Document doc, String localName, byte[] bytes) {
        Element el = XMLUtils.createElementInSignatureSpace(doc, localName);
        Text text = doc.createTextNode(encode(bytes));
        el.appendChild(text);
        return el;
    }

    public static byte[] decode(byte[] base64) throws Base64DecodingException {
        return decodeInternal(base64, -1);
    }

    public static String encode(byte[] binaryData) {
        if (XMLUtils.ignoreLineBreaks()) {
            return encode(binaryData, Integer.MAX_VALUE);
        }
        return encode(binaryData, 76);
    }

    public static byte[] decode(BufferedReader reader) throws Base64DecodingException, IOException {
        UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();
        while (true) {
            try {
                String line = reader.readLine();
                if (null != line) {
                    byte[] bytes = decode(line);
                    baos.write(bytes);
                } else {
                    byte[] retBytes = baos.toByteArray();
                    baos.close();
                    return retBytes;
                }
            } catch (Throwable th) {
                baos.close();
                throw th;
            }
        }
    }

    protected static boolean isWhiteSpace(byte octet) {
        return octet == 32 || octet == 13 || octet == 10 || octet == 9;
    }

    protected static boolean isPad(byte octet) {
        return octet == 61;
    }

    public static String encode(byte[] binaryData, int length) {
        if (length < 4) {
            length = Integer.MAX_VALUE;
        }
        if (binaryData == null) {
            return null;
        }
        long lengthDataBits = ((long) binaryData.length) * 8;
        if (lengthDataBits == 0) {
            return "";
        }
        long fewerThan24bits = lengthDataBits % 24;
        int numberTriplets = (int) (lengthDataBits / 24);
        int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
        int quartesPerLine = length / 4;
        int numberLines = (numberQuartet - 1) / quartesPerLine;
        char[] encodedData = new char[(numberQuartet * 4) + (numberLines * 2)];
        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                int i2 = dataIndex;
                int dataIndex2 = dataIndex + 1;
                byte b1 = binaryData[i2];
                int dataIndex3 = dataIndex2 + 1;
                byte b2 = binaryData[dataIndex2];
                dataIndex = dataIndex3 + 1;
                byte b3 = binaryData[dataIndex3];
                byte l = (byte) (b2 & 15);
                byte k = (byte) (b1 & 3);
                byte val1 = (b1 & (-128)) == 0 ? (byte) (b1 >> 2) : (byte) ((b1 >> 2) ^ 192);
                byte val2 = (b2 & (-128)) == 0 ? (byte) (b2 >> 4) : (byte) ((b2 >> 4) ^ 240);
                byte val3 = (byte) ((b3 & (-128)) == 0 ? b3 >> 6 : (b3 >> 6) ^ 252);
                int i3 = encodedIndex;
                int encodedIndex2 = encodedIndex + 1;
                encodedData[i3] = lookUpBase64Alphabet[val1];
                int encodedIndex3 = encodedIndex2 + 1;
                encodedData[encodedIndex2] = lookUpBase64Alphabet[val2 | (k << 4)];
                int encodedIndex4 = encodedIndex3 + 1;
                encodedData[encodedIndex3] = lookUpBase64Alphabet[(l << 2) | val3];
                encodedIndex = encodedIndex4 + 1;
                encodedData[encodedIndex4] = lookUpBase64Alphabet[b3 & 63];
                i++;
            }
            int i4 = encodedIndex;
            int encodedIndex5 = encodedIndex + 1;
            encodedData[i4] = '\r';
            encodedIndex = encodedIndex5 + 1;
            encodedData[encodedIndex5] = '\n';
        }
        while (i < numberTriplets) {
            int i5 = dataIndex;
            int dataIndex4 = dataIndex + 1;
            byte b4 = binaryData[i5];
            int dataIndex5 = dataIndex4 + 1;
            byte b5 = binaryData[dataIndex4];
            dataIndex = dataIndex5 + 1;
            byte b6 = binaryData[dataIndex5];
            byte l2 = (byte) (b5 & 15);
            byte k2 = (byte) (b4 & 3);
            byte val4 = (b4 & (-128)) == 0 ? (byte) (b4 >> 2) : (byte) ((b4 >> 2) ^ 192);
            byte val5 = (b5 & (-128)) == 0 ? (byte) (b5 >> 4) : (byte) ((b5 >> 4) ^ 240);
            byte val6 = (byte) ((b6 & (-128)) == 0 ? b6 >> 6 : (b6 >> 6) ^ 252);
            int i6 = encodedIndex;
            int encodedIndex6 = encodedIndex + 1;
            encodedData[i6] = lookUpBase64Alphabet[val4];
            int encodedIndex7 = encodedIndex6 + 1;
            encodedData[encodedIndex6] = lookUpBase64Alphabet[val5 | (k2 << 4)];
            int encodedIndex8 = encodedIndex7 + 1;
            encodedData[encodedIndex7] = lookUpBase64Alphabet[(l2 << 2) | val6];
            encodedIndex = encodedIndex8 + 1;
            encodedData[encodedIndex8] = lookUpBase64Alphabet[b6 & 63];
            i++;
        }
        if (fewerThan24bits == 8) {
            byte b7 = binaryData[dataIndex];
            byte k3 = (byte) (b7 & 3);
            byte val7 = (b7 & (-128)) == 0 ? (byte) (b7 >> 2) : (byte) ((b7 >> 2) ^ 192);
            int i7 = encodedIndex;
            int encodedIndex9 = encodedIndex + 1;
            encodedData[i7] = lookUpBase64Alphabet[val7];
            int encodedIndex10 = encodedIndex9 + 1;
            encodedData[encodedIndex9] = lookUpBase64Alphabet[k3 << 4];
            int encodedIndex11 = encodedIndex10 + 1;
            encodedData[encodedIndex10] = '=';
            int i8 = encodedIndex11 + 1;
            encodedData[encodedIndex11] = '=';
        } else if (fewerThan24bits == 16) {
            byte b8 = binaryData[dataIndex];
            byte b9 = binaryData[dataIndex + 1];
            byte l3 = (byte) (b9 & 15);
            byte k4 = (byte) (b8 & 3);
            byte val8 = (b8 & (-128)) == 0 ? (byte) (b8 >> 2) : (byte) ((b8 >> 2) ^ 192);
            byte val9 = (b9 & (-128)) == 0 ? (byte) (b9 >> 4) : (byte) ((b9 >> 4) ^ 240);
            int i9 = encodedIndex;
            int encodedIndex12 = encodedIndex + 1;
            encodedData[i9] = lookUpBase64Alphabet[val8];
            int encodedIndex13 = encodedIndex12 + 1;
            encodedData[encodedIndex12] = lookUpBase64Alphabet[val9 | (k4 << 4)];
            int encodedIndex14 = encodedIndex13 + 1;
            encodedData[encodedIndex13] = lookUpBase64Alphabet[l3 << 2];
            int i10 = encodedIndex14 + 1;
            encodedData[encodedIndex14] = '=';
        }
        return new String(encodedData);
    }

    public static byte[] decode(String encoded) throws Base64DecodingException {
        if (encoded == null) {
            return null;
        }
        byte[] bytes = new byte[encoded.length()];
        int len = getBytesInternal(encoded, bytes);
        return decodeInternal(bytes, len);
    }

    protected static int getBytesInternal(String s, byte[] result) {
        int length = s.length();
        int newSize = 0;
        for (int i = 0; i < length; i++) {
            byte dataS = (byte) s.charAt(i);
            if (!isWhiteSpace(dataS)) {
                int i2 = newSize;
                newSize++;
                result[i2] = dataS;
            }
        }
        return newSize;
    }

    protected static byte[] decodeInternal(byte[] base64Data, int len) throws Base64DecodingException {
        byte[] decodedData;
        if (len == -1) {
            len = removeWhiteSpace(base64Data);
        }
        if (len % 4 != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
        }
        int numberQuadruple = len / 4;
        if (numberQuadruple == 0) {
            return new byte[0];
        }
        int dataIndex = (numberQuadruple - 1) * 4;
        int encodedIndex = (numberQuadruple - 1) * 3;
        int dataIndex2 = dataIndex + 1;
        byte b1 = base64Alphabet[base64Data[dataIndex]];
        int dataIndex3 = dataIndex2 + 1;
        byte b2 = base64Alphabet[base64Data[dataIndex2]];
        if (b1 == -1 || b2 == -1) {
            throw new Base64DecodingException("decoding.general");
        }
        byte[] bArr = base64Alphabet;
        int dataIndex4 = dataIndex3 + 1;
        byte d3 = base64Data[dataIndex3];
        byte b3 = bArr[d3];
        byte[] bArr2 = base64Alphabet;
        int i = dataIndex4 + 1;
        byte d4 = base64Data[dataIndex4];
        byte b4 = bArr2[d4];
        if (b3 == -1 || b4 == -1) {
            if (isPad(d3) && isPad(d4)) {
                if ((b2 & 15) != 0) {
                    throw new Base64DecodingException("decoding.general");
                }
                decodedData = new byte[encodedIndex + 1];
                decodedData[encodedIndex] = (byte) ((b1 << 2) | (b2 >> 4));
            } else if (!isPad(d3) && isPad(d4)) {
                if ((b3 & 3) != 0) {
                    throw new Base64DecodingException("decoding.general");
                }
                decodedData = new byte[encodedIndex + 2];
                decodedData[encodedIndex] = (byte) ((b1 << 2) | (b2 >> 4));
                decodedData[encodedIndex + 1] = (byte) (((b2 & 15) << 4) | ((b3 >> 2) & 15));
            } else {
                throw new Base64DecodingException("decoding.general");
            }
        } else {
            decodedData = new byte[encodedIndex + 3];
            int encodedIndex2 = encodedIndex + 1;
            decodedData[encodedIndex] = (byte) ((b1 << 2) | (b2 >> 4));
            int encodedIndex3 = encodedIndex2 + 1;
            decodedData[encodedIndex2] = (byte) (((b2 & 15) << 4) | ((b3 >> 2) & 15));
            int i2 = encodedIndex3 + 1;
            decodedData[encodedIndex3] = (byte) ((b3 << 6) | b4);
        }
        int encodedIndex4 = 0;
        int dataIndex5 = 0;
        for (int i3 = numberQuadruple - 1; i3 > 0; i3--) {
            int i4 = dataIndex5;
            int dataIndex6 = dataIndex5 + 1;
            byte b5 = base64Alphabet[base64Data[i4]];
            int dataIndex7 = dataIndex6 + 1;
            byte b6 = base64Alphabet[base64Data[dataIndex6]];
            int dataIndex8 = dataIndex7 + 1;
            byte b7 = base64Alphabet[base64Data[dataIndex7]];
            dataIndex5 = dataIndex8 + 1;
            byte b8 = base64Alphabet[base64Data[dataIndex8]];
            if (b5 == -1 || b6 == -1 || b7 == -1 || b8 == -1) {
                throw new Base64DecodingException("decoding.general");
            }
            int i5 = encodedIndex4;
            int encodedIndex5 = encodedIndex4 + 1;
            decodedData[i5] = (byte) ((b5 << 2) | (b6 >> 4));
            int encodedIndex6 = encodedIndex5 + 1;
            decodedData[encodedIndex5] = (byte) (((b6 & 15) << 4) | ((b7 >> 2) & 15));
            encodedIndex4 = encodedIndex6 + 1;
            decodedData[encodedIndex6] = (byte) ((b7 << 6) | b8);
        }
        return decodedData;
    }

    public static void decode(String base64Data, OutputStream os) throws Base64DecodingException, IOException {
        byte[] bytes = new byte[base64Data.length()];
        int len = getBytesInternal(base64Data, bytes);
        decode(bytes, os, len);
    }

    public static void decode(byte[] base64Data, OutputStream os) throws Base64DecodingException, IOException {
        decode(base64Data, os, -1);
    }

    protected static void decode(byte[] base64Data, OutputStream os, int len) throws Base64DecodingException, IOException {
        if (len == -1) {
            len = removeWhiteSpace(base64Data);
        }
        if (len % 4 != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
        }
        int numberQuadruple = len / 4;
        if (numberQuadruple == 0) {
            return;
        }
        int dataIndex = 0;
        for (int i = numberQuadruple - 1; i > 0; i--) {
            int i2 = dataIndex;
            int dataIndex2 = dataIndex + 1;
            byte b1 = base64Alphabet[base64Data[i2]];
            int dataIndex3 = dataIndex2 + 1;
            byte b2 = base64Alphabet[base64Data[dataIndex2]];
            int dataIndex4 = dataIndex3 + 1;
            byte b3 = base64Alphabet[base64Data[dataIndex3]];
            dataIndex = dataIndex4 + 1;
            byte b4 = base64Alphabet[base64Data[dataIndex4]];
            if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
                throw new Base64DecodingException("decoding.general");
            }
            os.write((byte) ((b1 << 2) | (b2 >> 4)));
            os.write((byte) (((b2 & 15) << 4) | ((b3 >> 2) & 15)));
            os.write((byte) ((b3 << 6) | b4));
        }
        int i3 = dataIndex;
        int dataIndex5 = dataIndex + 1;
        byte b5 = base64Alphabet[base64Data[i3]];
        int dataIndex6 = dataIndex5 + 1;
        byte b6 = base64Alphabet[base64Data[dataIndex5]];
        if (b5 == -1 || b6 == -1) {
            throw new Base64DecodingException("decoding.general");
        }
        byte[] bArr = base64Alphabet;
        int dataIndex7 = dataIndex6 + 1;
        byte d3 = base64Data[dataIndex6];
        byte b7 = bArr[d3];
        byte[] bArr2 = base64Alphabet;
        int i4 = dataIndex7 + 1;
        byte d4 = base64Data[dataIndex7];
        byte b8 = bArr2[d4];
        if (b7 == -1 || b8 == -1) {
            if (isPad(d3) && isPad(d4)) {
                if ((b6 & 15) != 0) {
                    throw new Base64DecodingException("decoding.general");
                }
                os.write((byte) ((b5 << 2) | (b6 >> 4)));
                return;
            } else {
                if (!isPad(d3) && isPad(d4)) {
                    if ((b7 & 3) != 0) {
                        throw new Base64DecodingException("decoding.general");
                    }
                    os.write((byte) ((b5 << 2) | (b6 >> 4)));
                    os.write((byte) (((b6 & 15) << 4) | ((b7 >> 2) & 15)));
                    return;
                }
                throw new Base64DecodingException("decoding.general");
            }
        }
        os.write((byte) ((b5 << 2) | (b6 >> 4)));
        os.write((byte) (((b6 & 15) << 4) | ((b7 >> 2) & 15)));
        os.write((byte) ((b7 << 6) | b8));
    }

    public static void decode(InputStream is, OutputStream os) throws Base64DecodingException, IOException {
        int index = 0;
        byte[] data = new byte[4];
        while (true) {
            int read = is.read();
            if (read <= 0) {
                break;
            }
            byte readed = (byte) read;
            if (!isWhiteSpace(readed)) {
                if (isPad(readed)) {
                    int i = index;
                    int index2 = index + 1;
                    data[i] = readed;
                    if (index2 != 3) {
                        break;
                    }
                    int i2 = index2 + 1;
                    data[index2] = (byte) is.read();
                    break;
                }
                int i3 = index;
                index++;
                data[i3] = readed;
                if (readed == -1) {
                    throw new Base64DecodingException("decoding.general");
                }
                if (index == 4) {
                    index = 0;
                    byte b1 = base64Alphabet[data[0]];
                    byte b2 = base64Alphabet[data[1]];
                    byte b3 = base64Alphabet[data[2]];
                    byte b4 = base64Alphabet[data[3]];
                    os.write((byte) ((b1 << 2) | (b2 >> 4)));
                    os.write((byte) (((b2 & 15) << 4) | ((b3 >> 2) & 15)));
                    os.write((byte) ((b3 << 6) | b4));
                }
            }
        }
        byte d1 = data[0];
        byte d2 = data[1];
        byte d3 = data[2];
        byte d4 = data[3];
        byte b5 = base64Alphabet[d1];
        byte b6 = base64Alphabet[d2];
        byte b7 = base64Alphabet[d3];
        byte b8 = base64Alphabet[d4];
        if (b7 == -1 || b8 == -1) {
            if (isPad(d3) && isPad(d4)) {
                if ((b6 & 15) != 0) {
                    throw new Base64DecodingException("decoding.general");
                }
                os.write((byte) ((b5 << 2) | (b6 >> 4)));
                return;
            } else {
                if (!isPad(d3) && isPad(d4)) {
                    byte b9 = base64Alphabet[d3];
                    if ((b9 & 3) != 0) {
                        throw new Base64DecodingException("decoding.general");
                    }
                    os.write((byte) ((b5 << 2) | (b6 >> 4)));
                    os.write((byte) (((b6 & 15) << 4) | ((b9 >> 2) & 15)));
                    return;
                }
                throw new Base64DecodingException("decoding.general");
            }
        }
        os.write((byte) ((b5 << 2) | (b6 >> 4)));
        os.write((byte) (((b6 & 15) << 4) | ((b7 >> 2) & 15)));
        os.write((byte) ((b7 << 6) | b8));
    }

    protected static int removeWhiteSpace(byte[] data) {
        if (data == null) {
            return 0;
        }
        int newSize = 0;
        for (byte dataS : data) {
            if (!isWhiteSpace(dataS)) {
                int i = newSize;
                newSize++;
                data[i] = dataS;
            }
        }
        return newSize;
    }
}
