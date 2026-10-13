package org.bouncycastle.pqc.crypto.cmce;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/GF.class */
abstract class GF {
    GF() {
    }

    final short gf_iszero(short s) {
        return (short) ((s - 1) >> 31);
    }

    protected abstract void gf_mul_poly(int i, int[] iArr, short[] sArr, short[] sArr2, short[] sArr3, int[] iArr2);

    protected abstract void gf_sqr_poly(int i, int[] iArr, short[] sArr, short[] sArr2, int[] iArr2);

    protected abstract short gf_frac(short s, short s2);

    protected abstract short gf_inv(short s);

    protected abstract short gf_mul(short s, short s2);

    protected abstract int gf_mul_ext(short s, short s2);

    protected abstract short gf_reduce(int i);

    protected abstract short gf_sq(short s);

    protected abstract int gf_sq_ext(short s);
}
