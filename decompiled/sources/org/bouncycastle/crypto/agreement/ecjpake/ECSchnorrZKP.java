package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECSchnorrZKP.class */
public class ECSchnorrZKP {
    private final ECPoint V;
    private final BigInteger r;

    ECSchnorrZKP(ECPoint eCPoint, BigInteger bigInteger) {
        this.V = eCPoint;
        this.r = bigInteger;
    }

    public ECPoint getV() {
        return this.V;
    }

    public BigInteger getr() {
        return this.r;
    }
}
