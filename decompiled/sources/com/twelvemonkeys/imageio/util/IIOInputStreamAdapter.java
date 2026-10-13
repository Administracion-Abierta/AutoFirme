package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/IIOInputStreamAdapter.class */
class IIOInputStreamAdapter extends InputStream {
    private ImageInputStream input;
    private final boolean hasLength;
    private long left;
    private long markPosition;

    public IIOInputStreamAdapter(ImageInputStream imageInputStream) {
        this(imageInputStream, -1L, false);
    }

    public IIOInputStreamAdapter(ImageInputStream imageInputStream, long j) {
        this(imageInputStream, j, true);
    }

    private IIOInputStreamAdapter(ImageInputStream imageInputStream, long j, boolean z) {
        Validate.notNull(imageInputStream, "stream");
        Validate.isTrue(!z || j >= 0, Long.valueOf(j), "length < 0: %d");
        this.input = imageInputStream;
        this.left = j;
        this.hasLength = z;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.hasLength) {
            this.input.seek(this.input.getStreamPosition() + this.left);
        }
        this.left = 0L;
        this.input = null;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        if (!this.hasLength || this.left <= 0) {
            return 0;
        }
        return (int) Math.min(2147483647L, this.left);
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.InputStream
    public void mark(int i) {
        try {
            this.markPosition = this.input.getStreamPosition();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read stream position: " + e.getMessage(), e);
        }
    }

    @Override // java.io.InputStream
    public void reset() throws IOException {
        long streamPosition = this.input.getStreamPosition() - this.markPosition;
        this.input.seek(this.markPosition);
        this.left += streamPosition;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.hasLength) {
            long j = this.left;
            this.left = j - 1;
            if (j <= 0) {
                this.left = 0L;
                return -1;
            }
        }
        return this.input.read();
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.hasLength && this.left <= 0) {
            return -1;
        }
        int i3 = this.input.read(bArr, i, (int) findMaxLen(i2));
        if (this.hasLength) {
            this.left = i3 < 0 ? 0L : this.left - ((long) i3);
        }
        return i3;
    }

    private long findMaxLen(long j) {
        if (this.hasLength && this.left < j) {
            return Math.max(this.left, 0L);
        }
        return Math.max(j, 0L);
    }

    @Override // java.io.InputStream
    public long skip(long j) throws IOException {
        long jSkipBytes = this.input.skipBytes(findMaxLen(j));
        this.left -= jSkipBytes;
        return jSkipBytes;
    }
}
