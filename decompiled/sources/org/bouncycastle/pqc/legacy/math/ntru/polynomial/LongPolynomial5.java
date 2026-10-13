package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/LongPolynomial5.class */
public class LongPolynomial5 {
    private long[] coeffs;
    private int numCoeffs;

    public LongPolynomial5(IntegerPolynomial integerPolynomial) {
        this.numCoeffs = integerPolynomial.coeffs.length;
        this.coeffs = new long[(this.numCoeffs + 4) / 5];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.numCoeffs; i3++) {
            long[] jArr = this.coeffs;
            int i4 = i;
            jArr[i4] = jArr[i4] | (((long) integerPolynomial.coeffs[i3]) << i2);
            i2 += 12;
            if (i2 >= 60) {
                i2 = 0;
                i++;
            }
        }
    }

    private LongPolynomial5(long[] jArr, int i) {
        this.coeffs = jArr;
        this.numCoeffs = i;
    }

    public LongPolynomial5 mult(TernaryPolynomial ternaryPolynomial) {
        long j;
        int i;
        long[][] jArr = new long[5][(this.coeffs.length + ((ternaryPolynomial.size() + 4) / 5)) - 1];
        int[] ones = ternaryPolynomial.getOnes();
        for (int i2 = 0; i2 != ones.length; i2++) {
            int i3 = ones[i2];
            int i4 = i3 / 5;
            int i5 = i3 - (i4 * 5);
            for (int i6 = 0; i6 < this.coeffs.length; i6++) {
                jArr[i5][i4] = (jArr[i5][i4] + this.coeffs[i6]) & 576319980446939135L;
                i4++;
            }
        }
        int[] negOnes = ternaryPolynomial.getNegOnes();
        for (int i7 = 0; i7 != negOnes.length; i7++) {
            int i8 = negOnes[i7];
            int i9 = i8 / 5;
            int i10 = i8 - (i9 * 5);
            for (int i11 = 0; i11 < this.coeffs.length; i11++) {
                jArr[i10][i9] = ((576601524159907840L + jArr[i10][i9]) - this.coeffs[i11]) & 576319980446939135L;
                i9++;
            }
        }
        long[] jArrCopyOf = Arrays.copyOf(jArr[0], jArr[0].length + 1);
        for (int i12 = 1; i12 <= 4; i12++) {
            int i13 = i12 * 12;
            int i14 = 60 - i13;
            long j2 = (1 << i14) - 1;
            int length = jArr[i12].length;
            for (int i15 = 0; i15 < length; i15++) {
                long j3 = jArr[i12][i15] >> i14;
                jArrCopyOf[i15] = (jArrCopyOf[i15] + ((jArr[i12][i15] & j2) << i13)) & 576319980446939135L;
                int i16 = i15 + 1;
                jArrCopyOf[i16] = (jArrCopyOf[i16] + j3) & 576319980446939135L;
            }
        }
        int i17 = 12 * (this.numCoeffs % 5);
        for (int length2 = this.coeffs.length - 1; length2 < jArrCopyOf.length; length2++) {
            if (length2 == this.coeffs.length - 1) {
                j = this.numCoeffs == 5 ? 0L : jArrCopyOf[length2] >> i17;
                i = 0;
            } else {
                j = jArrCopyOf[length2];
                i = (length2 * 5) - this.numCoeffs;
            }
            int i18 = i / 5;
            int i19 = i - (i18 * 5);
            long j4 = j << (12 * i19);
            long j5 = j >> (12 * (5 - i19));
            jArrCopyOf[i18] = (jArrCopyOf[i18] + j4) & 576319980446939135L;
            int i20 = i18 + 1;
            if (i20 < this.coeffs.length) {
                jArrCopyOf[i20] = (jArrCopyOf[i20] + j5) & 576319980446939135L;
            }
        }
        return new LongPolynomial5(jArrCopyOf, this.numCoeffs);
    }

    public IntegerPolynomial toIntegerPolynomial() {
        int[] iArr = new int[this.numCoeffs];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.numCoeffs; i3++) {
            iArr[i3] = (int) ((this.coeffs[i] >> i2) & 2047);
            i2 += 12;
            if (i2 >= 60) {
                i2 = 0;
                i++;
            }
        }
        return new IntegerPolynomial(iArr);
    }
}
