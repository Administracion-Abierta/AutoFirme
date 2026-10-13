package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Blake3Mac.class */
public class Blake3Mac implements Mac {
    private final Blake3Digest theDigest;

    public Blake3Mac(Blake3Digest blake3Digest) {
        this.theDigest = blake3Digest;
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.theDigest.getAlgorithmName() + "Mac";
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        CipherParameters cipherParametersKey = cipherParameters;
        if (cipherParametersKey instanceof KeyParameter) {
            cipherParametersKey = Blake3Parameters.key(((KeyParameter) cipherParametersKey).getKey());
        }
        if (!(cipherParametersKey instanceof Blake3Parameters)) {
            throw new IllegalArgumentException("Invalid parameter passed to Blake3Mac init - " + cipherParameters.getClass().getName());
        }
        Blake3Parameters blake3Parameters = (Blake3Parameters) cipherParametersKey;
        if (blake3Parameters.getKey() == null) {
            throw new IllegalArgumentException("Blake3Mac requires a key parameter.");
        }
        this.theDigest.init(blake3Parameters);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.theDigest.getDigestSize();
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        this.theDigest.update(b);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        this.theDigest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        return this.theDigest.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        this.theDigest.reset();
    }
}
