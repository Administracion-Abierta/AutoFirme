package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/Tables64kGCMMultiplier.class */
public class Tables64kGCMMultiplier implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override // org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] bArr) {
        if (this.T == null) {
            this.T = new long[16][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, bArr)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(bArr, this.H);
        for (int i = 0; i < 16; i++) {
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
        long[] jArr = this.T[0][bArr[0] & 255];
        long[] jArr2 = this.T[1][bArr[1] & 255];
        long[] jArr3 = this.T[2][bArr[2] & 255];
        long[] jArr4 = this.T[3][bArr[3] & 255];
        long[] jArr5 = this.T[4][bArr[4] & 255];
        long[] jArr6 = this.T[5][bArr[5] & 255];
        long[] jArr7 = this.T[6][bArr[6] & 255];
        long[] jArr8 = this.T[7][bArr[7] & 255];
        long[] jArr9 = this.T[8][bArr[8] & 255];
        long[] jArr10 = this.T[9][bArr[9] & 255];
        long[] jArr11 = this.T[10][bArr[10] & 255];
        long[] jArr12 = this.T[11][bArr[11] & 255];
        long[] jArr13 = this.T[12][bArr[12] & 255];
        long[] jArr14 = this.T[13][bArr[13] & 255];
        long[] jArr15 = this.T[14][bArr[14] & 255];
        long[] jArr16 = this.T[15][bArr[15] & 255];
        long j = ((((((((((((((jArr[0] ^ jArr2[0]) ^ jArr3[0]) ^ jArr4[0]) ^ jArr5[0]) ^ jArr6[0]) ^ jArr7[0]) ^ jArr8[0]) ^ jArr9[0]) ^ jArr10[0]) ^ jArr11[0]) ^ jArr12[0]) ^ jArr13[0]) ^ jArr14[0]) ^ jArr15[0]) ^ jArr16[0];
        long j2 = ((((((((((((((jArr[1] ^ jArr2[1]) ^ jArr3[1]) ^ jArr4[1]) ^ jArr5[1]) ^ jArr6[1]) ^ jArr7[1]) ^ jArr8[1]) ^ jArr9[1]) ^ jArr10[1]) ^ jArr11[1]) ^ jArr12[1]) ^ jArr13[1]) ^ jArr14[1]) ^ jArr15[1]) ^ jArr16[1];
        Pack.longToBigEndian(j, bArr, 0);
        Pack.longToBigEndian(j2, bArr, 8);
    }
}
