package com.sun.java.help.search;

import java.io.PrintStream;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/IntegerArray.class */
class IntegerArray {
    private int[] _array;
    private int _size;
    private int _free;
    private static final int InitialSize = 128;

    public IntegerArray() {
        this._free = 0;
        this._size = 128;
        this._array = new int[128];
    }

    public IntegerArray(int i) {
        this._free = 0;
        this._size = i;
        this._array = new int[i];
    }

    public void clear() {
        this._free = 0;
    }

    public int at(int i) {
        return this._array[i];
    }

    public void add(int i) {
        if (this._free == this._size) {
            growArray(this._size * 2);
        }
        int[] iArr = this._array;
        int i2 = this._free;
        this._free = i2 + 1;
        iArr[i2] = i;
    }

    private void growArray(int i) {
        this._size = i;
        int[] iArr = new int[i];
        System.arraycopy(this._array, 0, iArr, 0, this._free);
        this._array = iArr;
    }

    public int popLast() {
        int[] iArr = this._array;
        int i = this._free - 1;
        this._free = i;
        return iArr[i];
    }

    public int cardinality() {
        return this._free;
    }

    public void toDifferences(IntegerArray integerArray) {
        if (integerArray._size < this._size) {
            integerArray.growArray(this._size);
        }
        int i = this._free;
        integerArray._free = i;
        if (i > 0) {
            integerArray._array[0] = this._array[0];
            for (int i2 = 1; i2 < this._free; i2++) {
                integerArray._array[i2] = this._array[i2] - this._array[i2 - 1];
            }
        }
    }

    public int indexOf(int i) {
        int i2 = 0;
        int i3 = this._free;
        while (i2 <= i3) {
            int i4 = (i2 + i3) / 2;
            if (this._array[i4] < i) {
                i2 = i4 + 1;
            } else if (i < this._array[i4]) {
                i3 = i4 - 1;
            } else {
                return i4;
            }
        }
        return -1;
    }

    public void print(PrintStream printStream) {
        for (int i = 0; i < this._free - 1; i++) {
            printStream.print(this._array[i]);
            printStream.print(' ');
        }
        printStream.println(this._array[this._free - 1]);
    }
}
