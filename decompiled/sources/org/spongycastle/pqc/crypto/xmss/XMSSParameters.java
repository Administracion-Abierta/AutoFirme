package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSParameters.class */
public final class XMSSParameters {
    private final XMSSOid oid;
    private final WOTSPlus wotsPlus;
    private final int height;
    private final int k;

    public XMSSParameters(int height, Digest digest) {
        if (height < 2) {
            throw new IllegalArgumentException("height must be >= 2");
        }
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(digest));
        this.height = height;
        this.k = determineMinK();
        this.oid = DefaultXMSSOid.lookup(getDigest().getAlgorithmName(), getDigestSize(), getWinternitzParameter(), this.wotsPlus.getParams().getLen(), height);
    }

    private int determineMinK() {
        for (int k = 2; k <= this.height; k++) {
            if ((this.height - k) % 2 == 0) {
                return k;
            }
        }
        throw new IllegalStateException("should never happen...");
    }

    protected Digest getDigest() {
        return this.wotsPlus.getParams().getDigest();
    }

    public int getDigestSize() {
        return this.wotsPlus.getParams().getDigestSize();
    }

    public int getWinternitzParameter() {
        return this.wotsPlus.getParams().getWinternitzParameter();
    }

    public int getHeight() {
        return this.height;
    }

    WOTSPlus getWOTSPlus() {
        return this.wotsPlus;
    }

    int getK() {
        return this.k;
    }
}
