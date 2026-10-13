package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/ZSignedDigitL2RMultiplier.class */
public class ZSignedDigitL2RMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint addP = p.normalize();
        ECPoint subP = addP.negate();
        ECPoint R0 = addP;
        int n = k.bitLength();
        int s = k.getLowestSetBit();
        int i = n;
        while (true) {
            i--;
            if (i > s) {
                R0 = R0.twicePlus(k.testBit(i) ? addP : subP);
            } else {
                return R0.timesPow2(s);
            }
        }
    }
}
