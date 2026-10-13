package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/math/ntru/HPS4096Polynomial.class */
public class HPS4096Polynomial extends HPSPolynomial {
    public HPS4096Polynomial(NTRUHPSParameterSet nTRUHPSParameterSet) {
        super(nTRUHPSParameterSet);
    }

    @Override // org.bouncycastle.pqc.math.ntru.HPSPolynomial, org.bouncycastle.pqc.math.ntru.Polynomial
    public byte[] sqToBytes(int i) {
        byte[] bArr = new byte[i];
        int iQ = this.params.q();
        for (int i2 = 0; i2 < this.params.packDegree() / 2; i2++) {
            bArr[(3 * i2) + 0] = (byte) (modQ(this.coeffs[(2 * i2) + 0] & 65535, iQ) & 255);
            bArr[(3 * i2) + 1] = (byte) ((modQ(this.coeffs[(2 * i2) + 0] & 65535, iQ) >>> 8) | ((modQ(this.coeffs[(2 * i2) + 1] & 65535, iQ) & 15) << 4));
            bArr[(3 * i2) + 2] = (byte) (modQ(this.coeffs[(2 * i2) + 1] & 65535, iQ) >>> 4);
        }
        return bArr;
    }

    @Override // org.bouncycastle.pqc.math.ntru.HPSPolynomial, org.bouncycastle.pqc.math.ntru.Polynomial
    public void sqFromBytes(byte[] bArr) {
        for (int i = 0; i < this.params.packDegree() / 2; i++) {
            this.coeffs[(2 * i) + 0] = (short) (((bArr[(3 * i) + 0] & 255) >>> 0) | ((((short) (bArr[(3 * i) + 1] & 255)) & 15) << 8));
            this.coeffs[(2 * i) + 1] = (short) (((bArr[(3 * i) + 1] & 255) >>> 4) | ((((short) (bArr[(3 * i) + 2] & 255)) & 255) << 4));
        }
        this.coeffs[this.params.n() - 1] = 0;
    }
}
