package org.spongycastle.pqc.jcajce.spec;

import java.security.spec.KeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/spec/RainbowPublicKeySpec.class */
public class RainbowPublicKeySpec implements KeySpec {
    private short[][] coeffquadratic;
    private short[][] coeffsingular;
    private short[] coeffscalar;
    private int docLength;

    public RainbowPublicKeySpec(int docLength, short[][] coeffquadratic, short[][] coeffSingular, short[] coeffScalar) {
        this.docLength = docLength;
        this.coeffquadratic = coeffquadratic;
        this.coeffsingular = coeffSingular;
        this.coeffscalar = coeffScalar;
    }

    public int getDocLength() {
        return this.docLength;
    }

    public short[][] getCoeffQuadratic() {
        return this.coeffquadratic;
    }

    public short[][] getCoeffSingular() {
        return this.coeffsingular;
    }

    public short[] getCoeffScalar() {
        return this.coeffscalar;
    }
}
