package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/net/QuotedPrintableCodec.class */
public class QuotedPrintableCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {
    private static final BitSet PRINTABLE_CHARS = new BitSet(256);
    private static final byte ESCAPE_CHAR = 61;
    private static final byte TAB = 9;
    private static final byte SPACE = 32;
    private static final byte CR = 13;
    private static final byte LF = 10;
    private static final int MIN_BYTES = 3;
    private static final int SAFE_LENGTH = 73;
    private final Charset charset;
    private final boolean strict;

    static {
        for (int i = 33; i <= 60; i++) {
            PRINTABLE_CHARS.set(i);
        }
        for (int i2 = 62; i2 <= 126; i2++) {
            PRINTABLE_CHARS.set(i2);
        }
        PRINTABLE_CHARS.set(9);
        PRINTABLE_CHARS.set(32);
    }

    public static final byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < bytes.length) {
            byte b = bytes[i];
            if (b == 61) {
                try {
                    i++;
                    if (bytes[i] != 13) {
                        int u = Utils.digit16(bytes[i]);
                        i++;
                        int l = Utils.digit16(bytes[i]);
                        buffer.write((char) ((u << 4) + l));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid quoted-printable encoding", e);
                }
            } else if (b != 13 && b != 10) {
                buffer.write(b);
            }
            i++;
        }
        return buffer.toByteArray();
    }

    private static int encodeByte(int b, boolean encode, ByteArrayOutputStream buffer) {
        if (encode) {
            return encodeQuotedPrintable(b, buffer);
        }
        buffer.write(b);
        return 1;
    }

    public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes) {
        return encodeQuotedPrintable(printable, bytes, false);
    }

    public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes, boolean strict) {
        int iEncodeByte;
        if (bytes == null) {
            return null;
        }
        if (printable == null) {
            printable = PRINTABLE_CHARS;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesLength = bytes.length;
        if (strict) {
            if (bytesLength < 3) {
                return null;
            }
            int pos = 1;
            for (int i = 0; i < bytesLength - 3; i++) {
                int b = getUnsignedOctet(i, bytes);
                if (pos < 73) {
                    iEncodeByte = pos + encodeByte(b, !printable.get(b), buffer);
                } else {
                    encodeByte(b, !printable.get(b) || isWhitespace(b), buffer);
                    buffer.write(61);
                    buffer.write(13);
                    buffer.write(10);
                    iEncodeByte = 1;
                }
                pos = iEncodeByte;
            }
            int b2 = getUnsignedOctet(bytesLength - 3, bytes);
            boolean encode = !printable.get(b2) || (isWhitespace(b2) && pos > 68);
            if (pos + encodeByte(b2, encode, buffer) > 71) {
                buffer.write(61);
                buffer.write(13);
                buffer.write(10);
            }
            int i2 = bytesLength - 2;
            while (i2 < bytesLength) {
                int b3 = getUnsignedOctet(i2, bytes);
                boolean encode2 = !printable.get(b3) || (i2 > bytesLength - 2 && isWhitespace(b3));
                encodeByte(b3, encode2, buffer);
                i2++;
            }
        } else {
            for (byte c : bytes) {
                int b4 = c;
                if (b4 < 0) {
                    b4 = 256 + b4;
                }
                if (printable.get(b4)) {
                    buffer.write(b4);
                } else {
                    encodeQuotedPrintable(b4, buffer);
                }
            }
        }
        return buffer.toByteArray();
    }

    private static final int encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
        buffer.write(61);
        char hex1 = Utils.hexDigit(b >> 4);
        char hex2 = Utils.hexDigit(b);
        buffer.write(hex1);
        buffer.write(hex2);
        return 3;
    }

    private static int getUnsignedOctet(int index, byte[] bytes) {
        int b = bytes[index];
        if (b < 0) {
            b = 256 + b;
        }
        return b;
    }

    private static boolean isWhitespace(int b) {
        return b == 32 || b == 9;
    }

    public QuotedPrintableCodec() {
        this(StandardCharsets.UTF_8, false);
    }

    public QuotedPrintableCodec(boolean strict) {
        this(StandardCharsets.UTF_8, strict);
    }

    public QuotedPrintableCodec(Charset charset) {
        this(charset, false);
    }

    public QuotedPrintableCodec(Charset charset, boolean strict) {
        this.charset = charset;
        this.strict = strict;
    }

    public QuotedPrintableCodec(String charsetName) throws IllegalArgumentException {
        this(Charset.forName(charsetName), false);
    }

    @Override // org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] bytes) throws DecoderException {
        return decodeQuotedPrintable(bytes);
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return decode((byte[]) obj);
        }
        if (obj instanceof String) {
            return decode((String) obj);
        }
        throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable decoded");
    }

    @Override // org.apache.commons.codec.StringDecoder
    public String decode(String sourceStr) throws DecoderException {
        return decode(sourceStr, getCharset());
    }

    public String decode(String sourceStr, Charset sourceCharset) throws DecoderException {
        if (sourceStr == null) {
            return null;
        }
        return new String(decode(StringUtils.getBytesUsAscii(sourceStr)), sourceCharset);
    }

    public String decode(String sourceStr, String sourceCharset) throws DecoderException, UnsupportedEncodingException {
        if (sourceStr == null) {
            return null;
        }
        return new String(decode(StringUtils.getBytesUsAscii(sourceStr)), sourceCharset);
    }

    @Override // org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] bytes) {
        return encodeQuotedPrintable(PRINTABLE_CHARS, bytes, this.strict);
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return encode((byte[]) obj);
        }
        if (obj instanceof String) {
            return encode((String) obj);
        }
        throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable encoded");
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String sourceStr) throws EncoderException {
        return encode(sourceStr, getCharset());
    }

    public String encode(String sourceStr, Charset sourceCharset) {
        if (sourceStr == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(encode(sourceStr.getBytes(sourceCharset)));
    }

    public String encode(String sourceStr, String sourceCharset) throws UnsupportedEncodingException {
        if (sourceStr == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(encode(sourceStr.getBytes(sourceCharset)));
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getDefaultCharset() {
        return this.charset.name();
    }
}
