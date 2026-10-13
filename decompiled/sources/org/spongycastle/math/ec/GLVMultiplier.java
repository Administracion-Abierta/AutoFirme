package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.math.ec.endo.GLVEndomorphism;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/GLVMultiplier.class */
public class GLVMultiplier extends AbstractECMultiplier {
    protected final ECCurve curve;
    protected final GLVEndomorphism glvEndomorphism;

    public GLVMultiplier(ECCurve curve, GLVEndomorphism glvEndomorphism) {
        if (curve == null || curve.getOrder() == null) {
            throw new IllegalArgumentException("Need curve with known group order");
        }
        this.curve = curve;
        this.glvEndomorphism = glvEndomorphism;
    }

    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        if (!this.curve.equals(p.getCurve())) {
            throw new IllegalStateException();
        }
        BigInteger n = p.getCurve().getOrder();
        BigInteger[] ab = this.glvEndomorphism.decomposeScalar(k.mod(n));
        BigInteger a = ab[0];
        BigInteger b = ab[1];
        ECPointMap pointMap = this.glvEndomorphism.getPointMap();
        if (this.glvEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implShamirsTrickWNaf(p, a, pointMap, b);
        }
        return ECAlgorithms.implShamirsTrickWNaf(p, a, pointMap.map(p), b);
    }
}
