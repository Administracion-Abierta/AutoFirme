package org.bouncycastle.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/Primes.class */
public abstract class Primes {
    public static final int SMALL_FACTOR_LIMIT = 211;
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/Primes$MROutput.class */
    public static class MROutput {
        private boolean provablyComposite;
        private BigInteger factor;

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput probablyPrime() {
            return new MROutput(false, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeWithFactor(BigInteger bigInteger) {
            return new MROutput(true, bigInteger);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeNotPrimePower() {
            return new MROutput(true, null);
        }

        private MROutput(boolean z, BigInteger bigInteger) {
            this.provablyComposite = z;
            this.factor = bigInteger;
        }

        public BigInteger getFactor() {
            return this.factor;
        }

        public boolean isProvablyComposite() {
            return this.provablyComposite;
        }

        public boolean isNotPrimePower() {
            return this.provablyComposite && this.factor == null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/Primes$STOutput.class */
    public static class STOutput {
        private BigInteger prime;
        private byte[] primeSeed;
        private int primeGenCounter;

        private STOutput(BigInteger bigInteger, byte[] bArr, int i) {
            this.prime = bigInteger;
            this.primeSeed = bArr;
            this.primeGenCounter = i;
        }

        public BigInteger getPrime() {
            return this.prime;
        }

        public byte[] getPrimeSeed() {
            return this.primeSeed;
        }

        public int getPrimeGenCounter() {
            return this.primeGenCounter;
        }
    }

    public static STOutput generateSTRandomPrime(Digest digest, int i, byte[] bArr) {
        if (digest == null) {
            throw new IllegalArgumentException("'hash' cannot be null");
        }
        if (i < 2) {
            throw new IllegalArgumentException("'length' must be >= 2");
        }
        if (bArr == null || bArr.length == 0) {
            throw new IllegalArgumentException("'inputSeed' cannot be null or empty");
        }
        return implSTRandomPrime(digest, i, Arrays.clone(bArr));
    }

    public static MROutput enhancedMRProbablePrimeTest(BigInteger bigInteger, SecureRandom secureRandom, int i) {
        checkCandidate(bigInteger, "candidate");
        if (secureRandom == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (i < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (bigInteger.bitLength() == 2) {
            return MROutput.probablyPrime();
        }
        if (!bigInteger.testBit(0)) {
            return MROutput.provablyCompositeWithFactor(TWO);
        }
        BigInteger bigIntegerSubtract = bigInteger.subtract(ONE);
        BigInteger bigIntegerSubtract2 = bigInteger.subtract(TWO);
        int lowestSetBit = bigIntegerSubtract.getLowestSetBit();
        BigInteger bigIntegerShiftRight = bigIntegerSubtract.shiftRight(lowestSetBit);
        for (int i2 = 0; i2 < i; i2++) {
            BigInteger bigIntegerCreateRandomInRange = BigIntegers.createRandomInRange(TWO, bigIntegerSubtract2, secureRandom);
            BigInteger bigIntegerGcd = bigIntegerCreateRandomInRange.gcd(bigInteger);
            if (bigIntegerGcd.compareTo(ONE) > 0) {
                return MROutput.provablyCompositeWithFactor(bigIntegerGcd);
            }
            BigInteger bigIntegerModPow = bigIntegerCreateRandomInRange.modPow(bigIntegerShiftRight, bigInteger);
            if (!bigIntegerModPow.equals(ONE) && !bigIntegerModPow.equals(bigIntegerSubtract)) {
                boolean z = false;
                BigInteger bigInteger2 = bigIntegerModPow;
                for (int i3 = 1; i3 < lowestSetBit; i3++) {
                    bigIntegerModPow = bigIntegerModPow.modPow(TWO, bigInteger);
                    if (bigIntegerModPow.equals(bigIntegerSubtract)) {
                        z = true;
                        break;
                    }
                    if (bigIntegerModPow.equals(ONE)) {
                        break;
                    }
                    bigInteger2 = bigIntegerModPow;
                }
                if (!z) {
                    if (!bigIntegerModPow.equals(ONE)) {
                        bigInteger2 = bigIntegerModPow;
                        BigInteger bigIntegerModPow2 = bigIntegerModPow.modPow(TWO, bigInteger);
                        if (!bigIntegerModPow2.equals(ONE)) {
                            bigInteger2 = bigIntegerModPow2;
                        }
                    }
                    BigInteger bigIntegerGcd2 = bigInteger2.subtract(ONE).gcd(bigInteger);
                    return bigIntegerGcd2.compareTo(ONE) > 0 ? MROutput.provablyCompositeWithFactor(bigIntegerGcd2) : MROutput.provablyCompositeNotPrimePower();
                }
            }
        }
        return MROutput.probablyPrime();
    }

    public static boolean hasAnySmallFactors(BigInteger bigInteger) {
        checkCandidate(bigInteger, "candidate");
        return implHasAnySmallFactors(bigInteger);
    }

    public static boolean isMRProbablePrime(BigInteger bigInteger, SecureRandom secureRandom, int i) {
        checkCandidate(bigInteger, "candidate");
        if (secureRandom == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (i < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (bigInteger.bitLength() == 2) {
            return true;
        }
        if (!bigInteger.testBit(0)) {
            return false;
        }
        BigInteger bigIntegerSubtract = bigInteger.subtract(ONE);
        BigInteger bigIntegerSubtract2 = bigInteger.subtract(TWO);
        int lowestSetBit = bigIntegerSubtract.getLowestSetBit();
        BigInteger bigIntegerShiftRight = bigIntegerSubtract.shiftRight(lowestSetBit);
        for (int i2 = 0; i2 < i; i2++) {
            if (!implMRProbablePrimeToBase(bigInteger, bigIntegerSubtract, bigIntegerShiftRight, lowestSetBit, BigIntegers.createRandomInRange(TWO, bigIntegerSubtract2, secureRandom))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMRProbablePrimeToBase(BigInteger bigInteger, BigInteger bigInteger2) {
        checkCandidate(bigInteger, "candidate");
        checkCandidate(bigInteger2, "base");
        if (bigInteger2.compareTo(bigInteger.subtract(ONE)) >= 0) {
            throw new IllegalArgumentException("'base' must be < ('candidate' - 1)");
        }
        if (bigInteger.bitLength() == 2) {
            return true;
        }
        BigInteger bigIntegerSubtract = bigInteger.subtract(ONE);
        int lowestSetBit = bigIntegerSubtract.getLowestSetBit();
        return implMRProbablePrimeToBase(bigInteger, bigIntegerSubtract, bigIntegerSubtract.shiftRight(lowestSetBit), lowestSetBit, bigInteger2);
    }

    private static void checkCandidate(BigInteger bigInteger, String str) {
        if (bigInteger == null || bigInteger.signum() < 1 || bigInteger.bitLength() < 2) {
            throw new IllegalArgumentException(OperatorName.SHOW_TEXT_LINE + str + "' must be non-null and >= 2");
        }
    }

    private static boolean implHasAnySmallFactors(BigInteger bigInteger) {
        int iIntValue = bigInteger.mod(BigInteger.valueOf(223092870)).intValue();
        if (iIntValue % 2 == 0 || iIntValue % 3 == 0 || iIntValue % 5 == 0 || iIntValue % 7 == 0 || iIntValue % 11 == 0 || iIntValue % 13 == 0 || iIntValue % 17 == 0 || iIntValue % 19 == 0 || iIntValue % 23 == 0) {
            return true;
        }
        int iIntValue2 = bigInteger.mod(BigInteger.valueOf(58642669)).intValue();
        if (iIntValue2 % 29 == 0 || iIntValue2 % 31 == 0 || iIntValue2 % 37 == 0 || iIntValue2 % 41 == 0 || iIntValue2 % 43 == 0) {
            return true;
        }
        int iIntValue3 = bigInteger.mod(BigInteger.valueOf(600662303)).intValue();
        if (iIntValue3 % 47 == 0 || iIntValue3 % 53 == 0 || iIntValue3 % 59 == 0 || iIntValue3 % 61 == 0 || iIntValue3 % 67 == 0) {
            return true;
        }
        int iIntValue4 = bigInteger.mod(BigInteger.valueOf(33984931)).intValue();
        if (iIntValue4 % 71 == 0 || iIntValue4 % 73 == 0 || iIntValue4 % 79 == 0 || iIntValue4 % 83 == 0) {
            return true;
        }
        int iIntValue5 = bigInteger.mod(BigInteger.valueOf(89809099)).intValue();
        if (iIntValue5 % 89 == 0 || iIntValue5 % 97 == 0 || iIntValue5 % 101 == 0 || iIntValue5 % 103 == 0) {
            return true;
        }
        int iIntValue6 = bigInteger.mod(BigInteger.valueOf(167375713)).intValue();
        if (iIntValue6 % 107 == 0 || iIntValue6 % 109 == 0 || iIntValue6 % 113 == 0 || iIntValue6 % 127 == 0) {
            return true;
        }
        int iIntValue7 = bigInteger.mod(BigInteger.valueOf(371700317)).intValue();
        if (iIntValue7 % 131 == 0 || iIntValue7 % 137 == 0 || iIntValue7 % 139 == 0 || iIntValue7 % 149 == 0) {
            return true;
        }
        int iIntValue8 = bigInteger.mod(BigInteger.valueOf(645328247)).intValue();
        if (iIntValue8 % 151 == 0 || iIntValue8 % 157 == 0 || iIntValue8 % 163 == 0 || iIntValue8 % 167 == 0) {
            return true;
        }
        int iIntValue9 = bigInteger.mod(BigInteger.valueOf(1070560157)).intValue();
        if (iIntValue9 % 173 == 0 || iIntValue9 % 179 == 0 || iIntValue9 % 181 == 0 || iIntValue9 % 191 == 0) {
            return true;
        }
        int iIntValue10 = bigInteger.mod(BigInteger.valueOf(1596463769)).intValue();
        return iIntValue10 % 193 == 0 || iIntValue10 % 197 == 0 || iIntValue10 % 199 == 0 || iIntValue10 % 211 == 0;
    }

    private static boolean implMRProbablePrimeToBase(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, int i, BigInteger bigInteger4) {
        BigInteger bigIntegerModPow = bigInteger4.modPow(bigInteger3, bigInteger);
        if (bigIntegerModPow.equals(ONE) || bigIntegerModPow.equals(bigInteger2)) {
            return true;
        }
        boolean z = false;
        for (int i2 = 1; i2 < i; i2++) {
            bigIntegerModPow = bigIntegerModPow.modPow(TWO, bigInteger);
            if (bigIntegerModPow.equals(bigInteger2)) {
                z = true;
                break;
            }
            if (bigIntegerModPow.equals(ONE)) {
                return false;
            }
        }
        return z;
    }

    private static STOutput implSTRandomPrime(Digest digest, int i, byte[] bArr) {
        int digestSize = digest.getDigestSize();
        if (i < 33) {
            int i2 = 0;
            byte[] bArr2 = new byte[digestSize];
            byte[] bArr3 = new byte[digestSize];
            do {
                hash(digest, bArr, bArr2, 0);
                inc(bArr, 1);
                hash(digest, bArr, bArr3, 0);
                inc(bArr, 1);
                i2++;
                long jExtract32 = ((long) (((extract32(bArr2) ^ extract32(bArr3)) & ((-1) >>> (32 - i))) | (1 << (i - 1)) | 1)) & 4294967295L;
                if (isPrime32(jExtract32)) {
                    return new STOutput(BigInteger.valueOf(jExtract32), bArr, i2);
                }
            } while (i2 <= 4 * i);
            throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
        }
        STOutput sTOutputImplSTRandomPrime = implSTRandomPrime(digest, (i + 3) / 2, bArr);
        BigInteger prime = sTOutputImplSTRandomPrime.getPrime();
        byte[] primeSeed = sTOutputImplSTRandomPrime.getPrimeSeed();
        int primeGenCounter = sTOutputImplSTRandomPrime.getPrimeGenCounter();
        int i3 = (i - 1) / (8 * digestSize);
        BigInteger bit = hashGen(digest, primeSeed, i3 + 1).mod(ONE.shiftLeft(i - 1)).setBit(i - 1);
        BigInteger bigIntegerShiftLeft = prime.shiftLeft(1);
        BigInteger bigIntegerShiftLeft2 = bit.subtract(ONE).divide(bigIntegerShiftLeft).add(ONE).shiftLeft(1);
        int i4 = 0;
        BigInteger bigIntegerAdd = bigIntegerShiftLeft2.multiply(prime).add(ONE);
        while (true) {
            BigInteger bigIntegerAdd2 = bigIntegerAdd;
            if (bigIntegerAdd2.bitLength() > i) {
                bigIntegerShiftLeft2 = ONE.shiftLeft(i - 1).subtract(ONE).divide(bigIntegerShiftLeft).add(ONE).shiftLeft(1);
                bigIntegerAdd2 = bigIntegerShiftLeft2.multiply(prime).add(ONE);
            }
            primeGenCounter++;
            if (implHasAnySmallFactors(bigIntegerAdd2)) {
                inc(primeSeed, i3 + 1);
            } else {
                BigInteger bigIntegerAdd3 = hashGen(digest, primeSeed, i3 + 1).mod(bigIntegerAdd2.subtract(THREE)).add(TWO);
                bigIntegerShiftLeft2 = bigIntegerShiftLeft2.add(BigInteger.valueOf(i4));
                i4 = 0;
                BigInteger bigIntegerModPow = bigIntegerAdd3.modPow(bigIntegerShiftLeft2, bigIntegerAdd2);
                if (bigIntegerAdd2.gcd(bigIntegerModPow.subtract(ONE)).equals(ONE) && bigIntegerModPow.modPow(prime, bigIntegerAdd2).equals(ONE)) {
                    return new STOutput(bigIntegerAdd2, primeSeed, primeGenCounter);
                }
            }
            if (primeGenCounter >= (4 * i) + primeGenCounter) {
                throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
            }
            i4 += 2;
            bigIntegerAdd = bigIntegerAdd2.add(bigIntegerShiftLeft);
        }
    }

    private static int extract32(byte[] bArr) {
        int i = 0;
        int iMin = Math.min(4, bArr.length);
        for (int i2 = 0; i2 < iMin; i2++) {
            i |= (bArr[bArr.length - (i2 + 1)] & 255) << (8 * i2);
        }
        return i;
    }

    private static void hash(Digest digest, byte[] bArr, byte[] bArr2, int i) {
        digest.update(bArr, 0, bArr.length);
        digest.doFinal(bArr2, i);
    }

    private static BigInteger hashGen(Digest digest, byte[] bArr, int i) {
        int digestSize = digest.getDigestSize();
        int i2 = i * digestSize;
        byte[] bArr2 = new byte[i2];
        for (int i3 = 0; i3 < i; i3++) {
            i2 -= digestSize;
            hash(digest, bArr, bArr2, i2);
            inc(bArr, 1);
        }
        return new BigInteger(1, bArr2);
    }

    private static void inc(byte[] bArr, int i) {
        int length = bArr.length;
        while (i > 0) {
            length--;
            if (length < 0) {
                return;
            }
            int i2 = i + (bArr[length] & 255);
            bArr[length] = (byte) i2;
            i = i2 >>> 8;
        }
    }

    private static boolean isPrime32(long j) {
        if ((j >>> 32) != 0) {
            throw new IllegalArgumentException("Size limit exceeded");
        }
        if (j <= 5) {
            return j == 2 || j == 3 || j == 5;
        }
        if ((j & 1) == 0 || j % 3 == 0 || j % 5 == 0) {
            return false;
        }
        long[] jArr = {1, 7, 11, 13, 17, 19, 23, 29};
        long j2 = 0;
        int i = 1;
        while (true) {
            if (i >= jArr.length) {
                j2 += 30;
                if (j2 * j2 >= j) {
                    return true;
                }
                i = 0;
            } else {
                if (j % (j2 + jArr[i]) == 0) {
                    return j < 30;
                }
                i++;
            }
        }
    }
}
