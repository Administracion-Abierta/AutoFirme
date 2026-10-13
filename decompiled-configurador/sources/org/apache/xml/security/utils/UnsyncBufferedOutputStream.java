package org.apache.xml.security.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/UnsyncBufferedOutputStream.class */
public class UnsyncBufferedOutputStream extends FilterOutputStream {
    protected byte[] buffer;
    protected int count;

    public UnsyncBufferedOutputStream(OutputStream out) {
        super(out);
        this.buffer = new byte[8192];
    }

    public UnsyncBufferedOutputStream(OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        this.buffer = new byte[size];
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        flushInternal();
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bytes, int offset, int length) throws IOException {
        if (length >= this.buffer.length) {
            flushInternal();
            this.out.write(bytes, offset, length);
        } else {
            if (length >= this.buffer.length - this.count) {
                flushInternal();
            }
            System.arraycopy(bytes, offset, this.buffer, this.count, length);
            this.count += length;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int oneByte) throws IOException {
        if (this.count == this.buffer.length) {
            this.out.write(this.buffer, 0, this.count);
            this.count = 0;
        }
        byte[] bArr = this.buffer;
        int i = this.count;
        this.count = i + 1;
        bArr[i] = (byte) oneByte;
    }

    private void flushInternal() throws IOException {
        if (this.count > 0) {
            this.out.write(this.buffer, 0, this.count);
            this.count = 0;
        }
    }
}
