package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/ElGamalParameterSpec.class */
public class ElGamalParameterSpec implements AlgorithmParameterSpec {
    private BigInteger p;
    private BigInteger g;

    public ElGamalParameterSpec(BigInteger bigInteger, BigInteger bigInteger2) {
        this.p = bigInteger;
        this.g = bigInteger2;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }
}
