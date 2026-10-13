package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/Encoder.class */
public interface Encoder {
    void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException;
}
