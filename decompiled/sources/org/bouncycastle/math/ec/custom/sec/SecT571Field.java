package org.bouncycastle.math.ec.custom.sec;

import com.sun.jna.platform.win32.WinError;
import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT571Field.class */
public class SecT571Field {
    private static final long M59 = 576460752303423487L;
    private static final long[] ROOT_Z = {3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L};

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 0; i < 9; i++) {
            jArr3[i] = jArr[i] ^ jArr2[i];
        }
    }

    private static void add(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3) {
        for (int i4 = 0; i4 < 9; i4++) {
            jArr3[i3 + i4] = jArr[i + i4] ^ jArr2[i2 + i4];
        }
    }

    public static void addBothTo(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 0; i < 9; i++) {
            int i2 = i;
            jArr3[i2] = jArr3[i2] ^ (jArr[i] ^ jArr2[i]);
        }
    }

    private static void addBothTo(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3) {
        for (int i4 = 0; i4 < 9; i4++) {
            int i5 = i3 + i4;
            jArr3[i5] = jArr3[i5] ^ (jArr[i + i4] ^ jArr2[i2 + i4]);
        }
    }

    public static void addExt(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 0; i < 18; i++) {
            jArr3[i] = jArr[i] ^ jArr2[i];
        }
    }

    public static void addOne(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0] ^ 1;
        for (int i = 1; i < 9; i++) {
            jArr2[i] = jArr[i];
        }
    }

    private static void addTo(long[] jArr, long[] jArr2) {
        for (int i = 0; i < 9; i++) {
            int i2 = i;
            jArr2[i2] = jArr2[i2] ^ jArr[i];
        }
    }

    public static long[] fromBigInteger(BigInteger bigInteger) {
        return Nat.fromBigInteger64(WinError.ERROR_IO_PRIVILEGE_FAILED, bigInteger);
    }

    public static void halfTrace(long[] jArr, long[] jArr2) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        Nat576.copy64(jArr, jArr2);
        for (int i = 1; i < 571; i += 2) {
            implSquare(jArr2, jArrCreateExt64);
            reduce(jArrCreateExt64, jArr2);
            implSquare(jArr2, jArrCreateExt64);
            reduce(jArrCreateExt64, jArr2);
            addTo(jArr, jArr2);
        }
    }

    public static void invert(long[] jArr, long[] jArr2) {
        if (Nat576.isZero64(jArr)) {
            throw new IllegalStateException();
        }
        long[] jArrCreate64 = Nat576.create64();
        long[] jArrCreate65 = Nat576.create64();
        long[] jArrCreate66 = Nat576.create64();
        square(jArr, jArrCreate66);
        square(jArrCreate66, jArrCreate64);
        square(jArrCreate64, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 2, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        multiply(jArrCreate64, jArrCreate66, jArrCreate64);
        squareN(jArrCreate64, 5, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate65, 5, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 15, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate66);
        squareN(jArrCreate66, 30, jArrCreate64);
        squareN(jArrCreate64, 30, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 60, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate65, 60, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate64, 180, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        squareN(jArrCreate65, 180, jArrCreate65);
        multiply(jArrCreate64, jArrCreate65, jArrCreate64);
        multiply(jArrCreate64, jArrCreate66, jArr2);
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implMultiply(jArr, jArr2, jArrCreateExt64);
        reduce(jArrCreateExt64, jArr3);
    }

    public static void multiplyAddToExt(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implMultiply(jArr, jArr2, jArrCreateExt64);
        addExt(jArr3, jArrCreateExt64, jArr3);
    }

    public static void multiplyPrecomp(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implMultiplyPrecomp(jArr, jArr2, jArrCreateExt64);
        reduce(jArrCreateExt64, jArr3);
    }

    public static void multiplyPrecompAddToExt(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implMultiplyPrecomp(jArr, jArr2, jArrCreateExt64);
        addExt(jArr3, jArrCreateExt64, jArr3);
    }

    public static long[] precompMultiplicand(long[] jArr) {
        long[] jArr2 = new long[144 << 1];
        System.arraycopy(jArr, 0, jArr2, 9, 9);
        int i = 0;
        for (int i2 = 7; i2 > 0; i2--) {
            i += 18;
            Nat.shiftUpBit64(9, jArr2, i >>> 1, 0L, jArr2, i);
            reduce5(jArr2, i);
            add(jArr2, 9, jArr2, i, jArr2, i + 9);
        }
        Nat.shiftUpBits64(144, jArr2, 0, 4, 0L, jArr2, 144);
        return jArr2;
    }

    public static void reduce(long[] jArr, long[] jArr2) {
        long j = jArr[9];
        long j2 = jArr[17];
        long j3 = (((j ^ (j2 >>> 59)) ^ (j2 >>> 57)) ^ (j2 >>> 54)) ^ (j2 >>> 49);
        long j4 = (((jArr[8] ^ (j2 << 5)) ^ (j2 << 7)) ^ (j2 << 10)) ^ (j2 << 15);
        for (int i = 16; i >= 10; i--) {
            long j5 = jArr[i];
            jArr2[i - 8] = (((j4 ^ (j5 >>> 59)) ^ (j5 >>> 57)) ^ (j5 >>> 54)) ^ (j5 >>> 49);
            j4 = (((jArr[i - 9] ^ (j5 << 5)) ^ (j5 << 7)) ^ (j5 << 10)) ^ (j5 << 15);
        }
        jArr2[1] = (((j4 ^ (j3 >>> 59)) ^ (j3 >>> 57)) ^ (j3 >>> 54)) ^ (j3 >>> 49);
        long j6 = (((jArr[0] ^ (j3 << 5)) ^ (j3 << 7)) ^ (j3 << 10)) ^ (j3 << 15);
        long j7 = jArr2[8];
        long j8 = j7 >>> 59;
        jArr2[0] = (((j6 ^ j8) ^ (j8 << 2)) ^ (j8 << 5)) ^ (j8 << 10);
        jArr2[8] = j7 & M59;
    }

    public static void reduce5(long[] jArr, int i) {
        long j = jArr[i + 8];
        long j2 = j >>> 59;
        jArr[i] = jArr[i] ^ (((j2 ^ (j2 << 2)) ^ (j2 << 5)) ^ (j2 << 10));
        jArr[i + 8] = j & M59;
    }

    public static void sqrt(long[] jArr, long[] jArr2) {
        long[] jArrCreate64 = Nat576.create64();
        long[] jArrCreate65 = Nat576.create64();
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            int i3 = i;
            int i4 = i + 1;
            long jUnshuffle = Interleave.unshuffle(jArr[i3]);
            i = i4 + 1;
            long jUnshuffle2 = Interleave.unshuffle(jArr[i4]);
            jArrCreate64[i2] = (jUnshuffle & 4294967295L) | (jUnshuffle2 << 32);
            jArrCreate65[i2] = (jUnshuffle >>> 32) | (jUnshuffle2 & (-4294967296L));
        }
        long jUnshuffle3 = Interleave.unshuffle(jArr[i]);
        jArrCreate64[4] = jUnshuffle3 & 4294967295L;
        jArrCreate65[4] = jUnshuffle3 >>> 32;
        multiply(jArrCreate65, ROOT_Z, jArr2);
        add(jArr2, jArrCreate64, jArr2);
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implSquare(jArr, jArrCreateExt64);
        reduce(jArrCreateExt64, jArr2);
    }

    public static void squareAddToExt(long[] jArr, long[] jArr2) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implSquare(jArr, jArrCreateExt64);
        addExt(jArr2, jArrCreateExt64, jArr2);
    }

    public static void squareN(long[] jArr, int i, long[] jArr2) {
        long[] jArrCreateExt64 = Nat576.createExt64();
        implSquare(jArr, jArrCreateExt64);
        reduce(jArrCreateExt64, jArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            }
            implSquare(jArr2, jArrCreateExt64);
            reduce(jArrCreateExt64, jArr2);
        }
    }

    public static int trace(long[] jArr) {
        return ((int) ((jArr[0] ^ (jArr[8] >>> 49)) ^ (jArr[8] >>> 57))) & 1;
    }

    protected static void implMultiply(long[] jArr, long[] jArr2, long[] jArr3) {
        long[] jArr4 = new long[16];
        for (int i = 0; i < 9; i++) {
            implMulwAcc(jArr4, jArr[i], jArr2[i], jArr3, i << 1);
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
        long j14 = j12 ^ jArr3[13];
        long j15 = j13 ^ jArr3[14];
        jArr3[7] = j15 ^ j14;
        long j16 = j14 ^ jArr3[15];
        long j17 = j15 ^ jArr3[16];
        jArr3[8] = j17 ^ j16;
        long j18 = j17 ^ (j16 ^ jArr3[17]);
        jArr3[9] = jArr3[0] ^ j18;
        jArr3[10] = jArr3[1] ^ j18;
        jArr3[11] = jArr3[2] ^ j18;
        jArr3[12] = jArr3[3] ^ j18;
        jArr3[13] = jArr3[4] ^ j18;
        jArr3[14] = jArr3[5] ^ j18;
        jArr3[15] = jArr3[6] ^ j18;
        jArr3[16] = jArr3[7] ^ j18;
        jArr3[17] = jArr3[8] ^ j18;
        implMulwAcc(jArr4, jArr[0] ^ jArr[1], jArr2[0] ^ jArr2[1], jArr3, 1);
        implMulwAcc(jArr4, jArr[0] ^ jArr[2], jArr2[0] ^ jArr2[2], jArr3, 2);
        implMulwAcc(jArr4, jArr[0] ^ jArr[3], jArr2[0] ^ jArr2[3], jArr3, 3);
        implMulwAcc(jArr4, jArr[1] ^ jArr[2], jArr2[1] ^ jArr2[2], jArr3, 3);
        implMulwAcc(jArr4, jArr[0] ^ jArr[4], jArr2[0] ^ jArr2[4], jArr3, 4);
        implMulwAcc(jArr4, jArr[1] ^ jArr[3], jArr2[1] ^ jArr2[3], jArr3, 4);
        implMulwAcc(jArr4, jArr[0] ^ jArr[5], jArr2[0] ^ jArr2[5], jArr3, 5);
        implMulwAcc(jArr4, jArr[1] ^ jArr[4], jArr2[1] ^ jArr2[4], jArr3, 5);
        implMulwAcc(jArr4, jArr[2] ^ jArr[3], jArr2[2] ^ jArr2[3], jArr3, 5);
        implMulwAcc(jArr4, jArr[0] ^ jArr[6], jArr2[0] ^ jArr2[6], jArr3, 6);
        implMulwAcc(jArr4, jArr[1] ^ jArr[5], jArr2[1] ^ jArr2[5], jArr3, 6);
        implMulwAcc(jArr4, jArr[2] ^ jArr[4], jArr2[2] ^ jArr2[4], jArr3, 6);
        implMulwAcc(jArr4, jArr[0] ^ jArr[7], jArr2[0] ^ jArr2[7], jArr3, 7);
        implMulwAcc(jArr4, jArr[1] ^ jArr[6], jArr2[1] ^ jArr2[6], jArr3, 7);
        implMulwAcc(jArr4, jArr[2] ^ jArr[5], jArr2[2] ^ jArr2[5], jArr3, 7);
        implMulwAcc(jArr4, jArr[3] ^ jArr[4], jArr2[3] ^ jArr2[4], jArr3, 7);
        implMulwAcc(jArr4, jArr[0] ^ jArr[8], jArr2[0] ^ jArr2[8], jArr3, 8);
        implMulwAcc(jArr4, jArr[1] ^ jArr[7], jArr2[1] ^ jArr2[7], jArr3, 8);
        implMulwAcc(jArr4, jArr[2] ^ jArr[6], jArr2[2] ^ jArr2[6], jArr3, 8);
        implMulwAcc(jArr4, jArr[3] ^ jArr[5], jArr2[3] ^ jArr2[5], jArr3, 8);
        implMulwAcc(jArr4, jArr[1] ^ jArr[8], jArr2[1] ^ jArr2[8], jArr3, 9);
        implMulwAcc(jArr4, jArr[2] ^ jArr[7], jArr2[2] ^ jArr2[7], jArr3, 9);
        implMulwAcc(jArr4, jArr[3] ^ jArr[6], jArr2[3] ^ jArr2[6], jArr3, 9);
        implMulwAcc(jArr4, jArr[4] ^ jArr[5], jArr2[4] ^ jArr2[5], jArr3, 9);
        implMulwAcc(jArr4, jArr[2] ^ jArr[8], jArr2[2] ^ jArr2[8], jArr3, 10);
        implMulwAcc(jArr4, jArr[3] ^ jArr[7], jArr2[3] ^ jArr2[7], jArr3, 10);
        implMulwAcc(jArr4, jArr[4] ^ jArr[6], jArr2[4] ^ jArr2[6], jArr3, 10);
        implMulwAcc(jArr4, jArr[3] ^ jArr[8], jArr2[3] ^ jArr2[8], jArr3, 11);
        implMulwAcc(jArr4, jArr[4] ^ jArr[7], jArr2[4] ^ jArr2[7], jArr3, 11);
        implMulwAcc(jArr4, jArr[5] ^ jArr[6], jArr2[5] ^ jArr2[6], jArr3, 11);
        implMulwAcc(jArr4, jArr[4] ^ jArr[8], jArr2[4] ^ jArr2[8], jArr3, 12);
        implMulwAcc(jArr4, jArr[5] ^ jArr[7], jArr2[5] ^ jArr2[7], jArr3, 12);
        implMulwAcc(jArr4, jArr[5] ^ jArr[8], jArr2[5] ^ jArr2[8], jArr3, 13);
        implMulwAcc(jArr4, jArr[6] ^ jArr[7], jArr2[6] ^ jArr2[7], jArr3, 13);
        implMulwAcc(jArr4, jArr[6] ^ jArr[8], jArr2[6] ^ jArr2[8], jArr3, 14);
        implMulwAcc(jArr4, jArr[7] ^ jArr[8], jArr2[7] ^ jArr2[8], jArr3, 15);
    }

    protected static void implMultiplyPrecomp(long[] jArr, long[] jArr2, long[] jArr3) {
        for (int i = 56; i >= 0; i -= 8) {
            for (int i2 = 1; i2 < 9; i2 += 2) {
                int i3 = (int) (jArr[i2] >>> i);
                addBothTo(jArr2, 9 * (i3 & 15), jArr2, 9 * (((i3 >>> 4) & 15) + 16), jArr3, i2 - 1);
            }
            Nat.shiftUpBits64(16, jArr3, 0, 8, 0L);
        }
        for (int i4 = 56; i4 >= 0; i4 -= 8) {
            for (int i5 = 0; i5 < 9; i5 += 2) {
                int i6 = (int) (jArr[i5] >>> i4);
                addBothTo(jArr2, 9 * (i6 & 15), jArr2, 9 * (((i6 >>> 4) & 15) + 16), jArr3, i5);
            }
            if (i4 > 0) {
                Nat.shiftUpBits64(18, jArr3, 0, 8, 0L);
            }
        }
    }

    protected static void implMulwAcc(long[] jArr, long j, long j2, long[] jArr2, int i) {
        jArr[1] = j2;
        for (int i2 = 2; i2 < 16; i2 += 2) {
            jArr[i2] = jArr[i2 >>> 1] << 1;
            jArr[i2 + 1] = jArr[i2] ^ j2;
        }
        int i3 = (int) j;
        long j3 = 0;
        long j4 = jArr[i3 & 15] ^ (jArr[(i3 >>> 4) & 15] << 4);
        int i4 = 56;
        do {
            int i5 = (int) (j >>> i4);
            long j5 = jArr[i5 & 15] ^ (jArr[(i5 >>> 4) & 15] << 4);
            j4 ^= j5 << i4;
            j3 ^= j5 >>> (-i4);
            i4 -= 8;
        } while (i4 > 0);
        for (int i6 = 0; i6 < 7; i6++) {
            j = (j & (-72340172838076674L)) >>> 1;
            j3 ^= j & ((j2 << i6) >> 63);
        }
        jArr2[i] = jArr2[i] ^ j4;
        int i7 = i + 1;
        jArr2[i7] = jArr2[i7] ^ j3;
    }

    protected static void implSquare(long[] jArr, long[] jArr2) {
        Interleave.expand64To128(jArr, 0, 9, jArr2, 0);
    }
}
