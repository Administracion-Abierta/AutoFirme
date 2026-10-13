package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT409Field.class */
public class SecT409Field {
    private static final long M25 = 33554431;
    private static final long M59 = 576460752303423487L;

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
        jArr3[2] = jArr[2] ^ jArr2[2];
        jArr3[3] = jArr[3] ^ jArr2[3];
        jArr3[4] = jArr[4] ^ jArr2[4];
        jArr3[5] = jArr[5] ^ jArr2[5];
        jArr3[6] = jArr[6] ^ jArr2[6];
    }

    public static void addExt(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 0; i < 13; i++) {
            jArr3[i] = jArr[i] ^ jArr2[i];
        }
    }

    public static void addOne(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0] ^ 1;
        jArr2[1] = jArr[1];
        jArr2[2] = jArr[2];
        jArr2[3] = jArr[3];
        jArr2[4] = jArr[4];
        jArr2[5] = jArr[5];
        jArr2[6] = jArr[6];
    }

    private static void addTo(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr2[0] ^ jArr[0];
        jArr2[1] = jArr2[1] ^ jArr[1];
        jArr2[2] = jArr2[2] ^ jArr[2];
        jArr2[3] = jArr2[3] ^ jArr[3];
        jArr2[4] = jArr2[4] ^ jArr[4];
        jArr2[5] = jArr2[5] ^ jArr[5];
        jArr2[6] = jArr2[6] ^ jArr[6];
    }

    public static long[] fromBigInteger(BigInteger bigInteger) {
        return Nat.fromBigInteger64(409, bigInteger);
    }

    public static void halfTrace(long[] jArr, long[] jArr2) {
        long[] jArrCreate64 = Nat.create64(13);
        Nat448.copy64(jArr, jArr2);
        for (int i = 1; i < 409; i += 2) {
            implSquare(jArr2, jArrCreate64);
            reduce(jArrCreate64, jArr2);
            implSquare(jArr2, jArrCreate64);
            reduce(jArrCreate64, jArr2);
            addTo(jArr, jArr2);
        }
    }

    public static void invert(long[] jArr, long[] jArr2) {
        if (Nat448.isZero64(jArr)) {
            throw new IllegalStateException();
        }
        long[] jArrCreate64 = Nat448.create64();
        long[] jArrCreate65 = Nat448.create64();
        long[] jArrCreate66 = Nat448.create64();
        square(jArr, jArrCreate64);
        squareN(jArrCreate64, 1, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate65, 1, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 3, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 6, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 12, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate66);
        squareN(jArrCreate66, 24, jArrCreate64);
        squareN(jArrCreate64, 24, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 48, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 96, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 192, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        multiply(jArrCreate64, jArrCreate66, jArr2);
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat448.createExt64();
        implMultiply(jArr, jArr2, jArrCreateExt64);
        reduce(jArrCreateExt64, jArr3);
    }

    public static void multiplyAddToExt(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat448.createExt64();
        implMultiply(jArr, jArr2, jArrCreateExt64);
        addExt(jArr3, jArrCreateExt64, jArr3);
    }

    public static void reduce(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = jArr[12];
        long j10 = j6 ^ (j9 << 39);
        long j11 = j7 ^ ((j9 >>> 25) ^ (j9 << 62));
        long j12 = j8 ^ (j9 >>> 2);
        long j13 = jArr[11];
        long j14 = j5 ^ (j13 << 39);
        long j15 = j10 ^ ((j13 >>> 25) ^ (j13 << 62));
        long j16 = j11 ^ (j13 >>> 2);
        long j17 = jArr[10];
        long j18 = j4 ^ (j17 << 39);
        long j19 = j14 ^ ((j17 >>> 25) ^ (j17 << 62));
        long j20 = j15 ^ (j17 >>> 2);
        long j21 = jArr[9];
        long j22 = j3 ^ (j21 << 39);
        long j23 = j18 ^ ((j21 >>> 25) ^ (j21 << 62));
        long j24 = j19 ^ (j21 >>> 2);
        long j25 = jArr[8];
        long j26 = j2 ^ (j25 << 39);
        long j27 = j22 ^ ((j25 >>> 25) ^ (j25 << 62));
        long j28 = j23 ^ (j25 >>> 2);
        long j29 = j ^ (j12 << 39);
        long j30 = j26 ^ ((j12 >>> 25) ^ (j12 << 62));
        long j31 = j27 ^ (j12 >>> 2);
        long j32 = j16 >>> 25;
        jArr2[0] = j29 ^ j32;
        jArr2[1] = j30 ^ (j32 << 23);
        jArr2[2] = j31;
        jArr2[3] = j28;
        jArr2[4] = j24;
        jArr2[5] = j20;
        jArr2[6] = j16 & M25;
    }

    public static void reduce39(long[] jArr, int i) {
        long j = jArr[i + 6];
        long j2 = j >>> 25;
        jArr[i] = jArr[i] ^ j2;
        int i2 = i + 1;
        jArr[i2] = jArr[i2] ^ (j2 << 23);
        jArr[i + 6] = j & M25;
    }

    public static void sqrt(long[] jArr, long[] jArr2) {
        long jUnshuffle = Interleave.unshuffle(jArr[0]);
        long jUnshuffle2 = Interleave.unshuffle(jArr[1]);
        long j = (jUnshuffle & 4294967295L) | (jUnshuffle2 << 32);
        long j2 = (jUnshuffle >>> 32) | (jUnshuffle2 & (-4294967296L));
        long jUnshuffle3 = Interleave.unshuffle(jArr[2]);
        long jUnshuffle4 = Interleave.unshuffle(jArr[3]);
        long j3 = (jUnshuffle3 & 4294967295L) | (jUnshuffle4 << 32);
        long j4 = (jUnshuffle3 >>> 32) | (jUnshuffle4 & (-4294967296L));
        long jUnshuffle5 = Interleave.unshuffle(jArr[4]);
        long jUnshuffle6 = Interleave.unshuffle(jArr[5]);
        long j5 = (jUnshuffle5 & 4294967295L) | (jUnshuffle6 << 32);
        long j6 = (jUnshuffle5 >>> 32) | (jUnshuffle6 & (-4294967296L));
        long jUnshuffle7 = Interleave.unshuffle(jArr[6]);
        long j7 = jUnshuffle7 & 4294967295L;
        long j8 = jUnshuffle7 >>> 32;
        jArr2[0] = j ^ (j2 << 44);
        jArr2[1] = (j3 ^ (j4 << 44)) ^ (j2 >>> 20);
        jArr2[2] = (j5 ^ (j6 << 44)) ^ (j4 >>> 20);
        jArr2[3] = ((j7 ^ (j8 << 44)) ^ (j6 >>> 20)) ^ (j2 << 13);
        jArr2[4] = ((j8 >>> 20) ^ (j4 << 13)) ^ (j2 >>> 51);
        jArr2[5] = (j6 << 13) ^ (j4 >>> 51);
        jArr2[6] = (j8 << 13) ^ (j6 >>> 51);
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArrCreate64 = Nat.create64(13);
        implSquare(jArr, jArrCreate64);
        reduce(jArrCreate64, jArr2);
    }

    public static void squareAddToExt(long[] jArr, long[] jArr2) {
        long[] jArrCreate64 = Nat.create64(13);
        implSquare(jArr, jArrCreate64);
        addExt(jArr2, jArrCreate64, jArr2);
    }

    public static void squareN(long[] jArr, int i, long[] jArr2) {
        long[] jArrCreate64 = Nat.create64(13);
        implSquare(jArr, jArrCreate64);
        reduce(jArrCreate64, jArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            }
            implSquare(jArr2, jArrCreate64);
            reduce(jArrCreate64, jArr2);
        }
    }

    public static int trace(long[] jArr) {
        return ((int) jArr[0]) & 1;
    }

    protected static void implCompactExt(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = jArr[8];
        long j10 = jArr[9];
        long j11 = jArr[10];
        long j12 = jArr[11];
        long j13 = jArr[12];
        long j14 = jArr[13];
        jArr[0] = j ^ (j2 << 59);
        jArr[1] = (j2 >>> 5) ^ (j3 << 54);
        jArr[2] = (j3 >>> 10) ^ (j4 << 49);
        jArr[3] = (j4 >>> 15) ^ (j5 << 44);
        jArr[4] = (j5 >>> 20) ^ (j6 << 39);
        jArr[5] = (j6 >>> 25) ^ (j7 << 34);
        jArr[6] = (j7 >>> 30) ^ (j8 << 29);
        jArr[7] = (j8 >>> 35) ^ (j9 << 24);
        jArr[8] = (j9 >>> 40) ^ (j10 << 19);
        jArr[9] = (j10 >>> 45) ^ (j11 << 14);
        jArr[10] = (j11 >>> 50) ^ (j12 << 9);
        jArr[11] = ((j12 >>> 55) ^ (j13 << 4)) ^ (j14 << 63);
        jArr[12] = j14 >>> 1;
    }

    protected static void implExpand(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        jArr2[0] = j & M59;
        jArr2[1] = ((j >>> 59) ^ (j2 << 5)) & M59;
        jArr2[2] = ((j2 >>> 54) ^ (j3 << 10)) & M59;
        jArr2[3] = ((j3 >>> 49) ^ (j4 << 15)) & M59;
        jArr2[4] = ((j4 >>> 44) ^ (j5 << 20)) & M59;
        jArr2[5] = ((j5 >>> 39) ^ (j6 << 25)) & M59;
        jArr2[6] = (j6 >>> 34) ^ (j7 << 30);
    }

    protected static void implMultiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArr4 = new long[7];
        long[] jArr5 = new long[7];
        implExpand(jArr, jArr4);
        implExpand(jArr2, jArr5);
        long[] jArr6 = new long[8];
        for (int i = 0; i < 7; i++) {
            implMulwAcc(jArr6, jArr4[i], jArr5[i], jArr3, i << 1);
        }
        long j = jArr3[0];
        long j2 = jArr3[1];
        long j3 = j ^ jArr3[2];
        jArr3[1] = j3 ^ j2;
        long j4 = j2 ^ jArr3[3];
        long j5 = j3 ^ jArr3[4];
        jArr3[2] = j5 ^ j4;
        long j6 = j4 ^ jArr3[5];
        long j7 = j5 ^ jArr3[6];
        jArr3[3] = j7 ^ j6;
        long j8 = j6 ^ jArr3[7];
        long j9 = j7 ^ jArr3[8];
        jArr3[4] = j9 ^ j8;
        long j10 = j8 ^ jArr3[9];
        long j11 = j9 ^ jArr3[10];
        jArr3[5] = j11 ^ j10;
        long j12 = j10 ^ jArr3[11];
        long j13 = j11 ^ jArr3[12];
        jArr3[6] = j13 ^ j12;
        long j14 = j13 ^ (j12 ^ jArr3[13]);
        jArr3[7] = jArr3[0] ^ j14;
        jArr3[8] = jArr3[1] ^ j14;
        jArr3[9] = jArr3[2] ^ j14;
        jArr3[10] = jArr3[3] ^ j14;
        jArr3[11] = jArr3[4] ^ j14;
        jArr3[12] = jArr3[5] ^ j14;
        jArr3[13] = jArr3[6] ^ j14;
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[1], jArr5[0] ^ jArr5[1], jArr3, 1);
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[2], jArr5[0] ^ jArr5[2], jArr3, 2);
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[3], jArr5[0] ^ jArr5[3], jArr3, 3);
        implMulwAcc(jArr6, jArr4[1] ^ jArr4[2], jArr5[1] ^ jArr5[2], jArr3, 3);
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[4], jArr5[0] ^ jArr5[4], jArr3, 4);
        implMulwAcc(jArr6, jArr4[1] ^ jArr4[3], jArr5[1] ^ jArr5[3], jArr3, 4);
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[5], jArr5[0] ^ jArr5[5], jArr3, 5);
        implMulwAcc(jArr6, jArr4[1] ^ jArr4[4], jArr5[1] ^ jArr5[4], jArr3, 5);
        implMulwAcc(jArr6, jArr4[2] ^ jArr4[3], jArr5[2] ^ jArr5[3], jArr3, 5);
        implMulwAcc(jArr6, jArr4[0] ^ jArr4[6], jArr5[0] ^ jArr5[6], jArr3, 6);
        implMulwAcc(jArr6, jArr4[1] ^ jArr4[5], jArr5[1] ^ jArr5[5], jArr3, 6);
        implMulwAcc(jArr6, jArr4[2] ^ jArr4[4], jArr5[2] ^ jArr5[4], jArr3, 6);
        implMulwAcc(jArr6, jArr4[1] ^ jArr4[6], jArr5[1] ^ jArr5[6], jArr3, 7);
        implMulwAcc(jArr6, jArr4[2] ^ jArr4[5], jArr5[2] ^ jArr5[5], jArr3, 7);
        implMulwAcc(jArr6, jArr4[3] ^ jArr4[4], jArr5[3] ^ jArr5[4], jArr3, 7);
        implMulwAcc(jArr6, jArr4[2] ^ jArr4[6], jArr5[2] ^ jArr5[6], jArr3, 8);
        implMulwAcc(jArr6, jArr4[3] ^ jArr4[5], jArr5[3] ^ jArr5[5], jArr3, 8);
        implMulwAcc(jArr6, jArr4[3] ^ jArr4[6], jArr5[3] ^ jArr5[6], jArr3, 9);
        implMulwAcc(jArr6, jArr4[4] ^ jArr4[5], jArr5[4] ^ jArr5[5], jArr3, 9);
        implMulwAcc(jArr6, jArr4[4] ^ jArr4[6], jArr5[4] ^ jArr5[6], jArr3, 10);
        implMulwAcc(jArr6, jArr4[5] ^ jArr4[6], jArr5[5] ^ jArr5[6], jArr3, 11);
        implCompactExt(jArr3);
    }

    protected static void implMulwAcc(long[] jArr, long j, long j2, long[] jArr2, int i) {
        jArr[1] = j2;
        jArr[2] = jArr[1] << 1;
        jArr[3] = jArr[2] ^ j2;
        jArr[4] = jArr[2] << 1;
        jArr[5] = jArr[4] ^ j2;
        jArr[6] = jArr[3] << 1;
        jArr[7] = jArr[6] ^ j2;
        int i2 = (int) j;
        long j3 = 0;
        long j4 = jArr[i2 & 7] ^ (jArr[(i2 >>> 3) & 7] << 3);
        int i3 = 54;
        do {
            int i4 = (int) (j >>> i3);
            long j5 = jArr[i4 & 7] ^ (jArr[(i4 >>> 3) & 7] << 3);
            j4 ^= j5 << i3;
            j3 ^= j5 >>> (-i3);
            i3 -= 6;
        } while (i3 > 0);
        jArr2[i] = jArr2[i] ^ (j4 & M59);
        int i5 = i + 1;
        jArr2[i5] = jArr2[i5] ^ ((j4 >>> 59) ^ (j3 << 5));
    }

    protected static void implSquare(long[] jArr, long[] jArr2) {
        Interleave.expand64To128(jArr, 0, 6, jArr2, 0);
        jArr2[12] = Interleave.expand32to64((int) jArr[6]);
    }
}
