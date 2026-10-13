package org.bouncycastle.pqc.crypto.rainbow;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/ComputeInField.class */
class ComputeInField {
    public short[] solveEquation(short[][] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            return null;
        }
        try {
            short[][] sArr3 = new short[sArr.length][sArr.length + 1];
            short[] sArr4 = new short[sArr.length];
            for (int i = 0; i < sArr.length; i++) {
                System.arraycopy(sArr[i], 0, sArr3[i], 0, sArr[0].length);
                sArr3[i][sArr2.length] = GF2Field.addElem(sArr2[i], sArr3[i][sArr2.length]);
            }
            gaussElim(sArr3);
            for (int i2 = 0; i2 < sArr3.length; i2++) {
                sArr4[i2] = sArr3[i2][sArr2.length];
            }
            return sArr4;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public short[][] inverse(short[][] sArr) {
        if (sArr.length != sArr[0].length) {
            throw new RuntimeException("The matrix is not invertible. Please choose another one!");
        }
        try {
            short[][] sArr2 = new short[sArr.length][2 * sArr.length];
            for (int i = 0; i < sArr.length; i++) {
                System.arraycopy(sArr[i], 0, sArr2[i], 0, sArr.length);
                for (int length = sArr.length; length < 2 * sArr.length; length++) {
                    sArr2[i][length] = 0;
                }
                sArr2[i][i + sArr2.length] = 1;
            }
            gaussElim(sArr2);
            short[][] sArr3 = new short[sArr2.length][sArr2.length];
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                for (int length2 = sArr2.length; length2 < 2 * sArr2.length; length2++) {
                    sArr3[i2][length2 - sArr2.length] = sArr2[i2][length2];
                }
            }
            return sArr3;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void gaussElim(short[][] sArr) {
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = i + 1; i2 < sArr.length; i2++) {
                if (sArr[i][i] == 0) {
                    for (int i3 = i; i3 < sArr[0].length; i3++) {
                        sArr[i][i3] = GF2Field.addElem(sArr[i][i3], sArr[i2][i3]);
                    }
                }
            }
            short sInvElem = GF2Field.invElem(sArr[i][i]);
            if (sInvElem == 0) {
                throw new RuntimeException("The matrix is not invertible");
            }
            sArr[i] = multVect(sInvElem, sArr[i]);
            for (int i4 = 0; i4 < sArr.length; i4++) {
                if (i != i4) {
                    short s = sArr[i4][i];
                    for (int i5 = i; i5 < sArr[0].length; i5++) {
                        sArr[i4][i5] = GF2Field.addElem(sArr[i4][i5], GF2Field.multElem(sArr[i][i5], s));
                    }
                }
            }
        }
    }

    public short[][] multiplyMatrix(short[][] sArr, short[][] sArr2) throws RuntimeException {
        if (sArr[0].length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[][] sArr3 = new short[sArr.length][sArr2[0].length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                for (int i3 = 0; i3 < sArr2[0].length; i3++) {
                    sArr3[i][i3] = GF2Field.addElem(sArr3[i][i3], GF2Field.multElem(sArr[i][i2], sArr2[i2][i3]));
                }
            }
        }
        return sArr3;
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

    public short multiplyMatrix_quad(short[][] sArr, short[] sArr2) throws RuntimeException {
        if (sArr.length != sArr[0].length || sArr[0].length != sArr2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[] sArr3 = new short[sArr.length];
        short sAddElem = 0;
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr2.length; i2++) {
                sArr3[i] = GF2Field.addElem(sArr3[i], GF2Field.multElem(sArr[i][i2], sArr2[i2]));
            }
            sAddElem = GF2Field.addElem(sAddElem, GF2Field.multElem(sArr3[i], sArr2[i]));
        }
        return sAddElem;
    }

    public short[] addVect(short[] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            throw new RuntimeException("Addition is not possible! vector1.length: " + sArr.length + " vector2.length: " + sArr2.length);
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

    public short[][] addMatrix(short[][] sArr, short[][] sArr2) {
        if (sArr.length != sArr2.length || sArr[0].length != sArr2[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        short[][] sArr3 = new short[sArr.length][sArr[0].length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr[0].length; i2++) {
                sArr3[i][i2] = GF2Field.addElem(sArr[i][i2], sArr2[i][i2]);
            }
        }
        return sArr3;
    }

    public short[][] addMatrixTranspose(short[][] sArr) {
        if (sArr.length != sArr[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        return addMatrix(sArr, transpose(sArr));
    }

    public short[][] transpose(short[][] sArr) {
        short[][] sArr2 = new short[sArr[0].length][sArr.length];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr[0].length; i2++) {
                sArr2[i2][i] = sArr[i][i2];
            }
        }
        return sArr2;
    }

    public short[][] to_UT(short[][] sArr) {
        if (sArr.length != sArr[0].length) {
            throw new RuntimeException("Computation to upper triangular matrix is not possible!");
        }
        short[][] sArr2 = new short[sArr.length][sArr.length];
        for (int i = 0; i < sArr.length; i++) {
            sArr2[i][i] = sArr[i][i];
            for (int i2 = i + 1; i2 < sArr[0].length; i2++) {
                sArr2[i][i2] = GF2Field.addElem(sArr[i][i2], sArr[i2][i]);
            }
        }
        return sArr2;
    }

    public short[][][] obfuscate_l1_polys(short[][] sArr, short[][][] sArr2, short[][][] sArr3) {
        if (sArr2[0].length != sArr3[0].length || sArr2[0][0].length != sArr3[0][0].length || sArr2.length != sArr[0].length || sArr3.length != sArr.length) {
            throw new RuntimeException("Multiplication not possible!");
        }
        short[][][] sArr4 = new short[sArr3.length][sArr3[0].length][sArr3[0][0].length];
        for (int i = 0; i < sArr2[0].length; i++) {
            for (int i2 = 0; i2 < sArr2[0][0].length; i2++) {
                for (int i3 = 0; i3 < sArr.length; i3++) {
                    for (int i4 = 0; i4 < sArr[0].length; i4++) {
                        sArr4[i3][i][i2] = GF2Field.addElem(sArr4[i3][i][i2], GF2Field.multElem(sArr[i3][i4], sArr2[i4][i][i2]));
                    }
                    sArr4[i3][i][i2] = GF2Field.addElem(sArr3[i3][i][i2], sArr4[i3][i][i2]);
                }
            }
        }
        return sArr4;
    }
}
