package org.bouncycastle.crypto.threshold;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/PolynomialNative.class */
class PolynomialNative extends Polynomial {
    private final int IRREDUCIBLE;

    public PolynomialNative(ShamirSecretSplitter.Algorithm algorithm) {
        switch (algorithm) {
            case AES:
                this.IRREDUCIBLE = TIFF.TAG_Y_RESOLUTION;
                return;
            case RSA:
                this.IRREDUCIBLE = TIFF.TAG_PAGE_NAME;
                return;
            default:
                throw new IllegalArgumentException("The algorithm is not correct");
        }
    }

    @Override // org.bouncycastle.crypto.threshold.Polynomial
    protected byte gfMul(int i, int i2) {
        int i3 = 0;
        while (i2 > 0) {
            if ((i2 & 1) != 0) {
                i3 ^= i;
            }
            i <<= 1;
            if ((i & 256) != 0) {
                i ^= this.IRREDUCIBLE;
            }
            i2 >>= 1;
        }
        while (i3 >= 256) {
            if ((i3 & 256) != 0) {
                i3 ^= this.IRREDUCIBLE;
            }
            i3 <<= 1;
        }
        return (byte) (i3 & 255);
    }

    @Override // org.bouncycastle.crypto.threshold.Polynomial
    protected byte gfDiv(int i, int i2) {
        return gfMul(i, gfPow((byte) i2, (byte) -2) & 255);
    }
}
