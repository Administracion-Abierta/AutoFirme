package com.twelvemonkeys.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/MemoryCacheSeekableStream.class */
public final class MemoryCacheSeekableStream extends AbstractCachedSeekableStream {
    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public /* bridge */ /* synthetic */ int read(byte[] bArr, int i, int i2) throws IOException {
        return super.read(bArr, i, i2);
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public /* bridge */ /* synthetic */ int available() throws IOException {
        return super.available();
    }

    public MemoryCacheSeekableStream(InputStream inputStream) {
        super(inputStream, new MemoryCache());
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, com.twelvemonkeys.io.Seekable
    public final boolean isCachedMemory() {
        return true;
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, com.twelvemonkeys.io.Seekable
    public final boolean isCachedFile() {
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/MemoryCacheSeekableStream$MemoryCache.class */
    static final class MemoryCache extends AbstractCachedSeekableStream.StreamCache {
        static final int BLOCK_SIZE = 8192;
        private final List<byte[]> cache = new ArrayList();
        private long length;
        private long position;
        private long start;

        MemoryCache() {
        }

        private byte[] getBlock() throws IOException {
            long j = this.position - this.start;
            if (j < 0) {
                throw new IOException("StreamCache flushed before read position");
            }
            long j2 = j / 8192;
            if (j2 >= 2147483647L) {
                throw new IOException("Memory cache max size exceeded");
            }
            if (j2 >= this.cache.size()) {
                try {
                    this.cache.add(new byte[8192]);
                } catch (OutOfMemoryError e) {
                    throw new IOException("No more memory for cache: " + (this.cache.size() * 8192));
                }
            }
            return this.cache.get((int) j2);
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void write(int i) throws IOException {
            getBlock()[(int) (this.position % 8192)] = (byte) i;
            this.position++;
            if (this.position > this.length) {
                this.length = this.position;
            }
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void write(byte[] bArr, int i, int i2) throws IOException {
            byte[] block = getBlock();
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = ((int) this.position) % 8192;
                if (i4 == 0) {
                    block = getBlock();
                }
                block[i4] = bArr[i + i3];
                this.position++;
            }
            if (this.position > this.length) {
                this.length = this.position;
            }
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public int read() throws IOException {
            if (this.position >= this.length) {
                return -1;
            }
            byte[] block = getBlock();
            int i = (int) (this.position % 8192);
            this.position++;
            return block[i] & 255;
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (this.position >= this.length) {
                return -1;
            }
            byte[] block = getBlock();
            int i3 = (int) (this.position % 8192);
            int iMin = (int) Math.min(Math.min(i2, block.length - i3), this.length - this.position);
            int i4 = 0;
            while (i4 < iMin) {
                bArr[i + i4] = block[i3 + i4];
                i4++;
            }
            this.position += (long) i4;
            return i4;
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void seek(long j) throws IOException {
            if (j < this.start) {
                throw new IOException("Seek before flush position");
            }
            this.position = j;
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void flush(long j) {
            int i = ((int) (j / 8192)) - 1;
            for (int i2 = 0; i2 < i; i2++) {
                this.cache.remove(0);
            }
            this.start = j;
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        void close() throws IOException {
            this.cache.clear();
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public long getPosition() {
            return this.position;
        }
    }
}
