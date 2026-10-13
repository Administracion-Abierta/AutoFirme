package org.spongycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/WTauNafPreCompInfo.class */
public class WTauNafPreCompInfo implements PreCompInfo {
    protected ECPoint.AbstractF2m[] preComp = null;

    public ECPoint.AbstractF2m[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint.AbstractF2m[] preComp) {
        this.preComp = preComp;
    }
}
