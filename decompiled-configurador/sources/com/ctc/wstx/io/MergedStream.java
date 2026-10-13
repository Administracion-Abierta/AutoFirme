package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/MergedStream.class */
public final class MergedStream extends InputStream {
    private final ReaderConfig mConfig;
    private final InputStream mIn;
    private byte[] mData;
    private int mPtr;
    private final int mEnd;

    public MergedStream(ReaderConfig cfg, InputStream in, byte[] buf, int start, int end) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream `in` should not be `null`");
        }
        this.mConfig = cfg;
        this.mIn = in;
        this.mData = buf;
        this.mPtr = start;
        this.mEnd = end;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        if (this.mData != null) {
            return this.mEnd - this.mPtr;
        }
        return this.mIn.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        freeMergedBuffer();
        this.mIn.close();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        if (this.mData == null) {
            this.mIn.mark(readlimit);
        }
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this.mData == null && this.mIn.markSupported();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.mData != null) {
            byte[] bArr = this.mData;
            int i = this.mPtr;
            this.mPtr = i + 1;
            int c = bArr[i] & 255;
            if (this.mPtr >= this.mEnd) {
                freeMergedBuffer();
            }
            return c;
        }
        return this.mIn.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.mData != null) {
            int avail = this.mEnd - this.mPtr;
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(this.mData, this.mPtr, b, off, len);
            this.mPtr += len;
            if (this.mPtr >= this.mEnd) {
                freeMergedBuffer();
            }
            return len;
        }
        return this.mIn.read(b, off, len);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        if (this.mData == null) {
            this.mIn.reset();
        }
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        long count = 0;
        if (this.mData != null) {
            int amount = this.mEnd - this.mPtr;
            if (amount > n) {
                this.mPtr += (int) n;
                return n;
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
            byte[] data = this.mData;
            this.mData = null;
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(data);
            }
        }
    }
}
