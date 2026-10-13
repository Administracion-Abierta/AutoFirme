package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/Tables8kGCMMultiplier.class */
public class Tables8kGCMMultiplier implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override // org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] bArr) {
        if (this.T == null) {
            this.T = new long[2][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, bArr)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(bArr, this.H);
        for (int i = 0; i < 2; i++) {
            long[][] jArr = this.T[i];
            if (i == 0) {
                GCMUtil.asLongs(this.H, jArr[1]);
                GCMUtil.multiplyP7(jArr[1], jArr[1]);
            } else {
                GCMUtil.multiplyP8(this.T[i - 1][1], jArr[1]);
            }
            for (int i2 = 2; i2 < 256; i2 += 2) {
                GCMUtil.divideP(jArr[i2 >> 1], jArr[i2]);
                GCMUtil.xor(jArr[i2], jArr[1], jArr[i2 + 1]);
            }
        }
    }

    @Override // org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] bArr) {
        long[][] jArr = this.T[0];
        long[][] jArr2 = this.T[1];
        long[] jArr3 = jArr[bArr[14] & 255];
        long[] jArr4 = jArr2[bArr[15] & 255];
        long j = jArr3[0] ^ jArr4[0];
        long j2 = jArr3[1] ^ jArr4[1];
        for (int i = 12; i >= 0; i -= 2) {
            long[] jArr5 = jArr[bArr[i] & 255];
            long[] jArr6 = jArr2[bArr[i + 1] & 255];
            long j3 = j2 << 48;
            j2 = (jArr5[1] ^ jArr6[1]) ^ ((j2 >>> 16) | (j << 48));
            j = (((((jArr5[0] ^ jArr6[0]) ^ (j >>> 16)) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2)) ^ (j3 >>> 7);
        }
        Pack.longToBigEndian(j, bArr, 0);
        Pack.longToBigEndian(j2, bArr, 8);
    }
}
