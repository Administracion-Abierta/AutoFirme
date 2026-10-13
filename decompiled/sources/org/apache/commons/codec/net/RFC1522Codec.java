package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.StringUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/net/RFC1522Codec.class */
abstract class RFC1522Codec {
    protected static final char SEP = '?';
    protected static final String POSTFIX = "?=";
    protected static final String PREFIX = "=?";
    protected final Charset charset;

    protected abstract byte[] doDecoding(byte[] bArr) throws DecoderException;

    protected abstract byte[] doEncoding(byte[] bArr) throws EncoderException;

    protected abstract String getEncoding();

    RFC1522Codec(Charset charset) {
        this.charset = (Charset) Objects.requireNonNull(charset, "charset");
    }

    protected String decodeText(String text) throws DecoderException, UnsupportedEncodingException {
        if (text == null) {
            return null;
        }
        if (!text.startsWith(PREFIX) || !text.endsWith(POSTFIX)) {
            throw new DecoderException("RFC 1522 violation: malformed encoded content");
        }
        int terminator = text.length() - 2;
        int to = text.indexOf(63, 2);
        if (to == terminator) {
            throw new DecoderException("RFC 1522 violation: charset token not found");
        }
        String charset = text.substring(2, to);
        if (charset.isEmpty()) {
            throw new DecoderException("RFC 1522 violation: charset not specified");
        }
        int from = to + 1;
        int to2 = text.indexOf(63, from);
        if (to2 == terminator) {
            throw new DecoderException("RFC 1522 violation: encoding token not found");
        }
        String encoding = text.substring(from, to2);
        if (!getEncoding().equalsIgnoreCase(encoding)) {
            throw new DecoderException("This codec cannot decode " + encoding + " encoded content");
        }
        int from2 = to2 + 1;
        byte[] data = StringUtils.getBytesUsAscii(text.substring(from2, text.indexOf(63, from2)));
        return new String(doDecoding(data), charset);
    }

    protected String encodeText(String text, Charset charset) throws EncoderException {
        if (text == null) {
            return null;
        }
        return PREFIX + charset + '?' + getEncoding() + '?' + StringUtils.newStringUsAscii(doEncoding(text.getBytes(charset))) + POSTFIX;
    }

    protected String encodeText(String text, String charsetName) throws EncoderException {
        if (text == null) {
            return null;
        }
        return encodeText(text, Charset.forName(charsetName));
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getDefaultCharset() {
        return this.charset.name();
    }
}
