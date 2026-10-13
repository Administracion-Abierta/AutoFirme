package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/GF2Matrix.class */
public class GF2Matrix extends Matrix {
    private int[][] matrix;
    private int length;

    public GF2Matrix(byte[] enc) {
        if (enc.length < 9) {
            throw new ArithmeticException("given array is not an encoded matrix over GF(2)");
        }
        this.numRows = LittleEndianConversions.OS2IP(enc, 0);
        this.numColumns = LittleEndianConversions.OS2IP(enc, 4);
        int n = ((this.numColumns + 7) >>> 3) * this.numRows;
        if (this.numRows <= 0 || n != enc.length - 8) {
            throw new ArithmeticException("given array is not an encoded matrix over GF(2)");
        }
        this.length = (this.numColumns + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        int q = this.numColumns >> 5;
        int r = this.numColumns & 31;
        int count = 8;
        for (int i = 0; i < this.numRows; i++) {
            int j = 0;
            while (j < q) {
                this.matrix[i][j] = LittleEndianConversions.OS2IP(enc, count);
                j++;
                count += 4;
            }
            for (int j2 = 0; j2 < r; j2 += 8) {
                int[] iArr = this.matrix[i];
                int i2 = count;
                count++;
                iArr[q] = iArr[q] ^ ((enc[i2] & 255) << j2);
            }
        }
    }

    public GF2Matrix(int numColumns, int[][] matrix) {
        int bitMask;
        if (matrix[0].length != ((numColumns + 31) >> 5)) {
            throw new ArithmeticException("Int array does not match given number of columns.");
        }
        this.numColumns = numColumns;
        this.numRows = matrix.length;
        this.length = matrix[0].length;
        int rest = numColumns & 31;
        if (rest == 0) {
            bitMask = -1;
        } else {
            bitMask = (1 << rest) - 1;
        }
        for (int i = 0; i < this.numRows; i++) {
            int[] iArr = matrix[i];
            int i2 = this.length - 1;
            iArr[i2] = iArr[i2] & bitMask;
        }
        this.matrix = matrix;
    }

    public GF2Matrix(int n, char typeOfMatrix) {
        this(n, typeOfMatrix, new SecureRandom());
    }

    public GF2Matrix(int n, char typeOfMatrix, SecureRandom sr) {
        if (n <= 0) {
            throw new ArithmeticException("Size of matrix is non-positive.");
        }
        switch (typeOfMatrix) {
            case 'I':
                assignUnitMatrix(n);
                return;
            case 'L':
                assignRandomLowerTriangularMatrix(n, sr);
                return;
            case 'R':
                assignRandomRegularMatrix(n, sr);
                return;
            case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                assignRandomUpperTriangularMatrix(n, sr);
                return;
            case 'Z':
                assignZeroMatrix(n, n);
                return;
            default:
                throw new ArithmeticException("Unknown matrix type.");
        }
    }

    public GF2Matrix(GF2Matrix a) {
        this.numColumns = a.getNumColumns();
        this.numRows = a.getNumRows();
        this.length = a.length;
        this.matrix = new int[a.matrix.length][];
        for (int i = 0; i < this.matrix.length; i++) {
            this.matrix[i] = IntUtils.clone(a.matrix[i]);
        }
    }

    private GF2Matrix(int m, int n) {
        if (n <= 0 || m <= 0) {
            throw new ArithmeticException("size of matrix is non-positive");
        }
        assignZeroMatrix(m, n);
    }

    private void assignZeroMatrix(int m, int n) {
        this.numRows = m;
        this.numColumns = n;
        this.length = (n + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.length; j++) {
                this.matrix[i][j] = 0;
            }
        }
    }

