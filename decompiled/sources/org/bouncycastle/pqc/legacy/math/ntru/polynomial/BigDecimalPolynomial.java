package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import java.math.BigDecimal;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/BigDecimalPolynomial.class */
public class BigDecimalPolynomial {
    private static final BigDecimal ZERO = new BigDecimal("0");
    private static final BigDecimal ONE_HALF = new BigDecimal("0.5");
    BigDecimal[] coeffs;

    BigDecimalPolynomial(int i) {
        this.coeffs = new BigDecimal[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.coeffs[i2] = ZERO;
        }
    }

    BigDecimalPolynomial(BigDecimal[] bigDecimalArr) {
        this.coeffs = bigDecimalArr;
    }

    public BigDecimalPolynomial(BigIntPolynomial bigIntPolynomial) {
        int length = bigIntPolynomial.coeffs.length;
        this.coeffs = new BigDecimal[length];
        for (int i = 0; i < length; i++) {
            this.coeffs[i] = new BigDecimal(bigIntPolynomial.coeffs[i]);
        }
    }

    public void halve() {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].multiply(ONE_HALF);
        }
    }

    public BigDecimalPolynomial mult(BigIntPolynomial bigIntPolynomial) {
        return mult(new BigDecimalPolynomial(bigIntPolynomial));
    }

    public BigDecimalPolynomial mult(BigDecimalPolynomial bigDecimalPolynomial) {
        int length = this.coeffs.length;
        if (bigDecimalPolynomial.coeffs.length != length) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        BigDecimalPolynomial bigDecimalPolynomialMultRecursive = multRecursive(bigDecimalPolynomial);
        if (bigDecimalPolynomialMultRecursive.coeffs.length > length) {
            for (int i = length; i < bigDecimalPolynomialMultRecursive.coeffs.length; i++) {
                bigDecimalPolynomialMultRecursive.coeffs[i - length] = bigDecimalPolynomialMultRecursive.coeffs[i - length].add(bigDecimalPolynomialMultRecursive.coeffs[i]);
            }
            bigDecimalPolynomialMultRecursive.coeffs = copyOf(bigDecimalPolynomialMultRecursive.coeffs, length);
        }
        return bigDecimalPolynomialMultRecursive;
    }

    private BigDecimalPolynomial multRecursive(BigDecimalPolynomial bigDecimalPolynomial) {
        BigDecimal[] bigDecimalArr = this.coeffs;
        BigDecimal[] bigDecimalArr2 = bigDecimalPolynomial.coeffs;
        int length = bigDecimalPolynomial.coeffs.length;
        if (length <= 1) {
            BigDecimal[] bigDecimalArr3 = (BigDecimal[]) this.coeffs.clone();
            for (int i = 0; i < this.coeffs.length; i++) {
                bigDecimalArr3[i] = bigDecimalArr3[i].multiply(bigDecimalPolynomial.coeffs[0]);
            }
            return new BigDecimalPolynomial(bigDecimalArr3);
        }
        int i2 = length / 2;
        BigDecimalPolynomial bigDecimalPolynomial2 = new BigDecimalPolynomial(copyOf(bigDecimalArr, i2));
        BigDecimalPolynomial bigDecimalPolynomial3 = new BigDecimalPolynomial(copyOfRange(bigDecimalArr, i2, length));
        BigDecimalPolynomial bigDecimalPolynomial4 = new BigDecimalPolynomial(copyOf(bigDecimalArr2, i2));
        BigDecimalPolynomial bigDecimalPolynomial5 = new BigDecimalPolynomial(copyOfRange(bigDecimalArr2, i2, length));
        BigDecimalPolynomial bigDecimalPolynomial6 = (BigDecimalPolynomial) bigDecimalPolynomial2.clone();
        bigDecimalPolynomial6.add(bigDecimalPolynomial3);
        BigDecimalPolynomial bigDecimalPolynomial7 = (BigDecimalPolynomial) bigDecimalPolynomial4.clone();
        bigDecimalPolynomial7.add(bigDecimalPolynomial5);
        BigDecimalPolynomial bigDecimalPolynomialMultRecursive = bigDecimalPolynomial2.multRecursive(bigDecimalPolynomial4);
        BigDecimalPolynomial bigDecimalPolynomialMultRecursive2 = bigDecimalPolynomial3.multRecursive(bigDecimalPolynomial5);
        BigDecimalPolynomial bigDecimalPolynomialMultRecursive3 = bigDecimalPolynomial6.multRecursive(bigDecimalPolynomial7);
        bigDecimalPolynomialMultRecursive3.sub(bigDecimalPolynomialMultRecursive);
        bigDecimalPolynomialMultRecursive3.sub(bigDecimalPolynomialMultRecursive2);
        BigDecimalPolynomial bigDecimalPolynomial8 = new BigDecimalPolynomial((2 * length) - 1);
        for (int i3 = 0; i3 < bigDecimalPolynomialMultRecursive.coeffs.length; i3++) {
            bigDecimalPolynomial8.coeffs[i3] = bigDecimalPolynomialMultRecursive.coeffs[i3];
        }
        for (int i4 = 0; i4 < bigDecimalPolynomialMultRecursive3.coeffs.length; i4++) {
            bigDecimalPolynomial8.coeffs[i2 + i4] = bigDecimalPolynomial8.coeffs[i2 + i4].add(bigDecimalPolynomialMultRecursive3.coeffs[i4]);
        }
        for (int i5 = 0; i5 < bigDecimalPolynomialMultRecursive2.coeffs.length; i5++) {
            bigDecimalPolynomial8.coeffs[(2 * i2) + i5] = bigDecimalPolynomial8.coeffs[(2 * i2) + i5].add(bigDecimalPolynomialMultRecursive2.coeffs[i5]);
        }
        return bigDecimalPolynomial8;
    }

    public void add(BigDecimalPolynomial bigDecimalPolynomial) {
        if (bigDecimalPolynomial.coeffs.length > this.coeffs.length) {
            int length = this.coeffs.length;
            this.coeffs = copyOf(this.coeffs, bigDecimalPolynomial.coeffs.length);
            for (int i = length; i < this.coeffs.length; i++) {
                this.coeffs[i] = ZERO;
            }
        }
        for (int i2 = 0; i2 < bigDecimalPolynomial.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].add(bigDecimalPolynomial.coeffs[i2]);
        }
    }

    void sub(BigDecimalPolynomial bigDecimalPolynomial) {
        if (bigDecimalPolynomial.coeffs.length > this.coeffs.length) {
            int length = this.coeffs.length;
            this.coeffs = copyOf(this.coeffs, bigDecimalPolynomial.coeffs.length);
            for (int i = length; i < this.coeffs.length; i++) {
                this.coeffs[i] = ZERO;
            }
        }
        for (int i2 = 0; i2 < bigDecimalPolynomial.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].subtract(bigDecimalPolynomial.coeffs[i2]);
        }
    }

    public BigIntPolynomial round() {
        int length = this.coeffs.length;
        BigIntPolynomial bigIntPolynomial = new BigIntPolynomial(length);
        for (int i = 0; i < length; i++) {
            bigIntPolynomial.coeffs[i] = this.coeffs[i].setScale(0, 6).toBigInteger();
        }
        return bigIntPolynomial;
    }

    public Object clone() {
        return new BigDecimalPolynomial((BigDecimal[]) this.coeffs.clone());
    }

    private BigDecimal[] copyOf(BigDecimal[] bigDecimalArr, int i) {
        BigDecimal[] bigDecimalArr2 = new BigDecimal[i];
        System.arraycopy(bigDecimalArr, 0, bigDecimalArr2, 0, bigDecimalArr.length < i ? bigDecimalArr.length : i);
        return bigDecimalArr2;
    }

    private BigDecimal[] copyOfRange(BigDecimal[] bigDecimalArr, int i, int i2) {
        int i3 = i2 - i;
        BigDecimal[] bigDecimalArr2 = new BigDecimal[i2 - i];
        System.arraycopy(bigDecimalArr, i, bigDecimalArr2, 0, bigDecimalArr.length - i < i3 ? bigDecimalArr.length - i : i3);
        return bigDecimalArr2;
    }

    public BigDecimal[] getCoeffs() {
        BigDecimal[] bigDecimalArr = new BigDecimal[this.coeffs.length];
        System.arraycopy(this.coeffs, 0, bigDecimalArr, 0, this.coeffs.length);
        return bigDecimalArr;
    }
}
