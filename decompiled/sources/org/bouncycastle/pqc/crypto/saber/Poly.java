package org.bouncycastle.pqc.crypto.saber;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/Poly.class */
class Poly {
    private static final int KARATSUBA_N = 64;
    private static int SCHB_N = 16;
    private final int N_RES;
    private final int N_SB;
    private final int N_SB_RES;
    private final int SABER_N;
    private final int SABER_L;
    private final SABEREngine engine;
    private final Utils utils;

    public Poly(SABEREngine sABEREngine) {
        this.engine = sABEREngine;
        this.SABER_L = sABEREngine.getSABER_L();
        this.SABER_N = sABEREngine.getSABER_N();
        this.N_RES = this.SABER_N << 1;
        this.N_SB = this.SABER_N >> 2;
        this.N_SB_RES = (2 * this.N_SB) - 1;
        this.utils = sABEREngine.getUtils();
    }

    public void GenMatrix(short[][][] sArr, byte[] bArr) {
        byte[] bArr2 = new byte[this.SABER_L * this.engine.getSABER_POLYVECBYTES()];
        this.engine.symmetric.prf(bArr2, bArr, this.engine.getSABER_SEEDBYTES(), bArr2.length);
        for (int i = 0; i < this.SABER_L; i++) {
            this.utils.BS2POLVECq(bArr2, i * this.engine.getSABER_POLYVECBYTES(), sArr[i]);
        }
    }

    public void GenSecret(short[][] sArr, byte[] bArr) {
        byte[] bArr2 = new byte[this.SABER_L * this.engine.getSABER_POLYCOINBYTES()];
        this.engine.symmetric.prf(bArr2, bArr, this.engine.getSABER_NOISE_SEEDBYTES(), bArr2.length);
        for (int i = 0; i < this.SABER_L; i++) {
            if (this.engine.usingEffectiveMasking) {
                for (int i2 = 0; i2 < this.SABER_N / 4; i2++) {
                    sArr[i][4 * i2] = (short) (((bArr2[i2 + (i * this.engine.getSABER_POLYCOINBYTES())] & 3) ^ 2) - 2);
                    sArr[i][(4 * i2) + 1] = (short) ((((bArr2[i2 + (i * this.engine.getSABER_POLYCOINBYTES())] >>> 2) & 3) ^ 2) - 2);
                    sArr[i][(4 * i2) + 2] = (short) ((((bArr2[i2 + (i * this.engine.getSABER_POLYCOINBYTES())] >>> 4) & 3) ^ 2) - 2);
                    sArr[i][(4 * i2) + 3] = (short) ((((bArr2[i2 + (i * this.engine.getSABER_POLYCOINBYTES())] >>> 6) & 3) ^ 2) - 2);
                }
            } else {
                cbd(sArr[i], bArr2, i * this.engine.getSABER_POLYCOINBYTES());
            }
        }
    }

    private long load_littleendian(byte[] bArr, int i, int i2) {
        long j = bArr[i + 0] & 255;
        for (int i3 = 1; i3 < i2; i3++) {
            j |= ((long) (bArr[i + i3] & 255)) << (8 * i3);
        }
        return j;
    }

