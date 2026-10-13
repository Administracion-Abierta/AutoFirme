package com.twelvemonkeys.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/SeekableOutputStream.class */
public abstract class SeekableOutputStream extends OutputStream implements Seekable {
    long position;
    long flushedPosition;
    boolean closed;
    protected Stack<Long> markedPositions = new Stack<>();

    protected abstract void seekImpl(long j) throws IOException;

    protected abstract void flushBeforeImpl(long j) throws IOException;

    protected abstract void closeImpl() throws IOException;

    @Override // java.io.OutputStream
    public final void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr != null ? bArr.length : 1);
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void seek(long j) throws IOException {
        checkOpen();
        if (j < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition!");
        }
        seekImpl(j);
        this.position = j;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void mark() {
        this.markedPositions.push(Long.valueOf(this.position));
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void reset() throws IOException {
        checkOpen();
        if (!this.markedPositions.isEmpty()) {
            long jLongValue = this.markedPositions.pop().longValue();
            if (jLongValue < this.flushedPosition) {
                throw new IOException("Previous marked position has been discarded!");
            }
            seek(jLongValue);
        }
    }

    @Override // com.twelvemonkeys.io.Seekable
    public final void flushBefore(long j) throws IOException {
        if (j < this.flushedPosition) {
            throw new IndexOutOfBoundsException("position < flushedPosition!");
        }
        if (j > getStreamPosition()) {
            throw new IndexOutOfBoundsException("position > getStreamPosition()!");
        }
        checkOpen();
        flushBeforeImpl(j);
        this.flushedPosition = j;
    }

    @Override // java.io.OutputStream, java.io.Flushable, com.twelvemonkeys.io.Seekable
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

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable, com.twelvemonkeys.io.Seekable
    public final void close() throws IOException {
        checkOpen();
        this.closed = true;
        closeImpl();
    }
}
