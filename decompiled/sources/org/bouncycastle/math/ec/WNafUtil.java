package org.bouncycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/WNafUtil.class */
public abstract class WNafUtil {
    public static final String PRECOMP_NAME = "bc_wnaf";
    private static final int MAX_WIDTH = 16;
    private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = {13, 41, 121, 337, 897, 2305};
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final int[] EMPTY_INTS = new int[0];
    private static final ECPoint[] EMPTY_POINTS = new ECPoint[0];

    public static void configureBasepoint(ECPoint eCPoint) {
        ECCurve curve = eCPoint.getCurve();
        if (null == curve) {
            return;
        }
        BigInteger order = curve.getOrder();
        final int iMin = Math.min(16, getWindowSize(null == order ? curve.getFieldSize() + 1 : order.bitLength()) + 3);
        curve.precompute(eCPoint, "bc_wnaf", new PreCompCallback() { // from class: org.bouncycastle.math.ec.WNafUtil.1
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                WNafPreCompInfo wNafPreCompInfo = preCompInfo instanceof WNafPreCompInfo ? (WNafPreCompInfo) preCompInfo : null;
                if (null != wNafPreCompInfo && wNafPreCompInfo.getConfWidth() == iMin) {
                    wNafPreCompInfo.setPromotionCountdown(0);
                    return wNafPreCompInfo;
                }
                WNafPreCompInfo wNafPreCompInfo2 = new WNafPreCompInfo();
                wNafPreCompInfo2.setPromotionCountdown(0);
                wNafPreCompInfo2.setConfWidth(iMin);
                if (null != wNafPreCompInfo) {
                    wNafPreCompInfo2.setPreComp(wNafPreCompInfo.getPreComp());
                    wNafPreCompInfo2.setPreCompNeg(wNafPreCompInfo.getPreCompNeg());
                    wNafPreCompInfo2.setTwice(wNafPreCompInfo.getTwice());
                    wNafPreCompInfo2.setWidth(wNafPreCompInfo.getWidth());
                }
                return wNafPreCompInfo2;
            }
        });
    }

    public static int[] generateCompactNaf(BigInteger bigInteger) {
        if ((bigInteger.bitLength() >>> 16) != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        }
        BigInteger bigIntegerAdd = bigInteger.shiftLeft(1).add(bigInteger);
        int iBitLength = bigIntegerAdd.bitLength();
        int[] iArrTrim = new int[iBitLength >> 1];
        BigInteger bigIntegerXor = bigIntegerAdd.xor(bigInteger);
        int i = iBitLength - 1;
        int i2 = 0;
        int i3 = 0;
        int i4 = 1;
        while (i4 < i) {
            if (bigIntegerXor.testBit(i4)) {
                int i5 = i2;
                i2++;
                iArrTrim[i5] = ((bigInteger.testBit(i4) ? -1 : 1) << 16) | i3;
                i3 = 1;
                i4++;
            } else {
                i3++;
            }
            i4++;
        }
        int i6 = i2;
        int i7 = i2 + 1;
        iArrTrim[i6] = 65536 | i3;
        if (iArrTrim.length > i7) {
            iArrTrim = trim(iArrTrim, i7);
        }
        return iArrTrim;
    }

    public static int[] generateCompactWindowNaf(int i, BigInteger bigInteger) {
        if (i == 2) {
            return generateCompactNaf(bigInteger);
        }
        if (i < 2 || i > 16) {
            throw new IllegalArgumentException("'width' must be in the range [2, 16]");
        }
        if ((bigInteger.bitLength() >>> 16) != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        }
        int[] iArrTrim = new int[(bigInteger.bitLength() / i) + 1];
        int i2 = 1 << i;
        int i3 = i2 - 1;
        int i4 = i2 >>> 1;
        boolean z = false;
        int i5 = 0;
        int i6 = 0;
        while (i6 <= bigInteger.bitLength()) {
            if (bigInteger.testBit(i6) == z) {
                i6++;
            } else {
                bigInteger = bigInteger.shiftRight(i6);
                int iIntValue = bigInteger.intValue() & i3;
                if (z) {
                    iIntValue++;
                }
                z = (iIntValue & i4) != 0;
                if (z) {
                    iIntValue -= i2;
                }
                int i7 = i5 > 0 ? i6 - 1 : i6;
                int i8 = i5;
                i5++;
                iArrTrim[i8] = (iIntValue << 16) | i7;
                i6 = i;
            }
        }
        if (iArrTrim.length > i5) {
            iArrTrim = trim(iArrTrim, i5);
        }
        return iArrTrim;
    }

    public static byte[] generateJSF(BigInteger bigInteger, BigInteger bigInteger2) {
        byte[] bArrTrim = new byte[Math.max(bigInteger.bitLength(), bigInteger2.bitLength()) + 1];
        BigInteger bigIntegerShiftRight = bigInteger;
        BigInteger bigIntegerShiftRight2 = bigInteger2;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if ((i2 | i3) == 0 && bigIntegerShiftRight.bitLength() <= i4 && bigIntegerShiftRight2.bitLength() <= i4) {
                break;
            }
            int iIntValue = ((bigIntegerShiftRight.intValue() >>> i4) + i2) & 7;
            int iIntValue2 = ((bigIntegerShiftRight2.intValue() >>> i4) + i3) & 7;
            int i5 = iIntValue & 1;
            if (i5 != 0) {
                i5 -= iIntValue & 2;
                if (iIntValue + i5 == 4 && (iIntValue2 & 3) == 2) {
                    i5 = -i5;
                }
            }
            int i6 = iIntValue2 & 1;
            if (i6 != 0) {
                i6 -= iIntValue2 & 2;
                if (iIntValue2 + i6 == 4 && (iIntValue & 3) == 2) {
                    i6 = -i6;
                }
            }
            if ((i2 << 1) == 1 + i5) {
                i2 ^= 1;
            }
            if ((i3 << 1) == 1 + i6) {
                i3 ^= 1;
            }
            i4++;
            if (i4 == 30) {
                i4 = 0;
                bigIntegerShiftRight = bigIntegerShiftRight.shiftRight(30);
                bigIntegerShiftRight2 = bigIntegerShiftRight2.shiftRight(30);
            }
            int i7 = i;
            i++;
            bArrTrim[i7] = (byte) ((i5 << 4) | (i6 & 15));
        }
        if (bArrTrim.length > i) {
            bArrTrim = trim(bArrTrim, i);
        }
        return bArrTrim;
    }

    public static byte[] generateNaf(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        }
        BigInteger bigIntegerAdd = bigInteger.shiftLeft(1).add(bigInteger);
        int iBitLength = bigIntegerAdd.bitLength() - 1;
        byte[] bArr = new byte[iBitLength];
        BigInteger bigIntegerXor = bigIntegerAdd.xor(bigInteger);
        int i = 1;
        while (i < iBitLength) {
            if (bigIntegerXor.testBit(i)) {
                bArr[i - 1] = (byte) (bigInteger.testBit(i) ? -1 : 1);
                i++;
            }
            i++;
        }
        bArr[iBitLength - 1] = 1;
        return bArr;
    }

    public static byte[] generateWindowNaf(int i, BigInteger bigInteger) {
        if (i == 2) {
            return generateNaf(bigInteger);
        }
        if (i < 2 || i > 8) {
            throw new IllegalArgumentException("'width' must be in the range [2, 8]");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        }
        byte[] bArrTrim = new byte[bigInteger.bitLength() + 1];
        int i2 = 1 << i;
        int i3 = i2 - 1;
        int i4 = i2 >>> 1;
        boolean z = false;
        int i5 = 0;
        int i6 = 0;
        while (i6 <= bigInteger.bitLength()) {
            if (bigInteger.testBit(i6) == z) {
                i6++;
            } else {
                bigInteger = bigInteger.shiftRight(i6);
                int iIntValue = bigInteger.intValue() & i3;
                if (z) {
                    iIntValue++;
                }
                z = (iIntValue & i4) != 0;
                if (z) {
                    iIntValue -= i2;
                }
                int i7 = i5 + (i5 > 0 ? i6 - 1 : i6);
                i5 = i7 + 1;
                bArrTrim[i7] = (byte) iIntValue;
                i6 = i;
            }
        }
        if (bArrTrim.length > i5) {
            bArrTrim = trim(bArrTrim, i5);
        }
        return bArrTrim;
    }

    public static int getNafWeight(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return 0;
        }
        return bigInteger.shiftLeft(1).add(bigInteger).xor(bigInteger).bitCount();
    }

    public static WNafPreCompInfo getWNafPreCompInfo(ECPoint eCPoint) {
        return getWNafPreCompInfo(eCPoint.getCurve().getPreCompInfo(eCPoint, "bc_wnaf"));
    }

    public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo instanceof WNafPreCompInfo) {
            return (WNafPreCompInfo) preCompInfo;
        }
        return null;
    }

    public static int getWindowSize(int i) {
        return getWindowSize(i, DEFAULT_WINDOW_SIZE_CUTOFFS, 16);
    }

    public static int getWindowSize(int i, int i2) {
        return getWindowSize(i, DEFAULT_WINDOW_SIZE_CUTOFFS, i2);
    }

    public static int getWindowSize(int i, int[] iArr) {
        return getWindowSize(i, iArr, 16);
    }

    public static int getWindowSize(int i, int[] iArr, int i2) {
        int i3 = 0;
        while (i3 < iArr.length && i >= iArr[i3]) {
            i3++;
        }
        return Math.max(2, Math.min(i2, i3 + 2));
    }

    public static WNafPreCompInfo precompute(final ECPoint eCPoint, final int i, final boolean z) {
        final ECCurve curve = eCPoint.getCurve();
        return (WNafPreCompInfo) curve.precompute(eCPoint, "bc_wnaf", new PreCompCallback() { // from class: org.bouncycastle.math.ec.WNafUtil.2
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                int length;
                WNafPreCompInfo wNafPreCompInfo = preCompInfo instanceof WNafPreCompInfo ? (WNafPreCompInfo) preCompInfo : null;
                int iMax = Math.max(2, Math.min(16, i));
                if (checkExisting(wNafPreCompInfo, iMax, 1 << (iMax - 2), z)) {
                    wNafPreCompInfo.decrementPromotionCountdown();
                    return wNafPreCompInfo;
                }
                WNafPreCompInfo wNafPreCompInfo2 = new WNafPreCompInfo();
                ECPoint[] eCPointArrResizeTable = null;
                ECPoint[] eCPointArrResizeTable2 = null;
                ECPoint twice = null;
                if (null != wNafPreCompInfo) {
                    wNafPreCompInfo2.setPromotionCountdown(wNafPreCompInfo.decrementPromotionCountdown());
                    wNafPreCompInfo2.setConfWidth(wNafPreCompInfo.getConfWidth());
                    eCPointArrResizeTable = wNafPreCompInfo.getPreComp();
                    eCPointArrResizeTable2 = wNafPreCompInfo.getPreCompNeg();
                    twice = wNafPreCompInfo.getTwice();
                }
                int iMin = Math.min(16, Math.max(wNafPreCompInfo2.getConfWidth(), iMax));
                int i2 = 1 << (iMin - 2);
                int length2 = 0;
                if (null == eCPointArrResizeTable) {
                    eCPointArrResizeTable = WNafUtil.EMPTY_POINTS;
                } else {
                    length2 = eCPointArrResizeTable.length;
                }
                if (length2 < i2) {
                    eCPointArrResizeTable = WNafUtil.resizeTable(eCPointArrResizeTable, i2);
                    if (i2 == 1) {
                        eCPointArrResizeTable[0] = eCPoint.normalize();
                    } else {
                        int i3 = length2;
                        if (i3 == 0) {
                            eCPointArrResizeTable[0] = eCPoint;
                            i3 = 1;
                        }
                        ECFieldElement zCoord = null;
                        if (i2 == 2) {
                            eCPointArrResizeTable[1] = eCPoint.threeTimes();
                        } else {
                            ECPoint eCPointTwice = twice;
                            ECPoint eCPointScaleY = eCPointArrResizeTable[i3 - 1];
                            if (null == eCPointTwice) {
                                eCPointTwice = eCPointArrResizeTable[0].twice();
                                twice = eCPointTwice;
                                if (!twice.isInfinity() && ECAlgorithms.isFpCurve(curve) && curve.getFieldSize() >= 64) {
                                    switch (curve.getCoordinateSystem()) {
                                        case 2:
                                        case 3:
                                        case 4:
                                            zCoord = twice.getZCoord(0);
                                            eCPointTwice = curve.createPoint(twice.getXCoord().toBigInteger(), twice.getYCoord().toBigInteger());
                                            ECFieldElement eCFieldElementSquare = zCoord.square();
                                            eCPointScaleY = eCPointScaleY.scaleX(eCFieldElementSquare).scaleY(eCFieldElementSquare.multiply(zCoord));
                                            if (length2 == 0) {
                                                eCPointArrResizeTable[0] = eCPointScaleY;
                                            }
                                            break;
                                    }
                                }
                            }
                            while (i3 < i2) {
                                int i4 = i3;
                                i3++;
                                ECPoint eCPointAdd = eCPointScaleY.add(eCPointTwice);
                                eCPointScaleY = eCPointAdd;
                                eCPointArrResizeTable[i4] = eCPointAdd;
                            }
                        }
                        curve.normalizeAll(eCPointArrResizeTable, length2, i2 - length2, zCoord);
                    }
                }
                if (z) {
                    if (null == eCPointArrResizeTable2) {
                        length = 0;
                        eCPointArrResizeTable2 = new ECPoint[i2];
                    } else {
                        length = eCPointArrResizeTable2.length;
                        if (length < i2) {
                            eCPointArrResizeTable2 = WNafUtil.resizeTable(eCPointArrResizeTable2, i2);
                        }
                    }
                    while (length < i2) {
                        eCPointArrResizeTable2[length] = eCPointArrResizeTable[length].negate();
                        length++;
                    }
                }
                wNafPreCompInfo2.setPreComp(eCPointArrResizeTable);
                wNafPreCompInfo2.setPreCompNeg(eCPointArrResizeTable2);
                wNafPreCompInfo2.setTwice(twice);
                wNafPreCompInfo2.setWidth(iMin);
                return wNafPreCompInfo2;
            }

            private boolean checkExisting(WNafPreCompInfo wNafPreCompInfo, int i2, int i3, boolean z2) {
                return null != wNafPreCompInfo && wNafPreCompInfo.getWidth() >= Math.max(wNafPreCompInfo.getConfWidth(), i2) && checkTable(wNafPreCompInfo.getPreComp(), i3) && (!z2 || checkTable(wNafPreCompInfo.getPreCompNeg(), i3));
            }

            private boolean checkTable(ECPoint[] eCPointArr, int i2) {
                return null != eCPointArr && eCPointArr.length >= i2;
            }
        });
    }

    public static WNafPreCompInfo precomputeWithPointMap(ECPoint eCPoint, final ECPointMap eCPointMap, final WNafPreCompInfo wNafPreCompInfo, final boolean z) {
        return (WNafPreCompInfo) eCPoint.getCurve().precompute(eCPoint, "bc_wnaf", new PreCompCallback() { // from class: org.bouncycastle.math.ec.WNafUtil.3
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                WNafPreCompInfo wNafPreCompInfo2 = preCompInfo instanceof WNafPreCompInfo ? (WNafPreCompInfo) preCompInfo : null;
                int width = wNafPreCompInfo.getWidth();
                if (checkExisting(wNafPreCompInfo2, width, wNafPreCompInfo.getPreComp().length, z)) {
                    wNafPreCompInfo2.decrementPromotionCountdown();
                    return wNafPreCompInfo2;
                }
                WNafPreCompInfo wNafPreCompInfo3 = new WNafPreCompInfo();
                wNafPreCompInfo3.setPromotionCountdown(wNafPreCompInfo.getPromotionCountdown());
                ECPoint twice = wNafPreCompInfo.getTwice();
                if (null != twice) {
                    wNafPreCompInfo3.setTwice(eCPointMap.map(twice));
                }
                ECPoint[] preComp = wNafPreCompInfo.getPreComp();
                ECPoint[] eCPointArr = new ECPoint[preComp.length];
                for (int i = 0; i < preComp.length; i++) {
                    eCPointArr[i] = eCPointMap.map(preComp[i]);
                }
                wNafPreCompInfo3.setPreComp(eCPointArr);
                wNafPreCompInfo3.setWidth(width);
                if (z) {
                    ECPoint[] eCPointArr2 = new ECPoint[eCPointArr.length];
                    for (int i2 = 0; i2 < eCPointArr2.length; i2++) {
                        eCPointArr2[i2] = eCPointArr[i2].negate();
                    }
                    wNafPreCompInfo3.setPreCompNeg(eCPointArr2);
                }
                return wNafPreCompInfo3;
            }

            private boolean checkExisting(WNafPreCompInfo wNafPreCompInfo2, int i, int i2, boolean z2) {
                return null != wNafPreCompInfo2 && wNafPreCompInfo2.getWidth() >= i && checkTable(wNafPreCompInfo2.getPreComp(), i2) && (!z2 || checkTable(wNafPreCompInfo2.getPreCompNeg(), i2));
            }

            private boolean checkTable(ECPoint[] eCPointArr, int i) {
                return null != eCPointArr && eCPointArr.length >= i;
            }
        });
    }

    private static byte[] trim(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        return bArr2;
    }

    private static int[] trim(int[] iArr, int i) {
        int[] iArr2 = new int[i];
        System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        return iArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ECPoint[] resizeTable(ECPoint[] eCPointArr, int i) {
        ECPoint[] eCPointArr2 = new ECPoint[i];
        System.arraycopy(eCPointArr, 0, eCPointArr2, 0, eCPointArr.length);
        return eCPointArr2;
    }
}
