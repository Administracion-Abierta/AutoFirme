package org.spongycastle.pqc.math.ntru.polynomial;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/BigIntPolynomial.class */
public class BigIntPolynomial {
    private static final double LOG_10_2 = Math.log10(2.0d);
    BigInteger[] coeffs;

    BigIntPolynomial(int N) {
        this.coeffs = new BigInteger[N];
        for (int i = 0; i < N; i++) {
            this.coeffs[i] = Constants.BIGINT_ZERO;
        }
    }

    BigIntPolynomial(BigInteger[] coeffs) {
        this.coeffs = coeffs;
    }

    public BigIntPolynomial(IntegerPolynomial p) {
        this.coeffs = new BigInteger[p.coeffs.length];
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = BigInteger.valueOf(p.coeffs[i]);
        }
    }

    static BigIntPolynomial generateRandomSmall(int N, int numOnes, int numNegOnes) {
        List coeffs = new ArrayList();
        for (int i = 0; i < numOnes; i++) {
            coeffs.add(Constants.BIGINT_ONE);
        }
        for (int i2 = 0; i2 < numNegOnes; i2++) {
            coeffs.add(BigInteger.valueOf(-1L));
        }
        while (coeffs.size() < N) {
            coeffs.add(Constants.BIGINT_ZERO);
        }
        Collections.shuffle(coeffs, new SecureRandom());
        BigIntPolynomial poly = new BigIntPolynomial(N);
        for (int i3 = 0; i3 < coeffs.size(); i3++) {
            poly.coeffs[i3] = (BigInteger) coeffs.get(i3);
        }
        return poly;
    }

    public BigIntPolynomial mult(BigIntPolynomial poly2) {
        int N = this.coeffs.length;
        if (poly2.coeffs.length != N) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        BigIntPolynomial c = multRecursive(poly2);
        if (c.coeffs.length > N) {
            for (int k = N; k < c.coeffs.length; k++) {
                c.coeffs[k - N] = c.coeffs[k - N].add(c.coeffs[k]);
            }
            c.coeffs = Arrays.copyOf(c.coeffs, N);
        }
        return c;
    }

    private BigIntPolynomial multRecursive(BigIntPolynomial poly2) {
        BigInteger[] a = this.coeffs;
        BigInteger[] b = poly2.coeffs;
        int n = poly2.coeffs.length;
        if (n <= 1) {
            BigInteger[] c = Arrays.clone(this.coeffs);
            for (int i = 0; i < this.coeffs.length; i++) {
                c[i] = c[i].multiply(poly2.coeffs[0]);
            }
            return new BigIntPolynomial(c);
        }
        int n1 = n / 2;
        BigIntPolynomial a1 = new BigIntPolynomial(Arrays.copyOf(a, n1));
        BigIntPolynomial a2 = new BigIntPolynomial(Arrays.copyOfRange(a, n1, n));
        BigIntPolynomial b1 = new BigIntPolynomial(Arrays.copyOf(b, n1));
        BigIntPolynomial b2 = new BigIntPolynomial(Arrays.copyOfRange(b, n1, n));
        BigIntPolynomial A = (BigIntPolynomial) a1.clone();
        A.add(a2);
        BigIntPolynomial B = (BigIntPolynomial) b1.clone();
        B.add(b2);
        BigIntPolynomial c1 = a1.multRecursive(b1);
        BigIntPolynomial c2 = a2.multRecursive(b2);
        BigIntPolynomial c3 = A.multRecursive(B);
        c3.sub(c1);
        c3.sub(c2);
        BigIntPolynomial c4 = new BigIntPolynomial((2 * n) - 1);
        for (int i2 = 0; i2 < c1.coeffs.length; i2++) {
            c4.coeffs[i2] = c1.coeffs[i2];
        }
        for (int i3 = 0; i3 < c3.coeffs.length; i3++) {
            c4.coeffs[n1 + i3] = c4.coeffs[n1 + i3].add(c3.coeffs[i3]);
        }
        for (int i4 = 0; i4 < c2.coeffs.length; i4++) {
            c4.coeffs[(2 * n1) + i4] = c4.coeffs[(2 * n1) + i4].add(c2.coeffs[i4]);
        }
        return c4;
    }

    void add(BigIntPolynomial b, BigInteger modulus) {
        add(b);
        mod(modulus);
    }

    public void add(BigIntPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            int N = this.coeffs.length;
            this.coeffs = Arrays.copyOf(this.coeffs, b.coeffs.length);
            for (int i = N; i < this.coeffs.length; i++) {
                this.coeffs[i] = Constants.BIGINT_ZERO;
            }
        }
        for (int i2 = 0; i2 < b.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].add(b.coeffs[i2]);
        }
    }

    public void sub(BigIntPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            int N = this.coeffs.length;
            this.coeffs = Arrays.copyOf(this.coeffs, b.coeffs.length);
            for (int i = N; i < this.coeffs.length; i++) {
                this.coeffs[i] = Constants.BIGINT_ZERO;
            }
        }
        for (int i2 = 0; i2 < b.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].subtract(b.coeffs[i2]);
        }
    }

    public void mult(BigInteger factor) {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].multiply(factor);
        }
    }

    void mult(int factor) {
        mult(BigInteger.valueOf(factor));
    }

    public void div(BigInteger divisor) {
        BigInteger d = divisor.add(Constants.BIGINT_ONE).divide(BigInteger.valueOf(2L));
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].compareTo(Constants.BIGINT_ZERO) > 0 ? this.coeffs[i].add(d) : this.coeffs[i].add(d.negate());
            this.coeffs[i] = this.coeffs[i].divide(divisor);
        }
    }

    public BigDecimalPolynomial div(BigDecimal divisor, int decimalPlaces) {
        BigInteger max = maxCoeffAbs();
        int coeffLength = ((int) (((double) max.bitLength()) * LOG_10_2)) + 1;
        BigDecimal factor = Constants.BIGDEC_ONE.divide(divisor, coeffLength + decimalPlaces + 1, 6);
        BigDecimalPolynomial p = new BigDecimalPolynomial(this.coeffs.length);
        for (int i = 0; i < this.coeffs.length; i++) {
            p.coeffs[i] = new BigDecimal(this.coeffs[i]).multiply(factor).setScale(decimalPlaces, 6);
        }
        return p;
    }

    public int getMaxCoeffLength() {
        return ((int) (((double) maxCoeffAbs().bitLength()) * LOG_10_2)) + 1;
    }

    private BigInteger maxCoeffAbs() {
        BigInteger max = this.coeffs[0].abs();
        for (int i = 1; i < this.coeffs.length; i++) {
            BigInteger coeff = this.coeffs[i].abs();
            if (coeff.compareTo(max) > 0) {
                max = coeff;
            }
        }
        return max;
    }

    public void mod(BigInteger modulus) {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].mod(modulus);
        }
    }

    BigInteger sumCoeffs() {
        BigInteger sum = Constants.BIGINT_ZERO;
        for (int i = 0; i < this.coeffs.length; i++) {
            sum = sum.add(this.coeffs[i]);
        }
        return sum;
    }

    public Object clone() {
        return new BigIntPolynomial((BigInteger[]) this.coeffs.clone());
    }

    public int hashCode() {
        int result = (31 * 1) + Arrays.hashCode(this.coeffs);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BigIntPolynomial other = (BigIntPolynomial) obj;
        if (!Arrays.areEqual(this.coeffs, other.coeffs)) {
            return false;
        }
        return true;
    }

    public BigInteger[] getCoeffs() {
        return Arrays.clone(this.coeffs);
    }
}
