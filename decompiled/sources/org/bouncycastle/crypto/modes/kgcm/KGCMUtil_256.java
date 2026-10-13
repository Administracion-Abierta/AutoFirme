package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/kgcm/KGCMUtil_256.class */
public class KGCMUtil_256 {
    public static final int SIZE = 4;

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
        jArr3[2] = jArr[2] ^ jArr2[2];
        jArr3[3] = jArr[3] ^ jArr2[3];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
        jArr2[2] = jArr[2];
        jArr2[3] = jArr[3];
    }

    public static boolean equal(long[] jArr, long[] jArr2) {
        return ((((0 | (jArr[0] ^ jArr2[0])) | (jArr[1] ^ jArr2[1])) | (jArr[2] ^ jArr2[2])) | (jArr[3] ^ jArr2[3])) == 0;
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr2[0];
        long j6 = jArr2[1];
        long j7 = jArr2[2];
        long j8 = jArr2[3];
        long j9 = 0;
        long j10 = 0;
        long j11 = 0;
        long j12 = 0;
        long j13 = 0;
        for (int i = 0; i < 64; i++) {
            long j14 = -(j & 1);
            j >>>= 1;
            j9 ^= j5 & j14;
            long j15 = j10 ^ (j6 & j14);
            long j16 = j11 ^ (j7 & j14);
            long j17 = j12 ^ (j8 & j14);
            long j18 = -(j2 & 1);
            j2 >>>= 1;
            j10 = j15 ^ (j5 & j18);
            j11 = j16 ^ (j6 & j18);
            j12 = j17 ^ (j7 & j18);
            j13 ^= j8 & j18;
            long j19 = j8 >> 63;
            j8 = (j8 << 1) | (j7 >>> 63);
            j7 = (j7 << 1) | (j6 >>> 63);
            j6 = (j6 << 1) | (j5 >>> 63);
            j5 = (j5 << 1) ^ (j19 & 1061);
        }
        long j20 = j8;
        long j21 = j7;
        long j22 = j6;
        long j23 = ((j5 ^ (j20 >>> 62)) ^ (j20 >>> 59)) ^ (j20 >>> 54);
        long j24 = ((j20 ^ (j20 << 2)) ^ (j20 << 5)) ^ (j20 << 10);
        for (int i2 = 0; i2 < 64; i2++) {
            long j25 = -(j3 & 1);
            j3 >>>= 1;
            j9 ^= j24 & j25;
            long j26 = j10 ^ (j23 & j25);
            long j27 = j11 ^ (j22 & j25);
            long j28 = j12 ^ (j21 & j25);
            long j29 = -(j4 & 1);
            j4 >>>= 1;
            j10 = j26 ^ (j24 & j29);
            j11 = j27 ^ (j23 & j29);
            j12 = j28 ^ (j22 & j29);
            j13 ^= j21 & j29;
            long j30 = j21 >> 63;
            j21 = (j21 << 1) | (j22 >>> 63);
            j22 = (j22 << 1) | (j23 >>> 63);
            j23 = (j23 << 1) | (j24 >>> 63);
            j24 = (j24 << 1) ^ (j30 & 1061);
        }
        jArr3[0] = j9 ^ (((j13 ^ (j13 << 2)) ^ (j13 << 5)) ^ (j13 << 10));
        jArr3[1] = j10 ^ (((j13 >>> 62) ^ (j13 >>> 59)) ^ (j13 >>> 54));
        jArr3[2] = j11;
        jArr3[3] = j12;
    }

    public static void multiplyX(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        jArr2[0] = (j << 1) ^ ((j4 >> 63) & 1061);
        jArr2[1] = (j2 << 1) | (j >>> 63);
        jArr2[2] = (j3 << 1) | (j2 >>> 63);
        jArr2[3] = (j4 << 1) | (j3 >>> 63);
    }

    public static void multiplyX8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = j4 >>> 56;
        jArr2[0] = ((((j << 8) ^ j5) ^ (j5 << 2)) ^ (j5 << 5)) ^ (j5 << 10);
        jArr2[1] = (j2 << 8) | (j >>> 56);
        jArr2[2] = (j3 << 8) | (j2 >>> 56);
        jArr2[3] = (j4 << 8) | (j3 >>> 56);
    }

    public static void one(long[] jArr) {
        jArr[0] = 1;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArr3 = new long[8];
        for (int i = 0; i < 4; i++) {
            Interleave.expand64To128(jArr[i], jArr3, i << 1);
        }
        int i2 = 8;
        while (true) {
            i2--;
            if (i2 < 4) {
                copy(jArr3, jArr2);
                return;
            }
            long j = jArr3[i2];
            int i3 = i2 - 4;
            jArr3[i3] = jArr3[i3] ^ (((j ^ (j << 2)) ^ (j << 5)) ^ (j << 10));
            int i4 = (i2 - 4) + 1;
            jArr3[i4] = jArr3[i4] ^ (((j >>> 62) ^ (j >>> 59)) ^ (j >>> 54));
        }
    }

    public static void x(long[] jArr) {
        jArr[0] = 2;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
    }

    public static void zero(long[] jArr) {
        jArr[0] = 0;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
    }
}
