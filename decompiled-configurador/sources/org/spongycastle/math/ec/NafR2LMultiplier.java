package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/NafR2LMultiplier.class */
public class NafR2LMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        int[] naf = WNafUtil.generateCompactNaf(k);
        ECPoint R0 = p.getCurve().getInfinity();
        ECPoint R1 = p;
        int zeroes = 0;
        for (int ni : naf) {
            int digit = ni >> 16;
            R1 = R1.timesPow2(zeroes + (ni & 65535));
            R0 = R0.add(digit < 0 ? R1.negate() : R1);
            zeroes = 1;
        }
        return R0;
    }
}
