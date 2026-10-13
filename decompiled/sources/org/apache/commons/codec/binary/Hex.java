package org.apache.commons.codec.binary;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Hex.class */
public class Hex implements BinaryEncoder, BinaryDecoder {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_CHARSET_NAME = CharEncoding.UTF_8;
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final Charset charset;

    public static byte[] decodeHex(char[] data) throws DecoderException {
        byte[] out = new byte[data.length >> 1];
        decodeHex(data, out, 0);
        return out;
    }

    public static int decodeHex(char[] data, byte[] out, int outOffset) throws DecoderException {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new DecoderException("Odd number of characters.");
        }
        int outLen = len >> 1;
        if (out.length - outOffset < outLen) {
            throw new DecoderException("Output array is not large enough to accommodate decoded data.");
        }
        int i = outOffset;
        int j = 0;
        while (j < len) {
            int f = toDigit(data[j], j) << 4;
            int j2 = j + 1;
            int f2 = f | toDigit(data[j2], j2);
            j = j2 + 1;
            out[i] = (byte) (f2 & 255);
            i++;
        }
        return outLen;
    }

    public static byte[] decodeHex(String data) throws DecoderException {
        return decodeHex(data.toCharArray());
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toAlphabet(toLowerCase));
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int dataLength = data.length;
        return encodeHex(data, 0, dataLength, toDigits, new char[dataLength << 1], 0);
    }

    public static char[] encodeHex(byte[] data, int dataOffset, int dataLen, boolean toLowerCase) {
        return encodeHex(data, dataOffset, dataLen, toAlphabet(toLowerCase), new char[dataLen << 1], 0);
    }

    public static void encodeHex(byte[] data, int dataOffset, int dataLen, boolean toLowerCase, char[] out, int outOffset) {
        encodeHex(data, dataOffset, dataLen, toAlphabet(toLowerCase), out, outOffset);
    }

    private static char[] encodeHex(byte[] data, int dataOffset, int dataLen, char[] toDigits, char[] out, int outOffset) {
        int j = outOffset;
        for (int i = dataOffset; i < dataOffset + dataLen; i++) {
            int i2 = j;
            int j2 = j + 1;
            out[i2] = toDigits[(240 & data[i]) >>> 4];
            j = j2 + 1;
            out[j2] = toDigits[15 & data[i]];
        }
        return out;
    }

    public static char[] encodeHex(ByteBuffer data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(ByteBuffer data, boolean toLowerCase) {
        return encodeHex(data, toAlphabet(toLowerCase));
    }

    protected static char[] encodeHex(ByteBuffer byteBuffer, char[] toDigits) {
        return encodeHex(toByteArray(byteBuffer), toDigits);
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    public static String encodeHexString(byte[] data, boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }

    public static String encodeHexString(ByteBuffer data) {
        return new String(encodeHex(data));
    }

    public static String encodeHexString(ByteBuffer data, boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }

    private static char[] toAlphabet(boolean toLowerCase) {
        return toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
    }

    private static byte[] toByteArray(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        if (byteBuffer.hasArray()) {
            byte[] byteArray = byteBuffer.array();
            if (remaining == byteArray.length) {
                byteBuffer.position(remaining);
                return byteArray;
            }
        }
        byte[] byteArray2 = new byte[remaining];
        byteBuffer.get(byteArray2);
        return byteArray2;
    }

    protected static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public Hex() {
        this.charset = DEFAULT_CHARSET;
    }

    public Hex(Charset charset) {
        this.charset = charset;
    }

    public Hex(String charsetName) {
        this(Charset.forName(charsetName));
    }

    @Override // org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] array) throws DecoderException {
        return decodeHex(new String(array, getCharset()).toCharArray());
    }

    public byte[] decode(ByteBuffer buffer) throws DecoderException {
        return decodeHex(new String(toByteArray(buffer), getCharset()).toCharArray());
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object object) throws DecoderException {
        if (object instanceof String) {
            return decode(((String) object).toCharArray());
        }
        if (object instanceof byte[]) {
            return decode((byte[]) object);
        }
        if (object instanceof ByteBuffer) {
            return decode((ByteBuffer) object);
        }
        try {
            return decodeHex((char[]) object);
        } catch (ClassCastException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }

    @Override // org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] array) {
        return encodeHexString(array).getBytes(getCharset());
    }

    public byte[] encode(ByteBuffer array) {
        return encodeHexString(array).getBytes(getCharset());
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object object) throws EncoderException {
        byte[] byteArray;
        if (object instanceof String) {
            byteArray = ((String) object).getBytes(getCharset());
        } else if (object instanceof ByteBuffer) {
            byteArray = toByteArray((ByteBuffer) object);
        } else {
            try {
                byteArray = (byte[]) object;
            } catch (ClassCastException e) {
                throw new EncoderException(e.getMessage(), e);
            }
        }
        return encodeHex(byteArray);
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        return this.charset.name();
    }

    public String toString() {
        return super.toString() + "[charsetName=" + this.charset + "]";
    }
}