    private void cbd(short[] sArr, byte[] bArr, int i) {
        int[] iArr = new int[4];
        int[] iArr2 = new int[4];
        if (this.engine.getSABER_MU() == 6) {
            for (int i2 = 0; i2 < this.SABER_N / 4; i2++) {
                int iLoad_littleendian = (int) load_littleendian(bArr, i + (3 * i2), 3);
                int i3 = 0;
                for (int i4 = 0; i4 < 3; i4++) {
                    i3 += (iLoad_littleendian >> i4) & 2396745;
                }
                iArr[0] = i3 & 7;
                iArr2[0] = (i3 >>> 3) & 7;
                iArr[1] = (i3 >>> 6) & 7;
                iArr2[1] = (i3 >>> 9) & 7;
                iArr[2] = (i3 >>> 12) & 7;
                iArr2[2] = (i3 >>> 15) & 7;
                iArr[3] = (i3 >>> 18) & 7;
                iArr2[3] = i3 >>> 21;
                sArr[(4 * i2) + 0] = (short) (iArr[0] - iArr2[0]);
                sArr[(4 * i2) + 1] = (short) (iArr[1] - iArr2[1]);
                sArr[(4 * i2) + 2] = (short) (iArr[2] - iArr2[2]);
                sArr[(4 * i2) + 3] = (short) (iArr[3] - iArr2[3]);
            }
            return;
        }
        if (this.engine.getSABER_MU() == 8) {
            for (int i5 = 0; i5 < this.SABER_N / 4; i5++) {
                int iLoad_littleendian2 = (int) load_littleendian(bArr, i + (4 * i5), 4);
                int i6 = 0;
                for (int i7 = 0; i7 < 4; i7++) {
                    i6 += (iLoad_littleendian2 >>> i7) & 286331153;
                }
                iArr[0] = i6 & 15;
                iArr2[0] = (i6 >>> 4) & 15;
                iArr[1] = (i6 >>> 8) & 15;
                iArr2[1] = (i6 >>> 12) & 15;
                iArr[2] = (i6 >>> 16) & 15;
                iArr2[2] = (i6 >>> 20) & 15;
                iArr[3] = (i6 >>> 24) & 15;
                iArr2[3] = i6 >>> 28;
                sArr[(4 * i5) + 0] = (short) (iArr[0] - iArr2[0]);
                sArr[(4 * i5) + 1] = (short) (iArr[1] - iArr2[1]);
                sArr[(4 * i5) + 2] = (short) (iArr[2] - iArr2[2]);
                sArr[(4 * i5) + 3] = (short) (iArr[3] - iArr2[3]);
            }
            return;
        }
        if (this.engine.getSABER_MU() == 10) {
            for (int i8 = 0; i8 < this.SABER_N / 4; i8++) {
                long jLoad_littleendian = load_littleendian(bArr, i + (5 * i8), 5);
                long j = 0;
                for (int i9 = 0; i9 < 5; i9++) {
                    j += (jLoad_littleendian >>> i9) & 35468117025L;
                }
                iArr[0] = (int) (j & 31);
                iArr2[0] = (int) ((j >>> 5) & 31);
                iArr[1] = (int) ((j >>> 10) & 31);
                iArr2[1] = (int) ((j >>> 15) & 31);
                iArr[2] = (int) ((j >>> 20) & 31);
                iArr2[2] = (int) ((j >>> 25) & 31);
                iArr[3] = (int) ((j >>> 30) & 31);
                iArr2[3] = (int) (j >>> 35);
                sArr[(4 * i8) + 0] = (short) (iArr[0] - iArr2[0]);
                sArr[(4 * i8) + 1] = (short) (iArr[1] - iArr2[1]);
                sArr[(4 * i8) + 2] = (short) (iArr[2] - iArr2[2]);
                sArr[(4 * i8) + 3] = (short) (iArr[3] - iArr2[3]);
            }
        }
    }

    private short OVERFLOWING_MUL(int i, int i2) {
        return (short) (i * i2);
    }

