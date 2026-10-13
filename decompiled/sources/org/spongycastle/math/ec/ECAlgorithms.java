package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.math.ec.endo.ECEndomorphism;
import org.spongycastle.math.ec.endo.GLVEndomorphism;
import org.spongycastle.math.field.FiniteField;
import org.spongycastle.math.field.PolynomialExtensionField;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/ECAlgorithms.class */
public class ECAlgorithms {
    public static boolean isF2mCurve(ECCurve c) {
        return isF2mField(c.getField());
    }

    public static boolean isF2mField(FiniteField field) {
        return field.getDimension() > 1 && field.getCharacteristic().equals(ECConstants.TWO) && (field instanceof PolynomialExtensionField);
    }

    public static boolean isFpCurve(ECCurve c) {
        return isFpField(c.getField());
    }

    public static boolean isFpField(FiniteField field) {
        return field.getDimension() == 1;
    }

    public static ECPoint sumOfMultiplies(ECPoint[] ps, BigInteger[] ks) {
        if (ps == null || ks == null || ps.length != ks.length || ps.length < 1) {
            throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
        }
        int count = ps.length;
        switch (count) {
            case 1:
                return ps[0].multiply(ks[0]);
            case 2:
                return sumOfTwoMultiplies(ps[0], ks[0], ps[1], ks[1]);
            default:
                ECPoint p = ps[0];
                ECCurve c = p.getCurve();
                ECPoint[] imported = new ECPoint[count];
                imported[0] = p;
                for (int i = 1; i < count; i++) {
                    imported[i] = importPoint(c, ps[i]);
                }
                ECEndomorphism endomorphism = c.getEndomorphism();
                if (endomorphism instanceof GLVEndomorphism) {
                    return validatePoint(implSumOfMultipliesGLV(imported, ks, (GLVEndomorphism) endomorphism));
                }
                return validatePoint(implSumOfMultiplies(imported, ks));
        }
    }

    public static ECPoint sumOfTwoMultiplies(ECPoint P, BigInteger a, ECPoint Q, BigInteger b) {
        ECCurve cp = P.getCurve();
        ECPoint Q2 = importPoint(cp, Q);
        if (cp instanceof ECCurve.AbstractF2m) {
            ECCurve.AbstractF2m f2mCurve = (ECCurve.AbstractF2m) cp;
            if (f2mCurve.isKoblitz()) {
                return validatePoint(P.multiply(a).add(Q2.multiply(b)));
            }
        }
        ECEndomorphism endomorphism = cp.getEndomorphism();
        if (endomorphism instanceof GLVEndomorphism) {
            return validatePoint(implSumOfMultipliesGLV(new ECPoint[]{P, Q2}, new BigInteger[]{a, b}, (GLVEndomorphism) endomorphism));
        }
        return validatePoint(implShamirsTrickWNaf(P, a, Q2, b));
    }

    public static ECPoint shamirsTrick(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        ECCurve cp = P.getCurve();
        return validatePoint(implShamirsTrickJsf(P, k, importPoint(cp, Q), l));
    }

    public static ECPoint importPoint(ECCurve c, ECPoint p) {
        ECCurve cp = p.getCurve();
        if (!c.equals(cp)) {
            throw new IllegalArgumentException("Point must be on the same curve");
        }
        return c.importPoint(p);
    }

    public static void montgomeryTrick(ECFieldElement[] zs, int off, int len) {
        montgomeryTrick(zs, off, len, null);
    }

    public static void montgomeryTrick(ECFieldElement[] zs, int off, int len, ECFieldElement scale) {
        ECFieldElement[] c = new ECFieldElement[len];
        c[0] = zs[off];
        int i = 0;
        while (true) {
            i++;
            if (i >= len) {
                break;
            } else {
                c[i] = c[i - 1].multiply(zs[off + i]);
            }
        }
        int i2 = i - 1;
        if (scale != null) {
            c[i2] = c[i2].multiply(scale);
        }
        ECFieldElement eCFieldElementInvert = c[i2].invert();
        while (true) {
            ECFieldElement u = eCFieldElementInvert;
            if (i2 > 0) {
                int i3 = i2;
                i2--;
                int j = off + i3;
                ECFieldElement tmp = zs[j];
                zs[j] = c[i2].multiply(u);
                eCFieldElementInvert = u.multiply(tmp);
            } else {
                zs[off] = u;
                return;
            }
        }
    }

    public static ECPoint referenceMultiply(ECPoint p, BigInteger k) {
        BigInteger x = k.abs();
        ECPoint q = p.getCurve().getInfinity();
        int t = x.bitLength();
        if (t > 0) {
            if (x.testBit(0)) {
                q = p;
            }
            for (int i = 1; i < t; i++) {
                p = p.twice();
                if (x.testBit(i)) {
                    q = q.add(p);
                }
            }
        }
        return k.signum() < 0 ? q.negate() : q;
    }

