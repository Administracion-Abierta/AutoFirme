package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/MergedReader.class */
public final class MergedReader extends Reader {
    final ReaderConfig mConfig;
    final Reader mIn;
    char[] mData;
    int mPtr;
    final int mEnd;

    public MergedReader(ReaderConfig cfg, Reader in, char[] buf, int start, int end) {
        this.mConfig = cfg;
        this.mIn = in;
        this.mData = buf;
        this.mPtr = start;
        this.mEnd = end;
        if (buf != null && start >= end) {
            throw new IllegalArgumentException("Trying to construct MergedReader with empty contents (start " + start + ", end " + end + ")");
        }
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        freeMergedBuffer();
        this.mIn.close();
    }

    @Override // java.io.Reader
    public void mark(int readlimit) throws IOException {
        if (this.mData == null) {
            this.mIn.mark(readlimit);
        }
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return this.mData == null && this.mIn.markSupported();
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.mData != null) {
            char[] cArr = this.mData;
            int i = this.mPtr;
            this.mPtr = i + 1;
            int c = cArr[i] & 255;
            if (this.mPtr >= this.mEnd) {
                freeMergedBuffer();
            }
            return c;
        }
        return this.mIn.read();
    }

    @Override // java.io.Reader
    public int read(char[] cbuf) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.mData != null) {
            int avail = this.mEnd - this.mPtr;
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(this.mData, this.mPtr, cbuf, off, len);
            this.mPtr += len;
            if (this.mPtr >= this.mEnd) {
                freeMergedBuffer();
            }
            return len;
        }
        return this.mIn.read(cbuf, off, len);
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        return this.mData != null || this.mIn.ready();
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        if (this.mData == null) {
            this.mIn.reset();
        }
    }

    @Override // java.io.Reader
    public long skip(long n) throws IOException {
        long count = 0;
        if (this.mData != null) {
            int amount = this.mEnd - this.mPtr;
            if (amount > n) {
                this.mPtr += (int) n;
                return amount;
            }
            freeMergedBuffer();
            count = 0 + ((long) amount);
            n -= (long) amount;
        }
        if (n > 0) {
            count += this.mIn.skip(n);
        }
        return count;
    }

    private void freeMergedBuffer() {
        if (this.mData != null) {
            char[] data = this.mData;
            this.mData = null;
            if (this.mConfig != null) {
                this.mConfig.freeSmallCBuffer(data);
            }
        }
    }
}
