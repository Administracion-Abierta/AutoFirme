package org.bouncycastle.util.io;

import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/io/LimitedBuffer.class */
public class LimitedBuffer extends OutputStream {
    private final byte[] buf;
    private int count = 0;

    public LimitedBuffer(int i) {
        this.buf = new byte[i];
    }

    public int copyTo(byte[] bArr, int i) {
        System.arraycopy(this.buf, 0, bArr, i, this.count);
        return this.count;
    }

    public int limit() {
        return this.buf.length;
    }

    public void reset() {
        this.count = 0;
    }

    public int size() {
        return this.count;
    }

    @Override // java.io.OutputStream
    public void write(int i) {
        byte[] bArr = this.buf;
        int i2 = this.count;
        this.count = i2 + 1;
        bArr[i2] = (byte) i;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) {
        System.arraycopy(bArr, 0, this.buf, this.count, bArr.length);
        this.count += bArr.length;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) {
        System.arraycopy(bArr, i, this.buf, this.count, i2);
        this.count += i2;
    }
}