    private void karatsuba_simple(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArr4 = new int[31];
        int[] iArr5 = new int[31];
        int[] iArr6 = new int[31];
        int[] iArr7 = new int[63];
        for (int i = 0; i < 16; i++) {
            int i2 = iArr[i];
            int i3 = iArr[i + 16];
            int i4 = iArr[i + 32];
            int i5 = iArr[i + 48];
            for (int i6 = 0; i6 < 16; i6++) {
                int i7 = iArr2[i6];
                int i8 = iArr2[i6 + 16];
                iArr3[i + i6 + 0] = iArr3[i + i6 + 0] + OVERFLOWING_MUL(i2, i7);
                iArr3[i + i6 + 32] = iArr3[i + i6 + 32] + OVERFLOWING_MUL(i3, i8);
                iArr4[i + i6] = (int) (((long) iArr4[i + i6]) + (((long) (i7 + i8)) * ((long) (i2 + i3))));
                int i9 = iArr2[i6 + 32];
                int i10 = iArr2[i6 + 48];
                iArr3[i + i6 + 64] = iArr3[i + i6 + 64] + OVERFLOWING_MUL(i9, i4);
                iArr3[i + i6 + 96] = iArr3[i + i6 + 96] + OVERFLOWING_MUL(i10, i5);
                iArr6[i + i6] = iArr6[i + i6] + OVERFLOWING_MUL(i4 + i5, i9 + i10);
                int i11 = i7 + i9;
                int i12 = i2 + i4;
                iArr7[i + i6 + 0] = iArr7[i + i6 + 0] + OVERFLOWING_MUL(i11, i12);
                int i13 = i8 + i10;
                int i14 = i3 + i5;
                iArr7[i + i6 + 32] = iArr7[i + i6 + 32] + OVERFLOWING_MUL(i13, i14);
                iArr5[i + i6] = iArr5[i + i6] + OVERFLOWING_MUL(i11 + i13, i12 + i14);
            }
        }
        for (int i15 = 0; i15 < 31; i15++) {
            iArr5[i15] = (iArr5[i15] - iArr7[i15 + 0]) - iArr7[i15 + 32];
            iArr4[i15] = (iArr4[i15] - iArr3[i15 + 0]) - iArr3[i15 + 32];
            iArr6[i15] = (iArr6[i15] - iArr3[i15 + 64]) - iArr3[i15 + 96];
        }
        for (int i16 = 0; i16 < 31; i16++) {
            iArr7[i16 + 16] = iArr7[i16 + 16] + iArr5[i16];
            iArr3[i16 + 16] = iArr3[i16 + 16] + iArr4[i16];
            iArr3[i16 + 80] = iArr3[i16 + 80] + iArr6[i16];
        }
        for (int i17 = 0; i17 < 63; i17++) {
            iArr7[i17] = (iArr7[i17] - iArr3[i17]) - iArr3[i17 + 64];
        }
        for (int i18 = 0; i18 < 63; i18++) {
            iArr3[i18 + 32] = iArr3[i18 + 32] + iArr7[i18];
        }
    }

