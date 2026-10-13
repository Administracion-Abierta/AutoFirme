package org.spongycastle.pqc.math.ntru.polynomial;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.pqc.math.ntru.util.ArrayEncoder;
import org.spongycastle.pqc.math.ntru.util.Util;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/SparseTernaryPolynomial.class */
public class SparseTernaryPolynomial implements TernaryPolynomial {
    private static final int BITS_PER_INDEX = 11;
    private int N;
    private int[] ones;
    private int[] negOnes;

    SparseTernaryPolynomial(int N, int[] ones, int[] negOnes) {
        this.N = N;
        this.ones = ones;
        this.negOnes = negOnes;
    }

    public SparseTernaryPolynomial(IntegerPolynomial intPoly) {
        this(intPoly.coeffs);
    }

    public SparseTernaryPolynomial(int[] coeffs) {
        this.N = coeffs.length;
        this.ones = new int[this.N];
        this.negOnes = new int[this.N];
        int onesIdx = 0;
        int negOnesIdx = 0;
        for (int i = 0; i < this.N; i++) {
            int c = coeffs[i];
            switch (c) {
                case -1:
                    int i2 = negOnesIdx;
                    negOnesIdx++;
                    this.negOnes[i2] = i;
                    break;
                case 0:
                    break;
                case 1:
                    int i3 = onesIdx;
                    onesIdx++;
                    this.ones[i3] = i;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal value: " + c + ", must be one of {-1, 0, 1}");
            }
        }
        this.ones = Arrays.copyOf(this.ones, onesIdx);
        this.negOnes = Arrays.copyOf(this.negOnes, negOnesIdx);
    }

    public static SparseTernaryPolynomial fromBinary(InputStream is, int N, int numOnes, int numNegOnes) throws IOException {
        int bitsPerIndex = 32 - Integer.numberOfLeadingZeros(2048 - 1);
        int data1Len = ((numOnes * bitsPerIndex) + 7) / 8;
        byte[] data1 = Util.readFullLength(is, data1Len);
        int[] ones = ArrayEncoder.decodeModQ(data1, numOnes, 2048);
        int data2Len = ((numNegOnes * bitsPerIndex) + 7) / 8;
        byte[] data2 = Util.readFullLength(is, data2Len);
        int[] negOnes = ArrayEncoder.decodeModQ(data2, numNegOnes, 2048);
        return new SparseTernaryPolynomial(N, ones, negOnes);
    }

    public static SparseTernaryPolynomial generateRandom(int N, int numOnes, int numNegOnes, SecureRandom random) {
        int[] coeffs = Util.generateRandomTernary(N, numOnes, numNegOnes, random);
        return new SparseTernaryPolynomial(coeffs);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial, org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2) {
        int[] b = poly2.coeffs;
        if (b.length != this.N) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        int[] c = new int[this.N];
        for (int idx = 0; idx != this.ones.length; idx++) {
            int i = this.ones[idx];
            int j = (this.N - 1) - i;
            for (int k = this.N - 1; k >= 0; k--) {
                int i2 = k;
                c[i2] = c[i2] + b[j];
                j--;
                if (j < 0) {
                    j = this.N - 1;
                }
            }
        }
        for (int idx2 = 0; idx2 != this.negOnes.length; idx2++) {
            int i3 = this.negOnes[idx2];
            int j2 = (this.N - 1) - i3;
            for (int k2 = this.N - 1; k2 >= 0; k2--) {
                int i4 = k2;
                c[i4] = c[i4] - b[j2];
                j2--;
                if (j2 < 0) {
                    j2 = this.N - 1;
                }
            }
        }
        return new IntegerPolynomial(c);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2, int modulus) {
        IntegerPolynomial c = mult(poly2);
        c.mod(modulus);
        return c;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public BigIntPolynomial mult(BigIntPolynomial poly2) {
        BigInteger[] b = poly2.coeffs;
        if (b.length != this.N) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        BigInteger[] c = new BigInteger[this.N];
        for (int i = 0; i < this.N; i++) {
            c[i] = BigInteger.ZERO;
        }
        for (int idx = 0; idx != this.ones.length; idx++) {
            int i2 = this.ones[idx];
            int j = (this.N - 1) - i2;
            for (int k = this.N - 1; k >= 0; k--) {
                c[k] = c[k].add(b[j]);
                j--;
                if (j < 0) {
                    j = this.N - 1;
                }
            }
        }
        for (int idx2 = 0; idx2 != this.negOnes.length; idx2++) {
            int i3 = this.negOnes[idx2];
            int j2 = (this.N - 1) - i3;
            for (int k2 = this.N - 1; k2 >= 0; k2--) {
                c[k2] = c[k2].subtract(b[j2]);
                j2--;
                if (j2 < 0) {
                    j2 = this.N - 1;
                }
            }
        }
        return new BigIntPolynomial(c);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int[] getOnes() {
        return this.ones;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int[] getNegOnes() {
        return this.negOnes;
    }

    public byte[] toBinary() {
        byte[] bin1 = ArrayEncoder.encodeModQ(this.ones, 2048);
        byte[] bin2 = ArrayEncoder.encodeModQ(this.negOnes, 2048);
        byte[] bin = Arrays.copyOf(bin1, bin1.length + bin2.length);
        System.arraycopy(bin2, 0, bin, bin1.length, bin2.length);
        return bin;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial toIntegerPolynomial() {
        int[] coeffs = new int[this.N];
        for (int idx = 0; idx != this.ones.length; idx++) {
            int i = this.ones[idx];
            coeffs[i] = 1;
        }
        for (int idx2 = 0; idx2 != this.negOnes.length; idx2++) {
            int i2 = this.negOnes[idx2];
            coeffs[i2] = -1;
        }
        return new IntegerPolynomial(coeffs);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public int size() {
        return this.N;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.TernaryPolynomial
    public void clear() {
        for (int i = 0; i < this.ones.length; i++) {
            this.ones[i] = 0;
        }
        for (int i2 = 0; i2 < this.negOnes.length; i2++) {
            this.negOnes[i2] = 0;
        }
    }

    public int hashCode() {
        int result = (31 * 1) + this.N;
        return (31 * ((31 * result) + Arrays.hashCode(this.negOnes))) + Arrays.hashCode(this.ones);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SparseTernaryPolynomial other = (SparseTernaryPolynomial) obj;
        if (this.N != other.N || !Arrays.areEqual(this.negOnes, other.negOnes) || !Arrays.areEqual(this.ones, other.ones)) {
            return false;
        }
        return true;
    }
}
