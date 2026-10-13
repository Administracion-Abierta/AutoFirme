package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/Base32InputStream.class */
public class Base32InputStream extends BaseNCodecInputStream {
    public Base32InputStream(InputStream inputStream) {
        this(inputStream, false);
    }

    public Base32InputStream(InputStream inputStream, boolean doEncode) {
        super(inputStream, new Base32(false), doEncode);
    }

    public Base32InputStream(InputStream inputStream, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(inputStream, new Base32(lineLength, lineSeparator), doEncode);
    }

    public Base32InputStream(InputStream inputStream, boolean doEncode, int lineLength, byte[] lineSeparator, CodecPolicy decodingPolicy) {
        super(inputStream, new Base32(lineLength, lineSeparator, false, (byte) 61, decodingPolicy), doEncode);
    }
}
