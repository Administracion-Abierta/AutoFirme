package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.raw.Interleave;
import org.spongycastle.math.raw.Nat192;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT163Field.class */
public class SecT163Field {
    private static final long M35 = 34359738367L;
    private static final long M55 = 36028797018963967L;
    private static final long[] ROOT_Z = {-5270498306774157648L, 5270498306774195053L, 19634136210L};

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        zz[0] = xx[0] ^ yy[0];
        zz[1] = xx[1] ^ yy[1];
        zz[2] = xx[2] ^ yy[2];
        zz[3] = xx[3] ^ yy[3];
        zz[4] = xx[4] ^ yy[4];
        zz[5] = xx[5] ^ yy[5];
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1;
        z[1] = x[1];
        z[2] = x[2];
    }

    public static long[] fromBigInteger(BigInteger x) {
        long[] z = Nat192.fromBigInteger64(x);
        reduce29(z, 0);
        return z;
    }

    public static void invert(long[] x, long[] z) {
        if (Nat192.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat192.create64();
        long[] t1 = Nat192.create64();
        square(x, t0);
        squareN(t0, 1, t1);
        multiply(t0, t1, t0);
        squareN(t1, 1, t1);
        multiply(t0, t1, t0);
        squareN(t0, 3, t1);
        multiply(t0, t1, t0);
        squareN(t1, 3, t1);
        multiply(t0, t1, t0);
        squareN(t0, 9, t1);
        multiply(t0, t1, t0);
        squareN(t1, 9, t1);
        multiply(t0, t1, t0);
        squareN(t0, 27, t1);
        multiply(t0, t1, t0);
        squareN(t1, 27, t1);
        multiply(t0, t1, t0);
        squareN(t0, 81, t1);
        multiply(t0, t1, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat192.createExt64();
        implMultiply(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat192.createExt64();
        implMultiply(x, y, tt);
        addExt(zz, tt, zz);
    }

    public static void reduce(long[] xx, long[] z) {
        long x0 = xx[0];
        long x1 = xx[1];
        long x2 = xx[2];
        long x3 = xx[3];
        long x4 = xx[4];
        long x5 = xx[5];
        long x6 = x2 ^ ((((x5 << 29) ^ (x5 << 32)) ^ (x5 << 35)) ^ (x5 << 36));
        long x7 = x3 ^ ((((x5 >>> 35) ^ (x5 >>> 32)) ^ (x5 >>> 29)) ^ (x5 >>> 28));
        long x8 = x1 ^ ((((x4 << 29) ^ (x4 << 32)) ^ (x4 << 35)) ^ (x4 << 36));
        long x9 = x6 ^ ((((x4 >>> 35) ^ (x4 >>> 32)) ^ (x4 >>> 29)) ^ (x4 >>> 28));
        long x10 = x0 ^ ((((x7 << 29) ^ (x7 << 32)) ^ (x7 << 35)) ^ (x7 << 36));
        long x11 = x8 ^ ((((x7 >>> 35) ^ (x7 >>> 32)) ^ (x7 >>> 29)) ^ (x7 >>> 28));
        long t = x9 >>> 35;
        z[0] = (((x10 ^ t) ^ (t << 3)) ^ (t << 6)) ^ (t << 7);
        z[1] = x11;
        z[2] = x9 & M35;
    }

    public static void reduce29(long[] z, int zOff) {
        long z2 = z[zOff + 2];
        long t = z2 >>> 35;
        z[zOff] = z[zOff] ^ (((t ^ (t << 3)) ^ (t << 6)) ^ (t << 7));
        z[zOff + 2] = z2 & M35;
    }

    public static void sqrt(long[] x, long[] z) {
        long[] odd = Nat192.create64();
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = (u0 & 4294967295L) | (u1 << 32);
        odd[0] = (u0 >>> 32) | (u1 & (-4294967296L));
        long u2 = Interleave.unshuffle(x[2]);
        long e1 = u2 & 4294967295L;
        odd[1] = u2 >>> 32;
        multiply(odd, ROOT_Z, z);
        z[0] = z[0] ^ e0;
        z[1] = z[1] ^ e1;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat192.createExt64();
        implSquare(x, tt);
        reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat192.createExt64();
        implSquare(x, tt);
        addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat192.createExt64();
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
        return ((int) (x[0] ^ (x[2] >>> 29))) & 1;
    }

    protected static void implCompactExt(long[] zz) {
        long z0 = zz[0];
        long z1 = zz[1];
        long z2 = zz[2];
        long z3 = zz[3];
        long z4 = zz[4];
        long z5 = zz[5];
        zz[0] = z0 ^ (z1 << 55);
        zz[1] = (z1 >>> 9) ^ (z2 << 46);
        zz[2] = (z2 >>> 18) ^ (z3 << 37);
        zz[3] = (z3 >>> 27) ^ (z4 << 28);
        zz[4] = (z4 >>> 36) ^ (z5 << 19);
        zz[5] = z5 >>> 45;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long f0 = x[0];
        long f1 = x[1];
        long f2 = (f1 >>> 46) ^ (x[2] << 18);
        long f3 = ((f0 >>> 55) ^ (f1 << 9)) & M55;
        long f4 = f0 & M55;
        long g0 = y[0];
        long g1 = y[1];
        long g2 = (g1 >>> 46) ^ (y[2] << 18);
        long g3 = ((g0 >>> 55) ^ (g1 << 9)) & M55;
        long g4 = g0 & M55;
        long[] H = new long[10];
        implMulw(f4, g4, H, 0);
        implMulw(f2, g2, H, 2);
        long t0 = (f4 ^ f3) ^ f2;
        long t1 = (g4 ^ g3) ^ g2;
        implMulw(t0, t1, H, 4);
        long t2 = (f3 << 1) ^ (f2 << 2);
        long t3 = (g3 << 1) ^ (g2 << 2);
        implMulw(f4 ^ t2, g4 ^ t3, H, 6);
        implMulw(t0 ^ t2, t1 ^ t3, H, 8);
        long t4 = H[6] ^ H[8];
        long t5 = H[7] ^ H[9];
        long v0 = (t4 << 1) ^ H[6];
        long v1 = (t4 ^ (t5 << 1)) ^ H[7];
        long u0 = H[0];
        long u1 = (H[1] ^ H[0]) ^ H[4];
        long u2 = H[1] ^ H[5];
        long w0 = ((u0 ^ v0) ^ (H[2] << 4)) ^ (H[2] << 1);
        long w1 = ((u1 ^ v1) ^ (H[3] << 4)) ^ (H[3] << 1);
        long w2 = u2 ^ t5;
        long w3 = w1 ^ (w0 >>> 55);
        long w4 = w0 & M55;
        long w5 = w2 ^ (w3 >>> 55);
        long w6 = w3 & M55;
        long w7 = (w4 >>> 1) ^ ((w6 & 1) << 54);
        long w8 = (w6 >>> 1) ^ ((w5 & 1) << 54);
        long w9 = w5 >>> 1;
        long w10 = w7 ^ (w7 << 1);
        long w11 = w10 ^ (w10 << 2);
        long w12 = w11 ^ (w11 << 4);
        long w13 = w12 ^ (w12 << 8);
        long w14 = w13 ^ (w13 << 16);
        long w15 = (w14 ^ (w14 << 32)) & M55;
        long w16 = w8 ^ (w15 >>> 54);
        long w17 = w16 ^ (w16 << 1);
        long w18 = w17 ^ (w17 << 2);
        long w19 = w18 ^ (w18 << 4);
        long w20 = w19 ^ (w19 << 8);
        long w21 = w20 ^ (w20 << 16);
        long w22 = (w21 ^ (w21 << 32)) & M55;
        long w23 = w9 ^ (w22 >>> 54);
        long w24 = w23 ^ (w23 << 1);
        long w25 = w24 ^ (w24 << 2);
        long w26 = w25 ^ (w25 << 4);
        long w27 = w26 ^ (w26 << 8);
        long w28 = w27 ^ (w27 << 16);
        long w29 = w28 ^ (w28 << 32);
        zz[0] = u0;
        zz[1] = (u1 ^ w15) ^ H[2];
        zz[2] = ((u2 ^ w22) ^ w15) ^ H[3];
        zz[3] = w29 ^ w22;
        zz[4] = w29 ^ H[2];
        zz[5] = H[3];
        implCompactExt(zz);
    }

    protected static void implMulw(long x, long y, long[] z, int zOff) {
        long[] u = new long[8];
        u[1] = y;
        u[2] = u[1] << 1;
        u[3] = u[2] ^ y;
        u[4] = u[2] << 1;
        u[5] = u[4] ^ y;
        u[6] = u[3] << 1;
        u[7] = u[6] ^ y;
        long h = 0;
        long l = u[((int) x) & 3];
        int k = 47;
        do {
            int j = (int) (x >>> k);
            long g = (u[j & 7] ^ (u[(j >>> 3) & 7] << 3)) ^ (u[(j >>> 6) & 7] << 6);
            l ^= g << k;
            h ^= g >>> (-k);
            k -= 9;
        } while (k > 0);
        z[zOff] = l & M55;
        z[zOff + 1] = (l >>> 55) ^ (h << 9);
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x[0], zz, 0);
        Interleave.expand64To128(x[1], zz, 2);
        long x2 = x[2];
        zz[4] = Interleave.expand32to64((int) x2);
        zz[5] = ((long) Interleave.expand8to16((int) (x2 >>> 32))) & 4294967295L;
    }
}
