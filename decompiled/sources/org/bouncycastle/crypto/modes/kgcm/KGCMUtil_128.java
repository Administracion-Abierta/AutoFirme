package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/kgcm/KGCMUtil_128.class */
public class KGCMUtil_128 {
    public static final int SIZE = 2;

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
    }

    public static boolean equal(long[] jArr, long[] jArr2) {
        return ((0 | (jArr[0] ^ jArr2[0])) | (jArr[1] ^ jArr2[1])) == 0;
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr2[0];
        long j4 = jArr2[1];
        long j5 = 0;
        long j6 = 0;
        long j7 = 0;
        for (int i = 0; i < 64; i++) {
            long j8 = -(j & 1);
            j >>>= 1;
            j5 ^= j3 & j8;
            long j9 = j6 ^ (j4 & j8);
            long j10 = -(j2 & 1);
            j2 >>>= 1;
            j6 = j9 ^ (j3 & j10);
            j7 ^= j4 & j10;
            long j11 = j4 >> 63;
            j4 = (j4 << 1) | (j3 >>> 63);
            j3 = (j3 << 1) ^ (j11 & 135);
        }
        jArr3[0] = j5 ^ (((j7 ^ (j7 << 1)) ^ (j7 << 2)) ^ (j7 << 7));
        jArr3[1] = j6 ^ (((j7 >>> 63) ^ (j7 >>> 62)) ^ (j7 >>> 57));
    }

    public static void multiplyX(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        jArr2[0] = (j << 1) ^ ((j2 >> 63) & 135);
        jArr2[1] = (j2 << 1) | (j >>> 63);
    }

    public static void multiplyX8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 >>> 56;
        jArr2[0] = ((((j << 8) ^ j3) ^ (j3 << 1)) ^ (j3 << 2)) ^ (j3 << 7);
        jArr2[1] = (j2 << 8) | (j >>> 56);
    }

    public static void one(long[] jArr) {
        jArr[0] = 1;
        jArr[1] = 0;
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArr3 = new long[4];
        Interleave.expand64To128(jArr[0], jArr3, 0);
        Interleave.expand64To128(jArr[1], jArr3, 2);
        long j = jArr3[0];
        long j2 = jArr3[1];
        long j3 = jArr3[2];
        long j4 = jArr3[3];
        long j5 = j2 ^ (((j4 ^ (j4 << 1)) ^ (j4 << 2)) ^ (j4 << 7));
        long j6 = j3 ^ (((j4 >>> 63) ^ (j4 >>> 62)) ^ (j4 >>> 57));
        jArr2[0] = j ^ (((j6 ^ (j6 << 1)) ^ (j6 << 2)) ^ (j6 << 7));
        jArr2[1] = j5 ^ (((j6 >>> 63) ^ (j6 >>> 62)) ^ (j6 >>> 57));
    }

    public static void x(long[] jArr) {
        jArr[0] = 2;
        jArr[1] = 0;
    }

    public static void zero(long[] jArr) {
        jArr[0] = 0;
        jArr[1] = 0;
    }
}
