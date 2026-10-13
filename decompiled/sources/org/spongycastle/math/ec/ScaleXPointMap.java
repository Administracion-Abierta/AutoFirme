package org.spongycastle.math.ec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/ScaleXPointMap.class */
public class ScaleXPointMap implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleXPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override // org.spongycastle.math.ec.ECPointMap
    public ECPoint map(ECPoint p) {
        return p.scaleX(this.scale);
    }
}
