package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat160;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecP160R1Field.class */
public class SecP160R1Field {
    private static final long M = 4294967295L;
    static final int[] P = {Integer.MAX_VALUE, -1, -1, -1, -1};
    static final int[] PExt = {1, 1073741825, 0, 0, 0, -2, -2, -1, -1, -1};
    private static final int[] PExtInv = {-1, -1073741826, -1, -1, -1, 1, 1};
    private static final int P4 = -1;
    private static final int PExt9 = -1;
    private static final int PInv = -2147483647;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat160.add(x, y, z);
        if (c != 0 || (z[4] == -1 && Nat160.gte(z, P))) {
            Nat.addWordTo(5, PInv, z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(10, xx, yy, zz);
        if ((c != 0 || (zz[9] == -1 && Nat.gte(10, zz, PExt))) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(10, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(5, x, z);
        if (c != 0 || (z[4] == -1 && Nat160.gte(z, P))) {
            Nat.addWordTo(5, PInv, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat160.fromBigInteger(x);
        if (z[4] == -1 && Nat160.gte(z, P)) {
            Nat160.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(5, x, 0, z);
        } else {
            int c = Nat160.add(x, P, z);
            Nat.shiftDownBit(5, z, c);
        }
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.mul(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat160.mulAddTo(x, y, zz);
        if ((c != 0 || (zz[9] == -1 && Nat.gte(10, zz, PExt))) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(10, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (Nat160.isZero(x)) {
            Nat160.zero(z);
        } else {
            Nat160.sub(P, x, z);
        }
    }

    public static void reduce(int[] xx, int[] z) {
        long x5 = ((long) xx[5]) & 4294967295L;
        long x6 = ((long) xx[6]) & 4294967295L;
        long x7 = ((long) xx[7]) & 4294967295L;
        long x8 = ((long) xx[8]) & 4294967295L;
        long x9 = ((long) xx[9]) & 4294967295L;
        long c = 0 + (((long) xx[0]) & 4294967295L) + x5 + (x5 << 31);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (((long) xx[1]) & 4294967295L) + x6 + (x6 << 31);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) xx[2]) & 4294967295L) + x7 + (x7 << 31);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) xx[3]) & 4294967295L) + x8 + (x8 << 31);
        z[3] = (int) c4;
        long c5 = (c4 >>> 32) + (((long) xx[4]) & 4294967295L) + x9 + (x9 << 31);
        z[4] = (int) c5;
        reduce32((int) (c5 >>> 32), z);
    }

    public static void reduce32(int x, int[] z) {
        if ((x != 0 && Nat160.mulWordsAdd(PInv, x, z, 0) != 0) || (z[4] == -1 && Nat160.gte(z, P))) {
            Nat.addWordTo(5, PInv, z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                Nat160.square(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat160.sub(x, y, z);
        if (c != 0) {
            Nat.subWordFrom(5, PInv, z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(10, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(10, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(5, x, 0, z);
        if (c != 0 || (z[4] == -1 && Nat160.gte(z, P))) {
            Nat.addWordTo(5, PInv, z);
        }
    }
}
