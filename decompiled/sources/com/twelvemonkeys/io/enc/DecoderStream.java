package com.twelvemonkeys.io.enc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/DecoderStream.class */
public final class DecoderStream extends FilterInputStream {
    private final ByteBuffer buffer;
    private final Decoder decoder;

    public DecoderStream(InputStream inputStream, Decoder decoder) {
        this(inputStream, decoder, 1024);
    }

    public DecoderStream(InputStream inputStream, Decoder decoder, int i) {
        super(inputStream);
        this.decoder = decoder;
        this.buffer = ByteBuffer.allocate(i);
        this.buffer.flip();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return this.buffer.remaining();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (!this.buffer.hasRemaining() && fill() < 0) {
            return -1;
        }
        return this.buffer.get() & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        if (i < 0 || i > bArr.length || i2 < 0 || i + i2 > bArr.length || i + i2 < 0) {
            throw new IndexOutOfBoundsException("bytes.length=" + bArr.length + " offset=" + i + " length=" + i2);
        }
        if (i2 == 0) {
            return 0;
        }
        if (!this.buffer.hasRemaining() && fill() < 0) {
            return -1;
        }
        int i3 = 0;
        int i4 = i;
        while (i2 > i3 && (this.buffer.hasRemaining() || fill() >= 0)) {
            int iMin = Math.min(i2 - i3, this.buffer.remaining());
            this.buffer.get(bArr, i4, iMin);
            i4 += iMin;
            i3 += iMin;
        }
        return i3;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long j) throws IOException {
        long j2;
        if (!this.buffer.hasRemaining() && fill() < 0) {
            return 0L;
        }
        long j3 = 0;
        while (true) {
            j2 = j3;
            if (j2 >= j || (!this.buffer.hasRemaining() && fill() < 0)) {
                break;
            }
            int iMin = (int) Math.min(j - j2, this.buffer.remaining());
            this.buffer.position(this.buffer.position() + iMin);
            j3 = j2 + ((long) iMin);
        }
        return j2;
    }

    private int fill() throws IOException {
        this.buffer.clear();
        int iDecode = this.decoder.decode(this.in, this.buffer);
        if (iDecode > this.buffer.capacity()) {
            throw new AssertionError(String.format("Decode beyond buffer (%d): %d (using %s decoder)", Integer.valueOf(this.buffer.capacity()), Integer.valueOf(iDecode), this.decoder.getClass().getName()));
        }
        this.buffer.flip();
        if (iDecode == 0) {
            return -1;
        }
        return iDecode;
    }
}
