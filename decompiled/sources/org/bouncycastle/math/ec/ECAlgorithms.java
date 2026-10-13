package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.raw.Nat;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECAlgorithms.class */
public class ECAlgorithms {
    public static boolean isF2mCurve(ECCurve eCCurve) {
        return isF2mField(eCCurve.getField());
    }

    public static boolean isF2mField(FiniteField finiteField) {
        return finiteField.getDimension() > 1 && finiteField.getCharacteristic().equals(ECConstants.TWO) && (finiteField instanceof PolynomialExtensionField);
    }

    public static boolean isFpCurve(ECCurve eCCurve) {
        return isFpField(eCCurve.getField());
    }

    public static boolean isFpField(FiniteField finiteField) {
        return finiteField.getDimension() == 1;
    }

    public static ECPoint sumOfMultiplies(ECPoint[] eCPointArr, BigInteger[] bigIntegerArr) {
        if (eCPointArr == null || bigIntegerArr == null || eCPointArr.length != bigIntegerArr.length || eCPointArr.length < 1) {
            throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
        }
        int length = eCPointArr.length;
        switch (length) {
            case 1:
                return eCPointArr[0].multiply(bigIntegerArr[0]);
            case 2:
                return sumOfTwoMultiplies(eCPointArr[0], bigIntegerArr[0], eCPointArr[1], bigIntegerArr[1]);
            default:
                ECPoint eCPoint = eCPointArr[0];
                ECCurve curve = eCPoint.getCurve();
                ECPoint[] eCPointArr2 = new ECPoint[length];
                eCPointArr2[0] = eCPoint;
                for (int i = 1; i < length; i++) {
                    eCPointArr2[i] = importPoint(curve, eCPointArr[i]);
                }
                ECEndomorphism endomorphism = curve.getEndomorphism();
                return endomorphism instanceof GLVEndomorphism ? implCheckResult(implSumOfMultipliesGLV(eCPointArr2, bigIntegerArr, (GLVEndomorphism) endomorphism)) : implCheckResult(implSumOfMultiplies(eCPointArr2, bigIntegerArr));
        }
    }

    public static ECPoint sumOfTwoMultiplies(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        ECCurve curve = eCPoint.getCurve();
        ECPoint eCPointImportPoint = importPoint(curve, eCPoint2);
        if ((curve instanceof ECCurve.AbstractF2m) && ((ECCurve.AbstractF2m) curve).isKoblitz()) {
            return implCheckResult(eCPoint.multiply(bigInteger).add(eCPointImportPoint.multiply(bigInteger2)));
        }
        ECEndomorphism endomorphism = curve.getEndomorphism();
        return endomorphism instanceof GLVEndomorphism ? implCheckResult(implSumOfMultipliesGLV(new ECPoint[]{eCPoint, eCPointImportPoint}, new BigInteger[]{bigInteger, bigInteger2}, (GLVEndomorphism) endomorphism)) : implCheckResult(implShamirsTrickWNaf(eCPoint, bigInteger, eCPointImportPoint, bigInteger2));
    }

    public static ECPoint shamirsTrick(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        return implCheckResult(implShamirsTrickJsf(eCPoint, bigInteger, importPoint(eCPoint.getCurve(), eCPoint2), bigInteger2));
    }

    public static ECPoint importPoint(ECCurve eCCurve, ECPoint eCPoint) {
        if (eCCurve.equals(eCPoint.getCurve())) {
            return eCCurve.importPoint(eCPoint);
        }
        throw new IllegalArgumentException("Point must be on the same curve");
    }

    public static void montgomeryTrick(ECFieldElement[] eCFieldElementArr, int i, int i2) {
        montgomeryTrick(eCFieldElementArr, i, i2, null);
    }

    public static void montgomeryTrick(ECFieldElement[] eCFieldElementArr, int i, int i2, ECFieldElement eCFieldElement) {
        ECFieldElement[] eCFieldElementArr2 = new ECFieldElement[i2];
        eCFieldElementArr2[0] = eCFieldElementArr[i];
        int i3 = 0;
        while (true) {
            i3++;
            if (i3 >= i2) {
                break;
            } else {
                eCFieldElementArr2[i3] = eCFieldElementArr2[i3 - 1].multiply(eCFieldElementArr[i + i3]);
            }
        }
        int i4 = i3 - 1;
        if (eCFieldElement != null) {
            eCFieldElementArr2[i4] = eCFieldElementArr2[i4].multiply(eCFieldElement);
        }
        ECFieldElement eCFieldElementInvert = eCFieldElementArr2[i4].invert();
        while (true) {
            ECFieldElement eCFieldElement2 = eCFieldElementInvert;
            if (i4 <= 0) {
                eCFieldElementArr[i] = eCFieldElement2;
                return;
            }
            int i5 = i4;
            i4--;
            int i6 = i + i5;
            ECFieldElement eCFieldElement3 = eCFieldElementArr[i6];
            eCFieldElementArr[i6] = eCFieldElementArr2[i4].multiply(eCFieldElement2);
            eCFieldElementInvert = eCFieldElement2.multiply(eCFieldElement3);
        }
    }

