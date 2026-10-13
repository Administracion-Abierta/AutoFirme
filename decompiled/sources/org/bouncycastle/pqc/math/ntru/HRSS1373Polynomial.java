package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/HRSS1373Polynomial.class */
public class HRSS1373Polynomial extends HRSSPolynomial {
    private static final int L = 1376;
    private static final int M = 344;
    private static final int K = 86;

    public HRSS1373Polynomial(NTRUHRSSParameterSet nTRUHRSSParameterSet) {
        super(nTRUHRSSParameterSet);
    }

    @Override // org.bouncycastle.pqc.math.ntru.HRSSPolynomial, org.bouncycastle.pqc.math.ntru.Polynomial
    public byte[] sqToBytes(int i) {
        byte[] bArr = new byte[i];
        short[] sArr = new short[4];
        int i2 = 0;
        while (i2 < this.params.packDegree() / 4) {
            for (int i3 = 0; i3 < 4; i3++) {
                sArr[i3] = (short) modQ(this.coeffs[(4 * i2) + i3] & 65535, this.params.q());
            }
            bArr[(7 * i2) + 0] = (byte) (sArr[0] & 255);
            bArr[(7 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 3) << 6));
            bArr[(7 * i2) + 2] = (byte) ((sArr[1] >>> 2) & 255);
            bArr[(7 * i2) + 3] = (byte) ((sArr[1] >>> 10) | ((sArr[2] & 15) << 4));
            bArr[(7 * i2) + 4] = (byte) ((sArr[2] >>> 4) & 255);
            bArr[(7 * i2) + 5] = (byte) ((sArr[2] >>> 12) | ((sArr[3] & 63) << 2));
            bArr[(7 * i2) + 6] = (byte) (sArr[3] >>> 6);
            i2++;
        }
        if (this.params.packDegree() % 4 == 2) {
            sArr[0] = (short) modQ(this.coeffs[this.params.packDegree() - 2] & 65535, this.params.q());
            sArr[1] = (short) modQ(this.coeffs[this.params.packDegree() - 1] & 65535, this.params.q());
            bArr[(7 * i2) + 0] = (byte) (sArr[0] & 255);
            bArr[(7 * i2) + 1] = (byte) ((sArr[0] >>> 8) | ((sArr[1] & 3) << 6));
            bArr[(7 * i2) + 2] = (byte) ((sArr[1] >>> 2) & 255);
            bArr[(7 * i2) + 3] = (byte) (sArr[1] >>> 10);
        }
        return bArr;
    }

    @Override // org.bouncycastle.pqc.math.ntru.HRSSPolynomial, org.bouncycastle.pqc.math.ntru.Polynomial
    public void sqFromBytes(byte[] bArr) {
        int i = 0;
        while (i < this.params.packDegree() / 4) {
            this.coeffs[(4 * i) + 0] = (short) ((bArr[(7 * i) + 0] & 255) | ((((short) (bArr[(7 * i) + 1] & 255)) & 63) << 8));
            this.coeffs[(4 * i) + 1] = (short) (((bArr[(7 * i) + 1] & 255) >>> 6) | (((short) (bArr[(7 * i) + 2] & 255)) << 2) | (((short) (bArr[(7 * i) + 3] & 15)) << 10));
            this.coeffs[(4 * i) + 2] = (short) (((bArr[(7 * i) + 3] & 255) >>> 4) | ((((short) (bArr[(7 * i) + 4] & 255)) & 255) << 4) | (((short) (bArr[(7 * i) + 5] & 3)) << 12));
            this.coeffs[(4 * i) + 3] = (short) (((bArr[(7 * i) + 5] & 255) >>> 2) | (((short) (bArr[(7 * i) + 6] & 255)) << 6));
            i++;
        }
        if (this.params.packDegree() % 4 == 2) {
            this.coeffs[(4 * i) + 0] = (short) (bArr[(7 * i) + 0] | ((bArr[(7 * i) + 1] & 63) << 8));
            this.coeffs[(4 * i) + 1] = (short) ((bArr[(7 * i) + 1] >>> 6) | (bArr[(7 * i) + 2] << 2) | ((bArr[(7 * i) + 3] & 15) << 10));
        }
        this.coeffs[this.params.n() - 1] = 0;
    }
}
