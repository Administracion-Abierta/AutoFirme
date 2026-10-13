package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/WNafL2RMultiplier.class */
public class WNafL2RMultiplier extends AbstractECMultiplier {
    @Override // org.bouncycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger) {
        ECPoint eCPointAdd;
        WNafPreCompInfo wNafPreCompInfoPrecompute = WNafUtil.precompute(eCPoint, WNafUtil.getWindowSize(bigInteger.bitLength()), true);
        ECPoint[] preComp = wNafPreCompInfoPrecompute.getPreComp();
        ECPoint[] preCompNeg = wNafPreCompInfoPrecompute.getPreCompNeg();
        int width = wNafPreCompInfoPrecompute.getWidth();
        int[] iArrGenerateCompactWindowNaf = WNafUtil.generateCompactWindowNaf(width, bigInteger);
        ECPoint infinity = eCPoint.getCurve().getInfinity();
        int length = iArrGenerateCompactWindowNaf.length;
        if (length > 1) {
            length--;
            int i = iArrGenerateCompactWindowNaf[length];
            int i2 = i >> 16;
            int i3 = i & 65535;
            int iAbs = Math.abs(i2);
            ECPoint[] eCPointArr = i2 < 0 ? preCompNeg : preComp;
            if ((iAbs << 2) < (1 << width)) {
                int iNumberOfLeadingZeros = 32 - Integers.numberOfLeadingZeros(iAbs);
                int i4 = width - iNumberOfLeadingZeros;
                eCPointAdd = eCPointArr[((1 << (width - 1)) - 1) >>> 1].add(eCPointArr[(((iAbs ^ (1 << (iNumberOfLeadingZeros - 1))) << i4) + 1) >>> 1]);
                i3 -= i4;
            } else {
                eCPointAdd = eCPointArr[iAbs >>> 1];
            }
            infinity = eCPointAdd.timesPow2(i3);
        }
        while (length > 0) {
            length--;
            int i5 = iArrGenerateCompactWindowNaf[length];
            int i6 = i5 >> 16;
            infinity = infinity.twicePlus((i6 < 0 ? preCompNeg : preComp)[Math.abs(i6) >>> 1]).timesPow2(i5 & 65535);
        }
        return infinity;
    }
}
