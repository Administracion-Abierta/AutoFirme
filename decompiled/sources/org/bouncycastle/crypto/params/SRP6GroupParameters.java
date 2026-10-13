package org.bouncycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/SRP6GroupParameters.class */
public class SRP6GroupParameters {
    private BigInteger N;
    private BigInteger g;

    public SRP6GroupParameters(BigInteger bigInteger, BigInteger bigInteger2) {
        this.N = bigInteger;
        this.g = bigInteger2;
    }

    public BigInteger getG() {
        return this.g;
    }

    public BigInteger getN() {
        return this.N;
    }
}
