package com.twelvemonkeys.io.enc;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/EncoderStream.class */
public final class EncoderStream extends FilterOutputStream {
    private final Encoder encoder;
    private final boolean flushOnWrite;
    private final ByteBuffer buffer;

    public EncoderStream(OutputStream outputStream, Encoder encoder) {
        this(outputStream, encoder, false);
    }

    public EncoderStream(OutputStream outputStream, Encoder encoder, boolean z) {
        super(outputStream);
        this.encoder = encoder;
        this.flushOnWrite = z;
        this.buffer = ByteBuffer.allocate(1024);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        flush();
        super.close();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        encodeBuffer();
        super.flush();
    }

    private void encodeBuffer() throws IOException {
        if (this.buffer.position() != 0) {
            this.buffer.flip();
            this.encoder.encode(this.out, this.buffer);
            this.buffer.clear();
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (!this.flushOnWrite && i2 < this.buffer.remaining()) {
            this.buffer.put(bArr, i, i2);
        } else {
            encodeBuffer();
            this.encoder.encode(this.out, ByteBuffer.wrap(bArr, i, i2));
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        if (!this.buffer.hasRemaining()) {
            encodeBuffer();
        }
        this.buffer.put((byte) i);
    }
}
