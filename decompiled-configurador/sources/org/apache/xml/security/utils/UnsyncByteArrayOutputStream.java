package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/UnsyncByteArrayOutputStream.class */
public class UnsyncByteArrayOutputStream extends OutputStream {
    private static final int VM_ARRAY_INDEX_MAX_VALUE = 2147483639;
    private static final int INITIAL_SIZE = 8192;
    private int pos;
    private int size = 8192;
    private byte[] buf = new byte[8192];

    @Override // java.io.OutputStream
    public void write(byte[] arg0) {
        if (VM_ARRAY_INDEX_MAX_VALUE - this.pos < arg0.length) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        int newPos = this.pos + arg0.length;
        if (newPos > this.size) {
            expandSize(newPos);
        }
        System.arraycopy(arg0, 0, this.buf, this.pos, arg0.length);
        this.pos = newPos;
    }

    @Override // java.io.OutputStream
    public void write(byte[] arg0, int arg1, int arg2) {
        if (VM_ARRAY_INDEX_MAX_VALUE - this.pos < arg2) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        int newPos = this.pos + arg2;
        if (newPos > this.size) {
            expandSize(newPos);
        }
        System.arraycopy(arg0, arg1, this.buf, this.pos, arg2);
        this.pos = newPos;
    }

    @Override // java.io.OutputStream
    public void write(int arg0) {
        if (VM_ARRAY_INDEX_MAX_VALUE - this.pos == 0) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        int newPos = this.pos + 1;
        if (newPos > this.size) {
            expandSize(newPos);
        }
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) arg0;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[this.pos];
        System.arraycopy(this.buf, 0, result, 0, this.pos);
        return result;
    }

    public void reset() {
        this.pos = 0;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.pos);
    }

    private void expandSize(int newPos) {
        int newSize = this.size;
        while (newPos > newSize) {
            newSize <<= 1;
            if (newSize < 0) {
                newSize = VM_ARRAY_INDEX_MAX_VALUE;
            }
        }
        byte[] newBuf = new byte[newSize];
        System.arraycopy(this.buf, 0, newBuf, 0, this.pos);
        this.buf = newBuf;
        this.size = newSize;
    }
}
