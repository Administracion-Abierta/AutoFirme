package org.spongycastle.jce.spec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/ECPrivateKeySpec.class */
public class ECPrivateKeySpec extends ECKeySpec {
    private BigInteger d;

    public ECPrivateKeySpec(BigInteger d, ECParameterSpec spec) {
        super(spec);
        this.d = d;
    }

    public BigInteger getD() {
        return this.d;
    }
}
