package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/FixedPointUtil.class */
public class FixedPointUtil {
    public static final String PRECOMP_NAME = "bc_fixed_point";

    public static int getCombSize(ECCurve c) {
        BigInteger order = c.getOrder();
        return order == null ? c.getFieldSize() + 1 : order.bitLength();
    }

    public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo != null && (preCompInfo instanceof FixedPointPreCompInfo)) {
            return (FixedPointPreCompInfo) preCompInfo;
        }
        return new FixedPointPreCompInfo();
    }

    public static FixedPointPreCompInfo precompute(ECPoint p, int minWidth) {
        ECCurve c = p.getCurve();
        int n = 1 << minWidth;
        FixedPointPreCompInfo info = getFixedPointPreCompInfo(c.getPreCompInfo(p, "bc_fixed_point"));
        ECPoint[] lookupTable = info.getPreComp();
        if (lookupTable == null || lookupTable.length < n) {
            int bits = getCombSize(c);
            int d = ((bits + minWidth) - 1) / minWidth;
            ECPoint[] pow2Table = new ECPoint[minWidth + 1];
            pow2Table[0] = p;
            for (int i = 1; i < minWidth; i++) {
                pow2Table[i] = pow2Table[i - 1].timesPow2(d);
            }
            pow2Table[minWidth] = pow2Table[0].subtract(pow2Table[1]);
            c.normalizeAll(pow2Table);
            ECPoint[] lookupTable2 = new ECPoint[n];
            lookupTable2[0] = pow2Table[0];
            for (int bit = minWidth - 1; bit >= 0; bit--) {
                ECPoint pow2 = pow2Table[bit];
                int step = 1 << bit;
                int i2 = step;
                while (true) {
                    int i3 = i2;
                    if (i3 < n) {
                        lookupTable2[i3] = lookupTable2[i3 - step].add(pow2);
                        i2 = i3 + (step << 1);
                    }
                }
            }
            c.normalizeAll(lookupTable2);
            info.setOffset(pow2Table[minWidth]);
            info.setPreComp(lookupTable2);
            info.setWidth(minWidth);
            c.setPreCompInfo(p, "bc_fixed_point", info);
        }
        return info;
    }
}
