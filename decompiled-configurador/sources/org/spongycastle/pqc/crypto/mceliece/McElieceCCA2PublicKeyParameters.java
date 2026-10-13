package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceCCA2PublicKeyParameters.class */
public class McElieceCCA2PublicKeyParameters extends McElieceCCA2KeyParameters {
    private int n;
    private int t;
    private GF2Matrix matrixG;

    public McElieceCCA2PublicKeyParameters(int n, int t, GF2Matrix matrix, String digest) {
        super(false, digest);
        this.n = n;
        this.t = t;
        this.matrixG = new GF2Matrix(matrix);
    }

    public int getN() {
        return this.n;
    }

    public int getT() {
        return this.t;
    }

    public GF2Matrix getG() {
        return this.matrixG;
    }

    public int getK() {
        return this.matrixG.getNumRows();
    }
}
