package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.io.LimitedBuffer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Prehash.class */
public class Prehash implements Digest {
    private final String algorithmName;
    private final LimitedBuffer buf;

    public static Prehash forDigest(Digest digest) {
        return new Prehash(digest);
    }

    private Prehash(Digest digest) {
        this.algorithmName = digest.getAlgorithmName();
        this.buf = new LimitedBuffer(digest.getDigestSize());
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.buf.limit();
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.buf.write(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        this.buf.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        try {
            if (getDigestSize() != this.buf.size()) {
                throw new IllegalStateException("Incorrect prehash size");
            }
            int iCopyTo = this.buf.copyTo(bArr, i);
            reset();
            return iCopyTo;
        } catch (Throwable th) {
            reset();
            throw th;
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.buf.reset();
    }
}
