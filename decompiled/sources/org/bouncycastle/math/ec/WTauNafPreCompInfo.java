package org.bouncycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/WTauNafPreCompInfo.class */
public class WTauNafPreCompInfo implements PreCompInfo {
    protected ECPoint.AbstractF2m[] preComp = null;

    public ECPoint.AbstractF2m[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint.AbstractF2m[] abstractF2mArr) {
        this.preComp = abstractF2mArr;
    }
}
