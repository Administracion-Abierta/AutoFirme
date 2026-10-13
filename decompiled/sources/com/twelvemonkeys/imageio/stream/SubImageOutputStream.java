package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/SubImageOutputStream.class */
public class SubImageOutputStream extends ImageOutputStreamImpl {
    private final ImageOutputStream stream;
    private final long startPos;

    public SubImageOutputStream(ImageOutputStream imageOutputStream) throws IOException {
        this.stream = (ImageOutputStream) Validate.notNull(imageOutputStream, "stream");
        this.startPos = imageOutputStream.getStreamPosition();
    }

    public void seek(long j) throws IOException {
        super.seek(j);
        this.stream.seek(this.startPos + j);
    }

    public void write(int i) throws IOException {
        flushBits();
        this.stream.write(i);
        this.streamPos++;
    }

    public void write(byte[] bArr, int i, int i2) throws IOException {
        flushBits();
        this.stream.write(bArr, i, i2);
        this.streamPos += (long) i2;
    }

    public int read() throws IOException {
        this.bitOffset = 0;
        this.streamPos++;
        return this.stream.read();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        this.bitOffset = 0;
        int i3 = this.stream.read(bArr, i, i2);
        this.streamPos += (long) i3;
        return i3;
    }

    public boolean isCached() {
        return this.stream.isCached();
    }

    public boolean isCachedMemory() {
        return this.stream.isCachedMemory();
    }

    public boolean isCachedFile() {
        return this.stream.isCachedFile();
    }
}
