package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/NafL2RMultiplier.class */
public class NafL2RMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        int[] naf = WNafUtil.generateCompactNaf(k);
        ECPoint addP = p.normalize();
        ECPoint subP = addP.negate();
        ECPoint R = p.getCurve().getInfinity();
        int i = naf.length;
        while (true) {
            i--;
            if (i >= 0) {
                int ni = naf[i];
                int digit = ni >> 16;
                int zeroes = ni & 65535;
                R = R.twicePlus(digit < 0 ? subP : addP).timesPow2(zeroes);
            } else {
                return R;
            }
        }
    }
}
