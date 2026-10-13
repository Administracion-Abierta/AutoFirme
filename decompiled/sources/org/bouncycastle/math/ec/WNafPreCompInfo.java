package org.bouncycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/WNafPreCompInfo.class */
public class WNafPreCompInfo implements PreCompInfo {
    volatile int promotionCountdown = 4;
    protected int confWidth = -1;
    protected ECPoint[] preComp = null;
    protected ECPoint[] preCompNeg = null;
    protected ECPoint twice = null;
    protected int width = -1;

    int decrementPromotionCountdown() {
        int i = this.promotionCountdown;
        if (i > 0) {
            i--;
            this.promotionCountdown = i;
        }
        return i;
    }

    int getPromotionCountdown() {
        return this.promotionCountdown;
    }

    void setPromotionCountdown(int i) {
        this.promotionCountdown = i;
    }

    public boolean isPromoted() {
        return this.promotionCountdown <= 0;
    }

    public int getConfWidth() {
        return this.confWidth;
    }

    public void setConfWidth(int i) {
        this.confWidth = i;
    }

    public ECPoint[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint[] eCPointArr) {
        this.preComp = eCPointArr;
    }

    public ECPoint[] getPreCompNeg() {
        return this.preCompNeg;
    }

    public void setPreCompNeg(ECPoint[] eCPointArr) {
        this.preCompNeg = eCPointArr;
    }

    public ECPoint getTwice() {
        return this.twice;
    }

    public void setTwice(ECPoint eCPoint) {
        this.twice = eCPoint;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }
}
