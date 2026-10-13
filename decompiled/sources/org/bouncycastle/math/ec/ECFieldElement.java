package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECFieldElement.class */
public abstract class ECFieldElement implements ECConstants {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECFieldElement$AbstractF2m.class */
    public static abstract class AbstractF2m extends ECFieldElement {
        public ECFieldElement halfTrace() {
            int fieldSize = getFieldSize();
            if ((fieldSize & 1) == 0) {
                throw new IllegalStateException("Half-trace only defined for odd m");
            }
            int i = (fieldSize + 1) >>> 1;
            int iNumberOfLeadingZeros = 31 - Integers.numberOfLeadingZeros(i);
            int i2 = 1;
            ECFieldElement eCFieldElementAdd = this;
            while (iNumberOfLeadingZeros > 0) {
                eCFieldElementAdd = eCFieldElementAdd.squarePow(i2 << 1).add(eCFieldElementAdd);
                iNumberOfLeadingZeros--;
                i2 = i >>> iNumberOfLeadingZeros;
                if (0 != (i2 & 1)) {
                    eCFieldElementAdd = eCFieldElementAdd.squarePow(2).add(this);
                }
            }
            return eCFieldElementAdd;
        }

        public boolean hasFastTrace() {
            return false;
        }

        public int trace() {
            int fieldSize = getFieldSize();
            int iNumberOfLeadingZeros = 31 - Integers.numberOfLeadingZeros(fieldSize);
            int i = 1;
            ECFieldElement eCFieldElementAdd = this;
            while (iNumberOfLeadingZeros > 0) {
                eCFieldElementAdd = eCFieldElementAdd.squarePow(i).add(eCFieldElementAdd);
                iNumberOfLeadingZeros--;
                i = fieldSize >>> iNumberOfLeadingZeros;
                if (0 != (i & 1)) {
                    eCFieldElementAdd = eCFieldElementAdd.square().add(this);
                }
            }
            if (eCFieldElementAdd.isZero()) {
                return 0;
            }
            if (eCFieldElementAdd.isOne()) {
                return 1;
            }
            throw new IllegalStateException("Internal error in trace calculation");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECFieldElement$AbstractFp.class */
    public static abstract class AbstractFp extends ECFieldElement {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECFieldElement$F2m.class */
    public static class F2m extends AbstractF2m {
        public static final int GNB = 1;
        public static final int TPB = 2;
        public static final int PPB = 3;
        private int representation;
        private int m;
        private int[] ks;
        LongArray x;

        F2m(int i, int[] iArr, LongArray longArray) {
            this.m = i;
            this.representation = iArr.length == 1 ? 2 : 3;
            this.ks = iArr;
            this.x = longArray;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public int bitLength() {
            return this.x.degree();
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public boolean isOne() {
            return this.x.isOne();
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public boolean isZero() {
            return this.x.isZero();
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public boolean testBitZero() {
            return this.x.testBitZero();
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.x.toBigInteger();
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public String getFieldName() {
            return "F2m";
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public int getFieldSize() {
            return this.m;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement add(ECFieldElement eCFieldElement) {
            LongArray longArray = (LongArray) this.x.clone();
            longArray.addShiftedByWords(((F2m) eCFieldElement).x, 0);
            return new F2m(this.m, this.ks, longArray);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement addOne() {
            return new F2m(this.m, this.ks, this.x.addOne());
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement eCFieldElement) {
            return add(eCFieldElement);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement eCFieldElement) {
            return new F2m(this.m, this.ks, this.x.modMultiply(((F2m) eCFieldElement).x, this.m, this.ks));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiplyMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
            return multiplyPlusProduct(eCFieldElement, eCFieldElement2, eCFieldElement3);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiplyPlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
            LongArray longArray = this.x;
            LongArray longArray2 = ((F2m) eCFieldElement).x;
            LongArray longArray3 = ((F2m) eCFieldElement2).x;
            LongArray longArray4 = ((F2m) eCFieldElement3).x;
            LongArray longArrayMultiply = longArray.multiply(longArray2, this.m, this.ks);
            LongArray longArrayMultiply2 = longArray3.multiply(longArray4, this.m, this.ks);
            if (longArrayMultiply == longArray || longArrayMultiply == longArray2) {
                longArrayMultiply = (LongArray) longArrayMultiply.clone();
            }
            longArrayMultiply.addShiftedByWords(longArrayMultiply2, 0);
            longArrayMultiply.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, longArrayMultiply);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement eCFieldElement) {
            return multiply(eCFieldElement.invert());
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement negate() {
            return this;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement square() {
            return new F2m(this.m, this.ks, this.x.modSquare(this.m, this.ks));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement squareMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            return squarePlusProduct(eCFieldElement, eCFieldElement2);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement squarePlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            LongArray longArray = this.x;
            LongArray longArray2 = ((F2m) eCFieldElement).x;
            LongArray longArray3 = ((F2m) eCFieldElement2).x;
            LongArray longArraySquare = longArray.square(this.m, this.ks);
            LongArray longArrayMultiply = longArray2.multiply(longArray3, this.m, this.ks);
            if (longArraySquare == longArray) {
                longArraySquare = (LongArray) longArraySquare.clone();
            }
            longArraySquare.addShiftedByWords(longArrayMultiply, 0);
            longArraySquare.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, longArraySquare);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement squarePow(int i) {
            return i < 1 ? this : new F2m(this.m, this.ks, this.x.modSquareN(i, this.m, this.ks));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement invert() {
            return new F2m(this.m, this.ks, this.x.modInverse(this.m, this.ks));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement sqrt() {
            return (this.x.isZero() || this.x.isOne()) ? this : squarePow(this.m - 1);
        }

        public int getRepresentation() {
            return this.representation;
        }

        public int getM() {
            return this.m;
        }

        public int getK1() {
            return this.ks[0];
        }

        public int getK2() {
            if (this.ks.length >= 2) {
                return this.ks[1];
            }
            return 0;
        }

        public int getK3() {
            if (this.ks.length >= 3) {
                return this.ks[2];
            }
            return 0;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof F2m)) {
                return false;
            }
            F2m f2m = (F2m) obj;
            return this.m == f2m.m && this.representation == f2m.representation && Arrays.areEqual(this.ks, f2m.ks) && this.x.equals(f2m.x);
        }

        public int hashCode() {
            return (this.x.hashCode() ^ this.m) ^ Arrays.hashCode(this.ks);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECFieldElement$Fp.class */
    public static class Fp extends AbstractFp {
        BigInteger q;
        BigInteger r;
        BigInteger x;

        static BigInteger calculateResidue(BigInteger bigInteger) {
            int iBitLength = bigInteger.bitLength();
            if (iBitLength < 96 || bigInteger.shiftRight(iBitLength - 64).longValue() != -1) {
                return null;
            }
            return ONE.shiftLeft(iBitLength).subtract(bigInteger);
        }

        Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
            this.q = bigInteger;
            this.r = bigInteger2;
            this.x = bigInteger3;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.x;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public String getFieldName() {
            return "Fp";
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public int getFieldSize() {
            return this.q.bitLength();
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement add(ECFieldElement eCFieldElement) {
            return new Fp(this.q, this.r, modAdd(this.x, eCFieldElement.toBigInteger()));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement addOne() {
            BigInteger bigIntegerAdd = this.x.add(ECConstants.ONE);
            if (bigIntegerAdd.compareTo(this.q) == 0) {
                bigIntegerAdd = ECConstants.ZERO;
            }
            return new Fp(this.q, this.r, bigIntegerAdd);
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement eCFieldElement) {
            return new Fp(this.q, this.r, modSubtract(this.x, eCFieldElement.toBigInteger()));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement eCFieldElement) {
            return new Fp(this.q, this.r, modMult(this.x, eCFieldElement.toBigInteger()));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiplyMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
            BigInteger bigInteger = this.x;
            BigInteger bigInteger2 = eCFieldElement.toBigInteger();
            BigInteger bigInteger3 = eCFieldElement2.toBigInteger();
            BigInteger bigInteger4 = eCFieldElement3.toBigInteger();
            return new Fp(this.q, this.r, modReduce(bigInteger.multiply(bigInteger2).subtract(bigInteger3.multiply(bigInteger4))));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement multiplyPlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
            BigInteger bigInteger = this.x;
            BigInteger bigInteger2 = eCFieldElement.toBigInteger();
            BigInteger bigInteger3 = eCFieldElement2.toBigInteger();
            BigInteger bigInteger4 = eCFieldElement3.toBigInteger();
            return new Fp(this.q, this.r, modReduce(bigInteger.multiply(bigInteger2).add(bigInteger3.multiply(bigInteger4))));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement eCFieldElement) {
            return new Fp(this.q, this.r, modMult(this.x, modInverse(eCFieldElement.toBigInteger())));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement negate() {
            return this.x.signum() == 0 ? this : new Fp(this.q, this.r, this.q.subtract(this.x));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement square() {
            return new Fp(this.q, this.r, modMult(this.x, this.x));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement squareMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            BigInteger bigInteger = this.x;
            BigInteger bigInteger2 = eCFieldElement.toBigInteger();
            BigInteger bigInteger3 = eCFieldElement2.toBigInteger();
            return new Fp(this.q, this.r, modReduce(bigInteger.multiply(bigInteger).subtract(bigInteger2.multiply(bigInteger3))));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement squarePlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            BigInteger bigInteger = this.x;
            BigInteger bigInteger2 = eCFieldElement.toBigInteger();
            BigInteger bigInteger3 = eCFieldElement2.toBigInteger();
            return new Fp(this.q, this.r, modReduce(bigInteger.multiply(bigInteger).add(bigInteger2.multiply(bigInteger3))));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement invert() {
            return new Fp(this.q, this.r, modInverse(this.x));
        }

        @Override // org.bouncycastle.math.ec.ECFieldElement
        public ECFieldElement sqrt() {
            if (isZero() || isOne()) {
                return this;
            }
            if (!this.q.testBit(0)) {
                throw new RuntimeException("not done yet");
            }
            if (this.q.testBit(1)) {
                return checkSqrt(new Fp(this.q, this.r, this.x.modPow(this.q.shiftRight(2).add(ECConstants.ONE), this.q)));
            }
            if (this.q.testBit(2)) {
                BigInteger bigIntegerModPow = this.x.modPow(this.q.shiftRight(3), this.q);
                BigInteger bigIntegerModMult = modMult(bigIntegerModPow, this.x);
                if (modMult(bigIntegerModMult, bigIntegerModPow).equals(ECConstants.ONE)) {
                    return checkSqrt(new Fp(this.q, this.r, bigIntegerModMult));
                }
                return checkSqrt(new Fp(this.q, this.r, modMult(bigIntegerModMult, ECConstants.TWO.modPow(this.q.shiftRight(2), this.q))));
            }
            BigInteger bigIntegerShiftRight = this.q.shiftRight(1);
            if (!this.x.modPow(bigIntegerShiftRight, this.q).equals(ECConstants.ONE)) {
                return null;
            }
            BigInteger bigInteger = this.x;
            BigInteger bigIntegerModDouble = modDouble(modDouble(bigInteger));
            BigInteger bigIntegerAdd = bigIntegerShiftRight.add(ECConstants.ONE);
            BigInteger bigIntegerSubtract = this.q.subtract(ECConstants.ONE);
            Random random = new Random();
            while (true) {
                BigInteger bigInteger2 = new BigInteger(this.q.bitLength(), random);
                if (bigInteger2.compareTo(this.q) < 0 && modReduce(bigInteger2.multiply(bigInteger2).subtract(bigIntegerModDouble)).modPow(bigIntegerShiftRight, this.q).equals(bigIntegerSubtract)) {
                    BigInteger[] bigIntegerArrLucasSequence = lucasSequence(bigInteger2, bigInteger, bigIntegerAdd);
                    BigInteger bigInteger3 = bigIntegerArrLucasSequence[0];
                    BigInteger bigInteger4 = bigIntegerArrLucasSequence[1];
                    if (modMult(bigInteger4, bigInteger4).equals(bigIntegerModDouble)) {
                        return new Fp(this.q, this.r, modHalfAbs(bigInteger4));
                    }
                    if (!bigInteger3.equals(ECConstants.ONE) && !bigInteger3.equals(bigIntegerSubtract)) {
                        return null;
                    }
                }
            }
        }

        private ECFieldElement checkSqrt(ECFieldElement eCFieldElement) {
            if (eCFieldElement.square().equals(this)) {
                return eCFieldElement;
            }
            return null;
        }

        private BigInteger[] lucasSequence(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
            int iBitLength = bigInteger3.bitLength();
            int lowestSetBit = bigInteger3.getLowestSetBit();
            BigInteger bigIntegerModReduce = ECConstants.ONE;
            BigInteger bigIntegerModReduce2 = ECConstants.TWO;
            BigInteger bigIntegerModReduce3 = bigInteger;
            BigInteger bigIntegerModMult = ECConstants.ONE;
            BigInteger bigIntegerModMult2 = ECConstants.ONE;
            for (int i = iBitLength - 1; i >= lowestSetBit + 1; i--) {
                bigIntegerModMult = modMult(bigIntegerModMult, bigIntegerModMult2);
                if (bigInteger3.testBit(i)) {
                    bigIntegerModMult2 = modMult(bigIntegerModMult, bigInteger2);
                    bigIntegerModReduce = modMult(bigIntegerModReduce, bigIntegerModReduce3);
                    bigIntegerModReduce2 = modReduce(bigIntegerModReduce3.multiply(bigIntegerModReduce2).subtract(bigInteger.multiply(bigIntegerModMult)));
                    bigIntegerModReduce3 = modReduce(bigIntegerModReduce3.multiply(bigIntegerModReduce3).subtract(bigIntegerModMult2.shiftLeft(1)));
                } else {
                    bigIntegerModMult2 = bigIntegerModMult;
                    bigIntegerModReduce = modReduce(bigIntegerModReduce.multiply(bigIntegerModReduce2).subtract(bigIntegerModMult));
                    bigIntegerModReduce3 = modReduce(bigIntegerModReduce3.multiply(bigIntegerModReduce2).subtract(bigInteger.multiply(bigIntegerModMult)));
                    bigIntegerModReduce2 = modReduce(bigIntegerModReduce2.multiply(bigIntegerModReduce2).subtract(bigIntegerModMult.shiftLeft(1)));
                }
            }
            BigInteger bigIntegerModMult3 = modMult(bigIntegerModMult, bigIntegerModMult2);
            BigInteger bigIntegerModMult4 = modMult(bigIntegerModMult3, bigInteger2);
            BigInteger bigIntegerModReduce4 = modReduce(bigIntegerModReduce.multiply(bigIntegerModReduce2).subtract(bigIntegerModMult3));
            BigInteger bigIntegerModReduce5 = modReduce(bigIntegerModReduce3.multiply(bigIntegerModReduce2).subtract(bigInteger.multiply(bigIntegerModMult3)));
            BigInteger bigIntegerModMult5 = modMult(bigIntegerModMult3, bigIntegerModMult4);
            for (int i2 = 1; i2 <= lowestSetBit; i2++) {
                bigIntegerModReduce4 = modMult(bigIntegerModReduce4, bigIntegerModReduce5);
                bigIntegerModReduce5 = modReduce(bigIntegerModReduce5.multiply(bigIntegerModReduce5).subtract(bigIntegerModMult5.shiftLeft(1)));
                bigIntegerModMult5 = modMult(bigIntegerModMult5, bigIntegerModMult5);
            }
            return new BigInteger[]{bigIntegerModReduce4, bigIntegerModReduce5};
        }

        protected BigInteger modAdd(BigInteger bigInteger, BigInteger bigInteger2) {
            BigInteger bigIntegerAdd = bigInteger.add(bigInteger2);
            if (bigIntegerAdd.compareTo(this.q) >= 0) {
                bigIntegerAdd = bigIntegerAdd.subtract(this.q);
            }
            return bigIntegerAdd;
        }

        protected BigInteger modDouble(BigInteger bigInteger) {
            BigInteger bigIntegerShiftLeft = bigInteger.shiftLeft(1);
            if (bigIntegerShiftLeft.compareTo(this.q) >= 0) {
                bigIntegerShiftLeft = bigIntegerShiftLeft.subtract(this.q);
            }
            return bigIntegerShiftLeft;
        }

        protected BigInteger modHalf(BigInteger bigInteger) {
            if (bigInteger.testBit(0)) {
                bigInteger = this.q.add(bigInteger);
            }
            return bigInteger.shiftRight(1);
        }

        protected BigInteger modHalfAbs(BigInteger bigInteger) {
            if (bigInteger.testBit(0)) {
                bigInteger = this.q.subtract(bigInteger);
            }
            return bigInteger.shiftRight(1);
        }

        protected BigInteger modInverse(BigInteger bigInteger) {
            return BigIntegers.modOddInverse(this.q, bigInteger);
        }

        protected BigInteger modMult(BigInteger bigInteger, BigInteger bigInteger2) {
            return modReduce(bigInteger.multiply(bigInteger2));
        }

        protected BigInteger modReduce(BigInteger bigInteger) {
            if (this.r != null) {
                boolean z = bigInteger.signum() < 0;
                if (z) {
                    bigInteger = bigInteger.abs();
                }
                int iBitLength = this.q.bitLength();
                boolean zEquals = this.r.equals(ECConstants.ONE);
                while (bigInteger.bitLength() > iBitLength + 1) {
                    BigInteger bigIntegerShiftRight = bigInteger.shiftRight(iBitLength);
                    BigInteger bigIntegerSubtract = bigInteger.subtract(bigIntegerShiftRight.shiftLeft(iBitLength));
                    if (!zEquals) {
                        bigIntegerShiftRight = bigIntegerShiftRight.multiply(this.r);
                    }
                    bigInteger = bigIntegerShiftRight.add(bigIntegerSubtract);
                }
                while (bigInteger.compareTo(this.q) >= 0) {
                    bigInteger = bigInteger.subtract(this.q);
                }
                if (z && bigInteger.signum() != 0) {
                    bigInteger = this.q.subtract(bigInteger);
                }
            } else {
                bigInteger = bigInteger.mod(this.q);
            }
            return bigInteger;
        }

        protected BigInteger modSubtract(BigInteger bigInteger, BigInteger bigInteger2) {
            BigInteger bigIntegerSubtract = bigInteger.subtract(bigInteger2);
            if (bigIntegerSubtract.signum() < 0) {
                bigIntegerSubtract = bigIntegerSubtract.add(this.q);
            }
            return bigIntegerSubtract;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Fp)) {
                return false;
            }
            Fp fp = (Fp) obj;
            return this.q.equals(fp.q) && this.x.equals(fp.x);
        }

        public int hashCode() {
            return this.q.hashCode() ^ this.x.hashCode();
        }
    }

    public abstract BigInteger toBigInteger();

    public abstract String getFieldName();

    public abstract int getFieldSize();

    public abstract ECFieldElement add(ECFieldElement eCFieldElement);

    public abstract ECFieldElement addOne();

    public abstract ECFieldElement subtract(ECFieldElement eCFieldElement);

    public abstract ECFieldElement multiply(ECFieldElement eCFieldElement);

    public abstract ECFieldElement divide(ECFieldElement eCFieldElement);

    public abstract ECFieldElement negate();

    public abstract ECFieldElement square();

    public abstract ECFieldElement invert();

    public abstract ECFieldElement sqrt();

    public int bitLength() {
        return toBigInteger().bitLength();
    }

    public boolean isOne() {
        return bitLength() == 1;
    }

    public boolean isZero() {
        return 0 == toBigInteger().signum();
    }

    public ECFieldElement multiplyMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        return multiply(eCFieldElement).subtract(eCFieldElement2.multiply(eCFieldElement3));
    }

    public ECFieldElement multiplyPlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        return multiply(eCFieldElement).add(eCFieldElement2.multiply(eCFieldElement3));
    }

    public ECFieldElement squareMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return square().subtract(eCFieldElement.multiply(eCFieldElement2));
    }

    public ECFieldElement squarePlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return square().add(eCFieldElement.multiply(eCFieldElement2));
    }

    public ECFieldElement squarePow(int i) {
        ECFieldElement eCFieldElementSquare = this;
        for (int i2 = 0; i2 < i; i2++) {
            eCFieldElementSquare = eCFieldElementSquare.square();
        }
        return eCFieldElementSquare;
    }

    public boolean testBitZero() {
        return toBigInteger().testBit(0);
    }

    public String toString() {
        return toBigInteger().toString(16);
    }

    public byte[] getEncoded() {
        return BigIntegers.asUnsignedByteArray(getEncodedLength(), toBigInteger());
    }

    public int getEncodedLength() {
        return (getFieldSize() + 7) / 8;
    }

    public void encodeTo(byte[] bArr, int i) {
        BigIntegers.asUnsignedByteArray(toBigInteger(), bArr, i, getEncodedLength());
    }
}
