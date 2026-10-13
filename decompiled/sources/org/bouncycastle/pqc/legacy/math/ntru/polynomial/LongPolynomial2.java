package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/LongPolynomial2.class */
public class LongPolynomial2 {
    private long[] coeffs;
    private int numCoeffs;

    public LongPolynomial2(IntegerPolynomial integerPolynomial) {
        long j;
        long j2;
        this.numCoeffs = integerPolynomial.coeffs.length;
        this.coeffs = new long[(this.numCoeffs + 1) / 2];
        int i = 0;
        int i2 = 0;
        while (i2 < this.numCoeffs) {
            int i3 = i2;
            i2++;
            int i4 = integerPolynomial.coeffs[i3];
            while (i4 < 0) {
                i4 += 2048;
            }
            if (i2 < this.numCoeffs) {
                i2++;
                j = integerPolynomial.coeffs[i2];
            } else {
                j = 0;
            }
            while (true) {
                j2 = j;
                j = j2 < 0 ? j2 + 2048 : j;
            }
            this.coeffs[i] = ((long) i4) + (j2 << 24);
            i++;
        }
    }

    private LongPolynomial2(long[] jArr) {
        this.coeffs = jArr;
    }

    private LongPolynomial2(int i) {
        this.coeffs = new long[i];
    }

    public LongPolynomial2 mult(LongPolynomial2 longPolynomial2) {
        int length = this.coeffs.length;
        if (longPolynomial2.coeffs.length != length || this.numCoeffs != longPolynomial2.numCoeffs) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        LongPolynomial2 longPolynomial2MultRecursive = multRecursive(longPolynomial2);
        if (longPolynomial2MultRecursive.coeffs.length > length) {
            if (this.numCoeffs % 2 == 0) {
                for (int i = length; i < longPolynomial2MultRecursive.coeffs.length; i++) {
                    longPolynomial2MultRecursive.coeffs[i - length] = (longPolynomial2MultRecursive.coeffs[i - length] + longPolynomial2MultRecursive.coeffs[i]) & 34342963199L;
                }
                longPolynomial2MultRecursive.coeffs = Arrays.copyOf(longPolynomial2MultRecursive.coeffs, length);
            } else {
                for (int i2 = length; i2 < longPolynomial2MultRecursive.coeffs.length; i2++) {
                    longPolynomial2MultRecursive.coeffs[i2 - length] = longPolynomial2MultRecursive.coeffs[i2 - length] + (longPolynomial2MultRecursive.coeffs[i2 - 1] >> 24);
                    longPolynomial2MultRecursive.coeffs[i2 - length] = longPolynomial2MultRecursive.coeffs[i2 - length] + ((longPolynomial2MultRecursive.coeffs[i2] & 2047) << 24);
                    long[] jArr = longPolynomial2MultRecursive.coeffs;
                    int i3 = i2 - length;
                    jArr[i3] = jArr[i3] & 34342963199L;
                }
                longPolynomial2MultRecursive.coeffs = Arrays.copyOf(longPolynomial2MultRecursive.coeffs, length);
                long[] jArr2 = longPolynomial2MultRecursive.coeffs;
                int length2 = longPolynomial2MultRecursive.coeffs.length - 1;
                jArr2[length2] = jArr2[length2] & 2047;
            }
        }
        LongPolynomial2 longPolynomial3 = new LongPolynomial2(longPolynomial2MultRecursive.coeffs);
        longPolynomial3.numCoeffs = this.numCoeffs;
        return longPolynomial3;
    }

