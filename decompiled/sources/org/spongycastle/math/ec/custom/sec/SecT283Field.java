package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.raw.Interleave;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat320;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT283Field.class */
public class SecT283Field {
    private static final long M27 = 134217727;
    private static final long M57 = 144115188075855871L;
    private static final long[] ROOT_Z = {878416384462358536L, 3513665537849438403L, -9076969306111048948L, 585610922974906400L, 34087042};

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
        z[4] = x[4] ^ y[4];
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        zz[0] = xx[0] ^ yy[0];
        zz[1] = xx[1] ^ yy[1];
        zz[2] = xx[2] ^ yy[2];
        zz[3] = xx[3] ^ yy[3];
        zz[4] = xx[4] ^ yy[4];
        zz[5] = xx[5] ^ yy[5];
        zz[6] = xx[6] ^ yy[6];
        zz[7] = xx[7] ^ yy[7];
        zz[8] = xx[8] ^ yy[8];
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1;
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
        z[4] = x[4];
    }

    public static long[] fromBigInteger(BigInteger x) {
        long[] z = Nat320.fromBigInteger64(x);
        reduce37(z, 0);
        return z;
    }

    public static void invert(long[] x, long[] z) {
        if (Nat320.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat320.create64();
        long[] t1 = Nat320.create64();
        square(x, t0);
        multiply(t0, x, t0);
        squareN(t0, 2, t1);
        multiply(t1, t0, t1);
        squareN(t1, 4, t0);
        multiply(t0, t1, t0);
        squareN(t0, 8, t1);
        multiply(t1, t0, t1);
        square(t1, t1);
        multiply(t1, x, t1);
        squareN(t1, 17, t0);
        multiply(t0, t1, t0);
        square(t0, t0);
        multiply(t0, x, t0);
        squareN(t0, 35, t1);
        multiply(t1, t0, t1);
        squareN(t1, 70, t0);
        multiply(t0, t1, t0);
        square(t0, t0);
        multiply(t0, x, t0);
        squareN(t0, 141, t1);
        multiply(t1, t0, t1);
        square(t1, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat320.createExt64();
        implMultiply(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat320.createExt64();
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
        long x6 = xx[6];
        long x7 = xx[7];
        long x8 = xx[8];
        long x9 = x3 ^ ((((x8 << 37) ^ (x8 << 42)) ^ (x8 << 44)) ^ (x8 << 49));
        long x10 = x4 ^ ((((x8 >>> 27) ^ (x8 >>> 22)) ^ (x8 >>> 20)) ^ (x8 >>> 15));
        long x11 = x2 ^ ((((x7 << 37) ^ (x7 << 42)) ^ (x7 << 44)) ^ (x7 << 49));
        long x12 = x9 ^ ((((x7 >>> 27) ^ (x7 >>> 22)) ^ (x7 >>> 20)) ^ (x7 >>> 15));
        long x13 = x1 ^ ((((x6 << 37) ^ (x6 << 42)) ^ (x6 << 44)) ^ (x6 << 49));
        long x14 = x11 ^ ((((x6 >>> 27) ^ (x6 >>> 22)) ^ (x6 >>> 20)) ^ (x6 >>> 15));
        long x15 = x0 ^ ((((x5 << 37) ^ (x5 << 42)) ^ (x5 << 44)) ^ (x5 << 49));
        long x16 = x13 ^ ((((x5 >>> 27) ^ (x5 >>> 22)) ^ (x5 >>> 20)) ^ (x5 >>> 15));
        long t = x10 >>> 27;
        z[0] = (((x15 ^ t) ^ (t << 5)) ^ (t << 7)) ^ (t << 12);
        z[1] = x16;
        z[2] = x14;
        z[3] = x12;
        z[4] = x10 & M27;
    }

    public static void reduce37(long[] z, int zOff) {
        long z4 = z[zOff + 4];
        long t = z4 >>> 27;
        z[zOff] = z[zOff] ^ (((t ^ (t << 5)) ^ (t << 7)) ^ (t << 12));
        z[zOff + 4] = z4 & M27;
    }

    public static void sqrt(long[] x, long[] z) {
        long[] odd = Nat320.create64();
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = (u0 & 4294967295L) | (u1 << 32);
        odd[0] = (u0 >>> 32) | (u1 & (-4294967296L));
        long u2 = Interleave.unshuffle(x[2]);
        long u3 = Interleave.unshuffle(x[3]);
        long e1 = (u2 & 4294967295L) | (u3 << 32);
        odd[1] = (u2 >>> 32) | (u3 & (-4294967296L));
        long u4 = Interleave.unshuffle(x[4]);
        long e2 = u4 & 4294967295L;
        odd[2] = u4 >>> 32;
        multiply(odd, ROOT_Z, z);
        z[0] = z[0] ^ e0;
        z[1] = z[1] ^ e1;
        z[2] = z[2] ^ e2;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat.create64(9);
        implSquare(x, tt);
        reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat.create64(9);
        implSquare(x, tt);
        addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat.create64(9);
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
        return ((int) (x[0] ^ (x[4] >>> 15))) & 1;
    }

    protected static void implCompactExt(long[] zz) {
        long z0 = zz[0];
        long z1 = zz[1];
        long z2 = zz[2];
        long z3 = zz[3];
        long z4 = zz[4];
        long z5 = zz[5];
        long z6 = zz[6];
        long z7 = zz[7];
        long z8 = zz[8];
        long z9 = zz[9];
        zz[0] = z0 ^ (z1 << 57);
        zz[1] = (z1 >>> 7) ^ (z2 << 50);
        zz[2] = (z2 >>> 14) ^ (z3 << 43);
        zz[3] = (z3 >>> 21) ^ (z4 << 36);
        zz[4] = (z4 >>> 28) ^ (z5 << 29);
        zz[5] = (z5 >>> 35) ^ (z6 << 22);
        zz[6] = (z6 >>> 42) ^ (z7 << 15);
        zz[7] = (z7 >>> 49) ^ (z8 << 8);
        zz[8] = (z8 >>> 56) ^ (z9 << 1);
        zz[9] = z9 >>> 63;
    }

    protected static void implExpand(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long x4 = x[4];
        z[0] = x0 & M57;
        z[1] = ((x0 >>> 57) ^ (x1 << 7)) & M57;
        z[2] = ((x1 >>> 50) ^ (x2 << 14)) & M57;
        z[3] = ((x2 >>> 43) ^ (x3 << 21)) & M57;
        z[4] = (x3 >>> 36) ^ (x4 << 28);
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long[] a = new long[5];
        long[] b = new long[5];
        implExpand(x, a);
        implExpand(y, b);
        long[] p = new long[26];
        implMulw(a[0], b[0], p, 0);
        implMulw(a[1], b[1], p, 2);
        implMulw(a[2], b[2], p, 4);
        implMulw(a[3], b[3], p, 6);
        implMulw(a[4], b[4], p, 8);
        long u0 = a[0] ^ a[1];
        long v0 = b[0] ^ b[1];
        long u1 = a[0] ^ a[2];
        long v1 = b[0] ^ b[2];
        long u2 = a[2] ^ a[4];
        long v2 = b[2] ^ b[4];
        long u3 = a[3] ^ a[4];
        long v3 = b[3] ^ b[4];
        implMulw(u1 ^ a[3], v1 ^ b[3], p, 18);
        implMulw(u2 ^ a[1], v2 ^ b[1], p, 20);
        long A4 = u0 ^ u3;
        long B4 = v0 ^ v3;
        long A5 = A4 ^ a[2];
        long B5 = B4 ^ b[2];
        implMulw(A4, B4, p, 22);
        implMulw(A5, B5, p, 24);
        implMulw(u0, v0, p, 10);
        implMulw(u1, v1, p, 12);
        implMulw(u2, v2, p, 14);
        implMulw(u3, v3, p, 16);
        zz[0] = p[0];
        zz[9] = p[9];
        long t1 = p[0] ^ p[1];
        long t2 = t1 ^ p[2];
        long t3 = t2 ^ p[10];
        zz[1] = t3;
        long t4 = p[3] ^ p[4];
        long t5 = p[11] ^ p[12];
        long t6 = t4 ^ t5;
        long t7 = t2 ^ t6;
        zz[2] = t7;
        long t8 = t1 ^ t4;
        long t9 = p[5] ^ p[6];
        long t10 = t8 ^ t9;
        long t11 = t10 ^ p[8];
        long t12 = p[13] ^ p[14];
        long t13 = t11 ^ t12;
        long t14 = p[18] ^ p[22];
        long t15 = t14 ^ p[24];
        long t16 = t13 ^ t15;
        zz[3] = t16;
        long t17 = p[7] ^ p[8];
        long t18 = t17 ^ p[9];
        long t19 = t18 ^ p[17];
        zz[8] = t19;
        long t20 = t18 ^ t9;
        long t21 = p[15] ^ p[16];
        long t22 = t20 ^ t21;
        zz[7] = t22;
        long t23 = t22 ^ t3;
        long t24 = p[19] ^ p[20];
        long t25 = p[25] ^ p[24];
        long t26 = p[18] ^ p[23];
        long t27 = t24 ^ t25;
        long t28 = t27 ^ t26;
        long t29 = t28 ^ t23;
        zz[4] = t29;
        long t30 = t7 ^ t19;
        long t31 = t27 ^ t30;
        long t32 = p[21] ^ p[22];
        long t33 = t31 ^ t32;
        zz[5] = t33;
        long t34 = t11 ^ p[0];
        long t35 = t34 ^ p[9];
        long t36 = t35 ^ t12;
        long t37 = t36 ^ p[21];
        long t38 = t37 ^ p[23];
        long t39 = t38 ^ p[25];
        zz[6] = t39;
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
        long l = u[((int) x) & 7];
        int k = 48;
        do {
            int j = (int) (x >>> k);
            long g = (u[j & 7] ^ (u[(j >>> 3) & 7] << 3)) ^ (u[(j >>> 6) & 7] << 6);
            l ^= g << k;
            h ^= g >>> (-k);
            k -= 9;
        } while (k > 0);
        z[zOff] = l & M57;
        z[zOff + 1] = (l >>> 57) ^ ((h ^ (((x & 72198606942111744L) & ((y << 7) >> 63)) >>> 8)) << 7);
    }

    protected static void implSquare(long[] x, long[] zz) {
        for (int i = 0; i < 4; i++) {
            Interleave.expand64To128(x[i], zz, i << 1);
        }
        zz[8] = Interleave.expand32to64((int) x[4]);
    }
}
