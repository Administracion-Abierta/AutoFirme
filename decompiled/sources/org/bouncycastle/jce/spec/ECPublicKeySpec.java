package org.bouncycastle.jce.spec;

import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/ECPublicKeySpec.class */
public class ECPublicKeySpec extends ECKeySpec {
    private ECPoint q;

    public ECPublicKeySpec(ECPoint eCPoint, ECParameterSpec eCParameterSpec) {
        super(eCParameterSpec);
        if (eCPoint.getCurve() != null) {
            this.q = eCPoint.normalize();
        } else {
            this.q = eCPoint;
        }
    }

    public ECPoint getQ() {
        return this.q;
    }
}
