package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/UtfHelpper.class */
public final class UtfHelpper {
    private static final boolean OLD_UTF8 = ((Boolean) AccessController.doPrivileged(() -> {
        return Boolean.valueOf(Boolean.getBoolean("org.apache.xml.security.c14n.oldUtf8"));
    })).booleanValue();

    private UtfHelpper() {
    }

    public static void writeByte(String str, OutputStream out, Map<String, byte[]> cache) throws IOException {
        byte[] result = cache.get(str);
        if (result == null) {
            result = getStringInUtf8(str);
            cache.put(str, result);
        }
        out.write(result);
    }

    public static void writeCodePointToUtf8(int c, OutputStream out) throws IOException {
        byte extraByte;
        if (!Character.isValidCodePoint(c) || ((c >= 55296 && c <= 56319) || (c >= 56320 && c <= 57343))) {
            out.write(63);
            return;
        }
        if (OLD_UTF8 && c >= 65536) {
            out.write(63);
            out.write(63);
            return;
        }
        if (c < 128) {
            out.write(c);
            return;
        }
        if (c < 2048) {
            extraByte = 1;
        } else if (c < 65536) {
            extraByte = 2;
        } else if (c < 2097152) {
            extraByte = 3;
        } else if (c < 67108864) {
            extraByte = 4;
        } else if (c <= Integer.MAX_VALUE) {
            extraByte = 5;
        } else {
            out.write(63);
            return;
        }
        int shift = 6 * extraByte;
        byte write = (byte) ((254 << (6 - extraByte)) | (c >>> shift));
        out.write(write);
        for (int i = extraByte - 1; i >= 0; i--) {
            shift -= 6;
            byte write2 = (byte) (128 | ((c >>> shift) & 63));
            out.write(write2);
        }
    }

    public static void writeStringToUtf8(String str, OutputStream out) throws IOException {
        byte extraByte;
        int length = str.length();
        int i = 0;
        while (i < length) {
            int c = str.codePointAt(i);
            i += Character.charCount(c);
            if (!Character.isValidCodePoint(c) || ((c >= 55296 && c <= 56319) || (c >= 56320 && c <= 57343))) {
                out.write(63);
            } else if (OLD_UTF8 && c >= 65536) {
                out.write(63);
                out.write(63);
            } else if (c < 128) {
                out.write(c);
            } else {
                if (c < 2048) {
                    extraByte = 1;
                } else if (c < 65536) {
                    extraByte = 2;
                } else if (c < 2097152) {
                    extraByte = 3;
                } else if (c < 67108864) {
                    extraByte = 4;
                } else if (c <= Integer.MAX_VALUE) {
                    extraByte = 5;
                } else {
                    out.write(63);
                }
                int shift = 6 * extraByte;
                byte write = (byte) ((254 << (6 - extraByte)) | (c >>> shift));
                out.write(write);
                for (int j = extraByte - 1; j >= 0; j--) {
                    shift -= 6;
                    byte write2 = (byte) (128 | ((c >>> shift) & 63));
                    out.write(write2);
                }
            }
        }
    }

    public static byte[] getStringInUtf8(String str) {
        byte extraByte;
        int length = str.length();
        boolean expanded = false;
        byte[] result = new byte[length];
        int i = 0;
        int out = 0;
        while (i < length) {
            int c = str.codePointAt(i);
            i += Character.charCount(c);
            if (!Character.isValidCodePoint(c) || ((c >= 55296 && c <= 56319) || (c >= 56320 && c <= 57343))) {
                int i2 = out;
                out++;
                result[i2] = 63;
            } else if (OLD_UTF8 && c >= 65536) {
                int i3 = out;
                int out2 = out + 1;
                result[i3] = 63;
                out = out2 + 1;
                result[out2] = 63;
            } else if (c < 128) {
                int i4 = out;
                out++;
                result[i4] = (byte) c;
            } else {
                if (!expanded) {
                    byte[] newResult = new byte[6 * length];
                    System.arraycopy(result, 0, newResult, 0, out);
                    result = newResult;
                    expanded = true;
                }
                if (c < 2048) {
                    extraByte = 1;
                } else if (c < 65536) {
                    extraByte = 2;
                } else if (c < 2097152) {
                    extraByte = 3;
                } else if (c < 67108864) {
                    extraByte = 4;
                } else if (c <= Integer.MAX_VALUE) {
                    extraByte = 5;
                } else {
                    int i5 = out;
                    out++;
                    result[i5] = 63;
                }
                int shift = 6 * extraByte;
                byte write = (byte) ((254 << (6 - extraByte)) | (c >>> shift));
                int i6 = out;
                out++;
                result[i6] = write;
                for (int j = extraByte - 1; j >= 0; j--) {
                    shift -= 6;
                    byte write2 = (byte) (128 | ((c >>> shift) & 63));
                    int i7 = out;
                    out++;
                    result[i7] = write2;
                }
            }
        }
        if (expanded) {
            byte[] newResult2 = new byte[out];
            System.arraycopy(result, 0, newResult2, 0, out);
            result = newResult2;
        }
        return result;
    }
}
