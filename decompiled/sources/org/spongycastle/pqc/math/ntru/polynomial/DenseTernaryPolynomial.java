package org.spongycastle.pqc.math.ntru.polynomial;

import java.security.SecureRandom;
import org.spongycastle.pqc.math.ntru.util.Util;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/DenseTernaryPolynomial.class */
public class DenseTernaryPolynomial extends IntegerPolynomial implements TernaryPolynomial {
    DenseTernaryPolynomial(int N) {
        super(N);
        checkTernarity();
    }

    public DenseTernaryPolynomial(IntegerPolynomial intPoly) {
        this(intPoly.coeffs);
    }

    public DenseTernaryPolynomial(int[] coeffs) {
        super(coeffs);
        checkTernarity();
    }

    private void checkTernarity() {
        for (int i = 0; i != this.coeffs.length; i++) {
            int c = this.coeffs[i];
            if (c < -1 || c > 1) {
                throw new IllegalStateException("Illegal value: " + c + ", must be one of {-1, 0, 1}");
            }
        }
    }

    public static DenseTernaryPolynomial generateRandom(int N, int numOnes, int numNegOnes, SecureRandom random) {
        int[] coeffs = Util.generateRandomTernary(N, numOnes, numNegOnes, random);
        return new DenseTernaryPolynomial(coeffs);
    }

    public static DenseTernaryPolynomial generateRandom(int N, SecureRandom random) {
        DenseTernaryPolynomial poly = new DenseTernaryPolynomial(N);
        for (int i = 0; i < N; i++) {
            poly.coeffs[i] = random.nextInt(3) - 1;
        }
        return poly;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial, org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2, int modulus) {
        if (modulus == 2048) {
            IntegerPolynomial poly2Pos = (IntegerPolynomial) poly2.clone();
            poly2Pos.modPositive(2048);
            LongPolynomial5 poly5 = new LongPolynomial5(poly2Pos);
            return poly5.mult(this).toIntegerPolynomial();
        }
        return super.mult(poly2, modulus);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int[] getOnes() {
        int N = this.coeffs.length;
        int[] ones = new int[N];
        int onesIdx = 0;
        for (int i = 0; i < N; i++) {
            int c = this.coeffs[i];
            if (c == 1) {
                int i2 = onesIdx;
                onesIdx++;
                ones[i2] = i;
            }
        }
        return Arrays.copyOf(ones, onesIdx);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int[] getNegOnes() {
        int N = this.coeffs.length;
        int[] negOnes = new int[N];
        int negOnesIdx = 0;
        for (int i = 0; i < N; i++) {
            int c = this.coeffs[i];
            if (c == -1) {
                int i2 = negOnesIdx;
                negOnesIdx++;
                negOnes[i2] = i;
            }
        }
        return Arrays.copyOf(negOnes, negOnesIdx);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int size() {
        return this.coeffs.length;
    }
}
