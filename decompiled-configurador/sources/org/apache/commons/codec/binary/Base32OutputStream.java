package org.apache.commons.codec.binary;

import java.io.OutputStream;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/Base32OutputStream.class */
public class Base32OutputStream extends BaseNCodecOutputStream {
    public Base32OutputStream(OutputStream outputStream) {
        this(outputStream, true);
    }

    public Base32OutputStream(OutputStream outputStream, boolean doEncode) {
        super(outputStream, new Base32(false), doEncode);
    }

    public Base32OutputStream(OutputStream outputStream, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(outputStream, new Base32(lineLength, lineSeparator), doEncode);
    }

    public Base32OutputStream(OutputStream outputStream, boolean doEncode, int lineLength, byte[] lineSeparator, CodecPolicy decodingPolicy) {
        super(outputStream, new Base32(lineLength, lineSeparator, false, (byte) 61, decodingPolicy), doEncode);
    }
}
