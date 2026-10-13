package com.twelvemonkeys.io.ole2;

import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/ole2/SIdChain.class */
final class SIdChain {
    int size = 0;
    int next = 0;
    int[] chain = new int[16];

    void addSID(int i) {
        ensureCapacity();
        int[] iArr = this.chain;
        int i2 = this.size;
        this.size = i2 + 1;
        iArr[i2] = i;
    }

    private void ensureCapacity() {
        if (this.chain.length == this.size) {
            int[] iArr = new int[this.size << 1];
            System.arraycopy(this.chain, 0, iArr, 0, this.size);
            this.chain = iArr;
        }
    }

    public int[] getChain() {
        int[] iArr = new int[this.size];
        System.arraycopy(this.chain, 0, iArr, 0, this.size);
        return iArr;
    }

    public void reset() {
        this.next = 0;
    }

    public boolean hasNext() {
        return this.next < this.size;
    }

    public int next() {
        if (this.next >= this.size) {
            throw new NoSuchElementException("No element");
        }
        int[] iArr = this.chain;
        int i = this.next;
        this.next = i + 1;
        return iArr[i];
    }

    public int get(int i) {
        return this.chain[i];
    }

    public int length() {
        return this.size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.size * 5);
        sb.append('[');
        for (int i = 0; i < this.size; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(this.chain[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