    public static ECPoint referenceMultiply(ECPoint eCPoint, BigInteger bigInteger) {
        BigInteger bigIntegerAbs = bigInteger.abs();
        ECPoint infinity = eCPoint.getCurve().getInfinity();
        int iBitLength = bigIntegerAbs.bitLength();
        if (iBitLength > 0) {
            if (bigIntegerAbs.testBit(0)) {
                infinity = eCPoint;
            }
            for (int i = 1; i < iBitLength; i++) {
                eCPoint = eCPoint.twice();
                if (bigIntegerAbs.testBit(i)) {
                    infinity = infinity.add(eCPoint);
                }
            }
        }
        return bigInteger.signum() < 0 ? infinity.negate() : infinity;
    }

    public static ECPoint validatePoint(ECPoint eCPoint) {
        if (eCPoint.isValid()) {
            return eCPoint;
        }
        throw new IllegalStateException("Invalid point");
    }

    public static ECPoint cleanPoint(ECCurve eCCurve, ECPoint eCPoint) {
        if (eCCurve.equals(eCPoint.getCurve())) {
            return eCCurve.decodePoint(eCPoint.getEncoded(false));
        }
        throw new IllegalArgumentException("Point must be on the same curve");
    }

    static ECPoint implCheckResult(ECPoint eCPoint) {
        if (eCPoint.isValidPartial()) {
            return eCPoint;
        }
        throw new IllegalStateException("Invalid result");
    }

    static ECPoint implShamirsTrickJsf(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        ECCurve curve = eCPoint.getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint[] eCPointArr = {eCPoint2, eCPoint.subtract(eCPoint2), eCPoint, eCPoint.add(eCPoint2)};
        curve.normalizeAll(eCPointArr);
        ECPoint[] eCPointArr2 = {eCPointArr[3].negate(), eCPointArr[2].negate(), eCPointArr[1].negate(), eCPointArr[0].negate(), infinity, eCPointArr[0], eCPointArr[1], eCPointArr[2], eCPointArr[3]};
        byte[] bArrGenerateJSF = WNafUtil.generateJSF(bigInteger, bigInteger2);
        ECPoint eCPointTwicePlus = infinity;
        int length = bArrGenerateJSF.length;
        while (true) {
            length--;
            if (length < 0) {
                return eCPointTwicePlus;
            }
            byte b = bArrGenerateJSF[length];
            eCPointTwicePlus = eCPointTwicePlus.twicePlus(eCPointArr2[4 + (((b << 24) >> 28) * 3) + ((b << 28) >> 28)]);
        }
    }

    static ECPoint implShamirsTrickWNaf(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        boolean z = bigInteger.signum() < 0;
        boolean z2 = bigInteger2.signum() < 0;
        BigInteger bigIntegerAbs = bigInteger.abs();
        BigInteger bigIntegerAbs2 = bigInteger2.abs();
        int windowSize = WNafUtil.getWindowSize(bigIntegerAbs.bitLength(), 8);
        int windowSize2 = WNafUtil.getWindowSize(bigIntegerAbs2.bitLength(), 8);
        WNafPreCompInfo wNafPreCompInfoPrecompute = WNafUtil.precompute(eCPoint, windowSize, true);
        WNafPreCompInfo wNafPreCompInfoPrecompute2 = WNafUtil.precompute(eCPoint2, windowSize2, true);
        int combSize = FixedPointUtil.getCombSize(eCPoint.getCurve());
        if (!z && !z2 && bigInteger.bitLength() <= combSize && bigInteger2.bitLength() <= combSize && wNafPreCompInfoPrecompute.isPromoted() && wNafPreCompInfoPrecompute2.isPromoted()) {
            return implShamirsTrickFixedPoint(eCPoint, bigInteger, eCPoint2, bigInteger2);
        }
        int iMin = Math.min(8, wNafPreCompInfoPrecompute.getWidth());
        int iMin2 = Math.min(8, wNafPreCompInfoPrecompute2.getWidth());
        return implShamirsTrickWNaf(z ? wNafPreCompInfoPrecompute.getPreCompNeg() : wNafPreCompInfoPrecompute.getPreComp(), z ? wNafPreCompInfoPrecompute.getPreComp() : wNafPreCompInfoPrecompute.getPreCompNeg(), WNafUtil.generateWindowNaf(iMin, bigIntegerAbs), z2 ? wNafPreCompInfoPrecompute2.getPreCompNeg() : wNafPreCompInfoPrecompute2.getPreComp(), z2 ? wNafPreCompInfoPrecompute2.getPreComp() : wNafPreCompInfoPrecompute2.getPreCompNeg(), WNafUtil.generateWindowNaf(iMin2, bigIntegerAbs2));
    }

