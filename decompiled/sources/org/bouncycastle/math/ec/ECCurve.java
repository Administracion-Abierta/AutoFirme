package org.bouncycastle.math.ec;

import com.sun.jna.platform.win32.WinError;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.FiniteFields;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve.class */
public abstract class ECCurve {
    public static final int COORD_AFFINE = 0;
    public static final int COORD_HOMOGENEOUS = 1;
    public static final int COORD_JACOBIAN = 2;
    public static final int COORD_JACOBIAN_CHUDNOVSKY = 3;
    public static final int COORD_JACOBIAN_MODIFIED = 4;
    public static final int COORD_LAMBDA_AFFINE = 5;
    public static final int COORD_LAMBDA_PROJECTIVE = 6;
    public static final int COORD_SKEWED = 7;
    protected FiniteField field;
    protected ECFieldElement a;
    protected ECFieldElement b;
    protected BigInteger order;
    protected BigInteger cofactor;
    protected int coord = 0;
    protected ECEndomorphism endomorphism = null;
    protected ECMultiplier multiplier = null;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve$AbstractF2m.class */
    public static abstract class AbstractF2m extends ECCurve {
        private BigInteger[] si;

        public static BigInteger inverse(int i, int[] iArr, BigInteger bigInteger) {
            return new LongArray(bigInteger).modInverse(i, iArr).toBigInteger();
        }

        private static FiniteField buildField(int i, int i2, int i3, int i4) {
            if (i > Properties.asInteger("org.bouncycastle.ec.max_f2m_field_size", WinError.ERROR_TOO_MANY_LINKS)) {
                throw new IllegalArgumentException("field size out of range: " + i);
            }
            return FiniteFields.getBinaryExtensionField((i3 | i4) == 0 ? new int[]{0, i2, i} : new int[]{0, i2, i3, i4, i});
        }

