package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McEliecePublicKeyParameters.class */
public class McEliecePublicKeyParameters extends McElieceKeyParameters {
    private int n;
    private int t;
    private GF2Matrix g;

    public McEliecePublicKeyParameters(int n, int t, GF2Matrix g) {
        super(false, null);
        this.n = n;
        this.t = t;
        this.g = new GF2Matrix(g);
    }

    public int getN() {
        return this.n;
    }

    public int getT() {
        return this.t;
    }

    public GF2Matrix getG() {
        return this.g;
    }

    public int getK() {
        return this.g.getNumRows();
    }
}
