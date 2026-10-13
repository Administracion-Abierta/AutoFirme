package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/GCMUtil.class */
public abstract class GCMUtil {
    public static final int SIZE_BYTES = 16;
    public static final int SIZE_INTS = 4;
    public static final int SIZE_LONGS = 2;
    private static final int E1 = -520093696;
    private static final long E1L = -2233785415175766016L;

    public static byte[] oneAsBytes() {
        byte[] bArr = new byte[16];
        bArr[0] = -128;
        return bArr;
    }

    public static int[] oneAsInts() {
        int[] iArr = new int[4];
        iArr[0] = Integer.MIN_VALUE;
        return iArr;
    }

    public static long[] oneAsLongs() {
        return new long[]{Long.MIN_VALUE, 0};
    }

    public static byte areEqual(byte[] bArr, byte[] bArr2) {
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            i |= bArr[i2] ^ bArr2[i2];
        }
        return (byte) ((((i >>> 1) | (i & 1)) - 1) >> 31);
    }

    public static int areEqual(int[] iArr, int[] iArr2) {
        int i = 0 | (iArr[0] ^ iArr2[0]) | (iArr[1] ^ iArr2[1]) | (iArr[2] ^ iArr2[2]) | (iArr[3] ^ iArr2[3]);
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static long areEqual(long[] jArr, long[] jArr2) {
        long j = 0 | (jArr[0] ^ jArr2[0]) | (jArr[1] ^ jArr2[1]);
        return (((j >>> 1) | (j & 1)) - 1) >> 63;
    }

    public static byte[] asBytes(int[] iArr) {
        byte[] bArr = new byte[16];
        Pack.intToBigEndian(iArr, 0, 4, bArr, 0);
        return bArr;
    }

    public static void asBytes(int[] iArr, byte[] bArr) {
        Pack.intToBigEndian(iArr, 0, 4, bArr, 0);
    }

    public static byte[] asBytes(long[] jArr) {
        byte[] bArr = new byte[16];
        Pack.longToBigEndian(jArr, 0, 2, bArr, 0);
        return bArr;
    }

    public static void asBytes(long[] jArr, byte[] bArr) {
        Pack.longToBigEndian(jArr, 0, 2, bArr, 0);
    }

    public static int[] asInts(byte[] bArr) {
        int[] iArr = new int[4];
        Pack.bigEndianToInt(bArr, 0, iArr, 0, 4);
        return iArr;
    }

    public static void asInts(byte[] bArr, int[] iArr) {
        Pack.bigEndianToInt(bArr, 0, iArr, 0, 4);
    }

    public static long[] asLongs(byte[] bArr) {
        long[] jArr = new long[2];
        Pack.bigEndianToLong(bArr, 0, jArr, 0, 2);
        return jArr;
    }

    public static void asLongs(byte[] bArr, long[] jArr) {
        Pack.bigEndianToLong(bArr, 0, jArr, 0, 2);
    }

    public static void copy(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < 16; i++) {
            bArr2[i] = bArr[i];
        }
    }

    public static void copy(int[] iArr, int[] iArr2) {
        iArr2[0] = iArr[0];
        iArr2[1] = iArr[1];
        iArr2[2] = iArr[2];
        iArr2[3] = iArr[3];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
    }

    public static void divideP(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j >> 63;
        jArr2[0] = ((j ^ (j3 & E1L)) << 1) | (j2 >>> 63);
        jArr2[1] = (j2 << 1) | (-j3);
    }

    public static void multiply(byte[] bArr, byte[] bArr2) {
        long[] jArrAsLongs = asLongs(bArr);
        multiply(jArrAsLongs, asLongs(bArr2));
        asBytes(jArrAsLongs, bArr);
    }

    static void multiply(byte[] bArr, long[] jArr) {
        long jBigEndianToLong = Pack.bigEndianToLong(bArr, 0);
        long jBigEndianToLong2 = Pack.bigEndianToLong(bArr, 8);
        long j = jArr[0];
        long j2 = jArr[1];
        long jReverse = Longs.reverse(jBigEndianToLong);
        long jReverse2 = Longs.reverse(jBigEndianToLong2);
        long jReverse3 = Longs.reverse(j);
        long jReverse4 = Longs.reverse(j2);
        long jReverse5 = Longs.reverse(implMul64(jReverse, jReverse3));
        long jImplMul64 = implMul64(jBigEndianToLong, j) << 1;
        long jReverse6 = Longs.reverse(implMul64(jReverse2, jReverse4));
        long jImplMul65 = implMul64(jBigEndianToLong2, j2) << 1;
        long jReverse7 = Longs.reverse(implMul64(jReverse ^ jReverse2, jReverse3 ^ jReverse4));
        long jImplMul66 = implMul64(jBigEndianToLong ^ jBigEndianToLong2, j ^ j2) << 1;
        long j3 = ((jImplMul64 ^ jReverse5) ^ jReverse6) ^ jReverse7;
        long j4 = ((jReverse6 ^ jImplMul64) ^ jImplMul65) ^ jImplMul66;
        long j5 = j3 ^ (((jImplMul65 ^ (jImplMul65 >>> 1)) ^ (jImplMul65 >>> 2)) ^ (jImplMul65 >>> 7));
        long j6 = j4 ^ ((jImplMul65 << 62) ^ (jImplMul65 << 57));
        Pack.longToBigEndian(jReverse5 ^ (((j6 ^ (j6 >>> 1)) ^ (j6 >>> 2)) ^ (j6 >>> 7)), bArr, 0);
        Pack.longToBigEndian(j5 ^ (((j6 << 63) ^ (j6 << 62)) ^ (j6 << 57)), bArr, 8);
    }

    public static void multiply(int[] iArr, int[] iArr2) {
        int i = iArr2[0];
        int i2 = iArr2[1];
        int i3 = iArr2[2];
        int i4 = iArr2[3];
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 0; i9 < 4; i9++) {
            int i10 = iArr[i9];
            for (int i11 = 0; i11 < 32; i11++) {
                int i12 = i10 >> 31;
                i10 <<= 1;
                i5 ^= i & i12;
                i6 ^= i2 & i12;
                i7 ^= i3 & i12;
                i8 ^= i4 & i12;
                int i13 = (i4 << 31) >> 8;
                i4 = (i4 >>> 1) | (i3 << 31);
                i3 = (i3 >>> 1) | (i2 << 31);
                i2 = (i2 >>> 1) | (i << 31);
                i = (i >>> 1) ^ (i13 & E1);
            }
        }
        iArr[0] = i5;
        iArr[1] = i6;
        iArr[2] = i7;
        iArr[3] = i8;
    }

    public static void multiply(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr2[0];
        long j4 = jArr2[1];
        long jReverse = Longs.reverse(j);
        long jReverse2 = Longs.reverse(j2);
        long jReverse3 = Longs.reverse(j3);
        long jReverse4 = Longs.reverse(j4);
        long jReverse5 = Longs.reverse(implMul64(jReverse, jReverse3));
        long jImplMul64 = implMul64(j, j3) << 1;
        long jReverse6 = Longs.reverse(implMul64(jReverse2, jReverse4));
        long jImplMul65 = implMul64(j2, j4) << 1;
        long jReverse7 = Longs.reverse(implMul64(jReverse ^ jReverse2, jReverse3 ^ jReverse4));
        long jImplMul66 = implMul64(j ^ j2, j3 ^ j4) << 1;
        long j5 = ((jImplMul64 ^ jReverse5) ^ jReverse6) ^ jReverse7;
        long j6 = ((jReverse6 ^ jImplMul64) ^ jImplMul65) ^ jImplMul66;
        long j7 = j5 ^ (((jImplMul65 ^ (jImplMul65 >>> 1)) ^ (jImplMul65 >>> 2)) ^ (jImplMul65 >>> 7));
        long j8 = j6 ^ ((jImplMul65 << 62) ^ (jImplMul65 << 57));
        jArr[0] = jReverse5 ^ (((j8 ^ (j8 >>> 1)) ^ (j8 >>> 2)) ^ (j8 >>> 7));
        jArr[1] = j7 ^ (((j8 << 63) ^ (j8 << 62)) ^ (j8 << 57));
    }

    public static void multiplyP(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        iArr[0] = (i >>> 1) ^ (((i4 << 31) >> 31) & E1);
        iArr[1] = (i2 >>> 1) | (i << 31);
        iArr[2] = (i3 >>> 1) | (i2 << 31);
        iArr[3] = (i4 >>> 1) | (i3 << 31);
    }

    public static void multiplyP(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        iArr2[0] = (i >>> 1) ^ (((i4 << 31) >> 31) & E1);
        iArr2[1] = (i2 >>> 1) | (i << 31);
        iArr2[2] = (i3 >>> 1) | (i2 << 31);
        iArr2[3] = (i4 >>> 1) | (i3 << 31);
    }

    public static void multiplyP(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        jArr[0] = (j >>> 1) ^ (((j2 << 63) >> 63) & E1L);
        jArr[1] = (j2 >>> 1) | (j << 63);
    }

    public static void multiplyP(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        jArr2[0] = (j >>> 1) ^ (((j2 << 63) >> 63) & E1L);
        jArr2[1] = (j2 >>> 1) | (j << 63);
    }

    public static void multiplyP3(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 61;
        jArr2[0] = ((((j >>> 3) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr2[1] = (j2 >>> 3) | (j << 61);
    }

    public static void multiplyP4(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 60;
        jArr2[0] = ((((j >>> 4) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr2[1] = (j2 >>> 4) | (j << 60);
    }

    public static void multiplyP7(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 57;
        jArr2[0] = ((((j >>> 7) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr2[1] = (j2 >>> 7) | (j << 57);
    }

    public static void multiplyP8(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = i4 << 24;
        iArr[0] = ((((i >>> 8) ^ i5) ^ (i5 >>> 1)) ^ (i5 >>> 2)) ^ (i5 >>> 7);
        iArr[1] = (i2 >>> 8) | (i << 24);
        iArr[2] = (i3 >>> 8) | (i2 << 24);
        iArr[3] = (i4 >>> 8) | (i3 << 24);
    }

    public static void multiplyP8(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = i4 << 24;
        iArr2[0] = ((((i >>> 8) ^ i5) ^ (i5 >>> 1)) ^ (i5 >>> 2)) ^ (i5 >>> 7);
        iArr2[1] = (i2 >>> 8) | (i << 24);
        iArr2[2] = (i3 >>> 8) | (i2 << 24);
        iArr2[3] = (i4 >>> 8) | (i3 << 24);
    }

    public static void multiplyP8(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 56;
        jArr[0] = ((((j >>> 8) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr[1] = (j2 >>> 8) | (j << 56);
    }

    public static void multiplyP8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 56;
        jArr2[0] = ((((j >>> 8) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr2[1] = (j2 >>> 8) | (j << 56);
    }

    public static void multiplyP16(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 48;
        jArr[0] = ((((j >>> 16) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        jArr[1] = (j2 >>> 16) | (j << 48);
    }

    public static long[] pAsLongs() {
        return new long[]{4611686018427387904L, 0};
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArr3 = new long[4];
        Interleave.expand64To128Rev(jArr[0], jArr3, 0);
        Interleave.expand64To128Rev(jArr[1], jArr3, 2);
        long j = jArr3[0];
        long j2 = jArr3[1];
        long j3 = jArr3[2];
        long j4 = jArr3[3];
        long j5 = j2 ^ (((j4 ^ (j4 >>> 1)) ^ (j4 >>> 2)) ^ (j4 >>> 7));
        long j6 = j3 ^ (((j4 << 63) ^ (j4 << 62)) ^ (j4 << 57));
        jArr2[0] = j ^ (((j6 ^ (j6 >>> 1)) ^ (j6 >>> 2)) ^ (j6 >>> 7));
        jArr2[1] = j5 ^ (((j6 << 63) ^ (j6 << 62)) ^ (j6 << 57));
    }

    public static void xor(byte[] bArr, byte[] bArr2) {
        int i = 0;
        do {
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ bArr2[i]);
            int i3 = i + 1;
            bArr[i3] = (byte) (bArr[i3] ^ bArr2[i3]);
            int i4 = i3 + 1;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i4]);
            int i5 = i4 + 1;
            bArr[i5] = (byte) (bArr[i5] ^ bArr2[i5]);
            i = i5 + 1;
        } while (i < 16);
    }

    public static void xor(byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        do {
            int i3 = i2;
            bArr[i3] = (byte) (bArr[i3] ^ bArr2[i + i2]);
            int i4 = i2 + 1;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i + i4]);
            int i5 = i4 + 1;
            bArr[i5] = (byte) (bArr[i5] ^ bArr2[i + i5]);
            int i6 = i5 + 1;
            bArr[i6] = (byte) (bArr[i6] ^ bArr2[i + i6]);
            i2 = i6 + 1;
        } while (i2 < 16);
    }

    public static void xor(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        int i4 = 0;
        do {
            bArr3[i3 + i4] = (byte) (bArr[i + i4] ^ bArr2[i2 + i4]);
            int i5 = i4 + 1;
            bArr3[i3 + i5] = (byte) (bArr[i + i5] ^ bArr2[i2 + i5]);
            int i6 = i5 + 1;
            bArr3[i3 + i6] = (byte) (bArr[i + i6] ^ bArr2[i2 + i6]);
            int i7 = i6 + 1;
            bArr3[i3 + i7] = (byte) (bArr[i + i7] ^ bArr2[i2 + i7]);
            i4 = i7 + 1;
        } while (i4 < 16);
    }

    public static void xor(byte[] bArr, byte[] bArr2, int i, int i2) {
        while (true) {
            i2--;
            if (i2 < 0) {
                return;
            } else {
                bArr[i2] = (byte) (bArr[i2] ^ bArr2[i + i2]);
            }
        }
    }

    public static void xor(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        while (true) {
            i3--;
            if (i3 < 0) {
                return;
            }
            int i4 = i + i3;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i2 + i3]);
        }
    }

    public static void xor(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i = 0;
        do {
            bArr3[i] = (byte) (bArr[i] ^ bArr2[i]);
            int i2 = i + 1;
            bArr3[i2] = (byte) (bArr[i2] ^ bArr2[i2]);
            int i3 = i2 + 1;
            bArr3[i3] = (byte) (bArr[i3] ^ bArr2[i3]);
            int i4 = i3 + 1;
            bArr3[i4] = (byte) (bArr[i4] ^ bArr2[i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(int[] iArr, int[] iArr2) {
        iArr[0] = iArr[0] ^ iArr2[0];
        iArr[1] = iArr[1] ^ iArr2[1];
        iArr[2] = iArr[2] ^ iArr2[2];
        iArr[3] = iArr[3] ^ iArr2[3];
    }

    public static void xor(int[] iArr, int[] iArr2, int[] iArr3) {
        iArr3[0] = iArr[0] ^ iArr2[0];
        iArr3[1] = iArr[1] ^ iArr2[1];
        iArr3[2] = iArr[2] ^ iArr2[2];
        iArr3[3] = iArr[3] ^ iArr2[3];
    }

    public static void xor(long[] jArr, long[] jArr2) {
        jArr[0] = jArr[0] ^ jArr2[0];
        jArr[1] = jArr[1] ^ jArr2[1];
    }

    public static void xor(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
    }

    private static long implMul64(long j, long j2) {
        long j3 = j & 1229782938247303441L;
        long j4 = j & 2459565876494606882L;
        long j5 = j & 4919131752989213764L;
        long j6 = j & (-8608480567731124088L);
        long j7 = j2 & 1229782938247303441L;
        long j8 = j2 & 2459565876494606882L;
        long j9 = j2 & 4919131752989213764L;
        long j10 = j2 & (-8608480567731124088L);
        long j11 = (((j3 * j7) ^ (j4 * j10)) ^ (j5 * j9)) ^ (j6 * j8);
        long j12 = (((j3 * j8) ^ (j4 * j7)) ^ (j5 * j10)) ^ (j6 * j9);
        long j13 = (((j3 * j9) ^ (j4 * j8)) ^ (j5 * j7)) ^ (j6 * j10);
        long j14 = (((j3 * j10) ^ (j4 * j9)) ^ (j5 * j8)) ^ (j6 * j7);
        long j15 = j11 & 1229782938247303441L;
        long j16 = j12 & 2459565876494606882L;
        long j17 = j13 & 4919131752989213764L;
        return j15 | j16 | j17 | (j14 & (-8608480567731124088L));
    }
}
