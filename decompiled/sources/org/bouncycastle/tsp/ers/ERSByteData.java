package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSByteData.class */
public class ERSByteData extends ERSCachingData {
    private final byte[] content;

    public ERSByteData(byte[] bArr) {
        this.content = bArr;
    }

    @Override // org.bouncycastle.tsp.ers.ERSCachingData
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] bArr) {
        byte[] bArrCalculateDigest = ERSUtil.calculateDigest(digestCalculator, this.content);
        return bArr != null ? ERSUtil.concatPreviousHashes(digestCalculator, bArr, bArrCalculateDigest) : bArrCalculateDigest;
    }
}
