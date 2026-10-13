package com.sun.java.help.search;

import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Compressor.class */
class Compressor {
    private static final int NBits = 32;
    private static final int BeginK = 5;
    private BitBuffer _buffer = new BitBuffer();

    Compressor() {
    }

    public void write(DataOutput dataOutput) throws IOException {
        this._buffer.write(dataOutput);
    }

    public int byteCount() {
        return this._buffer.byteCount();
    }

    public void clear() {
        this._buffer.clear();
    }

    public void concatenate(Compressor compressor) {
        this._buffer.concatenate(compressor._buffer);
    }

    public void encode(IntegerArray integerArray, int i) {
        int i2 = 1 << i;
        int i3 = 0;
        for (int i4 = 0; i4 < integerArray.cardinality(); i4++) {
            int iAt = integerArray.at(i4) >>> i;
            int iAt2 = integerArray.at(i4) % i2;
            if (iAt != i3) {
                int i5 = i3;
                int i6 = i3;
                int i7 = 0;
                int i8 = 1;
                if (iAt > i3) {
                    int i9 = i3;
                    while (i9 < iAt) {
                        if ((i6 & 1) != 0) {
                            i5 -= i8;
                        } else {
                            i9 += i8;
                        }
                        i6 >>>= 1;
                        i8 <<= 1;
                        i7++;
                    }
                } else {
                    while (i5 > iAt) {
                        if ((i6 & 1) != 0) {
                            i5 -= i8;
                        }
                        i6 >>>= 1;
                        i8 <<= 1;
                        i7++;
                    }
                }
                if ((i7 * 2) + 1 + i <= 32) {
                    this._buffer.append((((1 << i7) | (iAt - i5)) << i) | iAt2, (i7 * 2) + 1 + i);
                } else {
                    if ((i7 * 2) + 1 <= 32) {
                        this._buffer.append((1 << i7) | (iAt - i5), (i7 * 2) + 1);
                    } else {
                        this._buffer.append(0, i7);
                        this._buffer.append((1 << i7) | (iAt - i5), i7 + 1);
                    }
                    this._buffer.append(iAt2, i);
                }
                i3 = iAt;
            } else {
                this._buffer.append(iAt2 | i2, i + 1);
            }
        }
        this._buffer.append(2 | (i3 & 1), 3);
        this._buffer.close();
    }

    public void encode(IntegerArray integerArray, IntegerArray integerArray2, int i, int i2) {
        int i3 = 1 << i;
        int i4 = 0;
        for (int i5 = 0; i5 < integerArray.cardinality(); i5++) {
            int iAt = integerArray.at(i5) >>> i;
            int iAt2 = integerArray.at(i5) % i3;
            if (iAt != i4) {
                int i6 = i4;
                int i7 = i4;
                int i8 = 0;
                int i9 = 1;
                if (iAt > i4) {
                    int i10 = i4;
                    while (i10 < iAt) {
                        if ((i7 & 1) != 0) {
                            i6 -= i9;
                        } else {
                            i10 += i9;
                        }
                        i7 >>>= 1;
                        i9 <<= 1;
                        i8++;
                    }
                } else {
                    while (i6 > iAt) {
                        if ((i7 & 1) != 0) {
                            i6 -= i9;
                        }
                        i7 >>>= 1;
                        i9 <<= 1;
                        i8++;
                    }
                }
                if ((i8 * 2) + 1 + i <= 32) {
                    this._buffer.append((((1 << i8) | (iAt - i6)) << i) | iAt2, (i8 * 2) + 1 + i);
                } else {
                    if ((i8 * 2) + 1 <= 32) {
                        this._buffer.append((1 << i8) | (iAt - i6), (i8 * 2) + 1);
                    } else {
                        this._buffer.append(0, i8);
                        this._buffer.append((1 << i8) | (iAt - i6), i8 + 1);
                    }
                    this._buffer.append(iAt2, i);
                }
                this._buffer.append(integerArray2.at(i5), i2);
                i4 = iAt;
            } else {
                this._buffer.append(((iAt2 | i3) << i2) | integerArray2.at(i5), i + i2 + 1);
            }
        }
        this._buffer.append(2 | (i4 & 1), 3);
        this._buffer.close();
    }

    public int minimize(IntegerArray integerArray, int i) {
        int iBitCount;
        BitBuffer bitBuffer = new BitBuffer();
        int i2 = i;
        this._buffer.clear();
        encode(integerArray, i);
        int iBitCount2 = this._buffer.bitCount();
        bitBuffer.setFrom(this._buffer);
        this._buffer.clear();
        encode(integerArray, i + 1);
        if (this._buffer.bitCount() < iBitCount2) {
            int i3 = i + 1;
            do {
                bitBuffer.setFrom(this._buffer);
                iBitCount = this._buffer.bitCount();
                i2 = i3;
                this._buffer.clear();
                i3++;
                encode(integerArray, i3);
            } while (this._buffer.bitCount() < iBitCount);
        } else {
            for (int i4 = i - 1; i4 > 0; i4--) {
                this._buffer.clear();
                encode(integerArray, i4);
                if (this._buffer.bitCount() >= iBitCount2) {
                    break;
                }
                bitBuffer.setFrom(this._buffer);
                iBitCount2 = this._buffer.bitCount();
                i2 = i4;
            }
        }
        this._buffer.setFrom(bitBuffer);
        return i2;
    }

    public int compressAscending(IntegerArray integerArray) {
        IntegerArray integerArray2 = new IntegerArray(integerArray.cardinality());
        integerArray.toDifferences(integerArray2);
        return minimize(integerArray2, 5);
    }
}
