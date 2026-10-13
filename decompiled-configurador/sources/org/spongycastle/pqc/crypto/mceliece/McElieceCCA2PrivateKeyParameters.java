package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.GoppaCode;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.spongycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceCCA2PrivateKeyParameters.class */
public class McElieceCCA2PrivateKeyParameters extends McElieceCCA2KeyParameters {
    private int n;
    private int k;
    private GF2mField field;
    private PolynomialGF2mSmallM goppaPoly;
    private Permutation p;
    private GF2Matrix h;
    private PolynomialGF2mSmallM[] qInv;

    public McElieceCCA2PrivateKeyParameters(int n, int k, GF2mField field, PolynomialGF2mSmallM gp, Permutation p, String digest) {
        this(n, k, field, gp, GoppaCode.createCanonicalCheckMatrix(field, gp), p, digest);
    }

    public McElieceCCA2PrivateKeyParameters(int n, int k, GF2mField field, PolynomialGF2mSmallM gp, GF2Matrix canonicalCheckMatrix, Permutation p, String digest) {
        super(true, digest);
        this.n = n;
        this.k = k;
        this.field = field;
        this.goppaPoly = gp;
        this.h = canonicalCheckMatrix;
        this.p = p;
        PolynomialRingGF2m ring = new PolynomialRingGF2m(field, gp);
        this.qInv = ring.getSquareRootMatrix();
    }

    public int getN() {
        return this.n;
    }

    public int getK() {
        return this.k;
    }

    public int getT() {
        return this.goppaPoly.getDegree();
    }

    public GF2mField getField() {
        return this.field;
    }

    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.goppaPoly;
    }

    public Permutation getP() {
        return this.p;
    }

    public GF2Matrix getH() {
        return this.h;
    }

    public PolynomialGF2mSmallM[] getQInv() {
        return this.qInv;
    }
}
