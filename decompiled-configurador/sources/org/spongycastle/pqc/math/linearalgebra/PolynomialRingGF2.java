package org.spongycastle.pqc.math.linearalgebra;

import org.spongycastle.asn1.cmc.BodyPartID;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/PolynomialRingGF2.class */
public final class PolynomialRingGF2 {
    private PolynomialRingGF2() {
    }

    public static int add(int p, int q) {
        return p ^ q;
    }

    public static long multiply(int p, int q) {
        long result = 0;
        if (q != 0) {
            long j = ((long) q) & BodyPartID.bodyIdMax;
            while (true) {
                long q1 = j;
                if (p == 0) {
                    break;
                }
                byte b = (byte) (p & 1);
                if (b == 1) {
                    result ^= q1;
                }
                p >>>= 1;
                j = q1 << 1;
            }
        }
        return result;
    }

    public static int modMultiply(int a, int b, int r) {
        int result = 0;
        int p = remainder(a, r);
        int q = remainder(b, r);
        if (q != 0) {
            int d = 1 << degree(r);
            while (p != 0) {
                byte pMod2 = (byte) (p & 1);
                if (pMod2 == 1) {
                    result ^= q;
                }
                p >>>= 1;
                q <<= 1;
                if (q >= d) {
                    q ^= r;
                }
            }
        }
        return result;
    }

    public static int degree(int p) {
        int result = -1;
        while (p != 0) {
            result++;
            p >>>= 1;
        }
        return result;
    }

    public static int degree(long p) {
        int result = 0;
        while (p != 0) {
            result++;
            p >>>= 1;
        }
        return result - 1;
    }

    public static int remainder(int p, int q) {
        int result = p;
        if (q == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        while (degree(result) >= degree(q)) {
            result ^= q << (degree(result) - degree(q));
        }
        return result;
    }

    public static int rest(long p, int q) {
        long p1 = p;
        if (q == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        long q1 = ((long) q) & BodyPartID.bodyIdMax;
        while ((p1 >>> 32) != 0) {
            p1 ^= q1 << (degree(p1) - degree(q1));
        }
        int iDegree = (int) (p1 & (-1));
        while (true) {
            int result = iDegree;
            if (degree(result) >= degree(q)) {
                iDegree = result ^ (q << (degree(result) - degree(q)));
            } else {
                return result;
            }
        }
    }

    public static int gcd(int p, int q) {
        int a = p;
        int i = q;
        while (true) {
            int b = i;
            if (b != 0) {
                int c = remainder(a, b);
                a = b;
                i = c;
            } else {
                return a;
            }
        }
    }

    public static boolean isIrreducible(int p) {
        if (p == 0) {
            return false;
        }
        int d = degree(p) >>> 1;
        int u = 2;
        for (int i = 0; i < d; i++) {
            u = modMultiply(u, u, p);
            if (gcd(u ^ 2, p) != 1) {
                return false;
            }
        }
        return true;
    }

    public static int getIrreduciblePolynomial(int deg) {
        if (deg < 0) {
            System.err.println("The Degree is negative");
            return 0;
        }
        if (deg > 31) {
            System.err.println("The Degree is more then 31");
            return 0;
        }
        if (deg == 0) {
            return 1;
        }
        int a = 1 << deg;
        int a2 = a + 1;
        int b = 1 << (deg + 1);
        for (int i = a2; i < b; i += 2) {
            if (isIrreducible(i)) {
                return i;
            }
        }
        return 0;
    }
}
