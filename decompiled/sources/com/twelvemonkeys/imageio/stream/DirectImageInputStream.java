package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/DirectImageInputStream.class */
public final class DirectImageInputStream extends ImageInputStreamImpl {
    private final InputStream stream;
    private final long length;

    public DirectImageInputStream(InputStream inputStream) {
        this(inputStream, -1L);
    }

    public DirectImageInputStream(InputStream inputStream, long j) {
        this.stream = (InputStream) Validate.notNull(inputStream, "stream");
        this.length = ((Long) Validate.isTrue(j >= 0 || j == -1, Long.valueOf(j), "negative length: %d")).longValue();
    }

    public int read() throws IOException {
        this.bitOffset = 0;
        this.streamPos++;
        return this.stream.read();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        this.bitOffset = 0;
        int i3 = this.stream.read(bArr, i, i2);
        if (i3 > 0) {
            this.streamPos += (long) i3;
        }
        return i3;
    }

    public void seek(long j) throws IOException {
        checkClosed();
        if (j < this.streamPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos");
        }
        this.bitOffset = 0;
        while (this.streamPos < j) {
            long jSkip = this.stream.skip(j - this.streamPos);
            if (jSkip > 0) {
                this.streamPos += jSkip;
            } else {
                return;
            }
        }
    }

    public long getFlushedPosition() {
        return this.streamPos;
    }

    public long length() {
        return this.length;
    }

    public int readBit() throws IOException {
        throw new UnsupportedOperationException("Bit reading not supported");
    }

    public long readBits(int i) throws IOException {
        throw new UnsupportedOperationException("Bit reading not supported");
    }

    public void close() throws IOException {
        this.stream.close();
        super.close();
    }
}
