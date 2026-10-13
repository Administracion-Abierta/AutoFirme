package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/SeedDerive.class */
class SeedDerive {
    private final byte[] I;
    private final byte[] masterSeed;
    private final Digest digest;
    private int q;
    private int j;

    public SeedDerive(byte[] bArr, byte[] bArr2, Digest digest) {
        this.I = bArr;
        this.masterSeed = bArr2;
        this.digest = digest;
    }

    public int getQ() {
        return this.q;
    }

    public void setQ(int i) {
        this.q = i;
    }

    public int getJ() {
        return this.j;
    }

    public void setJ(int i) {
        this.j = i;
    }

    public byte[] getI() {
        return this.I;
    }

    public byte[] getMasterSeed() {
        return this.masterSeed;
    }

    public byte[] deriveSeed(byte[] bArr, int i) {
        if (bArr.length - i < this.digest.getDigestSize()) {
            throw new IllegalArgumentException("target length is less than digest size.");
        }
        this.digest.update(this.I, 0, this.I.length);
        this.digest.update((byte) (this.q >>> 24));
        this.digest.update((byte) (this.q >>> 16));
        this.digest.update((byte) (this.q >>> 8));
        this.digest.update((byte) this.q);
        this.digest.update((byte) (this.j >>> 8));
        this.digest.update((byte) this.j);
        this.digest.update((byte) -1);
        this.digest.update(this.masterSeed, 0, this.masterSeed.length);
        this.digest.doFinal(bArr, i);
        return bArr;
    }

    public void deriveSeed(byte[] bArr, boolean z) {
        deriveSeed(bArr, z, 0);
    }

    public void deriveSeed(byte[] bArr, boolean z, int i) {
        deriveSeed(bArr, i);
        if (z) {
            this.j++;
        }
    }
}
