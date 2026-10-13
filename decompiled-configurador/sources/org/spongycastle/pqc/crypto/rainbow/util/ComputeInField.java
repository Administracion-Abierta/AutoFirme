package org.spongycastle.pqc.crypto.rainbow.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/rainbow/util/ComputeInField.class */
public class ComputeInField {
    private short[][] A;
    short[] x;

    public short[] solveEquation(short[][] B, short[] b) {
        if (B.length != b.length) {
            return null;
        }
        try {
            this.A = new short[B.length][B.length + 1];
            this.x = new short[B.length];
            for (int i = 0; i < B.length; i++) {
                for (int j = 0; j < B[0].length; j++) {
                    this.A[i][j] = B[i][j];
                }
            }
            for (int i2 = 0; i2 < b.length; i2++) {
                this.A[i2][b.length] = GF2Field.addElem(b[i2], this.A[i2][b.length]);
            }
            computeZerosUnder(false);
            substitute();
            return this.x;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public short[][] inverse(short[][] coef) {
        try {
            this.A = new short[coef.length][2 * coef.length];
            if (coef.length != coef[0].length) {
                throw new RuntimeException("The matrix is not invertible. Please choose another one!");
            }
            for (int i = 0; i < coef.length; i++) {
                for (int j = 0; j < coef.length; j++) {
                    this.A[i][j] = coef[i][j];
                }
                for (int j2 = coef.length; j2 < 2 * coef.length; j2++) {
                    this.A[i][j2] = 0;
                }
                this.A[i][i + this.A.length] = 1;
            }
            computeZerosUnder(true);
            for (int i2 = 0; i2 < this.A.length; i2++) {
                short factor = GF2Field.invElem(this.A[i2][i2]);
                for (int j3 = i2; j3 < 2 * this.A.length; j3++) {
                    this.A[i2][j3] = GF2Field.multElem(this.A[i2][j3], factor);
                }
            }
            computeZerosAbove();
            short[][] inverse = new short[this.A.length][this.A.length];
            for (int i3 = 0; i3 < this.A.length; i3++) {
                for (int j4 = this.A.length; j4 < 2 * this.A.length; j4++) {
                    inverse[i3][j4 - this.A.length] = this.A[i3][j4];
                }
            }
            return inverse;
        } catch (RuntimeException e) {
            return (short[][]) null;
        }
    }

    private void computeZerosUnder(boolean usedForInverse) throws RuntimeException {
        int length;
        if (usedForInverse) {
            length = 2 * this.A.length;
        } else {
            length = this.A.length + 1;
        }
        for (int k = 0; k < this.A.length - 1; k++) {
            for (int i = k + 1; i < this.A.length; i++) {
                short factor1 = this.A[i][k];
                short factor2 = GF2Field.invElem(this.A[k][k]);
                if (factor2 == 0) {
                    throw new IllegalStateException("Matrix not invertible! We have to choose another one!");
                }
                for (int j = k; j < length; j++) {
                    short tmp = GF2Field.multElem(this.A[k][j], factor2);
                    this.A[i][j] = GF2Field.addElem(this.A[i][j], GF2Field.multElem(factor1, tmp));
                }
            }
        }
    }

    private void computeZerosAbove() throws RuntimeException {
        for (int k = this.A.length - 1; k > 0; k--) {
            for (int i = k - 1; i >= 0; i--) {
                short factor1 = this.A[i][k];
                short factor2 = GF2Field.invElem(this.A[k][k]);
                if (factor2 == 0) {
                    throw new RuntimeException("The matrix is not invertible");
                }
                for (int j = k; j < 2 * this.A.length; j++) {
                    short tmp = GF2Field.multElem(this.A[k][j], factor2);
                    this.A[i][j] = GF2Field.addElem(this.A[i][j], GF2Field.multElem(factor1, tmp));
                }
            }
        }
    }

    private void substitute() throws IllegalStateException {
        short temp = GF2Field.invElem(this.A[this.A.length - 1][this.A.length - 1]);
        if (temp == 0) {
            throw new IllegalStateException("The equation system is not solvable");
        }
        this.x[this.A.length - 1] = GF2Field.multElem(this.A[this.A.length - 1][this.A.length], temp);
        for (int i = this.A.length - 2; i >= 0; i--) {
            short tmp = this.A[i][this.A.length];
            for (int j = this.A.length - 1; j > i; j--) {
                tmp = GF2Field.addElem(tmp, GF2Field.multElem(this.A[i][j], this.x[j]));
            }
            short temp2 = GF2Field.invElem(this.A[i][i]);
            if (temp2 == 0) {
                throw new IllegalStateException("Not solvable equation system");
            }
            this.x[i] = GF2Field.multElem(tmp, temp2);
        }
    }

    public short[][] multiplyMatrix(short[][] M1, short[][] M2) throws RuntimeException {
        if (M1[0].length != M2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        this.A = new short[M1.length][M2[0].length];
        for (int i = 0; i < M1.length; i++) {
            for (int j = 0; j < M2.length; j++) {
                for (int k = 0; k < M2[0].length; k++) {
                    short tmp = GF2Field.multElem(M1[i][j], M2[j][k]);
                    this.A[i][k] = GF2Field.addElem(this.A[i][k], tmp);
                }
            }
        }
        return this.A;
    }

    public short[] multiplyMatrix(short[][] M1, short[] m) throws RuntimeException {
        if (M1[0].length != m.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[] B = new short[M1.length];
        for (int i = 0; i < M1.length; i++) {
            for (int j = 0; j < m.length; j++) {
                short tmp = GF2Field.multElem(M1[i][j], m[j]);
                B[i] = GF2Field.addElem(B[i], tmp);
            }
        }
        return B;
    }

    public short[] addVect(short[] vector1, short[] vector2) {
        if (vector1.length != vector2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[] rslt = new short[vector1.length];
        for (int n = 0; n < rslt.length; n++) {
            rslt[n] = GF2Field.addElem(vector1[n], vector2[n]);
        }
        return rslt;
    }

    public short[][] multVects(short[] vector1, short[] vector2) {
        if (vector1.length != vector2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[][] rslt = new short[vector1.length][vector2.length];
        for (int i = 0; i < vector1.length; i++) {
            for (int j = 0; j < vector2.length; j++) {
                rslt[i][j] = GF2Field.multElem(vector1[i], vector2[j]);
            }
        }
        return rslt;
    }

    public short[] multVect(short scalar, short[] vector) {
        short[] rslt = new short[vector.length];
        for (int n = 0; n < rslt.length; n++) {
            rslt[n] = GF2Field.multElem(scalar, vector[n]);
        }
        return rslt;
    }

    public short[][] multMatrix(short scalar, short[][] matrix) {
        short[][] rslt = new short[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                rslt[i][j] = GF2Field.multElem(scalar, matrix[i][j]);
            }
        }
        return rslt;
    }

    public short[][] addSquareMatrix(short[][] matrix1, short[][] matrix2) {
        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        short[][] rslt = new short[matrix1.length][matrix1.length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2.length; j++) {
                rslt[i][j] = GF2Field.addElem(matrix1[i][j], matrix2[i][j]);
            }
        }
        return rslt;
    }
}
