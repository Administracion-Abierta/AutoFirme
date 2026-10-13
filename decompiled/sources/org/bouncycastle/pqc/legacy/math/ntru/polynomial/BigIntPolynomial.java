package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/BigIntPolynomial.class */
public class BigIntPolynomial {
    private static final double LOG_10_2 = Math.log10(2.0d);
    BigInteger[] coeffs;

    BigIntPolynomial(int i) {
        this.coeffs = new BigInteger[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.coeffs[i2] = Constants.BIGINT_ZERO;
        }
    }

    BigIntPolynomial(BigInteger[] bigIntegerArr) {
        this.coeffs = bigIntegerArr;
    }

    public BigIntPolynomial(IntegerPolynomial integerPolynomial) {
        this.coeffs = new BigInteger[integerPolynomial.coeffs.length];
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = BigInteger.valueOf(integerPolynomial.coeffs[i]);
        }
    }

    static BigIntPolynomial generateRandomSmall(int i, int i2, int i3) {
        ArrayList arrayList = new ArrayList();
        for (int i4 = 0; i4 < i2; i4++) {
            arrayList.add(Constants.BIGINT_ONE);
        }
        for (int i5 = 0; i5 < i3; i5++) {
            arrayList.add(BigInteger.valueOf(-1L));
        }
        while (arrayList.size() < i) {
            arrayList.add(Constants.BIGINT_ZERO);
        }
        Collections.shuffle(arrayList, CryptoServicesRegistrar.getSecureRandom());
        BigIntPolynomial bigIntPolynomial = new BigIntPolynomial(i);
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            bigIntPolynomial.coeffs[i6] = (BigInteger) arrayList.get(i6);
        }
        return bigIntPolynomial;
    }

    public BigIntPolynomial mult(BigIntPolynomial bigIntPolynomial) {
        int length = this.coeffs.length;
        if (bigIntPolynomial.coeffs.length != length) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        BigIntPolynomial bigIntPolynomialMultRecursive = multRecursive(bigIntPolynomial);
        if (bigIntPolynomialMultRecursive.coeffs.length > length) {
            for (int i = length; i < bigIntPolynomialMultRecursive.coeffs.length; i++) {
                bigIntPolynomialMultRecursive.coeffs[i - length] = bigIntPolynomialMultRecursive.coeffs[i - length].add(bigIntPolynomialMultRecursive.coeffs[i]);
            }
            bigIntPolynomialMultRecursive.coeffs = Arrays.copyOf(bigIntPolynomialMultRecursive.coeffs, length);
        }
        return bigIntPolynomialMultRecursive;
    }

    private BigIntPolynomial multRecursive(BigIntPolynomial bigIntPolynomial) {
        BigInteger[] bigIntegerArr = this.coeffs;
        BigInteger[] bigIntegerArr2 = bigIntPolynomial.coeffs;
        int length = bigIntPolynomial.coeffs.length;
        if (length <= 1) {
            BigInteger[] bigIntegerArrClone = Arrays.clone(this.coeffs);
            for (int i = 0; i < this.coeffs.length; i++) {
                bigIntegerArrClone[i] = bigIntegerArrClone[i].multiply(bigIntPolynomial.coeffs[0]);
            }
            return new BigIntPolynomial(bigIntegerArrClone);
        }
        int i2 = length / 2;
        BigIntPolynomial bigIntPolynomial2 = new BigIntPolynomial(Arrays.copyOf(bigIntegerArr, i2));
        BigIntPolynomial bigIntPolynomial3 = new BigIntPolynomial(Arrays.copyOfRange(bigIntegerArr, i2, length));
        BigIntPolynomial bigIntPolynomial4 = new BigIntPolynomial(Arrays.copyOf(bigIntegerArr2, i2));
        BigIntPolynomial bigIntPolynomial5 = new BigIntPolynomial(Arrays.copyOfRange(bigIntegerArr2, i2, length));
        BigIntPolynomial bigIntPolynomial6 = (BigIntPolynomial) bigIntPolynomial2.clone();
        bigIntPolynomial6.add(bigIntPolynomial3);
        BigIntPolynomial bigIntPolynomial7 = (BigIntPolynomial) bigIntPolynomial4.clone();
        bigIntPolynomial7.add(bigIntPolynomial5);
        BigIntPolynomial bigIntPolynomialMultRecursive = bigIntPolynomial2.multRecursive(bigIntPolynomial4);
        BigIntPolynomial bigIntPolynomialMultRecursive2 = bigIntPolynomial3.multRecursive(bigIntPolynomial5);
        BigIntPolynomial bigIntPolynomialMultRecursive3 = bigIntPolynomial6.multRecursive(bigIntPolynomial7);
        bigIntPolynomialMultRecursive3.sub(bigIntPolynomialMultRecursive);
        bigIntPolynomialMultRecursive3.sub(bigIntPolynomialMultRecursive2);
        BigIntPolynomial bigIntPolynomial8 = new BigIntPolynomial((2 * length) - 1);
        for (int i3 = 0; i3 < bigIntPolynomialMultRecursive.coeffs.length; i3++) {
            bigIntPolynomial8.coeffs[i3] = bigIntPolynomialMultRecursive.coeffs[i3];
        }
        for (int i4 = 0; i4 < bigIntPolynomialMultRecursive3.coeffs.length; i4++) {
            bigIntPolynomial8.coeffs[i2 + i4] = bigIntPolynomial8.coeffs[i2 + i4].add(bigIntPolynomialMultRecursive3.coeffs[i4]);
        }
        for (int i5 = 0; i5 < bigIntPolynomialMultRecursive2.coeffs.length; i5++) {
            bigIntPolynomial8.coeffs[(2 * i2) + i5] = bigIntPolynomial8.coeffs[(2 * i2) + i5].add(bigIntPolynomialMultRecursive2.coeffs[i5]);
        }
        return bigIntPolynomial8;
    }

    void add(BigIntPolynomial bigIntPolynomial, BigInteger bigInteger) {
        add(bigIntPolynomial);
        mod(bigInteger);
    }

    public void add(BigIntPolynomial bigIntPolynomial) {
        if (bigIntPolynomial.coeffs.length > this.coeffs.length) {
            int length = this.coeffs.length;
            this.coeffs = Arrays.copyOf(this.coeffs, bigIntPolynomial.coeffs.length);
            for (int i = length; i < this.coeffs.length; i++) {
                this.coeffs[i] = Constants.BIGINT_ZERO;
            }
        }
        for (int i2 = 0; i2 < bigIntPolynomial.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].add(bigIntPolynomial.coeffs[i2]);
        }
    }

    public void sub(BigIntPolynomial bigIntPolynomial) {
        if (bigIntPolynomial.coeffs.length > this.coeffs.length) {
            int length = this.coeffs.length;
            this.coeffs = Arrays.copyOf(this.coeffs, bigIntPolynomial.coeffs.length);
            for (int i = length; i < this.coeffs.length; i++) {
                this.coeffs[i] = Constants.BIGINT_ZERO;
            }
        }
        for (int i2 = 0; i2 < bigIntPolynomial.coeffs.length; i2++) {
            this.coeffs[i2] = this.coeffs[i2].subtract(bigIntPolynomial.coeffs[i2]);
        }
    }

    public void mult(BigInteger bigInteger) {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].multiply(bigInteger);
        }
    }

    void mult(int i) {
        mult(BigInteger.valueOf(i));
    }

    public void div(BigInteger bigInteger) {
        BigInteger bigIntegerDivide = bigInteger.add(Constants.BIGINT_ONE).divide(BigInteger.valueOf(2L));
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].compareTo(Constants.BIGINT_ZERO) > 0 ? this.coeffs[i].add(bigIntegerDivide) : this.coeffs[i].add(bigIntegerDivide.negate());
            this.coeffs[i] = this.coeffs[i].divide(bigInteger);
        }
    }

    public BigDecimalPolynomial div(BigDecimal bigDecimal, int i) {
        BigDecimal bigDecimalDivide = Constants.BIGDEC_ONE.divide(bigDecimal, ((int) (((double) maxCoeffAbs().bitLength()) * LOG_10_2)) + 1 + i + 1, 6);
        BigDecimalPolynomial bigDecimalPolynomial = new BigDecimalPolynomial(this.coeffs.length);
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            bigDecimalPolynomial.coeffs[i2] = new BigDecimal(this.coeffs[i2]).multiply(bigDecimalDivide).setScale(i, 6);
        }
        return bigDecimalPolynomial;
    }

    public int getMaxCoeffLength() {
        return ((int) (((double) maxCoeffAbs().bitLength()) * LOG_10_2)) + 1;
    }

    private BigInteger maxCoeffAbs() {
        BigInteger bigIntegerAbs = this.coeffs[0].abs();
        for (int i = 1; i < this.coeffs.length; i++) {
            BigInteger bigIntegerAbs2 = this.coeffs[i].abs();
            if (bigIntegerAbs2.compareTo(bigIntegerAbs) > 0) {
                bigIntegerAbs = bigIntegerAbs2;
            }
        }
        return bigIntegerAbs;
    }

    public void mod(BigInteger bigInteger) {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = this.coeffs[i].mod(bigInteger);
        }
    }

    BigInteger sumCoeffs() {
        BigInteger bigIntegerAdd = Constants.BIGINT_ZERO;
        for (int i = 0; i < this.coeffs.length; i++) {
            bigIntegerAdd = bigIntegerAdd.add(this.coeffs[i]);
        }
        return bigIntegerAdd;
    }

    public Object clone() {
        return new BigIntPolynomial((BigInteger[]) this.coeffs.clone());
    }

    public int hashCode() {
        return (31 * 1) + Arrays.hashCode(this.coeffs);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && Arrays.areEqual(this.coeffs, ((BigIntPolynomial) obj).coeffs);
    }

    public BigInteger[] getCoeffs() {
        return Arrays.clone(this.coeffs);
    }
}
