package org.spongycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.pqc.math.linearalgebra.PolynomialRingGF2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/spec/McElieceCCA2KeyGenParameterSpec.class */
public class McElieceCCA2KeyGenParameterSpec implements AlgorithmParameterSpec {
    public static final String SHA1 = "SHA-1";
    public static final String SHA224 = "SHA-224";
    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";
    public static final int DEFAULT_M = 11;
    public static final int DEFAULT_T = 50;
    private final int m;
    private final int t;
    private final int n;
    private int fieldPoly;
    private final String digest;

    public McElieceCCA2KeyGenParameterSpec() {
        this(11, 50, "SHA-256");
    }

    public McElieceCCA2KeyGenParameterSpec(int keysize) {
        this(keysize, "SHA-256");
    }

    public McElieceCCA2KeyGenParameterSpec(int keysize, String digest) {
        if (keysize < 1) {
            throw new IllegalArgumentException("key size must be positive");
        }
        int m = 0;
        int n = 1;
        while (n < keysize) {
            n <<= 1;
            m++;
        }
        this.t = (n >>> 1) / m;
        this.m = m;
        this.n = n;
        this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(m);
        this.digest = digest;
    }

    public McElieceCCA2KeyGenParameterSpec(int m, int t) {
        this(m, t, "SHA-256");
    }

    public McElieceCCA2KeyGenParameterSpec(int m, int t, String digest) {
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

    public McElieceCCA2KeyGenParameterSpec(int m, int t, int poly) {
        this(m, t, poly, "SHA-256");
    }

    public McElieceCCA2KeyGenParameterSpec(int m, int t, int poly, String digest) {
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

    public String getDigest() {
        return this.digest;
    }
}
