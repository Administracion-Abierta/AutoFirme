package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import java.security.SecureRandom;
import org.bouncycastle.pqc.legacy.math.ntru.util.Util;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/DenseTernaryPolynomial.class */
public class DenseTernaryPolynomial extends IntegerPolynomial implements TernaryPolynomial {
    DenseTernaryPolynomial(int i) {
        super(i);
        checkTernarity();
    }

    public DenseTernaryPolynomial(IntegerPolynomial integerPolynomial) {
        this(integerPolynomial.coeffs);
    }

    public DenseTernaryPolynomial(int[] iArr) {
        super(iArr);
        checkTernarity();
    }

    private void checkTernarity() {
        for (int i = 0; i != this.coeffs.length; i++) {
            int i2 = this.coeffs[i];
            if (i2 < -1 || i2 > 1) {
                throw new IllegalStateException("Illegal value: " + i2 + ", must be one of {-1, 0, 1}");
            }
        }
    }

    public static DenseTernaryPolynomial generateRandom(int i, int i2, int i3, SecureRandom secureRandom) {
        return new DenseTernaryPolynomial(Util.generateRandomTernary(i, i2, i3, secureRandom));
    }

    public static DenseTernaryPolynomial generateRandom(int i, SecureRandom secureRandom) {
        DenseTernaryPolynomial denseTernaryPolynomial = new DenseTernaryPolynomial(i);
        for (int i2 = 0; i2 < i; i2++) {
            denseTernaryPolynomial.coeffs[i2] = secureRandom.nextInt(3) - 1;
        }
        return denseTernaryPolynomial;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.IntegerPolynomial, org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial integerPolynomial, int i) {
        if (i != 2048) {
            return super.mult(integerPolynomial, i);
        }
        IntegerPolynomial integerPolynomial2 = (IntegerPolynomial) integerPolynomial.clone();
        integerPolynomial2.modPositive(2048);
        return new LongPolynomial5(integerPolynomial2).mult(this).toIntegerPolynomial();
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.TernaryPolynomial
    public int[] getOnes() {
        int length = this.coeffs.length;
        int[] iArr = new int[length];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (this.coeffs[i2] == 1) {
                int i3 = i;
                i++;
                iArr[i3] = i2;
            }
        }
        return Arrays.copyOf(iArr, i);
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.TernaryPolynomial
    public int[] getNegOnes() {
        int length = this.coeffs.length;
        int[] iArr = new int[length];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (this.coeffs[i2] == -1) {
                int i3 = i;
                i++;
                iArr[i3] = i2;
            }
        }
        return Arrays.copyOf(iArr, i);
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.TernaryPolynomial
    public int size() {
        return this.coeffs.length;
    }
}
