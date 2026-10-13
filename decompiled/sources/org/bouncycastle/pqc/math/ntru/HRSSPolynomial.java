package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/HRSSPolynomial.class */
public class HRSSPolynomial extends Polynomial {
    public HRSSPolynomial(NTRUHRSSParameterSet nTRUHRSSParameterSet) {
        super(nTRUHRSSParameterSet);
    }

    @Override // org.bouncycastle.pqc.math.ntru.Polynomial
    public byte[] sqToBytes(int i) {
        byte[] bArr = new byte[i];
        short[] sArr = new short[8];
        int i2 = 0;
        while (i2 < this.params.packDegree() / 8) {
            for (int i3 = 0; i3 < 8; i3++) {
                sArr[i3] = (short) modQ(this.coeffs[(8 * i2) + i3] & 65535, this.params.q());
            }
            bArr[(13 * i2) + 0] = (byte) (sArr[0] & 255);
            bArr[(13 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 7) << 5));
            bArr[(13 * i2) + 2] = (byte) ((sArr[1] >>> 3) & 255);
            bArr[(13 * i2) + 3] = (byte) ((sArr[1] >>> 11) | ((sArr[2] & 63) << 2));
            bArr[(13 * i2) + 4] = (byte) ((sArr[2] >>> 6) | ((sArr[3] & 1) << 7));
            bArr[(13 * i2) + 5] = (byte) ((sArr[3] >>> 1) & 255);
            bArr[(13 * i2) + 6] = (byte) ((sArr[3] >>> 9) | ((sArr[4] & 15) << 4));
            bArr[(13 * i2) + 7] = (byte) ((sArr[4] >>> 4) & 255);
            bArr[(13 * i2) + 8] = (byte) ((sArr[4] >>> 12) | ((sArr[5] & 127) << 1));
            bArr[(13 * i2) + 9] = (byte) ((sArr[5] >>> 7) | ((sArr[6] & 3) << 6));
            bArr[(13 * i2) + 10] = (byte) ((sArr[6] >>> 2) & 255);
            bArr[(13 * i2) + 11] = (byte) ((sArr[6] >>> 10) | ((sArr[7] & 31) << 3));
            bArr[(13 * i2) + 12] = (byte) (sArr[7] >>> 5);
            i2++;
        }
        int i4 = 0;
        while (i4 < this.params.packDegree() - (8 * i2)) {
            sArr[i4] = (short) modQ(this.coeffs[(8 * i2) + i4] & 65535, this.params.q());
            i4++;
        }
        while (i4 < 8) {
            sArr[i4] = 0;
            i4++;
        }
        switch (this.params.packDegree() - (8 * (this.params.packDegree() / 8))) {
            case 4:
                bArr[(13 * i2) + 0] = (byte) (sArr[0] & 255);
                bArr[(13 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 7) << 5));
                bArr[(13 * i2) + 2] = (byte) ((sArr[1] >>> 3) & 255);
                bArr[(13 * i2) + 3] = (byte) ((sArr[1] >>> 11) | ((sArr[2] & 63) << 2));
                bArr[(13 * i2) + 4] = (byte) ((sArr[2] >>> 6) | ((sArr[3] & 1) << 7));
                bArr[(13 * i2) + 5] = (byte) ((sArr[3] >>> 1) & 255);
                bArr[(13 * i2) + 6] = (byte) ((sArr[3] >>> 9) | ((sArr[4] & 15) << 4));
            case 2:
                bArr[(13 * i2) + 0] = (byte) (sArr[0] & 255);
                bArr[(13 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 7) << 5));
                bArr[(13 * i2) + 2] = (byte) ((sArr[1] >>> 3) & 255);
                bArr[(13 * i2) + 3] = (byte) ((sArr[1] >>> 11) | ((sArr[2] & 63) << 2));
                break;
        }
        return bArr;
    }

    @Override // org.bouncycastle.pqc.math.ntru.Polynomial
    public void sqFromBytes(byte[] bArr) {
        int i = 0;
        while (i < this.params.packDegree() / 8) {
            this.coeffs[(8 * i) + 0] = (short) ((bArr[(13 * i) + 0] & 255) | ((((short) (bArr[(13 * i) + 1] & 255)) & 31) << 8));
            this.coeffs[(8 * i) + 1] = (short) (((bArr[(13 * i) + 1] & 255) >>> 5) | (((short) (bArr[(13 * i) + 2] & 255)) << 3) | ((((short) (bArr[(13 * i) + 3] & 255)) & 3) << 11));
            this.coeffs[(8 * i) + 2] = (short) (((bArr[(13 * i) + 3] & 255) >>> 2) | ((((short) (bArr[(13 * i) + 4] & 255)) & 127) << 6));
            this.coeffs[(8 * i) + 3] = (short) (((bArr[(13 * i) + 4] & 255) >>> 7) | (((short) (bArr[(13 * i) + 5] & 255)) << 1) | ((((short) (bArr[(13 * i) + 6] & 255)) & 15) << 9));
            this.coeffs[(8 * i) + 4] = (short) (((bArr[(13 * i) + 6] & 255) >>> 4) | (((short) (bArr[(13 * i) + 7] & 255)) << 4) | ((((short) (bArr[(13 * i) + 8] & 255)) & 1) << 12));
            this.coeffs[(8 * i) + 5] = (short) (((bArr[(13 * i) + 8] & 255) >>> 1) | ((((short) (bArr[(13 * i) + 9] & 255)) & 63) << 7));
            this.coeffs[(8 * i) + 6] = (short) (((bArr[(13 * i) + 9] & 255) >>> 6) | (((short) (bArr[(13 * i) + 10] & 255)) << 2) | ((((short) (bArr[(13 * i) + 11] & 255)) & 7) << 10));
            this.coeffs[(8 * i) + 7] = (short) (((bArr[(13 * i) + 11] & 255) >>> 3) | (((short) (bArr[(13 * i) + 12] & 255)) << 5));
            i++;
        }
        switch (this.params.packDegree() & 7) {
            case 2:
                this.coeffs[(8 * i) + 0] = (short) ((bArr[(13 * i) + 0] & 255) | ((((short) (bArr[(13 * i) + 1] & 255)) & 31) << 8));
                this.coeffs[(8 * i) + 1] = (short) (((bArr[(13 * i) + 1] & 255) >>> 5) | (((short) (bArr[(13 * i) + 2] & 255)) << 3) | ((((short) (bArr[(13 * i) + 3] & 255)) & 3) << 11));
                break;
            case 4:
                this.coeffs[(8 * i) + 0] = (short) ((bArr[(13 * i) + 0] & 255) | ((((short) (bArr[(13 * i) + 1] & 255)) & 31) << 8));
                this.coeffs[(8 * i) + 1] = (short) (((bArr[(13 * i) + 1] & 255) >>> 5) | (((short) (bArr[(13 * i) + 2] & 255)) << 3) | ((((short) (bArr[(13 * i) + 3] & 255)) & 3) << 11));
                this.coeffs[(8 * i) + 2] = (short) (((bArr[(13 * i) + 3] & 255) >>> 2) | ((((short) (bArr[(13 * i) + 4] & 255)) & 127) << 6));
                this.coeffs[(8 * i) + 3] = (short) (((bArr[(13 * i) + 4] & 255) >>> 7) | (((short) (bArr[(13 * i) + 5] & 255)) << 1) | ((((short) (bArr[(13 * i) + 6] & 255)) & 15) << 9));
                break;
        }
        this.coeffs[this.params.n() - 1] = 0;
    }

    @Override // org.bouncycastle.pqc.math.ntru.Polynomial
    public void lift(Polynomial polynomial) {
        int length = this.coeffs.length;
        Polynomial polynomialCreatePolynomial = this.params.createPolynomial();
        short s = (short) (3 - (length % 3));
        polynomialCreatePolynomial.coeffs[0] = (short) ((polynomial.coeffs[0] * (2 - s)) + (polynomial.coeffs[1] * 0) + (polynomial.coeffs[2] * s));
        polynomialCreatePolynomial.coeffs[1] = (short) ((polynomial.coeffs[1] * (2 - s)) + (polynomial.coeffs[2] * 0));
        polynomialCreatePolynomial.coeffs[2] = (short) (polynomial.coeffs[2] * (2 - s));
        short s2 = 0;
        for (int i = 3; i < length; i++) {
            short[] sArr = polynomialCreatePolynomial.coeffs;
            sArr[0] = (short) (sArr[0] + (polynomial.coeffs[i] * (s2 + (2 * s))));
            short[] sArr2 = polynomialCreatePolynomial.coeffs;
            sArr2[1] = (short) (sArr2[1] + (polynomial.coeffs[i] * (s2 + s)));
            short[] sArr3 = polynomialCreatePolynomial.coeffs;
            sArr3[2] = (short) (sArr3[2] + (polynomial.coeffs[i] * s2));
            s2 = (short) ((s2 + s) % 3);
        }
        short[] sArr4 = polynomialCreatePolynomial.coeffs;
        sArr4[1] = (short) (sArr4[1] + (polynomial.coeffs[0] * (s2 + s)));
        short[] sArr5 = polynomialCreatePolynomial.coeffs;
        sArr5[2] = (short) (sArr5[2] + (polynomial.coeffs[0] * s2));
        short[] sArr6 = polynomialCreatePolynomial.coeffs;
        sArr6[2] = (short) (sArr6[2] + (polynomial.coeffs[1] * (s2 + s)));
        for (int i2 = 3; i2 < length; i2++) {
            polynomialCreatePolynomial.coeffs[i2] = (short) (polynomialCreatePolynomial.coeffs[i2 - 3] + (2 * (polynomial.coeffs[i2] + polynomial.coeffs[i2 - 1] + polynomial.coeffs[i2 - 2])));
        }
        polynomialCreatePolynomial.mod3PhiN();
        polynomialCreatePolynomial.z3ToZq();
        this.coeffs[0] = (short) (-polynomialCreatePolynomial.coeffs[0]);
        for (int i3 = 0; i3 < length - 1; i3++) {
            this.coeffs[i3 + 1] = (short) (polynomialCreatePolynomial.coeffs[i3] - polynomialCreatePolynomial.coeffs[i3 + 1]);
        }
    }
}
