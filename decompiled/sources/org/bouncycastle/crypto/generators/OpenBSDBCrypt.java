package org.bouncycastle.crypto.generators;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/OpenBSDBCrypt.class */
public class OpenBSDBCrypt {
    private static final String defaultVersion = "2y";
    private static final byte[] encodingTable = {46, 47, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
    private static final byte[] decodingTable = new byte[128];
    private static final Set<String> allowedVersions = new HashSet();

    private OpenBSDBCrypt() {
    }

    public static String generate(char[] cArr, byte[] bArr, int i) {
        return generate(defaultVersion, cArr, bArr, i);
    }

    public static String generate(byte[] bArr, byte[] bArr2, int i) {
        return generate(defaultVersion, bArr, bArr2, i);
    }

    public static String generate(String str, char[] cArr, byte[] bArr, int i) {
        if (cArr == null) {
            throw new IllegalArgumentException("Password required.");
        }
        return doGenerate(str, Strings.toUTF8ByteArray(cArr), bArr, i);
    }

    public static String generate(String str, byte[] bArr, byte[] bArr2, int i) {
        if (bArr == null) {
            throw new IllegalArgumentException("Password required.");
        }
        return doGenerate(str, Arrays.clone(bArr), bArr2, i);
    }

    private static String doGenerate(String str, byte[] bArr, byte[] bArr2, int i) {
        if (!allowedVersions.contains(str)) {
            throw new IllegalArgumentException("Version " + str + " is not accepted by this implementation.");
        }
        if (bArr2 == null) {
            throw new IllegalArgumentException("Salt required.");
        }
        if (bArr2.length != 16) {
            throw new DataLengthException("16 byte salt required: " + bArr2.length);
        }
        if (i < 4 || i > 31) {
            throw new IllegalArgumentException("Invalid cost factor.");
        }
        byte[] bArr3 = new byte[bArr.length >= 72 ? 72 : bArr.length + 1];
        if (bArr3.length > bArr.length) {
            System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        } else {
            System.arraycopy(bArr, 0, bArr3, 0, bArr3.length);
        }
        Arrays.fill(bArr, (byte) 0);
        String strCreateBcryptString = createBcryptString(str, bArr3, bArr2, i);
        Arrays.fill(bArr3, (byte) 0);
        return strCreateBcryptString;
    }

    public static boolean checkPassword(String str, char[] cArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("Missing password.");
        }
        return doCheckPassword(str, Strings.toUTF8ByteArray(cArr));
    }

