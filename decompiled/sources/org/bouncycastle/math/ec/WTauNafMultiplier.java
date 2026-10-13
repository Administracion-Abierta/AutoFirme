package org.bouncycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/WTauNafMultiplier.class */
public class WTauNafMultiplier extends AbstractECMultiplier {
    static final String PRECOMP_NAME = "bc_wtnaf";

    @Override // org.bouncycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger) {
        if (!(eCPoint instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        ECPoint.AbstractF2m abstractF2m = (ECPoint.AbstractF2m) eCPoint;
        ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m) abstractF2m.getCurve();
        byte bByteValue = abstractF2m2.getA().toBigInteger().byteValue();
        byte mu = Tnaf.getMu(bByteValue);
        return multiplyWTnaf(abstractF2m, Tnaf.partModReduction(abstractF2m2, bigInteger, bByteValue, mu, (byte) 10), bByteValue, mu);
    }

    private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m abstractF2m, ZTauElement zTauElement, byte b, byte b2) {
        return multiplyFromWTnaf(abstractF2m, Tnaf.tauAdicWNaf(b2, zTauElement, 4, Tnaf.getTw(b2, 4).intValue(), b == 0 ? Tnaf.alpha0 : Tnaf.alpha1));
    }

    private static ECPoint.AbstractF2m multiplyFromWTnaf(final ECPoint.AbstractF2m abstractF2m, byte[] bArr) {
        ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m) abstractF2m.getCurve();
        final byte bByteValue = abstractF2m2.getA().toBigInteger().byteValue();
        ECPoint.AbstractF2m[] preComp = ((WTauNafPreCompInfo) abstractF2m2.precompute(abstractF2m, PRECOMP_NAME, new PreCompCallback() { // from class: org.bouncycastle.math.ec.WTauNafMultiplier.1
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                if (preCompInfo instanceof WTauNafPreCompInfo) {
                    return preCompInfo;
                }
                WTauNafPreCompInfo wTauNafPreCompInfo = new WTauNafPreCompInfo();
                wTauNafPreCompInfo.setPreComp(Tnaf.getPreComp(abstractF2m, bByteValue));
                return wTauNafPreCompInfo;
            }
        })).getPreComp();
        ECPoint.AbstractF2m[] abstractF2mArr = new ECPoint.AbstractF2m[preComp.length];
        for (int i = 0; i < preComp.length; i++) {
            abstractF2mArr[i] = (ECPoint.AbstractF2m) preComp[i].negate();
        }
        ECPoint.AbstractF2m abstractF2mTauPow = (ECPoint.AbstractF2m) abstractF2m.getCurve().getInfinity();
        int i2 = 0;
        for (int length = bArr.length - 1; length >= 0; length--) {
            i2++;
            byte b = bArr[length];
            if (b != 0) {
                ECPoint.AbstractF2m abstractF2mTauPow2 = abstractF2mTauPow.tauPow(i2);
                i2 = 0;
                abstractF2mTauPow = (ECPoint.AbstractF2m) abstractF2mTauPow2.add(b > 0 ? preComp[b >>> 1] : abstractF2mArr[(-b) >>> 1]);
            }
        }
        if (i2 > 0) {
            abstractF2mTauPow = abstractF2mTauPow.tauPow(i2);
        }
        return abstractF2mTauPow;
    }
}
