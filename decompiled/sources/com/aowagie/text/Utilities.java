package com.aowagie.text;

import com.aowagie.text.pdf.PRTokeniser;
import com.ctc.wstx.api.InvalidCharHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Utilities.class */
public class Utilities {
    static Set getKeySet(Hashtable table) {
        return table == null ? Collections.EMPTY_SET : table.keySet();
    }

    public static Object[][] addToArray(Object[][] original, Object[] item) {
        if (original == null) {
            return new Object[][]{item};
        }
        Object[][] original2 = new Object[original.length + 1][];
        System.arraycopy(original, 0, original2, 0, original.length);
        original2[original.length] = item;
        return original2;
    }

    public static boolean checkTrueOrFalse(Properties attributes, String key) {
        return "true".equalsIgnoreCase(attributes.getProperty(key));
    }

    static String unEscapeURL(String src) {
        StringBuffer bf = new StringBuffer();
        char[] s = src.toCharArray();
        int k = 0;
        while (k < s.length) {
            char c = s[k];
            if (c == '%') {
                if (k + 2 >= s.length) {
                    bf.append(c);
                } else {
                    int a0 = PRTokeniser.getHex(s[k + 1]);
                    int a1 = PRTokeniser.getHex(s[k + 2]);
                    if (a0 < 0 || a1 < 0) {
                        bf.append(c);
                    } else {
                        bf.append((char) ((a0 * 16) + a1));
                        k += 2;
                    }
                }
            } else {
                bf.append(c);
            }
            k++;
        }
        return bf.toString();
    }

    static URL toURL(String filename) throws MalformedURLException {
        try {
            return new URL(filename);
        } catch (Exception e) {
            return new File(filename).toURI().toURL();
        }
    }

    public static void skip(InputStream is, int size) throws IOException {
        while (size > 0) {
            long n = is.skip(size);
            if (n > 0) {
                size = (int) (((long) size) - n);
            } else {
                return;
            }
        }
    }

    private static final float millimetersToInches(float value) {
        return value / 25.4f;
    }

    private static final float pointsToInches(float value) {
        return value / 72.0f;
    }

    private static final float inchesToMillimeters(float value) {
        return value * 25.4f;
    }

    private static final float inchesToPoints(float value) {
        return value * 72.0f;
    }

    public static boolean isSurrogateHigh(char c) {
        return c >= 55296 && c <= 56319;
    }

    private static boolean isSurrogateLow(char c) {
        return c >= 56320 && c <= 57343;
    }

    public static boolean isSurrogatePair(String text, int idx) {
        return idx >= 0 && idx <= text.length() - 2 && isSurrogateHigh(text.charAt(idx)) && isSurrogateLow(text.charAt(idx + 1));
    }

    public static boolean isSurrogatePair(char[] text, int idx) {
        return idx >= 0 && idx <= text.length - 2 && isSurrogateHigh(text[idx]) && isSurrogateLow(text[idx + 1]);
    }

    public static int convertToUtf32(char highSurrogate, char lowSurrogate) {
        return ((((highSurrogate - InvalidCharHandler.FailingHandler.SURR1_FIRST) * 1024) + lowSurrogate) - InvalidCharHandler.FailingHandler.SURR2_FIRST) + 65536;
    }

    public static int convertToUtf32(char[] text, int idx) {
        return ((((text[idx] - InvalidCharHandler.FailingHandler.SURR1_FIRST) * 1024) + text[idx + 1]) - InvalidCharHandler.FailingHandler.SURR2_FIRST) + 65536;
    }

    public static int convertToUtf32(String text, int idx) {
        return ((((text.charAt(idx) - InvalidCharHandler.FailingHandler.SURR1_FIRST) * 1024) + text.charAt(idx + 1)) - InvalidCharHandler.FailingHandler.SURR2_FIRST) + 65536;
    }
}
