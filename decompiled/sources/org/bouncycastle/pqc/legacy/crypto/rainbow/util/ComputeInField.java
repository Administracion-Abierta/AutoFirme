package org.bouncycastle.pqc.legacy.crypto.rainbow.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/rainbow/util/ComputeInField.class */
public class ComputeInField {
    private short[][] A;
    short[] x;

    public short[] solveEquation(short[][] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            return null;
        }
        try {
            this.A = new short[sArr.length][sArr.length + 1];
            this.x = new short[sArr.length];
            for (int i = 0; i < sArr.length; i++) {
                for (int i2 = 0; i2 < sArr[0].length; i2++) {
                    this.A[i][i2] = sArr[i][i2];
                }
            }
            for (int i3 = 0; i3 < sArr2.length; i3++) {
                this.A[i3][sArr2.length] = GF2Field.addElem(sArr2[i3], this.A[i3][sArr2.length]);
            }
            computeZerosUnder(false);
            substitute();
            return this.x;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public short[][] inverse(short[][] sArr) {
        try {
            this.A = new short[sArr.length][2 * sArr.length];
            if (sArr.length != sArr[0].length) {
                throw new RuntimeException("The matrix is not invertible. Please choose another one!");
            }
            for (int i = 0; i < sArr.length; i++) {
                for (int i2 = 0; i2 < sArr.length; i2++) {
                    this.A[i][i2] = sArr[i][i2];
                }
                for (int length = sArr.length; length < 2 * sArr.length; length++) {
                    this.A[i][length] = 0;
                }
                this.A[i][i + this.A.length] = 1;
            }
            computeZerosUnder(true);
            for (int i3 = 0; i3 < this.A.length; i3++) {
                short sInvElem = GF2Field.invElem(this.A[i3][i3]);
                for (int i4 = i3; i4 < 2 * this.A.length; i4++) {
                    this.A[i3][i4] = GF2Field.multElem(this.A[i3][i4], sInvElem);
                }
            }
            computeZerosAbove();
            short[][] sArr2 = new short[this.A.length][this.A.length];
            for (int i5 = 0; i5 < this.A.length; i5++) {
                for (int length2 = this.A.length; length2 < 2 * this.A.length; length2++) {
                    sArr2[i5][length2 - this.A.length] = this.A[i5][length2];
                }
            }
            return sArr2;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void computeZerosUnder(boolean z) throws RuntimeException {
        int length = z ? 2 * this.A.length : this.A.length + 1;
        for (int i = 0; i < this.A.length - 1; i++) {
            for (int i2 = i + 1; i2 < this.A.length; i2++) {
                short s = this.A[i2][i];
                short sInvElem = GF2Field.invElem(this.A[i][i]);
                if (sInvElem == 0) {
                    throw new IllegalStateException("Matrix not invertible! We have to choose another one!");
                }
                for (int i3 = i; i3 < length; i3++) {
                    this.A[i2][i3] = GF2Field.addElem(this.A[i2][i3], GF2Field.multElem(s, GF2Field.multElem(this.A[i][i3], sInvElem)));
                }
            }
        }
    }

    private void computeZerosAbove() throws RuntimeException {
        for (int length = this.A.length - 1; length > 0; length--) {
            for (int i = length - 1; i >= 0; i--) {
                short s = this.A[i][length];
                short sInvElem = GF2Field.invElem(this.A[length][length]);
                if (sInvElem == 0) {
                    throw new RuntimeException("The matrix is not invertible");
                }
                for (int i2 = length; i2 < 2 * this.A.length; i2++) {
                    this.A[i][i2] = GF2Field.addElem(this.A[i][i2], GF2Field.multElem(s, GF2Field.multElem(this.A[length][i2], sInvElem)));
                }
            }
        }
    }

    private void substitute() throws IllegalStateException {
        short sInvElem = GF2Field.invElem(this.A[this.A.length - 1][this.A.length - 1]);
        if (sInvElem == 0) {
            throw new IllegalStateException("The equation system is not solvable");
        }
        this.x[this.A.length - 1] = GF2Field.multElem(this.A[this.A.length - 1][this.A.length], sInvElem);
        for (int length = this.A.length - 2; length >= 0; length--) {
            short sAddElem = this.A[length][this.A.length];
            for (int length2 = this.A.length - 1; length2 > length; length2--) {
                sAddElem = GF2Field.addElem(sAddElem, GF2Field.multElem(this.A[length][length2], this.x[length2]));
            }
            short sInvElem2 = GF2Field.invElem(this.A[length][length]);
            if (sInvElem2 == 0) {
                throw new IllegalStateException("Not solvable equation system");
            }
            this.x[length] = GF2Field.multElem(sAddElem, sInvElem2);
        }
    }

    public short[][] multiplyMatrix(short[][] sArr, short[][] sArr2) throws RuntimeException {
        if (sArr[0].length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        this.A = new short[sArr.length][sArr2[0].length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                for (int i3 = 0; i3 < sArr2[0].length; i3++) {
                    this.A[i][i3] = GF2Field.addElem(this.A[i][i3], GF2Field.multElem(sArr[i][i2], sArr2[i2][i3]));
                }
            }
        }
        return this.A;
    }

    public short[] multiplyMatrix(short[][] sArr, short[] sArr2) throws RuntimeException {
        if (sArr[0].length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[] sArr3 = new short[sArr.length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                sArr3[i] = GF2Field.addElem(sArr3[i], GF2Field.multElem(sArr[i][i2], sArr2[i2]));
            }
        }
        return sArr3;
    }

    public short[] addVect(short[] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[] sArr3 = new short[sArr.length];
        for (int i = 0; i < sArr3.length; i++) {
            sArr3[i] = GF2Field.addElem(sArr[i], sArr2[i]);
        }
        return sArr3;
    }

    public short[][] multVects(short[] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[][] sArr3 = new short[sArr.length][sArr2.length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                sArr3[i][i2] = GF2Field.multElem(sArr[i], sArr2[i2]);
            }
        }
        return sArr3;
    }

    public short[] multVect(short s, short[] sArr) {
        short[] sArr2 = new short[sArr.length];
        for (int i = 0; i < sArr2.length; i++) {
            sArr2[i] = GF2Field.multElem(s, sArr[i]);
        }
        return sArr2;
    }

    public short[][] multMatrix(short s, short[][] sArr) {
        short[][] sArr2 = new short[sArr.length][sArr[0].length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr[0].length; i2++) {
                sArr2[i][i2] = GF2Field.multElem(s, sArr[i][i2]);
            }
        }
        return sArr2;
    }

    public short[][] addSquareMatrix(short[][] sArr, short[][] sArr2) {
        if (sArr.length != sArr2.length || sArr[0].length != sArr2[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        short[][] sArr3 = new short[sArr.length][sArr.length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                sArr3[i][i2] = GF2Field.addElem(sArr[i][i2], sArr2[i][i2]);
            }
        }
        return sArr3;
    }
}
