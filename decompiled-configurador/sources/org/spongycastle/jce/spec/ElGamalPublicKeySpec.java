package org.spongycastle.jce.spec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/spec/ElGamalPublicKeySpec.class */
public class ElGamalPublicKeySpec extends ElGamalKeySpec {
    private BigInteger y;

    public ElGamalPublicKeySpec(BigInteger y, ElGamalParameterSpec spec) {
        super(spec);
        this.y = y;
    }

    public BigInteger getY() {
        return this.y;
    }
}
