package org.spongycastle.crypto.digests;

import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.params.SkeinParameters;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinDigest.class */
public class SkeinDigest implements ExtendedDigest, Memoable {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;

    public SkeinDigest(int stateSizeBits, int digestSizeBits) {
        this.engine = new SkeinEngine(stateSizeBits, digestSizeBits);
        init(null);
    }

    public SkeinDigest(SkeinDigest digest) {
        this.engine = new SkeinEngine(digest.engine);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        SkeinDigest d = (SkeinDigest) other;
        this.engine.reset(d.engine);
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new SkeinDigest(this);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Skein-" + (this.engine.getBlockSize() * 8) + "-" + (this.engine.getOutputSize() * 8);
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.engine.getOutputSize();
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.engine.getBlockSize();
    }

    public void init(SkeinParameters params) {
        this.engine.init(params);
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.engine.reset();
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this.engine.update(in);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.engine.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        return this.engine.doFinal(out, outOff);
    }
}
