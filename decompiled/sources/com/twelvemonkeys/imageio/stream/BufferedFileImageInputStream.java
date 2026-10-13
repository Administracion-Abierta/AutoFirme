package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStreamImpl;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedFileImageInputStream.class */
@Deprecated
public final class BufferedFileImageInputStream extends ImageInputStreamImpl {
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private byte[] buffer;
    private int bufferPos;
    private int bufferLimit;
    private final ByteBuffer integralCache;
    private final byte[] integralCacheArray;
    private RandomAccessFile raf;

    public BufferedFileImageInputStream(File file) throws FileNotFoundException {
        this(new RandomAccessFile((File) Validate.notNull(file, "file"), PDPageLabelRange.STYLE_ROMAN_LOWER));
    }

    public BufferedFileImageInputStream(RandomAccessFile randomAccessFile) {
        this.buffer = new byte[8192];
        this.integralCache = ByteBuffer.allocate(8);
        this.integralCacheArray = this.integralCache.array();
        this.raf = (RandomAccessFile) Validate.notNull(randomAccessFile, "raf");
    }

    private boolean fillBuffer() throws IOException {
        int i = this.raf.read(this.buffer, 0, this.buffer.length);
        this.bufferPos = 0;
        this.bufferLimit = Math.max(i, 0);
        return this.bufferLimit > 0;
    }

    private boolean bufferEmpty() {
        return this.bufferPos >= this.bufferLimit;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        super.setByteOrder(byteOrder);
        this.integralCache.order(byteOrder);
    }

    public int read() throws IOException {
        checkClosed();
        if (bufferEmpty() && !fillBuffer()) {
            return -1;
        }
        this.bitOffset = 0;
        this.streamPos++;
        byte[] bArr = this.buffer;
        int i = this.bufferPos;
        this.bufferPos = i + 1;
        return bArr[i] & 255;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        checkClosed();
        this.bitOffset = 0;
        if (bufferEmpty()) {
            if (i2 >= this.buffer.length) {
                return readDirect(bArr, i, i2);
            }
            if (!fillBuffer()) {
                return -1;
            }
        }
        int buffered = readBuffered(bArr, i, i2);
        if (i2 > buffered) {
            return buffered + Math.max(0, readDirect(bArr, i + buffered, i2 - buffered));
        }
        return buffered;
    }

    private int readDirect(byte[] bArr, int i, int i2) throws IOException {
        this.bufferLimit = 0;
        int i3 = this.raf.read(bArr, i, i2);
        if (i3 > 0) {
            this.streamPos += (long) i3;
        }
        return i3;
    }

    private int readBuffered(byte[] bArr, int i, int i2) {
        int iMin = Math.min(this.bufferLimit - this.bufferPos, i2);
        if (iMin > 0) {
            System.arraycopy(this.buffer, this.bufferPos, bArr, i, iMin);
            this.bufferPos += iMin;
            this.streamPos += (long) iMin;
        }
        return iMin;
    }

    public long length() {
        try {
            checkClosed();
            return this.raf.length();
        } catch (IOException e) {
            return -1L;
        }
    }

    public void close() throws IOException {
        super.close();
        this.buffer = null;
        this.raf.close();
        this.raf = null;
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

    public void seek(long j) throws IOException {
        checkClosed();
        if (j < this.flushedPos) {
            throw new IndexOutOfBoundsException("position < flushedPos!");
        }
        this.bitOffset = 0;
        if (this.streamPos == j) {
            return;
        }
        long j2 = (((long) this.bufferPos) + j) - this.streamPos;
        if (j2 >= 0 && j2 < this.bufferLimit) {
            this.bufferPos = (int) j2;
        } else {
            this.bufferLimit = 0;
            this.raf.seek(j);
        }
        this.streamPos = j;
    }
}