        protected AbstractF2m(int i, int i2, int i3, int i4) {
            super(buildField(i, i2, i3, i4));
            this.si = null;
            if (Properties.isOverrideSet("org.bouncycastle.ec.disable")) {
                throw new UnsupportedOperationException("F2M disabled by \"org.bouncycastle.ec.disable\"");
            }
            if (Properties.isOverrideSet("org.bouncycastle.ec.disable_f2m")) {
                throw new UnsupportedOperationException("F2M disabled by \"org.bouncycastle.ec.disable_f2m\"");
            }
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2) {
            ECFieldElement eCFieldElementFromBigInteger = fromBigInteger(bigInteger);
            ECFieldElement eCFieldElementFromBigInteger2 = fromBigInteger(bigInteger2);
            switch (getCoordinateSystem()) {
                case 5:
                case 6:
                    if (!eCFieldElementFromBigInteger.isZero()) {
                        eCFieldElementFromBigInteger2 = eCFieldElementFromBigInteger2.divide(eCFieldElementFromBigInteger).add(eCFieldElementFromBigInteger);
                    } else if (!eCFieldElementFromBigInteger2.square().equals(getB())) {
                        throw new IllegalArgumentException();
                    }
                    break;
            }
            return createRawPoint(eCFieldElementFromBigInteger, eCFieldElementFromBigInteger2);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public boolean isValidFieldElement(BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.bitLength() <= getFieldSize();
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement randomFieldElement(SecureRandom secureRandom) {
            return fromBigInteger(BigIntegers.createRandomBigInteger(getFieldSize(), secureRandom));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement randomFieldElementMult(SecureRandom secureRandom) {
            int fieldSize = getFieldSize();
            return fromBigInteger(implRandomFieldElementMult(secureRandom, fieldSize)).multiply(fromBigInteger(implRandomFieldElementMult(secureRandom, fieldSize)));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint decompressPoint(int i, BigInteger bigInteger) {
            ECFieldElement eCFieldElementFromBigInteger = fromBigInteger(bigInteger);
            ECFieldElement eCFieldElementMultiply = null;
            if (!eCFieldElementFromBigInteger.isZero()) {
                ECFieldElement eCFieldElementSolveQuadraticEquation = solveQuadraticEquation(eCFieldElementFromBigInteger.square().invert().multiply(getB()).add(getA()).add(eCFieldElementFromBigInteger));
                if (eCFieldElementSolveQuadraticEquation != null) {
                    if (eCFieldElementSolveQuadraticEquation.testBitZero() != (i == 1)) {
                        eCFieldElementSolveQuadraticEquation = eCFieldElementSolveQuadraticEquation.addOne();
                    }
                    switch (getCoordinateSystem()) {
                        case 5:
                        case 6:
                            eCFieldElementMultiply = eCFieldElementSolveQuadraticEquation.add(eCFieldElementFromBigInteger);
                            break;
                        default:
                            eCFieldElementMultiply = eCFieldElementSolveQuadraticEquation.multiply(eCFieldElementFromBigInteger);
                            break;
                    }
                }
            } else {
                eCFieldElementMultiply = getB().sqrt();
            }
            if (eCFieldElementMultiply == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            return createRawPoint(eCFieldElementFromBigInteger, eCFieldElementMultiply);
        }

        protected ECFieldElement solveQuadraticEquation(ECFieldElement eCFieldElement) {
            ECFieldElement eCFieldElementAdd;
            ECFieldElement.AbstractF2m abstractF2m = (ECFieldElement.AbstractF2m) eCFieldElement;
            boolean zHasFastTrace = abstractF2m.hasFastTrace();
            if (zHasFastTrace && 0 != abstractF2m.trace()) {
                return null;
            }
            int fieldSize = getFieldSize();
            if (0 != (fieldSize & 1)) {
                ECFieldElement eCFieldElementHalfTrace = abstractF2m.halfTrace();
                if (zHasFastTrace || eCFieldElementHalfTrace.square().add(eCFieldElementHalfTrace).add(eCFieldElement).isZero()) {
                    return eCFieldElementHalfTrace;
                }
                return null;
            }
            if (eCFieldElement.isZero()) {
                return eCFieldElement;
            }
            ECFieldElement eCFieldElementFromBigInteger = fromBigInteger(ECConstants.ZERO);
            Random random = new Random();
            do {
                ECFieldElement eCFieldElementFromBigInteger2 = fromBigInteger(new BigInteger(fieldSize, random));
                eCFieldElementAdd = eCFieldElementFromBigInteger;
                ECFieldElement eCFieldElementAdd2 = eCFieldElement;
                for (int i = 1; i < fieldSize; i++) {
                    ECFieldElement eCFieldElementSquare = eCFieldElementAdd2.square();
                    eCFieldElementAdd = eCFieldElementAdd.square().add(eCFieldElementSquare.multiply(eCFieldElementFromBigInteger2));
                    eCFieldElementAdd2 = eCFieldElementSquare.add(eCFieldElement);
                }
                if (!eCFieldElementAdd2.isZero()) {
                    return null;
                }
            } while (eCFieldElementAdd.square().add(eCFieldElementAdd).isZero());
            return eCFieldElementAdd;
        }

        synchronized BigInteger[] getSi() {
            if (this.si == null) {
                this.si = Tnaf.getSi(this);
            }
            return this.si;
        }

        public boolean isKoblitz() {
            return this.order != null && this.cofactor != null && this.b.isOne() && (this.a.isZero() || this.a.isOne());
        }

        private static BigInteger implRandomFieldElementMult(SecureRandom secureRandom, int i) {
            BigInteger bigIntegerCreateRandomBigInteger;
            do {
                bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(i, secureRandom);
            } while (bigIntegerCreateRandomBigInteger.signum() <= 0);
            return bigIntegerCreateRandomBigInteger;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve$AbstractFp.class */
    public static abstract class AbstractFp extends ECCurve {
        protected AbstractFp(BigInteger bigInteger) {
            super(FiniteFields.getPrimeField(bigInteger));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public boolean isValidFieldElement(BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.compareTo(getField().getCharacteristic()) < 0;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement randomFieldElement(SecureRandom secureRandom) {
            BigInteger characteristic = getField().getCharacteristic();
            return fromBigInteger(implRandomFieldElement(secureRandom, characteristic)).multiply(fromBigInteger(implRandomFieldElement(secureRandom, characteristic)));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement randomFieldElementMult(SecureRandom secureRandom) {
            BigInteger characteristic = getField().getCharacteristic();
            return fromBigInteger(implRandomFieldElementMult(secureRandom, characteristic)).multiply(fromBigInteger(implRandomFieldElementMult(secureRandom, characteristic)));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint decompressPoint(int i, BigInteger bigInteger) {
            ECFieldElement eCFieldElementFromBigInteger = fromBigInteger(bigInteger);
            ECFieldElement eCFieldElementSqrt = eCFieldElementFromBigInteger.square().add(this.a).multiply(eCFieldElementFromBigInteger).add(this.b).sqrt();
            if (eCFieldElementSqrt == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            if (eCFieldElementSqrt.testBitZero() != (i == 1)) {
                eCFieldElementSqrt = eCFieldElementSqrt.negate();
            }
            return createRawPoint(eCFieldElementFromBigInteger, eCFieldElementSqrt);
        }

        private static BigInteger implRandomFieldElement(SecureRandom secureRandom, BigInteger bigInteger) {
            BigInteger bigIntegerCreateRandomBigInteger;
            do {
                bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(bigInteger.bitLength(), secureRandom);
            } while (bigIntegerCreateRandomBigInteger.compareTo(bigInteger) >= 0);
            return bigIntegerCreateRandomBigInteger;
        }

        private static BigInteger implRandomFieldElementMult(SecureRandom secureRandom, BigInteger bigInteger) {
            while (true) {
                BigInteger bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(bigInteger.bitLength(), secureRandom);
                if (bigIntegerCreateRandomBigInteger.signum() > 0 && bigIntegerCreateRandomBigInteger.compareTo(bigInteger) < 0) {
                    return bigIntegerCreateRandomBigInteger;
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve$Config.class */
    public class Config {
        protected int coord;
        protected ECEndomorphism endomorphism;
        protected ECMultiplier multiplier;

        Config(int i, ECEndomorphism eCEndomorphism, ECMultiplier eCMultiplier) {
            this.coord = i;
            this.endomorphism = eCEndomorphism;
            this.multiplier = eCMultiplier;
        }

        public Config setCoordinateSystem(int i) {
            this.coord = i;
            return this;
        }

        public Config setEndomorphism(ECEndomorphism eCEndomorphism) {
            this.endomorphism = eCEndomorphism;
            return this;
        }

        public Config setMultiplier(ECMultiplier eCMultiplier) {
            this.multiplier = eCMultiplier;
            return this;
        }

        public ECCurve create() {
            if (!ECCurve.this.supportsCoordinateSystem(this.coord)) {
                throw new IllegalStateException("unsupported coordinate system");
            }
            ECCurve eCCurveCloneCurve = ECCurve.this.cloneCurve();
            if (eCCurveCloneCurve == ECCurve.this) {
                throw new IllegalStateException("implementation returned current curve");
            }
            synchronized (eCCurveCloneCurve) {
                eCCurveCloneCurve.coord = this.coord;
                eCCurveCloneCurve.endomorphism = this.endomorphism;
                eCCurveCloneCurve.multiplier = this.multiplier;
            }
            return eCCurveCloneCurve;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve$F2m.class */
    public static class F2m extends AbstractF2m {
        private static final int F2M_DEFAULT_COORDS = 6;
        private int m;
        private int k1;
        private int k2;
        private int k3;
        private ECPoint.F2m infinity;

        public F2m(int i, int i2, BigInteger bigInteger, BigInteger bigInteger2) {
            this(i, i2, 0, 0, bigInteger, bigInteger2, (BigInteger) null, (BigInteger) null);
        }

        public F2m(int i, int i2, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
            this(i, i2, 0, 0, bigInteger, bigInteger2, bigInteger3, bigInteger4);
        }

        public F2m(int i, int i2, int i3, int i4, BigInteger bigInteger, BigInteger bigInteger2) {
            this(i, i2, i3, i4, bigInteger, bigInteger2, (BigInteger) null, (BigInteger) null);
        }

        public F2m(int i, int i2, int i3, int i4, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
            super(i, i2, i3, i4);
            this.m = i;
            this.k1 = i2;
            this.k2 = i3;
            this.k3 = i4;
            this.order = bigInteger3;
            this.cofactor = bigInteger4;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = fromBigInteger(bigInteger);
            this.b = fromBigInteger(bigInteger2);
            this.coord = 6;
        }

        protected F2m(int i, int i2, int i3, int i4, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, BigInteger bigInteger, BigInteger bigInteger2) {
            super(i, i2, i3, i4);
            this.m = i;
            this.k1 = i2;
            this.k2 = i3;
            this.k3 = i4;
            this.order = bigInteger;
            this.cofactor = bigInteger2;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = eCFieldElement;
            this.b = eCFieldElement2;
            this.coord = 6;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECCurve cloneCurve() {
            return new F2m(this.m, this.k1, this.k2, this.k3, this.a, this.b, this.order, this.cofactor);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public boolean supportsCoordinateSystem(int i) {
            switch (i) {
                case 0:
                case 1:
                case 6:
                    return true;
                default:
                    return false;
            }
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECMultiplier createDefaultMultiplier() {
            return isKoblitz() ? new WTauNafMultiplier() : super.createDefaultMultiplier();
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public int getFieldSize() {
            return this.m;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement fromBigInteger(BigInteger bigInteger) {
            if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.bitLength() > this.m) {
                throw new IllegalArgumentException("x value invalid in F2m field element");
            }
            return new ECFieldElement.F2m(this.m, (this.k2 | this.k3) == 0 ? new int[]{this.k1} : new int[]{this.k1, this.k2, this.k3}, new LongArray(bigInteger));
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            return new ECPoint.F2m(this, eCFieldElement, eCFieldElement2);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            return new ECPoint.F2m(this, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECPoint getInfinity() {
            return this.infinity;
        }

        public int getM() {
            return this.m;
        }

        public boolean isTrinomial() {
            return this.k2 == 0 && this.k3 == 0;
        }

        public int getK1() {
            return this.k1;
        }

        public int getK2() {
            return this.k2;
        }

        public int getK3() {
            return this.k3;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
            final int i3 = (this.m + 63) >>> 6;
            final int[] iArr = isTrinomial() ? new int[]{this.k1} : new int[]{this.k1, this.k2, this.k3};
            final long[] jArr = new long[i2 * i3 * 2];
            int i4 = 0;
            for (int i5 = 0; i5 < i2; i5++) {
                ECPoint eCPoint = eCPointArr[i + i5];
                ((ECFieldElement.F2m) eCPoint.getRawXCoord()).x.copyTo(jArr, i4);
                int i6 = i4 + i3;
                ((ECFieldElement.F2m) eCPoint.getRawYCoord()).x.copyTo(jArr, i6);
                i4 = i6 + i3;
            }
            return new AbstractECLookupTable() { // from class: org.bouncycastle.math.ec.ECCurve.F2m.1
                @Override // org.bouncycastle.math.ec.ECLookupTable
                public int getSize() {
                    return i2;
                }

                @Override // org.bouncycastle.math.ec.ECLookupTable
                public ECPoint lookup(int i7) {
                    long[] jArrCreate64 = Nat.create64(i3);
                    long[] jArrCreate65 = Nat.create64(i3);
                    int i8 = 0;
                    for (int i9 = 0; i9 < i2; i9++) {
                        long j = ((i9 ^ i7) - 1) >> 31;
                        for (int i10 = 0; i10 < i3; i10++) {
                            int i11 = i10;
                            jArrCreate64[i11] = jArrCreate64[i11] ^ (jArr[i8 + i10] & j);
                            int i12 = i10;
                            jArrCreate65[i12] = jArrCreate65[i12] ^ (jArr[(i8 + i3) + i10] & j);
                        }
                        i8 += i3 * 2;
                    }
                    return createPoint(jArrCreate64, jArrCreate65);
                }

                @Override // org.bouncycastle.math.ec.AbstractECLookupTable, org.bouncycastle.math.ec.ECLookupTable
                public ECPoint lookupVar(int i7) {
                    long[] jArrCreate64 = Nat.create64(i3);
                    long[] jArrCreate65 = Nat.create64(i3);
                    int i8 = i7 * i3 * 2;
                    for (int i9 = 0; i9 < i3; i9++) {
                        jArrCreate64[i9] = jArr[i8 + i9];
                        jArrCreate65[i9] = jArr[i8 + i3 + i9];
                    }
                    return createPoint(jArrCreate64, jArrCreate65);
                }

                private ECPoint createPoint(long[] jArr2, long[] jArr3) {
                    return F2m.this.createRawPoint(new ECFieldElement.F2m(F2m.this.m, iArr, new LongArray(jArr2)), new ECFieldElement.F2m(F2m.this.m, iArr, new LongArray(jArr3)));
                }
            };
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECCurve$Fp.class */
    public static class Fp extends AbstractFp {
        private static final int FP_DEFAULT_COORDS = 4;
        private static final Set<BigInteger> knownQs = Collections.synchronizedSet(new HashSet());
        private static final BigIntegers.Cache validatedQs = new BigIntegers.Cache();
        BigInteger q;
        BigInteger r;
        ECPoint.Fp infinity;

        public Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
            this(bigInteger, bigInteger2, bigInteger3, null, null);
        }

        public Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5) {
            this(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, false);
        }

        public Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, boolean z) {
            super(bigInteger);
            if (z) {
                this.q = bigInteger;
                knownQs.add(bigInteger);
            } else if (knownQs.contains(bigInteger) || validatedQs.contains(bigInteger)) {
                this.q = bigInteger;
            } else {
                int iAsInteger = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
                int iAsInteger2 = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
                int iBitLength = bigInteger.bitLength();
                if (iAsInteger < iBitLength) {
                    throw new IllegalArgumentException("Fp q value out of range");
                }
                if (Primes.hasAnySmallFactors(bigInteger) || !Primes.isMRProbablePrime(bigInteger, CryptoServicesRegistrar.getSecureRandom(), ECCurve.getNumberOfIterations(iBitLength, iAsInteger2))) {
                    throw new IllegalArgumentException("Fp q value not prime");
                }
                validatedQs.add(bigInteger);
                this.q = bigInteger;
            }
            this.r = ECFieldElement.Fp.calculateResidue(bigInteger);
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = fromBigInteger(bigInteger2);
            this.b = fromBigInteger(bigInteger3);
            this.order = bigInteger4;
            this.cofactor = bigInteger5;
            this.coord = 4;
        }

        protected Fp(BigInteger bigInteger, BigInteger bigInteger2, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, BigInteger bigInteger3, BigInteger bigInteger4) {
            super(bigInteger);
            this.q = bigInteger;
            this.r = bigInteger2;
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = eCFieldElement;
            this.b = eCFieldElement2;
            this.order = bigInteger3;
            this.cofactor = bigInteger4;
            this.coord = 4;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECCurve cloneCurve() {
            return new Fp(this.q, this.r, this.a, this.b, this.order, this.cofactor);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public boolean supportsCoordinateSystem(int i) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 4:
                    return true;
                case 3:
                default:
                    return false;
            }
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public int getFieldSize() {
            return this.q.bitLength();
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECFieldElement fromBigInteger(BigInteger bigInteger) {
            if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(this.q) >= 0) {
                throw new IllegalArgumentException("x value invalid for Fp field element");
            }
            return new ECFieldElement.Fp(this.q, this.r, bigInteger);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            return new ECPoint.Fp(this, eCFieldElement, eCFieldElement2);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            return new ECPoint.Fp(this, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECPoint importPoint(ECPoint eCPoint) {
            if (this != eCPoint.getCurve() && getCoordinateSystem() == 2 && !eCPoint.isInfinity()) {
                switch (eCPoint.getCurve().getCoordinateSystem()) {
                    case 2:
                    case 3:
                    case 4:
                        return new ECPoint.Fp(this, fromBigInteger(eCPoint.x.toBigInteger()), fromBigInteger(eCPoint.y.toBigInteger()), new ECFieldElement[]{fromBigInteger(eCPoint.zs[0].toBigInteger())});
                }
            }
            return super.importPoint(eCPoint);
        }

        @Override // org.bouncycastle.math.ec.ECCurve
        public ECPoint getInfinity() {
            return this.infinity;
        }
    }

    public static int[] getAllCoordinateSystems() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    }

    protected ECCurve(FiniteField finiteField) {
        this.field = finiteField;
    }

    public abstract int getFieldSize();

    public abstract ECFieldElement fromBigInteger(BigInteger bigInteger);

    public abstract boolean isValidFieldElement(BigInteger bigInteger);

    public abstract ECFieldElement randomFieldElement(SecureRandom secureRandom);

    public abstract ECFieldElement randomFieldElementMult(SecureRandom secureRandom);

    public synchronized Config configure() {
        return new Config(this.coord, this.endomorphism, this.multiplier);
    }

    public int getFieldElementEncodingLength() {
        return (getFieldSize() + 7) / 8;
    }

    public int getAffinePointEncodingLength(boolean z) {
        int fieldElementEncodingLength = getFieldElementEncodingLength();
        return z ? 1 + fieldElementEncodingLength : 1 + (fieldElementEncodingLength * 2);
    }

    public ECPoint validatePoint(BigInteger bigInteger, BigInteger bigInteger2) {
        ECPoint eCPointCreatePoint = createPoint(bigInteger, bigInteger2);
        if (eCPointCreatePoint.isValid()) {
            return eCPointCreatePoint;
        }
        throw new IllegalArgumentException("Invalid point coordinates");
    }

    public ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2) {
        return createRawPoint(fromBigInteger(bigInteger), fromBigInteger(bigInteger2));
    }

    protected abstract ECCurve cloneCurve();

    protected abstract ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2);

    protected abstract ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr);

    protected ECMultiplier createDefaultMultiplier() {
        return this.endomorphism instanceof GLVEndomorphism ? new GLVMultiplier(this, (GLVEndomorphism) this.endomorphism) : new WNafL2RMultiplier();
    }

    public boolean supportsCoordinateSystem(int i) {
        return i == 0;
    }

    public PreCompInfo getPreCompInfo(ECPoint eCPoint, String str) {
        Hashtable hashtable;
        PreCompInfo preCompInfo;
        checkPoint(eCPoint);
        synchronized (eCPoint) {
            hashtable = eCPoint.preCompTable;
        }
        if (null == hashtable) {
            return null;
        }
        synchronized (hashtable) {
            preCompInfo = (PreCompInfo) hashtable.get(str);
        }
        return preCompInfo;
    }

    public PreCompInfo precompute(ECPoint eCPoint, String str, PreCompCallback preCompCallback) {
        Hashtable hashtable;
        PreCompInfo preCompInfoPrecompute;
        checkPoint(eCPoint);
        synchronized (eCPoint) {
            hashtable = eCPoint.preCompTable;
            if (null == hashtable) {
                Hashtable hashtable2 = new Hashtable(4);
                hashtable = hashtable2;
                eCPoint.preCompTable = hashtable2;
            }
        }
        synchronized (hashtable) {
            PreCompInfo preCompInfo = (PreCompInfo) hashtable.get(str);
            preCompInfoPrecompute = preCompCallback.precompute(preCompInfo);
            if (preCompInfoPrecompute != preCompInfo) {
                hashtable.put(str, preCompInfoPrecompute);
            }
        }
        return preCompInfoPrecompute;
    }

    public ECPoint importPoint(ECPoint eCPoint) {
        if (this == eCPoint.getCurve()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return getInfinity();
        }
        ECPoint eCPointNormalize = eCPoint.normalize();
        return createPoint(eCPointNormalize.getXCoord().toBigInteger(), eCPointNormalize.getYCoord().toBigInteger());
    }

    public void normalizeAll(ECPoint[] eCPointArr) {
        normalizeAll(eCPointArr, 0, eCPointArr.length, null);
    }

    public void normalizeAll(ECPoint[] eCPointArr, int i, int i2, ECFieldElement eCFieldElement) {
        checkPoints(eCPointArr, i, i2);
        switch (getCoordinateSystem()) {
            case 0:
            case 5:
                if (eCFieldElement != null) {
                    throw new IllegalArgumentException("'iso' not valid for affine coordinates");
                }
                return;
            default:
                ECFieldElement[] eCFieldElementArr = new ECFieldElement[i2];
                int[] iArr = new int[i2];
                int i3 = 0;
                for (int i4 = 0; i4 < i2; i4++) {
                    ECPoint eCPoint = eCPointArr[i + i4];
                    if (null != eCPoint && (eCFieldElement != null || !eCPoint.isNormalized())) {
                        eCFieldElementArr[i3] = eCPoint.getZCoord(0);
                        int i5 = i3;
                        i3++;
                        iArr[i5] = i + i4;
                    }
                }
                if (i3 == 0) {
                    return;
                }
                ECAlgorithms.montgomeryTrick(eCFieldElementArr, 0, i3, eCFieldElement);
                for (int i6 = 0; i6 < i3; i6++) {
                    int i7 = iArr[i6];
                    eCPointArr[i7] = eCPointArr[i7].normalize(eCFieldElementArr[i6]);
                }
                return;
        }
    }

    public abstract ECPoint getInfinity();

    public FiniteField getField() {
        return this.field;
    }

    public ECFieldElement getA() {
        return this.a;
    }

    public ECFieldElement getB() {
        return this.b;
    }

    public BigInteger getOrder() {
        return this.order;
    }

    public BigInteger getCofactor() {
        return this.cofactor;
    }

    public int getCoordinateSystem() {
        return this.coord;
    }

    protected abstract ECPoint decompressPoint(int i, BigInteger bigInteger);

    public ECEndomorphism getEndomorphism() {
        return this.endomorphism;
    }

    public ECMultiplier getMultiplier() {
        if (this.multiplier == null) {
            this.multiplier = createDefaultMultiplier();
        }
        return this.multiplier;
    }

    public ECPoint decodePoint(byte[] bArr) {
        ECPoint eCPointValidatePoint;
        int fieldElementEncodingLength = getFieldElementEncodingLength();
        byte b = bArr[0];
        switch (b) {
            case 0:
                if (bArr.length != 1) {
                    throw new IllegalArgumentException("Incorrect length for infinity encoding");
                }
                eCPointValidatePoint = getInfinity();
                break;
            case 1:
            case 5:
            default:
                throw new IllegalArgumentException("Invalid point encoding 0x" + Integer.toString(b, 16));
            case 2:
            case 3:
                if (bArr.length != fieldElementEncodingLength + 1) {
                    throw new IllegalArgumentException("Incorrect length for compressed encoding");
                }
                eCPointValidatePoint = decompressPoint(b & 1, BigIntegers.fromUnsignedByteArray(bArr, 1, fieldElementEncodingLength));
                if (!eCPointValidatePoint.implIsValid(true, true)) {
                    throw new IllegalArgumentException("Invalid point");
                }
                break;
            case 4:
                if (bArr.length != (2 * fieldElementEncodingLength) + 1) {
                    throw new IllegalArgumentException("Incorrect length for uncompressed encoding");
                }
                eCPointValidatePoint = validatePoint(BigIntegers.fromUnsignedByteArray(bArr, 1, fieldElementEncodingLength), BigIntegers.fromUnsignedByteArray(bArr, 1 + fieldElementEncodingLength, fieldElementEncodingLength));
                break;
            case 6:
            case 7:
                if (bArr.length != (2 * fieldElementEncodingLength) + 1) {
                    throw new IllegalArgumentException("Incorrect length for hybrid encoding");
                }
                BigInteger bigIntegerFromUnsignedByteArray = BigIntegers.fromUnsignedByteArray(bArr, 1, fieldElementEncodingLength);
                BigInteger bigIntegerFromUnsignedByteArray2 = BigIntegers.fromUnsignedByteArray(bArr, 1 + fieldElementEncodingLength, fieldElementEncodingLength);
                if (bigIntegerFromUnsignedByteArray2.testBit(0) != (b == 7)) {
                    throw new IllegalArgumentException("Inconsistent Y coordinate in hybrid encoding");
                }
                eCPointValidatePoint = validatePoint(bigIntegerFromUnsignedByteArray, bigIntegerFromUnsignedByteArray2);
                break;
        }
        if (b == 0 || !eCPointValidatePoint.isInfinity()) {
            return eCPointValidatePoint;
        }
        throw new IllegalArgumentException("Invalid infinity encoding");
    }

    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
        final int fieldElementEncodingLength = getFieldElementEncodingLength();
        final byte[] bArr = new byte[i2 * fieldElementEncodingLength * 2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            ECPoint eCPoint = eCPointArr[i + i4];
            eCPoint.getRawXCoord().encodeTo(bArr, i3);
            int i5 = i3 + fieldElementEncodingLength;
            eCPoint.getRawYCoord().encodeTo(bArr, i5);
            i3 = i5 + fieldElementEncodingLength;
        }
        return new AbstractECLookupTable() { // from class: org.bouncycastle.math.ec.ECCurve.1
            @Override // org.bouncycastle.math.ec.ECLookupTable
            public int getSize() {
                return i2;
            }

            @Override // org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookup(int i6) {
                byte[] bArr2 = new byte[fieldElementEncodingLength];
                byte[] bArr3 = new byte[fieldElementEncodingLength];
                int i7 = 0;
                for (int i8 = 0; i8 < i2; i8++) {
                    int i9 = ((i8 ^ i6) - 1) >> 31;
                    for (int i10 = 0; i10 < fieldElementEncodingLength; i10++) {
                        int i11 = i10;
                        bArr2[i11] = (byte) (bArr2[i11] ^ (bArr[i7 + i10] & i9));
                        int i12 = i10;
                        bArr3[i12] = (byte) (bArr3[i12] ^ (bArr[(i7 + fieldElementEncodingLength) + i10] & i9));
                    }
                    i7 += fieldElementEncodingLength * 2;
                }
                return createPoint(bArr2, bArr3);
            }

            @Override // org.bouncycastle.math.ec.AbstractECLookupTable, org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookupVar(int i6) {
                byte[] bArr2 = new byte[fieldElementEncodingLength];
                byte[] bArr3 = new byte[fieldElementEncodingLength];
                int i7 = i6 * fieldElementEncodingLength * 2;
                for (int i8 = 0; i8 < fieldElementEncodingLength; i8++) {
                    bArr2[i8] = bArr[i7 + i8];
                    bArr3[i8] = bArr[i7 + fieldElementEncodingLength + i8];
                }
                return createPoint(bArr2, bArr3);
            }

            private ECPoint createPoint(byte[] bArr2, byte[] bArr3) {
                return ECCurve.this.createRawPoint(ECCurve.this.fromBigInteger(new BigInteger(1, bArr2)), ECCurve.this.fromBigInteger(new BigInteger(1, bArr3)));
            }
        };
    }

    protected void checkPoint(ECPoint eCPoint) {
        if (null == eCPoint || this != eCPoint.getCurve()) {
            throw new IllegalArgumentException("'point' must be non-null and on this curve");
        }
    }

    protected void checkPoints(ECPoint[] eCPointArr) {
        checkPoints(eCPointArr, 0, eCPointArr.length);
    }

    protected void checkPoints(ECPoint[] eCPointArr, int i, int i2) {
        if (eCPointArr == null) {
            throw new IllegalArgumentException("'points' cannot be null");
        }
        if (i < 0 || i2 < 0 || i > eCPointArr.length - i2) {
            throw new IllegalArgumentException("invalid range specified for 'points'");
        }
        for (int i3 = 0; i3 < i2; i3++) {
            ECPoint eCPoint = eCPointArr[i + i3];
            if (null != eCPoint && this != eCPoint.getCurve()) {
                throw new IllegalArgumentException("'points' entries must be null or on this curve");
            }
        }
    }

    public boolean equals(ECCurve eCCurve) {
        return this == eCCurve || (null != eCCurve && getField().equals(eCCurve.getField()) && getA().toBigInteger().equals(eCCurve.getA().toBigInteger()) && getB().toBigInteger().equals(eCCurve.getB().toBigInteger()));
    }

    public boolean equals(Object obj) {
        return this == obj || ((obj instanceof ECCurve) && equals((ECCurve) obj));
    }

    public int hashCode() {
        return (getField().hashCode() ^ Integers.rotateLeft(getA().toBigInteger().hashCode(), 8)) ^ Integers.rotateLeft(getB().toBigInteger().hashCode(), 16);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getNumberOfIterations(int i, int i2) {
        if (i >= 1536) {
            if (i2 <= 100) {
                return 3;
            }
            if (i2 <= 128) {
                return 4;
            }
            return 4 + (((i2 - 128) + 1) / 2);
        }
        if (i >= 1024) {
            if (i2 <= 100) {
                return 4;
            }
            if (i2 <= 112) {
                return 5;
            }
            return 5 + (((i2 - 112) + 1) / 2);
        }
        if (i < 512) {
            if (i2 <= 80) {
                return 40;
            }
            return 40 + (((i2 - 80) + 1) / 2);
        }
        if (i2 <= 80) {
            return 5;
        }
        if (i2 <= 100) {
            return 7;
        }
        return 7 + (((i2 - 100) + 1) / 2);
    }
}
