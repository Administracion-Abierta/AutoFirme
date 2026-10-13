package com.twelvemonkeys.imageio.stream;

import com.sun.jna.platform.win32.WinNT;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/MemoryCache.class */
final class MemoryCache implements Cache {
    static final int BLOCK_SIZE = 8192;
    private static final byte[] NULL_BLOCK = new byte[0];
    private final List<byte[]> cache;
    private final ReadableByteChannel channel;
    private int maxBlock;
    private long length;
    private long position;
    private long start;

    MemoryCache(InputStream inputStream) {
        this(Channels.newChannel((InputStream) Validate.notNull(inputStream, "stream")));
    }

    public MemoryCache(ReadableByteChannel readableByteChannel) {
        this.cache = new ArrayList();
        this.maxBlock = WinNT.MAXLONG;
        this.channel = (ReadableByteChannel) Validate.notNull(readableByteChannel, "channel");
    }

    byte[] fetchBlock() throws IOException {
        long j = this.position / 8192;
        if (j >= 2147483647L) {
            throw new IOException("Memory cache max size exceeded");
        }
        if (j > this.maxBlock) {
            return NULL_BLOCK;
        }
        while (j >= this.cache.size()) {
            try {
                byte[] bArr = new byte[8192];
                this.cache.add(bArr);
                int block = readBlock(bArr);
                this.length += (long) block;
                if (block < 8192) {
                    this.maxBlock = (int) j;
                    return bArr;
                }
            } catch (OutOfMemoryError e) {
                throw new IOException("No more memory for cache: " + (this.cache.size() * 8192));
            }
        }
        return this.cache.get((int) j);
    }

    private int readBlock(byte[] bArr) throws IOException {
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        while (byteBufferWrap.hasRemaining() && this.channel.read(byteBufferWrap) != -1) {
        }
        return byteBufferWrap.position();
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.cache.clear();
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer byteBuffer) throws IOException {
        byte[] bArrFetchBlock = fetchBlock();
        if (this.position >= this.length) {
            return -1;
        }
        int i = (int) (this.position % 8192);
        int iMin = Math.min(byteBuffer.remaining(), (int) Math.min(8192 - i, this.length - this.position));
        byteBuffer.put(bArrFetchBlock, i, iMin);
        this.position += (long) iMin;
        return iMin;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long position() throws IOException {
        return this.position;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel position(long j) throws IOException {
        if (j < this.start) {
            throw new IOException("Seek before flush position");
        }
        this.position = j;
        return this;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        return -1L;
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer byteBuffer) {
        throw new NonWritableChannelException();
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel truncate(long j) {
        throw new NonWritableChannelException();
    }

    @Override // com.twelvemonkeys.imageio.stream.Cache
    public void flushBefore(long j) {
        if (j < this.start) {
            throw new IndexOutOfBoundsException("pos < flushed position");
        }
        if (j > this.position) {
            throw new IndexOutOfBoundsException("pos > current position");
        }
        int i = (int) (j / 8192);
        for (int i2 = 0; i2 < i; i2++) {
            this.cache.set(i2, null);
        }
        this.start = j;
    }
}
