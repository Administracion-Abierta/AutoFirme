package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/MontgomeryLadderMultiplier.class */
public class MontgomeryLadderMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint[] R = new ECPoint[2];
        R[0] = p.getCurve().getInfinity();
        R[1] = p;
        int n = k.bitLength();
        int i = n;
        while (true) {
            i--;
            if (i >= 0) {
                int b = k.testBit(i) ? 1 : 0;
                int bp = 1 - b;
                R[bp] = R[bp].add(R[b]);
                R[b] = R[b].twice();
            } else {
                return R[0];
            }
        }
    }
}
