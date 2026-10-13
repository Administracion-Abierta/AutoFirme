package org.bouncycastle.crypto.threshold;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/ShamirSplitSecret.class */
public class ShamirSplitSecret implements SplitSecret {
    private final ShamirSplitSecretShare[] secretShares;
    private final Polynomial poly;

    public ShamirSplitSecret(ShamirSecretSplitter.Algorithm algorithm, ShamirSecretSplitter.Mode mode, ShamirSplitSecretShare[] shamirSplitSecretShareArr) {
        this.secretShares = shamirSplitSecretShareArr;
        this.poly = Polynomial.newInstance(algorithm, mode);
    }

    ShamirSplitSecret(Polynomial polynomial, ShamirSplitSecretShare[] shamirSplitSecretShareArr) {
        this.secretShares = shamirSplitSecretShareArr;
        this.poly = polynomial;
    }

    @Override // org.bouncycastle.crypto.threshold.SplitSecret
    public ShamirSplitSecretShare[] getSecretShares() {
        return this.secretShares;
    }

    public ShamirSplitSecret multiple(int i) throws IOException {
        for (int i2 = 0; i2 < this.secretShares.length; i2++) {
            byte[] encoded = this.secretShares[i2].getEncoded();
            for (int i3 = 0; i3 < encoded.length; i3++) {
                encoded[i3] = this.poly.gfMul(encoded[i3] & 255, i);
            }
            this.secretShares[i2] = new ShamirSplitSecretShare(encoded, i2 + 1);
        }
        return this;
    }

    public ShamirSplitSecret divide(int i) throws IOException {
        for (int i2 = 0; i2 < this.secretShares.length; i2++) {
            byte[] encoded = this.secretShares[i2].getEncoded();
            for (int i3 = 0; i3 < encoded.length; i3++) {
                encoded[i3] = this.poly.gfDiv(encoded[i3] & 255, i);
            }
            this.secretShares[i2] = new ShamirSplitSecretShare(encoded, i2 + 1);
        }
        return this;
    }

    @Override // org.bouncycastle.crypto.threshold.SplitSecret
    public byte[] getSecret() throws IOException {
        int length = this.secretShares.length;
        byte[] bArr = new byte[length];
        byte[] bArr2 = new byte[length - 1];
        byte[][] bArr3 = new byte[length][this.secretShares[0].getEncoded().length];
        for (int i = 0; i < length; i++) {
            bArr3[i] = this.secretShares[i].getEncoded();
            byte b = 0;
            for (int i2 = 0; i2 < length; i2++) {
                if (i2 != i) {
                    byte b2 = b;
                    b = (byte) (b + 1);
                    bArr2[b2] = this.poly.gfDiv(this.secretShares[i2].r, this.secretShares[i].r ^ this.secretShares[i2].r);
                }
            }
            byte bGfMul = 1;
            for (int i3 = 0; i3 != bArr2.length; i3++) {
                bGfMul = this.poly.gfMul(bGfMul & 255, bArr2[i3] & 255);
            }
            bArr[i] = bGfMul;
        }
        return this.poly.gfVecMul(bArr, bArr3);
    }
}
