package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.raw.Interleave;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat576;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT571Field.class */
public class SecT571Field {
    private static final long M59 = 576460752303423487L;
    private static final long RM = -1190112520884487202L;
    private static final long[] ROOT_Z = {3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L};

    public static void add(long[] x, long[] y, long[] z) {
        for (int i = 0; i < 9; i++) {
            z[i] = x[i] ^ y[i];
        }
    }

    private static void add(long[] x, int xOff, long[] y, int yOff, long[] z, int zOff) {
        for (int i = 0; i < 9; i++) {
            z[zOff + i] = x[xOff + i] ^ y[yOff + i];
        }
    }

    public static void addBothTo(long[] x, long[] y, long[] z) {
        for (int i = 0; i < 9; i++) {
            int i2 = i;
            z[i2] = z[i2] ^ (x[i] ^ y[i]);
        }
    }

    private static void addBothTo(long[] x, int xOff, long[] y, int yOff, long[] z, int zOff) {
        for (int i = 0; i < 9; i++) {
            int i2 = zOff + i;
            z[i2] = z[i2] ^ (x[xOff + i] ^ y[yOff + i]);
        }
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        for (int i = 0; i < 18; i++) {
            zz[i] = xx[i] ^ yy[i];
        }
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1;
        for (int i = 1; i < 9; i++) {
            z[i] = x[i];
        }
    }

    public static long[] fromBigInteger(BigInteger x) {
        long[] z = Nat576.fromBigInteger64(x);
        reduce5(z, 0);
        return z;
    }

