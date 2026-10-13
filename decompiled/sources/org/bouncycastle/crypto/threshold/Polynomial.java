package org.bouncycastle.crypto.threshold;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/Polynomial.class */
abstract class Polynomial {
    Polynomial() {
    }

    public static Polynomial newInstance(ShamirSecretSplitter.Algorithm algorithm, ShamirSecretSplitter.Mode mode) {
        return mode == ShamirSecretSplitter.Mode.Native ? new PolynomialNative(algorithm) : new PolynomialTable(algorithm);
    }

    protected abstract byte gfMul(int i, int i2);

    protected abstract byte gfDiv(int i, int i2);

    protected byte gfPow(int i, byte b) {
        byte bGfMul = 1;
        for (int i2 = 0; i2 < 8; i2++) {
            if ((b & (1 << i2)) != 0) {
                bGfMul = gfMul(bGfMul & 255, i & 255);
            }
            i = gfMul(i & 255, i & 255);
        }
        return bGfMul;
    }

    public byte[] gfVecMul(byte[] bArr, byte[][] bArr2) {
        byte[] bArr3 = new byte[bArr2[0].length];
        for (int i = 0; i < bArr2[0].length; i++) {
            int iGfMul = 0;
            for (int i2 = 0; i2 < bArr.length; i2++) {
                iGfMul ^= gfMul(bArr[i2] & 255, bArr2[i2][i] & 255);
            }
            bArr3[i] = (byte) iGfMul;
        }
        return bArr3;
    }
}
