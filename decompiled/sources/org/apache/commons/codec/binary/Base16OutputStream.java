package org.apache.commons.codec.binary;

import java.io.OutputStream;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Base16OutputStream.class */
public class Base16OutputStream extends BaseNCodecOutputStream {
    public Base16OutputStream(OutputStream outputStream) {
        this(outputStream, true);
    }

    public Base16OutputStream(OutputStream outputStream, boolean doEncode) {
        this(outputStream, doEncode, false);
    }

    public Base16OutputStream(OutputStream outputStream, boolean doEncode, boolean lowerCase) {
        this(outputStream, doEncode, lowerCase, CodecPolicy.LENIENT);
    }

    public Base16OutputStream(OutputStream outputStream, boolean doEncode, boolean lowerCase, CodecPolicy decodingPolicy) {
        super(outputStream, new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
