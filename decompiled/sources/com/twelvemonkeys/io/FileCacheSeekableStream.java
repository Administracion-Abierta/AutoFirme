package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileCacheSeekableStream.class */
public final class FileCacheSeekableStream extends AbstractCachedSeekableStream {
    private byte[] buffer;

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public /* bridge */ /* synthetic */ int available() throws IOException {
        return super.available();
    }

    public FileCacheSeekableStream(InputStream inputStream) throws IOException {
        this(inputStream, "iocache", null);
    }

    public FileCacheSeekableStream(InputStream inputStream, String str) throws IOException {
        this(inputStream, str, null);
    }

    public FileCacheSeekableStream(InputStream inputStream, String str, File file) throws IOException {
        this((InputStream) Validate.notNull(inputStream, "stream"), createTempFile(str, file));
    }

    static File createTempFile(String str, File file) throws IOException {
        Validate.notNull(str, "tempBaseName");
        File fileCreateTempFile = File.createTempFile(str, null, file);
        fileCreateTempFile.deleteOnExit();
        return fileCreateTempFile;
    }

    FileCacheSeekableStream(InputStream inputStream, File file) throws FileNotFoundException {
        super(inputStream, new FileCache(file));
        this.buffer = new byte[1024];
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, com.twelvemonkeys.io.Seekable
    public final boolean isCachedMemory() {
        return false;
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, com.twelvemonkeys.io.Seekable
    public final boolean isCachedFile() {
        return true;
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, com.twelvemonkeys.io.SeekableInputStream
    protected void closeImpl() throws IOException {
        super.closeImpl();
        this.buffer = null;
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public int read() throws IOException {
        int ahead;
        checkOpen();
        if (this.position == this.streamPosition) {
            ahead = readAhead(this.buffer, 0, this.buffer.length);
            if (ahead >= 0) {
                ahead = this.buffer[0] & 255;
            }
        } else {
            syncPosition();
            ahead = getCache().read();
        }
        if (ahead != -1) {
            this.position++;
        }
        return ahead;
    }

    @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int ahead;
        checkOpen();
        if (this.position == this.streamPosition) {
            ahead = readAhead(bArr, i, i2);
        } else {
            syncPosition();
            ahead = getCache().read(bArr, i, (int) Math.min(i2, this.streamPosition - this.position));
        }
        if (ahead > 0) {
            this.position += (long) ahead;
        }
        return ahead;
    }

    private int readAhead(byte[] bArr, int i, int i2) throws IOException {
        int i3 = this.stream.read(bArr, i, i2);
        if (i3 > 0) {
            this.streamPosition += (long) i3;
            getCache().write(bArr, i, i3);
        }
        return i3;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileCacheSeekableStream$FileCache.class */
    static final class FileCache extends AbstractCachedSeekableStream.StreamCache {
        private RandomAccessFile cacheFile;

        public FileCache(File file) throws FileNotFoundException {
            Validate.notNull(file, "file");
            this.cacheFile = new RandomAccessFile(file, "rw");
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void write(int i) throws IOException {
            this.cacheFile.write(i);
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.cacheFile.write(bArr, i, i2);
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public int read() throws IOException {
            return this.cacheFile.read();
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public int read(byte[] bArr, int i, int i2) throws IOException {
            return this.cacheFile.read(bArr, i, i2);
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public void seek(long j) throws IOException {
            this.cacheFile.seek(j);
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        public long getPosition() throws IOException {
            return this.cacheFile.getFilePointer();
        }

        @Override // com.twelvemonkeys.io.AbstractCachedSeekableStream.StreamCache
        void close() throws IOException {
            this.cacheFile.close();
        }
    }
}
