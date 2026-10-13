package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.MGFParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/MGF1BytesGenerator.class */
public class MGF1BytesGenerator implements DerivationFunction {
    private Digest digest;
    private byte[] seed;
    private int hLen;

    public MGF1BytesGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public void init(DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof MGFParameters)) {
            throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
        }
        this.seed = ((MGFParameters) derivationParameters).getSeed();
    }

    public Digest getDigest() {
        return this.digest;
    }

    private void ItoOSP(int i, byte[] bArr) {
        bArr[0] = (byte) (i >>> 24);
        bArr[1] = (byte) (i >>> 16);
        bArr[2] = (byte) (i >>> 8);
        bArr[3] = (byte) (i >>> 0);
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException {
        if (bArr.length - i2 < i) {
            throw new OutputLengthException("output buffer too small");
        }
        byte[] bArr2 = new byte[this.hLen];
        byte[] bArr3 = new byte[4];
        int i3 = 0;
        this.digest.reset();
        if (i2 > this.hLen) {
            do {
                ItoOSP(i3, bArr3);
                this.digest.update(this.seed, 0, this.seed.length);
                this.digest.update(bArr3, 0, bArr3.length);
                this.digest.doFinal(bArr2, 0);
                System.arraycopy(bArr2, 0, bArr, i + (i3 * this.hLen), this.hLen);
                i3++;
            } while (i3 < i2 / this.hLen);
        }
        if (i3 * this.hLen < i2) {
            ItoOSP(i3, bArr3);
            this.digest.update(this.seed, 0, this.seed.length);
            this.digest.update(bArr3, 0, bArr3.length);
            this.digest.doFinal(bArr2, 0);
            System.arraycopy(bArr2, 0, bArr, i + (i3 * this.hLen), i2 - (i3 * this.hLen));
        }
        return i2;
    }
}
