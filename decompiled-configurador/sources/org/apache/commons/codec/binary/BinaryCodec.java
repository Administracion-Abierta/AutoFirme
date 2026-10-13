package org.apache.commons.codec.binary;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/BinaryCodec.class */
public class BinaryCodec implements BinaryDecoder, BinaryEncoder {
    private static final int BIT_0 = 1;
    private static final int BIT_1 = 2;
    private static final int BIT_2 = 4;
    private static final int BIT_3 = 8;
    private static final int BIT_4 = 16;
    private static final int BIT_5 = 32;
    private static final int BIT_6 = 64;
    private static final int BIT_7 = 128;
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int[] BITS = {1, 2, 4, 8, 16, 32, 64, 128};

    public static byte[] fromAscii(byte[] ascii) {
        if (isEmpty(ascii)) {
            return EMPTY_BYTE_ARRAY;
        }
        int asciiLength = ascii.length;
        byte[] raw = new byte[asciiLength >> 3];
        int ii = 0;
        int jj = asciiLength - 1;
        while (ii < raw.length) {
            for (int bits = 0; bits < BITS.length; bits++) {
                if (ascii[jj - bits] == 49) {
                    int i = ii;
                    raw[i] = (byte) (raw[i] | BITS[bits]);
                }
            }
            ii++;
            jj -= 8;
        }
        return raw;
    }

    public static byte[] fromAscii(char[] ascii) {
        if (ascii == null || ascii.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        int asciiLength = ascii.length;
        byte[] raw = new byte[asciiLength >> 3];
        int ii = 0;
        int jj = asciiLength - 1;
        while (ii < raw.length) {
            for (int bits = 0; bits < BITS.length; bits++) {
                if (ascii[jj - bits] == '1') {
                    int i = ii;
                    raw[i] = (byte) (raw[i] | BITS[bits]);
                }
            }
            ii++;
            jj -= 8;
        }
        return raw;
    }

    static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static byte[] toAsciiBytes(byte[] raw) {
        if (isEmpty(raw)) {
            return EMPTY_BYTE_ARRAY;
        }
        int rawLength = raw.length;
        byte[] l_ascii = new byte[rawLength << 3];
        int ii = 0;
        int jj = l_ascii.length - 1;
        while (ii < rawLength) {
            for (int bits = 0; bits < BITS.length; bits++) {
                if ((raw[ii] & BITS[bits]) == 0) {
                    l_ascii[jj - bits] = 48;
                } else {
                    l_ascii[jj - bits] = 49;
                }
            }
            ii++;
            jj -= 8;
        }
        return l_ascii;
    }

    public static char[] toAsciiChars(byte[] raw) {
        if (isEmpty(raw)) {
            return EMPTY_CHAR_ARRAY;
        }
        int rawLength = raw.length;
        char[] l_ascii = new char[rawLength << 3];
        int ii = 0;
        int jj = l_ascii.length - 1;
        while (ii < rawLength) {
            for (int bits = 0; bits < BITS.length; bits++) {
                if ((raw[ii] & BITS[bits]) == 0) {
                    l_ascii[jj - bits] = '0';
                } else {
                    l_ascii[jj - bits] = '1';
                }
            }
            ii++;
            jj -= 8;
        }
        return l_ascii;
    }

    public static String toAsciiString(byte[] raw) {
        return new String(toAsciiChars(raw));
    }

    @Override // org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] ascii) {
        return fromAscii(ascii);
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object ascii) throws DecoderException {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        if (ascii instanceof byte[]) {
            return fromAscii((byte[]) ascii);
        }
        if (ascii instanceof char[]) {
            return fromAscii((char[]) ascii);
        }
        if (ascii instanceof String) {
            return fromAscii(((String) ascii).toCharArray());
        }
        throw new DecoderException("argument not a byte array");
    }

    @Override // org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] raw) {
        return toAsciiBytes(raw);
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object raw) throws EncoderException {
        if (!(raw instanceof byte[])) {
            throw new EncoderException("argument not a byte array");
        }
        return toAsciiChars((byte[]) raw);
    }

    public byte[] toByteArray(String ascii) {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return fromAscii(ascii.toCharArray());
    }
}
