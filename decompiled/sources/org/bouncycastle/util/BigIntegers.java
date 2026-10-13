package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/BigIntegers.class */
public final class BigIntegers {
    private static final int MAX_ITERATIONS = 1000;
    public static final BigInteger ZERO = BigInteger.valueOf(0);
    public static final BigInteger ONE = BigInteger.valueOf(1);
    public static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private static final int MAX_SMALL = BigInteger.valueOf(743).bitLength();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/BigIntegers$Cache.class */
    public static class Cache {
        private final Map<BigInteger, Boolean> values = new WeakHashMap();
        private final BigInteger[] preserve = new BigInteger[8];
        private int preserveCounter = 0;

        public synchronized void add(BigInteger bigInteger) {
            this.values.put(bigInteger, Boolean.TRUE);
            this.preserve[this.preserveCounter] = bigInteger;
            this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
        }

        public synchronized boolean contains(BigInteger bigInteger) {
            return this.values.containsKey(bigInteger);
        }

        public synchronized int size() {
            return this.values.size();
        }

        public synchronized void clear() {
            this.values.clear();
            for (int i = 0; i != this.preserve.length; i++) {
                this.preserve[i] = null;
            }
        }
    }

    public static byte[] asUnsignedByteArray(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] != 0 || byteArray.length == 1) {
            return byteArray;
        }
        byte[] bArr = new byte[byteArray.length - 1];
        System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
        return bArr;
    }

    public static byte[] asUnsignedByteArray(int i, BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == i) {
            return byteArray;
        }
        int i2 = (byteArray[0] != 0 || byteArray.length == 1) ? 0 : 1;
        int length = byteArray.length - i2;
        if (length > i) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        byte[] bArr = new byte[i];
        System.arraycopy(byteArray, i2, bArr, bArr.length - length, length);
        return bArr;
    }

    public static void asUnsignedByteArray(BigInteger bigInteger, byte[] bArr, int i, int i2) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == i2) {
            System.arraycopy(byteArray, 0, bArr, i, i2);
            return;
        }
        int i3 = (byteArray[0] != 0 || byteArray.length == 1) ? 0 : 1;
        int length = byteArray.length - i3;
        if (length > i2) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        int i4 = i2 - length;
        Arrays.fill(bArr, i, i + i4, (byte) 0);
        System.arraycopy(byteArray, i3, bArr, i + i4, length);
    }

    public static BigInteger createRandomInRange(BigInteger bigInteger, BigInteger bigInteger2, SecureRandom secureRandom) {
        int iCompareTo = bigInteger.compareTo(bigInteger2);
        if (iCompareTo >= 0) {
            if (iCompareTo > 0) {
                throw new IllegalArgumentException("'min' may not be greater than 'max'");
            }
            return bigInteger;
        }
        if (bigInteger.bitLength() > bigInteger2.bitLength() / 2) {
            return createRandomInRange(ZERO, bigInteger2.subtract(bigInteger), secureRandom).add(bigInteger);
        }
        for (int i = 0; i < 1000; i++) {
            BigInteger bigIntegerCreateRandomBigInteger = createRandomBigInteger(bigInteger2.bitLength(), secureRandom);
            if (bigIntegerCreateRandomBigInteger.compareTo(bigInteger) >= 0 && bigIntegerCreateRandomBigInteger.compareTo(bigInteger2) <= 0) {
                return bigIntegerCreateRandomBigInteger;
            }
        }
        return createRandomBigInteger(bigInteger2.subtract(bigInteger).bitLength() - 1, secureRandom).add(bigInteger);
    }

    public static BigInteger fromUnsignedByteArray(byte[] bArr) {
        return new BigInteger(1, bArr);
    }

    public static BigInteger fromUnsignedByteArray(byte[] bArr, int i, int i2) {
        byte[] bArr2 = bArr;
        if (i != 0 || i2 != bArr.length) {
            bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
        }
        return new BigInteger(1, bArr2);
    }

    public static byte byteValueExact(BigInteger bigInteger) {
        if (bigInteger.bitLength() > 7) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return bigInteger.byteValue();
    }

    public static short shortValueExact(BigInteger bigInteger) {
        if (bigInteger.bitLength() > 15) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return bigInteger.shortValue();
    }

    public static int intValueExact(BigInteger bigInteger) {
        if (bigInteger.bitLength() > 31) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return bigInteger.intValue();
    }

    public static long longValueExact(BigInteger bigInteger) {
        if (bigInteger.bitLength() > 63) {
            throw new ArithmeticException("BigInteger out of long range");
        }
        return bigInteger.longValue();
    }

    public static BigInteger modOddInverse(BigInteger bigInteger, BigInteger bigInteger2) {
        if (!bigInteger.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (bigInteger.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (bigInteger2.signum() < 0 || bigInteger2.bitLength() > bigInteger.bitLength()) {
            bigInteger2 = bigInteger2.mod(bigInteger);
        }
        int iBitLength = bigInteger.bitLength();
        int[] iArrFromBigInteger = Nat.fromBigInteger(iBitLength, bigInteger);
        int[] iArrFromBigInteger2 = Nat.fromBigInteger(iBitLength, bigInteger2);
        int length = iArrFromBigInteger.length;
        int[] iArrCreate = Nat.create(length);
        if (0 == Mod.modOddInverse(iArrFromBigInteger, iArrFromBigInteger2, iArrCreate)) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return Nat.toBigInteger(length, iArrCreate);
    }

    public static BigInteger modOddInverseVar(BigInteger bigInteger, BigInteger bigInteger2) {
        if (!bigInteger.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (bigInteger.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (bigInteger.equals(ONE)) {
            return ZERO;
        }
        if (bigInteger2.signum() < 0 || bigInteger2.bitLength() > bigInteger.bitLength()) {
            bigInteger2 = bigInteger2.mod(bigInteger);
        }
        if (bigInteger2.equals(ONE)) {
            return ONE;
        }
        int iBitLength = bigInteger.bitLength();
        int[] iArrFromBigInteger = Nat.fromBigInteger(iBitLength, bigInteger);
        int[] iArrFromBigInteger2 = Nat.fromBigInteger(iBitLength, bigInteger2);
        int length = iArrFromBigInteger.length;
        int[] iArrCreate = Nat.create(length);
        if (Mod.modOddInverseVar(iArrFromBigInteger, iArrFromBigInteger2, iArrCreate)) {
            return Nat.toBigInteger(length, iArrCreate);
        }
        throw new ArithmeticException("BigInteger not invertible.");
    }

    public static boolean modOddIsCoprime(BigInteger bigInteger, BigInteger bigInteger2) {
        if (!bigInteger.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (bigInteger.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (bigInteger2.signum() < 0 || bigInteger2.bitLength() > bigInteger.bitLength()) {
            bigInteger2 = bigInteger2.mod(bigInteger);
        }
        int iBitLength = bigInteger.bitLength();
        return 0 != Mod.modOddIsCoprime(Nat.fromBigInteger(iBitLength, bigInteger), Nat.fromBigInteger(iBitLength, bigInteger2));
    }

    public static boolean modOddIsCoprimeVar(BigInteger bigInteger, BigInteger bigInteger2) {
        if (!bigInteger.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (bigInteger.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (bigInteger2.signum() < 0 || bigInteger2.bitLength() > bigInteger.bitLength()) {
            bigInteger2 = bigInteger2.mod(bigInteger);
        }
        if (bigInteger2.equals(ONE)) {
            return true;
        }
        int iBitLength = bigInteger.bitLength();
        return Mod.modOddIsCoprimeVar(Nat.fromBigInteger(iBitLength, bigInteger), Nat.fromBigInteger(iBitLength, bigInteger2));
    }

    public static int getUnsignedByteLength(BigInteger bigInteger) {
        if (bigInteger.equals(ZERO)) {
            return 1;
        }
        return (bigInteger.bitLength() + 7) / 8;
    }

    public static BigInteger createRandomBigInteger(int i, SecureRandom secureRandom) {
        return new BigInteger(1, createRandom(i, secureRandom));
    }

    public static BigInteger createRandomPrime(int i, int i2, SecureRandom secureRandom) {
        BigInteger bigInteger;
        if (i < 2) {
            throw new IllegalArgumentException("bitLength < 2");
        }
        if (i == 2) {
            return secureRandom.nextInt() < 0 ? TWO : THREE;
        }
        do {
            byte[] bArrCreateRandom = createRandom(i, secureRandom);
            bArrCreateRandom[0] = (byte) (bArrCreateRandom[0] | ((byte) (1 << (7 - ((8 * bArrCreateRandom.length) - i)))));
            int length = bArrCreateRandom.length - 1;
            bArrCreateRandom[length] = (byte) (bArrCreateRandom[length] | 1);
            bigInteger = new BigInteger(1, bArrCreateRandom);
            if (i > MAX_SMALL) {
                while (!bigInteger.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
                    bigInteger = bigInteger.add(TWO);
                }
            }
        } while (!bigInteger.isProbablePrime(i2));
        return bigInteger;
    }

    private static byte[] createRandom(int i, SecureRandom secureRandom) throws IllegalArgumentException {
        if (i < 1) {
            throw new IllegalArgumentException("bitLength must be at least 1");
        }
        int i2 = (i + 7) / 8;
        byte[] bArr = new byte[i2];
        secureRandom.nextBytes(bArr);
        bArr[0] = (byte) (bArr[0] & ((byte) (255 >>> ((8 * i2) - i))));
        return bArr;
    }
}
