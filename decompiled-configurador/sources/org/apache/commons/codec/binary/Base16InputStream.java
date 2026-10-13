package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/Base16InputStream.class */
public class Base16InputStream extends BaseNCodecInputStream {
    public Base16InputStream(InputStream inputStream) {
        this(inputStream, false);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode) {
        this(inputStream, doEncode, false);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode, boolean lowerCase) {
        this(inputStream, doEncode, lowerCase, CodecPolicy.LENIENT);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode, boolean lowerCase, CodecPolicy decodingPolicy) {
        super(inputStream, new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
