package com.sun.java.help.search;

import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BitBuffer.class */
class BitBuffer {
    private static final int InitSize = 256;
    private static final int NBits = 32;
    private static final int BitsInByte = 8;
    private static final int BytesInInt = 4;
    private int _avail = 32;
    private int _word = 0;
    private int _free = 0;
    private int _size = 256;
    private int[] _array = new int[256];

    BitBuffer() {
    }

    public void close() {
        if (this._avail < 32) {
            store(this._word << this._avail);
        } else {
            this._avail = 0;
        }
    }

    public void write(DataOutput dataOutput) throws IOException {
        for (int i = 0; i < this._free - 1; i++) {
            dataOutput.writeInt(this._array[i]);
        }
        int i2 = this._array[this._free - 1];
        int i3 = 4 - (this._avail / 8);
        int i4 = 32;
        while (true) {
            int i5 = i3;
            i3 = i5 - 1;
            if (i5 > 0) {
                i4 -= 8;
                dataOutput.writeByte((i2 >>> i4) & 255);
            } else {
                return;
            }
        }
    }

    public void clear() {
        this._word = 0;
        this._avail = 32;
        this._free = 0;
    }

    public int byteCount() {
        return (this._free * 4) - (this._avail / 8);
    }

    public int bitCount() {
        return (32 * this._free) - this._avail;
    }

    public void setFrom(BitBuffer bitBuffer) {
        this._word = bitBuffer._word;
        this._avail = bitBuffer._avail;
        int i = bitBuffer._free;
        this._free = i;
        if (i > this._size) {
            int i2 = bitBuffer._free;
            this._size = i2;
            this._array = new int[i2];
        }
        System.arraycopy(bitBuffer._array, 0, this._array, 0, this._free);
    }

    private void growArray(int i) {
        this._size = i;
        int[] iArr = new int[i];
        System.arraycopy(this._array, 0, iArr, 0, this._free);
        this._array = iArr;
    }

    private void store(int i) {
        if (this._free == this._size) {
            growArray(this._size * 2);
        }
        int[] iArr = this._array;
        int i2 = this._free;
        this._free = i2 + 1;
        iArr[i2] = i;
    }

    public void append(int i) {
        this._word = (this._word << 1) | i;
        int i2 = this._avail - 1;
        this._avail = i2;
        if (i2 == 0) {
            store(this._word);
            this._word = 0;
            this._avail = 32;
        }
    }

    public void append(int i, int i2) {
        if (i2 < this._avail) {
            this._word = (this._word << i2) | i;
            this._avail -= i2;
        } else {
            if (i2 > this._avail) {
                int i3 = i2 - this._avail;
                store((this._word << this._avail) | (i >>> i3));
                this._word = i;
                this._avail = 32 - i3;
                return;
            }
            store((this._word << i2) | i);
            this._word = 0;
            this._avail = 32;
        }
    }

    public void concatenate(BitBuffer bitBuffer) {
        if ((32 * (this._size - this._free)) + this._avail < bitBuffer.bitCount()) {
            growArray(this._free + bitBuffer._free + 1);
        }
        if (this._avail == 0) {
            System.arraycopy(bitBuffer._array, 0, this._array, this._free, bitBuffer._free);
            this._avail = bitBuffer._avail;
            this._free += bitBuffer._free;
            return;
        }
        int i = this._free - 1;
        int i2 = 0;
        do {
            int[] iArr = this._array;
            int i3 = i;
            i++;
            iArr[i3] = iArr[i3] | (bitBuffer._array[i2] >>> (32 - this._avail));
            int i4 = i2;
            i2++;
            this._array[i] = bitBuffer._array[i4] << this._avail;
        } while (i2 < bitBuffer._free);
        this._free += bitBuffer._free;
        int i5 = this._avail + bitBuffer._avail;
        this._avail = i5;
        if (i5 >= 32) {
            this._avail -= 32;
            this._free--;
        }
    }
}
