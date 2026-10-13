package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/WTauNafMultiplier.class */
public class WTauNafMultiplier extends AbstractECMultiplier {
    static final String PRECOMP_NAME = "bc_wtnaf";

    @Override // org.spongycastle.math.ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint point, BigInteger k) {
        if (!(point instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        ECPoint.AbstractF2m p = (ECPoint.AbstractF2m) point;
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        int m = curve.getFieldSize();
        byte a = curve.getA().toBigInteger().byteValue();
        byte mu = Tnaf.getMu(a);
        BigInteger[] s = curve.getSi();
        ZTauElement rho = Tnaf.partModReduction(k, m, a, s, mu, (byte) 10);
        return multiplyWTnaf(p, rho, curve.getPreCompInfo(p, PRECOMP_NAME), a, mu);
    }

    private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m p, ZTauElement lambda, PreCompInfo preCompInfo, byte a, byte mu) {
        ZTauElement[] alpha = a == 0 ? Tnaf.alpha0 : Tnaf.alpha1;
        BigInteger tw = Tnaf.getTw(mu, 4);
        byte[] u = Tnaf.tauAdicWNaf(mu, lambda, (byte) 4, BigInteger.valueOf(16L), tw, alpha);
        return multiplyFromWTnaf(p, u, preCompInfo);
    }

    private static ECPoint.AbstractF2m multiplyFromWTnaf(ECPoint.AbstractF2m p, byte[] u, PreCompInfo preCompInfo) {
        ECPoint.AbstractF2m[] pu;
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        byte a = curve.getA().toBigInteger().byteValue();
        if (preCompInfo == null || !(preCompInfo instanceof WTauNafPreCompInfo)) {
            pu = Tnaf.getPreComp(p, a);
            WTauNafPreCompInfo pre = new WTauNafPreCompInfo();
            pre.setPreComp(pu);
            curve.setPreCompInfo(p, PRECOMP_NAME, pre);
        } else {
            pu = ((WTauNafPreCompInfo) preCompInfo).getPreComp();
        }
        ECPoint[] puNeg = new ECPoint.AbstractF2m[pu.length];
        for (int i = 0; i < pu.length; i++) {
            puNeg[i] = (ECPoint.AbstractF2m) pu[i].negate();
        }
        ECPoint.AbstractF2m q = (ECPoint.AbstractF2m) p.getCurve().getInfinity();
        int tauCount = 0;
        for (int i2 = u.length - 1; i2 >= 0; i2--) {
            tauCount++;
            byte b = u[i2];
            if (b != 0) {
                ECPoint.AbstractF2m q2 = q.tauPow(tauCount);
                tauCount = 0;
                ECPoint x = b > 0 ? pu[b >>> 1] : puNeg[(-b) >>> 1];
                q = (ECPoint.AbstractF2m) q2.add(x);
            }
        }
        if (tauCount > 0) {
            q = q.tauPow(tauCount);
        }
        return q;
    }
}
