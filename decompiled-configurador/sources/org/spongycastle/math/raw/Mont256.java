package org.spongycastle.math.raw;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/raw/Mont256.class */
public abstract class Mont256 {
    private static final long M = 4294967295L;

    public static int inverse32(int x) {
        int z = x * (2 - (x * x));
        int z2 = z * (2 - (x * z));
        int z3 = z2 * (2 - (x * z2));
        return z3 * (2 - (x * z3));
    }

    public static void multAdd(int[] x, int[] y, int[] z, int[] m, int mInv32) {
        int z_8 = 0;
        long y_0 = ((long) y[0]) & 4294967295L;
        for (int i = 0; i < 8; i++) {
            long z_0 = ((long) z[0]) & 4294967295L;
            long x_i = ((long) x[i]) & 4294967295L;
            long prod1 = x_i * y_0;
            long carry = (prod1 & 4294967295L) + z_0;
            long t = ((long) (((int) carry) * mInv32)) & 4294967295L;
            long prod2 = t * (((long) m[0]) & 4294967295L);
            long carry2 = ((carry + (prod2 & 4294967295L)) >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            for (int j = 1; j < 8; j++) {
                long prod3 = x_i * (((long) y[j]) & 4294967295L);
                long prod4 = t * (((long) m[j]) & 4294967295L);
                long carry3 = carry2 + (prod3 & 4294967295L) + (prod4 & 4294967295L) + (((long) z[j]) & 4294967295L);
                z[j - 1] = (int) carry3;
                carry2 = (carry3 >>> 32) + (prod3 >>> 32) + (prod4 >>> 32);
            }
            long carry4 = carry2 + (((long) z_8) & 4294967295L);
            z[7] = (int) carry4;
            z_8 = (int) (carry4 >>> 32);
        }
        if (z_8 != 0 || Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void multAddXF(int[] x, int[] y, int[] z, int[] m) {
        int z_8 = 0;
        long y_0 = ((long) y[0]) & 4294967295L;
        for (int i = 0; i < 8; i++) {
            long x_i = ((long) x[i]) & 4294967295L;
            long carry = (x_i * y_0) + (((long) z[0]) & 4294967295L);
            long t = carry & 4294967295L;
            long carry2 = (carry >>> 32) + t;
            for (int j = 1; j < 8; j++) {
                long prod1 = x_i * (((long) y[j]) & 4294967295L);
                long prod2 = t * (((long) m[j]) & 4294967295L);
                long carry3 = carry2 + (prod1 & 4294967295L) + (prod2 & 4294967295L) + (((long) z[j]) & 4294967295L);
                z[j - 1] = (int) carry3;
                carry2 = (carry3 >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            }
            long carry4 = carry2 + (((long) z_8) & 4294967295L);
            z[7] = (int) carry4;
            z_8 = (int) (carry4 >>> 32);
        }
        if (z_8 != 0 || Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void reduce(int[] z, int[] m, int mInv32) {
        for (int i = 0; i < 8; i++) {
            int z_0 = z[0];
            long t = ((long) (z_0 * mInv32)) & 4294967295L;
            long carry = (t * (((long) m[0]) & 4294967295L)) + (((long) z_0) & 4294967295L);
            long carry2 = carry >>> 32;
            for (int j = 1; j < 8; j++) {
                long carry3 = carry2 + (t * (((long) m[j]) & 4294967295L)) + (((long) z[j]) & 4294967295L);
                z[j - 1] = (int) carry3;
                carry2 = carry3 >>> 32;
            }
            z[7] = (int) carry2;
        }
        if (Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void reduceXF(int[] z, int[] m) {
        for (int i = 0; i < 8; i++) {
            int z_0 = z[0];
            long t = ((long) z_0) & 4294967295L;
            long carry = t;
            for (int j = 1; j < 8; j++) {
                long carry2 = carry + (t * (((long) m[j]) & 4294967295L)) + (((long) z[j]) & 4294967295L);
                z[j - 1] = (int) carry2;
                carry = carry2 >>> 32;
            }
            z[7] = (int) carry;
        }
        if (Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }
}
