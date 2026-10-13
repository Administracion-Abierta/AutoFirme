package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/kdf/ConcatenationKDFGenerator.class */
public class ConcatenationKDFGenerator implements DerivationFunction {
    private Digest digest;
    private byte[] shared;
    private byte[] otherInfo;
    private int hLen;

    public ConcatenationKDFGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public void init(DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof KDFParameters)) {
            throw new IllegalArgumentException("KDF parameters required for generator");
        }
        KDFParameters kDFParameters = (KDFParameters) derivationParameters;
        this.shared = kDFParameters.getSharedSecret();
        this.otherInfo = kDFParameters.getIV();
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
        int i3;
        if (i2 <= 0) {
            throw new IllegalArgumentException("len must be > 0");
        }
        if (bArr.length - i2 < i) {
            throw new OutputLengthException("output buffer too small");
        }
        byte[] bArr2 = new byte[this.hLen];
        byte[] bArr3 = new byte[4];
        int i4 = 1;
        int i5 = 0;
        this.digest.reset();
        if (i2 > this.hLen) {
            do {
                ItoOSP(i4, bArr3);
                this.digest.update(bArr3, 0, bArr3.length);
                this.digest.update(this.shared, 0, this.shared.length);
                this.digest.update(this.otherInfo, 0, this.otherInfo.length);
                this.digest.doFinal(bArr2, 0);
                System.arraycopy(bArr2, 0, bArr, i + i5, this.hLen);
                i5 += this.hLen;
                i3 = i4;
                i4++;
            } while (i3 < i2 / this.hLen);
        }
        if (i5 < i2) {
            ItoOSP(i4, bArr3);
            this.digest.update(bArr3, 0, bArr3.length);
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(this.otherInfo, 0, this.otherInfo.length);
            this.digest.doFinal(bArr2, 0);
            System.arraycopy(bArr2, 0, bArr, i + i5, i2 - i5);
        }
        return i2;
    }
}
