package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/SubStream.class */
public final class SubStream extends FilterInputStream {
    private long bytesLeft;
    private int markLimit;

    public SubStream(InputStream inputStream, long j) {
        super((InputStream) Validate.notNull(inputStream, "stream"));
        this.bytesLeft = ((Long) Validate.isTrue(j >= 0, Long.valueOf(j), "length < 0: %s")).longValue();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        while (this.bytesLeft > 0) {
            if (skip(this.bytesLeft) <= 0 && read() < 0) {
                return;
            }
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return (int) findMaxLen(super.available());
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int i) {
        super.mark(i);
        this.markLimit = i;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
        super.reset();
        this.bytesLeft += (long) this.markLimit;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        long j = this.bytesLeft;
        this.bytesLeft = j - 1;
        if (j <= 0) {
            return -1;
        }
        return super.read();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.bytesLeft <= 0) {
            return -1;
        }
        int i3 = super.read(bArr, i, (int) findMaxLen(i2));
        this.bytesLeft = i3 < 0 ? 0L : this.bytesLeft - ((long) i3);
        return i3;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long j) throws IOException {
        long jSkip = super.skip(findMaxLen(j));
        this.bytesLeft -= jSkip;
        return jSkip;
    }

    private long findMaxLen(long j) {
        return this.bytesLeft < j ? Math.max(this.bytesLeft, 0L) : j;
    }
}
