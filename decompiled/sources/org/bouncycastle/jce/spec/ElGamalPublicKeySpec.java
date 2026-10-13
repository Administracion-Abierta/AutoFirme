package org.bouncycastle.jce.spec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/ElGamalPublicKeySpec.class */
public class ElGamalPublicKeySpec extends ElGamalKeySpec {
    private BigInteger y;

    public ElGamalPublicKeySpec(BigInteger bigInteger, ElGamalParameterSpec elGamalParameterSpec) {
        super(elGamalParameterSpec);
        this.y = bigInteger;
    }

    public BigInteger getY() {
        return this.y;
    }
}
