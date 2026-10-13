package org.bouncycastle.pqc.legacy.crypto.mceliece;

import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/mceliece/McElieceCCA2PublicKeyParameters.class */
public class McElieceCCA2PublicKeyParameters extends McElieceCCA2KeyParameters {
    private int n;
    private int t;
    private GF2Matrix matrixG;

    public McElieceCCA2PublicKeyParameters(int i, int i2, GF2Matrix gF2Matrix, String str) {
        super(false, str);
        this.n = i;
        this.t = i2;
        this.matrixG = new GF2Matrix(gF2Matrix);
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
