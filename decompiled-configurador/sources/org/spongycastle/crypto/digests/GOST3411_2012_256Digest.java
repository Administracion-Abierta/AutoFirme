package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/GOST3411_2012_256Digest.class */
public final class GOST3411_2012_256Digest extends GOST3411_2012Digest {
    private static final byte[] IV = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    public GOST3411_2012_256Digest() {
        super(IV);
    }

    public GOST3411_2012_256Digest(GOST3411_2012_256Digest other) {
        super(IV);
        reset(other);
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "GOST3411-2012-256";
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        byte[] result = new byte[64];
        super.doFinal(result, 0);
        System.arraycopy(result, 32, out, outOff, 32);
        return 32;
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.util.Memoable
    public Memoable copy() {
        return new GOST3411_2012_256Digest(this);
    }
}
