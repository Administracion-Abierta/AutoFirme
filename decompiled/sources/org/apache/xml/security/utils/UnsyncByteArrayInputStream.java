package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/UnsyncByteArrayInputStream.class */
public class UnsyncByteArrayInputStream extends InputStream {
    protected byte[] buf;
    protected int pos;
    protected int mark;
    protected int count;

    public UnsyncByteArrayInputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }

    public UnsyncByteArrayInputStream(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.mark = offset;
        this.count = offset + length > buf.length ? buf.length : offset + length;
    }

    @Override // java.io.InputStream
    public int available() {
        return this.count - this.pos;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // java.io.InputStream
    public void mark(int readlimit) {
        this.mark = this.pos;
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.InputStream
    public int read() {
        if (this.pos >= this.count) {
            return -1;
        }
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int length) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset > b.length || length < 0 || length > b.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        if (this.pos >= this.count) {
            return -1;
        }
        if (length == 0) {
            return 0;
        }
        int copylen = this.count - this.pos < length ? this.count - this.pos : length;
        System.arraycopy(this.buf, this.pos, b, offset, copylen);
        this.pos += copylen;
        return copylen;
    }

    @Override // java.io.InputStream
    public void reset() {
        this.pos = this.mark;
    }

    @Override // java.io.InputStream
    public long skip(long n) {
        if (n <= 0) {
            return 0L;
        }
        int temp = this.pos;
        this.pos = ((long) (this.count - this.pos)) < n ? this.count : (int) (((long) this.pos) + n);
        return this.pos - temp;
    }
}
