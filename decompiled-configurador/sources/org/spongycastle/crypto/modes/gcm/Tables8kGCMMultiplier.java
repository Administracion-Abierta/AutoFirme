package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/Tables8kGCMMultiplier.class */
public class Tables8kGCMMultiplier implements GCMMultiplier {
    private byte[] H;
    private int[][][] M;

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        if (this.M == null) {
            this.M = new int[32][16][4];
        } else if (Arrays.areEqual(this.H, H)) {
            return;
        }
        this.H = Arrays.clone(H);
        GCMUtil.asInts(H, this.M[1][8]);
        int i = 4;
        while (true) {
            int j = i;
            if (j < 1) {
                break;
            }
            GCMUtil.multiplyP(this.M[1][j + j], this.M[1][j]);
            i = j >> 1;
        }
        GCMUtil.multiplyP(this.M[1][1], this.M[0][8]);
        int i2 = 4;
        while (true) {
            int j2 = i2;
            if (j2 < 1) {
                break;
            }
            GCMUtil.multiplyP(this.M[0][j2 + j2], this.M[0][j2]);
            i2 = j2 >> 1;
        }
        int i3 = 0;
        while (true) {
            int i4 = 2;
            while (true) {
                int j3 = i4;
                if (j3 >= 16) {
                    break;
                }
                for (int k = 1; k < j3; k++) {
                    GCMUtil.xor(this.M[i3][j3], this.M[i3][k], this.M[i3][j3 + k]);
                }
                i4 = j3 + j3;
            }
            i3++;
            if (i3 == 32) {
                return;
            }
            if (i3 > 1) {
                int i5 = 8;
                while (true) {
                    int j4 = i5;
                    if (j4 > 0) {
                        GCMUtil.multiplyP8(this.M[i3 - 2][j4], this.M[i3][j4]);
                        i5 = j4 >> 1;
                    }
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        int[] z = new int[4];
        for (int i = 15; i >= 0; i--) {
            int[] m = this.M[i + i][x[i] & 15];
            z[0] = z[0] ^ m[0];
            z[1] = z[1] ^ m[1];
            z[2] = z[2] ^ m[2];
            z[3] = z[3] ^ m[3];
            int[] m2 = this.M[i + i + 1][(x[i] & 240) >>> 4];
            z[0] = z[0] ^ m2[0];
            z[1] = z[1] ^ m2[1];
            z[2] = z[2] ^ m2[2];
            z[3] = z[3] ^ m2[3];
        }
        Pack.intToBigEndian(z, x, 0);
    }
}
