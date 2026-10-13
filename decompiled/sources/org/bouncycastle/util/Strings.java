package org.bouncycastle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;
import org.bouncycastle.util.encoders.UTF8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Strings.class */
public final class Strings {
    private static String LINE_SEPARATOR;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Strings$StringListImpl.class */
    private static class StringListImpl extends ArrayList<String> implements StringList {
        private StringListImpl() {
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean add(String str) {
            return super.add(str);
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
        public String set(int i, String str) {
            return (String) super.set(i, str);
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
        public void add(int i, String str) {
            super.add(i, str);
        }

        @Override // org.bouncycastle.util.StringList
        public String[] toStringArray() {
            String[] strArr = new String[size()];
            for (int i = 0; i != strArr.length; i++) {
                strArr[i] = (String) get(i);
            }
            return strArr;
        }

        @Override // org.bouncycastle.util.StringList
        public String[] toStringArray(int i, int i2) {
            String[] strArr = new String[i2 - i];
            for (int i3 = i; i3 != size() && i3 != i2; i3++) {
                strArr[i3 - i] = (String) get(i3);
            }
            return strArr;
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List, org.bouncycastle.util.StringList
        public /* bridge */ /* synthetic */ String get(int i) {
            return (String) super.get(i);
        }
    }

    public static String fromUTF8ByteArray(byte[] bArr) {
        char[] cArr = new char[bArr.length];
        int iTranscodeToUTF16 = UTF8.transcodeToUTF16(bArr, cArr);
        if (iTranscodeToUTF16 < 0) {
            throw new IllegalArgumentException("Invalid UTF-8 input");
        }
        return new String(cArr, 0, iTranscodeToUTF16);
    }

    public static String fromUTF8ByteArray(byte[] bArr, int i, int i2) {
        char[] cArr = new char[i2];
        int iTranscodeToUTF16 = UTF8.transcodeToUTF16(bArr, i, i2, cArr);
        if (iTranscodeToUTF16 < 0) {
            throw new IllegalArgumentException("Invalid UTF-8 input");
        }
        return new String(cArr, 0, iTranscodeToUTF16);
    }

    public static byte[] toUTF8ByteArray(String str) {
        return toUTF8ByteArray(str.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] cArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            toUTF8ByteArray(cArr, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
    }

    public static void toUTF8ByteArray(char[] cArr, OutputStream outputStream) throws IOException {
        int i = 0;
        while (i < cArr.length) {
            char c = cArr[i];
            if (c < 128) {
                outputStream.write(c);
            } else if (c < 2048) {
                outputStream.write(192 | (c >> 6));
                outputStream.write(128 | (c & '?'));
            } else if (c < 55296 || c > 57343) {
                outputStream.write(224 | (c >> '\f'));
                outputStream.write(128 | ((c >> 6) & 63));
                outputStream.write(128 | (c & '?'));
            } else {
                if (i + 1 >= cArr.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                i++;
                char c2 = cArr[i];
                if (c > 56319) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int i2 = (((c & 1023) << 10) | (c2 & 1023)) + 65536;
                outputStream.write(240 | (i2 >> 18));
                outputStream.write(128 | ((i2 >> 12) & 63));
                outputStream.write(128 | ((i2 >> 6) & 63));
                outputStream.write(128 | (i2 & 63));
            }
            i++;
        }
    }

    public static String toUpperCase(String str) {
        boolean z = false;
        char[] charArray = str.toCharArray();
        for (int i = 0; i != charArray.length; i++) {
            char c = charArray[i];
            if ('a' <= c && 'z' >= c) {
                z = true;
                charArray[i] = (char) ((c - 'a') + 65);
            }
        }
        return z ? new String(charArray) : str;
    }

    public static String toLowerCase(String str) {
        boolean z = false;
        char[] charArray = str.toCharArray();
        for (int i = 0; i != charArray.length; i++) {
            char c = charArray[i];
            if ('A' <= c && 'Z' >= c) {
                z = true;
                charArray[i] = (char) ((c - 'A') + 97);
            }
        }
        return z ? new String(charArray) : str;
    }

    public static byte[] toByteArray(char[] cArr) {
        byte[] bArr = new byte[cArr.length];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) cArr[i];
        }
        return bArr;
    }

    public static byte[] toByteArray(String str) {
        byte[] bArr = new byte[str.length()];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) str.charAt(i);
        }
        return bArr;
    }

    public static int toByteArray(String str, byte[] bArr, int i) {
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            bArr[i + i2] = (byte) str.charAt(i2);
        }
        return length;
    }

    public static boolean constantTimeAreEqual(String str, String str2) {
        boolean z = str.length() == str2.length();
        int length = str.length();
        if (z) {
            for (int i = 0; i != length; i++) {
                z &= str.charAt(i) == str2.charAt(i);
            }
        } else {
            for (int i2 = 0; i2 != length; i2++) {
                z &= str.charAt(i2) == ' ';
            }
        }
        return z;
    }

    public static String fromByteArray(byte[] bArr) {
        return new String(asCharArray(bArr));
    }

    public static char[] asCharArray(byte[] bArr) {
        char[] cArr = new char[bArr.length];
        for (int i = 0; i != cArr.length; i++) {
            cArr[i] = (char) (bArr[i] & 255);
        }
        return cArr;
    }

    public static String[] split(String str, char c) {
        Vector vector = new Vector();
        boolean z = true;
        while (z) {
            int iIndexOf = str.indexOf(c);
            if (iIndexOf > 0) {
                vector.addElement(str.substring(0, iIndexOf));
                str = str.substring(iIndexOf + 1);
            } else {
                z = false;
                vector.addElement(str);
            }
        }
        String[] strArr = new String[vector.size()];
        for (int i = 0; i != strArr.length; i++) {
            strArr[i] = (String) vector.elementAt(i);
        }
        return strArr;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    static {
        try {
            LINE_SEPARATOR = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.bouncycastle.util.Strings.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty("line.separator");
                }
            });
        } catch (Exception e) {
            try {
                LINE_SEPARATOR = String.format("%n", new Object[0]);
            } catch (Exception e2) {
                LINE_SEPARATOR = "\n";
            }
        }
    }
}
