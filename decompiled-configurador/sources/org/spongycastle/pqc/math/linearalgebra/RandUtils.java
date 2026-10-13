package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/RandUtils.class */
public class RandUtils {
    static int nextInt(SecureRandom rand, int n) {
        int bits;
        int value;
        if ((n & (-n)) == n) {
            return (int) ((((long) n) * ((long) (rand.nextInt() >>> 1))) >> 31);
        }
        do {
            bits = rand.nextInt() >>> 1;
            value = bits % n;
        } while ((bits - value) + (n - 1) < 0);
        return value;
    }
}
