package org.spongycastle.crypto.digests;

import org.spongycastle.crypto.ExtendedDigest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/ShortenedDigest.class */
public class ShortenedDigest implements ExtendedDigest {
    private ExtendedDigest baseDigest;
    private int length;

    public ShortenedDigest(ExtendedDigest baseDigest, int length) {
        if (baseDigest == null) {
            throw new IllegalArgumentException("baseDigest must not be null");
        }
        if (length > baseDigest.getDigestSize()) {
            throw new IllegalArgumentException("baseDigest output not large enough to support length");
        }
        this.baseDigest = baseDigest;
        this.length = length;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.baseDigest.getAlgorithmName() + "(" + (this.length * 8) + ")";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.length;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this.baseDigest.update(in);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.baseDigest.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        byte[] tmp = new byte[this.baseDigest.getDigestSize()];
        this.baseDigest.doFinal(tmp, 0);
        System.arraycopy(tmp, 0, out, outOff, this.length);
        return this.length;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.baseDigest.reset();
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.baseDigest.getByteLength();
    }
}
