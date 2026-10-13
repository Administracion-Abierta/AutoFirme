package com.twelvemonkeys.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/SeekableInputStream.class */
public abstract class SeekableInputStream extends InputStream implements Seekable {
    long position;
    long flushedPosition;
    boolean closed;
    protected Stack<Long> markedPositions = new Stack<>();

    protected abstract void seekImpl(long j) throws IOException;

    protected abstract void flushBeforeImpl(long j) throws IOException;

    protected abstract void closeImpl() throws IOException;

    @Override // java.io.InputStream
    public final int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr != null ? bArr.length : 1);
    }

    @Override // java.io.InputStream
    public final long skip(long j) throws IOException {
        long j2 = this.position;
        long j3 = j2 + j;
        if (j3 < this.flushedPosition) {
            throw new IOException("position < flushedPosition");
        }
        int iAvailable = available();
        if (iAvailable > 0) {
            seek(Math.min(j3, j2 + ((long) iAvailable)));
        } else {
            for (int iMax = (int) Math.max(Math.min(j, 512L), -512L); iMax > 0 && read() >= 0; iMax--) {
            }
        }
        return this.position - j2;
    }

    @Override // java.io.InputStream
    public final void mark(int i) {
        mark();
        try {
            flushBefore(Math.max(this.position - ((long) i), this.flushedPosition));
        } catch (IOException e) {
        }
    }

    @Override // java.io.InputStream
    public final boolean markSupported() {
        return true;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void seek(long j) throws IOException {
        checkOpen();
        if (j < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition");
        }
        seekImpl(j);
        this.position = j;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void mark() {
        this.markedPositions.push(Long.valueOf(this.position));
    }

    @Override // java.io.InputStream, com.twelvemonkeys.io.Seekable
    public final void reset() throws IOException {
        checkOpen();
        if (!this.markedPositions.isEmpty()) {
            long jLongValue = this.markedPositions.pop().longValue();
            if (jLongValue < this.flushedPosition) {
                throw new IOException("Previous marked position has been discarded");
            }
            seek(jLongValue);
            return;
        }
        seek(0L);
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void flushBefore(long j) throws IOException {
        if (j < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition");
        }
        if (j > getStreamPosition()) {
            throw new IndexOutOfBoundsException("position > stream position");
        }
        checkOpen();
        flushBeforeImpl(j);
        this.flushedPosition = j;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void flush() throws IOException {
        flushBefore(this.flushedPosition);
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final long getFlushedPosition() throws IOException {
        checkOpen();
        return this.flushedPosition;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final long getStreamPosition() throws IOException {
        checkOpen();
        return this.position;
    }

    protected final void checkOpen() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable, com.twelvemonkeys.io.Seekable
    public final void close() throws IOException {
        checkOpen();
        this.closed = true;
        closeImpl();
    }

    protected void finalize() throws Throwable {
        if (!this.closed) {
            try {
                close();
            } catch (IOException e) {
            }
        }
        super.finalize();
    }
}
