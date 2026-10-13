package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Decompressor.class */
abstract class Decompressor {
    private static final int BitsInByte = 8;
    private static final int NBits = 32;
    private int _readByte;
    private int _toRead = 0;
    private int _path = 0;

    protected abstract int getNextByte() throws Exception;

    Decompressor() {
    }

    protected void initReading() {
        this._toRead = 0;
    }

    private int countZeroes() throws Exception {
        int i = 0;
        while (true) {
            int i2 = this._toRead;
            this._toRead = i2 - 1;
            if (i2 <= 0) {
                this._readByte = getNextByte();
                this._toRead = 8;
            } else {
                if ((this._readByte & (1 << this._toRead)) != 0) {
                    return i;
                }
                i++;
            }
        }
    }

    private int read() throws Exception {
        int i = this._toRead;
        this._toRead = i - 1;
        if (i > 0) {
            return this._readByte & (1 << this._toRead);
        }
        this._toRead = 7;
        int nextByte = getNextByte();
        this._readByte = nextByte;
        return nextByte & 128;
    }

    public int read(int i) throws Exception {
        int i2 = 8 - this._toRead;
        if (i <= this._toRead) {
            int i3 = (this._readByte << i2) & 255;
            int i4 = this._toRead - i;
            this._toRead = i4;
            return i3 >>> (i2 + i4);
        }
        int nextByte = this._toRead > 0 ? ((this._readByte << i2) & 255) >>> i2 : 0;
        int i5 = i - this._toRead;
        while (i5 >= 8) {
            nextByte = (nextByte << 8) | getNextByte();
            i5 -= 8;
        }
        if (i5 > 0) {
            int nextByte2 = getNextByte();
            this._readByte = nextByte2;
            int i6 = 8 - i5;
            this._toRead = i6;
            return (nextByte << i5) | (nextByte2 >>> i6);
        }
        this._toRead = 0;
        return nextByte;
    }

    public void beginIteration() {
        this._path = 0;
    }

    public boolean readNext(int i, CompressorIterator compressorIterator) throws Exception {
        if (read() != 0) {
            compressorIterator.value(this._path | read(i));
            return true;
        }
        int i2 = 1;
        while (true) {
            int i3 = this._toRead;
            this._toRead = i3 - 1;
            if (i3 <= 0) {
                this._readByte = getNextByte();
                this._toRead = 8;
            } else {
                if ((this._readByte & (1 << this._toRead)) != 0) {
                    break;
                }
                i2++;
            }
        }
        int i4 = this._path;
        this._path = (((this._path >>> (i + i2)) << i2) | read(i2)) << i;
        if (this._path != i4) {
            compressorIterator.value(this._path | read(i));
            return true;
        }
        return false;
    }

    public void decode(int i, IntegerArray integerArray) throws Exception {
        int i2 = 0;
        while (true) {
            if (read() != 0) {
                integerArray.add(i2 | read(i));
            } else {
                int iCountZeroes = countZeroes() + 1;
                int i3 = i2;
                i2 = (((i2 >>> (i + iCountZeroes)) << iCountZeroes) | read(iCountZeroes)) << i;
                if (i2 != i3) {
                    integerArray.add(i2 | read(i));
                } else {
                    return;
                }
            }
        }
    }

    public void ascDecode(int i, IntegerArray integerArray) throws Exception {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (read() != 0) {
                int i4 = i3 + (i2 | read(i));
                i3 = i4;
                integerArray.add(i4);
            } else {
                int iCountZeroes = countZeroes() + 1;
                int i5 = i2;
                i2 = (((i2 >>> (i + iCountZeroes)) << iCountZeroes) | read(iCountZeroes)) << i;
                if (i2 != i5) {
                    int i6 = i3 + (i2 | read(i));
                    i3 = i6;
                    integerArray.add(i6);
                } else {
                    return;
                }
            }
        }
    }

    public int ascendingDecode(int i, int i2, int[] iArr) throws Exception {
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (read() != 0) {
                int i5 = i4;
                i4++;
                int i6 = i2 + (i3 | read(i));
                i2 = i6;
                iArr[i5] = i6;
            } else {
                int i7 = 0;
                while (true) {
                    int i8 = this._toRead;
                    this._toRead = i8 - 1;
                    if (i8 <= 0) {
                        this._readByte = getNextByte();
                        this._toRead = 8;
                    } else {
                        if ((this._readByte & (1 << this._toRead)) != 0) {
                            break;
                        }
                        i7++;
                    }
                }
                int i9 = i7 + 1;
                int i10 = (((i3 >>> (i + i9)) << i9) | read(i9)) << i;
                if (i10 != i3) {
                    int i11 = i4;
                    i4++;
                    i3 = i10;
                    int i12 = i2 + (i10 | read(i));
                    i2 = i12;
                    iArr[i11] = i12;
                } else {
                    return i4;
                }
            }
        }
    }
}
