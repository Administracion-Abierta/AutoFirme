package org.bouncycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/NaccacheSternKeyParameters.class */
public class NaccacheSternKeyParameters extends AsymmetricKeyParameter {
    private BigInteger g;
    private BigInteger n;
    int lowerSigmaBound;

    public NaccacheSternKeyParameters(boolean z, BigInteger bigInteger, BigInteger bigInteger2, int i) {
        super(z);
        this.g = bigInteger;
        this.n = bigInteger2;
        this.lowerSigmaBound = i;
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
