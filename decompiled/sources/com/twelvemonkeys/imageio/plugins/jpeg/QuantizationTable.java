package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.plugins.jpeg.JPEGQTable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/QuantizationTable.class */
final class QuantizationTable extends Segment {
    private static final int[] ZIGZAG = {0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63};
    private final int[] precision;
    private final boolean[] tq;
    private final int[][] quantTables;

    QuantizationTable() {
        super(JPEG.DQT);
        this.precision = new int[4];
        this.tq = new boolean[4];
        this.quantTables = new int[4][64];
    }

    void enhanceTables() {
        for (int i = 0; i < 4; i++) {
            if (this.tq[i]) {
                enhanceQuantizationTable(this.quantTables[i], ZIGZAG);
            }
        }
    }

    private void enhanceQuantizationTable(int[] iArr, int[] iArr2) {
        for (int i = 0; i < 8; i++) {
            int i2 = iArr2[i];
            iArr[i2] = iArr[i2] * 90;
            int i3 = iArr2[32 + i];
            iArr[i3] = iArr[i3] * 90;
            int i4 = iArr2[16 + i];
            iArr[i4] = iArr[i4] * 118;
            int i5 = iArr2[48 + i];
            iArr[i5] = iArr[i5] * 49;
            int i6 = iArr2[40 + i];
            iArr[i6] = iArr[i6] * 71;
            int i7 = iArr2[8 + i];
            iArr[i7] = iArr[i7] * 126;
            int i8 = iArr2[56 + i];
            iArr[i8] = iArr[i8] * 25;
            int i9 = iArr2[24 + i];
            iArr[i9] = iArr[i9] * 106;
        }
        for (int i10 = 0; i10 < 8; i10++) {
            int i11 = iArr2[8 * i10];
            iArr[i11] = iArr[i11] * 90;
            int i12 = iArr2[4 + (8 * i10)];
            iArr[i12] = iArr[i12] * 90;
            int i13 = iArr2[2 + (8 * i10)];
            iArr[i13] = iArr[i13] * 118;
            int i14 = iArr2[6 + (8 * i10)];
            iArr[i14] = iArr[i14] * 49;
            int i15 = iArr2[5 + (8 * i10)];
            iArr[i15] = iArr[i15] * 71;
            int i16 = iArr2[1 + (8 * i10)];
            iArr[i16] = iArr[i16] * 126;
            int i17 = iArr2[7 + (8 * i10)];
            iArr[i17] = iArr[i17] * 25;
            int i18 = iArr2[3 + (8 * i10)];
            iArr[i18] = iArr[i18] * 106;
        }
        for (int i19 = 0; i19 < 64; i19++) {
            int i20 = i19;
            iArr[i20] = iArr[i20] >> 6;
        }
    }

    public String toString() {
        return "DQT[]";
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static QuantizationTable read(DataInput dataInput, int i) throws IOException, IIOException {
        int i2 = 2;
        QuantizationTable quantizationTable = new QuantizationTable();
        while (i2 < i) {
            int unsignedByte = dataInput.readUnsignedByte();
            i2++;
            int i3 = unsignedByte & 15;
            if (i3 > 3) {
                throw new IIOException("Unexpected JPEG Quantization Table Id (> 3): " + i3);
            }
            quantizationTable.precision[i3] = unsignedByte >> 4;
            if (quantizationTable.precision[i3] == 0) {
                quantizationTable.precision[i3] = 8;
            } else if (quantizationTable.precision[i3] == 1) {
                quantizationTable.precision[i3] = 16;
            } else {
                throw new IIOException("Unexpected JPEG Quantization Table precision: " + quantizationTable.precision[i3]);
            }
            quantizationTable.tq[i3] = true;
            if (quantizationTable.precision[i3] == 8) {
                for (int i4 = 0; i4 < 64; i4++) {
                    if (i2 > i) {
                        throw new IIOException("JPEG Quantization Table format error");
                    }
                    quantizationTable.quantTables[i3][i4] = dataInput.readUnsignedByte();
                    i2++;
                }
            } else {
                for (int i5 = 0; i5 < 64; i5++) {
                    if (i2 > i) {
                        throw new IIOException("JPEG Quantization Table format error");
                    }
                    quantizationTable.quantTables[i3][i5] = dataInput.readUnsignedShort();
                    i2 += 2;
                }
            }
        }
        if (i2 != i) {
            throw new IIOException("JPEG Quantization Table error, bad segment length: " + i);
        }
        return quantizationTable;
    }

    public boolean isPresent(int i) {
        return this.tq[i];
    }

    int precision(int i) {
        return this.precision[i];
    }

    int[] qTable(int i) {
        return this.quantTables[i];
    }

    JPEGQTable toNativeTable(int i) {
        int[] iArr = new int[this.quantTables[i].length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = this.quantTables[i][ZIGZAG[i2]];
        }
        return new JPEGQTable(iArr);
    }
}
