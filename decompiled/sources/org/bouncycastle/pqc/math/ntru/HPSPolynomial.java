package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/HPSPolynomial.class */
public class HPSPolynomial extends Polynomial {
    public HPSPolynomial(NTRUHPSParameterSet nTRUHPSParameterSet) {
        super(nTRUHPSParameterSet);
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
            bArr[(11 * i2) + 0] = (byte) (sArr[0] & 255);
            bArr[(11 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 31) << 3));
            bArr[(11 * i2) + 2] = (byte) ((sArr[1] >>> 5) | ((sArr[2] & 3) << 6));
            bArr[(11 * i2) + 3] = (byte) ((sArr[2] >>> 2) & 255);
            bArr[(11 * i2) + 4] = (byte) ((sArr[2] >>> 10) | ((sArr[3] & 127) << 1));
            bArr[(11 * i2) + 5] = (byte) ((sArr[3] >>> 7) | ((sArr[4] & 15) << 4));
            bArr[(11 * i2) + 6] = (byte) ((sArr[4] >>> 4) | ((sArr[5] & 1) << 7));
            bArr[(11 * i2) + 7] = (byte) ((sArr[5] >>> 1) & 255);
            bArr[(11 * i2) + 8] = (byte) ((sArr[5] >>> 9) | ((sArr[6] & 63) << 2));
            bArr[(11 * i2) + 9] = (byte) ((sArr[6] >>> 6) | ((sArr[7] & 7) << 5));
            bArr[(11 * i2) + 10] = (byte) (sArr[7] >>> 3);
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
        switch (this.params.packDegree() & 7) {
            case 2:
                bArr[(11 * i2) + 0] = (byte) (sArr[0] & 255);
                bArr[(11 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 31) << 3));
                bArr[(11 * i2) + 2] = (byte) ((sArr[1] >>> 5) | ((sArr[2] & 3) << 6));
                break;
            case 4:
                bArr[(11 * i2) + 0] = (byte) (sArr[0] & 255);
                bArr[(11 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 31) << 3));
                bArr[(11 * i2) + 2] = (byte) ((sArr[1] >>> 5) | ((sArr[2] & 3) << 6));
                bArr[(11 * i2) + 3] = (byte) ((sArr[2] >>> 2) & 255);
                bArr[(11 * i2) + 4] = (byte) ((sArr[2] >>> 10) | ((sArr[3] & 127) << 1));
                bArr[(11 * i2) + 5] = (byte) ((sArr[3] >>> 7) | ((sArr[4] & 15) << 4));
                break;
        }
        return bArr;
    }

    @Override // org.bouncycastle.pqc.math.ntru.Polynomial
    public void sqFromBytes(byte[] bArr) {
        int length = this.coeffs.length;
        int i = 0;
        while (i < this.params.packDegree() / 8) {
            this.coeffs[(8 * i) + 0] = (short) (((bArr[(11 * i) + 0] & 255) >>> 0) | ((((short) (bArr[(11 * i) + 1] & 255)) & 7) << 8));
            this.coeffs[(8 * i) + 1] = (short) (((bArr[(11 * i) + 1] & 255) >>> 3) | ((((short) (bArr[(11 * i) + 2] & 255)) & 63) << 5));
            this.coeffs[(8 * i) + 2] = (short) (((bArr[(11 * i) + 2] & 255) >>> 6) | ((((short) (bArr[(11 * i) + 3] & 255)) & 255) << 2) | ((((short) (bArr[(11 * i) + 4] & 255)) & 1) << 10));
            this.coeffs[(8 * i) + 3] = (short) (((bArr[(11 * i) + 4] & 255) >>> 1) | ((((short) (bArr[(11 * i) + 5] & 255)) & 15) << 7));
            this.coeffs[(8 * i) + 4] = (short) (((bArr[(11 * i) + 5] & 255) >>> 4) | ((((short) (bArr[(11 * i) + 6] & 255)) & 127) << 4));
            this.coeffs[(8 * i) + 5] = (short) (((bArr[(11 * i) + 6] & 255) >>> 7) | ((((short) (bArr[(11 * i) + 7] & 255)) & 255) << 1) | ((((short) (bArr[(11 * i) + 8] & 255)) & 3) << 9));
            this.coeffs[(8 * i) + 6] = (short) (((bArr[(11 * i) + 8] & 255) >>> 2) | ((((short) (bArr[(11 * i) + 9] & 255)) & 31) << 6));
            this.coeffs[(8 * i) + 7] = (short) (((bArr[(11 * i) + 9] & 255) >>> 5) | ((((short) (bArr[(11 * i) + 10] & 255)) & 255) << 3));
            i++;
        }
        switch (this.params.packDegree() & 7) {
            case 2:
                this.coeffs[(8 * i) + 0] = (short) (((bArr[(11 * i) + 0] & 255) >>> 0) | ((((short) (bArr[(11 * i) + 1] & 255)) & 7) << 8));
                this.coeffs[(8 * i) + 1] = (short) (((bArr[(11 * i) + 1] & 255) >>> 3) | ((((short) (bArr[(11 * i) + 2] & 255)) & 63) << 5));
                break;
            case 4:
                this.coeffs[(8 * i) + 0] = (short) (((bArr[(11 * i) + 0] & 255) >>> 0) | ((((short) (bArr[(11 * i) + 1] & 255)) & 7) << 8));
                this.coeffs[(8 * i) + 1] = (short) (((bArr[(11 * i) + 1] & 255) >>> 3) | ((((short) (bArr[(11 * i) + 2] & 255)) & 63) << 5));
                this.coeffs[(8 * i) + 2] = (short) (((bArr[(11 * i) + 2] & 255) >>> 6) | ((((short) (bArr[(11 * i) + 3] & 255)) & 255) << 2) | ((((short) (bArr[(11 * i) + 4] & 255)) & 1) << 10));
                this.coeffs[(8 * i) + 3] = (short) (((bArr[(11 * i) + 4] & 255) >>> 1) | ((((short) (bArr[(11 * i) + 5] & 255)) & 15) << 7));
                break;
        }
        this.coeffs[length - 1] = 0;
    }

    @Override // org.bouncycastle.pqc.math.ntru.Polynomial
    public void lift(Polynomial polynomial) {
        System.arraycopy(polynomial.coeffs, 0, this.coeffs, 0, this.coeffs.length);
        z3ToZq();
    }
}
