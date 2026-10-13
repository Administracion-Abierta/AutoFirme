package org.spongycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/BufferingOutputStream.class */
public class BufferingOutputStream extends OutputStream {
    private final OutputStream other;
    private final byte[] buf;
    private int bufOff;

    public BufferingOutputStream(OutputStream other) {
        this.other = other;
        this.buf = new byte[4096];
    }

    public BufferingOutputStream(OutputStream other, int bufferSize) {
        this.other = other;
        this.buf = new byte[bufferSize];
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int offset, int len) throws IOException {
        int len2;
        if (len < this.buf.length - this.bufOff) {
            System.arraycopy(bytes, offset, this.buf, this.bufOff, len);
            this.bufOff += len;
            return;
        }
        int gap = this.buf.length - this.bufOff;
        System.arraycopy(bytes, offset, this.buf, this.bufOff, gap);
        this.bufOff += gap;
        flush();
        int offset2 = offset + gap;
        int i = len;
        int length = gap;
        while (true) {
            len2 = i - length;
            if (len2 < this.buf.length) {
                break;
            }
            this.other.write(bytes, offset2, this.buf.length);
            offset2 += this.buf.length;
            i = len2;
            length = this.buf.length;
        }
        if (len2 > 0) {
            System.arraycopy(bytes, offset2, this.buf, this.bufOff, len2);
            this.bufOff += len2;
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = (byte) b;
        if (this.bufOff == this.buf.length) {
            flush();
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.other.write(this.buf, 0, this.bufOff);
        this.bufOff = 0;
        Arrays.fill(this.buf, (byte) 0);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        flush();
        this.other.close();
    }
}
