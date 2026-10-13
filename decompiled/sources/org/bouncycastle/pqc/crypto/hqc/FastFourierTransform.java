package org.bouncycastle.pqc.crypto.hqc;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/FastFourierTransform.class */
class FastFourierTransform {
    FastFourierTransform() {
    }

    static void fastFourierTransform(int[] iArr, int[] iArr2, int i, int i2) {
        int i3 = 1 << i2;
        int[] iArr3 = new int[i3];
        int[] iArr4 = new int[i3];
        int[] iArr5 = new int[8 - 1];
        int[] iArr6 = new int[128];
        int[] iArr7 = new int[128];
        int[] iArr8 = new int[8 - 1];
        int[] iArr9 = new int[128];
        computeFFTBetas(iArr8, 8);
        computeSubsetSum(iArr9, iArr8, 8 - 1);
        computeRadix(iArr3, iArr4, iArr2, i2, i2);
        for (int i4 = 0; i4 < 8 - 1; i4++) {
            iArr5[i4] = GFCalculator.mult(iArr8[i4], iArr8[i4]) ^ iArr8[i4];
        }
        computeFFTRec(iArr6, iArr3, (i + 1) / 2, 8 - 1, i2 - 1, iArr5, i2, 8);
        computeFFTRec(iArr7, iArr4, i / 2, 8 - 1, i2 - 1, iArr5, i2, 8);
        int i5 = 1 << (8 - 1);
        System.arraycopy(iArr7, 0, iArr, i5, i5);
        iArr[0] = iArr6[0];
        iArr[i5] = iArr[i5] ^ iArr6[0];
        for (int i6 = 1; i6 < i5; i6++) {
            iArr[i6] = iArr6[i6] ^ GFCalculator.mult(iArr9[i6], iArr7[i6]);
            int i7 = i5 + i6;
            iArr[i7] = iArr[i7] ^ iArr[i6];
        }
    }

    static void computeFFTBetas(int[] iArr, int i) {
        for (int i2 = 0; i2 < i - 1; i2++) {
            iArr[i2] = 1 << ((i - 1) - i2);
        }
    }

    static void computeSubsetSum(int[] iArr, int[] iArr2, int i) {
        iArr[0] = 0;
        for (int i2 = 0; i2 < i; i2++) {
            for (int i3 = 0; i3 < (1 << i2); i3++) {
                iArr[(1 << i2) + i3] = iArr2[i2] ^ iArr[i3];
            }
        }
    }

    static void computeRadix(int[] iArr, int[] iArr2, int[] iArr3, int i, int i2) {
        switch (i) {
            case 1:
                iArr[0] = iArr3[0];
                iArr2[0] = iArr3[1];
                break;
            case 2:
                iArr[0] = iArr3[0];
                iArr[1] = iArr3[2] ^ iArr3[3];
                iArr2[0] = iArr3[1] ^ iArr[1];
                iArr2[1] = iArr3[3];
                break;
            case 3:
                iArr[0] = iArr3[0];
                iArr[2] = iArr3[4] ^ iArr3[6];
                iArr[3] = iArr3[6] ^ iArr3[7];
                iArr2[1] = (iArr3[3] ^ iArr3[5]) ^ iArr3[7];
                iArr2[2] = iArr3[5] ^ iArr3[6];
                iArr2[3] = iArr3[7];
                iArr[1] = (iArr3[2] ^ iArr[2]) ^ iArr2[1];
                iArr2[0] = iArr3[1] ^ iArr[1];
                break;
            case 4:
                iArr[4] = iArr3[8] ^ iArr3[12];
                iArr[6] = iArr3[12] ^ iArr3[14];
                iArr[7] = iArr3[14] ^ iArr3[15];
                iArr2[5] = iArr3[11] ^ iArr3[13];
                iArr2[6] = iArr3[13] ^ iArr3[14];
                iArr2[7] = iArr3[15];
                iArr[5] = (iArr3[10] ^ iArr3[12]) ^ iArr2[5];
                iArr2[4] = (iArr3[9] ^ iArr3[13]) ^ iArr[5];
                iArr[0] = iArr3[0];
                iArr2[3] = (iArr3[7] ^ iArr3[11]) ^ iArr3[15];
                iArr[3] = ((iArr3[6] ^ iArr3[10]) ^ iArr3[14]) ^ iArr2[3];
                iArr[2] = ((iArr3[4] ^ iArr[4]) ^ iArr[3]) ^ iArr2[3];
                iArr2[1] = (((iArr3[3] ^ iArr3[5]) ^ iArr3[9]) ^ iArr3[13]) ^ iArr2[3];
                iArr2[2] = (iArr3[3] ^ iArr2[1]) ^ iArr[3];
                iArr[1] = (iArr3[2] ^ iArr[2]) ^ iArr2[1];
                iArr2[0] = iArr3[1] ^ iArr[1];
                break;
            default:
                computeRadixBig(iArr, iArr2, iArr3, i, i2);
                break;
        }
    }

