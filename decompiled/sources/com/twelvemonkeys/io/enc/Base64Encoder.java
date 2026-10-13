package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/Base64Encoder.class */
public class Base64Encoder implements Encoder {
    @Override // com.twelvemonkeys.io.enc.Encoder
    public void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        while (byteBuffer.hasRemaining()) {
            switch (Math.min(3, byteBuffer.remaining())) {
                case 1:
                    byte b = byteBuffer.get();
                    outputStream.write(Base64Decoder.PEM_ARRAY[(b >>> 2) & 63]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[((b << 4) & 48) + ((0 >>> 4) & 15)]);
                    outputStream.write(61);
                    outputStream.write(61);
                    break;
                case 2:
                    byte b2 = byteBuffer.get();
                    byte b3 = byteBuffer.get();
                    outputStream.write(Base64Decoder.PEM_ARRAY[(b2 >>> 2) & 63]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[((b2 << 4) & 48) + ((b3 >>> 4) & 15)]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[((b3 << 2) & 60) + ((0 >>> 6) & 3)]);
                    outputStream.write(61);
                    break;
                default:
                    byte b4 = byteBuffer.get();
                    byte b5 = byteBuffer.get();
                    byte b6 = byteBuffer.get();
                    outputStream.write(Base64Decoder.PEM_ARRAY[(b4 >>> 2) & 63]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[((b4 << 4) & 48) + ((b5 >>> 4) & 15)]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[((b5 << 2) & 60) + ((b6 >>> 6) & 3)]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[b6 & 63]);
                    break;
            }
        }
    }
}
