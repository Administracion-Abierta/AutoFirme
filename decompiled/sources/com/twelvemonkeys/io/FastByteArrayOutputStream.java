package com.twelvemonkeys.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FastByteArrayOutputStream.class */
public final class FastByteArrayOutputStream extends ByteArrayOutputStream {
    public FastByteArrayOutputStream(int i) {
        super(i);
    }

    public FastByteArrayOutputStream(byte[] bArr) {
        super(0);
        this.buf = bArr;
        this.count = bArr.length;
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) {
        if (i < 0 || i > bArr.length || i2 < 0 || i + i2 > bArr.length || i + i2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i2 == 0) {
            return;
        }
        int i3 = this.count + i2;
        growIfNeeded(i3);
        System.arraycopy(bArr, i, this.buf, this.count, i2);
        this.count = i3;
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(int i) {
        int i2 = this.count + 1;
        growIfNeeded(i2);
        this.buf[this.count] = (byte) i;
        this.count = i2;
    }

    private void growIfNeeded(int i) {
        if (i > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, i));
        }
    }

    @Override // java.io.ByteArrayOutputStream
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.count);
    }

    @Override // java.io.ByteArrayOutputStream
    public byte[] toByteArray() {
        return Arrays.copyOf(this.buf, this.count);
    }

    public ByteArrayInputStream createInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }
}
