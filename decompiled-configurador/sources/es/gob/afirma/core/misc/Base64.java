package es.gob.afirma.core.misc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/Base64.class */
public final class Base64 {
    private static final int NO_OPTIONS = 0;
    private static final int DO_BREAK_LINES = 8;
    private static final int URL_SAFE = 16;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final Charset PREFERRED_ENCODING;
    private static final byte WHITE_SPACE_ENC = -9;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] STANDARD_ALPHABET;
    private static final byte[] STANDARD_DECODABET;
    private static final byte[] URL_SAFE_ALPHABET;
    private static final byte[] URL_SAFE_DECODABET;
    private static final String BASE_64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz=_-\t\n+/0123456789\r~";
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Base64.class.desiredAssertionStatus();
        PREFERRED_ENCODING = StandardCharsets.US_ASCII;
        STANDARD_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        STANDARD_DECODABET = new byte[]{WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 62, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -1, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC};
        URL_SAFE_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        URL_SAFE_DECODABET = new byte[]{WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -5, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 62, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -1, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, 63, WHITE_SPACE_ENC, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC, WHITE_SPACE_ENC};
    }

    private static byte[] getAlphabet(int i) {
        if ((i & 16) == 16) {
            return URL_SAFE_ALPHABET;
        }
        return STANDARD_ALPHABET;
    }

    private static byte[] getDecodabet(int i) {
        if ((i & 16) == 16) {
            return URL_SAFE_DECODABET;
        }
        return STANDARD_DECODABET;
    }

    private Base64() {
    }

    private static byte[] encode3to4(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) {
        byte[] alphabet = getAlphabet(i4);
        int i5 = (i2 > 0 ? (bArr[i] << 24) >>> 8 : 0) | (i2 > 1 ? (bArr[i + 1] << 24) >>> 16 : 0) | (i2 > 2 ? (bArr[i + 2] << 24) >>> 24 : 0);
        switch (i2) {
            case 1:
                bArr2[i3] = alphabet[i5 >>> 18];
                bArr2[i3 + 1] = alphabet[(i5 >>> 12) & 63];
                bArr2[i3 + 2] = 61;
                bArr2[i3 + 3] = 61;
                break;
            case 2:
                bArr2[i3] = alphabet[i5 >>> 18];
                bArr2[i3 + 1] = alphabet[(i5 >>> 12) & 63];
                bArr2[i3 + 2] = alphabet[(i5 >>> 6) & 63];
                bArr2[i3 + 3] = 61;
                break;
            case 3:
                bArr2[i3] = alphabet[i5 >>> 18];
                bArr2[i3 + 1] = alphabet[(i5 >>> 12) & 63];
                bArr2[i3 + 2] = alphabet[(i5 >>> 6) & 63];
                bArr2[i3 + 3] = alphabet[i5 & 63];
                break;
            default:
                break;
        }
        return bArr2;
    }

    public static String encode(byte[] bArr) {
        String strEncodeBytes = encodeBytes(bArr, 0, bArr.length, 0);
        if ($assertionsDisabled || strEncodeBytes != null) {
            return strEncodeBytes;
        }
        throw new AssertionError();
    }

    public static String encode(byte[] bArr, boolean z) {
        return encodeBytes(bArr, 0, bArr.length, z ? 16 : 0);
    }

    private static String encodeBytes(byte[] bArr, int i, int i2, int i3) {
        return new String(encodeBytesToBytes(bArr, i, i2, i3), PREFERRED_ENCODING);
    }

    private static byte[] encodeBytesToBytes(byte[] bArr, int i, int i2, int i3) {
        if (bArr == null) {
            throw new IllegalArgumentException("Cannot serialize a null array");
        }
        if (i < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + i);
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + i2);
        }
        if (i + i2 > bArr.length) {
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(bArr.length)));
        }
        boolean z = (i3 & 8) != 0;
        int i4 = ((i2 / 3) * 4) + (i2 % 3 > 0 ? 4 : 0);
        if (z) {
            i4 += i4 / 76;
        }
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        int i6 = 0;
        int i7 = i2 - 2;
        int i8 = 0;
        while (i5 < i7) {
            encode3to4(bArr, i5 + i, 3, bArr2, i6, i3);
            i8 += 4;
            if (z && i8 >= 76) {
                bArr2[i6 + 4] = 10;
                i6++;
                i8 = 0;
            }
            i5 += 3;
            i6 += 4;
        }
        if (i5 < i2) {
            encode3to4(bArr, i5 + i, i2 - i5, bArr2, i6, i3);
            i6 += 4;
        }
        if (i6 <= bArr2.length - 1) {
            byte[] bArr3 = new byte[i6];
            System.arraycopy(bArr2, 0, bArr3, 0, i6);
            return bArr3;
        }
        return bArr2;
    }

    private static int decode4to3(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        if (bArr == null) {
            throw new IllegalArgumentException("Source array was null");
        }
        if (bArr2 == null) {
            throw new IllegalArgumentException("Destination array was null");
        }
        if (i < 0 || i + 3 >= bArr.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes", Integer.valueOf(bArr.length), Integer.valueOf(i)));
        }
        if (i2 < 0 || i2 + 2 >= bArr2.length) {
            throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", Integer.valueOf(bArr2.length), Integer.valueOf(i2)));
        }
        byte[] decodabet = getDecodabet(i3);
        if (bArr[i + 2] == 61) {
            bArr2[i2] = (byte) ((((decodabet[bArr[i]] & 255) << 18) | ((decodabet[bArr[i + 1]] & 255) << 12)) >>> 16);
            return 1;
        }
        if (bArr[i + 3] == 61) {
            int i4 = ((decodabet[bArr[i]] & 255) << 18) | ((decodabet[bArr[i + 1]] & 255) << 12) | ((decodabet[bArr[i + 2]] & 255) << 6);
            bArr2[i2] = (byte) (i4 >>> 16);
            bArr2[i2 + 1] = (byte) (i4 >>> 8);
            return 2;
        }
        int i5 = ((decodabet[bArr[i]] & 255) << 18) | ((decodabet[bArr[i + 1]] & 255) << 12) | ((decodabet[bArr[i + 2]] & 255) << 6) | (decodabet[bArr[i + 3]] & 255);
        bArr2[i2] = (byte) (i5 >> 16);
        bArr2[i2 + 1] = (byte) (i5 >> 8);
        bArr2[i2 + 2] = (byte) i5;
        return 3;
    }

    public static byte[] decode(byte[] bArr, int i, int i2, boolean z) throws IOException {
        int i3 = 0;
        if (z) {
            i3 = 16;
        }
        if (bArr == null) {
            throw new IllegalArgumentException("Cannot decode null source array");
        }
        if (i < 0 || i + i2 > bArr.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
        }
        if (i2 == 0) {
            return new byte[0];
        }
        if (i2 < 4) {
            throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + i2);
        }
        byte[] decodabet = getDecodabet(i3);
        byte[] bArr2 = new byte[(i2 * 3) / 4];
        int iDecode4to3 = 0;
        byte[] bArr3 = new byte[4];
        int i4 = 0;
        for (int i5 = i; i5 < i + i2; i5++) {
            byte b = decodabet[bArr[i5] & 255];
            if (b >= WHITE_SPACE_ENC) {
                if (b >= -1) {
                    int i6 = i4;
                    i4++;
                    bArr3[i6] = bArr[i5];
                    if (i4 > 3) {
                        iDecode4to3 += decode4to3(bArr3, 0, bArr2, iDecode4to3, i3);
                        i4 = 0;
                        if (bArr[i5] == 61) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", Integer.valueOf(bArr[i5] & 255), Integer.valueOf(i5)));
            }
        }
        byte[] bArr4 = new byte[iDecode4to3];
        System.arraycopy(bArr2, 0, bArr4, 0, iDecode4to3);
        return bArr4;
    }

    public static byte[] decode(String str) throws IOException {
        return decode(str, false);
    }

    public static byte[] decode(String str, boolean z) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("Input string was null");
        }
        byte[] bytes = str.getBytes(PREFERRED_ENCODING);
        return decode(bytes, 0, bytes.length, z);
    }

    public static boolean isBase64(byte[] bArr) {
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            char c = (char) bArr[i2];
            if (BASE_64_ALPHABET.indexOf(c) == -1) {
                return false;
            }
            if (c == '=' && i2 < bArr.length - 2) {
                return false;
            }
            if (c != '\n' && c != '\r') {
                i++;
            }
        }
        return i % 4 == 0;
    }

    public static boolean isBase64(String str) {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (BASE_64_ALPHABET.indexOf(cCharAt) == -1) {
                return false;
            }
            if (cCharAt == '=' && i2 < str.length() - 2) {
                return false;
            }
            if (cCharAt != '\n' && cCharAt != '\r') {
                i++;
            }
        }
        return i % 4 == 0;
    }
}
