package com.twelvemonkeys.io.enc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/PackBitsDecoder.class */
public final class PackBitsDecoder implements Decoder {
    private final boolean disableNoOp;
    private final byte[] sample;
    private boolean reachedEOF;

    public PackBitsDecoder() {
        this(1, false);
    }

    public PackBitsDecoder(boolean z) {
        this(1, z);
    }

    public PackBitsDecoder(int i, boolean z) {
        this.sample = new byte[i];
        this.disableNoOp = z;
    }

    @Override // com.twelvemonkeys.io.enc.Decoder
    public int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        if (this.reachedEOF) {
            return -1;
        }
        int i = inputStream.read();
        if (i < 0) {
            this.reachedEOF = true;
            return 0;
        }
        byte b = (byte) i;
        try {
            if (b >= 0) {
                readFully(inputStream, byteBuffer, this.sample.length * (b + 1));
            } else if (this.disableNoOp || b != -128) {
                for (int i2 = 0; i2 < this.sample.length; i2++) {
                    this.sample[i2] = readByte(inputStream);
                }
                for (int i3 = (-b) + 1; i3 > 0; i3--) {
                    byteBuffer.put(this.sample);
                }
            }
            return byteBuffer.position();
        } catch (IndexOutOfBoundsException e) {
            throw new DecodeException("Error in PackBits decompression, data seems corrupt", e);
        }
    }

    static byte readByte(InputStream inputStream) throws IOException {
        int i = inputStream.read();
        if (i < 0) {
            throw new EOFException("Unexpected end of PackBits stream");
        }
        return (byte) i;
    }

    static void readFully(InputStream inputStream, ByteBuffer byteBuffer, int i) throws IOException {
        if (i < 0) {
            throw new IndexOutOfBoundsException(String.format("Negative length: %d", Integer.valueOf(i)));
        }
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < i) {
                int i4 = inputStream.read(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position() + i3, i - i3);
                if (i4 < 0) {
                    throw new EOFException("Unexpected end of PackBits stream");
                }
                i2 = i3 + i4;
            } else {
                byteBuffer.position(byteBuffer.position() + i3);
                return;
            }
        }
    }
}
