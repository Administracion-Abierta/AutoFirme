package org.spongycastle.pqc.crypto.rainbow;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/rainbow/RainbowPublicKeyParameters.class */
public class RainbowPublicKeyParameters extends RainbowKeyParameters {
    private short[][] coeffquadratic;
    private short[][] coeffsingular;
    private short[] coeffscalar;

    public RainbowPublicKeyParameters(int docLength, short[][] coeffQuadratic, short[][] coeffSingular, short[] coeffScalar) {
        super(false, docLength);
        this.coeffquadratic = coeffQuadratic;
        this.coeffsingular = coeffSingular;
        this.coeffscalar = coeffScalar;
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
