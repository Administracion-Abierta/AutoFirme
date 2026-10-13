package org.bouncycastle.pqc.crypto.cmce;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/BENES13.class */
class BENES13 extends BENES {
    public BENES13(int i, int i2, int i3) {
        super(i, i2, i3);
    }

    static void layer_in(long[] jArr, long[] jArr2, int i) {
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
                int i8 = i2 + 1;
                long j = (jArr[i6 + 0] ^ jArr[i6 + i3]) & jArr2[i7];
                int i9 = i6 + 0;
                jArr[i9] = jArr[i9] ^ j;
                int i10 = i6 + i3;
                jArr[i10] = jArr[i10] ^ j;
                i2 = i8 + 1;
                long j2 = (jArr[(64 + i6) + 0] ^ jArr[(64 + i6) + i3]) & jArr2[i8];
                int i11 = 64 + i6 + 0;
                jArr[i11] = jArr[i11] ^ j2;
                int i12 = 64 + i6 + i3;
                jArr[i12] = jArr[i12] ^ j2;
            }
            i4 = i5 + (i3 * 2);
        }
    }

    static void layer_ex(long[] jArr, long[] jArr2, int i) {
        int i2 = 0;
        int i3 = 1 << i;
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= 128) {
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

    void apply_benes(byte[] bArr, byte[] bArr2, int i) {
        int i2;
        int i3;
        long[] jArr = new long[128];
        long[] jArr2 = new long[128];
        long[] jArr3 = new long[64];
        long[] jArr4 = new long[64];
        if (i == 0) {
            i2 = (this.SYS_T * 2) + 40;
            i3 = 0;
        } else {
            i2 = (this.SYS_T * 2) + 40 + 12288;
            i3 = -1024;
        }
        for (int i4 = 0; i4 < 64; i4++) {
            jArr[i4 + 0] = Utils.load8(bArr, 0 + (i4 * 16) + 0);
            jArr[i4 + 64] = Utils.load8(bArr, 0 + (i4 * 16) + 8);
        }
        transpose_64x64(jArr2, jArr, 0);
        transpose_64x64(jArr2, jArr, 64);
        for (int i5 = 0; i5 <= 6; i5++) {
            for (int i6 = 0; i6 < 64; i6++) {
                jArr3[i6] = Utils.load8(bArr2, i2);
                i2 += 8;
            }
            i2 += i3;
            transpose_64x64(jArr4, jArr3);
            layer_ex(jArr2, jArr4, i5);
        }
        transpose_64x64(jArr, jArr2, 0);
        transpose_64x64(jArr, jArr2, 64);
        for (int i7 = 0; i7 <= 5; i7++) {
            for (int i8 = 0; i8 < 64; i8++) {
                jArr3[i8] = Utils.load8(bArr2, i2);
                i2 += 8;
            }
            i2 += i3;
            layer_in(jArr, jArr3, i7);
        }
        for (int i9 = 4; i9 >= 0; i9--) {
            for (int i10 = 0; i10 < 64; i10++) {
                jArr3[i10] = Utils.load8(bArr2, i2);
                i2 += 8;
            }
            i2 += i3;
            layer_in(jArr, jArr3, i9);
        }
        transpose_64x64(jArr2, jArr, 0);
        transpose_64x64(jArr2, jArr, 64);
        for (int i11 = 6; i11 >= 0; i11--) {
            for (int i12 = 0; i12 < 64; i12++) {
                jArr3[i12] = Utils.load8(bArr2, i2);
                i2 += 8;
            }
            i2 += i3;
            transpose_64x64(jArr4, jArr3);
            layer_ex(jArr2, jArr4, i11);
        }
        transpose_64x64(jArr, jArr2, 0);
        transpose_64x64(jArr, jArr2, 64);
        for (int i13 = 0; i13 < 64; i13++) {
            Utils.store8(bArr, 0 + (i13 * 16) + 0, jArr[0 + i13]);
            Utils.store8(bArr, 0 + (i13 * 16) + 8, jArr[64 + i13]);
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
