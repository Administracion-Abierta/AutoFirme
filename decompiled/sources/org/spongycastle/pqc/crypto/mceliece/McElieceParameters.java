package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.pqc.math.linearalgebra.PolynomialRingGF2;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/mceliece/McElieceParameters.class */
public class McElieceParameters implements CipherParameters {
    public static final int DEFAULT_M = 11;
    public static final int DEFAULT_T = 50;
    private int m;
    private int t;
    private int n;
    private int fieldPoly;
    private Digest digest;

    public McElieceParameters() {
        this(11, 50);
    }

    public McElieceParameters(Digest digest) {
        this(11, 50, digest);
    }

    public McElieceParameters(int keysize) {
        this(keysize, (Digest) null);
    }

    public McElieceParameters(int keysize, Digest digest) {
        if (keysize < 1) {
            throw new IllegalArgumentException("key size must be positive");
        }
        this.m = 0;
        this.n = 1;
        while (this.n < keysize) {
            this.n <<= 1;
            this.m++;
        }
        this.t = this.n >>> 1;
        this.t /= this.m;
        this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(this.m);
        this.digest = digest;
    }

    public McElieceParameters(int m, int t) {
        this(m, t, (Digest) null);
    }

    public McElieceParameters(int m, int t, Digest digest) {
        if (m < 1) {
            throw new IllegalArgumentException("m must be positive");
        }
        if (m > 32) {
            throw new IllegalArgumentException("m is too large");
        }
        this.m = m;
        this.n = 1 << m;
        if (t < 0) {
            throw new IllegalArgumentException("t must be positive");
        }
        if (t > this.n) {
            throw new IllegalArgumentException("t must be less than n = 2^m");
        }
        this.t = t;
        this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(m);
        this.digest = digest;
    }

    public McElieceParameters(int m, int t, int poly) {
        this(m, t, poly, null);
    }

    public McElieceParameters(int m, int t, int poly, Digest digest) {
        this.m = m;
        if (m < 1) {
            throw new IllegalArgumentException("m must be positive");
        }
        if (m > 32) {
            throw new IllegalArgumentException(" m is too large");
        }
        this.n = 1 << m;
        this.t = t;
        if (t < 0) {
            throw new IllegalArgumentException("t must be positive");
        }
        if (t > this.n) {
            throw new IllegalArgumentException("t must be less than n = 2^m");
        }
        if (PolynomialRingGF2.degree(poly) == m && PolynomialRingGF2.isIrreducible(poly)) {
            this.fieldPoly = poly;
            this.digest = digest;
            return;
        }
        throw new IllegalArgumentException("polynomial is not a field polynomial for GF(2^m)");
    }

    public int getM() {
        return this.m;
    }

    public int getN() {
        return this.n;
    }

    public int getT() {
        return this.t;
    }

    public int getFieldPoly() {
        return this.fieldPoly;
    }
}