    public static boolean checkPassword(String str, byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException("Missing password.");
        }
        return doCheckPassword(str, Arrays.clone(bArr));
    }

    private static boolean doCheckPassword(String str, byte[] bArr) {
        String strSubstring;
        int i;
        if (str == null) {
            throw new IllegalArgumentException("Missing bcryptString.");
        }
        if (str.charAt(1) != '2') {
            throw new IllegalArgumentException("not a Bcrypt string");
        }
        int length = str.length();
        if (length != 60 && (length != 59 || str.charAt(2) != '$')) {
            throw new DataLengthException("Bcrypt String length: " + length + ", 60 required.");
        }
        if (str.charAt(2) == '$') {
            if (str.charAt(0) != '$' || str.charAt(5) != '$') {
                throw new IllegalArgumentException("Invalid Bcrypt String format.");
            }
        } else if (str.charAt(0) != '$' || str.charAt(3) != '$' || str.charAt(6) != '$') {
            throw new IllegalArgumentException("Invalid Bcrypt String format.");
        }
        if (str.charAt(2) == '$') {
            strSubstring = str.substring(1, 2);
            i = 3;
        } else {
            strSubstring = str.substring(1, 3);
            i = 4;
        }
        if (!allowedVersions.contains(strSubstring)) {
            throw new IllegalArgumentException("Bcrypt version '" + strSubstring + "' is not supported by this implementation");
        }
        String strSubstring2 = str.substring(i, i + 2);
        try {
            int i2 = Integer.parseInt(strSubstring2);
            if (i2 < 4 || i2 > 31) {
                throw new IllegalArgumentException("Invalid cost factor: " + i2 + ", 4 < cost < 31 expected.");
            }
            return Strings.constantTimeAreEqual(str, doGenerate(strSubstring, bArr, decodeSaltString(str.substring(str.lastIndexOf(36) + 1, length - 31)), i2));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid cost factor: " + strSubstring2);
        }
    }

    private static String createBcryptString(String str, byte[] bArr, byte[] bArr2, int i) {
        if (!allowedVersions.contains(str)) {
            throw new IllegalArgumentException("Version " + str + " is not accepted by this implementation.");
        }
        StringBuilder sb = new StringBuilder(60);
        sb.append('$');
        sb.append(str);
        sb.append('$');
        sb.append(i < 10 ? "0" + i : Integer.toString(i));
        sb.append('$');
        encodeData(sb, bArr2);
        encodeData(sb, BCrypt.generate(bArr, bArr2, i));
        return sb.toString();
    }

    private static void encodeData(StringBuilder sb, byte[] bArr) {
        if (bArr.length != 24 && bArr.length != 16) {
            throw new DataLengthException("Invalid length: " + bArr.length + ", 24 for key or 16 for salt expected");
        }
        boolean z = false;
        if (bArr.length == 16) {
            z = true;
            byte[] bArr2 = new byte[18];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            bArr = bArr2;
        } else {
            bArr[bArr.length - 1] = 0;
        }
        int length = bArr.length;
        for (int i = 0; i < length; i += 3) {
            int i2 = bArr[i] & 255;
            int i3 = bArr[i + 1] & 255;
            int i4 = bArr[i + 2] & 255;
            sb.append((char) encodingTable[(i2 >>> 2) & 63]);
            sb.append((char) encodingTable[((i2 << 4) | (i3 >>> 4)) & 63]);
            sb.append((char) encodingTable[((i3 << 2) | (i4 >>> 6)) & 63]);
            sb.append((char) encodingTable[i4 & 63]);
        }
        if (z) {
            sb.setLength(sb.length() - 2);
        } else {
            sb.setLength(sb.length() - 1);
        }
    }

    private static byte[] decodeSaltString(String str) {
        char[] charArray = str.toCharArray();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16);
        if (charArray.length != 22) {
            throw new DataLengthException("Invalid base64 salt length: " + charArray.length + " , 22 required.");
        }
        for (char c : charArray) {
            if (c > 'z' || c < '.' || (c > '9' && c < 'A')) {
                throw new IllegalArgumentException("Salt string contains invalid character: " + ((int) c));
            }
        }
        char[] cArr = new char[24];
        System.arraycopy(charArray, 0, cArr, 0, charArray.length);
        int length = cArr.length;
        for (int i = 0; i < length; i += 4) {
            byte b = decodingTable[cArr[i]];
            byte b2 = decodingTable[cArr[i + 1]];
            byte b3 = decodingTable[cArr[i + 2]];
            byte b4 = decodingTable[cArr[i + 3]];
            byteArrayOutputStream.write((b << 2) | (b2 >> 4));
            byteArrayOutputStream.write((b2 << 4) | (b3 >> 2));
            byteArrayOutputStream.write((b3 << 6) | b4);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byte[] bArr = new byte[16];
        System.arraycopy(byteArray, 0, bArr, 0, bArr.length);
        return bArr;
    }

    static {
        allowedVersions.add("2");
        allowedVersions.add("2x");
        allowedVersions.add("2a");
        allowedVersions.add(defaultVersion);
        allowedVersions.add("2b");
        for (int i = 0; i < decodingTable.length; i++) {
            decodingTable[i] = -1;
        }
        for (int i2 = 0; i2 < encodingTable.length; i2++) {
            decodingTable[encodingTable[i2]] = (byte) i2;
        }
    }
}
