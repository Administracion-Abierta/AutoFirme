package org.spongycastle.crypto.agreement.kdf;

import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KDFParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/agreement/kdf/ConcatenationKDFGenerator.class */
public class ConcatenationKDFGenerator implements DerivationFunction {
    private Digest digest;
    private byte[] shared;
    private byte[] otherInfo;
    private int hLen;

    public ConcatenationKDFGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        if (param instanceof KDFParameters) {
            KDFParameters p = (KDFParameters) param;
            this.shared = p.getSharedSecret();
            this.otherInfo = p.getIV();
            return;
        }
        throw new IllegalArgumentException("KDF parameters required for generator");
    }

    public Digest getDigest() {
        return this.digest;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte) (i >>> 24);
        sp[1] = (byte) (i >>> 16);
        sp[2] = (byte) (i >>> 8);
        sp[3] = (byte) (i >>> 0);
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        int i;
        if (out.length - len < outOff) {
            throw new OutputLengthException("output buffer too small");
        }
        byte[] hashBuf = new byte[this.hLen];
        byte[] C = new byte[4];
        int counter = 1;
        int outputLen = 0;
        this.digest.reset();
        if (len > this.hLen) {
            do {
                ItoOSP(counter, C);
                this.digest.update(C, 0, C.length);
                this.digest.update(this.shared, 0, this.shared.length);
                this.digest.update(this.otherInfo, 0, this.otherInfo.length);
                this.digest.doFinal(hashBuf, 0);
                System.arraycopy(hashBuf, 0, out, outOff + outputLen, this.hLen);
                outputLen += this.hLen;
                i = counter;
                counter++;
            } while (i < len / this.hLen);
        }
        if (outputLen < len) {
            ItoOSP(counter, C);
            this.digest.update(C, 0, C.length);
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(this.otherInfo, 0, this.otherInfo.length);
            this.digest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, out, outOff + outputLen, len - outputLen);
        }
        return len;
    }
}
