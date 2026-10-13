package org.bouncycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/GOST3410PublicKeyParameters.class */
public class GOST3410PublicKeyParameters extends GOST3410KeyParameters {
    private BigInteger y;

    public GOST3410PublicKeyParameters(BigInteger bigInteger, GOST3410Parameters gOST3410Parameters) {
        super(false, gOST3410Parameters);
        this.y = bigInteger;
    }

    public BigInteger getY() {
        return this.y;
    }
}
