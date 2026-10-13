package org.spongycastle.jce.spec;

import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/ECPublicKeySpec.class */
public class ECPublicKeySpec extends ECKeySpec {
    private ECPoint q;

    public ECPublicKeySpec(ECPoint q, ECParameterSpec spec) {
        super(spec);
        if (q.getCurve() != null) {
            this.q = q.normalize();
        } else {
            this.q = q;
        }
    }

    public ECPoint getQ() {
        return this.q;
    }
}
