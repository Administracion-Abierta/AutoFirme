package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/Tables64kGCMMultiplier.class */
public class Tables64kGCMMultiplier implements GCMMultiplier {
    private byte[] H;
    private int[][][] M;

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        if (this.M == null) {
            this.M = new int[16][256][4];
        } else if (Arrays.areEqual(this.H, H)) {
            return;
        }
        this.H = Arrays.clone(H);
        GCMUtil.asInts(H, this.M[0][128]);
        int i = 64;
        while (true) {
            int j = i;
            if (j < 1) {
                break;
            }
            GCMUtil.multiplyP(this.M[0][j + j], this.M[0][j]);
            i = j >> 1;
        }
        int i2 = 0;
        while (true) {
            int i3 = 2;
            while (true) {
                int j2 = i3;
                if (j2 >= 256) {
                    break;
                }
                for (int k = 1; k < j2; k++) {
                    GCMUtil.xor(this.M[i2][j2], this.M[i2][k], this.M[i2][j2 + k]);
                }
                i3 = j2 + j2;
            }
            i2++;
            if (i2 == 16) {
                return;
            }
            int i4 = 128;
            while (true) {
                int j3 = i4;
                if (j3 > 0) {
                    GCMUtil.multiplyP8(this.M[i2 - 1][j3], this.M[i2][j3]);
                    i4 = j3 >> 1;
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        int[] z = new int[4];
        for (int i = 15; i >= 0; i--) {
            int[] m = this.M[i][x[i] & 255];
            z[0] = z[0] ^ m[0];
            z[1] = z[1] ^ m[1];
            z[2] = z[2] ^ m[2];
            z[3] = z[3] ^ m[3];
        }
        Pack.intToBigEndian(z, x, 0);
    }
}
