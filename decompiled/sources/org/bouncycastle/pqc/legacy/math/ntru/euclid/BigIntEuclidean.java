package org.bouncycastle.pqc.legacy.math.ntru.euclid;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/euclid/BigIntEuclidean.class */
public class BigIntEuclidean {
    public BigInteger x;
    public BigInteger y;
    public BigInteger gcd;

    private BigIntEuclidean() {
    }

    public static BigIntEuclidean calculate(BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger bigIntegerSubtract = BigInteger.ZERO;
        BigInteger bigInteger3 = BigInteger.ONE;
        BigInteger bigIntegerSubtract2 = BigInteger.ONE;
        BigInteger bigInteger4 = BigInteger.ZERO;
        while (true) {
            BigInteger bigInteger5 = bigInteger4;
            if (bigInteger2.equals(BigInteger.ZERO)) {
                BigIntEuclidean bigIntEuclidean = new BigIntEuclidean();
                bigIntEuclidean.x = bigInteger3;
                bigIntEuclidean.y = bigInteger5;
                bigIntEuclidean.gcd = bigInteger;
                return bigIntEuclidean;
            }
            BigInteger[] bigIntegerArrDivideAndRemainder = bigInteger.divideAndRemainder(bigInteger2);
            BigInteger bigInteger6 = bigIntegerArrDivideAndRemainder[0];
            bigInteger = bigInteger2;
            bigInteger2 = bigIntegerArrDivideAndRemainder[1];
            BigInteger bigInteger7 = bigIntegerSubtract;
            bigIntegerSubtract = bigInteger3.subtract(bigInteger6.multiply(bigIntegerSubtract));
            bigInteger3 = bigInteger7;
            BigInteger bigInteger8 = bigIntegerSubtract2;
            bigIntegerSubtract2 = bigInteger5.subtract(bigInteger6.multiply(bigIntegerSubtract2));
            bigInteger4 = bigInteger8;
        }
    }
}
