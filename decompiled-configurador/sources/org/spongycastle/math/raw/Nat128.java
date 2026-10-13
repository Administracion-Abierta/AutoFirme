package org.spongycastle.math.raw;

import java.math.BigInteger;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/raw/Nat128.class */
public abstract class Nat128 {
    private static final long M = 4294967295L;

    public static int add(int[] x, int[] y, int[] z) {
        long c = 0 + (((long) x[0]) & 4294967295L) + (((long) y[0]) & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (((long) x[1]) & 4294967295L) + (((long) y[1]) & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) x[2]) & 4294967295L) + (((long) y[2]) & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) x[3]) & 4294967295L) + (((long) y[3]) & 4294967295L);
        z[3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static int addBothTo(int[] x, int[] y, int[] z) {
        long c = 0 + (((long) x[0]) & 4294967295L) + (((long) y[0]) & 4294967295L) + (((long) z[0]) & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (((long) x[1]) & 4294967295L) + (((long) y[1]) & 4294967295L) + (((long) z[1]) & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) x[2]) & 4294967295L) + (((long) y[2]) & 4294967295L) + (((long) z[2]) & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) x[3]) & 4294967295L) + (((long) y[3]) & 4294967295L) + (((long) z[3]) & 4294967295L);
        z[3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static int addTo(int[] x, int[] z) {
        long c = 0 + (((long) x[0]) & 4294967295L) + (((long) z[0]) & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (((long) x[1]) & 4294967295L) + (((long) z[1]) & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) x[2]) & 4294967295L) + (((long) z[2]) & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) x[3]) & 4294967295L) + (((long) z[3]) & 4294967295L);
        z[3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static int addTo(int[] x, int xOff, int[] z, int zOff, int cIn) {
        long c = (((long) cIn) & 4294967295L) + (((long) x[xOff + 0]) & 4294967295L) + (((long) z[zOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + (((long) x[xOff + 1]) & 4294967295L) + (((long) z[zOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) x[xOff + 2]) & 4294967295L) + (((long) z[zOff + 2]) & 4294967295L);
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) x[xOff + 3]) & 4294967295L) + (((long) z[zOff + 3]) & 4294967295L);
        z[zOff + 3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static int addToEachOther(int[] u, int uOff, int[] v, int vOff) {
        long c = 0 + (((long) u[uOff + 0]) & 4294967295L) + (((long) v[vOff + 0]) & 4294967295L);
        u[uOff + 0] = (int) c;
        v[vOff + 0] = (int) c;
        long c2 = (c >>> 32) + (((long) u[uOff + 1]) & 4294967295L) + (((long) v[vOff + 1]) & 4294967295L);
        u[uOff + 1] = (int) c2;
        v[vOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) u[uOff + 2]) & 4294967295L) + (((long) v[vOff + 2]) & 4294967295L);
        u[uOff + 2] = (int) c3;
        v[vOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (((long) u[uOff + 3]) & 4294967295L) + (((long) v[vOff + 3]) & 4294967295L);
        u[uOff + 3] = (int) c4;
        v[vOff + 3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static void copy(int[] x, int[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
    }

    public static void copy64(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
    }

    public static int[] create() {
        return new int[4];
    }

    public static long[] create64() {
        return new long[2];
    }

    public static int[] createExt() {
        return new int[8];
    }

    public static long[] createExt64() {
        return new long[4];
    }

    public static boolean diff(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        boolean pos = gte(x, xOff, y, yOff);
        if (pos) {
            sub(x, xOff, y, yOff, z, zOff);
        } else {
            sub(y, yOff, x, xOff, z, zOff);
        }
        return pos;
    }

    public static boolean eq(int[] x, int[] y) {
        for (int i = 3; i >= 0; i--) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean eq64(long[] x, long[] y) {
        for (int i = 1; i >= 0; i--) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] fromBigInteger(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 128) {
            throw new IllegalArgumentException();
        }
        int[] z = create();
        int i = 0;
        while (x.signum() != 0) {
            int i2 = i;
            i++;
            z[i2] = x.intValue();
            x = x.shiftRight(32);
        }
        return z;
    }

    public static long[] fromBigInteger64(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 128) {
            throw new IllegalArgumentException();
        }
        long[] z = create64();
        int i = 0;
        while (x.signum() != 0) {
            int i2 = i;
            i++;
            z[i2] = x.longValue();
            x = x.shiftRight(64);
        }
        return z;
    }

    public static int getBit(int[] x, int bit) {
        if (bit == 0) {
            return x[0] & 1;
        }
        int w = bit >> 5;
        if (w < 0 || w >= 4) {
            return 0;
        }
        int b = bit & 31;
        return (x[w] >>> b) & 1;
    }

    public static boolean gte(int[] x, int[] y) {
        for (int i = 3; i >= 0; i--) {
            int x_i = x[i] ^ PKIFailureInfo.systemUnavail;
            int y_i = y[i] ^ PKIFailureInfo.systemUnavail;
            if (x_i < y_i) {
                return false;
            }
            if (x_i > y_i) {
                return true;
            }
        }
        return true;
    }

    public static boolean gte(int[] x, int xOff, int[] y, int yOff) {
        for (int i = 3; i >= 0; i--) {
            int x_i = x[xOff + i] ^ PKIFailureInfo.systemUnavail;
            int y_i = y[yOff + i] ^ PKIFailureInfo.systemUnavail;
            if (x_i < y_i) {
                return false;
            }
            if (x_i > y_i) {
                return true;
            }
        }
        return true;
    }

    public static boolean isOne(int[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < 4; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOne64(long[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < 2; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero(int[] x) {
        for (int i = 0; i < 4; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero64(long[] x) {
        for (int i = 0; i < 2; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static void mul(int[] x, int[] y, int[] zz) {
        long y_0 = ((long) y[0]) & 4294967295L;
        long y_1 = ((long) y[1]) & 4294967295L;
        long y_2 = ((long) y[2]) & 4294967295L;
        long y_3 = ((long) y[3]) & 4294967295L;
        long x_0 = ((long) x[0]) & 4294967295L;
        long c = 0 + (x_0 * y_0);
        zz[0] = (int) c;
        long c2 = (c >>> 32) + (x_0 * y_1);
        zz[1] = (int) c2;
        long c3 = (c2 >>> 32) + (x_0 * y_2);
        zz[2] = (int) c3;
        long c4 = (c3 >>> 32) + (x_0 * y_3);
        zz[3] = (int) c4;
        zz[4] = (int) (c4 >>> 32);
        for (int i = 1; i < 4; i++) {
            long x_i = ((long) x[i]) & 4294967295L;
            long c5 = 0 + (x_i * y_0) + (((long) zz[i + 0]) & 4294967295L);
            zz[i + 0] = (int) c5;
            long c6 = (c5 >>> 32) + (x_i * y_1) + (((long) zz[i + 1]) & 4294967295L);
            zz[i + 1] = (int) c6;
            long c7 = (c6 >>> 32) + (x_i * y_2) + (((long) zz[i + 2]) & 4294967295L);
            zz[i + 2] = (int) c7;
            long c8 = (c7 >>> 32) + (x_i * y_3) + (((long) zz[i + 3]) & 4294967295L);
            zz[i + 3] = (int) c8;
            zz[i + 4] = (int) (c8 >>> 32);
        }
    }

    public static void mul(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = ((long) y[yOff + 0]) & 4294967295L;
        long y_1 = ((long) y[yOff + 1]) & 4294967295L;
        long y_2 = ((long) y[yOff + 2]) & 4294967295L;
        long y_3 = ((long) y[yOff + 3]) & 4294967295L;
        long x_0 = ((long) x[xOff + 0]) & 4294967295L;
        long c = 0 + (x_0 * y_0);
        zz[zzOff + 0] = (int) c;
        long c2 = (c >>> 32) + (x_0 * y_1);
        zz[zzOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (x_0 * y_2);
        zz[zzOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (x_0 * y_3);
        zz[zzOff + 3] = (int) c4;
        zz[zzOff + 4] = (int) (c4 >>> 32);
        for (int i = 1; i < 4; i++) {
            zzOff++;
            long x_i = ((long) x[xOff + i]) & 4294967295L;
            long c5 = 0 + (x_i * y_0) + (((long) zz[zzOff + 0]) & 4294967295L);
            zz[zzOff + 0] = (int) c5;
            long c6 = (c5 >>> 32) + (x_i * y_1) + (((long) zz[zzOff + 1]) & 4294967295L);
            zz[zzOff + 1] = (int) c6;
            long c7 = (c6 >>> 32) + (x_i * y_2) + (((long) zz[zzOff + 2]) & 4294967295L);
            zz[zzOff + 2] = (int) c7;
            long c8 = (c7 >>> 32) + (x_i * y_3) + (((long) zz[zzOff + 3]) & 4294967295L);
            zz[zzOff + 3] = (int) c8;
            zz[zzOff + 4] = (int) (c8 >>> 32);
        }
    }

    public static int mulAddTo(int[] x, int[] y, int[] zz) {
        long y_0 = ((long) y[0]) & 4294967295L;
        long y_1 = ((long) y[1]) & 4294967295L;
        long y_2 = ((long) y[2]) & 4294967295L;
        long y_3 = ((long) y[3]) & 4294967295L;
        long zc = 0;
        for (int i = 0; i < 4; i++) {
            long x_i = ((long) x[i]) & 4294967295L;
            long c = 0 + (x_i * y_0) + (((long) zz[i + 0]) & 4294967295L);
            zz[i + 0] = (int) c;
            long c2 = (c >>> 32) + (x_i * y_1) + (((long) zz[i + 1]) & 4294967295L);
            zz[i + 1] = (int) c2;
            long c3 = (c2 >>> 32) + (x_i * y_2) + (((long) zz[i + 2]) & 4294967295L);
            zz[i + 2] = (int) c3;
            long c4 = (c3 >>> 32) + (x_i * y_3) + (((long) zz[i + 3]) & 4294967295L);
            zz[i + 3] = (int) c4;
            long c5 = (c4 >>> 32) + zc + (((long) zz[i + 4]) & 4294967295L);
            zz[i + 4] = (int) c5;
            zc = c5 >>> 32;
        }
        return (int) zc;
    }

    public static int mulAddTo(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = ((long) y[yOff + 0]) & 4294967295L;
        long y_1 = ((long) y[yOff + 1]) & 4294967295L;
        long y_2 = ((long) y[yOff + 2]) & 4294967295L;
        long y_3 = ((long) y[yOff + 3]) & 4294967295L;
        long zc = 0;
        for (int i = 0; i < 4; i++) {
            long x_i = ((long) x[xOff + i]) & 4294967295L;
            long c = 0 + (x_i * y_0) + (((long) zz[zzOff + 0]) & 4294967295L);
            zz[zzOff + 0] = (int) c;
            long c2 = (c >>> 32) + (x_i * y_1) + (((long) zz[zzOff + 1]) & 4294967295L);
            zz[zzOff + 1] = (int) c2;
            long c3 = (c2 >>> 32) + (x_i * y_2) + (((long) zz[zzOff + 2]) & 4294967295L);
            zz[zzOff + 2] = (int) c3;
            long c4 = (c3 >>> 32) + (x_i * y_3) + (((long) zz[zzOff + 3]) & 4294967295L);
            zz[zzOff + 3] = (int) c4;
            long c5 = (c4 >>> 32) + zc + (((long) zz[zzOff + 4]) & 4294967295L);
            zz[zzOff + 4] = (int) c5;
            zc = c5 >>> 32;
            zzOff++;
        }
        return (int) zc;
    }

    public static long mul33Add(int w, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long wVal = ((long) w) & 4294967295L;
        long x0 = ((long) x[xOff + 0]) & 4294967295L;
        long c = 0 + (wVal * x0) + (((long) y[yOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = c >>> 32;
        long x1 = ((long) x[xOff + 1]) & 4294967295L;
        long c3 = c2 + (wVal * x1) + x0 + (((long) y[yOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c3;
        long c4 = c3 >>> 32;
        long x2 = ((long) x[xOff + 2]) & 4294967295L;
        long c5 = c4 + (wVal * x2) + x1 + (((long) y[yOff + 2]) & 4294967295L);
        z[zOff + 2] = (int) c5;
        long c6 = c5 >>> 32;
        long x3 = ((long) x[xOff + 3]) & 4294967295L;
        long c7 = c6 + (wVal * x3) + x2 + (((long) y[yOff + 3]) & 4294967295L);
        z[zOff + 3] = (int) c7;
        return (c7 >>> 32) + x3;
    }

    public static int mulWordAddExt(int x, int[] yy, int yyOff, int[] zz, int zzOff) {
        long xVal = ((long) x) & 4294967295L;
        long c = 0 + (xVal * (((long) yy[yyOff + 0]) & 4294967295L)) + (((long) zz[zzOff + 0]) & 4294967295L);
        zz[zzOff + 0] = (int) c;
        long c2 = (c >>> 32) + (xVal * (((long) yy[yyOff + 1]) & 4294967295L)) + (((long) zz[zzOff + 1]) & 4294967295L);
        zz[zzOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (xVal * (((long) yy[yyOff + 2]) & 4294967295L)) + (((long) zz[zzOff + 2]) & 4294967295L);
        zz[zzOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (xVal * (((long) yy[yyOff + 3]) & 4294967295L)) + (((long) zz[zzOff + 3]) & 4294967295L);
        zz[zzOff + 3] = (int) c4;
        return (int) (c4 >>> 32);
    }

    public static int mul33DWordAdd(int x, long y, int[] z, int zOff) {
        long xVal = ((long) x) & 4294967295L;
        long y00 = y & 4294967295L;
        long c = 0 + (xVal * y00) + (((long) z[zOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = c >>> 32;
        long y01 = y >>> 32;
        long c3 = c2 + (xVal * y01) + y00 + (((long) z[zOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c3;
        long c4 = (c3 >>> 32) + y01 + (((long) z[zOff + 2]) & 4294967295L);
        z[zOff + 2] = (int) c4;
        long c5 = (c4 >>> 32) + (((long) z[zOff + 3]) & 4294967295L);
        z[zOff + 3] = (int) c5;
        return (int) (c5 >>> 32);
    }

    public static int mul33WordAdd(int x, int y, int[] z, int zOff) {
        long xVal = ((long) x) & 4294967295L;
        long yVal = ((long) y) & 4294967295L;
        long c = 0 + (yVal * xVal) + (((long) z[zOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + yVal + (((long) z[zOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) z[zOff + 2]) & 4294967295L);
        z[zOff + 2] = (int) c3;
        if ((c3 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(4, z, zOff, 3);
    }

    public static int mulWordDwordAdd(int x, long y, int[] z, int zOff) {
        long xVal = ((long) x) & 4294967295L;
        long c = 0 + (xVal * (y & 4294967295L)) + (((long) z[zOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + (xVal * (y >>> 32)) + (((long) z[zOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (((long) z[zOff + 2]) & 4294967295L);
        z[zOff + 2] = (int) c3;
        if ((c3 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(4, z, zOff, 3);
    }

    public static int mulWordsAdd(int x, int y, int[] z, int zOff) {
        long xVal = ((long) x) & 4294967295L;
        long yVal = ((long) y) & 4294967295L;
        long c = 0 + (yVal * xVal) + (((long) z[zOff + 0]) & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + (((long) z[zOff + 1]) & 4294967295L);
        z[zOff + 1] = (int) c2;
        if ((c2 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(4, z, zOff, 2);
    }

    public static int mulWord(int x, int[] y, int[] z, int zOff) {
        long c = 0;
        long xVal = ((long) x) & 4294967295L;
        int i = 0;
        do {
            long c2 = c + (xVal * (((long) y[i]) & 4294967295L));
            z[zOff + i] = (int) c2;
            c = c2 >>> 32;
            i++;
        } while (i < 4);
        return (int) c;
    }

    public static void square(int[] x, int[] zz) {
        long x_0 = ((long) x[0]) & 4294967295L;
        int c = 0;
        int i = 3;
        int j = 8;
        do {
            int i2 = i;
            i--;
            long xVal = ((long) x[i2]) & 4294967295L;
            long p = xVal * xVal;
            int j2 = j - 1;
            zz[j2] = (c << 31) | ((int) (p >>> 33));
            j = j2 - 1;
            zz[j] = (int) (p >>> 1);
            c = (int) p;
        } while (i > 0);
        long p2 = x_0 * x_0;
        long zz_1 = (((long) (c << 31)) & 4294967295L) | (p2 >>> 33);
        zz[0] = (int) p2;
        int c2 = ((int) (p2 >>> 32)) & 1;
        long x_1 = ((long) x[1]) & 4294967295L;
        long zz_2 = ((long) zz[2]) & 4294967295L;
        long zz_3 = zz_1 + (x_1 * x_0);
        int w = (int) zz_3;
        zz[1] = (w << 1) | c2;
        int c3 = w >>> 31;
        long zz_4 = zz_2 + (zz_3 >>> 32);
        long x_2 = ((long) x[2]) & 4294967295L;
        long zz_5 = ((long) zz[3]) & 4294967295L;
        long zz_6 = ((long) zz[4]) & 4294967295L;
        long zz_7 = zz_4 + (x_2 * x_0);
        int w2 = (int) zz_7;
        zz[2] = (w2 << 1) | c3;
        int c4 = w2 >>> 31;
        long zz_8 = zz_5 + (zz_7 >>> 32) + (x_2 * x_1);
        long zz_9 = zz_6 + (zz_8 >>> 32);
        long zz_10 = zz_8 & 4294967295L;
        long x_3 = ((long) x[3]) & 4294967295L;
        long zz_11 = (((long) zz[5]) & 4294967295L) + (zz_9 >>> 32);
        long zz_12 = zz_9 & 4294967295L;
        long zz_13 = (((long) zz[6]) & 4294967295L) + (zz_11 >>> 32);
        long zz_14 = zz_11 & 4294967295L;
        long zz_15 = zz_10 + (x_3 * x_0);
        int w3 = (int) zz_15;
        zz[3] = (w3 << 1) | c4;
        int c5 = w3 >>> 31;
        long zz_16 = zz_12 + (zz_15 >>> 32) + (x_3 * x_1);
        long zz_17 = zz_14 + (zz_16 >>> 32) + (x_3 * x_2);
        long zz_18 = zz_13 + (zz_17 >>> 32);
        long zz_19 = zz_17 & 4294967295L;
        int w4 = (int) zz_16;
        zz[4] = (w4 << 1) | c5;
        int c6 = w4 >>> 31;
        int w5 = (int) zz_19;
        zz[5] = (w5 << 1) | c6;
        int c7 = w5 >>> 31;
        int w6 = (int) zz_18;
        zz[6] = (w6 << 1) | c7;
        int c8 = w6 >>> 31;
        zz[7] = ((zz[7] + ((int) (zz_18 >>> 32))) << 1) | c8;
    }

    public static void square(int[] x, int xOff, int[] zz, int zzOff) {
        long x_0 = ((long) x[xOff + 0]) & 4294967295L;
        int c = 0;
        int i = 3;
        int j = 8;
        do {
            int i2 = i;
            i--;
            long xVal = ((long) x[xOff + i2]) & 4294967295L;
            long p = xVal * xVal;
            int j2 = j - 1;
            zz[zzOff + j2] = (c << 31) | ((int) (p >>> 33));
            j = j2 - 1;
            zz[zzOff + j] = (int) (p >>> 1);
            c = (int) p;
        } while (i > 0);
        long p2 = x_0 * x_0;
        long zz_1 = (((long) (c << 31)) & 4294967295L) | (p2 >>> 33);
        zz[zzOff + 0] = (int) p2;
        int c2 = ((int) (p2 >>> 32)) & 1;
        long x_1 = ((long) x[xOff + 1]) & 4294967295L;
        long zz_2 = ((long) zz[zzOff + 2]) & 4294967295L;
        long zz_3 = zz_1 + (x_1 * x_0);
        int w = (int) zz_3;
        zz[zzOff + 1] = (w << 1) | c2;
        int c3 = w >>> 31;
        long zz_4 = zz_2 + (zz_3 >>> 32);
        long x_2 = ((long) x[xOff + 2]) & 4294967295L;
        long zz_5 = ((long) zz[zzOff + 3]) & 4294967295L;
        long zz_6 = ((long) zz[zzOff + 4]) & 4294967295L;
        long zz_7 = zz_4 + (x_2 * x_0);
        int w2 = (int) zz_7;
        zz[zzOff + 2] = (w2 << 1) | c3;
        int c4 = w2 >>> 31;
        long zz_8 = zz_5 + (zz_7 >>> 32) + (x_2 * x_1);
        long zz_9 = zz_6 + (zz_8 >>> 32);
        long zz_10 = zz_8 & 4294967295L;
        long x_3 = ((long) x[xOff + 3]) & 4294967295L;
        long zz_11 = (((long) zz[zzOff + 5]) & 4294967295L) + (zz_9 >>> 32);
        long zz_12 = zz_9 & 4294967295L;
        long zz_13 = (((long) zz[zzOff + 6]) & 4294967295L) + (zz_11 >>> 32);
        long zz_14 = zz_11 & 4294967295L;
        long zz_15 = zz_10 + (x_3 * x_0);
        int w3 = (int) zz_15;
        zz[zzOff + 3] = (w3 << 1) | c4;
        int c5 = w3 >>> 31;
        long zz_16 = zz_12 + (zz_15 >>> 32) + (x_3 * x_1);
        long zz_17 = zz_14 + (zz_16 >>> 32) + (x_3 * x_2);
        long zz_18 = zz_13 + (zz_17 >>> 32);
        int w4 = (int) zz_16;
        zz[zzOff + 4] = (w4 << 1) | c5;
        int c6 = w4 >>> 31;
        int w5 = (int) zz_17;
        zz[zzOff + 5] = (w5 << 1) | c6;
        int c7 = w5 >>> 31;
        int w6 = (int) zz_18;
        zz[zzOff + 6] = (w6 << 1) | c7;
        int c8 = w6 >>> 31;
        zz[zzOff + 7] = ((zz[zzOff + 7] + ((int) (zz_18 >>> 32))) << 1) | c8;
    }

    public static int sub(int[] x, int[] y, int[] z) {
        long c = 0 + ((((long) x[0]) & 4294967295L) - (((long) y[0]) & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + ((((long) x[1]) & 4294967295L) - (((long) y[1]) & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + ((((long) x[2]) & 4294967295L) - (((long) y[2]) & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + ((((long) x[3]) & 4294967295L) - (((long) y[3]) & 4294967295L));
        z[3] = (int) c4;
        return (int) (c4 >> 32);
    }

    public static int sub(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0 + ((((long) x[xOff + 0]) & 4294967295L) - (((long) y[yOff + 0]) & 4294967295L));
        z[zOff + 0] = (int) c;
        long c2 = (c >> 32) + ((((long) x[xOff + 1]) & 4294967295L) - (((long) y[yOff + 1]) & 4294967295L));
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >> 32) + ((((long) x[xOff + 2]) & 4294967295L) - (((long) y[yOff + 2]) & 4294967295L));
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >> 32) + ((((long) x[xOff + 3]) & 4294967295L) - (((long) y[yOff + 3]) & 4294967295L));
        z[zOff + 3] = (int) c4;
        return (int) (c4 >> 32);
    }

    public static int subBothFrom(int[] x, int[] y, int[] z) {
        long c = 0 + (((((long) z[0]) & 4294967295L) - (((long) x[0]) & 4294967295L)) - (((long) y[0]) & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + (((((long) z[1]) & 4294967295L) - (((long) x[1]) & 4294967295L)) - (((long) y[1]) & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + (((((long) z[2]) & 4294967295L) - (((long) x[2]) & 4294967295L)) - (((long) y[2]) & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + (((((long) z[3]) & 4294967295L) - (((long) x[3]) & 4294967295L)) - (((long) y[3]) & 4294967295L));
        z[3] = (int) c4;
        return (int) (c4 >> 32);
    }

    public static int subFrom(int[] x, int[] z) {
        long c = 0 + ((((long) z[0]) & 4294967295L) - (((long) x[0]) & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + ((((long) z[1]) & 4294967295L) - (((long) x[1]) & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + ((((long) z[2]) & 4294967295L) - (((long) x[2]) & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + ((((long) z[3]) & 4294967295L) - (((long) x[3]) & 4294967295L));
        z[3] = (int) c4;
        return (int) (c4 >> 32);
    }

    public static int subFrom(int[] x, int xOff, int[] z, int zOff) {
        long c = 0 + ((((long) z[zOff + 0]) & 4294967295L) - (((long) x[xOff + 0]) & 4294967295L));
        z[zOff + 0] = (int) c;
        long c2 = (c >> 32) + ((((long) z[zOff + 1]) & 4294967295L) - (((long) x[xOff + 1]) & 4294967295L));
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >> 32) + ((((long) z[zOff + 2]) & 4294967295L) - (((long) x[xOff + 2]) & 4294967295L));
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >> 32) + ((((long) z[zOff + 3]) & 4294967295L) - (((long) x[xOff + 3]) & 4294967295L));
        z[zOff + 3] = (int) c4;
        return (int) (c4 >> 32);
    }

    public static BigInteger toBigInteger(int[] x) {
        byte[] bs = new byte[16];
        for (int i = 0; i < 4; i++) {
            int x_i = x[i];
            if (x_i != 0) {
                Pack.intToBigEndian(x_i, bs, (3 - i) << 2);
            }
        }
        return new BigInteger(1, bs);
    }

    public static BigInteger toBigInteger64(long[] x) {
        byte[] bs = new byte[16];
        for (int i = 0; i < 2; i++) {
            long x_i = x[i];
            if (x_i != 0) {
                Pack.longToBigEndian(x_i, bs, (1 - i) << 3);
            }
        }
        return new BigInteger(1, bs);
    }

    public static void zero(int[] z) {
        z[0] = 0;
        z[1] = 0;
        z[2] = 0;
        z[3] = 0;
    }
}
