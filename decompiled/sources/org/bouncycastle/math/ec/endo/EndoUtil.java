package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/endo/EndoUtil.class */
public abstract class EndoUtil {
    public static final String PRECOMP_NAME = "bc_endo";

    public static BigInteger[] decomposeScalar(ScalarSplitParameters scalarSplitParameters, BigInteger bigInteger) {
        int bits = scalarSplitParameters.getBits();
        BigInteger bigIntegerCalculateB = calculateB(bigInteger, scalarSplitParameters.getG1(), bits);
        BigInteger bigIntegerCalculateB2 = calculateB(bigInteger, scalarSplitParameters.getG2(), bits);
        return new BigInteger[]{bigInteger.subtract(bigIntegerCalculateB.multiply(scalarSplitParameters.getV1A()).add(bigIntegerCalculateB2.multiply(scalarSplitParameters.getV2A()))), bigIntegerCalculateB.multiply(scalarSplitParameters.getV1B()).add(bigIntegerCalculateB2.multiply(scalarSplitParameters.getV2B())).negate()};
    }

    public static ECPoint mapPoint(final ECEndomorphism eCEndomorphism, final ECPoint eCPoint) {
        return ((EndoPreCompInfo) eCPoint.getCurve().precompute(eCPoint, PRECOMP_NAME, new PreCompCallback() { // from class: org.bouncycastle.math.ec.endo.EndoUtil.1
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                EndoPreCompInfo endoPreCompInfo = preCompInfo instanceof EndoPreCompInfo ? (EndoPreCompInfo) preCompInfo : null;
                if (checkExisting(endoPreCompInfo, eCEndomorphism)) {
                    return endoPreCompInfo;
                }
                ECPoint map = eCEndomorphism.getPointMap().map(eCPoint);
                EndoPreCompInfo endoPreCompInfo2 = new EndoPreCompInfo();
                endoPreCompInfo2.setEndomorphism(eCEndomorphism);
                endoPreCompInfo2.setMappedPoint(map);
                return endoPreCompInfo2;
            }

            private boolean checkExisting(EndoPreCompInfo endoPreCompInfo, ECEndomorphism eCEndomorphism2) {
                return (null == endoPreCompInfo || endoPreCompInfo.getEndomorphism() != eCEndomorphism2 || endoPreCompInfo.getMappedPoint() == null) ? false : true;
            }
        })).getMappedPoint();
    }

    private static BigInteger calculateB(BigInteger bigInteger, BigInteger bigInteger2, int i) {
        boolean z = bigInteger2.signum() < 0;
        BigInteger bigIntegerMultiply = bigInteger.multiply(bigInteger2.abs());
        boolean zTestBit = bigIntegerMultiply.testBit(i - 1);
        BigInteger bigIntegerShiftRight = bigIntegerMultiply.shiftRight(i);
        if (zTestBit) {
            bigIntegerShiftRight = bigIntegerShiftRight.add(ECConstants.ONE);
        }
        return z ? bigIntegerShiftRight.negate() : bigIntegerShiftRight;
    }
}
