package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat128;
import org.spongycastle.math.raw.Nat256;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecP128R1Field.class */
public class SecP128R1Field {
    private static final long M = 4294967295L;
    static final int[] P = {-1, -1, -1, -3};
    static final int[] PExt = {1, 0, 0, 4, -2, -1, 3, -4};
    private static final int[] PExtInv = {-1, -1, -1, -5, 1, 0, -4, 3};
    private static final int P3s1 = 2147483646;
    private static final int PExt7s1 = 2147483646;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat128.add(x, y, z);
        if (c != 0 || ((z[3] >>> 1) >= 2147483646 && Nat128.gte(z, P))) {
            addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat256.add(xx, yy, zz);
        if (c != 0 || ((zz[7] >>> 1) >= 2147483646 && Nat256.gte(zz, PExt))) {
            Nat.addTo(PExtInv.length, PExtInv, zz);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(4, x, z);
        if (c != 0 || ((z[3] >>> 1) >= 2147483646 && Nat128.gte(z, P))) {
            addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat128.fromBigInteger(x);
        if ((z[3] >>> 1) >= 2147483646 && Nat128.gte(z, P)) {
            Nat128.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(4, x, 0, z);
        } else {
            int c = Nat128.add(x, P, z);
            Nat.shiftDownBit(4, z, c);
        }
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.mul(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat128.mulAddTo(x, y, zz);
        if (c != 0 || ((zz[7] >>> 1) >= 2147483646 && Nat256.gte(zz, PExt))) {
            Nat.addTo(PExtInv.length, PExtInv, zz);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (Nat128.isZero(x)) {
            Nat128.zero(z);
        } else {
            Nat128.sub(P, x, z);
        }
    }

    public static void reduce(int[] xx, int[] z) {
        long x0 = ((long) xx[0]) & 4294967295L;
        long x1 = ((long) xx[1]) & 4294967295L;
        long x2 = ((long) xx[2]) & 4294967295L;
        long x3 = ((long) xx[3]) & 4294967295L;
        long x4 = ((long) xx[4]) & 4294967295L;
        long x5 = ((long) xx[5]) & 4294967295L;
        long x6 = ((long) xx[6]) & 4294967295L;
        long x7 = ((long) xx[7]) & 4294967295L;
        long x8 = x3 + x7;
        long x9 = x6 + (x7 << 1);
        long x10 = x2 + x9;
        long x11 = x5 + (x9 << 1);
        long x12 = x1 + x11;
        long x13 = x4 + (x11 << 1);
        long x14 = x0 + x13;
        long x15 = x8 + (x13 << 1);
        z[0] = (int) x14;
        long x16 = x12 + (x14 >>> 32);
        z[1] = (int) x16;
        long x17 = x10 + (x16 >>> 32);
        z[2] = (int) x17;
        long x18 = x15 + (x17 >>> 32);
        z[3] = (int) x18;
        reduce32((int) (x18 >>> 32), z);
    }

    public static void reduce32(int x, int[] z) {
        while (x != 0) {
            long x4 = ((long) x) & 4294967295L;
            long c = (((long) z[0]) & 4294967295L) + x4;
            z[0] = (int) c;
            long c2 = c >> 32;
            if (c2 != 0) {
                long c3 = c2 + (((long) z[1]) & 4294967295L);
                z[1] = (int) c3;
                long c4 = (c3 >> 32) + (((long) z[2]) & 4294967295L);
                z[2] = (int) c4;
                c2 = c4 >> 32;
            }
            long c5 = c2 + (((long) z[3]) & 4294967295L) + (x4 << 1);
            z[3] = (int) c5;
            x = (int) (c5 >> 32);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.square(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.square(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                Nat128.square(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat128.sub(x, y, z);
        if (c != 0) {
            subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(10, xx, yy, zz);
        if (c != 0) {
            Nat.subFrom(PExtInv.length, PExtInv, zz);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(4, x, 0, z);
        if (c != 0 || ((z[3] >>> 1) >= 2147483646 && Nat128.gte(z, P))) {
            addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = (((long) z[0]) & 4294967295L) + 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (((long) z[1]) & 4294967295L);
            z[1] = (int) c3;
            long c4 = (c3 >> 32) + (((long) z[2]) & 4294967295L);
            z[2] = (int) c4;
            c2 = c4 >> 32;
        }
        z[3] = (int) (c2 + (((long) z[3]) & 4294967295L) + 2);
    }

    private static void subPInvFrom(int[] z) {
        long c = (((long) z[0]) & 4294967295L) - 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (((long) z[1]) & 4294967295L);
            z[1] = (int) c3;
            long c4 = (c3 >> 32) + (((long) z[2]) & 4294967295L);
            z[2] = (int) c4;
            c2 = c4 >> 32;
        }
        z[3] = (int) (c2 + ((((long) z[3]) & 4294967295L) - 2));
    }
}
