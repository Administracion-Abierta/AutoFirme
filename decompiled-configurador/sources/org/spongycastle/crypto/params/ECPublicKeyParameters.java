package org.spongycastle.crypto.params;

import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/ECPublicKeyParameters.class */
public class ECPublicKeyParameters extends ECKeyParameters {
    private final ECPoint Q;

    public ECPublicKeyParameters(ECPoint Q, ECDomainParameters params) {
        super(false, params);
        this.Q = validate(Q);
    }

    private ECPoint validate(ECPoint q) {
        if (q == null) {
            throw new IllegalArgumentException("point has null value");
        }
        if (q.isInfinity()) {
            throw new IllegalArgumentException("point at infinity");
        }
        ECPoint q2 = q.normalize();
        if (!q2.isValid()) {
            throw new IllegalArgumentException("point not on curve");
        }
        return q2;
    }

    public ECPoint getQ() {
        return this.Q;
    }
}
