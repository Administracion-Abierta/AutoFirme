package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/GOST3410PrivateKeyParameters.class */
public class GOST3410PrivateKeyParameters extends GOST3410KeyParameters {
    private BigInteger x;

    public GOST3410PrivateKeyParameters(BigInteger x, GOST3410Parameters params) {
        super(true, params);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }
}
