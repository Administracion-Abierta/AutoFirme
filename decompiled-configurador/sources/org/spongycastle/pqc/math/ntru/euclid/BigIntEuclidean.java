package org.spongycastle.pqc.math.ntru.euclid;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/ntru/euclid/BigIntEuclidean.class */
public class BigIntEuclidean {
    public BigInteger x;
    public BigInteger y;
    public BigInteger gcd;

    private BigIntEuclidean() {
    }

    public static BigIntEuclidean calculate(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO;
        BigInteger lastx = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger bigInteger = BigInteger.ZERO;
        while (true) {
            BigInteger lasty = bigInteger;
            if (!b.equals(BigInteger.ZERO)) {
                BigInteger[] quotientAndRemainder = a.divideAndRemainder(b);
                BigInteger quotient = quotientAndRemainder[0];
                a = b;
                b = quotientAndRemainder[1];
                BigInteger temp = x;
                x = lastx.subtract(quotient.multiply(x));
                lastx = temp;
                BigInteger temp2 = y;
                y = lasty.subtract(quotient.multiply(y));
                bigInteger = temp2;
            } else {
                BigIntEuclidean result = new BigIntEuclidean();
                result.x = lastx;
                result.y = lasty;
                result.gcd = a;
                return result;
            }
        }
    }
}
