package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/DSAPrivateKeyParameters.class */
public class DSAPrivateKeyParameters extends DSAKeyParameters {
    private BigInteger x;

    public DSAPrivateKeyParameters(BigInteger x, DSAParameters params) {
        super(true, params);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }
}
