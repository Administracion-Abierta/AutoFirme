package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedImageInputStream.class */
@Deprecated
public final class BufferedImageInputStream extends ImageInputStreamImpl implements ImageInputStream {
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private ImageInputStream stream;
    private ByteBuffer buffer;
    private final ByteBuffer integralCache;
    private final byte[] integralCacheArray;

    public BufferedImageInputStream(ImageInputStream imageInputStream) throws IOException {
        this(imageInputStream, 8192);
    }

    private BufferedImageInputStream(ImageInputStream imageInputStream, int i) throws IOException {
        this.integralCache = ByteBuffer.allocate(8);
        this.integralCacheArray = this.integralCache.array();
        this.stream = (ImageInputStream) Validate.notNull(imageInputStream, "stream");
        this.streamPos = imageInputStream.getStreamPosition();
        this.buffer = ByteBuffer.allocate(i);
        this.buffer.limit(0);
    }

    private void fillBuffer() throws IOException {
        this.buffer.clear();
        int i = this.stream.read(this.buffer.array(), 0, this.buffer.capacity());
        if (i >= 0) {
            this.buffer.position(i);
            this.buffer.flip();
        } else {
            this.buffer.limit(0);
        }
    }

    public void setByteOrder(ByteOrder byteOrder) {
        super.setByteOrder(byteOrder);
        this.integralCache.order(byteOrder);
    }

    public int read() throws IOException {
        checkClosed();
        if (!this.buffer.hasRemaining()) {
            fillBuffer();
            if (!this.buffer.hasRemaining()) {
                return -1;
            }
        }
        this.bitOffset = 0;
        this.streamPos++;
        return this.buffer.get() & 255;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        checkClosed();
        this.bitOffset = 0;
        if (!this.buffer.hasRemaining()) {
            if (i2 >= this.buffer.capacity()) {
                return readDirect(bArr, i, i2);
            }
            fillBuffer();
        }
        return readBuffered(bArr, i, i2);
    }

    private int readDirect(byte[] bArr, int i, int i2) throws IOException {
        this.buffer.limit(0);
        int i3 = this.stream.read(bArr, i, i2);
        if (i3 > 0) {
            this.streamPos += (long) i3;
        }
        return i3;
    }

    private int readBuffered(byte[] bArr, int i, int i2) {
        if (!this.buffer.hasRemaining()) {
            return -1;
        }
        int iMin = Math.min(this.buffer.remaining(), i2);
        if (iMin > 0) {
            int iPosition = this.buffer.position();
            System.arraycopy(this.buffer.array(), iPosition, bArr, i, iMin);
            this.buffer.position(iPosition + iMin);
        }
        this.streamPos += (long) iMin;
        return iMin;
    }

    public short readShort() throws IOException {
        readFully(this.integralCacheArray, 0, 2);
        return this.integralCache.getShort(0);
    }

    public int readInt() throws IOException {
        readFully(this.integralCacheArray, 0, 4);
        return this.integralCache.getInt(0);
    }

    public long readLong() throws IOException {
        readFully(this.integralCacheArray, 0, 8);
        return this.integralCache.getLong(0);
    }

    public int readBit() throws IOException {
        checkClosed();
        if (!this.buffer.hasRemaining()) {
            fillBuffer();
            if (!this.buffer.hasRemaining()) {
                throw new EOFException();
            }
        }
        int i = (this.bitOffset + 1) & 7;
        int i2 = this.buffer.get() & 255;
        if (i != 0) {
            this.buffer.position(this.buffer.position() - 1);
            i2 >>= 8 - i;
        } else {
            this.streamPos++;
        }
        this.bitOffset = i;
        return i2 & 1;
    }

    public long readBits(int i) throws IOException {
        checkClosed();
        if (i < 0 || i > 64) {
            throw new IllegalArgumentException();
        }
        if (i == 0) {
            return 0L;
        }
        int i2 = i + this.bitOffset;
        int i3 = (this.bitOffset + i) & 7;
        long j = 0;
        while (i2 > 0) {
            if (!this.buffer.hasRemaining()) {
                fillBuffer();
                if (!this.buffer.hasRemaining()) {
                    throw new EOFException();
                }
            }
            int i4 = this.buffer.get() & 255;
            this.streamPos++;
            j = (j << 8) | ((long) i4);
            i2 -= 8;
        }
        if (i3 != 0) {
            this.buffer.position(this.buffer.position() - 1);
            this.streamPos--;
        }
        this.bitOffset = i3;
        return (j >>> (-i2)) & ((-1) >>> (64 - i));
    }

    public void seek(long j) throws IOException {
        checkClosed();
        this.bitOffset = 0;
        if (this.streamPos == j) {
            return;
        }
        long jPosition = (((long) this.buffer.position()) + j) - this.streamPos;
        if (jPosition >= 0 && jPosition <= this.buffer.limit()) {
            this.buffer.position((int) jPosition);
        } else {
            this.buffer.limit(0);
            this.stream.seek(j);
        }
        this.streamPos = j;
    }

    public void flushBefore(long j) throws IOException {
        checkClosed();
        this.stream.flushBefore(j);
    }

    public long getFlushedPosition() {
        return this.stream.getFlushedPosition();
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

    public void close() throws IOException {
        if (this.stream != null) {
            this.stream = null;
            this.buffer = null;
        }
        super.close();
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public long length() {
        try {
            return this.stream.length();
        } catch (IOException e) {
            return -1L;
        }
    }
}
