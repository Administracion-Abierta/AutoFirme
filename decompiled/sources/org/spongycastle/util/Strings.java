package org.spongycastle.util;

import com.sun.jna.platform.win32.WinUser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/Strings.class */
public final class Strings {
    private static String LINE_SEPARATOR;

    static {
        try {
            LINE_SEPARATOR = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.spongycastle.util.Strings.1
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

    public static String fromUTF8ByteArray(byte[] bytes) {
        char ch;
        int i = 0;
        int length = 0;
        while (i < bytes.length) {
            length++;
            if ((bytes[i] & 240) == 240) {
                length++;
                i += 4;
            } else if ((bytes[i] & 224) == 224) {
                i += 3;
            } else if ((bytes[i] & 192) == 192) {
                i += 2;
            } else {
                i++;
            }
        }
        char[] cs = new char[length];
        int i2 = 0;
        int length2 = 0;
        while (i2 < bytes.length) {
            if ((bytes[i2] & 240) == 240) {
                int codePoint = ((bytes[i2] & 3) << 18) | ((bytes[i2 + 1] & 63) << 12) | ((bytes[i2 + 2] & 63) << 6) | (bytes[i2 + 3] & 63);
                int U = codePoint - 65536;
                char W1 = (char) (55296 | (U >> 10));
                char W2 = (char) (56320 | (U & WinUser.CF_GDIOBJLAST));
                int i3 = length2;
                length2++;
                cs[i3] = W1;
                ch = W2;
                i2 += 4;
            } else if ((bytes[i2] & 224) == 224) {
                ch = (char) (((bytes[i2] & 15) << 12) | ((bytes[i2 + 1] & 63) << 6) | (bytes[i2 + 2] & 63));
                i2 += 3;
            } else if ((bytes[i2] & 208) == 208) {
                ch = (char) (((bytes[i2] & 31) << 6) | (bytes[i2 + 1] & 63));
                i2 += 2;
            } else if ((bytes[i2] & 192) == 192) {
                ch = (char) (((bytes[i2] & 31) << 6) | (bytes[i2 + 1] & 63));
                i2 += 2;
            } else {
                ch = (char) (bytes[i2] & 255);
                i2++;
            }
            int i4 = length2;
            length2++;
            cs[i4] = ch;
        }
        return new String(cs);
    }

    public static byte[] toUTF8ByteArray(String string) {
        return toUTF8ByteArray(string.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] string) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            toUTF8ByteArray(string, bOut);
            return bOut.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
    }

    public static void toUTF8ByteArray(char[] string, OutputStream sOut) throws IOException {
        int i = 0;
        while (i < string.length) {
            char ch = string[i];
            if (ch < 128) {
                sOut.write(ch);
            } else if (ch < 2048) {
                sOut.write(192 | (ch >> 6));
                sOut.write(128 | (ch & '?'));
            } else if (ch >= 55296 && ch <= 57343) {
                if (i + 1 >= string.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                i++;
                char ch2 = string[i];
                if (ch <= 56319) {
                    int codePoint = (((ch & 1023) << 10) | (ch2 & 1023)) + 65536;
                    sOut.write(240 | (codePoint >> 18));
                    sOut.write(128 | ((codePoint >> 12) & 63));
                    sOut.write(128 | ((codePoint >> 6) & 63));
                    sOut.write(128 | (codePoint & 63));
                } else {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
            } else {
                sOut.write(224 | (ch >> '\f'));
                sOut.write(128 | ((ch >> 6) & 63));
                sOut.write(128 | (ch & '?'));
            }
            i++;
        }
    }

    public static String toUpperCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; i++) {
            char ch = chars[i];
            if ('a' <= ch && 'z' >= ch) {
                changed = true;
                chars[i] = (char) ((ch - 'a') + 65);
            }
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }

    public static String toLowerCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; i++) {
            char ch = chars[i];
            if ('A' <= ch && 'Z' >= ch) {
                changed = true;
                chars[i] = (char) ((ch - 'A') + 97);
            }
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }

    public static byte[] toByteArray(char[] chars) {
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i != bytes.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];
        for (int i = 0; i != bytes.length; i++) {
            char ch = string.charAt(i);
            bytes[i] = (byte) ch;
        }
        return bytes;
    }

    public static int toByteArray(String s, byte[] buf, int off) {
        int count = s.length();
        for (int i = 0; i < count; i++) {
            char c = s.charAt(i);
            buf[off + i] = (byte) c;
        }
        return count;
    }

    public static String fromByteArray(byte[] bytes) {
        return new String(asCharArray(bytes));
    }

    public static char[] asCharArray(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i != chars.length; i++) {
            chars[i] = (char) (bytes[i] & 255);
        }
        return chars;
    }

    public static String[] split(String input, char delimiter) {
        Vector v = new Vector();
        boolean moreTokens = true;
        while (moreTokens) {
            int tokenLocation = input.indexOf(delimiter);
            if (tokenLocation > 0) {
                String subString = input.substring(0, tokenLocation);
                v.addElement(subString);
                input = input.substring(tokenLocation + 1);
            } else {
                moreTokens = false;
                v.addElement(input);
            }
        }
        String[] res = new String[v.size()];
        for (int i = 0; i != res.length; i++) {
            res[i] = (String) v.elementAt(i);
        }
        return res;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/Strings$StringListImpl.class */
    private static class StringListImpl extends ArrayList<String> implements StringList {
        private StringListImpl() {
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List, org.spongycastle.util.StringList
        public /* bridge */ /* synthetic */ String get(int x0) {
            return (String) super.get(x0);
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean add(String s) {
            return super.add(s);
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
        public String set(int index, String element) {
            return (String) super.set(index, element);
        }

        @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
        public void add(int index, String element) {
            super.add(index, element);
        }

        @Override // org.spongycastle.util.StringList
        public String[] toStringArray() {
            String[] strs = new String[size()];
            for (int i = 0; i != strs.length; i++) {
                strs[i] = (String) get(i);
            }
            return strs;
        }

        @Override // org.spongycastle.util.StringList
        public String[] toStringArray(int from, int to) {
            String[] strs = new String[to - from];
            for (int i = from; i != size() && i != to; i++) {
                strs[i - from] = (String) get(i);
            }
            return strs;
        }
    }
}
