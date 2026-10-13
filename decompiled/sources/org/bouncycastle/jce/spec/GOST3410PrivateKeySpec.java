package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.KeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/GOST3410PrivateKeySpec.class */
public class GOST3410PrivateKeySpec implements KeySpec {
    private BigInteger x;
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;

    public GOST3410PrivateKeySpec(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
        this.x = bigInteger;
        this.p = bigInteger2;
        this.q = bigInteger3;
        this.a = bigInteger4;
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getA() {
        return this.a;
    }
}
