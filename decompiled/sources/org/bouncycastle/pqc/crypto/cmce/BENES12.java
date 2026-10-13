package org.bouncycastle.pqc.crypto.cmce;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/BENES12.class */
class BENES12 extends BENES {
    public BENES12(int i, int i2, int i3) {
        super(i, i2, i3);
    }

    static void layerBenes(long[] jArr, long[] jArr2, int i) {
        int i2 = 0;
        int i3 = 1 << i;
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= 64) {
                return;
            }
            for (int i6 = i5; i6 < i5 + i3; i6++) {
                int i7 = i2;
                i2++;
                long j = (jArr[i6 + 0] ^ jArr[i6 + i3]) & jArr2[i7];
                int i8 = i6 + 0;
                jArr[i8] = jArr[i8] ^ j;
                int i9 = i6 + i3;
                jArr[i9] = jArr[i9] ^ j;
            }
            i4 = i5 + (i3 * 2);
        }
    }

    private void apply_benes(byte[] bArr, byte[] bArr2, int i) {
        int i2;
        int i3;
        long[] jArr = new long[64];
        long[] jArr2 = new long[64];
        for (int i4 = 0; i4 < 64; i4++) {
            jArr[i4] = Utils.load8(bArr, i4 * 8);
        }
        if (i == 0) {
            i2 = 256;
            i3 = (this.SYS_T * 2) + 40;
        } else {
            i2 = -256;
            i3 = (this.SYS_T * 2) + 40 + (((2 * this.GFBITS) - 2) * 256);
        }
        transpose_64x64(jArr, jArr);
        for (int i5 = 0; i5 <= 5; i5++) {
            for (int i6 = 0; i6 < 64; i6++) {
                jArr2[i6] = Utils.load4(bArr2, i3 + (i6 * 4));
            }
            transpose_64x64(jArr2, jArr2);
            layerBenes(jArr, jArr2, i5);
            i3 += i2;
        }
        transpose_64x64(jArr, jArr);
        for (int i7 = 0; i7 <= 5; i7++) {
            for (int i8 = 0; i8 < 32; i8++) {
                jArr2[i8] = Utils.load8(bArr2, i3 + (i8 * 8));
            }
            layerBenes(jArr, jArr2, i7);
            i3 += i2;
        }
        for (int i9 = 4; i9 >= 0; i9--) {
            for (int i10 = 0; i10 < 32; i10++) {
                jArr2[i10] = Utils.load8(bArr2, i3 + (i10 * 8));
            }
            layerBenes(jArr, jArr2, i9);
            i3 += i2;
        }
        transpose_64x64(jArr, jArr);
        for (int i11 = 5; i11 >= 0; i11--) {
            for (int i12 = 0; i12 < 64; i12++) {
                jArr2[i12] = Utils.load4(bArr2, i3 + (i12 * 4));
            }
            transpose_64x64(jArr2, jArr2);
            layerBenes(jArr, jArr2, i11);
            i3 += i2;
        }
        transpose_64x64(jArr, jArr);
        for (int i13 = 0; i13 < 64; i13++) {
            Utils.store8(bArr, i13 * 8, jArr[i13]);
        }
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.BENES
    public void support_gen(short[] sArr, byte[] bArr) {
        byte[][] bArr2 = new byte[this.GFBITS][(1 << this.GFBITS) / 8];
        for (int i = 0; i < this.GFBITS; i++) {
            for (int i2 = 0; i2 < (1 << this.GFBITS) / 8; i2++) {
                bArr2[i][i2] = 0;
            }
        }
        for (int i3 = 0; i3 < (1 << this.GFBITS); i3++) {
            short sBitrev = Utils.bitrev((short) i3, this.GFBITS);
            for (int i4 = 0; i4 < this.GFBITS; i4++) {
                byte[] bArr3 = bArr2[i4];
                int i5 = i3 / 8;
                bArr3[i5] = (byte) (bArr3[i5] | (((sBitrev >> i4) & 1) << (i3 % 8)));
            }
        }
        for (int i6 = 0; i6 < this.GFBITS; i6++) {
            apply_benes(bArr2[i6], bArr, 0);
        }
        for (int i7 = 0; i7 < this.SYS_N; i7++) {
            sArr[i7] = 0;
            for (int i8 = this.GFBITS - 1; i8 >= 0; i8--) {
                int i9 = i7;
                sArr[i9] = (short) (sArr[i9] << 1);
                int i10 = i7;
                sArr[i10] = (short) (sArr[i10] | ((bArr2[i8][i7 / 8] >> (i7 % 8)) & 1));
            }
        }
    }
}
