package org.bouncycastle.pqc.crypto.cmce;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/BENES.class */
abstract class BENES {
    private static final long[] TRANSPOSE_MASKS = {6148914691236517205L, 3689348814741910323L, 1085102592571150095L, 71777214294589695L, 281470681808895L, 4294967295L};
    protected final int SYS_N;
    protected final int SYS_T;
    protected final int GFBITS;

    public BENES(int i, int i2, int i3) {
        this.SYS_N = i;
        this.SYS_T = i2;
        this.GFBITS = i3;
    }

    static void transpose_64x64(long[] jArr, long[] jArr2) {
        transpose_64x64(jArr, jArr2, 0);
    }

    static void transpose_64x64(long[] jArr, long[] jArr2, int i) {
        System.arraycopy(jArr2, i, jArr, i, 64);
        int i2 = 5;
        do {
            long j = TRANSPOSE_MASKS[i2];
            int i3 = 1 << i2;
            int i4 = i;
            while (true) {
                int i5 = i4;
                if (i5 >= i + 64) {
                    break;
                }
                for (int i6 = i5; i6 < i5 + i3; i6 += 4) {
                    long j2 = jArr[i6 + 0];
                    long j3 = jArr[i6 + 1];
                    long j4 = jArr[i6 + 2];
                    long j5 = jArr[i6 + 3];
                    long j6 = jArr[i6 + i3 + 0];
                    long j7 = jArr[i6 + i3 + 1];
                    long j8 = jArr[i6 + i3 + 2];
                    long j9 = jArr[i6 + i3 + 3];
                    long j10 = ((j2 >>> i3) ^ j6) & j;
                    long j11 = ((j3 >>> i3) ^ j7) & j;
                    long j12 = ((j4 >>> i3) ^ j8) & j;
                    long j13 = ((j5 >>> i3) ^ j9) & j;
                    jArr[i6 + 0] = j2 ^ (j10 << i3);
                    jArr[i6 + 1] = j3 ^ (j11 << i3);
                    jArr[i6 + 2] = j4 ^ (j12 << i3);
                    jArr[i6 + 3] = j5 ^ (j13 << i3);
                    jArr[i6 + i3 + 0] = j6 ^ j10;
                    jArr[i6 + i3 + 1] = j7 ^ j11;
                    jArr[i6 + i3 + 2] = j8 ^ j12;
                    jArr[i6 + i3 + 3] = j9 ^ j13;
                }
                i4 = i5 + (i3 * 2);
            }
            i2--;
        } while (i2 >= 2);
        do {
            long j14 = TRANSPOSE_MASKS[i2];
            int i7 = 1 << i2;
            int i8 = i;
            while (true) {
                int i9 = i8;
                if (i9 >= i + 64) {
                    break;
                }
                for (int i10 = i9; i10 < i9 + i7; i10++) {
                    long j15 = jArr[i10 + 0];
                    long j16 = jArr[i10 + i7];
                    long j17 = ((j15 >>> i7) ^ j16) & j14;
                    jArr[i10 + 0] = j15 ^ (j17 << i7);
                    jArr[i10 + i7] = j16 ^ j17;
                }
                i8 = i9 + (i7 * 2);
            }
            i2--;
        } while (i2 >= 0);
    }

    protected abstract void support_gen(short[] sArr, byte[] bArr);
}
