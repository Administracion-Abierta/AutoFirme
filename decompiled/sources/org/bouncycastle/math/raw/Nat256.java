package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/raw/Nat256.class */
public abstract class Nat256 {
    private static final long M = 4294967295L;

    public static int add(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = 0 + (((long) iArr[0]) & 4294967295L) + (((long) iArr2[0]) & 4294967295L);
        iArr3[0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[1]) & 4294967295L) + (((long) iArr2[1]) & 4294967295L);
        iArr3[1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[2]) & 4294967295L) + (((long) iArr2[2]) & 4294967295L);
        iArr3[2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[3]) & 4294967295L) + (((long) iArr2[3]) & 4294967295L);
        iArr3[3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[4]) & 4294967295L) + (((long) iArr2[4]) & 4294967295L);
        iArr3[4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[5]) & 4294967295L) + (((long) iArr2[5]) & 4294967295L);
        iArr3[5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[6]) & 4294967295L) + (((long) iArr2[6]) & 4294967295L);
        iArr3[6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[7]) & 4294967295L) + (((long) iArr2[7]) & 4294967295L);
        iArr3[7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int add(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        long j = 0 + (((long) iArr[i + 0]) & 4294967295L) + (((long) iArr2[i2 + 0]) & 4294967295L);
        iArr3[i3 + 0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[i + 1]) & 4294967295L) + (((long) iArr2[i2 + 1]) & 4294967295L);
        iArr3[i3 + 1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[i + 2]) & 4294967295L) + (((long) iArr2[i2 + 2]) & 4294967295L);
        iArr3[i3 + 2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[i + 3]) & 4294967295L) + (((long) iArr2[i2 + 3]) & 4294967295L);
        iArr3[i3 + 3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[i + 4]) & 4294967295L) + (((long) iArr2[i2 + 4]) & 4294967295L);
        iArr3[i3 + 4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[i + 5]) & 4294967295L) + (((long) iArr2[i2 + 5]) & 4294967295L);
        iArr3[i3 + 5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[i + 6]) & 4294967295L) + (((long) iArr2[i2 + 6]) & 4294967295L);
        iArr3[i3 + 6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[i + 7]) & 4294967295L) + (((long) iArr2[i2 + 7]) & 4294967295L);
        iArr3[i3 + 7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addBothTo(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = 0 + (((long) iArr[0]) & 4294967295L) + (((long) iArr2[0]) & 4294967295L) + (((long) iArr3[0]) & 4294967295L);
        iArr3[0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[1]) & 4294967295L) + (((long) iArr2[1]) & 4294967295L) + (((long) iArr3[1]) & 4294967295L);
        iArr3[1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[2]) & 4294967295L) + (((long) iArr2[2]) & 4294967295L) + (((long) iArr3[2]) & 4294967295L);
        iArr3[2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[3]) & 4294967295L) + (((long) iArr2[3]) & 4294967295L) + (((long) iArr3[3]) & 4294967295L);
        iArr3[3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[4]) & 4294967295L) + (((long) iArr2[4]) & 4294967295L) + (((long) iArr3[4]) & 4294967295L);
        iArr3[4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[5]) & 4294967295L) + (((long) iArr2[5]) & 4294967295L) + (((long) iArr3[5]) & 4294967295L);
        iArr3[5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[6]) & 4294967295L) + (((long) iArr2[6]) & 4294967295L) + (((long) iArr3[6]) & 4294967295L);
        iArr3[6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[7]) & 4294967295L) + (((long) iArr2[7]) & 4294967295L) + (((long) iArr3[7]) & 4294967295L);
        iArr3[7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addBothTo(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        long j = 0 + (((long) iArr[i + 0]) & 4294967295L) + (((long) iArr2[i2 + 0]) & 4294967295L) + (((long) iArr3[i3 + 0]) & 4294967295L);
        iArr3[i3 + 0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[i + 1]) & 4294967295L) + (((long) iArr2[i2 + 1]) & 4294967295L) + (((long) iArr3[i3 + 1]) & 4294967295L);
        iArr3[i3 + 1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[i + 2]) & 4294967295L) + (((long) iArr2[i2 + 2]) & 4294967295L) + (((long) iArr3[i3 + 2]) & 4294967295L);
        iArr3[i3 + 2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[i + 3]) & 4294967295L) + (((long) iArr2[i2 + 3]) & 4294967295L) + (((long) iArr3[i3 + 3]) & 4294967295L);
        iArr3[i3 + 3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[i + 4]) & 4294967295L) + (((long) iArr2[i2 + 4]) & 4294967295L) + (((long) iArr3[i3 + 4]) & 4294967295L);
        iArr3[i3 + 4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[i + 5]) & 4294967295L) + (((long) iArr2[i2 + 5]) & 4294967295L) + (((long) iArr3[i3 + 5]) & 4294967295L);
        iArr3[i3 + 5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[i + 6]) & 4294967295L) + (((long) iArr2[i2 + 6]) & 4294967295L) + (((long) iArr3[i3 + 6]) & 4294967295L);
        iArr3[i3 + 6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[i + 7]) & 4294967295L) + (((long) iArr2[i2 + 7]) & 4294967295L) + (((long) iArr3[i3 + 7]) & 4294967295L);
        iArr3[i3 + 7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addTo(int[] iArr, int[] iArr2) {
        long j = 0 + (((long) iArr[0]) & 4294967295L) + (((long) iArr2[0]) & 4294967295L);
        iArr2[0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[1]) & 4294967295L) + (((long) iArr2[1]) & 4294967295L);
        iArr2[1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[2]) & 4294967295L) + (((long) iArr2[2]) & 4294967295L);
        iArr2[2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[3]) & 4294967295L) + (((long) iArr2[3]) & 4294967295L);
        iArr2[3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[4]) & 4294967295L) + (((long) iArr2[4]) & 4294967295L);
        iArr2[4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[5]) & 4294967295L) + (((long) iArr2[5]) & 4294967295L);
        iArr2[5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[6]) & 4294967295L) + (((long) iArr2[6]) & 4294967295L);
        iArr2[6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[7]) & 4294967295L) + (((long) iArr2[7]) & 4294967295L);
        iArr2[7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addTo(int[] iArr, int[] iArr2, int i) {
        long j = (((long) i) & 4294967295L) + (((long) iArr[0]) & 4294967295L) + (((long) iArr2[0]) & 4294967295L);
        iArr2[0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[1]) & 4294967295L) + (((long) iArr2[1]) & 4294967295L);
        iArr2[1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[2]) & 4294967295L) + (((long) iArr2[2]) & 4294967295L);
        iArr2[2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[3]) & 4294967295L) + (((long) iArr2[3]) & 4294967295L);
        iArr2[3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[4]) & 4294967295L) + (((long) iArr2[4]) & 4294967295L);
        iArr2[4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[5]) & 4294967295L) + (((long) iArr2[5]) & 4294967295L);
        iArr2[5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[6]) & 4294967295L) + (((long) iArr2[6]) & 4294967295L);
        iArr2[6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[7]) & 4294967295L) + (((long) iArr2[7]) & 4294967295L);
        iArr2[7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addTo(int[] iArr, int i, int[] iArr2, int i2, int i3) {
        long j = (((long) i3) & 4294967295L) + (((long) iArr[i + 0]) & 4294967295L) + (((long) iArr2[i2 + 0]) & 4294967295L);
        iArr2[i2 + 0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[i + 1]) & 4294967295L) + (((long) iArr2[i2 + 1]) & 4294967295L);
        iArr2[i2 + 1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[i + 2]) & 4294967295L) + (((long) iArr2[i2 + 2]) & 4294967295L);
        iArr2[i2 + 2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[i + 3]) & 4294967295L) + (((long) iArr2[i2 + 3]) & 4294967295L);
        iArr2[i2 + 3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[i + 4]) & 4294967295L) + (((long) iArr2[i2 + 4]) & 4294967295L);
        iArr2[i2 + 4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[i + 5]) & 4294967295L) + (((long) iArr2[i2 + 5]) & 4294967295L);
        iArr2[i2 + 5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[i + 6]) & 4294967295L) + (((long) iArr2[i2 + 6]) & 4294967295L);
        iArr2[i2 + 6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[i + 7]) & 4294967295L) + (((long) iArr2[i2 + 7]) & 4294967295L);
        iArr2[i2 + 7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static int addToEachOther(int[] iArr, int i, int[] iArr2, int i2) {
        long j = 0 + (((long) iArr[i + 0]) & 4294967295L) + (((long) iArr2[i2 + 0]) & 4294967295L);
        iArr[i + 0] = (int) j;
        iArr2[i2 + 0] = (int) j;
        long j2 = (j >>> 32) + (((long) iArr[i + 1]) & 4294967295L) + (((long) iArr2[i2 + 1]) & 4294967295L);
        iArr[i + 1] = (int) j2;
        iArr2[i2 + 1] = (int) j2;
        long j3 = (j2 >>> 32) + (((long) iArr[i + 2]) & 4294967295L) + (((long) iArr2[i2 + 2]) & 4294967295L);
        iArr[i + 2] = (int) j3;
        iArr2[i2 + 2] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[i + 3]) & 4294967295L) + (((long) iArr2[i2 + 3]) & 4294967295L);
        iArr[i + 3] = (int) j4;
        iArr2[i2 + 3] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[i + 4]) & 4294967295L) + (((long) iArr2[i2 + 4]) & 4294967295L);
        iArr[i + 4] = (int) j5;
        iArr2[i2 + 4] = (int) j5;
        long j6 = (j5 >>> 32) + (((long) iArr[i + 5]) & 4294967295L) + (((long) iArr2[i2 + 5]) & 4294967295L);
        iArr[i + 5] = (int) j6;
        iArr2[i2 + 5] = (int) j6;
        long j7 = (j6 >>> 32) + (((long) iArr[i + 6]) & 4294967295L) + (((long) iArr2[i2 + 6]) & 4294967295L);
        iArr[i + 6] = (int) j7;
        iArr2[i2 + 6] = (int) j7;
        long j8 = (j7 >>> 32) + (((long) iArr[i + 7]) & 4294967295L) + (((long) iArr2[i2 + 7]) & 4294967295L);
        iArr[i + 7] = (int) j8;
        iArr2[i2 + 7] = (int) j8;
        return (int) (j8 >>> 32);
    }

    public static void copy(int[] iArr, int[] iArr2) {
        iArr2[0] = iArr[0];
        iArr2[1] = iArr[1];
        iArr2[2] = iArr[2];
        iArr2[3] = iArr[3];
        iArr2[4] = iArr[4];
        iArr2[5] = iArr[5];
        iArr2[6] = iArr[6];
        iArr2[7] = iArr[7];
    }

    public static void copy(int[] iArr, int i, int[] iArr2, int i2) {
        iArr2[i2 + 0] = iArr[i + 0];
        iArr2[i2 + 1] = iArr[i + 1];
        iArr2[i2 + 2] = iArr[i + 2];
        iArr2[i2 + 3] = iArr[i + 3];
        iArr2[i2 + 4] = iArr[i + 4];
        iArr2[i2 + 5] = iArr[i + 5];
        iArr2[i2 + 6] = iArr[i + 6];
        iArr2[i2 + 7] = iArr[i + 7];
    }

    public static void copy64(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
        jArr2[2] = jArr[2];
        jArr2[3] = jArr[3];
    }

    public static void copy64(long[] jArr, int i, long[] jArr2, int i2) {
        jArr2[i2 + 0] = jArr[i + 0];
        jArr2[i2 + 1] = jArr[i + 1];
        jArr2[i2 + 2] = jArr[i + 2];
        jArr2[i2 + 3] = jArr[i + 3];
    }

    public static int[] create() {
        return new int[8];
    }

    public static long[] create64() {
        return new long[4];
    }

    public static int[] createExt() {
        return new int[16];
    }

    public static long[] createExt64() {
        return new long[8];
    }

    public static boolean diff(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        boolean zGte = gte(iArr, i, iArr2, i2);
        if (zGte) {
            sub(iArr, i, iArr2, i2, iArr3, i3);
        } else {
            sub(iArr2, i2, iArr, i, iArr3, i3);
        }
        return zGte;
    }

    public static boolean eq(int[] iArr, int[] iArr2) {
        for (int i = 7; i >= 0; i--) {
            if (iArr[i] != iArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean eq64(long[] jArr, long[] jArr2) {
        for (int i = 3; i >= 0; i--) {
            if (jArr[i] != jArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] fromBigInteger(BigInteger bigInteger) {
        if (bigInteger.signum() < 0 || bigInteger.bitLength() > 256) {
            throw new IllegalArgumentException();
        }
        int[] iArrCreate = create();
        for (int i = 0; i < 8; i++) {
            iArrCreate[i] = bigInteger.intValue();
            bigInteger = bigInteger.shiftRight(32);
        }
        return iArrCreate;
    }

    public static long[] fromBigInteger64(BigInteger bigInteger) {
        if (bigInteger.signum() < 0 || bigInteger.bitLength() > 256) {
            throw new IllegalArgumentException();
        }
        long[] jArrCreate64 = create64();
        for (int i = 0; i < 4; i++) {
            jArrCreate64[i] = bigInteger.longValue();
            bigInteger = bigInteger.shiftRight(64);
        }
        return jArrCreate64;
    }

    public static int getBit(int[] iArr, int i) {
        if (i == 0) {
            return iArr[0] & 1;
        }
        if ((i & 255) != i) {
            return 0;
        }
        return (iArr[i >>> 5] >>> (i & 31)) & 1;
    }

    public static boolean gte(int[] iArr, int[] iArr2) {
        for (int i = 7; i >= 0; i--) {
            int i2 = iArr[i] ^ Integer.MIN_VALUE;
            int i3 = iArr2[i] ^ Integer.MIN_VALUE;
            if (i2 < i3) {
                return false;
            }
            if (i2 > i3) {
                return true;
            }
        }
        return true;
    }

    public static boolean gte(int[] iArr, int i, int[] iArr2, int i2) {
        for (int i3 = 7; i3 >= 0; i3--) {
            int i4 = iArr[i + i3] ^ Integer.MIN_VALUE;
            int i5 = iArr2[i2 + i3] ^ Integer.MIN_VALUE;
            if (i4 < i5) {
                return false;
            }
            if (i4 > i5) {
                return true;
            }
        }
        return true;
    }

    public static boolean isOne(int[] iArr) {
        if (iArr[0] != 1) {
            return false;
        }
        for (int i = 1; i < 8; i++) {
            if (iArr[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOne64(long[] jArr) {
        if (jArr[0] != 1) {
            return false;
        }
        for (int i = 1; i < 4; i++) {
            if (jArr[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero(int[] iArr) {
        for (int i = 0; i < 8; i++) {
            if (iArr[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero64(long[] jArr) {
        for (int i = 0; i < 4; i++) {
            if (jArr[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static void mul(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = ((long) iArr2[0]) & 4294967295L;
        long j2 = ((long) iArr2[1]) & 4294967295L;
        long j3 = ((long) iArr2[2]) & 4294967295L;
        long j4 = ((long) iArr2[3]) & 4294967295L;
        long j5 = ((long) iArr2[4]) & 4294967295L;
        long j6 = ((long) iArr2[5]) & 4294967295L;
        long j7 = ((long) iArr2[6]) & 4294967295L;
        long j8 = ((long) iArr2[7]) & 4294967295L;
        long j9 = ((long) iArr[0]) & 4294967295L;
        long j10 = 0 + (j9 * j);
        iArr3[0] = (int) j10;
        long j11 = (j10 >>> 32) + (j9 * j2);
        iArr3[1] = (int) j11;
        long j12 = (j11 >>> 32) + (j9 * j3);
        iArr3[2] = (int) j12;
        long j13 = (j12 >>> 32) + (j9 * j4);
        iArr3[3] = (int) j13;
        long j14 = (j13 >>> 32) + (j9 * j5);
        iArr3[4] = (int) j14;
        long j15 = (j14 >>> 32) + (j9 * j6);
        iArr3[5] = (int) j15;
        long j16 = (j15 >>> 32) + (j9 * j7);
        iArr3[6] = (int) j16;
        long j17 = (j16 >>> 32) + (j9 * j8);
        iArr3[7] = (int) j17;
        iArr3[8] = (int) (j17 >>> 32);
        for (int i = 1; i < 8; i++) {
            long j18 = ((long) iArr[i]) & 4294967295L;
            long j19 = 0 + (j18 * j) + (((long) iArr3[i + 0]) & 4294967295L);
            iArr3[i + 0] = (int) j19;
            long j20 = (j19 >>> 32) + (j18 * j2) + (((long) iArr3[i + 1]) & 4294967295L);
            iArr3[i + 1] = (int) j20;
            long j21 = (j20 >>> 32) + (j18 * j3) + (((long) iArr3[i + 2]) & 4294967295L);
            iArr3[i + 2] = (int) j21;
            long j22 = (j21 >>> 32) + (j18 * j4) + (((long) iArr3[i + 3]) & 4294967295L);
            iArr3[i + 3] = (int) j22;
            long j23 = (j22 >>> 32) + (j18 * j5) + (((long) iArr3[i + 4]) & 4294967295L);
            iArr3[i + 4] = (int) j23;
            long j24 = (j23 >>> 32) + (j18 * j6) + (((long) iArr3[i + 5]) & 4294967295L);
            iArr3[i + 5] = (int) j24;
            long j25 = (j24 >>> 32) + (j18 * j7) + (((long) iArr3[i + 6]) & 4294967295L);
            iArr3[i + 6] = (int) j25;
            long j26 = (j25 >>> 32) + (j18 * j8) + (((long) iArr3[i + 7]) & 4294967295L);
            iArr3[i + 7] = (int) j26;
            iArr3[i + 8] = (int) (j26 >>> 32);
        }
    }

    public static void mul(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        long j = ((long) iArr2[i2 + 0]) & 4294967295L;
        long j2 = ((long) iArr2[i2 + 1]) & 4294967295L;
        long j3 = ((long) iArr2[i2 + 2]) & 4294967295L;
        long j4 = ((long) iArr2[i2 + 3]) & 4294967295L;
        long j5 = ((long) iArr2[i2 + 4]) & 4294967295L;
        long j6 = ((long) iArr2[i2 + 5]) & 4294967295L;
        long j7 = ((long) iArr2[i2 + 6]) & 4294967295L;
        long j8 = ((long) iArr2[i2 + 7]) & 4294967295L;
        long j9 = ((long) iArr[i + 0]) & 4294967295L;
        long j10 = 0 + (j9 * j);
        iArr3[i3 + 0] = (int) j10;
        long j11 = (j10 >>> 32) + (j9 * j2);
        iArr3[i3 + 1] = (int) j11;
        long j12 = (j11 >>> 32) + (j9 * j3);
        iArr3[i3 + 2] = (int) j12;
        long j13 = (j12 >>> 32) + (j9 * j4);
        iArr3[i3 + 3] = (int) j13;
        long j14 = (j13 >>> 32) + (j9 * j5);
        iArr3[i3 + 4] = (int) j14;
        long j15 = (j14 >>> 32) + (j9 * j6);
        iArr3[i3 + 5] = (int) j15;
        long j16 = (j15 >>> 32) + (j9 * j7);
        iArr3[i3 + 6] = (int) j16;
        long j17 = (j16 >>> 32) + (j9 * j8);
        iArr3[i3 + 7] = (int) j17;
        iArr3[i3 + 8] = (int) (j17 >>> 32);
        for (int i4 = 1; i4 < 8; i4++) {
            i3++;
            long j18 = ((long) iArr[i + i4]) & 4294967295L;
            long j19 = 0 + (j18 * j) + (((long) iArr3[i3 + 0]) & 4294967295L);
            iArr3[i3 + 0] = (int) j19;
            long j20 = (j19 >>> 32) + (j18 * j2) + (((long) iArr3[i3 + 1]) & 4294967295L);
            iArr3[i3 + 1] = (int) j20;
            long j21 = (j20 >>> 32) + (j18 * j3) + (((long) iArr3[i3 + 2]) & 4294967295L);
            iArr3[i3 + 2] = (int) j21;
            long j22 = (j21 >>> 32) + (j18 * j4) + (((long) iArr3[i3 + 3]) & 4294967295L);
            iArr3[i3 + 3] = (int) j22;
            long j23 = (j22 >>> 32) + (j18 * j5) + (((long) iArr3[i3 + 4]) & 4294967295L);
            iArr3[i3 + 4] = (int) j23;
            long j24 = (j23 >>> 32) + (j18 * j6) + (((long) iArr3[i3 + 5]) & 4294967295L);
            iArr3[i3 + 5] = (int) j24;
            long j25 = (j24 >>> 32) + (j18 * j7) + (((long) iArr3[i3 + 6]) & 4294967295L);
            iArr3[i3 + 6] = (int) j25;
            long j26 = (j25 >>> 32) + (j18 * j8) + (((long) iArr3[i3 + 7]) & 4294967295L);
            iArr3[i3 + 7] = (int) j26;
            iArr3[i3 + 8] = (int) (j26 >>> 32);
        }
    }

    public static void mul128(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = ((long) iArr[0]) & 4294967295L;
        long j2 = ((long) iArr[1]) & 4294967295L;
        long j3 = ((long) iArr[2]) & 4294967295L;
        long j4 = ((long) iArr[3]) & 4294967295L;
        long j5 = ((long) iArr[4]) & 4294967295L;
        long j6 = ((long) iArr[5]) & 4294967295L;
        long j7 = ((long) iArr[6]) & 4294967295L;
        long j8 = ((long) iArr[7]) & 4294967295L;
        long j9 = ((long) iArr2[0]) & 4294967295L;
        long j10 = 0 + (j9 * j);
        iArr3[0] = (int) j10;
        long j11 = (j10 >>> 32) + (j9 * j2);
        iArr3[1] = (int) j11;
        long j12 = (j11 >>> 32) + (j9 * j3);
        iArr3[2] = (int) j12;
        long j13 = (j12 >>> 32) + (j9 * j4);
        iArr3[3] = (int) j13;
        long j14 = (j13 >>> 32) + (j9 * j5);
        iArr3[4] = (int) j14;
        long j15 = (j14 >>> 32) + (j9 * j6);
        iArr3[5] = (int) j15;
        long j16 = (j15 >>> 32) + (j9 * j7);
        iArr3[6] = (int) j16;
        long j17 = (j16 >>> 32) + (j9 * j8);
        iArr3[7] = (int) j17;
        iArr3[8] = (int) (j17 >>> 32);
        for (int i = 1; i < 4; i++) {
            long j18 = ((long) iArr2[i]) & 4294967295L;
            long j19 = 0 + (j18 * j) + (((long) iArr3[i + 0]) & 4294967295L);
            iArr3[i + 0] = (int) j19;
            long j20 = (j19 >>> 32) + (j18 * j2) + (((long) iArr3[i + 1]) & 4294967295L);
            iArr3[i + 1] = (int) j20;
            long j21 = (j20 >>> 32) + (j18 * j3) + (((long) iArr3[i + 2]) & 4294967295L);
            iArr3[i + 2] = (int) j21;
            long j22 = (j21 >>> 32) + (j18 * j4) + (((long) iArr3[i + 3]) & 4294967295L);
            iArr3[i + 3] = (int) j22;
            long j23 = (j22 >>> 32) + (j18 * j5) + (((long) iArr3[i + 4]) & 4294967295L);
            iArr3[i + 4] = (int) j23;
            long j24 = (j23 >>> 32) + (j18 * j6) + (((long) iArr3[i + 5]) & 4294967295L);
            iArr3[i + 5] = (int) j24;
            long j25 = (j24 >>> 32) + (j18 * j7) + (((long) iArr3[i + 6]) & 4294967295L);
            iArr3[i + 6] = (int) j25;
            long j26 = (j25 >>> 32) + (j18 * j8) + (((long) iArr3[i + 7]) & 4294967295L);
            iArr3[i + 7] = (int) j26;
            iArr3[i + 8] = (int) (j26 >>> 32);
        }
    }

    public static int mulAddTo(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = ((long) iArr2[0]) & 4294967295L;
        long j2 = ((long) iArr2[1]) & 4294967295L;
        long j3 = ((long) iArr2[2]) & 4294967295L;
        long j4 = ((long) iArr2[3]) & 4294967295L;
        long j5 = ((long) iArr2[4]) & 4294967295L;
        long j6 = ((long) iArr2[5]) & 4294967295L;
        long j7 = ((long) iArr2[6]) & 4294967295L;
        long j8 = ((long) iArr2[7]) & 4294967295L;
        long j9 = 0;
        for (int i = 0; i < 8; i++) {
            long j10 = ((long) iArr[i]) & 4294967295L;
            long j11 = 0 + (j10 * j) + (((long) iArr3[i + 0]) & 4294967295L);
            iArr3[i + 0] = (int) j11;
            long j12 = (j11 >>> 32) + (j10 * j2) + (((long) iArr3[i + 1]) & 4294967295L);
            iArr3[i + 1] = (int) j12;
            long j13 = (j12 >>> 32) + (j10 * j3) + (((long) iArr3[i + 2]) & 4294967295L);
            iArr3[i + 2] = (int) j13;
            long j14 = (j13 >>> 32) + (j10 * j4) + (((long) iArr3[i + 3]) & 4294967295L);
            iArr3[i + 3] = (int) j14;
            long j15 = (j14 >>> 32) + (j10 * j5) + (((long) iArr3[i + 4]) & 4294967295L);
            iArr3[i + 4] = (int) j15;
            long j16 = (j15 >>> 32) + (j10 * j6) + (((long) iArr3[i + 5]) & 4294967295L);
            iArr3[i + 5] = (int) j16;
            long j17 = (j16 >>> 32) + (j10 * j7) + (((long) iArr3[i + 6]) & 4294967295L);
            iArr3[i + 6] = (int) j17;
            long j18 = (j17 >>> 32) + (j10 * j8) + (((long) iArr3[i + 7]) & 4294967295L);
            iArr3[i + 7] = (int) j18;
            long j19 = j9 + (j18 >>> 32) + (((long) iArr3[i + 8]) & 4294967295L);
            iArr3[i + 8] = (int) j19;
            j9 = j19 >>> 32;
        }
        return (int) j9;
    }

    public static int mulAddTo(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        long j = ((long) iArr2[i2 + 0]) & 4294967295L;
        long j2 = ((long) iArr2[i2 + 1]) & 4294967295L;
        long j3 = ((long) iArr2[i2 + 2]) & 4294967295L;
        long j4 = ((long) iArr2[i2 + 3]) & 4294967295L;
        long j5 = ((long) iArr2[i2 + 4]) & 4294967295L;
        long j6 = ((long) iArr2[i2 + 5]) & 4294967295L;
        long j7 = ((long) iArr2[i2 + 6]) & 4294967295L;
        long j8 = ((long) iArr2[i2 + 7]) & 4294967295L;
        long j9 = 0;
        for (int i4 = 0; i4 < 8; i4++) {
            long j10 = ((long) iArr[i + i4]) & 4294967295L;
            long j11 = 0 + (j10 * j) + (((long) iArr3[i3 + 0]) & 4294967295L);
            iArr3[i3 + 0] = (int) j11;
            long j12 = (j11 >>> 32) + (j10 * j2) + (((long) iArr3[i3 + 1]) & 4294967295L);
            iArr3[i3 + 1] = (int) j12;
            long j13 = (j12 >>> 32) + (j10 * j3) + (((long) iArr3[i3 + 2]) & 4294967295L);
            iArr3[i3 + 2] = (int) j13;
            long j14 = (j13 >>> 32) + (j10 * j4) + (((long) iArr3[i3 + 3]) & 4294967295L);
            iArr3[i3 + 3] = (int) j14;
            long j15 = (j14 >>> 32) + (j10 * j5) + (((long) iArr3[i3 + 4]) & 4294967295L);
            iArr3[i3 + 4] = (int) j15;
            long j16 = (j15 >>> 32) + (j10 * j6) + (((long) iArr3[i3 + 5]) & 4294967295L);
            iArr3[i3 + 5] = (int) j16;
            long j17 = (j16 >>> 32) + (j10 * j7) + (((long) iArr3[i3 + 6]) & 4294967295L);
            iArr3[i3 + 6] = (int) j17;
            long j18 = (j17 >>> 32) + (j10 * j8) + (((long) iArr3[i3 + 7]) & 4294967295L);
            iArr3[i3 + 7] = (int) j18;
            long j19 = j9 + (j18 >>> 32) + (((long) iArr3[i3 + 8]) & 4294967295L);
            iArr3[i3 + 8] = (int) j19;
            j9 = j19 >>> 32;
            i3++;
        }
        return (int) j9;
    }

    public static long mul33Add(int i, int[] iArr, int i2, int[] iArr2, int i3, int[] iArr3, int i4) {
        long j = ((long) i) & 4294967295L;
        long j2 = ((long) iArr[i2 + 0]) & 4294967295L;
        long j3 = 0 + (j * j2) + (((long) iArr2[i3 + 0]) & 4294967295L);
        iArr3[i4 + 0] = (int) j3;
        long j4 = j3 >>> 32;
        long j5 = ((long) iArr[i2 + 1]) & 4294967295L;
        long j6 = j4 + (j * j5) + j2 + (((long) iArr2[i3 + 1]) & 4294967295L);
        iArr3[i4 + 1] = (int) j6;
        long j7 = j6 >>> 32;
        long j8 = ((long) iArr[i2 + 2]) & 4294967295L;
        long j9 = j7 + (j * j8) + j5 + (((long) iArr2[i3 + 2]) & 4294967295L);
        iArr3[i4 + 2] = (int) j9;
        long j10 = j9 >>> 32;
        long j11 = ((long) iArr[i2 + 3]) & 4294967295L;
        long j12 = j10 + (j * j11) + j8 + (((long) iArr2[i3 + 3]) & 4294967295L);
        iArr3[i4 + 3] = (int) j12;
        long j13 = j12 >>> 32;
        long j14 = ((long) iArr[i2 + 4]) & 4294967295L;
        long j15 = j13 + (j * j14) + j11 + (((long) iArr2[i3 + 4]) & 4294967295L);
        iArr3[i4 + 4] = (int) j15;
        long j16 = j15 >>> 32;
        long j17 = ((long) iArr[i2 + 5]) & 4294967295L;
        long j18 = j16 + (j * j17) + j14 + (((long) iArr2[i3 + 5]) & 4294967295L);
        iArr3[i4 + 5] = (int) j18;
        long j19 = j18 >>> 32;
        long j20 = ((long) iArr[i2 + 6]) & 4294967295L;
        long j21 = j19 + (j * j20) + j17 + (((long) iArr2[i3 + 6]) & 4294967295L);
        iArr3[i4 + 6] = (int) j21;
        long j22 = j21 >>> 32;
        long j23 = ((long) iArr[i2 + 7]) & 4294967295L;
        long j24 = j22 + (j * j23) + j20 + (((long) iArr2[i3 + 7]) & 4294967295L);
        iArr3[i4 + 7] = (int) j24;
        return (j24 >>> 32) + j23;
    }

    public static int mulByWord(int i, int[] iArr) {
        long j = ((long) i) & 4294967295L;
        long j2 = 0 + (j * (((long) iArr[0]) & 4294967295L));
        iArr[0] = (int) j2;
        long j3 = (j2 >>> 32) + (j * (((long) iArr[1]) & 4294967295L));
        iArr[1] = (int) j3;
        long j4 = (j3 >>> 32) + (j * (((long) iArr[2]) & 4294967295L));
        iArr[2] = (int) j4;
        long j5 = (j4 >>> 32) + (j * (((long) iArr[3]) & 4294967295L));
        iArr[3] = (int) j5;
        long j6 = (j5 >>> 32) + (j * (((long) iArr[4]) & 4294967295L));
        iArr[4] = (int) j6;
        long j7 = (j6 >>> 32) + (j * (((long) iArr[5]) & 4294967295L));
        iArr[5] = (int) j7;
        long j8 = (j7 >>> 32) + (j * (((long) iArr[6]) & 4294967295L));
        iArr[6] = (int) j8;
        long j9 = (j8 >>> 32) + (j * (((long) iArr[7]) & 4294967295L));
        iArr[7] = (int) j9;
        return (int) (j9 >>> 32);
    }

    public static int mulByWordAddTo(int i, int[] iArr, int[] iArr2) {
        long j = ((long) i) & 4294967295L;
        long j2 = 0 + (j * (((long) iArr2[0]) & 4294967295L)) + (((long) iArr[0]) & 4294967295L);
        iArr2[0] = (int) j2;
        long j3 = (j2 >>> 32) + (j * (((long) iArr2[1]) & 4294967295L)) + (((long) iArr[1]) & 4294967295L);
        iArr2[1] = (int) j3;
        long j4 = (j3 >>> 32) + (j * (((long) iArr2[2]) & 4294967295L)) + (((long) iArr[2]) & 4294967295L);
        iArr2[2] = (int) j4;
        long j5 = (j4 >>> 32) + (j * (((long) iArr2[3]) & 4294967295L)) + (((long) iArr[3]) & 4294967295L);
        iArr2[3] = (int) j5;
        long j6 = (j5 >>> 32) + (j * (((long) iArr2[4]) & 4294967295L)) + (((long) iArr[4]) & 4294967295L);
        iArr2[4] = (int) j6;
        long j7 = (j6 >>> 32) + (j * (((long) iArr2[5]) & 4294967295L)) + (((long) iArr[5]) & 4294967295L);
        iArr2[5] = (int) j7;
        long j8 = (j7 >>> 32) + (j * (((long) iArr2[6]) & 4294967295L)) + (((long) iArr[6]) & 4294967295L);
        iArr2[6] = (int) j8;
        long j9 = (j8 >>> 32) + (j * (((long) iArr2[7]) & 4294967295L)) + (((long) iArr[7]) & 4294967295L);
        iArr2[7] = (int) j9;
        return (int) (j9 >>> 32);
    }

    public static int mulWordAddTo(int i, int[] iArr, int i2, int[] iArr2, int i3) {
        long j = ((long) i) & 4294967295L;
        long j2 = 0 + (j * (((long) iArr[i2 + 0]) & 4294967295L)) + (((long) iArr2[i3 + 0]) & 4294967295L);
        iArr2[i3 + 0] = (int) j2;
        long j3 = (j2 >>> 32) + (j * (((long) iArr[i2 + 1]) & 4294967295L)) + (((long) iArr2[i3 + 1]) & 4294967295L);
        iArr2[i3 + 1] = (int) j3;
        long j4 = (j3 >>> 32) + (j * (((long) iArr[i2 + 2]) & 4294967295L)) + (((long) iArr2[i3 + 2]) & 4294967295L);
        iArr2[i3 + 2] = (int) j4;
        long j5 = (j4 >>> 32) + (j * (((long) iArr[i2 + 3]) & 4294967295L)) + (((long) iArr2[i3 + 3]) & 4294967295L);
        iArr2[i3 + 3] = (int) j5;
        long j6 = (j5 >>> 32) + (j * (((long) iArr[i2 + 4]) & 4294967295L)) + (((long) iArr2[i3 + 4]) & 4294967295L);
        iArr2[i3 + 4] = (int) j6;
        long j7 = (j6 >>> 32) + (j * (((long) iArr[i2 + 5]) & 4294967295L)) + (((long) iArr2[i3 + 5]) & 4294967295L);
        iArr2[i3 + 5] = (int) j7;
        long j8 = (j7 >>> 32) + (j * (((long) iArr[i2 + 6]) & 4294967295L)) + (((long) iArr2[i3 + 6]) & 4294967295L);
        iArr2[i3 + 6] = (int) j8;
        long j9 = (j8 >>> 32) + (j * (((long) iArr[i2 + 7]) & 4294967295L)) + (((long) iArr2[i3 + 7]) & 4294967295L);
        iArr2[i3 + 7] = (int) j9;
        return (int) (j9 >>> 32);
    }

    public static int mul33DWordAdd(int i, long j, int[] iArr, int i2) {
        long j2 = ((long) i) & 4294967295L;
        long j3 = j & 4294967295L;
        long j4 = 0 + (j2 * j3) + (((long) iArr[i2 + 0]) & 4294967295L);
        iArr[i2 + 0] = (int) j4;
        long j5 = j4 >>> 32;
        long j6 = j >>> 32;
        long j7 = j5 + (j2 * j6) + j3 + (((long) iArr[i2 + 1]) & 4294967295L);
        iArr[i2 + 1] = (int) j7;
        long j8 = (j7 >>> 32) + j6 + (((long) iArr[i2 + 2]) & 4294967295L);
        iArr[i2 + 2] = (int) j8;
        long j9 = (j8 >>> 32) + (((long) iArr[i2 + 3]) & 4294967295L);
        iArr[i2 + 3] = (int) j9;
        if ((j9 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(8, iArr, i2, 4);
    }

    public static int mul33WordAdd(int i, int i2, int[] iArr, int i3) {
        long j = ((long) i2) & 4294967295L;
        long j2 = 0 + (j * (((long) i) & 4294967295L)) + (((long) iArr[i3 + 0]) & 4294967295L);
        iArr[i3 + 0] = (int) j2;
        long j3 = (j2 >>> 32) + j + (((long) iArr[i3 + 1]) & 4294967295L);
        iArr[i3 + 1] = (int) j3;
        long j4 = (j3 >>> 32) + (((long) iArr[i3 + 2]) & 4294967295L);
        iArr[i3 + 2] = (int) j4;
        if ((j4 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(8, iArr, i3, 3);
    }

    public static int mulWordDwordAdd(int i, long j, int[] iArr, int i2) {
        long j2 = ((long) i) & 4294967295L;
        long j3 = 0 + (j2 * (j & 4294967295L)) + (((long) iArr[i2 + 0]) & 4294967295L);
        iArr[i2 + 0] = (int) j3;
        long j4 = (j3 >>> 32) + (j2 * (j >>> 32)) + (((long) iArr[i2 + 1]) & 4294967295L);
        iArr[i2 + 1] = (int) j4;
        long j5 = (j4 >>> 32) + (((long) iArr[i2 + 2]) & 4294967295L);
        iArr[i2 + 2] = (int) j5;
        if ((j5 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(8, iArr, i2, 3);
    }

    public static int mulWord(int i, int[] iArr, int[] iArr2, int i2) {
        long j = 0;
        long j2 = ((long) i) & 4294967295L;
        int i3 = 0;
        do {
            long j3 = j + (j2 * (((long) iArr[i3]) & 4294967295L));
            iArr2[i2 + i3] = (int) j3;
            j = j3 >>> 32;
            i3++;
        } while (i3 < 8);
        return (int) j;
    }

    public static void square(int[] iArr, int[] iArr2) {
        long j = ((long) iArr[0]) & 4294967295L;
        int i = 0;
        int i2 = 7;
        int i3 = 16;
        do {
            int i4 = i2;
            i2--;
            long j2 = ((long) iArr[i4]) & 4294967295L;
            long j3 = j2 * j2;
            int i5 = i3 - 1;
            iArr2[i5] = (i << 31) | ((int) (j3 >>> 33));
            i3 = i5 - 1;
            iArr2[i3] = (int) (j3 >>> 1);
            i = (int) j3;
        } while (i2 > 0);
        long j4 = j * j;
        long j5 = (((long) (i << 31)) & 4294967295L) | (j4 >>> 33);
        iArr2[0] = (int) j4;
        int i6 = ((int) (j4 >>> 32)) & 1;
        long j6 = ((long) iArr[1]) & 4294967295L;
        long j7 = ((long) iArr2[2]) & 4294967295L;
        long j8 = j5 + (j6 * j);
        int i7 = (int) j8;
        iArr2[1] = (i7 << 1) | i6;
        int i8 = i7 >>> 31;
        long j9 = j7 + (j8 >>> 32);
        long j10 = ((long) iArr[2]) & 4294967295L;
        long j11 = ((long) iArr2[3]) & 4294967295L;
        long j12 = ((long) iArr2[4]) & 4294967295L;
        long j13 = j9 + (j10 * j);
        int i9 = (int) j13;
        iArr2[2] = (i9 << 1) | i8;
        int i10 = i9 >>> 31;
        long j14 = j11 + (j13 >>> 32) + (j10 * j6);
        long j15 = j12 + (j14 >>> 32);
        long j16 = j14 & 4294967295L;
        long j17 = ((long) iArr[3]) & 4294967295L;
        long j18 = (((long) iArr2[5]) & 4294967295L) + (j15 >>> 32);
        long j19 = j15 & 4294967295L;
        long j20 = (((long) iArr2[6]) & 4294967295L) + (j18 >>> 32);
        long j21 = j18 & 4294967295L;
        long j22 = j16 + (j17 * j);
        int i11 = (int) j22;
        iArr2[3] = (i11 << 1) | i10;
        int i12 = i11 >>> 31;
        long j23 = j19 + (j22 >>> 32) + (j17 * j6);
        long j24 = j21 + (j23 >>> 32) + (j17 * j10);
        long j25 = j23 & 4294967295L;
        long j26 = j20 + (j24 >>> 32);
        long j27 = j24 & 4294967295L;
        long j28 = ((long) iArr[4]) & 4294967295L;
        long j29 = (((long) iArr2[7]) & 4294967295L) + (j26 >>> 32);
        long j30 = j26 & 4294967295L;
        long j31 = (((long) iArr2[8]) & 4294967295L) + (j29 >>> 32);
        long j32 = j29 & 4294967295L;
        long j33 = j25 + (j28 * j);
        int i13 = (int) j33;
        iArr2[4] = (i13 << 1) | i12;
        int i14 = i13 >>> 31;
        long j34 = j27 + (j33 >>> 32) + (j28 * j6);
        long j35 = j30 + (j34 >>> 32) + (j28 * j10);
        long j36 = j34 & 4294967295L;
        long j37 = j32 + (j35 >>> 32) + (j28 * j17);
        long j38 = j35 & 4294967295L;
        long j39 = j31 + (j37 >>> 32);
        long j40 = j37 & 4294967295L;
        long j41 = ((long) iArr[5]) & 4294967295L;
        long j42 = (((long) iArr2[9]) & 4294967295L) + (j39 >>> 32);
        long j43 = j39 & 4294967295L;
        long j44 = (((long) iArr2[10]) & 4294967295L) + (j42 >>> 32);
        long j45 = j42 & 4294967295L;
        long j46 = j36 + (j41 * j);
        int i15 = (int) j46;
        iArr2[5] = (i15 << 1) | i14;
        int i16 = i15 >>> 31;
        long j47 = j38 + (j46 >>> 32) + (j41 * j6);
        long j48 = j40 + (j47 >>> 32) + (j41 * j10);
        long j49 = j47 & 4294967295L;
        long j50 = j43 + (j48 >>> 32) + (j41 * j17);
        long j51 = j48 & 4294967295L;
        long j52 = j45 + (j50 >>> 32) + (j41 * j28);
        long j53 = j50 & 4294967295L;
        long j54 = j44 + (j52 >>> 32);
        long j55 = j52 & 4294967295L;
        long j56 = ((long) iArr[6]) & 4294967295L;
        long j57 = (((long) iArr2[11]) & 4294967295L) + (j54 >>> 32);
        long j58 = j54 & 4294967295L;
        long j59 = (((long) iArr2[12]) & 4294967295L) + (j57 >>> 32);
        long j60 = j57 & 4294967295L;
        long j61 = j49 + (j56 * j);
        int i17 = (int) j61;
        iArr2[6] = (i17 << 1) | i16;
        int i18 = i17 >>> 31;
        long j62 = j51 + (j61 >>> 32) + (j56 * j6);
        long j63 = j53 + (j62 >>> 32) + (j56 * j10);
        long j64 = j62 & 4294967295L;
        long j65 = j55 + (j63 >>> 32) + (j56 * j17);
        long j66 = j63 & 4294967295L;
        long j67 = j58 + (j65 >>> 32) + (j56 * j28);
        long j68 = j65 & 4294967295L;
        long j69 = j60 + (j67 >>> 32) + (j56 * j41);
        long j70 = j67 & 4294967295L;
        long j71 = j59 + (j69 >>> 32);
        long j72 = j69 & 4294967295L;
        long j73 = ((long) iArr[7]) & 4294967295L;
        long j74 = (((long) iArr2[13]) & 4294967295L) + (j71 >>> 32);
        long j75 = j71 & 4294967295L;
        long j76 = (((long) iArr2[14]) & 4294967295L) + (j74 >>> 32);
        long j77 = j74 & 4294967295L;
        long j78 = j64 + (j73 * j);
        int i19 = (int) j78;
        iArr2[7] = (i19 << 1) | i18;
        int i20 = i19 >>> 31;
        long j79 = j66 + (j78 >>> 32) + (j73 * j6);
        long j80 = j68 + (j79 >>> 32) + (j73 * j10);
        long j81 = j70 + (j80 >>> 32) + (j73 * j17);
        long j82 = j72 + (j81 >>> 32) + (j73 * j28);
        long j83 = j75 + (j82 >>> 32) + (j73 * j41);
        long j84 = j77 + (j83 >>> 32) + (j73 * j56);
        long j85 = j76 + (j84 >>> 32);
        int i21 = (int) j79;
        iArr2[8] = (i21 << 1) | i20;
        int i22 = i21 >>> 31;
        int i23 = (int) j80;
        iArr2[9] = (i23 << 1) | i22;
        int i24 = i23 >>> 31;
        int i25 = (int) j81;
        iArr2[10] = (i25 << 1) | i24;
        int i26 = i25 >>> 31;
        int i27 = (int) j82;
        iArr2[11] = (i27 << 1) | i26;
        int i28 = i27 >>> 31;
        int i29 = (int) j83;
        iArr2[12] = (i29 << 1) | i28;
        int i30 = i29 >>> 31;
        int i31 = (int) j84;
        iArr2[13] = (i31 << 1) | i30;
        int i32 = i31 >>> 31;
        int i33 = (int) j85;
        iArr2[14] = (i33 << 1) | i32;
        iArr2[15] = ((iArr2[15] + ((int) (j85 >>> 32))) << 1) | (i33 >>> 31);
    }

    public static void square(int[] iArr, int i, int[] iArr2, int i2) {
        long j = ((long) iArr[i + 0]) & 4294967295L;
        int i3 = 0;
        int i4 = 7;
        int i5 = 16;
        do {
            int i6 = i4;
            i4--;
            long j2 = ((long) iArr[i + i6]) & 4294967295L;
            long j3 = j2 * j2;
            int i7 = i5 - 1;
            iArr2[i2 + i7] = (i3 << 31) | ((int) (j3 >>> 33));
            i5 = i7 - 1;
            iArr2[i2 + i5] = (int) (j3 >>> 1);
            i3 = (int) j3;
        } while (i4 > 0);
        long j4 = j * j;
        long j5 = (((long) (i3 << 31)) & 4294967295L) | (j4 >>> 33);
        iArr2[i2 + 0] = (int) j4;
        int i8 = ((int) (j4 >>> 32)) & 1;
        long j6 = ((long) iArr[i + 1]) & 4294967295L;
        long j7 = ((long) iArr2[i2 + 2]) & 4294967295L;
        long j8 = j5 + (j6 * j);
        int i9 = (int) j8;
        iArr2[i2 + 1] = (i9 << 1) | i8;
        int i10 = i9 >>> 31;
        long j9 = j7 + (j8 >>> 32);
        long j10 = ((long) iArr[i + 2]) & 4294967295L;
        long j11 = ((long) iArr2[i2 + 3]) & 4294967295L;
        long j12 = ((long) iArr2[i2 + 4]) & 4294967295L;
        long j13 = j9 + (j10 * j);
        int i11 = (int) j13;
        iArr2[i2 + 2] = (i11 << 1) | i10;
        int i12 = i11 >>> 31;
        long j14 = j11 + (j13 >>> 32) + (j10 * j6);
        long j15 = j12 + (j14 >>> 32);
        long j16 = j14 & 4294967295L;
        long j17 = ((long) iArr[i + 3]) & 4294967295L;
        long j18 = (((long) iArr2[i2 + 5]) & 4294967295L) + (j15 >>> 32);
        long j19 = j15 & 4294967295L;
        long j20 = (((long) iArr2[i2 + 6]) & 4294967295L) + (j18 >>> 32);
        long j21 = j18 & 4294967295L;
        long j22 = j16 + (j17 * j);
        int i13 = (int) j22;
        iArr2[i2 + 3] = (i13 << 1) | i12;
        int i14 = i13 >>> 31;
        long j23 = j19 + (j22 >>> 32) + (j17 * j6);
        long j24 = j21 + (j23 >>> 32) + (j17 * j10);
        long j25 = j23 & 4294967295L;
        long j26 = j20 + (j24 >>> 32);
        long j27 = j24 & 4294967295L;
        long j28 = ((long) iArr[i + 4]) & 4294967295L;
        long j29 = (((long) iArr2[i2 + 7]) & 4294967295L) + (j26 >>> 32);
        long j30 = j26 & 4294967295L;
        long j31 = (((long) iArr2[i2 + 8]) & 4294967295L) + (j29 >>> 32);
        long j32 = j29 & 4294967295L;
        long j33 = j25 + (j28 * j);
        int i15 = (int) j33;
        iArr2[i2 + 4] = (i15 << 1) | i14;
        int i16 = i15 >>> 31;
        long j34 = j27 + (j33 >>> 32) + (j28 * j6);
        long j35 = j30 + (j34 >>> 32) + (j28 * j10);
        long j36 = j34 & 4294967295L;
        long j37 = j32 + (j35 >>> 32) + (j28 * j17);
        long j38 = j35 & 4294967295L;
        long j39 = j31 + (j37 >>> 32);
        long j40 = j37 & 4294967295L;
        long j41 = ((long) iArr[i + 5]) & 4294967295L;
        long j42 = (((long) iArr2[i2 + 9]) & 4294967295L) + (j39 >>> 32);
        long j43 = j39 & 4294967295L;
        long j44 = (((long) iArr2[i2 + 10]) & 4294967295L) + (j42 >>> 32);
        long j45 = j42 & 4294967295L;
        long j46 = j36 + (j41 * j);
        int i17 = (int) j46;
        iArr2[i2 + 5] = (i17 << 1) | i16;
        int i18 = i17 >>> 31;
        long j47 = j38 + (j46 >>> 32) + (j41 * j6);
        long j48 = j40 + (j47 >>> 32) + (j41 * j10);
        long j49 = j47 & 4294967295L;
        long j50 = j43 + (j48 >>> 32) + (j41 * j17);
        long j51 = j48 & 4294967295L;
        long j52 = j45 + (j50 >>> 32) + (j41 * j28);
        long j53 = j50 & 4294967295L;
        long j54 = j44 + (j52 >>> 32);
        long j55 = j52 & 4294967295L;
        long j56 = ((long) iArr[i + 6]) & 4294967295L;
        long j57 = (((long) iArr2[i2 + 11]) & 4294967295L) + (j54 >>> 32);
        long j58 = j54 & 4294967295L;
        long j59 = (((long) iArr2[i2 + 12]) & 4294967295L) + (j57 >>> 32);
        long j60 = j57 & 4294967295L;
        long j61 = j49 + (j56 * j);
        int i19 = (int) j61;
        iArr2[i2 + 6] = (i19 << 1) | i18;
        int i20 = i19 >>> 31;
        long j62 = j51 + (j61 >>> 32) + (j56 * j6);
        long j63 = j53 + (j62 >>> 32) + (j56 * j10);
        long j64 = j62 & 4294967295L;
        long j65 = j55 + (j63 >>> 32) + (j56 * j17);
        long j66 = j63 & 4294967295L;
        long j67 = j58 + (j65 >>> 32) + (j56 * j28);
        long j68 = j65 & 4294967295L;
        long j69 = j60 + (j67 >>> 32) + (j56 * j41);
        long j70 = j67 & 4294967295L;
        long j71 = j59 + (j69 >>> 32);
        long j72 = j69 & 4294967295L;
        long j73 = ((long) iArr[i + 7]) & 4294967295L;
        long j74 = (((long) iArr2[i2 + 13]) & 4294967295L) + (j71 >>> 32);
        long j75 = j71 & 4294967295L;
        long j76 = (((long) iArr2[i2 + 14]) & 4294967295L) + (j74 >>> 32);
        long j77 = j74 & 4294967295L;
        long j78 = j64 + (j73 * j);
        int i21 = (int) j78;
        iArr2[i2 + 7] = (i21 << 1) | i20;
        int i22 = i21 >>> 31;
        long j79 = j66 + (j78 >>> 32) + (j73 * j6);
        long j80 = j68 + (j79 >>> 32) + (j73 * j10);
        long j81 = j70 + (j80 >>> 32) + (j73 * j17);
        long j82 = j72 + (j81 >>> 32) + (j73 * j28);
        long j83 = j75 + (j82 >>> 32) + (j73 * j41);
        long j84 = j77 + (j83 >>> 32) + (j73 * j56);
        long j85 = j76 + (j84 >>> 32);
        int i23 = (int) j79;
        iArr2[i2 + 8] = (i23 << 1) | i22;
        int i24 = i23 >>> 31;
        int i25 = (int) j80;
        iArr2[i2 + 9] = (i25 << 1) | i24;
        int i26 = i25 >>> 31;
        int i27 = (int) j81;
        iArr2[i2 + 10] = (i27 << 1) | i26;
        int i28 = i27 >>> 31;
        int i29 = (int) j82;
        iArr2[i2 + 11] = (i29 << 1) | i28;
        int i30 = i29 >>> 31;
        int i31 = (int) j83;
        iArr2[i2 + 12] = (i31 << 1) | i30;
        int i32 = i31 >>> 31;
        int i33 = (int) j84;
        iArr2[i2 + 13] = (i33 << 1) | i32;
        int i34 = i33 >>> 31;
        int i35 = (int) j85;
        iArr2[i2 + 14] = (i35 << 1) | i34;
        iArr2[i2 + 15] = ((iArr2[i2 + 15] + ((int) (j85 >>> 32))) << 1) | (i35 >>> 31);
    }

    public static int sub(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = 0 + ((((long) iArr[0]) & 4294967295L) - (((long) iArr2[0]) & 4294967295L));
        iArr3[0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr[1]) & 4294967295L) - (((long) iArr2[1]) & 4294967295L));
        iArr3[1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr[2]) & 4294967295L) - (((long) iArr2[2]) & 4294967295L));
        iArr3[2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr[3]) & 4294967295L) - (((long) iArr2[3]) & 4294967295L));
        iArr3[3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr[4]) & 4294967295L) - (((long) iArr2[4]) & 4294967295L));
        iArr3[4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr[5]) & 4294967295L) - (((long) iArr2[5]) & 4294967295L));
        iArr3[5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr[6]) & 4294967295L) - (((long) iArr2[6]) & 4294967295L));
        iArr3[6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr[7]) & 4294967295L) - (((long) iArr2[7]) & 4294967295L));
        iArr3[7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int sub(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        long j = 0 + ((((long) iArr[i + 0]) & 4294967295L) - (((long) iArr2[i2 + 0]) & 4294967295L));
        iArr3[i3 + 0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr[i + 1]) & 4294967295L) - (((long) iArr2[i2 + 1]) & 4294967295L));
        iArr3[i3 + 1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr[i + 2]) & 4294967295L) - (((long) iArr2[i2 + 2]) & 4294967295L));
        iArr3[i3 + 2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr[i + 3]) & 4294967295L) - (((long) iArr2[i2 + 3]) & 4294967295L));
        iArr3[i3 + 3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr[i + 4]) & 4294967295L) - (((long) iArr2[i2 + 4]) & 4294967295L));
        iArr3[i3 + 4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr[i + 5]) & 4294967295L) - (((long) iArr2[i2 + 5]) & 4294967295L));
        iArr3[i3 + 5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr[i + 6]) & 4294967295L) - (((long) iArr2[i2 + 6]) & 4294967295L));
        iArr3[i3 + 6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr[i + 7]) & 4294967295L) - (((long) iArr2[i2 + 7]) & 4294967295L));
        iArr3[i3 + 7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int subBothFrom(int[] iArr, int[] iArr2, int[] iArr3) {
        long j = 0 + (((((long) iArr3[0]) & 4294967295L) - (((long) iArr[0]) & 4294967295L)) - (((long) iArr2[0]) & 4294967295L));
        iArr3[0] = (int) j;
        long j2 = (j >> 32) + (((((long) iArr3[1]) & 4294967295L) - (((long) iArr[1]) & 4294967295L)) - (((long) iArr2[1]) & 4294967295L));
        iArr3[1] = (int) j2;
        long j3 = (j2 >> 32) + (((((long) iArr3[2]) & 4294967295L) - (((long) iArr[2]) & 4294967295L)) - (((long) iArr2[2]) & 4294967295L));
        iArr3[2] = (int) j3;
        long j4 = (j3 >> 32) + (((((long) iArr3[3]) & 4294967295L) - (((long) iArr[3]) & 4294967295L)) - (((long) iArr2[3]) & 4294967295L));
        iArr3[3] = (int) j4;
        long j5 = (j4 >> 32) + (((((long) iArr3[4]) & 4294967295L) - (((long) iArr[4]) & 4294967295L)) - (((long) iArr2[4]) & 4294967295L));
        iArr3[4] = (int) j5;
        long j6 = (j5 >> 32) + (((((long) iArr3[5]) & 4294967295L) - (((long) iArr[5]) & 4294967295L)) - (((long) iArr2[5]) & 4294967295L));
        iArr3[5] = (int) j6;
        long j7 = (j6 >> 32) + (((((long) iArr3[6]) & 4294967295L) - (((long) iArr[6]) & 4294967295L)) - (((long) iArr2[6]) & 4294967295L));
        iArr3[6] = (int) j7;
        long j8 = (j7 >> 32) + (((((long) iArr3[7]) & 4294967295L) - (((long) iArr[7]) & 4294967295L)) - (((long) iArr2[7]) & 4294967295L));
        iArr3[7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int subFrom(int[] iArr, int[] iArr2) {
        long j = 0 + ((((long) iArr2[0]) & 4294967295L) - (((long) iArr[0]) & 4294967295L));
        iArr2[0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr2[1]) & 4294967295L) - (((long) iArr[1]) & 4294967295L));
        iArr2[1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr2[2]) & 4294967295L) - (((long) iArr[2]) & 4294967295L));
        iArr2[2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr2[3]) & 4294967295L) - (((long) iArr[3]) & 4294967295L));
        iArr2[3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr2[4]) & 4294967295L) - (((long) iArr[4]) & 4294967295L));
        iArr2[4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr2[5]) & 4294967295L) - (((long) iArr[5]) & 4294967295L));
        iArr2[5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr2[6]) & 4294967295L) - (((long) iArr[6]) & 4294967295L));
        iArr2[6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr2[7]) & 4294967295L) - (((long) iArr[7]) & 4294967295L));
        iArr2[7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int subFrom(int[] iArr, int[] iArr2, int i) {
        long j = (((long) i) & 4294967295L) + ((((long) iArr2[0]) & 4294967295L) - (((long) iArr[0]) & 4294967295L));
        iArr2[0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr2[1]) & 4294967295L) - (((long) iArr[1]) & 4294967295L));
        iArr2[1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr2[2]) & 4294967295L) - (((long) iArr[2]) & 4294967295L));
        iArr2[2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr2[3]) & 4294967295L) - (((long) iArr[3]) & 4294967295L));
        iArr2[3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr2[4]) & 4294967295L) - (((long) iArr[4]) & 4294967295L));
        iArr2[4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr2[5]) & 4294967295L) - (((long) iArr[5]) & 4294967295L));
        iArr2[5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr2[6]) & 4294967295L) - (((long) iArr[6]) & 4294967295L));
        iArr2[6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr2[7]) & 4294967295L) - (((long) iArr[7]) & 4294967295L));
        iArr2[7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int subFrom(int[] iArr, int i, int[] iArr2, int i2) {
        long j = 0 + ((((long) iArr2[i2 + 0]) & 4294967295L) - (((long) iArr[i + 0]) & 4294967295L));
        iArr2[i2 + 0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr2[i2 + 1]) & 4294967295L) - (((long) iArr[i + 1]) & 4294967295L));
        iArr2[i2 + 1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr2[i2 + 2]) & 4294967295L) - (((long) iArr[i + 2]) & 4294967295L));
        iArr2[i2 + 2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr2[i2 + 3]) & 4294967295L) - (((long) iArr[i + 3]) & 4294967295L));
        iArr2[i2 + 3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr2[i2 + 4]) & 4294967295L) - (((long) iArr[i + 4]) & 4294967295L));
        iArr2[i2 + 4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr2[i2 + 5]) & 4294967295L) - (((long) iArr[i + 5]) & 4294967295L));
        iArr2[i2 + 5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr2[i2 + 6]) & 4294967295L) - (((long) iArr[i + 6]) & 4294967295L));
        iArr2[i2 + 6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr2[i2 + 7]) & 4294967295L) - (((long) iArr[i + 7]) & 4294967295L));
        iArr2[i2 + 7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static int subFrom(int[] iArr, int i, int[] iArr2, int i2, int i3) {
        long j = (((long) i3) & 4294967295L) + ((((long) iArr2[i2 + 0]) & 4294967295L) - (((long) iArr[i + 0]) & 4294967295L));
        iArr2[i2 + 0] = (int) j;
        long j2 = (j >> 32) + ((((long) iArr2[i2 + 1]) & 4294967295L) - (((long) iArr[i + 1]) & 4294967295L));
        iArr2[i2 + 1] = (int) j2;
        long j3 = (j2 >> 32) + ((((long) iArr2[i2 + 2]) & 4294967295L) - (((long) iArr[i + 2]) & 4294967295L));
        iArr2[i2 + 2] = (int) j3;
        long j4 = (j3 >> 32) + ((((long) iArr2[i2 + 3]) & 4294967295L) - (((long) iArr[i + 3]) & 4294967295L));
        iArr2[i2 + 3] = (int) j4;
        long j5 = (j4 >> 32) + ((((long) iArr2[i2 + 4]) & 4294967295L) - (((long) iArr[i + 4]) & 4294967295L));
        iArr2[i2 + 4] = (int) j5;
        long j6 = (j5 >> 32) + ((((long) iArr2[i2 + 5]) & 4294967295L) - (((long) iArr[i + 5]) & 4294967295L));
        iArr2[i2 + 5] = (int) j6;
        long j7 = (j6 >> 32) + ((((long) iArr2[i2 + 6]) & 4294967295L) - (((long) iArr[i + 6]) & 4294967295L));
        iArr2[i2 + 6] = (int) j7;
        long j8 = (j7 >> 32) + ((((long) iArr2[i2 + 7]) & 4294967295L) - (((long) iArr[i + 7]) & 4294967295L));
        iArr2[i2 + 7] = (int) j8;
        return (int) (j8 >> 32);
    }

    public static BigInteger toBigInteger(int[] iArr) {
        byte[] bArr = new byte[32];
        for (int i = 0; i < 8; i++) {
            int i2 = iArr[i];
            if (i2 != 0) {
                Pack.intToBigEndian(i2, bArr, (7 - i) << 2);
            }
        }
        return new BigInteger(1, bArr);
    }

    public static BigInteger toBigInteger64(long[] jArr) {
        byte[] bArr = new byte[32];
        for (int i = 0; i < 4; i++) {
            long j = jArr[i];
            if (j != 0) {
                Pack.longToBigEndian(j, bArr, (3 - i) << 3);
            }
        }
        return new BigInteger(1, bArr);
    }

    public static void zero(int[] iArr) {
        iArr[0] = 0;
        iArr[1] = 0;
        iArr[2] = 0;
        iArr[3] = 0;
        iArr[4] = 0;
        iArr[5] = 0;
        iArr[6] = 0;
        iArr[7] = 0;
    }
}
