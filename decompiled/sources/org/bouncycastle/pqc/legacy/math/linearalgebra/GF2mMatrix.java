package org.bouncycastle.pqc.legacy.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2mMatrix.class */
public class GF2mMatrix extends Matrix {
    protected GF2mField field;
    protected int[][] matrix;

    public GF2mMatrix(GF2mField gF2mField, byte[] bArr) {
        this.field = gF2mField;
        int i = 8;
        int i2 = 1;
        while (gF2mField.getDegree() > i) {
            i2++;
            i += 8;
        }
        if (bArr.length < 5) {
            throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
        }
        this.numRows = ((((bArr[3] & 255) << 24) ^ ((bArr[2] & 255) << 16)) ^ ((bArr[1] & 255) << 8)) ^ (bArr[0] & 255);
        int i3 = i2 * this.numRows;
        if (this.numRows <= 0 || (bArr.length - 4) % i3 != 0) {
            throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
        }
        this.numColumns = (bArr.length - 4) / i3;
        this.matrix = new int[this.numRows][this.numColumns];
        int i4 = 4;
        for (int i5 = 0; i5 < this.numRows; i5++) {
            for (int i6 = 0; i6 < this.numColumns; i6++) {
                for (int i7 = 0; i7 < i; i7 += 8) {
                    int[] iArr = this.matrix[i5];
                    int i8 = i6;
                    int i9 = i4;
                    i4++;
                    iArr[i8] = iArr[i8] ^ ((bArr[i9] & 255) << i7);
                }
                if (!this.field.isElementOfThisField(this.matrix[i5][i6])) {
                    throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
                }
            }
        }
    }

    public GF2mMatrix(GF2mMatrix gF2mMatrix) {
        this.numRows = gF2mMatrix.numRows;
        this.numColumns = gF2mMatrix.numColumns;
        this.field = gF2mMatrix.field;
        this.matrix = new int[this.numRows][];
        for (int i = 0; i < this.numRows; i++) {
            this.matrix[i] = IntUtils.clone(gF2mMatrix.matrix[i]);
        }
    }

    protected GF2mMatrix(GF2mField gF2mField, int[][] iArr) {
        this.field = gF2mField;
        this.matrix = iArr;
        this.numRows = iArr.length;
        this.numColumns = iArr[0].length;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public byte[] getEncoded() {
        int i = 8;
        int i2 = 1;
        while (this.field.getDegree() > i) {
            i2++;
            i += 8;
        }
        byte[] bArr = new byte[(this.numRows * this.numColumns * i2) + 4];
        bArr[0] = (byte) (this.numRows & 255);
        bArr[1] = (byte) ((this.numRows >>> 8) & 255);
        bArr[2] = (byte) ((this.numRows >>> 16) & 255);
        bArr[3] = (byte) ((this.numRows >>> 24) & 255);
        int i3 = 4;
        for (int i4 = 0; i4 < this.numRows; i4++) {
            for (int i5 = 0; i5 < this.numColumns; i5++) {
                for (int i6 = 0; i6 < i; i6 += 8) {
                    int i7 = i3;
                    i3++;
                    bArr[i7] = (byte) (this.matrix[i4][i5] >>> i6);
                }
            }
        }
        return bArr;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public boolean isZero() {
        for (int i = 0; i < this.numRows; i++) {
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                if (this.matrix[i][i2] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public Matrix computeInverse() {
        int i;
        if (this.numRows != this.numColumns) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
        int[][] iArr = new int[this.numRows][this.numRows];
        for (int i2 = this.numRows - 1; i2 >= 0; i2--) {
            iArr[i2] = IntUtils.clone(this.matrix[i2]);
        }
        int[][] iArr2 = new int[this.numRows][this.numRows];
        for (int i3 = this.numRows - 1; i3 >= 0; i3--) {
            iArr2[i3][i3] = 1;
        }
        for (int i4 = 0; i4 < this.numRows; i4++) {
            if (iArr[i4][i4] == 0) {
                boolean z = false;
                int i5 = i4 + 1;
                while (i5 < this.numRows) {
                    if (iArr[i5][i4] != 0) {
                        z = true;
                        swapColumns(iArr, i4, i5);
                        swapColumns(iArr2, i4, i5);
                        i5 = this.numRows;
                    }
                    i5++;
                }
                if (!z) {
                    throw new ArithmeticException("Matrix is not invertible.");
                }
            }
            int iInverse = this.field.inverse(iArr[i4][i4]);
            multRowWithElementThis(iArr[i4], iInverse);
            multRowWithElementThis(iArr2[i4], iInverse);
            for (int i6 = 0; i6 < this.numRows; i6++) {
                if (i6 != i4 && (i = iArr[i6][i4]) != 0) {
                    int[] iArrMultRowWithElement = multRowWithElement(iArr[i4], i);
                    int[] iArrMultRowWithElement2 = multRowWithElement(iArr2[i4], i);
                    addToRow(iArrMultRowWithElement, iArr[i6]);
                    addToRow(iArrMultRowWithElement2, iArr2[i6]);
                }
            }
        }
        return new GF2mMatrix(this.field, iArr2);
    }

    private static void swapColumns(int[][] iArr, int i, int i2) {
        int[] iArr2 = iArr[i];
        iArr[i] = iArr[i2];
        iArr[i2] = iArr2;
    }

    private void multRowWithElementThis(int[] iArr, int i) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            iArr[length] = this.field.mult(iArr[length], i);
        }
    }

    private int[] multRowWithElement(int[] iArr, int i) {
        int[] iArr2 = new int[iArr.length];
        for (int length = iArr.length - 1; length >= 0; length--) {
            iArr2[length] = this.field.mult(iArr[length], i);
        }
        return iArr2;
    }

    private void addToRow(int[] iArr, int[] iArr2) {
        for (int length = iArr2.length - 1; length >= 0; length--) {
            iArr2[length] = this.field.add(iArr[length], iArr2[length]);
        }
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public Matrix rightMultiply(Matrix matrix) {
        throw new RuntimeException("Not implemented.");
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public Matrix rightMultiply(Permutation permutation) {
        throw new RuntimeException("Not implemented.");
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public Vector leftMultiply(Vector vector) {
        throw new RuntimeException("Not implemented.");
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public Vector rightMultiply(Vector vector) {
        throw new RuntimeException("Not implemented.");
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GF2mMatrix)) {
            return false;
        }
        GF2mMatrix gF2mMatrix = (GF2mMatrix) obj;
        if (!this.field.equals(gF2mMatrix.field) || gF2mMatrix.numRows != this.numColumns || gF2mMatrix.numColumns != this.numColumns) {
            return false;
        }
        for (int i = 0; i < this.numRows; i++) {
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                if (this.matrix[i][i2] != gF2mMatrix.matrix[i][i2]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        int iHashCode = (((this.field.hashCode() * 31) + this.numRows) * 31) + this.numColumns;
        for (int i = 0; i < this.numRows; i++) {
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                iHashCode = (iHashCode * 31) + this.matrix[i][i2];
            }
        }
        return iHashCode;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Matrix
    public String toString() {
        String str = this.numRows + " x " + this.numColumns + " Matrix over " + this.field.toString() + ": \n";
        for (int i = 0; i < this.numRows; i++) {
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                str = str + this.field.elementToStr(this.matrix[i][i2]) + " : ";
            }
            str = str + "\n";
        }
        return str;
    }
}
