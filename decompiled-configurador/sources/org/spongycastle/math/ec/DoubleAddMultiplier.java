package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/DoubleAddMultiplier.class */
public class DoubleAddMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint[] R = new ECPoint[2];
        R[0] = p.getCurve().getInfinity();
        R[1] = p;
        int n = k.bitLength();
        for (int i = 0; i < n; i++) {
            int b = k.testBit(i) ? 1 : 0;
            int bp = 1 - b;
            R[bp] = R[bp].twicePlus(R[b]);
        }
        return R[0];
    }
}
