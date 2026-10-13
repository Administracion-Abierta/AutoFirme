package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/WNafL2RMultiplier.class */
public class WNafL2RMultiplier extends AbstractECMultiplier {
    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint R;
        int width = Math.max(2, Math.min(16, getWindowSize(k.bitLength())));
        WNafPreCompInfo wnafPreCompInfo = WNafUtil.precompute(p, width, true);
        ECPoint[] preComp = wnafPreCompInfo.getPreComp();
        ECPoint[] preCompNeg = wnafPreCompInfo.getPreCompNeg();
        int[] wnaf = WNafUtil.generateCompactWindowNaf(width, k);
        ECPoint R2 = p.getCurve().getInfinity();
        int i = wnaf.length;
        if (i > 1) {
            i--;
            int wi = wnaf[i];
            int digit = wi >> 16;
            int zeroes = wi & 65535;
            int n = Math.abs(digit);
            ECPoint[] table = digit < 0 ? preCompNeg : preComp;
            if ((n << 2) < (1 << width)) {
                byte b = LongArray.bitLengths[n];
                int scale = width - b;
                int lowBits = n ^ (1 << (b - 1));
                int i1 = (1 << (width - 1)) - 1;
                int i2 = (lowBits << scale) + 1;
                R = table[i1 >>> 1].add(table[i2 >>> 1]);
                zeroes -= scale;
            } else {
                R = table[n >>> 1];
            }
            R2 = R.timesPow2(zeroes);
        }
        while (i > 0) {
            i--;
            int wi2 = wnaf[i];
            int digit2 = wi2 >> 16;
            int zeroes2 = wi2 & 65535;
            ECPoint r = (digit2 < 0 ? preCompNeg : preComp)[Math.abs(digit2) >>> 1];
            R2 = R2.twicePlus(r).timesPow2(zeroes2);
        }
        return R2;
    }

    protected int getWindowSize(int bits) {
        return WNafUtil.getWindowSize(bits);
    }
}
