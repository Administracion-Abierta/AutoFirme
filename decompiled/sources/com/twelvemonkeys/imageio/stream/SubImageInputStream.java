package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/SubImageInputStream.class */
public final class SubImageInputStream extends ImageInputStreamImpl {
    private final ImageInputStream stream;
    private final long startPos;
    private final long length;

    public SubImageInputStream(ImageInputStream imageInputStream, long j) throws IOException {
        Validate.notNull(imageInputStream, "stream");
        Validate.isTrue(j >= 0, Long.valueOf(j), "length < 0: %d");
        this.stream = imageInputStream;
        this.startPos = imageInputStream.getStreamPosition();
        this.length = j;
    }

    public int read() throws IOException {
        if (this.streamPos >= this.length) {
            return -1;
        }
        int i = this.stream.read();
        if (i >= 0) {
            this.streamPos++;
        }
        return i;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.streamPos >= this.length) {
            return -1;
        }
        int i3 = this.stream.read(bArr, i, (int) Math.min(i2, this.length - this.streamPos));
        if (i3 >= 0) {
            this.streamPos += (long) i3;
        }
        return i3;
    }

    public long length() {
        try {
            long length = this.stream.length();
            if (length < 0) {
                return -1L;
            }
            return Math.min(length - this.startPos, this.length);
        } catch (IOException e) {
            return -1L;
        }
    }

    public void seek(long j) throws IOException {
        if (j < getFlushedPosition()) {
            throw new IndexOutOfBoundsException("pos < flushedPosition");
        }
        this.stream.seek(this.startPos + j);
        this.streamPos = j;
    }

    protected void finalize() {
    }
}
