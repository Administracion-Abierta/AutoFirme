package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/kgcm/KGCMUtil_512.class */
public class KGCMUtil_512 {
    public static final int SIZE = 8;

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
        jArr3[2] = jArr[2] ^ jArr2[2];
        jArr3[3] = jArr[3] ^ jArr2[3];
        jArr3[4] = jArr[4] ^ jArr2[4];
        jArr3[5] = jArr[5] ^ jArr2[5];
        jArr3[6] = jArr[6] ^ jArr2[6];
        jArr3[7] = jArr[7] ^ jArr2[7];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
        jArr2[2] = jArr[2];
        jArr2[3] = jArr[3];
        jArr2[4] = jArr[4];
        jArr2[5] = jArr[5];
        jArr2[6] = jArr[6];
        jArr2[7] = jArr[7];
    }

    public static boolean equal(long[] jArr, long[] jArr2) {
        return ((((((((0 | (jArr[0] ^ jArr2[0])) | (jArr[1] ^ jArr2[1])) | (jArr[2] ^ jArr2[2])) | (jArr[3] ^ jArr2[3])) | (jArr[4] ^ jArr2[4])) | (jArr[5] ^ jArr2[5])) | (jArr[6] ^ jArr2[6])) | (jArr[7] ^ jArr2[7])) == 0;
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long j = jArr2[0];
        long j2 = jArr2[1];
        long j3 = jArr2[2];
        long j4 = jArr2[3];
        long j5 = jArr2[4];
        long j6 = jArr2[5];
        long j7 = jArr2[6];
        long j8 = jArr2[7];
        long j9 = 0;
        long j10 = 0;
        long j11 = 0;
        long j12 = 0;
        long j13 = 0;
        long j14 = 0;
        long j15 = 0;
        long j16 = 0;
        long j17 = 0;
        for (int i = 0; i < 8; i += 2) {
            long j18 = jArr[i];
            long j19 = jArr[i + 1];
            for (int i2 = 0; i2 < 64; i2++) {
                long j20 = -(j18 & 1);
                j18 >>>= 1;
                j9 ^= j & j20;
                long j21 = j10 ^ (j2 & j20);
                long j22 = j11 ^ (j3 & j20);
                long j23 = j12 ^ (j4 & j20);
                long j24 = j13 ^ (j5 & j20);
                long j25 = j14 ^ (j6 & j20);
                long j26 = j15 ^ (j7 & j20);
                long j27 = j16 ^ (j8 & j20);
                long j28 = -(j19 & 1);
                j19 >>>= 1;
                j10 = j21 ^ (j & j28);
                j11 = j22 ^ (j2 & j28);
                j12 = j23 ^ (j3 & j28);
                j13 = j24 ^ (j4 & j28);
                j14 = j25 ^ (j5 & j28);
                j15 = j26 ^ (j6 & j28);
                j16 = j27 ^ (j7 & j28);
                j17 ^= j8 & j28;
                long j29 = j8 >> 63;
                j8 = (j8 << 1) | (j7 >>> 63);
                j7 = (j7 << 1) | (j6 >>> 63);
                j6 = (j6 << 1) | (j5 >>> 63);
                j5 = (j5 << 1) | (j4 >>> 63);
                j4 = (j4 << 1) | (j3 >>> 63);
                j3 = (j3 << 1) | (j2 >>> 63);
                j2 = (j2 << 1) | (j >>> 63);
                j = (j << 1) ^ (j29 & 293);
            }
            long j30 = j8;
            j8 = j7;
            j7 = j6;
            j6 = j5;
            j5 = j4;
            j4 = j3;
            j3 = j2;
            j2 = ((j ^ (j30 >>> 62)) ^ (j30 >>> 59)) ^ (j30 >>> 56);
            j = ((j30 ^ (j30 << 2)) ^ (j30 << 5)) ^ (j30 << 8);
        }
        jArr3[0] = j9 ^ (((j17 ^ (j17 << 2)) ^ (j17 << 5)) ^ (j17 << 8));
        jArr3[1] = j10 ^ (((j17 >>> 62) ^ (j17 >>> 59)) ^ (j17 >>> 56));
        jArr3[2] = j11;
        jArr3[3] = j12;
        jArr3[4] = j13;
        jArr3[5] = j14;
        jArr3[6] = j15;
        jArr3[7] = j16;
    }

    public static void multiplyX(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        jArr2[0] = (j << 1) ^ ((j8 >> 63) & 293);
        jArr2[1] = (j2 << 1) | (j >>> 63);
        jArr2[2] = (j3 << 1) | (j2 >>> 63);
        jArr2[3] = (j4 << 1) | (j3 >>> 63);
        jArr2[4] = (j5 << 1) | (j4 >>> 63);
        jArr2[5] = (j6 << 1) | (j5 >>> 63);
        jArr2[6] = (j7 << 1) | (j6 >>> 63);
        jArr2[7] = (j8 << 1) | (j7 >>> 63);
    }

    public static void multiplyX8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = j8 >>> 56;
        jArr2[0] = ((((j << 8) ^ j9) ^ (j9 << 2)) ^ (j9 << 5)) ^ (j9 << 8);
        jArr2[1] = (j2 << 8) | (j >>> 56);
        jArr2[2] = (j3 << 8) | (j2 >>> 56);
        jArr2[3] = (j4 << 8) | (j3 >>> 56);
        jArr2[4] = (j5 << 8) | (j4 >>> 56);
        jArr2[5] = (j6 << 8) | (j5 >>> 56);
        jArr2[6] = (j7 << 8) | (j6 >>> 56);
        jArr2[7] = (j8 << 8) | (j7 >>> 56);
    }

    public static void one(long[] jArr) {
        jArr[0] = 1;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArr3 = new long[16];
        for (int i = 0; i < 8; i++) {
            Interleave.expand64To128(jArr[i], jArr3, i << 1);
        }
        int i2 = 16;
        while (true) {
            i2--;
            if (i2 < 8) {
                copy(jArr3, jArr2);
                return;
            }
            long j = jArr3[i2];
            int i3 = i2 - 8;
            jArr3[i3] = jArr3[i3] ^ (((j ^ (j << 2)) ^ (j << 5)) ^ (j << 8));
            int i4 = (i2 - 8) + 1;
            jArr3[i4] = jArr3[i4] ^ (((j >>> 62) ^ (j >>> 59)) ^ (j >>> 56));
        }
    }

    public static void x(long[] jArr) {
        jArr[0] = 2;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }

    public static void zero(long[] jArr) {
        jArr[0] = 0;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }
}
