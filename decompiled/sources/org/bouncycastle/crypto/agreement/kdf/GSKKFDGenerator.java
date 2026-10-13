package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/kdf/GSKKFDGenerator.class */
public class GSKKFDGenerator implements DigestDerivationFunction {
    private final Digest digest;
    private byte[] z;
    private int counter;
    private byte[] r;
    private byte[] buf;

    public GSKKFDGenerator(Digest digest) {
        this.digest = digest;
        this.buf = new byte[digest.getDigestSize()];
    }

    @Override // org.bouncycastle.crypto.DigestDerivationFunction
    public Digest getDigest() {
        return this.digest;
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public void init(DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof GSKKDFParameters)) {
            throw new IllegalArgumentException("unkown parameters type");
        }
        this.z = ((GSKKDFParameters) derivationParameters).getZ();
        this.counter = ((GSKKDFParameters) derivationParameters).getStartCounter();
        this.r = ((GSKKDFParameters) derivationParameters).getNonce();
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("output buffer too small");
        }
        this.digest.update(this.z, 0, this.z.length);
        int i3 = this.counter;
        this.counter = i3 + 1;
        byte[] bArrIntToBigEndian = Pack.intToBigEndian(i3);
        this.digest.update(bArrIntToBigEndian, 0, bArrIntToBigEndian.length);
        if (this.r != null) {
            this.digest.update(this.r, 0, this.r.length);
        }
        this.digest.doFinal(this.buf, 0);
        System.arraycopy(this.buf, 0, bArr, i, i2);
        Arrays.clear(this.buf);
        return i2;
    }
}
