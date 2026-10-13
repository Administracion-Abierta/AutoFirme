package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/Tables4kGCMMultiplier.class */
public class Tables4kGCMMultiplier implements GCMMultiplier {
    private byte[] H;
    private long[][] T;

    @Override // org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] bArr) {
        if (this.T == null) {
            this.T = new long[256][2];
        } else if (0 != GCMUtil.areEqual(this.H, bArr)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(bArr, this.H);
        GCMUtil.asLongs(this.H, this.T[1]);
        GCMUtil.multiplyP7(this.T[1], this.T[1]);
        for (int i = 2; i < 256; i += 2) {
            GCMUtil.divideP(this.T[i >> 1], this.T[i]);
            GCMUtil.xor(this.T[i], this.T[1], this.T[i + 1]);
        }
    }

    @Override // org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] bArr) {
        long[] jArr = this.T[bArr[15] & 255];
        long j = jArr[0];
        long j2 = jArr[1];
        for (int i = 14; i >= 0; i--) {
            long[] jArr2 = this.T[bArr[i] & 255];
            long j3 = j2 << 56;
            j2 = jArr2[1] ^ ((j2 >>> 8) | (j << 56));
            j = ((((jArr2[0] ^ (j >>> 8)) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        }
        Pack.longToBigEndian(j, bArr, 0);
        Pack.longToBigEndian(j2, bArr, 8);
    }
}
