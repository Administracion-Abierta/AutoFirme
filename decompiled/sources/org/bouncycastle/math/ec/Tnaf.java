package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/Tnaf.class */
class Tnaf {
    public static final byte WIDTH = 4;
    private static final BigInteger MINUS_ONE = ECConstants.ONE.negate();
    private static final BigInteger MINUS_TWO = ECConstants.TWO.negate();
    private static final BigInteger MINUS_THREE = ECConstants.THREE.negate();
    public static final ZTauElement[] alpha0 = {null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, MINUS_ONE), null, new ZTauElement(ECConstants.ONE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.THREE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ZERO)};
    public static final byte[][] alpha0Tnaf = {null, new byte[]{1}, null, new byte[]{-1, 0, 1}, null, new byte[]{1, 0, 1}, null, new byte[]{-1, 0, 0, 1}};
    public static final ZTauElement[] alpha1 = {null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, MINUS_ONE), null, new ZTauElement(ECConstants.ONE, MINUS_ONE), null, new ZTauElement(ECConstants.THREE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ZERO)};
    public static final byte[][] alpha1Tnaf = {null, new byte[]{1}, null, new byte[]{-1, 0, 1}, null, new byte[]{1, 0, 1}, null, new byte[]{-1, 0, 0, -1}};

    Tnaf() {
    }

    public static BigInteger norm(byte b, ZTauElement zTauElement) {
        BigInteger bigIntegerMultiply = zTauElement.u.multiply(zTauElement.u);
        if (b == 1) {
            return zTauElement.v.shiftLeft(1).add(zTauElement.u).multiply(zTauElement.v).add(bigIntegerMultiply);
        }
        if (b == -1) {
            return zTauElement.v.shiftLeft(1).subtract(zTauElement.u).multiply(zTauElement.v).add(bigIntegerMultiply);
        }
        throw new IllegalArgumentException("mu must be 1 or -1");
    }

    public static SimpleBigDecimal norm(byte b, SimpleBigDecimal simpleBigDecimal, SimpleBigDecimal simpleBigDecimal2) {
        SimpleBigDecimal simpleBigDecimalAdd;
        SimpleBigDecimal simpleBigDecimalMultiply = simpleBigDecimal.multiply(simpleBigDecimal);
        SimpleBigDecimal simpleBigDecimalMultiply2 = simpleBigDecimal.multiply(simpleBigDecimal2);
        SimpleBigDecimal simpleBigDecimalShiftLeft = simpleBigDecimal2.multiply(simpleBigDecimal2).shiftLeft(1);
        if (b == 1) {
            simpleBigDecimalAdd = simpleBigDecimalMultiply.add(simpleBigDecimalMultiply2).add(simpleBigDecimalShiftLeft);
        } else {
            if (b != -1) {
                throw new IllegalArgumentException("mu must be 1 or -1");
            }
            simpleBigDecimalAdd = simpleBigDecimalMultiply.subtract(simpleBigDecimalMultiply2).add(simpleBigDecimalShiftLeft);
        }
        return simpleBigDecimalAdd;
    }

    public static ZTauElement round(SimpleBigDecimal simpleBigDecimal, SimpleBigDecimal simpleBigDecimal2, byte b) {
        SimpleBigDecimal simpleBigDecimalAdd;
        SimpleBigDecimal simpleBigDecimalSubtract;
        if (simpleBigDecimal2.getScale() != simpleBigDecimal.getScale()) {
            throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
        }
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger bigIntegerRound = simpleBigDecimal.round();
        BigInteger bigIntegerRound2 = simpleBigDecimal2.round();
        SimpleBigDecimal simpleBigDecimalSubtract2 = simpleBigDecimal.subtract(bigIntegerRound);
        SimpleBigDecimal simpleBigDecimalSubtract3 = simpleBigDecimal2.subtract(bigIntegerRound2);
        SimpleBigDecimal simpleBigDecimalAdd2 = simpleBigDecimalSubtract2.add(simpleBigDecimalSubtract2);
        SimpleBigDecimal simpleBigDecimalAdd3 = b == 1 ? simpleBigDecimalAdd2.add(simpleBigDecimalSubtract3) : simpleBigDecimalAdd2.subtract(simpleBigDecimalSubtract3);
        SimpleBigDecimal simpleBigDecimalAdd4 = simpleBigDecimalSubtract3.add(simpleBigDecimalSubtract3).add(simpleBigDecimalSubtract3);
        SimpleBigDecimal simpleBigDecimalAdd5 = simpleBigDecimalAdd4.add(simpleBigDecimalSubtract3);
        if (b == 1) {
            simpleBigDecimalAdd = simpleBigDecimalSubtract2.subtract(simpleBigDecimalAdd4);
            simpleBigDecimalSubtract = simpleBigDecimalSubtract2.add(simpleBigDecimalAdd5);
        } else {
            simpleBigDecimalAdd = simpleBigDecimalSubtract2.add(simpleBigDecimalAdd4);
            simpleBigDecimalSubtract = simpleBigDecimalSubtract2.subtract(simpleBigDecimalAdd5);
        }
        int i = 0;
        byte b2 = 0;
        if (simpleBigDecimalAdd3.compareTo(ECConstants.ONE) >= 0) {
            if (simpleBigDecimalAdd.compareTo(MINUS_ONE) < 0) {
                b2 = b;
            } else {
                i = 1;
            }
        } else if (simpleBigDecimalSubtract.compareTo(ECConstants.TWO) >= 0) {
            b2 = b;
        }
        if (simpleBigDecimalAdd3.compareTo(MINUS_ONE) < 0) {
            if (simpleBigDecimalAdd.compareTo(ECConstants.ONE) >= 0) {
                b2 = (byte) (-b);
            } else {
                i = -1;
            }
        } else if (simpleBigDecimalSubtract.compareTo(MINUS_TWO) < 0) {
            b2 = (byte) (-b);
        }
        return new ZTauElement(bigIntegerRound.add(BigInteger.valueOf(i)), bigIntegerRound2.add(BigInteger.valueOf(b2)));
    }

    public static SimpleBigDecimal approximateDivisionByN(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, byte b, int i, int i2) {
        int i3 = ((i + 5) / 2) + i2;
        BigInteger bigIntegerMultiply = bigInteger2.multiply(bigInteger.shiftRight(((i - i3) - 2) + b));
        BigInteger bigIntegerAdd = bigIntegerMultiply.add(bigInteger3.multiply(bigIntegerMultiply.shiftRight(i)));
        BigInteger bigIntegerShiftRight = bigIntegerAdd.shiftRight(i3 - i2);
        if (bigIntegerAdd.testBit((i3 - i2) - 1)) {
            bigIntegerShiftRight = bigIntegerShiftRight.add(ECConstants.ONE);
        }
        return new SimpleBigDecimal(bigIntegerShiftRight, i2);
    }

    public static byte[] tauAdicNaf(byte b, ZTauElement zTauElement) {
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        int iBitLength = norm(b, zTauElement).bitLength();
        byte[] bArr = new byte[iBitLength > 30 ? iBitLength + 4 : 34];
        int i = 0;
        int i2 = 0;
        BigInteger bigIntegerAdd = zTauElement.u;
        BigInteger bigIntegerNegate = zTauElement.v;
        while (true) {
            if (bigIntegerAdd.equals(ECConstants.ZERO) && bigIntegerNegate.equals(ECConstants.ZERO)) {
                int i3 = i2 + 1;
                byte[] bArr2 = new byte[i3];
                System.arraycopy(bArr, 0, bArr2, 0, i3);
                return bArr2;
            }
            if (bigIntegerAdd.testBit(0)) {
                bArr[i] = (byte) ECConstants.TWO.subtract(bigIntegerAdd.subtract(bigIntegerNegate.shiftLeft(1)).mod(ECConstants.FOUR)).intValue();
                bigIntegerAdd = bArr[i] == 1 ? bigIntegerAdd.clearBit(0) : bigIntegerAdd.add(ECConstants.ONE);
                i2 = i;
            } else {
                bArr[i] = 0;
            }
            BigInteger bigInteger = bigIntegerAdd;
            BigInteger bigIntegerShiftRight = bigIntegerAdd.shiftRight(1);
            bigIntegerAdd = b == 1 ? bigIntegerNegate.add(bigIntegerShiftRight) : bigIntegerNegate.subtract(bigIntegerShiftRight);
            bigIntegerNegate = bigInteger.shiftRight(1).negate();
            i++;
        }
    }

    public static ECPoint.AbstractF2m tau(ECPoint.AbstractF2m abstractF2m) {
        return abstractF2m.tau();
    }

    public static byte getMu(ECCurve.AbstractF2m abstractF2m) {
        if (abstractF2m.isKoblitz()) {
            return abstractF2m.getA().isZero() ? (byte) -1 : (byte) 1;
        }
        throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible");
    }

    public static byte getMu(ECFieldElement eCFieldElement) {
        return (byte) (eCFieldElement.isZero() ? -1 : 1);
    }

    public static byte getMu(int i) {
        return (byte) (i == 0 ? -1 : 1);
    }

    public static BigInteger[] getLucas(byte b, int i, boolean z) {
        BigInteger bigInteger;
        BigInteger bigIntegerValueOf;
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        if (z) {
            bigInteger = ECConstants.TWO;
            bigIntegerValueOf = BigInteger.valueOf(b);
        } else {
            bigInteger = ECConstants.ZERO;
            bigIntegerValueOf = ECConstants.ONE;
        }
        for (int i2 = 1; i2 < i; i2++) {
            BigInteger bigIntegerNegate = bigIntegerValueOf;
            if (b < 0) {
                bigIntegerNegate = bigIntegerNegate.negate();
            }
            BigInteger bigIntegerSubtract = bigIntegerNegate.subtract(bigInteger.shiftLeft(1));
            bigInteger = bigIntegerValueOf;
            bigIntegerValueOf = bigIntegerSubtract;
        }
        return new BigInteger[]{bigInteger, bigIntegerValueOf};
    }

    public static BigInteger getTw(byte b, int i) {
        if (i == 4) {
            return b == 1 ? BigInteger.valueOf(6L) : BigInteger.valueOf(10L);
        }
        BigInteger[] lucas = getLucas(b, i, false);
        BigInteger bit = ECConstants.ZERO.setBit(i);
        return lucas[0].shiftLeft(1).multiply(lucas[1].modInverse(bit)).mod(bit);
    }

    public static BigInteger[] getSi(ECCurve.AbstractF2m abstractF2m) {
        if (abstractF2m.isKoblitz()) {
            return getSi(abstractF2m.getFieldSize(), abstractF2m.getA().toBigInteger().intValue(), abstractF2m.getCofactor());
        }
        throw new IllegalArgumentException("si is defined for Koblitz curves only");
    }

    public static BigInteger[] getSi(int i, int i2, BigInteger bigInteger) {
        byte mu = getMu(i2);
        int shiftsForCofactor = getShiftsForCofactor(bigInteger);
        BigInteger[] lucas = getLucas(mu, (i + 3) - i2, false);
        if (mu == 1) {
            lucas[0] = lucas[0].negate();
            lucas[1] = lucas[1].negate();
        }
        return new BigInteger[]{ECConstants.ONE.add(lucas[1]).shiftRight(shiftsForCofactor), ECConstants.ONE.add(lucas[0]).shiftRight(shiftsForCofactor).negate()};
    }

    protected static int getShiftsForCofactor(BigInteger bigInteger) {
        if (bigInteger != null) {
            if (bigInteger.equals(ECConstants.TWO)) {
                return 1;
            }
            if (bigInteger.equals(ECConstants.FOUR)) {
                return 2;
            }
        }
        throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
    }

    public static ZTauElement partModReduction(ECCurve.AbstractF2m abstractF2m, BigInteger bigInteger, byte b, byte b2, byte b3) {
        int fieldSize = abstractF2m.getFieldSize();
        BigInteger[] si = abstractF2m.getSi();
        BigInteger bigIntegerAdd = b2 == 1 ? si[0].add(si[1]) : si[0].subtract(si[1]);
        BigInteger bigIntegerSubtract = abstractF2m.isKoblitz() ? ECConstants.ONE.shiftLeft(fieldSize).add(ECConstants.ONE).subtract(abstractF2m.getOrder().multiply(abstractF2m.getCofactor())) : getLucas(b2, fieldSize, true)[1];
        ZTauElement zTauElementRound = round(approximateDivisionByN(bigInteger, si[0], bigIntegerSubtract, b, fieldSize, b3), approximateDivisionByN(bigInteger, si[1], bigIntegerSubtract, b, fieldSize, b3), b2);
        return new ZTauElement(bigInteger.subtract(bigIntegerAdd.multiply(zTauElementRound.u)).subtract(si[1].multiply(zTauElementRound.v).shiftLeft(1)), si[1].multiply(zTauElementRound.u).subtract(si[0].multiply(zTauElementRound.v)));
    }

    public static ECPoint.AbstractF2m multiplyRTnaf(ECPoint.AbstractF2m abstractF2m, BigInteger bigInteger) {
        ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m) abstractF2m.getCurve();
        int iIntValue = abstractF2m2.getA().toBigInteger().intValue();
        return multiplyTnaf(abstractF2m, partModReduction(abstractF2m2, bigInteger, (byte) iIntValue, getMu(iIntValue), (byte) 10));
    }

    public static ECPoint.AbstractF2m multiplyTnaf(ECPoint.AbstractF2m abstractF2m, ZTauElement zTauElement) {
        return multiplyFromTnaf(abstractF2m, (ECPoint.AbstractF2m) abstractF2m.negate(), tauAdicNaf(getMu(((ECCurve.AbstractF2m) abstractF2m.getCurve()).getA()), zTauElement));
    }

    public static ECPoint.AbstractF2m multiplyFromTnaf(ECPoint.AbstractF2m abstractF2m, ECPoint.AbstractF2m abstractF2m2, byte[] bArr) {
        ECPoint.AbstractF2m abstractF2mTauPow = (ECPoint.AbstractF2m) abstractF2m.getCurve().getInfinity();
        int i = 0;
        for (int length = bArr.length - 1; length >= 0; length--) {
            i++;
            byte b = bArr[length];
            if (b != 0) {
                ECPoint.AbstractF2m abstractF2mTauPow2 = abstractF2mTauPow.tauPow(i);
                i = 0;
                abstractF2mTauPow = (ECPoint.AbstractF2m) abstractF2mTauPow2.add(b > 0 ? abstractF2m : abstractF2m2);
            }
        }
        if (i > 0) {
            abstractF2mTauPow = abstractF2mTauPow.tauPow(i);
        }
        return abstractF2mTauPow;
    }

    public static byte[] tauAdicWNaf(byte b, ZTauElement zTauElement, int i, int i2, ZTauElement[] zTauElementArr) {
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        int iBitLength = norm(b, zTauElement).bitLength();
        byte[] bArr = new byte[iBitLength > 30 ? iBitLength + 4 + i : 34 + i];
        int i3 = (1 << i) - 1;
        int i4 = 32 - i;
        BigInteger bigIntegerAdd = zTauElement.u;
        BigInteger bigIntegerNegate = zTauElement.v;
        int i5 = 0;
        while (true) {
            if (bigIntegerAdd.bitLength() <= 62 && bigIntegerNegate.bitLength() <= 62) {
                break;
            }
            if (bigIntegerAdd.testBit(0)) {
                int iIntValue = bigIntegerAdd.intValue() + (bigIntegerNegate.intValue() * i2);
                int i6 = iIntValue & i3;
                bArr[i5] = (byte) ((iIntValue << i4) >> i4);
                bigIntegerAdd = bigIntegerAdd.subtract(zTauElementArr[i6].u);
                bigIntegerNegate = bigIntegerNegate.subtract(zTauElementArr[i6].v);
            }
            i5++;
            BigInteger bigIntegerShiftRight = bigIntegerAdd.shiftRight(1);
            bigIntegerAdd = b == 1 ? bigIntegerNegate.add(bigIntegerShiftRight) : bigIntegerNegate.subtract(bigIntegerShiftRight);
            bigIntegerNegate = bigIntegerShiftRight.negate();
        }
        long jLongValueExact = BigIntegers.longValueExact(bigIntegerAdd);
        long jLongValueExact2 = BigIntegers.longValueExact(bigIntegerNegate);
        while (true) {
            long jIntValue = jLongValueExact2;
            if ((jLongValueExact | jIntValue) == 0) {
                return bArr;
            }
            if ((jLongValueExact & 1) != 0) {
                int i7 = ((int) jLongValueExact) + (((int) jIntValue) * i2);
                int i8 = i7 & i3;
                bArr[i5] = (byte) ((i7 << i4) >> i4);
                jLongValueExact -= (long) zTauElementArr[i8].u.intValue();
                jIntValue -= (long) zTauElementArr[i8].v.intValue();
            }
            i5++;
            long j = jLongValueExact >> 1;
            jLongValueExact = b == 1 ? jIntValue + j : jIntValue - j;
            jLongValueExact2 = -j;
        }
    }

    public static ECPoint.AbstractF2m[] getPreComp(ECPoint.AbstractF2m abstractF2m, byte b) {
        ECPoint.AbstractF2m abstractF2m2 = (ECPoint.AbstractF2m) abstractF2m.negate();
        byte[][] bArr = b == 0 ? alpha0Tnaf : alpha1Tnaf;
        ECPoint.AbstractF2m[] abstractF2mArr = new ECPoint.AbstractF2m[(bArr.length + 1) >>> 1];
        abstractF2mArr[0] = abstractF2m;
        int length = bArr.length;
        for (int i = 3; i < length; i += 2) {
            abstractF2mArr[i >>> 1] = multiplyFromTnaf(abstractF2m, abstractF2m2, bArr[i]);
        }
        abstractF2m.getCurve().normalizeAll(abstractF2mArr);
        return abstractF2mArr;
    }
}
