package org.spongycastle.pqc.math.ntru.polynomial;

import java.math.BigDecimal;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/ntru/polynomial/BigDecimalPolynomial.class */
public class BigDecimalPolynomial {
    private static final BigDecimal ZERO = new BigDecimal("0");
    private static final BigDecimal ONE_HALF = new BigDecimal("0.5");
    BigDecimal[] coeffs;

    BigDecimalPolynomial(int N) {
        this.coeffs = new BigDecimal[N];
        for (int i = 0; i < N; i++) {
            this.coeffs[i] = ZERO;
        }
    }

    BigDecimalPolynomial(BigDecimal[] coeffs) {
        this.coeffs = coeffs;
    }

    public BigDecimalPolynomial(BigIntPolynomial p) {
        int N = p.coeffs.length;
        this.coeffs = new BigDecimal[N];
        for (int i = 0; i < N; i++) {
            this.coeffs[i] = new BigDecimal(p.coeffs[i]);
        }
    }

    public void halve() {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].multiply(ONE_HALF);
        }
    }

    public BigDecimalPolynomial mult(BigIntPolynomial poly2) {
        return mult(new BigDecimalPolynomial(poly2));
    }

    public BigDecimalPolynomial mult(BigDecimalPolynomial poly2) {
        int N = this.coeffs.length;
        if (poly2.coeffs.length != N) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        BigDecimalPolynomial c = multRecursive(poly2);
        if (c.coeffs.length > N) {
            for (int k = N; k < c.coeffs.length; k++) {
                c.coeffs[k - N] = c.coeffs[k - N].add(c.coeffs[k]);
            }
            c.coeffs = copyOf(c.coeffs, N);
        }
        return c;
    }

    private BigDecimalPolynomial multRecursive(BigDecimalPolynomial poly2) {
        BigDecimal[] a = this.coeffs;
        BigDecimal[] b = poly2.coeffs;
        int n = poly2.coeffs.length;
        if (n <= 1) {
            BigDecimal[] c = (BigDecimal[]) this.coeffs.clone();
            for (int i = 0; i < this.coeffs.length; i++) {
                c[i] = c[i].multiply(poly2.coeffs[0]);
            }
            return new BigDecimalPolynomial(c);
        }
        int n1 = n / 2;
        BigDecimalPolynomial a1 = new BigDecimalPolynomial(copyOf(a, n1));
        BigDecimalPolynomial a2 = new BigDecimalPolynomial(copyOfRange(a, n1, n));
        BigDecimalPolynomial b1 = new BigDecimalPolynomial(copyOf(b, n1));
        BigDecimalPolynomial b2 = new BigDecimalPolynomial(copyOfRange(b, n1, n));
        BigDecimalPolynomial A = (BigDecimalPolynomial) a1.clone();
        A.add(a2);
        BigDecimalPolynomial B = (BigDecimalPolynomial) b1.clone();
        B.add(b2);
        BigDecimalPolynomial c1 = a1.multRecursive(b1);
        BigDecimalPolynomial c2 = a2.multRecursive(b2);
        BigDecimalPolynomial c3 = A.multRecursive(B);
        c3.sub(c1);
        c3.sub(c2);
        BigDecimalPolynomial c4 = new BigDecimalPolynomial((2 * n) - 1);
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

    public void add(BigDecimalPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            int N = this.coeffs.length;
            this.coeffs = copyOf(this.coeffs, b.coeffs.length);
            for (int i = N; i < this.coeffs.length; i++) {
                this.coeffs[i] = ZERO;
            }
        }
        for (int i2 = 0; i2 < b.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].add(b.coeffs[i2]);
        }
    }

    void sub(BigDecimalPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            int N = this.coeffs.length;
            this.coeffs = copyOf(this.coeffs, b.coeffs.length);
            for (int i = N; i < this.coeffs.length; i++) {
                this.coeffs[i] = ZERO;
            }
        }
        for (int i2 = 0; i2 < b.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].subtract(b.coeffs[i2]);
        }
    }

    public BigIntPolynomial round() {
        int N = this.coeffs.length;
        BigIntPolynomial p = new BigIntPolynomial(N);
        for (int i = 0; i < N; i++) {
            p.coeffs[i] = this.coeffs[i].setScale(0, 6).toBigInteger();
        }
        return p;
    }

    public Object clone() {
        return new BigDecimalPolynomial((BigDecimal[]) this.coeffs.clone());
    }

    private BigDecimal[] copyOf(BigDecimal[] a, int length) {
        BigDecimal[] tmp = new BigDecimal[length];
        System.arraycopy(a, 0, tmp, 0, a.length < length ? a.length : length);
        return tmp;
    }

    private BigDecimal[] copyOfRange(BigDecimal[] a, int from, int to) {
        int newLength = to - from;
        BigDecimal[] tmp = new BigDecimal[to - from];
        System.arraycopy(a, from, tmp, 0, a.length - from < newLength ? a.length - from : newLength);
        return tmp;
    }

    public BigDecimal[] getCoeffs() {
        BigDecimal[] tmp = new BigDecimal[this.coeffs.length];
        System.arraycopy(this.coeffs, 0, tmp, 0, this.coeffs.length);
        return tmp;
    }
}
