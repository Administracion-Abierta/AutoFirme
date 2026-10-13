package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/ReedMuller.class */
class ReedMuller {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/ReedMuller$Codeword.class */
    static class Codeword {
        int[] type32 = new int[4];
        int[] type8 = new int[16];
    }

    ReedMuller() {
    }

    static void encodeSub(Codeword codeword, int i) {
        int iBit0Mask = ((((Bit0Mask(i >> 7) ^ (Bit0Mask(i >> 0) & (-1431655766))) ^ (Bit0Mask(i >> 1) & (-858993460))) ^ (Bit0Mask(i >> 2) & (-252645136))) ^ (Bit0Mask(i >> 3) & (-16711936))) ^ (Bit0Mask(i >> 4) & (-65536));
        codeword.type32[0] = iBit0Mask;
        int iBit0Mask2 = iBit0Mask ^ Bit0Mask(i >> 5);
        codeword.type32[1] = iBit0Mask2;
        int iBit0Mask3 = iBit0Mask2 ^ Bit0Mask(i >> 6);
        codeword.type32[3] = iBit0Mask3;
        codeword.type32[2] = iBit0Mask3 ^ Bit0Mask(i >> 5);
    }

    private static void hadamardTransform(int[] iArr, int[] iArr2) {
        int[] iArrClone = Arrays.clone(iArr);
        int[] iArrClone2 = Arrays.clone(iArr2);
        for (int i = 0; i < 7; i++) {
            for (int i2 = 0; i2 < 64; i2++) {
                iArrClone2[i2] = iArrClone[2 * i2] + iArrClone[(2 * i2) + 1];
                iArrClone2[i2 + 64] = iArrClone[2 * i2] - iArrClone[(2 * i2) + 1];
            }
            int[] iArr3 = iArrClone;
            iArrClone = iArrClone2;
            iArrClone2 = iArr3;
        }
        System.arraycopy(iArrClone2, 0, iArr, 0, iArr.length);
        System.arraycopy(iArrClone, 0, iArr2, 0, iArr2.length);
    }

    private static void expandThenSum(int[] iArr, Codeword[] codewordArr, int i, int i2) {
        for (int i3 = 0; i3 < 4; i3++) {
            for (int i4 = 0; i4 < 32; i4++) {
                long j = (codewordArr[0 + i].type32[i3] >> i4) & 1;
                iArr[(i3 * 32) + i4] = (codewordArr[0 + i].type32[i3] >> i4) & 1;
            }
        }
        for (int i5 = 1; i5 < i2; i5++) {
            for (int i6 = 0; i6 < 4; i6++) {
                for (int i7 = 0; i7 < 32; i7++) {
                    int i8 = (i6 * 32) + i7;
                    iArr[i8] = iArr[i8] + ((codewordArr[i5 + i].type32[i6] >> i7) & 1);
                }
            }
        }
    }

    private static int findPeaks(int[] iArr) {
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < 128; i4++) {
            int i5 = iArr[i4];
            int i6 = i5 > 0 ? -1 : 0;
            int i7 = (i6 & i5) | ((i6 ^ (-1)) & (-i5));
            i2 = i7 > i ? i5 : i2;
            i3 = i7 > i ? i4 : i3;
            i = i7 > i ? i7 : i;
        }
        return i3 | (128 * (i2 > 0 ? 1 : 0));
    }

    private static int Bit0Mask(int i) {
        return (-(i & 1)) & (-1);
    }

    public static void encode(long[] jArr, byte[] bArr, int i, int i2) {
        byte[] bArrClone = Arrays.clone(bArr);
        Codeword[] codewordArr = new Codeword[i * i2];
        for (int i3 = 0; i3 < codewordArr.length; i3++) {
            codewordArr[i3] = new Codeword();
        }
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i4 * i2;
            encodeSub(codewordArr[i5], bArrClone[i4]);
            for (int i6 = 1; i6 < i2; i6++) {
                codewordArr[i5 + i6] = codewordArr[i5];
            }
        }
        int[] iArr = new int[codewordArr.length * 4];
        int i7 = 0;
        for (int i8 = 0; i8 < codewordArr.length; i8++) {
            System.arraycopy(codewordArr[i8].type32, 0, iArr, i7, codewordArr[i8].type32.length);
            i7 += 4;
        }
        Utils.fromByte32ArrayToLongArray(jArr, iArr);
    }

    public static void decode(byte[] bArr, long[] jArr, int i, int i2) {
        byte[] bArrClone = Arrays.clone(bArr);
        Codeword[] codewordArr = new Codeword[jArr.length / 2];
        int[] iArr = new int[jArr.length * 2];
        Utils.fromLongArrayToByte32Array(iArr, jArr);
        for (int i3 = 0; i3 < codewordArr.length; i3++) {
            codewordArr[i3] = new Codeword();
            for (int i4 = 0; i4 < 4; i4++) {
                codewordArr[i3].type32[i4] = iArr[(i3 * 4) + i4];
            }
        }
        int[] iArr2 = new int[128];
        for (int i5 = 0; i5 < i; i5++) {
            expandThenSum(iArr2, codewordArr, i5 * i2, i2);
            int[] iArr3 = new int[128];
            hadamardTransform(iArr2, iArr3);
            iArr3[0] = iArr3[0] - (64 * i2);
            bArrClone[i5] = (byte) findPeaks(iArr3);
        }
        int[] iArr4 = new int[codewordArr.length * 4];
        int i6 = 0;
        for (int i7 = 0; i7 < codewordArr.length; i7++) {
            System.arraycopy(codewordArr[i7].type32, 0, iArr4, i6, codewordArr[i7].type32.length);
            i6 += 4;
        }
        Utils.fromByte32ArrayToLongArray(jArr, iArr4);
        System.arraycopy(bArrClone, 0, bArr, 0, bArr.length);
    }
}
