package org.bouncycastle.pqc.crypto.hqc;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/GF2PolynomialCalculator.class */
class GF2PolynomialCalculator {
    private final int VEC_N_SIZE_64;
    private final int PARAM_N;
    private final long RED_MASK;

    GF2PolynomialCalculator(int i, int i2, long j) {
        this.VEC_N_SIZE_64 = i;
        this.PARAM_N = i2;
        this.RED_MASK = j;
    }

    protected void multLongs(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArr4 = new long[this.VEC_N_SIZE_64 << 3];
        long[] jArr5 = new long[(this.VEC_N_SIZE_64 << 1) + 1];
        karatsuba(jArr5, 0, jArr2, 0, jArr3, 0, this.VEC_N_SIZE_64, jArr4, 0);
        reduce(jArr, jArr5);
    }

    private void base_mul(long[] jArr, int i, long j, long j2) {
        long[] jArr2 = new long[16];
        long[] jArr3 = new long[4];
        jArr2[0] = 0;
        jArr2[1] = j2 & 1152921504606846975L;
        jArr2[2] = jArr2[1] << 1;
        jArr2[3] = jArr2[2] ^ jArr2[1];
        jArr2[4] = jArr2[2] << 1;
        jArr2[5] = jArr2[4] ^ jArr2[1];
        jArr2[6] = jArr2[3] << 1;
        jArr2[7] = jArr2[6] ^ jArr2[1];
        jArr2[8] = jArr2[4] << 1;
        jArr2[9] = jArr2[8] ^ jArr2[1];
        jArr2[10] = jArr2[5] << 1;
        jArr2[11] = jArr2[10] ^ jArr2[1];
        jArr2[12] = jArr2[6] << 1;
        jArr2[13] = jArr2[12] ^ jArr2[1];
        jArr2[14] = jArr2[7] << 1;
        jArr2[15] = jArr2[14] ^ jArr2[1];
        long j3 = 0;
        long j4 = j & 15;
        for (int i2 = 0; i2 < 16; i2++) {
            long j5 = j4 - ((long) i2);
            j3 ^= jArr2[i2] & (-(1 - ((j5 | (-j5)) >>> 63)));
        }
        long j6 = j3;
        long j7 = 0;
        byte b = 4;
        while (true) {
            byte b2 = b;
            if (b2 >= 64) {
                jArr3[0] = -((j2 >> 60) & 1);
                jArr3[1] = -((j2 >> 61) & 1);
                jArr3[2] = -((j2 >> 62) & 1);
                jArr3[3] = -((j2 >> 63) & 1);
                long j8 = j6 ^ ((j << 60) & jArr3[0]);
                long j9 = j7 ^ ((j >>> 4) & jArr3[0]);
                long j10 = j8 ^ ((j << 61) & jArr3[1]);
                long j11 = j9 ^ ((j >>> 3) & jArr3[1]);
                long j12 = j10 ^ ((j << 62) & jArr3[2]);
                long j13 = j11 ^ ((j >>> 2) & jArr3[2]);
                long j14 = j12 ^ ((j << 63) & jArr3[3]);
                long j15 = j13 ^ ((j >>> 1) & jArr3[3]);
                jArr[0 + i] = j14;
                jArr[1 + i] = j15;
                return;
            }
            long j16 = 0;
            long j17 = (j >> b2) & 15;
            for (int i3 = 0; i3 < 16; i3++) {
                long j18 = j17 - ((long) i3);
                j16 ^= jArr2[i3] & (-(1 - ((j18 | (-j18)) >>> 63)));
            }
            j6 ^= j16 << b2;
            j7 ^= j16 >>> (64 - b2);
            b = (byte) (b2 + 4);
        }
    }

    private void karatsuba_add1(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, long[] jArr4, int i4, int i5, int i6) {
        for (int i7 = 0; i7 < i6; i7++) {
            jArr[i7 + i] = jArr3[i7 + i3] ^ jArr3[(i7 + i5) + i3];
            jArr2[i7 + i2] = jArr4[i7 + i4] ^ jArr4[(i7 + i5) + i4];
        }
        if (i6 < i5) {
            jArr[i6 + i] = jArr3[i6 + i3];
            jArr2[i6 + i2] = jArr4[i6 + i4];
        }
    }

    private void karatsuba_add2(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, int i4, int i5) {
        for (int i6 = 0; i6 < 2 * i4; i6++) {
            jArr2[i6 + i2] = jArr2[i6 + i2] ^ jArr[i6 + i];
        }
        for (int i7 = 0; i7 < 2 * i5; i7++) {
            jArr2[i7 + i2] = jArr2[i7 + i2] ^ jArr3[i7 + i3];
        }
        for (int i8 = 0; i8 < 2 * i4; i8++) {
            jArr[i8 + i4 + i] = jArr[(i8 + i4) + i] ^ jArr2[i8 + i2];
        }
    }

    private void karatsuba(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, int i4, long[] jArr4, int i5) {
        if (i4 == 1) {
            base_mul(jArr, i, jArr2[0 + i2], jArr3[0 + i3]);
            return;
        }
        int i6 = i4 / 2;
        int i7 = (i4 + 1) / 2;
        int i8 = i5 + i7;
        int i9 = i8 + i7;
        int i10 = i + (i7 * 2);
        int i11 = i5 + (4 * i7);
        karatsuba(jArr, i, jArr2, i2, jArr3, i3, i7, jArr4, i11);
        karatsuba(jArr, i10, jArr2, i2 + i7, jArr3, i3 + i7, i6, jArr4, i11);
        karatsuba_add1(jArr4, i5, jArr4, i8, jArr2, i2, jArr3, i3, i7, i6);
        karatsuba(jArr4, i9, jArr4, i5, jArr4, i8, i7, jArr4, i11);
        karatsuba_add2(jArr, i, jArr4, i9, jArr, i10, i7, i6);
    }

    private void reduce(long[] jArr, long[] jArr2) {
        for (int i = 0; i < this.VEC_N_SIZE_64; i++) {
            jArr[i] = (jArr2[i] ^ (jArr2[(i + this.VEC_N_SIZE_64) - 1] >>> (this.PARAM_N & 63))) ^ (jArr2[i + this.VEC_N_SIZE_64] << ((int) (64 - (((long) this.PARAM_N) & 63))));
        }
        int i2 = this.VEC_N_SIZE_64 - 1;
        jArr[i2] = jArr[i2] & this.RED_MASK;
    }

    static void addLongs(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 0; i < jArr2.length; i++) {
            jArr[i] = jArr2[i] ^ jArr3[i];
        }
    }
}
