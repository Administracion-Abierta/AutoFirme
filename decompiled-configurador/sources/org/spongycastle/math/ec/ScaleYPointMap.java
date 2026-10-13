package org.spongycastle.math.ec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/ScaleYPointMap.class */
public class ScaleYPointMap implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleYPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override // org.spongycastle.math.ec.ECPointMap
    public ECPoint map(ECPoint p) {
        return p.scaleY(this.scale);
    }
}
