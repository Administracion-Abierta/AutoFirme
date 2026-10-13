package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/NaccacheSternKeyParameters.class */
public class NaccacheSternKeyParameters extends AsymmetricKeyParameter {
    private BigInteger g;
    private BigInteger n;
    int lowerSigmaBound;

    public NaccacheSternKeyParameters(boolean privateKey, BigInteger g, BigInteger n, int lowerSigmaBound) {
        super(privateKey);
        this.g = g;
        this.n = n;
        this.lowerSigmaBound = lowerSigmaBound;
    }

    public BigInteger getG() {
        return this.g;
    }

    public int getLowerSigmaBound() {
        return this.lowerSigmaBound;
    }

    public BigInteger getModulus() {
        return this.n;
    }
}