    public IntegerPolynomial toIntegerPolynomial() {
        int[] iArr = new int[this.numCoeffs];
        int i = 0;
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int i3 = i;
            i++;
            iArr[i3] = (int) (this.coeffs[i2] & 2047);
            if (i < this.numCoeffs) {
                i++;
                iArr[i] = (int) ((this.coeffs[i2] >> 24) & 2047);
            }
        }
        return new IntegerPolynomial(iArr);
    }

    private LongPolynomial2 multRecursive(LongPolynomial2 longPolynomial2) {
        long[] jArr = this.coeffs;
        long[] jArr2 = longPolynomial2.coeffs;
        int length = longPolynomial2.coeffs.length;
        if (length <= 32) {
            int i = 2 * length;
            LongPolynomial2 longPolynomial3 = new LongPolynomial2(new long[i]);
            for (int i2 = 0; i2 < i; i2++) {
                for (int iMax = Math.max(0, (i2 - length) + 1); iMax <= Math.min(i2, length - 1); iMax++) {
                    long j = jArr[i2 - iMax] * jArr2[iMax];
                    longPolynomial3.coeffs[i2] = (longPolynomial3.coeffs[i2] + (j & (34342961152L + (j & 2047)))) & 34342963199L;
                    longPolynomial3.coeffs[i2 + 1] = (longPolynomial3.coeffs[i2 + 1] + ((j >>> 48) & 2047)) & 34342963199L;
                }
            }
            return longPolynomial3;
        }
        int i3 = length / 2;
        LongPolynomial2 longPolynomial4 = new LongPolynomial2(Arrays.copyOf(jArr, i3));
        LongPolynomial2 longPolynomial5 = new LongPolynomial2(Arrays.copyOfRange(jArr, i3, length));
        LongPolynomial2 longPolynomial6 = new LongPolynomial2(Arrays.copyOf(jArr2, i3));
        LongPolynomial2 longPolynomial7 = new LongPolynomial2(Arrays.copyOfRange(jArr2, i3, length));
        LongPolynomial2 longPolynomial8 = (LongPolynomial2) longPolynomial4.clone();
        longPolynomial8.add(longPolynomial5);
        LongPolynomial2 longPolynomial9 = (LongPolynomial2) longPolynomial6.clone();
        longPolynomial9.add(longPolynomial7);
        LongPolynomial2 longPolynomial2MultRecursive = longPolynomial4.multRecursive(longPolynomial6);
        LongPolynomial2 longPolynomial2MultRecursive2 = longPolynomial5.multRecursive(longPolynomial7);
        LongPolynomial2 longPolynomial2MultRecursive3 = longPolynomial8.multRecursive(longPolynomial9);
        longPolynomial2MultRecursive3.sub(longPolynomial2MultRecursive);
        longPolynomial2MultRecursive3.sub(longPolynomial2MultRecursive2);
        LongPolynomial2 longPolynomial10 = new LongPolynomial2(2 * length);
        for (int i4 = 0; i4 < longPolynomial2MultRecursive.coeffs.length; i4++) {
            longPolynomial10.coeffs[i4] = longPolynomial2MultRecursive.coeffs[i4] & 34342963199L;
        }
        for (int i5 = 0; i5 < longPolynomial2MultRecursive3.coeffs.length; i5++) {
            longPolynomial10.coeffs[i3 + i5] = (longPolynomial10.coeffs[i3 + i5] + longPolynomial2MultRecursive3.coeffs[i5]) & 34342963199L;
        }
        for (int i6 = 0; i6 < longPolynomial2MultRecursive2.coeffs.length; i6++) {
            longPolynomial10.coeffs[(2 * i3) + i6] = (longPolynomial10.coeffs[(2 * i3) + i6] + longPolynomial2MultRecursive2.coeffs[i6]) & 34342963199L;
        }
        return longPolynomial10;
    }

    private void add(LongPolynomial2 longPolynomial2) {
        if (longPolynomial2.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, longPolynomial2.coeffs.length);
        }
        for (int i = 0; i < longPolynomial2.coeffs.length; i++) {
            this.coeffs[i] = (this.coeffs[i] + longPolynomial2.coeffs[i]) & 34342963199L;
        }
    }

    private void sub(LongPolynomial2 longPolynomial2) {
        if (longPolynomial2.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, longPolynomial2.coeffs.length);
        }
        for (int i = 0; i < longPolynomial2.coeffs.length; i++) {
            this.coeffs[i] = ((140737496743936L + this.coeffs[i]) - longPolynomial2.coeffs[i]) & 34342963199L;
        }
    }

    public void subAnd(LongPolynomial2 longPolynomial2, int i) {
        long j = (((long) i) << 24) + ((long) i);
        for (int i2 = 0; i2 < longPolynomial2.coeffs.length; i2++) {
            this.coeffs[i2] = ((140737496743936L + this.coeffs[i2]) - longPolynomial2.coeffs[i2]) & j;
        }
    }

    public void mult2And(int i) {
        long j = (((long) i) << 24) + ((long) i);
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            this.coeffs[i2] = (this.coeffs[i2] << 1) & j;
        }
    }

    public Object clone() {
        LongPolynomial2 longPolynomial2 = new LongPolynomial2((long[]) this.coeffs.clone());
        longPolynomial2.numCoeffs = this.numCoeffs;
        return longPolynomial2;
    }

    public boolean equals(Object obj) {
        if (obj instanceof LongPolynomial2) {
            return Arrays.areEqual(this.coeffs, ((LongPolynomial2) obj).coeffs);
        }
        return false;
    }
}
