package org.spongycastle.math.ec.endo;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPointMap;
import org.spongycastle.math.ec.ScaleXPointMap;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/endo/GLVTypeBEndomorphism.class */
public class GLVTypeBEndomorphism implements GLVEndomorphism {
    protected final ECCurve curve;
    protected final GLVTypeBParameters parameters;
    protected final ECPointMap pointMap;

    public GLVTypeBEndomorphism(ECCurve curve, GLVTypeBParameters parameters) {
        this.curve = curve;
        this.parameters = parameters;
        this.pointMap = new ScaleXPointMap(curve.fromBigInteger(parameters.getBeta()));
    }

    @Override // org.spongycastle.math.ec.endo.GLVEndomorphism
    public BigInteger[] decomposeScalar(BigInteger k) {
        int bits = this.parameters.getBits();
        BigInteger b1 = calculateB(k, this.parameters.getG1(), bits);
        BigInteger b2 = calculateB(k, this.parameters.getG2(), bits);
        GLVTypeBParameters p = this.parameters;
        BigInteger a = k.subtract(b1.multiply(p.getV1A()).add(b2.multiply(p.getV2A())));
        BigInteger b = b1.multiply(p.getV1B()).add(b2.multiply(p.getV2B())).negate();
        return new BigInteger[]{a, b};
    }

    @Override // org.spongycastle.math.ec.endo.ECEndomorphism
    public ECPointMap getPointMap() {
        return this.pointMap;
    }

    @Override // org.spongycastle.math.ec.endo.ECEndomorphism
    public boolean hasEfficientPointMap() {
        return true;
    }

    protected BigInteger calculateB(BigInteger k, BigInteger g, int t) {
        boolean negative = g.signum() < 0;
        BigInteger b = k.multiply(g.abs());
        boolean extra = b.testBit(t - 1);
        BigInteger b2 = b.shiftRight(t);
        if (extra) {
            b2 = b2.add(ECConstants.ONE);
        }
        return negative ? b2.negate() : b2;
    }
}
