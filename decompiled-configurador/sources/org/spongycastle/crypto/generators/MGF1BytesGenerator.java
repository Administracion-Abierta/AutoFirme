package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.MGFParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/MGF1BytesGenerator.class */
public class MGF1BytesGenerator implements DerivationFunction {
    private Digest digest;
    private byte[] seed;
    private int hLen;

    public MGF1BytesGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        if (!(param instanceof MGFParameters)) {
            throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
        }
        MGFParameters p = (MGFParameters) param;
        this.seed = p.getSeed();
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
        if (out.length - len < outOff) {
            throw new OutputLengthException("output buffer too small");
        }
        byte[] hashBuf = new byte[this.hLen];
        byte[] C = new byte[4];
        int counter = 0;
        this.digest.reset();
        if (len > this.hLen) {
            do {
                ItoOSP(counter, C);
                this.digest.update(this.seed, 0, this.seed.length);
                this.digest.update(C, 0, C.length);
                this.digest.doFinal(hashBuf, 0);
                System.arraycopy(hashBuf, 0, out, outOff + (counter * this.hLen), this.hLen);
                counter++;
            } while (counter < len / this.hLen);
        }
        if (counter * this.hLen < len) {
            ItoOSP(counter, C);
            this.digest.update(this.seed, 0, this.seed.length);
            this.digest.update(C, 0, C.length);
            this.digest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, out, outOff + (counter * this.hLen), len - (counter * this.hLen));
        }
        return len;
    }
}
