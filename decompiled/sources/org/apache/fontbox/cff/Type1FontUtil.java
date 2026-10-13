package org.apache.fontbox.cff;

import com.sun.jna.platform.win32.WinError;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/Type1FontUtil.class */
public final class Type1FontUtil {
    private Type1FontUtil() {
    }

    public static String hexEncode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String string = Integer.toHexString(aByte & 255);
            if (string.length() == 1) {
                sb.append("0");
            }
            sb.append(string.toUpperCase(Locale.US));
        }
        return sb.toString();
    }

    public static byte[] hexDecode(String string) {
        if (string.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < string.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(string.substring(i, i + 2), 16);
        }
        return bytes;
    }

    public static byte[] eexecEncrypt(byte[] buffer) {
        return encrypt(buffer, 55665, 4);
    }

    public static byte[] charstringEncrypt(byte[] buffer, int n) {
        return encrypt(buffer, WinError.ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA, n);
    }

    private static byte[] encrypt(byte[] plaintextBytes, int r, int n) {
        byte[] buffer = new byte[plaintextBytes.length + n];
        System.arraycopy(plaintextBytes, 0, buffer, n, buffer.length - n);
        byte[] ciphertextBytes = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            int plain = buffer[i] & 255;
            int cipher = plain ^ (r >> 8);
            ciphertextBytes[i] = (byte) cipher;
            r = (((cipher + r) * 52845) + 22719) & 65535;
        }
        return ciphertextBytes;
    }

    public static byte[] eexecDecrypt(byte[] buffer) {
        return decrypt(buffer, 55665, 4);
    }

    public static byte[] charstringDecrypt(byte[] buffer, int n) {
        return decrypt(buffer, WinError.ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA, n);
    }

    private static byte[] decrypt(byte[] ciphertextBytes, int r, int n) {
        byte[] buffer = new byte[ciphertextBytes.length];
        for (int i = 0; i < ciphertextBytes.length; i++) {
            int cipher = ciphertextBytes[i] & 255;
            int plain = cipher ^ (r >> 8);
            buffer[i] = (byte) plain;
            r = (((cipher + r) * 52845) + 22719) & 65535;
        }
        byte[] plaintextBytes = new byte[ciphertextBytes.length - n];
        System.arraycopy(buffer, n, plaintextBytes, 0, plaintextBytes.length);
        return plaintextBytes;
    }
}
