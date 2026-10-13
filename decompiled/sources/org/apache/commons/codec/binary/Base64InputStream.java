package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Base64InputStream.class */
public class Base64InputStream extends BaseNCodecInputStream {
    public Base64InputStream(InputStream inputStream) {
        this(inputStream, false);
    }

    public Base64InputStream(InputStream inputStream, boolean doEncode) {
        super(inputStream, new Base64(false), doEncode);
    }

    public Base64InputStream(InputStream inputStream, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(inputStream, new Base64(lineLength, lineSeparator), doEncode);
    }

    public Base64InputStream(InputStream inputStream, boolean doEncode, int lineLength, byte[] lineSeparator, CodecPolicy decodingPolicy) {
        super(inputStream, new Base64(lineLength, lineSeparator, false, decodingPolicy), doEncode);
    }
}