    static ECPoint implShamirsTrickWNaf(ECEndomorphism eCEndomorphism, ECPoint eCPoint, BigInteger bigInteger, BigInteger bigInteger2) {
        boolean z = bigInteger.signum() < 0;
        boolean z2 = bigInteger2.signum() < 0;
        BigInteger bigIntegerAbs = bigInteger.abs();
        BigInteger bigIntegerAbs2 = bigInteger2.abs();
        WNafPreCompInfo wNafPreCompInfoPrecompute = WNafUtil.precompute(eCPoint, WNafUtil.getWindowSize(Math.max(bigIntegerAbs.bitLength(), bigIntegerAbs2.bitLength()), 8), true);
        WNafPreCompInfo wNafPreCompInfoPrecomputeWithPointMap = WNafUtil.precomputeWithPointMap(EndoUtil.mapPoint(eCEndomorphism, eCPoint), eCEndomorphism.getPointMap(), wNafPreCompInfoPrecompute, true);
        int iMin = Math.min(8, wNafPreCompInfoPrecompute.getWidth());
        int iMin2 = Math.min(8, wNafPreCompInfoPrecomputeWithPointMap.getWidth());
        return implShamirsTrickWNaf(z ? wNafPreCompInfoPrecompute.getPreCompNeg() : wNafPreCompInfoPrecompute.getPreComp(), z ? wNafPreCompInfoPrecompute.getPreComp() : wNafPreCompInfoPrecompute.getPreCompNeg(), WNafUtil.generateWindowNaf(iMin, bigIntegerAbs), z2 ? wNafPreCompInfoPrecomputeWithPointMap.getPreCompNeg() : wNafPreCompInfoPrecomputeWithPointMap.getPreComp(), z2 ? wNafPreCompInfoPrecomputeWithPointMap.getPreComp() : wNafPreCompInfoPrecomputeWithPointMap.getPreCompNeg(), WNafUtil.generateWindowNaf(iMin2, bigIntegerAbs2));
    }

    private static ECPoint implShamirsTrickWNaf(ECPoint[] eCPointArr, ECPoint[] eCPointArr2, byte[] bArr, ECPoint[] eCPointArr3, ECPoint[] eCPointArr4, byte[] bArr2) {
        int iMax = Math.max(bArr.length, bArr2.length);
        ECPoint infinity = eCPointArr[0].getCurve().getInfinity();
        ECPoint eCPointTimesPow2 = infinity;
        int i = 0;
        int i2 = iMax - 1;
        while (i2 >= 0) {
            byte b = i2 < bArr.length ? bArr[i2] : (byte) 0;
            byte b2 = i2 < bArr2.length ? bArr2[i2] : (byte) 0;
            if ((b | b2) == 0) {
                i++;
            } else {
                ECPoint eCPointAdd = infinity;
                if (b != 0) {
                    eCPointAdd = eCPointAdd.add((b < 0 ? eCPointArr2 : eCPointArr)[Math.abs((int) b) >>> 1]);
                }
                if (b2 != 0) {
                    eCPointAdd = eCPointAdd.add((b2 < 0 ? eCPointArr4 : eCPointArr3)[Math.abs((int) b2) >>> 1]);
                }
                if (i > 0) {
                    eCPointTimesPow2 = eCPointTimesPow2.timesPow2(i);
                    i = 0;
                }
                eCPointTimesPow2 = eCPointTimesPow2.twicePlus(eCPointAdd);
            }
            i2--;
        }
        if (i > 0) {
            eCPointTimesPow2 = eCPointTimesPow2.timesPow2(i);
        }
        return eCPointTimesPow2;
    }

