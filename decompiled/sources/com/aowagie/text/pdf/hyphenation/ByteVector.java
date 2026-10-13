package com.aowagie.text.pdf.hyphenation;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/ByteVector.class */
class ByteVector implements Serializable {
    private static final long serialVersionUID = -1096301185375029343L;
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;
    private byte[] array;
    private int n;

    public ByteVector() {
        this(2048);
    }

    private ByteVector(int capacity) {
        if (capacity > 0) {
            this.blockSize = capacity;
        } else {
            this.blockSize = 2048;
        }
        this.array = new byte[this.blockSize];
        this.n = 0;
    }

    public byte[] getArray() {
        return this.array;
    }

    int length() {
        return this.n;
    }

    byte get(int index) {
        return this.array[index];
    }

    int alloc(int size) {
        int index = this.n;
        int len = this.array.length;
        if (this.n + size >= len) {
            byte[] aux = new byte[len + this.blockSize];
            System.arraycopy(this.array, 0, aux, 0, len);
            this.array = aux;
        }
        this.n += size;
        return index;
    }

    void trimToSize() {
        if (this.n < this.array.length) {
            byte[] aux = new byte[this.n];
            System.arraycopy(this.array, 0, aux, 0, this.n);
            this.array = aux;
        }
    }
}
