package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.raw.Interleave;
import org.spongycastle.math.raw.Nat256;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecT233Field.class */
public class SecT233Field {
    private static final long M41 = 2199023255551L;
    private static final long M59 = 576460752303423487L;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
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
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1;
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
    }

    public static long[] fromBigInteger(BigInteger x) {
        long[] z = Nat256.fromBigInteger64(x);
        reduce23(z, 0);
        return z;
    }

    public static void invert(long[] x, long[] z) {
        if (Nat256.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat256.create64();
        long[] t1 = Nat256.create64();
        square(x, t0);
        multiply(t0, x, t0);
        square(t0, t0);
        multiply(t0, x, t0);
        squareN(t0, 3, t1);
        multiply(t1, t0, t1);
        square(t1, t1);
        multiply(t1, x, t1);
        squareN(t1, 7, t0);
        multiply(t0, t1, t0);
        squareN(t0, 14, t1);
        multiply(t1, t0, t1);
        square(t1, t1);
        multiply(t1, x, t1);
        squareN(t1, 29, t0);
        multiply(t0, t1, t0);
        squareN(t0, 58, t1);
        multiply(t1, t0, t1);
        squareN(t1, 116, t0);
        multiply(t0, t1, t0);
        square(t0, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat256.createExt64();
        implMultiply(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat256.createExt64();
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
        long x8 = x3 ^ (x7 << 23);
        long x9 = x4 ^ ((x7 >>> 41) ^ (x7 << 33));
        long x10 = x5 ^ (x7 >>> 31);
        long x11 = x2 ^ (x6 << 23);
        long x12 = x8 ^ ((x6 >>> 41) ^ (x6 << 33));
        long x13 = x9 ^ (x6 >>> 31);
        long x14 = x1 ^ (x10 << 23);
        long x15 = x11 ^ ((x10 >>> 41) ^ (x10 << 33));
        long x16 = x12 ^ (x10 >>> 31);
        long x17 = x0 ^ (x13 << 23);
        long x18 = x14 ^ ((x13 >>> 41) ^ (x13 << 33));
        long x19 = x15 ^ (x13 >>> 31);
        long t = x16 >>> 41;
        z[0] = x17 ^ t;
        z[1] = x18 ^ (t << 10);
        z[2] = x19;
        z[3] = x16 & M41;
    }

    public static void reduce23(long[] z, int zOff) {
        long z3 = z[zOff + 3];
        long t = z3 >>> 41;
        z[zOff] = z[zOff] ^ t;
        int i = zOff + 1;
        z[i] = z[i] ^ (t << 10);
        z[zOff + 3] = z3 & M41;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat256.createExt64();
        implSquare(x, tt);
        reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat256.createExt64();
        implSquare(x, tt);
        addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat256.createExt64();
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

    public static void sqrt(long[] x, long[] z) {
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = (u0 & BodyPartID.bodyIdMax) | (u1 << 32);
        long c0 = (u0 >>> 32) | (u1 & (-4294967296L));
        long u2 = Interleave.unshuffle(x[2]);
        long u3 = Interleave.unshuffle(x[3]);
        long e1 = (u2 & BodyPartID.bodyIdMax) | (u3 << 32);
        long c1 = (u2 >>> 32) | (u3 & (-4294967296L));
        long c2 = c1 >>> 27;
        long c3 = c1 ^ ((c0 >>> 27) | (c1 << 37));
        long c4 = c0 ^ (c0 << 37);
        long[] tt = Nat256.createExt64();
        int[] shifts = {32, 117, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256};
        for (int i = 0; i < shifts.length; i++) {
            int w = shifts[i] >>> 6;
            int s = shifts[i] & 63;
            tt[w] = tt[w] ^ (c4 << s);
            int i2 = w + 1;
            tt[i2] = tt[i2] ^ ((c3 << s) | (c4 >>> (-s)));
            int i3 = w + 2;
            tt[i3] = tt[i3] ^ ((c2 << s) | (c3 >>> (-s)));
            int i4 = w + 3;
            tt[i4] = tt[i4] ^ (c2 >>> (-s));
        }
        reduce(tt, z);
        z[0] = z[0] ^ e0;
        z[1] = z[1] ^ e1;
    }

    public static int trace(long[] x) {
        return ((int) (x[0] ^ (x[2] >>> 31))) & 1;
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
        zz[0] = z0 ^ (z1 << 59);
        zz[1] = (z1 >>> 5) ^ (z2 << 54);
        zz[2] = (z2 >>> 10) ^ (z3 << 49);
        zz[3] = (z3 >>> 15) ^ (z4 << 44);
        zz[4] = (z4 >>> 20) ^ (z5 << 39);
        zz[5] = (z5 >>> 25) ^ (z6 << 34);
        zz[6] = (z6 >>> 30) ^ (z7 << 29);
        zz[7] = z7 >>> 35;
    }

    protected static void implExpand(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        z[0] = x0 & M59;
        z[1] = ((x0 >>> 59) ^ (x1 << 5)) & M59;
        z[2] = ((x1 >>> 54) ^ (x2 << 10)) & M59;
        z[3] = (x2 >>> 49) ^ (x3 << 15);
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long[] f = new long[4];
        long[] g = new long[4];
        implExpand(x, f);
        implExpand(y, g);
        implMulwAcc(f[0], g[0], zz, 0);
        implMulwAcc(f[1], g[1], zz, 1);
        implMulwAcc(f[2], g[2], zz, 2);
        implMulwAcc(f[3], g[3], zz, 3);
        for (int i = 5; i > 0; i--) {
            int i2 = i;
            zz[i2] = zz[i2] ^ zz[i - 1];
        }
        implMulwAcc(f[0] ^ f[1], g[0] ^ g[1], zz, 1);
        implMulwAcc(f[2] ^ f[3], g[2] ^ g[3], zz, 3);
        for (int i3 = 7; i3 > 1; i3--) {
            int i4 = i3;
            zz[i4] = zz[i4] ^ zz[i3 - 2];
        }
        long c0 = f[0] ^ f[2];
        long c1 = f[1] ^ f[3];
        long d0 = g[0] ^ g[2];
        long d1 = g[1] ^ g[3];
        implMulwAcc(c0 ^ c1, d0 ^ d1, zz, 3);
        long[] t = new long[3];
        implMulwAcc(c0, d0, t, 0);
        implMulwAcc(c1, d1, t, 1);
        long t0 = t[0];
        long t1 = t[1];
        long t2 = t[2];
        zz[2] = zz[2] ^ t0;
        zz[3] = zz[3] ^ (t0 ^ t1);
        zz[4] = zz[4] ^ (t2 ^ t1);
        zz[5] = zz[5] ^ t2;
        implCompactExt(zz);
    }

    protected static void implMulwAcc(long x, long y, long[] z, int zOff) {
        long[] u = new long[8];
        u[1] = y;
        u[2] = u[1] << 1;
        u[3] = u[2] ^ y;
        u[4] = u[2] << 1;
        u[5] = u[4] ^ y;
        u[6] = u[3] << 1;
        u[7] = u[6] ^ y;
        int j = (int) x;
        long h = 0;
        long l = u[j & 7] ^ (u[(j >>> 3) & 7] << 3);
        int k = 54;
        do {
            int j2 = (int) (x >>> k);
            long g = u[j2 & 7] ^ (u[(j2 >>> 3) & 7] << 3);
            l ^= g << k;
            h ^= g >>> (-k);
            k -= 6;
        } while (k > 0);
        z[zOff] = z[zOff] ^ (l & M59);
        int i = zOff + 1;
        z[i] = z[i] ^ ((l >>> 59) ^ (h << 5));
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x[0], zz, 0);
        Interleave.expand64To128(x[1], zz, 2);
        Interleave.expand64To128(x[2], zz, 4);
        long x3 = x[3];
        zz[6] = Interleave.expand32to64((int) x3);
        zz[7] = ((long) Interleave.expand16to32((int) (x3 >>> 32))) & BodyPartID.bodyIdMax;
    }
}
