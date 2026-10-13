package com.twelvemonkeys.io;

import com.sun.jna.platform.win32.WinNT;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/AbstractCachedSeekableStream.class */
abstract class AbstractCachedSeekableStream extends SeekableInputStream {
    protected final InputStream stream;
    protected long streamPosition;
    private StreamCache cache;

    @Override // com.twelvemonkeys.io.Seekable
    public abstract boolean isCachedMemory();

    @Override // com.twelvemonkeys.io.Seekable
    public abstract boolean isCachedFile();

    protected AbstractCachedSeekableStream(InputStream inputStream, StreamCache streamCache) {
        Validate.notNull(inputStream, "stream");
        Validate.notNull(streamCache, "cache");
        this.stream = inputStream;
        this.cache = streamCache;
    }

    protected final StreamCache getCache() {
        return this.cache;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        long jAvailable = (this.streamPosition - this.position) + ((long) this.stream.available());
        return jAvailable > 2147483647L ? WinNT.MAXLONG : (int) jAvailable;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int i;
        checkOpen();
        if (this.position == this.streamPosition) {
            i = this.stream.read();
            if (i >= 0) {
                this.streamPosition++;
                this.cache.write(i);
            }
        } else {
            syncPosition();
            i = this.cache.read();
        }
        if (i != -1) {
            this.position++;
        }
        return i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        checkOpen();
        if (this.position == this.streamPosition) {
            i3 = this.stream.read(bArr, i, i2);
            if (i3 > 0) {
                this.streamPosition += (long) i3;
                this.cache.write(bArr, i, i3);
            }
        } else {
            syncPosition();
            i3 = this.cache.read(bArr, i, i2);
        }
        if (i3 > 0) {
            this.position += (long) i3;
        }
        return i3;
    }

    protected final void syncPosition() throws IOException {
        if (this.cache.getPosition() != this.position) {
            this.cache.seek(this.position);
        }
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final boolean isCached() {
        return true;
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    protected void seekImpl(long j) throws IOException {
        if (this.streamPosition < j) {
            if (this.cache.getPosition() != this.streamPosition) {
                this.cache.seek(this.streamPosition);
            }
            long j2 = j - this.streamPosition;
            byte[] bArr = new byte[j2 > 1024 ? 1024 : (int) j2];
            while (j2 > 0) {
                int i = this.stream.read(bArr, 0, ((long) bArr.length) < j2 ? bArr.length : (int) j2);
                if (i > 0) {
                    this.cache.write(bArr, 0, i);
                    this.streamPosition += (long) i;
                    j2 -= (long) i;
                } else if (i < 0) {
                    return;
                }
            }
            return;
        }
        this.cache.seek(j);
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    protected void flushBeforeImpl(long j) {
        this.cache.flush(j);
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    protected void closeImpl() throws IOException {
        this.cache.close();
        this.cache = null;
        this.stream.close();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/AbstractCachedSeekableStream$StreamCache.class */
    static abstract class StreamCache {
        abstract void write(int i) throws IOException;

        abstract int read() throws IOException;

        abstract void seek(long j) throws IOException;

        abstract long getPosition() throws IOException;

        abstract void close() throws IOException;

        StreamCache() {
        }

        void write(byte[] bArr, int i, int i2) throws IOException {
            for (int i3 = 0; i3 < i2; i3++) {
                write(bArr[i + i3]);
            }
        }

        int read(byte[] bArr, int i, int i2) throws IOException {
            int i3;
            int i4 = 0;
            for (int i5 = 0; i5 < i2 && (i3 = read()) >= 0; i5++) {
                bArr[i + i5] = (byte) i3;
                i4++;
            }
            return i4;
        }

        void flush(long j) {
        }
    }
}
