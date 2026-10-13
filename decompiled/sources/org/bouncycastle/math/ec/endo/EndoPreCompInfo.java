package org.bouncycastle.math.ec.endo;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompInfo;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/endo/EndoPreCompInfo.class */
public class EndoPreCompInfo implements PreCompInfo {
    protected ECEndomorphism endomorphism;
    protected ECPoint mappedPoint;

    public ECEndomorphism getEndomorphism() {
        return this.endomorphism;
    }

    public void setEndomorphism(ECEndomorphism eCEndomorphism) {
        this.endomorphism = eCEndomorphism;
    }

    public ECPoint getMappedPoint() {
        return this.mappedPoint;
    }

    public void setMappedPoint(ECPoint eCPoint) {
        this.mappedPoint = eCPoint;
    }
}
