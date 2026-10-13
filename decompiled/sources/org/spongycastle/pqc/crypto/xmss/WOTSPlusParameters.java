package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/WOTSPlusParameters.class */
final class WOTSPlusParameters {
    private final XMSSOid oid;
    private final Digest digest;
    private final int digestSize;
    private final int winternitzParameter;
    private final int len;
    private final int len1;
    private final int len2;

    protected WOTSPlusParameters(Digest digest) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        this.digest = digest;
        this.digestSize = XMSSUtil.getDigestSize(digest);
        this.winternitzParameter = 16;
        this.len1 = (int) Math.ceil(((double) (8 * this.digestSize)) / ((double) XMSSUtil.log2(this.winternitzParameter)));
        this.len2 = ((int) Math.floor(XMSSUtil.log2(this.len1 * (this.winternitzParameter - 1)) / XMSSUtil.log2(this.winternitzParameter))) + 1;
        this.len = this.len1 + this.len2;
        this.oid = WOTSPlusOid.lookup(digest.getAlgorithmName(), this.digestSize, this.winternitzParameter, this.len);
        if (this.oid == null) {
            throw new IllegalArgumentException("cannot find OID for digest algorithm: " + digest.getAlgorithmName());
        }
    }

    protected XMSSOid getOid() {
        return this.oid;
    }

    protected Digest getDigest() {
        return this.digest;
    }

    protected int getDigestSize() {
        return this.digestSize;
    }

    protected int getWinternitzParameter() {
        return this.winternitzParameter;
    }

    protected int getLen() {
        return this.len;
    }

    protected int getLen1() {
        return this.len1;
    }

    protected int getLen2() {
        return this.len2;
    }
}
