package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/Permutation.class */
public class Permutation {
    private int[] perm;

    public Permutation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            this.perm[i] = i;
        }
    }

    public Permutation(int[] perm) {
        if (!isPermutation(perm)) {
            throw new IllegalArgumentException("array is not a permutation vector");
        }
        this.perm = IntUtils.clone(perm);
    }

    public Permutation(byte[] enc) {
        if (enc.length <= 4) {
            throw new IllegalArgumentException("invalid encoding");
        }
        int n = LittleEndianConversions.OS2IP(enc, 0);
        int size = IntegerFunctions.ceilLog256(n - 1);
        if (enc.length != 4 + (n * size)) {
            throw new IllegalArgumentException("invalid encoding");
        }
        this.perm = new int[n];
        for (int i = 0; i < n; i++) {
            this.perm[i] = LittleEndianConversions.OS2IP(enc, 4 + (i * size), size);
        }
        if (!isPermutation(this.perm)) {
            throw new IllegalArgumentException("invalid encoding");
        }
    }

    public Permutation(int n, SecureRandom sr) {
        if (n <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[n];
        int[] help = new int[n];
        for (int i = 0; i < n; i++) {
            help[i] = i;
        }
        int k = n;
        for (int j = 0; j < n; j++) {
            int i2 = RandUtils.nextInt(sr, k);
            k--;
            this.perm[j] = help[i2];
            help[i2] = help[k];
        }
    }

    public byte[] getEncoded() {
        int n = this.perm.length;
        int size = IntegerFunctions.ceilLog256(n - 1);
        byte[] result = new byte[4 + (n * size)];
        LittleEndianConversions.I2OSP(n, result, 0);
        for (int i = 0; i < n; i++) {
            LittleEndianConversions.I2OSP(this.perm[i], result, 4 + (i * size), size);
        }
        return result;
    }

    public int[] getVector() {
        return IntUtils.clone(this.perm);
    }

    public Permutation computeInverse() {
        Permutation result = new Permutation(this.perm.length);
        for (int i = this.perm.length - 1; i >= 0; i--) {
            result.perm[this.perm[i]] = i;
        }
        return result;
    }

    public Permutation rightMultiply(Permutation p) {
        if (p.perm.length != this.perm.length) {
            throw new IllegalArgumentException("length mismatch");
        }
        Permutation result = new Permutation(this.perm.length);
        for (int i = this.perm.length - 1; i >= 0; i--) {
            result.perm[i] = this.perm[p.perm[i]];
        }
        return result;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Permutation)) {
            return false;
        }
        Permutation otherPerm = (Permutation) other;
        return IntUtils.equals(this.perm, otherPerm.perm);
    }

    public String toString() {
        String result = "[" + this.perm[0];
        for (int i = 1; i < this.perm.length; i++) {
            result = result + ", " + this.perm[i];
        }
        return result + "]";
    }

    public int hashCode() {
        return this.perm.hashCode();
    }

    private boolean isPermutation(int[] perm) {
        int n = perm.length;
        boolean[] onlyOnce = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (perm[i] < 0 || perm[i] >= n || onlyOnce[perm[i]]) {
                return false;
            }
            onlyOnce[perm[i]] = true;
        }
        return true;
    }
}
