package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/Decoder.class */
public interface Decoder {
    int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException;
}
