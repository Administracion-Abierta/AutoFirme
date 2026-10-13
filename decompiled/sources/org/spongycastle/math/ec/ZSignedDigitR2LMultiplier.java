package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/ZSignedDigitR2LMultiplier.class */
public class ZSignedDigitR2LMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint R0 = p.getCurve().getInfinity();
        int n = k.bitLength();
        int s = k.getLowestSetBit();
        ECPoint R1 = p.timesPow2(s);
        int i = s;
        while (true) {
            i++;
            if (i < n) {
                R0 = R0.add(k.testBit(i) ? R1 : R1.negate());
                R1 = R1.twice();
            } else {
                return R0.add(R1);
            }
        }
    }
}
