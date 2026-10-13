package org.bouncycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/FixedPointUtil.class */
public class FixedPointUtil {
    public static final String PRECOMP_NAME = "bc_fixed_point";

    public static int getCombSize(ECCurve eCCurve) {
        BigInteger order = eCCurve.getOrder();
        return order == null ? eCCurve.getFieldSize() + 1 : order.bitLength();
    }

    public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo instanceof FixedPointPreCompInfo) {
            return (FixedPointPreCompInfo) preCompInfo;
        }
        return null;
    }

    public static FixedPointPreCompInfo precompute(final ECPoint eCPoint) {
        final ECCurve curve = eCPoint.getCurve();
        return (FixedPointPreCompInfo) curve.precompute(eCPoint, "bc_fixed_point", new PreCompCallback() { // from class: org.bouncycastle.math.ec.FixedPointUtil.1
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                FixedPointPreCompInfo fixedPointPreCompInfo = preCompInfo instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo) preCompInfo : null;
                int combSize = FixedPointUtil.getCombSize(curve);
                int i = combSize > 250 ? 6 : 5;
                int i2 = 1 << i;
                if (checkExisting(fixedPointPreCompInfo, i2)) {
                    return fixedPointPreCompInfo;
                }
                int i3 = ((combSize + i) - 1) / i;
                ECPoint[] eCPointArr = new ECPoint[i + 1];
                eCPointArr[0] = eCPoint;
                for (int i4 = 1; i4 < i; i4++) {
                    eCPointArr[i4] = eCPointArr[i4 - 1].timesPow2(i3);
                }
                eCPointArr[i] = eCPointArr[0].subtract(eCPointArr[1]);
                curve.normalizeAll(eCPointArr);
                ECPoint[] eCPointArr2 = new ECPoint[i2];
                eCPointArr2[0] = eCPointArr[0];
                for (int i5 = i - 1; i5 >= 0; i5--) {
                    ECPoint eCPoint2 = eCPointArr[i5];
                    int i6 = 1 << i5;
                    int i7 = i6;
                    while (true) {
                        int i8 = i7;
                        if (i8 < i2) {
                            eCPointArr2[i8] = eCPointArr2[i8 - i6].add(eCPoint2);
                            i7 = i8 + (i6 << 1);
                        }
                    }
                }
                curve.normalizeAll(eCPointArr2);
                FixedPointPreCompInfo fixedPointPreCompInfo2 = new FixedPointPreCompInfo();
                fixedPointPreCompInfo2.setLookupTable(curve.createCacheSafeLookupTable(eCPointArr2, 0, eCPointArr2.length));
                fixedPointPreCompInfo2.setOffset(eCPointArr[i]);
                fixedPointPreCompInfo2.setWidth(i);
                return fixedPointPreCompInfo2;
            }

            private boolean checkExisting(FixedPointPreCompInfo fixedPointPreCompInfo, int i) {
                return fixedPointPreCompInfo != null && checkTable(fixedPointPreCompInfo.getLookupTable(), i);
            }

            private boolean checkTable(ECLookupTable eCLookupTable, int i) {
                return eCLookupTable != null && eCLookupTable.getSize() >= i;
            }
        });
    }
}