    private void toom_cook_4way(short[] sArr, short[] sArr2, short[] sArr3) {
        int[] iArr = new int[this.N_SB];
        int[] iArr2 = new int[this.N_SB];
        int[] iArr3 = new int[this.N_SB];
        int[] iArr4 = new int[this.N_SB];
        int[] iArr5 = new int[this.N_SB];
        int[] iArr6 = new int[this.N_SB];
        int[] iArr7 = new int[this.N_SB];
        int[] iArr8 = new int[this.N_SB];
        int[] iArr9 = new int[this.N_SB];
        int[] iArr10 = new int[this.N_SB];
        int[] iArr11 = new int[this.N_SB];
        int[] iArr12 = new int[this.N_SB];
        int[] iArr13 = new int[this.N_SB];
        int[] iArr14 = new int[this.N_SB];
        int[] iArr15 = new int[this.N_SB_RES];
        int[] iArr16 = new int[this.N_SB_RES];
        int[] iArr17 = new int[this.N_SB_RES];
        int[] iArr18 = new int[this.N_SB_RES];
        int[] iArr19 = new int[this.N_SB_RES];
        int[] iArr20 = new int[this.N_SB_RES];
        int[] iArr21 = new int[this.N_SB_RES];
        for (int i = 0; i < this.N_SB; i++) {
            short s = sArr[i];
            short s2 = sArr[i + this.N_SB];
            short s3 = sArr[i + (this.N_SB * 2)];
            short s4 = sArr[i + (this.N_SB * 3)];
            short s5 = (short) (s + s3);
            short s6 = (short) (s2 + s4);
            iArr3[i] = (short) (s5 + s6);
            iArr4[i] = (short) (s5 - s6);
            short s7 = (short) (((s << 2) + s3) << 1);
            short s8 = (short) ((s2 << 2) + s4);
            iArr5[i] = (short) (s7 + s8);
            iArr6[i] = (short) (s7 - s8);
            iArr2[i] = (short) ((s4 << 3) + (s3 << 2) + (s2 << 1) + s);
            iArr7[i] = s;
            iArr[i] = s4;
        }
        for (int i2 = 0; i2 < this.N_SB; i2++) {
            short s9 = sArr2[i2];
            short s10 = sArr2[i2 + this.N_SB];
            short s11 = sArr2[i2 + (this.N_SB * 2)];
            short s12 = sArr2[i2 + (this.N_SB * 3)];
            int i3 = s9 + s11;
            int i4 = s10 + s12;
            iArr10[i2] = i3 + i4;
            iArr11[i2] = i3 - i4;
            int i5 = ((s9 << 2) + s11) << 1;
            int i6 = (s10 << 2) + s12;
            iArr12[i2] = i5 + i6;
            iArr13[i2] = i5 - i6;
            iArr9[i2] = (s12 << 3) + (s11 << 2) + (s10 << 1) + s9;
            iArr14[i2] = s9;
            iArr8[i2] = s12;
        }
        karatsuba_simple(iArr, iArr8, iArr15);
        karatsuba_simple(iArr2, iArr9, iArr16);
        karatsuba_simple(iArr3, iArr10, iArr17);
        karatsuba_simple(iArr4, iArr11, iArr18);
        karatsuba_simple(iArr5, iArr12, iArr19);
        karatsuba_simple(iArr6, iArr13, iArr20);
        karatsuba_simple(iArr7, iArr14, iArr21);
        for (int i7 = 0; i7 < this.N_SB_RES; i7++) {
            int i8 = iArr15[i7];
            int i9 = iArr16[i7];
            int i10 = iArr17[i7];
            int i11 = iArr18[i7];
            int i12 = iArr19[i7];
            int i13 = iArr20[i7];
            int i14 = iArr21[i7];
            int i15 = i9 + i12;
            int i16 = i13 - i12;
            int i17 = ((i11 & 65535) - (i10 & 65535)) >>> 1;
            int i18 = (((i12 - i8) - (i14 << 6)) << 1) + i16;
            int i19 = i10 + i17;
            int i20 = (i15 - (i19 << 6)) - i19;
            int i21 = (i19 - i14) - i8;
            int i22 = i20 + (45 * i21);
            int i23 = (((i18 & 65535) - (i21 << 3)) * 43691) >> 3;
            int i24 = i16 + i22;
            int i25 = (((i22 & 65535) + ((i17 & 65535) << 4)) * 36409) >> 1;
            int i26 = -(i17 + i25);
            int i27 = (((30 * (i25 & 65535)) - (i24 & 65535)) * 61167) >> 2;
            int i28 = i7;
            sArr3[i28] = (short) (sArr3[i28] + (i14 & 65535));
            int i29 = i7 + 64;
            sArr3[i29] = (short) (sArr3[i29] + (i27 & 65535));
            int i30 = i7 + 128;
            sArr3[i30] = (short) (sArr3[i30] + (i23 & 65535));
            int i31 = i7 + 192;
            sArr3[i31] = (short) (sArr3[i31] + (i26 & 65535));
            int i32 = i7 + 256;
            sArr3[i32] = (short) (sArr3[i32] + ((i21 - i23) & 65535));
            int i33 = i7 + 320;
            sArr3[i33] = (short) (sArr3[i33] + ((i25 - i27) & 65535));
            int i34 = i7 + 384;
            sArr3[i34] = (short) (sArr3[i34] + (i8 & 65535));
        }
    }

    private void poly_mul_acc(short[] sArr, short[] sArr2, short[] sArr3) {
        short[] sArr4 = new short[2 * this.SABER_N];
        toom_cook_4way(sArr, sArr2, sArr4);
        for (int i = this.SABER_N; i < 2 * this.SABER_N; i++) {
            int i2 = i - this.SABER_N;
            sArr3[i2] = (short) (sArr3[i2] + (sArr4[i - this.SABER_N] - sArr4[i]));
        }
    }

    public void MatrixVectorMul(short[][][] sArr, short[][] sArr2, short[][] sArr3, int i) {
        for (int i2 = 0; i2 < this.SABER_L; i2++) {
            for (int i3 = 0; i3 < this.SABER_L; i3++) {
                if (i == 1) {
                    poly_mul_acc(sArr[i3][i2], sArr2[i3], sArr3[i2]);
                } else {
                    poly_mul_acc(sArr[i2][i3], sArr2[i3], sArr3[i2]);
                }
            }
        }
    }

    public void InnerProd(short[][] sArr, short[][] sArr2, short[] sArr3) {
        for (int i = 0; i < this.SABER_L; i++) {
            poly_mul_acc(sArr[i], sArr2[i], sArr3);
        }
    }
}