    static ECPoint implSumOfMultiplies(ECPoint[] eCPointArr, BigInteger[] bigIntegerArr) {
        int length = eCPointArr.length;
        boolean[] zArr = new boolean[length];
        WNafPreCompInfo[] wNafPreCompInfoArr = new WNafPreCompInfo[length];
        byte[][] bArr = new byte[length][];
        for (int i = 0; i < length; i++) {
            BigInteger bigInteger = bigIntegerArr[i];
            zArr[i] = bigInteger.signum() < 0;
            BigInteger bigIntegerAbs = bigInteger.abs();
            WNafPreCompInfo wNafPreCompInfoPrecompute = WNafUtil.precompute(eCPointArr[i], WNafUtil.getWindowSize(bigIntegerAbs.bitLength(), 8), true);
            int iMin = Math.min(8, wNafPreCompInfoPrecompute.getWidth());
            wNafPreCompInfoArr[i] = wNafPreCompInfoPrecompute;
            bArr[i] = WNafUtil.generateWindowNaf(iMin, bigIntegerAbs);
        }
        return implSumOfMultiplies(zArr, wNafPreCompInfoArr, bArr);
    }

    static ECPoint implSumOfMultipliesGLV(ECPoint[] eCPointArr, BigInteger[] bigIntegerArr, GLVEndomorphism gLVEndomorphism) {
        BigInteger order = eCPointArr[0].getCurve().getOrder();
        int length = eCPointArr.length;
        BigInteger[] bigIntegerArr2 = new BigInteger[length << 1];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            BigInteger[] bigIntegerArrDecomposeScalar = gLVEndomorphism.decomposeScalar(bigIntegerArr[i2].mod(order));
            int i3 = i;
            int i4 = i + 1;
            bigIntegerArr2[i3] = bigIntegerArrDecomposeScalar[0];
            i = i4 + 1;
            bigIntegerArr2[i4] = bigIntegerArrDecomposeScalar[1];
        }
        if (gLVEndomorphism.hasEfficientPointMap()) {
            return implSumOfMultiplies(gLVEndomorphism, eCPointArr, bigIntegerArr2);
        }
        ECPoint[] eCPointArr2 = new ECPoint[length << 1];
        int i5 = 0;
        for (ECPoint eCPoint : eCPointArr) {
            ECPoint eCPointMapPoint = EndoUtil.mapPoint(gLVEndomorphism, eCPoint);
            int i6 = i5;
            int i7 = i5 + 1;
            eCPointArr2[i6] = eCPoint;
            i5 = i7 + 1;
            eCPointArr2[i7] = eCPointMapPoint;
        }
        return implSumOfMultiplies(eCPointArr2, bigIntegerArr2);
    }

    static ECPoint implSumOfMultiplies(ECEndomorphism eCEndomorphism, ECPoint[] eCPointArr, BigInteger[] bigIntegerArr) {
        int length = eCPointArr.length;
        int i = length << 1;
        boolean[] zArr = new boolean[i];
        WNafPreCompInfo[] wNafPreCompInfoArr = new WNafPreCompInfo[i];
        byte[][] bArr = new byte[i][];
        ECPointMap pointMap = eCEndomorphism.getPointMap();
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 << 1;
            int i4 = i3 + 1;
            BigInteger bigInteger = bigIntegerArr[i3];
            zArr[i3] = bigInteger.signum() < 0;
            BigInteger bigIntegerAbs = bigInteger.abs();
            BigInteger bigInteger2 = bigIntegerArr[i4];
            zArr[i4] = bigInteger2.signum() < 0;
            BigInteger bigIntegerAbs2 = bigInteger2.abs();
            int windowSize = WNafUtil.getWindowSize(Math.max(bigIntegerAbs.bitLength(), bigIntegerAbs2.bitLength()), 8);
            ECPoint eCPoint = eCPointArr[i2];
            WNafPreCompInfo wNafPreCompInfoPrecompute = WNafUtil.precompute(eCPoint, windowSize, true);
            WNafPreCompInfo wNafPreCompInfoPrecomputeWithPointMap = WNafUtil.precomputeWithPointMap(EndoUtil.mapPoint(eCEndomorphism, eCPoint), pointMap, wNafPreCompInfoPrecompute, true);
            int iMin = Math.min(8, wNafPreCompInfoPrecompute.getWidth());
            int iMin2 = Math.min(8, wNafPreCompInfoPrecomputeWithPointMap.getWidth());
            wNafPreCompInfoArr[i3] = wNafPreCompInfoPrecompute;
            wNafPreCompInfoArr[i4] = wNafPreCompInfoPrecomputeWithPointMap;
            bArr[i3] = WNafUtil.generateWindowNaf(iMin, bigIntegerAbs);
            bArr[i4] = WNafUtil.generateWindowNaf(iMin2, bigIntegerAbs2);
        }
        return implSumOfMultiplies(zArr, wNafPreCompInfoArr, bArr);
    }

    private static ECPoint implSumOfMultiplies(boolean[] zArr, WNafPreCompInfo[] wNafPreCompInfoArr, byte[][] bArr) {
        int iMax = 0;
        int length = bArr.length;
        for (byte[] bArr2 : bArr) {
            iMax = Math.max(iMax, bArr2.length);
        }
        ECPoint infinity = wNafPreCompInfoArr[0].getPreComp()[0].getCurve().getInfinity();
        ECPoint eCPointTimesPow2 = infinity;
        int i = 0;
        int i2 = iMax - 1;
        while (i2 >= 0) {
            ECPoint eCPointAdd = infinity;
            for (int i3 = 0; i3 < length; i3++) {
                byte[] bArr3 = bArr[i3];
                byte b = i2 < bArr3.length ? bArr3[i2] : (byte) 0;
                if (b != 0) {
                    int iAbs = Math.abs((int) b);
                    WNafPreCompInfo wNafPreCompInfo = wNafPreCompInfoArr[i3];
                    eCPointAdd = eCPointAdd.add(((b < 0) == zArr[i3] ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg())[iAbs >>> 1]);
                }
            }
            if (eCPointAdd == infinity) {
                i++;
            } else {
                if (i > 0) {
                    eCPointTimesPow2 = eCPointTimesPow2.timesPow2(i);
                    i = 0;
                }
                eCPointTimesPow2 = eCPointTimesPow2.twicePlus(eCPointAdd);
            }
            i2--;
        }
        if (i > 0) {
            eCPointTimesPow2 = eCPointTimesPow2.timesPow2(i);
        }
        return eCPointTimesPow2;
    }

    private static ECPoint implShamirsTrickFixedPoint(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        ECCurve curve = eCPoint.getCurve();
        int combSize = FixedPointUtil.getCombSize(curve);
        if (bigInteger.bitLength() > combSize || bigInteger2.bitLength() > combSize) {
            throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
        }
        FixedPointPreCompInfo fixedPointPreCompInfoPrecompute = FixedPointUtil.precompute(eCPoint);
        FixedPointPreCompInfo fixedPointPreCompInfoPrecompute2 = FixedPointUtil.precompute(eCPoint2);
        ECLookupTable lookupTable = fixedPointPreCompInfoPrecompute.getLookupTable();
        ECLookupTable lookupTable2 = fixedPointPreCompInfoPrecompute2.getLookupTable();
        int width = fixedPointPreCompInfoPrecompute.getWidth();
        if (width != fixedPointPreCompInfoPrecompute2.getWidth()) {
            FixedPointCombMultiplier fixedPointCombMultiplier = new FixedPointCombMultiplier();
            return fixedPointCombMultiplier.multiply(eCPoint, bigInteger).add(fixedPointCombMultiplier.multiply(eCPoint2, bigInteger2));
        }
        int i = ((combSize + width) - 1) / width;
        ECPoint infinity = curve.getInfinity();
        int i2 = i * width;
        int[] iArrFromBigInteger = Nat.fromBigInteger(i2, bigInteger);
        int[] iArrFromBigInteger2 = Nat.fromBigInteger(i2, bigInteger2);
        int i3 = i2 - 1;
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = 0;
            int i6 = 0;
            int i7 = i3;
            int i8 = i4;
            while (true) {
                int i9 = i7 - i8;
                if (i9 >= 0) {
                    int i10 = iArrFromBigInteger[i9 >>> 5] >>> (i9 & 31);
                    i5 = ((i5 ^ (i10 >>> 1)) << 1) ^ i10;
                    int i11 = iArrFromBigInteger2[i9 >>> 5] >>> (i9 & 31);
                    i6 = ((i6 ^ (i11 >>> 1)) << 1) ^ i11;
                    i7 = i9;
                    i8 = i;
                }
            }
            infinity = infinity.twicePlus(lookupTable.lookupVar(i5).add(lookupTable2.lookupVar(i6)));
        }
        return infinity.add(fixedPointPreCompInfoPrecompute.getOffset()).add(fixedPointPreCompInfoPrecompute2.getOffset());
    }
}
