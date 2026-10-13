package org.bouncycastle.pqc.legacy.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/PolynomialRingGF2.class */
public final class PolynomialRingGF2 {
    private PolynomialRingGF2() {
    }

    public static int add(int i, int i2) {
        return i ^ i2;
    }

    public static long multiply(int i, int i2) {
        long j = 0;
        if (i2 != 0) {
            long j2 = ((long) i2) & 4294967295L;
            while (true) {
                long j3 = j2;
                if (i == 0) {
                    break;
                }
                if (((byte) (i & 1)) == 1) {
                    j ^= j3;
                }
                i >>>= 1;
                j2 = j3 << 1;
            }
        }
        return j;
    }

    public static int modMultiply(int i, int i2, int i3) {
        int i4 = 0;
        int iRemainder = remainder(i, i3);
        int iRemainder2 = remainder(i2, i3);
        if (iRemainder2 != 0) {
            int iDegree = 1 << degree(i3);
            while (iRemainder != 0) {
                if (((byte) (iRemainder & 1)) == 1) {
                    i4 ^= iRemainder2;
                }
                iRemainder >>>= 1;
                iRemainder2 <<= 1;
                if (iRemainder2 >= iDegree) {
                    iRemainder2 ^= i3;
                }
            }
        }
        return i4;
    }

    public static int degree(int i) {
        int i2 = -1;
        while (i != 0) {
            i2++;
            i >>>= 1;
        }
        return i2;
    }

    public static int degree(long j) {
        int i = 0;
        while (j != 0) {
            i++;
            j >>>= 1;
        }
        return i - 1;
    }

    public static int remainder(int i, int i2) {
        int iDegree = i;
        if (i2 == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        while (degree(iDegree) >= degree(i2)) {
            iDegree ^= i2 << (degree(iDegree) - degree(i2));
        }
        return iDegree;
    }

    public static int rest(long j, int i) {
        long jDegree = j;
        if (i == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        long j2 = ((long) i) & 4294967295L;
        while ((jDegree >>> 32) != 0) {
            jDegree ^= j2 << (degree(jDegree) - degree(j2));
        }
        int iDegree = (int) (jDegree & (-1));
        while (true) {
            int i2 = iDegree;
            if (degree(i2) < degree(i)) {
                return i2;
            }
            iDegree = i2 ^ (i << (degree(i2) - degree(i)));
        }
    }

    public static int gcd(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        while (true) {
            int i5 = i4;
            if (i5 == 0) {
                return i3;
            }
            int iRemainder = remainder(i3, i5);
            i3 = i5;
            i4 = iRemainder;
        }
    }

    public static boolean isIrreducible(int i) {
        if (i == 0) {
            return false;
        }
        int iDegree = degree(i) >>> 1;
        int iModMultiply = 2;
        for (int i2 = 0; i2 < iDegree; i2++) {
            iModMultiply = modMultiply(iModMultiply, iModMultiply, i);
            if (gcd(iModMultiply ^ 2, i) != 1) {
                return false;
            }
        }
        return true;
    }

    public static int getIrreduciblePolynomial(int i) {
        if (i < 0) {
            System.err.println("The Degree is negative");
            return 0;
        }
        if (i > 31) {
            System.err.println("The Degree is more then 31");
            return 0;
        }
        if (i == 0) {
            return 1;
        }
        int i2 = 1 << (i + 1);
        for (int i3 = (1 << i) + 1; i3 < i2; i3 += 2) {
            if (isIrreducible(i3)) {
                return i3;
            }
        }
        return 0;
    }
}