    public static ECPoint validatePoint(ECPoint p) {
        if (!p.isValid()) {
            throw new IllegalArgumentException("Invalid point");
        }
        return p;
    }

    static ECPoint implShamirsTrickJsf(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        ECCurve curve = P.getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint PaddQ = P.add(Q);
        ECPoint PsubQ = P.subtract(Q);
        ECPoint[] points = {Q, PsubQ, P, PaddQ};
        curve.normalizeAll(points);
        ECPoint[] table = {points[3].negate(), points[2].negate(), points[1].negate(), points[0].negate(), infinity, points[0], points[1], points[2], points[3]};
        byte[] jsf = WNafUtil.generateJSF(k, l);
        ECPoint R = infinity;
        int i = jsf.length;
        while (true) {
            i--;
            if (i >= 0) {
                byte b = jsf[i];
                int kDigit = (b << 24) >> 28;
                int lDigit = (b << 28) >> 28;
                int index = 4 + (kDigit * 3) + lDigit;
                R = R.twicePlus(table[index]);
            } else {
                return R;
            }
        }
    }

    static ECPoint implShamirsTrickWNaf(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        boolean negK = k.signum() < 0;
        boolean negL = l.signum() < 0;
        BigInteger k2 = k.abs();
        BigInteger l2 = l.abs();
        int widthP = Math.max(2, Math.min(16, WNafUtil.getWindowSize(k2.bitLength())));
        int widthQ = Math.max(2, Math.min(16, WNafUtil.getWindowSize(l2.bitLength())));
        WNafPreCompInfo infoP = WNafUtil.precompute(P, widthP, true);
        WNafPreCompInfo infoQ = WNafUtil.precompute(Q, widthQ, true);
        ECPoint[] preCompP = negK ? infoP.getPreCompNeg() : infoP.getPreComp();
        ECPoint[] preCompQ = negL ? infoQ.getPreCompNeg() : infoQ.getPreComp();
        ECPoint[] preCompNegP = negK ? infoP.getPreComp() : infoP.getPreCompNeg();
        ECPoint[] preCompNegQ = negL ? infoQ.getPreComp() : infoQ.getPreCompNeg();
        byte[] wnafP = WNafUtil.generateWindowNaf(widthP, k2);
        byte[] wnafQ = WNafUtil.generateWindowNaf(widthQ, l2);
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ);
    }

    static ECPoint implShamirsTrickWNaf(ECPoint P, BigInteger k, ECPointMap pointMapQ, BigInteger l) {
        boolean negK = k.signum() < 0;
        boolean negL = l.signum() < 0;
        BigInteger k2 = k.abs();
        BigInteger l2 = l.abs();
        int width = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(k2.bitLength(), l2.bitLength()))));
        ECPoint Q = WNafUtil.mapPointWithPrecomp(P, width, true, pointMapQ);
        WNafPreCompInfo infoP = WNafUtil.getWNafPreCompInfo(P);
        WNafPreCompInfo infoQ = WNafUtil.getWNafPreCompInfo(Q);
        ECPoint[] preCompP = negK ? infoP.getPreCompNeg() : infoP.getPreComp();
        ECPoint[] preCompQ = negL ? infoQ.getPreCompNeg() : infoQ.getPreComp();
        ECPoint[] preCompNegP = negK ? infoP.getPreComp() : infoP.getPreCompNeg();
        ECPoint[] preCompNegQ = negL ? infoQ.getPreComp() : infoQ.getPreCompNeg();
        byte[] wnafP = WNafUtil.generateWindowNaf(width, k2);
        byte[] wnafQ = WNafUtil.generateWindowNaf(width, l2);
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ);
    }

    private static ECPoint implShamirsTrickWNaf(ECPoint[] preCompP, ECPoint[] preCompNegP, byte[] wnafP, ECPoint[] preCompQ, ECPoint[] preCompNegQ, byte[] wnafQ) {
        int len = Math.max(wnafP.length, wnafQ.length);
        ECCurve curve = preCompP[0].getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint R = infinity;
        int zeroes = 0;
        int i = len - 1;
        while (i >= 0) {
            int wiP = i < wnafP.length ? wnafP[i] : (byte) 0;
            int wiQ = i < wnafQ.length ? wnafQ[i] : (byte) 0;
            if ((wiP | wiQ) == 0) {
                zeroes++;
            } else {
                ECPoint r = infinity;
                if (wiP != 0) {
                    int nP = Math.abs(wiP);
                    ECPoint[] tableP = wiP < 0 ? preCompNegP : preCompP;
                    r = r.add(tableP[nP >>> 1]);
                }
                if (wiQ != 0) {
                    int nQ = Math.abs(wiQ);
                    ECPoint[] tableQ = wiQ < 0 ? preCompNegQ : preCompQ;
                    r = r.add(tableQ[nQ >>> 1]);
                }
                if (zeroes > 0) {
                    R = R.timesPow2(zeroes);
                    zeroes = 0;
                }
                R = R.twicePlus(r);
            }
            i--;
        }
        if (zeroes > 0) {
            R = R.timesPow2(zeroes);
        }
        return R;
    }

    static ECPoint implSumOfMultiplies(ECPoint[] ps, BigInteger[] ks) {
        int count = ps.length;
        boolean[] negs = new boolean[count];
        WNafPreCompInfo[] infos = new WNafPreCompInfo[count];
        byte[][] wnafs = new byte[count][];
        for (int i = 0; i < count; i++) {
            BigInteger ki = ks[i];
            negs[i] = ki.signum() < 0;
            BigInteger ki2 = ki.abs();
            int width = Math.max(2, Math.min(16, WNafUtil.getWindowSize(ki2.bitLength())));
            infos[i] = WNafUtil.precompute(ps[i], width, true);
            wnafs[i] = WNafUtil.generateWindowNaf(width, ki2);
        }
        return implSumOfMultiplies(negs, infos, wnafs);
    }

    static ECPoint implSumOfMultipliesGLV(ECPoint[] ps, BigInteger[] ks, GLVEndomorphism glvEndomorphism) {
        BigInteger n = ps[0].getCurve().getOrder();
        int len = ps.length;
        BigInteger[] abs = new BigInteger[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            BigInteger[] ab = glvEndomorphism.decomposeScalar(ks[i].mod(n));
            int i2 = j;
            int j2 = j + 1;
            abs[i2] = ab[0];
            j = j2 + 1;
            abs[j2] = ab[1];
        }
        ECPointMap pointMap = glvEndomorphism.getPointMap();
        if (glvEndomorphism.hasEfficientPointMap()) {
            return implSumOfMultiplies(ps, pointMap, abs);
        }
        ECPoint[] pqs = new ECPoint[len << 1];
        int j3 = 0;
        for (ECPoint p : ps) {
            ECPoint q = pointMap.map(p);
            int i3 = j3;
            int j4 = j3 + 1;
            pqs[i3] = p;
            j3 = j4 + 1;
            pqs[j4] = q;
        }
        return implSumOfMultiplies(pqs, abs);
    }

    static ECPoint implSumOfMultiplies(ECPoint[] ps, ECPointMap pointMap, BigInteger[] ks) {
        int halfCount = ps.length;
        int fullCount = halfCount << 1;
        boolean[] negs = new boolean[fullCount];
        WNafPreCompInfo[] infos = new WNafPreCompInfo[fullCount];
        byte[][] wnafs = new byte[fullCount][];
        for (int i = 0; i < halfCount; i++) {
            int j0 = i << 1;
            int j1 = j0 + 1;
            BigInteger kj0 = ks[j0];
            negs[j0] = kj0.signum() < 0;
            BigInteger kj1 = kj0.abs();
            BigInteger kj2 = ks[j1];
            negs[j1] = kj2.signum() < 0;
            BigInteger kj3 = kj2.abs();
            int width = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(kj1.bitLength(), kj3.bitLength()))));
            ECPoint P = ps[i];
            ECPoint Q = WNafUtil.mapPointWithPrecomp(P, width, true, pointMap);
            infos[j0] = WNafUtil.getWNafPreCompInfo(P);
            infos[j1] = WNafUtil.getWNafPreCompInfo(Q);
            wnafs[j0] = WNafUtil.generateWindowNaf(width, kj1);
            wnafs[j1] = WNafUtil.generateWindowNaf(width, kj3);
        }
        return implSumOfMultiplies(negs, infos, wnafs);
    }

    private static ECPoint implSumOfMultiplies(boolean[] negs, WNafPreCompInfo[] infos, byte[][] wnafs) {
        int len = 0;
        int count = wnafs.length;
        for (byte[] bArr : wnafs) {
            len = Math.max(len, bArr.length);
        }
        ECCurve curve = infos[0].getPreComp()[0].getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint R = infinity;
        int zeroes = 0;
        int i = len - 1;
        while (i >= 0) {
            ECPoint r = infinity;
            for (int j = 0; j < count; j++) {
                byte[] wnaf = wnafs[j];
                int wi = i < wnaf.length ? wnaf[i] : (byte) 0;
                if (wi != 0) {
                    int n = Math.abs(wi);
                    WNafPreCompInfo info = infos[j];
                    ECPoint[] table = (wi < 0) == negs[j] ? info.getPreComp() : info.getPreCompNeg();
                    r = r.add(table[n >>> 1]);
                }
            }
            if (r == infinity) {
                zeroes++;
            } else {
                if (zeroes > 0) {
                    R = R.timesPow2(zeroes);
                    zeroes = 0;
                }
                R = R.twicePlus(r);
            }
            i--;
        }
        if (zeroes > 0) {
            R = R.timesPow2(zeroes);
        }
        return R;
    }
}