    static void computeRadixBig(int[] iArr, int[] iArr2, int[] iArr3, int i, int i2) {
        int i3 = 1 << (i - 2);
        int i4 = 1 << (i2 - 2);
        int[] iArr4 = new int[(2 * i4) + 1];
        int[] iArr5 = new int[(2 * i4) + 1];
        int[] iArr6 = new int[i4];
        int[] iArr7 = new int[i4];
        int[] iArr8 = new int[i4];
        int[] iArr9 = new int[i4];
        Utils.copyBytes(iArr3, 3 * i3, iArr4, 0, 2 * i3);
        Utils.copyBytes(iArr3, 3 * i3, iArr4, i3, 2 * i3);
        Utils.copyBytes(iArr3, 0, iArr5, 0, 4 * i3);
        for (int i5 = 0; i5 < i3; i5++) {
            int i6 = i5;
            iArr4[i6] = iArr4[i6] ^ iArr3[(2 * i3) + i5];
            int i7 = i3 + i5;
            iArr5[i7] = iArr5[i7] ^ iArr4[i5];
        }
        computeRadix(iArr6, iArr7, iArr4, i - 1, i2);
        computeRadix(iArr8, iArr9, iArr5, i - 1, i2);
        Utils.copyBytes(iArr8, 0, iArr, 0, 2 * i3);
        Utils.copyBytes(iArr6, 0, iArr, i3, 2 * i3);
        Utils.copyBytes(iArr9, 0, iArr2, 0, 2 * i3);
        Utils.copyBytes(iArr7, 0, iArr2, i3, 2 * i3);
    }

    static void computeFFTRec(int[] iArr, int[] iArr2, int i, int i2, int i3, int[] iArr3, int i4, int i5) {
        int i6 = 1 << (i4 - 2);
        int i7 = 1 << (i5 - 2);
        int[] iArr4 = new int[i6];
        int[] iArr5 = new int[i6];
        int[] iArr6 = new int[i5 - 2];
        int[] iArr7 = new int[i5 - 2];
        int[] iArr8 = new int[i7];
        int[] iArr9 = new int[i7];
        int[] iArr10 = new int[i7];
        int[] iArr11 = new int[(i5 - i4) + 1];
        if (i3 == 1) {
            for (int i8 = 0; i8 < i2; i8++) {
                iArr11[i8] = GFCalculator.mult(iArr3[i8], iArr2[1]);
            }
            iArr[0] = iArr2[0];
            int i9 = 1;
            for (int i10 = 0; i10 < i2; i10++) {
                for (int i11 = 0; i11 < i9; i11++) {
                    iArr[i9 + i11] = iArr[i11] ^ iArr11[i10];
                }
                i9 <<= 1;
            }
            return;
        }
        if (iArr3[i2 - 1] != 1) {
            int iMult = 1;
            int i12 = 1 << i3;
            for (int i13 = 1; i13 < i12; i13++) {
                iMult = GFCalculator.mult(iMult, iArr3[i2 - 1]);
                iArr2[i13] = GFCalculator.mult(iMult, iArr2[i13]);
            }
        }
        computeRadix(iArr4, iArr5, iArr2, i3, i4);
        for (int i14 = 0; i14 < i2 - 1; i14++) {
            iArr6[i14] = GFCalculator.mult(iArr3[i14], GFCalculator.inverse(iArr3[i2 - 1]));
            iArr7[i14] = GFCalculator.mult(iArr6[i14], iArr6[i14]) ^ iArr6[i14];
        }
        computeSubsetSum(iArr8, iArr6, i2 - 1);
        computeFFTRec(iArr9, iArr4, (i + 1) / 2, i2 - 1, i3 - 1, iArr7, i4, i5);
        int i15 = 1 << ((i2 - 1) & 15);
        if (i <= 3) {
            iArr[0] = iArr9[0];
            iArr[i15] = iArr9[0] ^ iArr5[0];
            for (int i16 = 1; i16 < i15; i16++) {
                iArr[i16] = iArr9[i16] ^ GFCalculator.mult(iArr8[i16], iArr5[0]);
                iArr[i15 + i16] = iArr[i16] ^ iArr5[0];
            }
            return;
        }
        computeFFTRec(iArr10, iArr5, i / 2, i2 - 1, i3 - 1, iArr7, i4, i5);
        System.arraycopy(iArr10, 0, iArr, i15, i15);
        iArr[0] = iArr9[0];
        iArr[i15] = iArr[i15] ^ iArr9[0];
        for (int i17 = 1; i17 < i15; i17++) {
            iArr[i17] = iArr9[i17] ^ GFCalculator.mult(iArr8[i17], iArr10[i17]);
            int i18 = i15 + i17;
            iArr[i18] = iArr[i18] ^ iArr[i17];
        }
    }

    static void fastFourierTransformGetError(byte[] bArr, int[] iArr, int i, int[] iArr2) {
        int[] iArr3 = new int[8 - 1];
        int[] iArr4 = new int[i];
        computeFFTBetas(iArr3, 8);
        computeSubsetSum(iArr4, iArr3, 8 - 1);
        bArr[0] = (byte) (bArr[0] ^ (1 ^ Utils.toUnsigned16Bits((-iArr[0]) >> 15)));
        bArr[0] = (byte) (bArr[0] ^ (1 ^ Utils.toUnsigned16Bits((-iArr[i]) >> 15)));
        for (int i2 = 1; i2 < i; i2++) {
            int i3 = 255 - iArr2[iArr4[i2]];
            bArr[i3] = (byte) (bArr[i3] ^ (1 ^ Math.abs((-iArr[i2]) >> 15)));
            int i4 = 255 - iArr2[iArr4[i2] ^ 1];
            bArr[i4] = (byte) (bArr[i4] ^ (1 ^ Math.abs((-iArr[i + i2]) >> 15)));
        }
    }
}
