package org.bouncycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/AbstractECMultiplier.class */
public abstract class AbstractECMultiplier implements ECMultiplier {
    @Override // org.bouncycastle.math.ec.ECMultiplier
    public ECPoint multiply(ECPoint eCPoint, BigInteger bigInteger) {
        int iSignum = bigInteger.signum();
        if (iSignum == 0 || eCPoint.isInfinity()) {
            return eCPoint.getCurve().getInfinity();
        }
        ECPoint eCPointMultiplyPositive = multiplyPositive(eCPoint, bigInteger.abs());
        return checkResult(iSignum > 0 ? eCPointMultiplyPositive : eCPointMultiplyPositive.negate());
    }

    protected abstract ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger);

    protected ECPoint checkResult(ECPoint eCPoint) {
        return ECAlgorithms.implCheckResult(eCPoint);
    }
}
