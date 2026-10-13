package org.apache.commons.codec.binary;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/StringUtils.class */
public class StringUtils {
    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if ((cs1 instanceof String) && (cs2 instanceof String)) {
            return cs1.equals(cs2);
        }
        return cs1.length() == cs2.length() && CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }

    private static ByteBuffer getByteBuffer(String string, Charset charset) {
        if (string == null) {
            return null;
        }
        return ByteBuffer.wrap(string.getBytes(charset));
    }

    public static ByteBuffer getByteBufferUtf8(String string) {
        return getByteBuffer(string, StandardCharsets.UTF_8);
    }

    private static byte[] getBytes(String string, Charset charset) {
        if (string == null) {
            return null;
        }
        return string.getBytes(charset);
    }

    public static byte[] getBytesIso8859_1(String string) {
        return getBytes(string, StandardCharsets.ISO_8859_1);
    }

    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }

    public static byte[] getBytesUsAscii(String string) {
        return getBytes(string, StandardCharsets.US_ASCII);
    }

    public static byte[] getBytesUtf16(String string) {
        return getBytes(string, StandardCharsets.UTF_16);
    }

    public static byte[] getBytesUtf16Be(String string) {
        return getBytes(string, StandardCharsets.UTF_16BE);
    }

    public static byte[] getBytesUtf16Le(String string) {
        return getBytes(string, StandardCharsets.UTF_16LE);
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, StandardCharsets.UTF_8);
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    private static String newString(byte[] bytes, Charset charset) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public static String newString(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw newIllegalStateException(charsetName, e);
        }
    }

    public static String newStringIso8859_1(byte[] bytes) {
        return newString(bytes, StandardCharsets.ISO_8859_1);
    }

    public static String newStringUsAscii(byte[] bytes) {
        return newString(bytes, StandardCharsets.US_ASCII);
    }

    public static String newStringUtf16(byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16);
    }

    public static String newStringUtf16Be(byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16BE);
    }

    public static String newStringUtf16Le(byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16LE);
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_8);
    }

    @Deprecated
    public StringUtils() {
    }
}