    public static void invert(long[] x, long[] z) {
        if (Nat576.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat576.create64();
        long[] t1 = Nat576.create64();
        long[] t2 = Nat576.create64();
        square(x, t2);
        square(t2, t0);
        square(t0, t1);
        multiply(t0, t1, t0);
        squareN(t0, 2, t1);
        multiply(t0, t1, t0);
        multiply(t0, t2, t0);
        squareN(t0, 5, t1);
        multiply(t0, t1, t0);
        squareN(t1, 5, t1);
        multiply(t0, t1, t0);
        squareN(t0, 15, t1);
        multiply(t0, t1, t2);
        squareN(t2, 30, t0);
        squareN(t0, 30, t1);
        multiply(t0, t1, t0);
        squareN(t0, 60, t1);
        multiply(t0, t1, t0);
        squareN(t1, 60, t1);
        multiply(t0, t1, t0);
        squareN(t0, 180, t1);
        multiply(t0, t1, t0);
        squareN(t1, 180, t1);
        multiply(t0, t1, t0);
        multiply(t0, t2, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat576.createExt64();
        implMultiply(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat576.createExt64();
        implMultiply(x, y, tt);
        addExt(zz, tt, zz);
    }

    public static void multiplyPrecomp(long[] x, long[] precomp, long[] z) {
        long[] tt = Nat576.createExt64();
        implMultiplyPrecomp(x, precomp, tt);
        reduce(tt, z);
    }

    public static void multiplyPrecompAddToExt(long[] x, long[] precomp, long[] zz) {
        long[] tt = Nat576.createExt64();
        implMultiplyPrecomp(x, precomp, tt);
        addExt(zz, tt, zz);
    }

    public static long[] precompMultiplicand(long[] x) {
        long[] t = new long[144 << 1];
        System.arraycopy(x, 0, t, 9, 9);
        int tOff = 0;
        for (int i = 7; i > 0; i--) {
            tOff += 18;
            Nat.shiftUpBit64(9, t, tOff >>> 1, 0L, t, tOff);
            reduce5(t, tOff);
            add(t, 9, t, tOff, t, tOff + 9);
        }
        Nat.shiftUpBits64(144, t, 0, 4, 0L, t, 144);
        return t;
    }

    public static void reduce(long[] xx, long[] z) {
        long xx09 = xx[9];
        long u = xx[17];
        long xx010 = (((xx09 ^ (u >>> 59)) ^ (u >>> 57)) ^ (u >>> 54)) ^ (u >>> 49);
        long v = (((xx[8] ^ (u << 5)) ^ (u << 7)) ^ (u << 10)) ^ (u << 15);
        for (int i = 16; i >= 10; i--) {
            long u2 = xx[i];
            z[i - 8] = (((v ^ (u2 >>> 59)) ^ (u2 >>> 57)) ^ (u2 >>> 54)) ^ (u2 >>> 49);
            v = (((xx[i - 9] ^ (u2 << 5)) ^ (u2 << 7)) ^ (u2 << 10)) ^ (u2 << 15);
        }
        z[1] = (((v ^ (xx010 >>> 59)) ^ (xx010 >>> 57)) ^ (xx010 >>> 54)) ^ (xx010 >>> 49);
        long v2 = (((xx[0] ^ (xx010 << 5)) ^ (xx010 << 7)) ^ (xx010 << 10)) ^ (xx010 << 15);
        long x08 = z[8];
        long t = x08 >>> 59;
        z[0] = (((v2 ^ t) ^ (t << 2)) ^ (t << 5)) ^ (t << 10);
        z[8] = x08 & M59;
    }

    public static void reduce5(long[] z, int zOff) {
        long z8 = z[zOff + 8];
        long t = z8 >>> 59;
        z[zOff] = z[zOff] ^ (((t ^ (t << 2)) ^ (t << 5)) ^ (t << 10));
        z[zOff + 8] = z8 & M59;
    }

    public static void sqrt(long[] x, long[] z) {
        long[] evn = Nat576.create64();
        long[] odd = Nat576.create64();
        int pos = 0;
        for (int i = 0; i < 4; i++) {
            int i2 = pos;
            int pos2 = pos + 1;
            long u0 = Interleave.unshuffle(x[i2]);
            pos = pos2 + 1;
            long u1 = Interleave.unshuffle(x[pos2]);
            evn[i] = (u0 & 4294967295L) | (u1 << 32);
            odd[i] = (u0 >>> 32) | (u1 & (-4294967296L));
        }
        long u2 = Interleave.unshuffle(x[pos]);
        evn[4] = u2 & 4294967295L;
        odd[4] = u2 >>> 32;
        multiply(odd, ROOT_Z, z);
        add(z, evn, z);
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat576.createExt64();
        implSquare(x, tt);
        reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat576.createExt64();
        implSquare(x, tt);
        addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat576.createExt64();
        implSquare(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                implSquare(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static int trace(long[] x) {
        return ((int) ((x[0] ^ (x[8] >>> 49)) ^ (x[8] >>> 57))) & 1;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long[] precomp = precompMultiplicand(y);
        implMultiplyPrecomp(x, precomp, zz);
    }

    protected static void implMultiplyPrecomp(long[] x, long[] precomp, long[] zz) {
        for (int k = 56; k >= 0; k -= 8) {
            for (int j = 1; j < 9; j += 2) {
                int aVal = (int) (x[j] >>> k);
                int u = aVal & 15;
                int v = (aVal >>> 4) & 15;
                addBothTo(precomp, 9 * u, precomp, 9 * (v + 16), zz, j - 1);
            }
            Nat.shiftUpBits64(16, zz, 0, 8, 0L);
        }
        for (int k2 = 56; k2 >= 0; k2 -= 8) {
            for (int j2 = 0; j2 < 9; j2 += 2) {
                int aVal2 = (int) (x[j2] >>> k2);
                int u2 = aVal2 & 15;
                int v2 = (aVal2 >>> 4) & 15;
                addBothTo(precomp, 9 * u2, precomp, 9 * (v2 + 16), zz, j2);
            }
            if (k2 > 0) {
                Nat.shiftUpBits64(18, zz, 0, 8, 0L);
            }
        }
    }

    protected static void implMulwAcc(long[] xs, long y, long[] z, int zOff) {
        long[] u = new long[32];
        u[1] = y;
        for (int i = 2; i < 32; i += 2) {
            u[i] = u[i >>> 1] << 1;
            u[i + 1] = u[i] ^ y;
        }
        long l = 0;
        for (int i2 = 0; i2 < 9; i2++) {
            long x = xs[i2];
            int j = (int) x;
            long l2 = l ^ u[j & 31];
            long h = 0;
            int k = 60;
            do {
                int j2 = (int) (x >>> k);
                long g = u[j2 & 31];
                l2 ^= g << k;
                h ^= g >>> (-k);
                k -= 5;
            } while (k > 0);
            for (int p = 0; p < 4; p++) {
                x = (x & RM) >>> 1;
                h ^= x & ((y << p) >> 63);
            }
            int i3 = zOff + i2;
            z[i3] = z[i3] ^ l2;
            l = h;
        }
        int i4 = zOff + 9;
        z[i4] = z[i4] ^ l;
    }

    protected static void implSquare(long[] x, long[] zz) {
        for (int i = 0; i < 9; i++) {
            Interleave.expand64To128(x[i], zz, i << 1);
        }
    }
}
