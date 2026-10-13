package org.spongycastle.math.ec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/FixedPointPreCompInfo.class */
public class FixedPointPreCompInfo implements PreCompInfo {
    protected ECPoint offset = null;
    protected ECPoint[] preComp = null;
    protected int width = -1;

    public ECPoint getOffset() {
        return this.offset;
    }

    public void setOffset(ECPoint offset) {
        this.offset = offset;
    }

    public ECPoint[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint[] preComp) {
        this.preComp = preComp;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
