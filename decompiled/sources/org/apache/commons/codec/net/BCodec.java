package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/net/BCodec.class */
public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder {
    private static final CodecPolicy DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;
    private final CodecPolicy decodingPolicy;

    @Override // org.apache.commons.codec.net.RFC1522Codec
    public /* bridge */ /* synthetic */ String getDefaultCharset() {
        return super.getDefaultCharset();
    }

    @Override // org.apache.commons.codec.net.RFC1522Codec
    public /* bridge */ /* synthetic */ Charset getCharset() {
        return super.getCharset();
    }

    public BCodec() {
        this(StandardCharsets.UTF_8);
    }

    public BCodec(Charset charset) {
        this(charset, DECODING_POLICY_DEFAULT);
    }

    public BCodec(Charset charset, CodecPolicy decodingPolicy) {
        super(charset);
        this.decodingPolicy = decodingPolicy;
    }

    public BCodec(String charsetName) {
        this(Charset.forName(charsetName));
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object value) throws DecoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return decode((String) value);
        }
        throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
    }

    @Override // org.apache.commons.codec.StringDecoder
    public String decode(String value) throws DecoderException {
        try {
            return decodeText(value);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }

    @Override // org.apache.commons.codec.net.RFC1522Codec
    protected byte[] doDecoding(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new Base64(0, BaseNCodec.getChunkSeparator(), false, this.decodingPolicy).decode(bytes);
    }

    @Override // org.apache.commons.codec.net.RFC1522Codec
    protected byte[] doEncoding(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.encodeBase64(bytes);
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object value) throws EncoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return encode((String) value);
        }
        throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String strSource) throws EncoderException {
        return encode(strSource, getCharset());
    }

    public String encode(String strSource, Charset sourceCharset) throws EncoderException {
        return encodeText(strSource, sourceCharset);
    }

    public String encode(String strSource, String sourceCharset) throws EncoderException {
        try {
            return encodeText(strSource, sourceCharset);
        } catch (UnsupportedCharsetException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }

    @Override // org.apache.commons.codec.net.RFC1522Codec
    protected String getEncoding() {
        return "B";
    }

    public boolean isStrictDecoding() {
        return this.decodingPolicy == CodecPolicy.STRICT;
    }
}
