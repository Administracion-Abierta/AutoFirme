package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/GOST3411_2012_512Digest.class */
public class GOST3411_2012_512Digest extends GOST3411_2012Digest {
    private static final byte[] IV = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public GOST3411_2012_512Digest() {
        super(IV);
    }

    public GOST3411_2012_512Digest(GOST3411_2012_512Digest other) {
        super(IV);
        reset(other);
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "GOST3411-2012-512";
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 64;
    }

    @Override // org.spongycastle.crypto.digests.GOST3411_2012Digest, org.spongycastle.util.Memoable
    public Memoable copy() {
        return new GOST3411_2012_512Digest(this);
    }
}
