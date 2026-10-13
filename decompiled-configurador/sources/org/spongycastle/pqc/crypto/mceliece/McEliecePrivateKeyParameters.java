package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.GoppaCode;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.spongycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McEliecePrivateKeyParameters.class */
public class McEliecePrivateKeyParameters extends McElieceKeyParameters {
    private String oid;
    private int n;
    private int k;
    private GF2mField field;
    private PolynomialGF2mSmallM goppaPoly;
    private GF2Matrix sInv;
    private Permutation p1;
    private Permutation p2;
    private GF2Matrix h;
    private PolynomialGF2mSmallM[] qInv;

    public McEliecePrivateKeyParameters(int n, int k, GF2mField field, PolynomialGF2mSmallM gp, Permutation p1, Permutation p2, GF2Matrix sInv) {
        super(true, null);
        this.k = k;
        this.n = n;
        this.field = field;
        this.goppaPoly = gp;
        this.sInv = sInv;
        this.p1 = p1;
        this.p2 = p2;
        this.h = GoppaCode.createCanonicalCheckMatrix(field, gp);
        PolynomialRingGF2m ring = new PolynomialRingGF2m(field, gp);
        this.qInv = ring.getSquareRootMatrix();
    }

    public McEliecePrivateKeyParameters(int n, int k, byte[] encField, byte[] encGoppaPoly, byte[] encSInv, byte[] encP1, byte[] encP2, byte[] encH, byte[][] encQInv) {
        super(true, null);
        this.n = n;
        this.k = k;
        this.field = new GF2mField(encField);
        this.goppaPoly = new PolynomialGF2mSmallM(this.field, encGoppaPoly);
        this.sInv = new GF2Matrix(encSInv);
        this.p1 = new Permutation(encP1);
        this.p2 = new Permutation(encP2);
        this.h = new GF2Matrix(encH);
        this.qInv = new PolynomialGF2mSmallM[encQInv.length];
        for (int i = 0; i < encQInv.length; i++) {
            this.qInv[i] = new PolynomialGF2mSmallM(this.field, encQInv[i]);
        }
    }

    public int getN() {
        return this.n;
    }

    public int getK() {
        return this.k;
    }

    public GF2mField getField() {
        return this.field;
    }

    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.goppaPoly;
    }

    public GF2Matrix getSInv() {
        return this.sInv;
    }

    public Permutation getP1() {
        return this.p1;
    }

    public Permutation getP2() {
        return this.p2;
    }

    public GF2Matrix getH() {
        return this.h;
    }

    public PolynomialGF2mSmallM[] getQInv() {
        return this.qInv;
    }
}
