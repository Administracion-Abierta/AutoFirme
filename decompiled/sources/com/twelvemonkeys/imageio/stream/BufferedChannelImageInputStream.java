package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/BufferedChannelImageInputStream.class */
final class BufferedChannelImageInputStream extends ImageInputStreamImpl {
    private static final Closeable CLOSEABLE_STUB = new Closeable() { // from class: com.twelvemonkeys.imageio.stream.BufferedChannelImageInputStream.1
        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    };
    static final int DEFAULT_BUFFER_SIZE = 8192;
    private ByteBuffer byteBuffer;
    private byte[] buffer;
    private int bufferPos;
    private int bufferLimit;
    private final ByteBuffer integralCache;
    private final byte[] integralCacheArray;
    private SeekableByteChannel channel;
    private Closeable closeable;

    public BufferedChannelImageInputStream(File file) throws IOException {
        this(((File) Validate.notNull(file, "file")).toPath());
    }

    public BufferedChannelImageInputStream(Path path) throws IOException {
        this(FileChannel.open((Path) Validate.notNull(path, "file"), StandardOpenOption.READ), true);
    }

    public BufferedChannelImageInputStream(RandomAccessFile randomAccessFile) {
        this(((RandomAccessFile) Validate.notNull(randomAccessFile, "file")).getChannel(), true);
    }

    public BufferedChannelImageInputStream(FileInputStream fileInputStream) {
        this(((FileInputStream) Validate.notNull(fileInputStream, "inputStream")).getChannel(), false);
    }

    public BufferedChannelImageInputStream(SeekableByteChannel seekableByteChannel) {
        this((SeekableByteChannel) Validate.notNull(seekableByteChannel, "channel"), false);
    }

    BufferedChannelImageInputStream(Cache cache) {
        this((SeekableByteChannel) Validate.notNull(cache, "cache"), true);
    }

    private BufferedChannelImageInputStream(SeekableByteChannel seekableByteChannel, boolean z) {
        this.byteBuffer = ByteBuffer.allocate(8192);
        this.buffer = this.byteBuffer.array();
        this.integralCache = ByteBuffer.allocate(8);
        this.integralCacheArray = this.integralCache.array();
        this.channel = (SeekableByteChannel) Validate.notNull(seekableByteChannel, "channel");
        this.closeable = z ? this.channel : CLOSEABLE_STUB;
    }

    private boolean fillBuffer() throws IOException {
        this.byteBuffer.rewind();
        int i = this.channel.read(this.byteBuffer);
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
        int i3;
        this.bufferLimit = 0;
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr, i, i2);
        int i4 = 0;
        while (true) {
            i3 = i4;
            if (!byteBufferWrap.hasRemaining()) {
                break;
            }
            int i5 = this.channel.read(byteBufferWrap);
            if (i5 == -1) {
                if (i3 != 0) {
                    break;
                }
                return -1;
            }
            i4 = i3 + i5;
        }
        this.streamPos += (long) i3;
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
            return this.channel.size();
        } catch (IOException e) {
            return -1L;
        }
    }

    public void close() throws IOException {
        super.close();
        this.buffer = null;
        this.byteBuffer = null;
        this.channel = null;
        try {
            this.closeable.close();
        } finally {
            this.closeable = null;
        }
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
            this.channel.position(j);
        }
        this.streamPos = j;
    }

    public void flushBefore(long j) throws IOException {
        super.flushBefore(j);
        if (this.channel instanceof Cache) {
            ((Cache) this.channel).flushBefore(j);
        }
    }
}