    private void assignUnitMatrix(int n) {
        this.numRows = n;
        this.numColumns = n;
        this.length = (n + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.length; j++) {
                this.matrix[i][j] = 0;
            }
        }
        for (int i2 = 0; i2 < this.numRows; i2++) {
            int rest = i2 & 31;
            this.matrix[i2][i2 >>> 5] = 1 << rest;
        }
    }

    private void assignRandomLowerTriangularMatrix(int n, SecureRandom sr) {
        this.numRows = n;
        this.numColumns = n;
        this.length = (n + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; i++) {
            int q = i >>> 5;
            int r = i & 31;
            int s = 31 - r;
            int r2 = 1 << r;
            for (int j = 0; j < q; j++) {
                this.matrix[i][j] = sr.nextInt();
            }
            this.matrix[i][q] = (sr.nextInt() >>> s) | r2;
            for (int j2 = q + 1; j2 < this.length; j2++) {
                this.matrix[i][j2] = 0;
            }
        }
    }

    private void assignRandomUpperTriangularMatrix(int n, SecureRandom sr) {
        int help;
        this.numRows = n;
        this.numColumns = n;
        this.length = (n + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        int rest = n & 31;
        if (rest == 0) {
            help = -1;
        } else {
            help = (1 << rest) - 1;
        }
        for (int i = 0; i < this.numRows; i++) {
            int q = i >>> 5;
            int r = i & 31;
            int r2 = 1 << r;
            for (int j = 0; j < q; j++) {
                this.matrix[i][j] = 0;
            }
            this.matrix[i][q] = (sr.nextInt() << r) | r2;
            for (int j2 = q + 1; j2 < this.length; j2++) {
                this.matrix[i][j2] = sr.nextInt();
            }
            int[] iArr = this.matrix[i];
            int i2 = this.length - 1;
            iArr[i2] = iArr[i2] & help;
        }
    }

    private void assignRandomRegularMatrix(int n, SecureRandom sr) {
        this.numRows = n;
        this.numColumns = n;
        this.length = (n + 31) >>> 5;
        this.matrix = new int[this.numRows][this.length];
        GF2Matrix lm = new GF2Matrix(n, 'L', sr);
        GF2Matrix um = new GF2Matrix(n, 'U', sr);
        GF2Matrix rm = (GF2Matrix) lm.rightMultiply(um);
        Permutation perm = new Permutation(n, sr);
        int[] p = perm.getVector();
        for (int i = 0; i < n; i++) {
            System.arraycopy(rm.matrix[i], 0, this.matrix[p[i]], 0, this.length);
        }
    }

    public static GF2Matrix[] createRandomRegularMatrixAndItsInverse(int n, SecureRandom sr) {
        GF2Matrix[] result = new GF2Matrix[2];
        int length = (n + 31) >> 5;
        GF2Matrix lm = new GF2Matrix(n, 'L', sr);
        GF2Matrix um = new GF2Matrix(n, 'U', sr);
        GF2Matrix rm = (GF2Matrix) lm.rightMultiply(um);
        Permutation p = new Permutation(n, sr);
        int[] pVec = p.getVector();
        int[][] matrix = new int[n][length];
        for (int i = 0; i < n; i++) {
            System.arraycopy(rm.matrix[pVec[i]], 0, matrix[i], 0, length);
        }
        result[0] = new GF2Matrix(n, matrix);
        GF2Matrix invLm = new GF2Matrix(n, 'I');
        for (int i2 = 0; i2 < n; i2++) {
            int rest = i2 & 31;
            int q = i2 >>> 5;
            int r = 1 << rest;
            for (int j = i2 + 1; j < n; j++) {
                int b = lm.matrix[j][q] & r;
                if (b != 0) {
                    for (int k = 0; k <= q; k++) {
                        int[] iArr = invLm.matrix[j];
                        int i3 = k;
                        iArr[i3] = iArr[i3] ^ invLm.matrix[i2][k];
                    }
                }
            }
        }
        GF2Matrix invUm = new GF2Matrix(n, 'I');
        for (int i4 = n - 1; i4 >= 0; i4--) {
            int rest2 = i4 & 31;
            int q2 = i4 >>> 5;
            int r2 = 1 << rest2;
            for (int j2 = i4 - 1; j2 >= 0; j2--) {
                int b2 = um.matrix[j2][q2] & r2;
                if (b2 != 0) {
                    for (int k2 = q2; k2 < length; k2++) {
                        int[] iArr2 = invUm.matrix[j2];
                        int i5 = k2;
                        iArr2[i5] = iArr2[i5] ^ invUm.matrix[i4][k2];
                    }
                }
            }
        }
        result[1] = (GF2Matrix) invUm.rightMultiply(invLm.rightMultiply(p));
        return result;
    }

    public int[][] getIntArray() {
        return this.matrix;
    }

    public int getLength() {
        return this.length;
    }

    public int[] getRow(int index) {
        return this.matrix[index];
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public byte[] getEncoded() {
        int n = (this.numColumns + 7) >>> 3;
        byte[] enc = new byte[(n * this.numRows) + 8];
        LittleEndianConversions.I2OSP(this.numRows, enc, 0);
        LittleEndianConversions.I2OSP(this.numColumns, enc, 4);
        int q = this.numColumns >>> 5;
        int r = this.numColumns & 31;
        int count = 8;
        for (int i = 0; i < this.numRows; i++) {
            int j = 0;
            while (j < q) {
                LittleEndianConversions.I2OSP(this.matrix[i][j], enc, count);
                j++;
                count += 4;
            }
            for (int j2 = 0; j2 < r; j2 += 8) {
                int i2 = count;
                count++;
                enc[i2] = (byte) ((this.matrix[i][q] >>> j2) & 255);
            }
        }
        return enc;
    }

    public double getHammingWeight() {
        int d;
        double counter = 0.0d;
        double elementCounter = 0.0d;
        int rest = this.numColumns & 31;
        if (rest == 0) {
            d = this.length;
        } else {
            d = this.length - 1;
        }
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < d; j++) {
                int a = this.matrix[i][j];
                for (int k = 0; k < 32; k++) {
                    int b = (a >>> k) & 1;
                    counter += (double) b;
                    elementCounter += 1.0d;
                }
            }
            int a2 = this.matrix[i][this.length - 1];
            for (int k2 = 0; k2 < rest; k2++) {
                int b2 = (a2 >>> k2) & 1;
                counter += (double) b2;
                elementCounter += 1.0d;
            }
        }
        return counter / elementCounter;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public boolean isZero() {
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.length; j++) {
                if (this.matrix[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public GF2Matrix getLeftSubMatrix() {
        if (this.numColumns <= this.numRows) {
            throw new ArithmeticException("empty submatrix");
        }
        int length = (this.numRows + 31) >> 5;
        int[][] result = new int[this.numRows][length];
        int bitMask = (1 << (this.numRows & 31)) - 1;
        if (bitMask == 0) {
            bitMask = -1;
        }
        for (int i = this.numRows - 1; i >= 0; i--) {
            System.arraycopy(this.matrix[i], 0, result[i], 0, length);
            int[] iArr = result[i];
            int i2 = length - 1;
            iArr[i2] = iArr[i2] & bitMask;
        }
        return new GF2Matrix(this.numRows, result);
    }

    public GF2Matrix extendLeftCompactForm() {
        int newNumColumns = this.numColumns + this.numRows;
        GF2Matrix result = new GF2Matrix(this.numRows, newNumColumns);
        int ind = (this.numRows - 1) + this.numColumns;
        int i = this.numRows - 1;
        while (i >= 0) {
            System.arraycopy(this.matrix[i], 0, result.matrix[i], 0, this.length);
            int[] iArr = result.matrix[i];
            int i2 = ind >> 5;
            iArr[i2] = iArr[i2] | (1 << (ind & 31));
            i--;
            ind--;
        }
        return result;
    }

    public GF2Matrix getRightSubMatrix() {
        if (this.numColumns <= this.numRows) {
            throw new ArithmeticException("empty submatrix");
        }
        int q = this.numRows >> 5;
        int r = this.numRows & 31;
        GF2Matrix result = new GF2Matrix(this.numRows, this.numColumns - this.numRows);
        for (int i = this.numRows - 1; i >= 0; i--) {
            if (r != 0) {
                int ind = q;
                for (int j = 0; j < result.length - 1; j++) {
                    int i2 = ind;
                    ind++;
                    result.matrix[i][j] = (this.matrix[i][i2] >>> r) | (this.matrix[i][ind] << (32 - r));
                }
                int i3 = ind;
                int ind2 = ind + 1;
                result.matrix[i][result.length - 1] = this.matrix[i][i3] >>> r;
                if (ind2 < this.length) {
                    int[] iArr = result.matrix[i];
                    int i4 = result.length - 1;
                    iArr[i4] = iArr[i4] | (this.matrix[i][ind2] << (32 - r));
                }
            } else {
                System.arraycopy(this.matrix[i], q, result.matrix[i], 0, result.length);
            }
        }
        return result;
    }

    public GF2Matrix extendRightCompactForm() {
        GF2Matrix result = new GF2Matrix(this.numRows, this.numRows + this.numColumns);
        int q = this.numRows >> 5;
        int r = this.numRows & 31;
        for (int i = this.numRows - 1; i >= 0; i--) {
            int[] iArr = result.matrix[i];
            int i2 = i >> 5;
            iArr[i2] = iArr[i2] | (1 << (i & 31));
            if (r != 0) {
                int ind = q;
                for (int j = 0; j < this.length - 1; j++) {
                    int mw = this.matrix[i][j];
                    int[] iArr2 = result.matrix[i];
                    int i3 = ind;
                    ind++;
                    iArr2[i3] = iArr2[i3] | (mw << r);
                    int[] iArr3 = result.matrix[i];
                    iArr3[ind] = iArr3[ind] | (mw >>> (32 - r));
                }
                int mw2 = this.matrix[i][this.length - 1];
                int[] iArr4 = result.matrix[i];
                int i4 = ind;
                int ind2 = ind + 1;
                iArr4[i4] = iArr4[i4] | (mw2 << r);
                if (ind2 < result.length) {
                    int[] iArr5 = result.matrix[i];
                    iArr5[ind2] = iArr5[ind2] | (mw2 >>> (32 - r));
                }
            } else {
                System.arraycopy(this.matrix[i], 0, result.matrix[i], q, this.length);
            }
        }
        return result;
    }

    public Matrix computeTranspose() {
        int[][] result = new int[this.numColumns][(this.numRows + 31) >>> 5];
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numColumns; j++) {
                int qs = j >>> 5;
                int rs = j & 31;
                int b = (this.matrix[i][qs] >>> rs) & 1;
                int qt = i >>> 5;
                int rt = i & 31;
                if (b == 1) {
                    int[] iArr = result[j];
                    iArr[qt] = iArr[qt] | (1 << rt);
                }
            }
        }
        return new GF2Matrix(this.numRows, result);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public Matrix computeInverse() {
        if (this.numRows != this.numColumns) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
        int[][] tmpMatrix = new int[this.numRows][this.length];
        for (int i = this.numRows - 1; i >= 0; i--) {
            tmpMatrix[i] = IntUtils.clone(this.matrix[i]);
        }
        int[][] invMatrix = new int[this.numRows][this.length];
        for (int i2 = this.numRows - 1; i2 >= 0; i2--) {
            int r = i2 & 31;
            invMatrix[i2][i2 >> 5] = 1 << r;
        }
        for (int i3 = 0; i3 < this.numRows; i3++) {
            int q = i3 >> 5;
            int bitMask = 1 << (i3 & 31);
            if ((tmpMatrix[i3][q] & bitMask) == 0) {
                boolean foundNonZero = false;
                int j = i3 + 1;
                while (j < this.numRows) {
                    if ((tmpMatrix[j][q] & bitMask) != 0) {
                        foundNonZero = true;
                        swapRows(tmpMatrix, i3, j);
                        swapRows(invMatrix, i3, j);
                        j = this.numRows;
                    }
                    j++;
                }
                if (!foundNonZero) {
                    throw new ArithmeticException("Matrix is not invertible.");
                }
            }
            for (int j2 = this.numRows - 1; j2 >= 0; j2--) {
                if (j2 != i3 && (tmpMatrix[j2][q] & bitMask) != 0) {
                    addToRow(tmpMatrix[i3], tmpMatrix[j2], q);
                    addToRow(invMatrix[i3], invMatrix[j2], 0);
                }
            }
        }
        return new GF2Matrix(this.numColumns, invMatrix);
    }

    public Matrix leftMultiply(Permutation p) {
        int[] pVec = p.getVector();
        if (pVec.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        int[][] result = new int[this.numRows][];
        for (int i = this.numRows - 1; i >= 0; i--) {
            result[i] = IntUtils.clone(this.matrix[pVec[i]]);
        }
        return new GF2Matrix(this.numRows, result);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public Vector leftMultiply(Vector vec) {
        if (!(vec instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vec.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        int[] v = ((GF2Vector) vec).getVecArray();
        int[] res = new int[this.length];
        int q = this.numRows >> 5;
        int r = 1 << (this.numRows & 31);
        int row = 0;
        for (int i = 0; i < q; i++) {
            int bitMask = 1;
            do {
                int b = v[i] & bitMask;
                if (b != 0) {
                    for (int j = 0; j < this.length; j++) {
                        int i2 = j;
                        res[i2] = res[i2] ^ this.matrix[row][j];
                    }
                }
                row++;
                bitMask <<= 1;
            } while (bitMask != 0);
        }
        int i3 = 1;
        while (true) {
            int bitMask2 = i3;
            if (bitMask2 != r) {
                int b2 = v[q] & bitMask2;
                if (b2 != 0) {
                    for (int j2 = 0; j2 < this.length; j2++) {
                        int i4 = j2;
                        res[i4] = res[i4] ^ this.matrix[row][j2];
                    }
                }
                row++;
                i3 = bitMask2 << 1;
            } else {
                return new GF2Vector(res, this.numColumns);
            }
        }
    }

    public Vector leftMultiplyLeftCompactForm(Vector vec) {
        if (!(vec instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vec.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        int[] v = ((GF2Vector) vec).getVecArray();
        int[] res = new int[((this.numRows + this.numColumns) + 31) >>> 5];
        int words = this.numRows >>> 5;
        int row = 0;
        for (int i = 0; i < words; i++) {
            int bitMask = 1;
            do {
                int b = v[i] & bitMask;
                if (b != 0) {
                    for (int j = 0; j < this.length; j++) {
                        int i2 = j;
                        res[i2] = res[i2] ^ this.matrix[row][j];
                    }
                    int q = (this.numColumns + row) >>> 5;
                    int r = (this.numColumns + row) & 31;
                    res[q] = res[q] | (1 << r);
                }
                row++;
                bitMask <<= 1;
            } while (bitMask != 0);
        }
        int rem = 1 << (this.numRows & 31);
        int i3 = 1;
        while (true) {
            int bitMask2 = i3;
            if (bitMask2 != rem) {
                int b2 = v[words] & bitMask2;
                if (b2 != 0) {
                    for (int j2 = 0; j2 < this.length; j2++) {
                        int i4 = j2;
                        res[i4] = res[i4] ^ this.matrix[row][j2];
                    }
                    int q2 = (this.numColumns + row) >>> 5;
                    int r2 = (this.numColumns + row) & 31;
                    res[q2] = res[q2] | (1 << r2);
                }
                row++;
                i3 = bitMask2 << 1;
            } else {
                return new GF2Vector(res, this.numRows + this.numColumns);
            }
        }
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public Matrix rightMultiply(Matrix mat) {
        int d;
        if (!(mat instanceof GF2Matrix)) {
            throw new ArithmeticException("matrix is not defined over GF(2)");
        }
        if (mat.numRows != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        GF2Matrix a = (GF2Matrix) mat;
        GF2Matrix result = new GF2Matrix(this.numRows, mat.numColumns);
        int rest = this.numColumns & 31;
        if (rest == 0) {
            d = this.length;
        } else {
            d = this.length - 1;
        }
        for (int i = 0; i < this.numRows; i++) {
            int count = 0;
            for (int j = 0; j < d; j++) {
                int e = this.matrix[i][j];
                for (int h = 0; h < 32; h++) {
                    int b = e & (1 << h);
                    if (b != 0) {
                        for (int g = 0; g < a.length; g++) {
                            int[] iArr = result.matrix[i];
                            int i2 = g;
                            iArr[i2] = iArr[i2] ^ a.matrix[count][g];
                        }
                    }
                    count++;
                }
            }
            int e2 = this.matrix[i][this.length - 1];
            for (int h2 = 0; h2 < rest; h2++) {
                int b2 = e2 & (1 << h2);
                if (b2 != 0) {
                    for (int g2 = 0; g2 < a.length; g2++) {
                        int[] iArr2 = result.matrix[i];
                        int i3 = g2;
                        iArr2[i3] = iArr2[i3] ^ a.matrix[count][g2];
                    }
                }
                count++;
            }
        }
        return result;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public Matrix rightMultiply(Permutation p) {
        int[] pVec = p.getVector();
        if (pVec.length != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        GF2Matrix result = new GF2Matrix(this.numRows, this.numColumns);
        for (int i = this.numColumns - 1; i >= 0; i--) {
            int q = i >>> 5;
            int r = i & 31;
            int pq = pVec[i] >>> 5;
            int pr = pVec[i] & 31;
            for (int j = this.numRows - 1; j >= 0; j--) {
                int[] iArr = result.matrix[j];
                iArr[q] = iArr[q] | (((this.matrix[j][pq] >>> pr) & 1) << r);
            }
        }
        return result;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public Vector rightMultiply(Vector vec) {
        if (!(vec instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vec.length != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        int[] v = ((GF2Vector) vec).getVecArray();
        int[] res = new int[(this.numRows + 31) >>> 5];
        for (int i = 0; i < this.numRows; i++) {
            int help = 0;
            for (int j = 0; j < this.length; j++) {
                help ^= this.matrix[i][j] & v[j];
            }
            int bitValue = 0;
            for (int j2 = 0; j2 < 32; j2++) {
                bitValue ^= (help >>> j2) & 1;
            }
            if (bitValue == 1) {
                int i2 = i >>> 5;
                res[i2] = res[i2] | (1 << (i & 31));
            }
        }
        return new GF2Vector(res, this.numRows);
    }

    public Vector rightMultiplyRightCompactForm(Vector vec) {
        if (!(vec instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vec.length != this.numColumns + this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        int[] v = ((GF2Vector) vec).getVecArray();
        int[] res = new int[(this.numRows + 31) >>> 5];
        int q = this.numRows >> 5;
        int r = this.numRows & 31;
        for (int i = 0; i < this.numRows; i++) {
            int help = (v[i >> 5] >>> (i & 31)) & 1;
            int vInd = q;
            if (r != 0) {
                for (int j = 0; j < this.length - 1; j++) {
                    int i2 = vInd;
                    vInd++;
                    help ^= this.matrix[i][j] & ((v[i2] >>> r) | (v[vInd] << (32 - r)));
                }
                int i3 = vInd;
                int vInd2 = vInd + 1;
                int vw = v[i3] >>> r;
                if (vInd2 < v.length) {
                    vw |= v[vInd2] << (32 - r);
                }
                help ^= this.matrix[i][this.length - 1] & vw;
            } else {
                for (int j2 = 0; j2 < this.length; j2++) {
                    int i4 = vInd;
                    vInd++;
                    help ^= this.matrix[i][j2] & v[i4];
                }
            }
            int bitValue = 0;
            for (int j3 = 0; j3 < 32; j3++) {
                bitValue ^= help & 1;
                help >>>= 1;
            }
            if (bitValue == 1) {
                int i5 = i >> 5;
                res[i5] = res[i5] | (1 << (i & 31));
            }
        }
        return new GF2Vector(res, this.numRows);
    }

    public boolean equals(Object other) {
        if (!(other instanceof GF2Matrix)) {
            return false;
        }
        GF2Matrix otherMatrix = (GF2Matrix) other;
        if (this.numRows != otherMatrix.numRows || this.numColumns != otherMatrix.numColumns || this.length != otherMatrix.length) {
            return false;
        }
        for (int i = 0; i < this.numRows; i++) {
            if (!IntUtils.equals(this.matrix[i], otherMatrix.matrix[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = (((this.numRows * 31) + this.numColumns) * 31) + this.length;
        for (int i = 0; i < this.numRows; i++) {
            hash = (hash * 31) + this.matrix[i].hashCode();
        }
        return hash;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Matrix
    public String toString() {
        int d;
        int rest = this.numColumns & 31;
        if (rest == 0) {
            d = this.length;
        } else {
            d = this.length - 1;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.numRows; i++) {
            buf.append(i + ": ");
            for (int j = 0; j < d; j++) {
                int a = this.matrix[i][j];
                for (int k = 0; k < 32; k++) {
                    int b = (a >>> k) & 1;
                    if (b == 0) {
                        buf.append('0');
                    } else {
                        buf.append('1');
                    }
                }
                buf.append(' ');
            }
            int a2 = this.matrix[i][this.length - 1];
            for (int k2 = 0; k2 < rest; k2++) {
                int b2 = (a2 >>> k2) & 1;
                if (b2 == 0) {
                    buf.append('0');
                } else {
                    buf.append('1');
                }
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    private static void swapRows(int[][] matrix, int first, int second) {
        int[] tmp = matrix[first];
        matrix[first] = matrix[second];
        matrix[second] = tmp;
    }

    private static void addToRow(int[] fromRow, int[] toRow, int startIndex) {
        for (int i = toRow.length - 1; i >= startIndex; i--) {
            toRow[i] = fromRow[i] ^ toRow[i];
        }
    }
}
