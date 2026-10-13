package org.bouncycastle.pqc.crypto.cmce;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/CMCEEngine.class */
class CMCEEngine {
    private int SYS_N;
    private int SYS_T;
    private int GFBITS;
    private int IRR_BYTES;
    private int COND_BYTES;
    private int PK_NROWS;
    private int PK_NCOLS;
    private int PK_ROW_BYTES;
    private int SYND_BYTES;
    private int GFMASK;
    private int[] poly;
    private final int defaultKeySize;
    private GF gf;
    private BENES benes;
    private boolean usePadding;
    private boolean countErrorIndices;
    private boolean usePivots;

    public int getIrrBytes() {
        return this.IRR_BYTES;
    }

    public int getCondBytes() {
        return this.COND_BYTES;
    }

    public int getPrivateKeySize() {
        return this.COND_BYTES + this.IRR_BYTES + (this.SYS_N / 8) + 40;
    }

    public int getPublicKeySize() {
        return this.usePadding ? this.PK_NROWS * ((this.SYS_N / 8) - ((this.PK_NROWS - 1) / 8)) : (this.PK_NROWS * this.PK_NCOLS) / 8;
    }

    public int getCipherTextSize() {
        return this.SYND_BYTES;
    }

    public CMCEEngine(int i, int i2, int i3, int[] iArr, boolean z, int i4) {
        this.usePivots = z;
        this.SYS_N = i2;
        this.SYS_T = i3;
        this.GFBITS = i;
        this.poly = iArr;
        this.defaultKeySize = i4;
        this.IRR_BYTES = this.SYS_T * 2;
        this.COND_BYTES = (1 << (this.GFBITS - 4)) * ((2 * this.GFBITS) - 1);
        this.PK_NROWS = this.SYS_T * this.GFBITS;
        this.PK_NCOLS = this.SYS_N - this.PK_NROWS;
        this.PK_ROW_BYTES = (this.PK_NCOLS + 7) / 8;
        this.SYND_BYTES = (this.PK_NROWS + 7) / 8;
        this.GFMASK = (1 << this.GFBITS) - 1;
        if (this.GFBITS == 12) {
            this.gf = new GF12();
            this.benes = new BENES12(this.SYS_N, this.SYS_T, this.GFBITS);
        } else {
            this.gf = new GF13();
            this.benes = new BENES13(this.SYS_N, this.SYS_T, this.GFBITS);
        }
        this.usePadding = this.SYS_T % 8 != 0;
        this.countErrorIndices = (1 << this.GFBITS) > this.SYS_N;
    }

    public byte[] generate_public_key_from_private_key(byte[] bArr) {
        byte[] bArr2 = new byte[getPublicKeySize()];
        short[] sArr = new short[1 << this.GFBITS];
        long[] jArr = {0};
        int[] iArr = new int[1 << this.GFBITS];
        byte[] bArr3 = new byte[(this.SYS_N / 8) + ((1 << this.GFBITS) * 4)];
        int length = ((bArr3.length - 32) - this.IRR_BYTES) - ((1 << this.GFBITS) * 4);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte) 64);
        sHAKEDigest.update(bArr, 0, 32);
        sHAKEDigest.doFinal(bArr3, 0, bArr3.length);
        for (int i = 0; i < (1 << this.GFBITS); i++) {
            iArr[i] = Utils.load4(bArr3, length + (i * 4));
        }
        pk_gen(bArr2, bArr, iArr, sArr, jArr);
        return bArr2;
    }

    public byte[] decompress_private_key(byte[] bArr) {
        byte[] bArr2 = new byte[getPrivateKeySize()];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        byte[] bArr3 = new byte[(this.SYS_N / 8) + ((1 << this.GFBITS) * 4) + this.IRR_BYTES + 32];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte) 64);
        sHAKEDigest.update(bArr, 0, 32);
        sHAKEDigest.doFinal(bArr3, 0, bArr3.length);
        if (bArr.length <= 40) {
            short[] sArr = new short[this.SYS_T];
            byte[] bArr4 = new byte[this.IRR_BYTES];
            int length = (bArr3.length - 32) - this.IRR_BYTES;
            for (int i = 0; i < this.SYS_T; i++) {
                sArr[i] = Utils.load_gf(bArr3, length + (i * 2), this.GFMASK);
            }
            generate_irr_poly(sArr);
            for (int i2 = 0; i2 < this.SYS_T; i2++) {
                Utils.store_gf(bArr4, i2 * 2, sArr[i2]);
            }
            System.arraycopy(bArr4, 0, bArr2, 40, this.IRR_BYTES);
        }
        if (bArr.length <= 40 + this.IRR_BYTES) {
            int[] iArr = new int[1 << this.GFBITS];
            short[] sArr2 = new short[1 << this.GFBITS];
            int length2 = ((bArr3.length - 32) - this.IRR_BYTES) - ((1 << this.GFBITS) * 4);
            for (int i3 = 0; i3 < (1 << this.GFBITS); i3++) {
                iArr[i3] = Utils.load4(bArr3, length2 + (i3 * 4));
            }
            if (this.usePivots) {
                pk_gen(null, bArr2, iArr, sArr2, new long[]{0});
            } else {
                long[] jArr = new long[1 << this.GFBITS];
                for (int i4 = 0; i4 < (1 << this.GFBITS); i4++) {
                    jArr[i4] = iArr[i4];
                    int i5 = i4;
                    jArr[i5] = jArr[i5] << 31;
                    int i6 = i4;
                    jArr[i6] = jArr[i6] | ((long) i4);
                    int i7 = i4;
                    jArr[i7] = jArr[i7] & Long.MAX_VALUE;
                }
                sort64(jArr, 0, jArr.length);
                for (int i8 = 0; i8 < (1 << this.GFBITS); i8++) {
                    sArr2[i8] = (short) (jArr[i8] & ((long) this.GFMASK));
                }
            }
            byte[] bArr5 = new byte[this.COND_BYTES];
            controlbitsfrompermutation(bArr5, sArr2, this.GFBITS, 1 << this.GFBITS);
            System.arraycopy(bArr5, 0, bArr2, this.IRR_BYTES + 40, bArr5.length);
        }
        System.arraycopy(bArr3, 0, bArr2, getPrivateKeySize() - (this.SYS_N / 8), this.SYS_N / 8);
        return bArr2;
    }

    public void kem_keypair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        int i;
        short[] sArr;
        byte[] bArrCopyOfRange = new byte[32];
        byte[] bArr3 = {64};
        secureRandom.nextBytes(bArrCopyOfRange);
        byte[] bArr4 = new byte[(this.SYS_N / 8) + ((1 << this.GFBITS) * 4) + (this.SYS_T * 2) + 32];
        byte[] bArrCopyOfRange2 = bArrCopyOfRange;
        long[] jArr = {0};
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        while (true) {
            sHAKEDigest.update(bArr3, 0, bArr3.length);
            sHAKEDigest.update(bArrCopyOfRange, 0, bArrCopyOfRange.length);
            sHAKEDigest.doFinal(bArr4, 0, bArr4.length);
            int length = bArr4.length - 32;
            bArrCopyOfRange = Arrays.copyOfRange(bArr4, length, length + 32);
            System.arraycopy(bArrCopyOfRange2, 0, bArr2, 0, 32);
            bArrCopyOfRange2 = Arrays.copyOfRange(bArrCopyOfRange, 0, 32);
            short[] sArr2 = new short[this.SYS_T];
            int length2 = (bArr4.length - 32) - (2 * this.SYS_T);
            for (int i2 = 0; i2 < this.SYS_T; i2++) {
                sArr2[i2] = Utils.load_gf(bArr4, length2 + (i2 * 2), this.GFMASK);
            }
            if (generate_irr_poly(sArr2) != -1) {
                for (int i3 = 0; i3 < this.SYS_T; i3++) {
                    Utils.store_gf(bArr2, 40 + (i3 * 2), sArr2[i3]);
                }
                int[] iArr = new int[1 << this.GFBITS];
                i = length2 - ((1 << this.GFBITS) * 4);
                for (int i4 = 0; i4 < (1 << this.GFBITS); i4++) {
                    iArr[i4] = Utils.load4(bArr4, i + (i4 * 4));
                }
                sArr = new short[1 << this.GFBITS];
                if (pk_gen(bArr, bArr2, iArr, sArr, jArr) != -1) {
                    break;
                }
            }
        }
        byte[] bArr5 = new byte[this.COND_BYTES];
        controlbitsfrompermutation(bArr5, sArr, this.GFBITS, 1 << this.GFBITS);
        System.arraycopy(bArr5, 0, bArr2, this.IRR_BYTES + 40, bArr5.length);
        System.arraycopy(bArr4, i - (this.SYS_N / 8), bArr2, bArr2.length - (this.SYS_N / 8), this.SYS_N / 8);
        if (this.usePivots) {
            Utils.store8(bArr2, 32, jArr[0]);
        } else {
            Utils.store8(bArr2, 32, 4294967295L);
        }
    }

    private void syndrome(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        short[] sArr = new short[this.SYS_N / 8];
        int i = 0;
        int i2 = this.PK_NROWS % 8;
        for (int i3 = 0; i3 < this.SYND_BYTES; i3++) {
            bArr[i3] = 0;
        }
        for (int i4 = 0; i4 < this.PK_NROWS; i4++) {
            for (int i5 = 0; i5 < this.SYS_N / 8; i5++) {
                sArr[i5] = 0;
            }
            for (int i6 = 0; i6 < this.PK_ROW_BYTES; i6++) {
                sArr[((this.SYS_N / 8) - this.PK_ROW_BYTES) + i6] = bArr2[i + i6];
            }
            if (this.usePadding) {
                for (int i7 = (this.SYS_N / 8) - 1; i7 >= (this.SYS_N / 8) - this.PK_ROW_BYTES; i7--) {
                    sArr[i7] = (short) ((((sArr[i7] & 255) << i2) | ((sArr[i7 - 1] & 255) >>> (8 - i2))) & 255);
                }
            }
            int i8 = i4 / 8;
            sArr[i8] = (short) (sArr[i8] | (1 << (i4 % 8)));
            byte b = 0;
            for (int i9 = 0; i9 < this.SYS_N / 8; i9++) {
                b = (byte) (b ^ (sArr[i9] & bArr3[i9]));
            }
            byte b2 = (byte) (b ^ (b >>> 4));
            byte b3 = (byte) (b2 ^ (b2 >>> 2));
            int i10 = i4 / 8;
            bArr[i10] = (byte) (bArr[i10] | (((byte) (((byte) (b3 ^ (b3 >>> 1))) & 1)) << (i4 % 8)));
            i += this.PK_ROW_BYTES;
        }
    }

    private void generate_error_vector(byte[] bArr, SecureRandom secureRandom) {
        short[] sArr = new short[this.SYS_T * 2];
        short[] sArr2 = new short[this.SYS_T];
        byte[] bArr2 = new byte[this.SYS_T];
        while (true) {
            if (this.countErrorIndices) {
                byte[] bArr3 = new byte[this.SYS_T * 4];
                secureRandom.nextBytes(bArr3);
                for (int i = 0; i < this.SYS_T * 2; i++) {
                    sArr[i] = Utils.load_gf(bArr3, i * 2, this.GFMASK);
                }
                int i2 = 0;
                for (int i3 = 0; i3 < this.SYS_T * 2 && i2 < this.SYS_T; i3++) {
                    if (sArr[i3] < this.SYS_N) {
                        int i4 = i2;
                        i2++;
                        sArr2[i4] = sArr[i3];
                    }
                }
                if (i2 < this.SYS_T) {
                    continue;
                }
            } else {
                byte[] bArr4 = new byte[this.SYS_T * 2];
                secureRandom.nextBytes(bArr4);
                for (int i5 = 0; i5 < this.SYS_T; i5++) {
                    sArr2[i5] = Utils.load_gf(bArr4, i5 * 2, this.GFMASK);
                }
            }
            boolean z = false;
            for (int i6 = 1; i6 < this.SYS_T && !z; i6++) {
                for (int i7 = 0; i7 < i6; i7++) {
                    if (sArr2[i6] == sArr2[i7]) {
                        z = true;
                        break;
                    }
                }
            }
            if (!z) {
                break;
            }
        }
        for (int i8 = 0; i8 < this.SYS_T; i8++) {
            bArr2[i8] = (byte) (1 << (sArr2[i8] & 7));
        }
        short s = 0;
        while (true) {
            short s2 = s;
            if (s2 >= this.SYS_N / 8) {
                return;
            }
            bArr[s2] = 0;
            for (int i9 = 0; i9 < this.SYS_T; i9++) {
                bArr[s2] = (byte) (bArr[s2] | (bArr2[i9] & ((short) (same_mask32(s2, (short) (sArr2[i9] >> 3)) & 255))));
            }
            s = (short) (s2 + 1);
        }
    }

    private void encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3, SecureRandom secureRandom) {
        generate_error_vector(bArr3, secureRandom);
        syndrome(bArr, bArr2, bArr3);
    }

    public int kem_enc(byte[] bArr, byte[] bArr2, byte[] bArr3, SecureRandom secureRandom) {
        byte[] bArr4 = new byte[this.SYS_N / 8];
        int iCheck_pk_padding = this.usePadding ? check_pk_padding(bArr3) : 0;
        encrypt(bArr, bArr3, bArr4, secureRandom);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte) 1);
        sHAKEDigest.update(bArr4, 0, bArr4.length);
        sHAKEDigest.update(bArr, 0, bArr.length);
        sHAKEDigest.doFinal(bArr2, 0, bArr2.length);
        if (!this.usePadding) {
            return 0;
        }
        byte b = (byte) (((byte) iCheck_pk_padding) ^ 255);
        for (int i = 0; i < this.SYND_BYTES; i++) {
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] & b);
        }
        for (int i3 = 0; i3 < 32; i3++) {
            int i4 = i3;
            bArr2[i4] = (byte) (bArr2[i4] & b);
        }
        return iCheck_pk_padding;
    }

    public int kem_dec(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[this.SYS_N / 8];
        byte[] bArr5 = new byte[1 + (this.SYS_N / 8) + this.SYND_BYTES];
        int iCheck_c_padding = this.usePadding ? check_c_padding(bArr2) : 0;
        short sDecrypt = (short) (((short) (((short) (((byte) decrypt(bArr4, bArr3, bArr2)) - 1)) >> 8)) & 255);
        bArr5[0] = (byte) (sDecrypt & 1);
        for (int i = 0; i < this.SYS_N / 8; i++) {
            bArr5[1 + i] = (byte) (((sDecrypt ^ (-1)) & bArr3[i + 40 + this.IRR_BYTES + this.COND_BYTES]) | (sDecrypt & bArr4[i]));
        }
        for (int i2 = 0; i2 < this.SYND_BYTES; i2++) {
            bArr5[1 + (this.SYS_N / 8) + i2] = bArr2[i2];
        }
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(bArr5, 0, bArr5.length);
        sHAKEDigest.doFinal(bArr, 0, bArr.length);
        if (!this.usePadding) {
            return 0;
        }
        byte b = (byte) iCheck_c_padding;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            int i4 = i3;
            bArr[i4] = (byte) (bArr[i4] | b);
        }
        return iCheck_c_padding;
    }

    private int decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        short[] sArr = new short[this.SYS_T + 1];
        short[] sArr2 = new short[this.SYS_N];
        short[] sArr3 = new short[this.SYS_T * 2];
        short[] sArr4 = new short[this.SYS_T * 2];
        short[] sArr5 = new short[this.SYS_T + 1];
        short[] sArr6 = new short[this.SYS_N];
        byte[] bArr4 = new byte[this.SYS_N / 8];
        for (int i = 0; i < this.SYND_BYTES; i++) {
            bArr4[i] = bArr3[i];
        }
        for (int i2 = this.SYND_BYTES; i2 < this.SYS_N / 8; i2++) {
            bArr4[i2] = 0;
        }
        for (int i3 = 0; i3 < this.SYS_T; i3++) {
            sArr[i3] = Utils.load_gf(bArr2, 40 + (i3 * 2), this.GFMASK);
        }
        sArr[this.SYS_T] = 1;
        this.benes.support_gen(sArr2, bArr2);
        synd(sArr3, sArr, sArr2, bArr4);
        bm(sArr5, sArr3);
        root(sArr6, sArr5, sArr2);
        for (int i4 = 0; i4 < this.SYS_N / 8; i4++) {
            bArr[i4] = 0;
        }
        int i5 = 0;
        for (int i6 = 0; i6 < this.SYS_N; i6++) {
            short sGf_iszero = (short) (this.gf.gf_iszero(sArr6[i6]) & 1);
            int i7 = i6 / 8;
            bArr[i7] = (byte) (bArr[i7] | (sGf_iszero << (i6 % 8)));
            i5 += sGf_iszero;
        }
        synd(sArr4, sArr, sArr2, bArr);
        int i8 = i5 ^ this.SYS_T;
        for (int i9 = 0; i9 < this.SYS_T * 2; i9++) {
            i8 |= sArr3[i9] ^ sArr4[i9];
        }
        int i10 = ((i8 - 1) >> 15) & 1;
        if ((i10 ^ 1) != 0) {
        }
        return i10 ^ 1;
    }

    private static int min(short s, int i) {
        return s < i ? s : i;
    }

    private void bm(short[] sArr, short[] sArr2) {
        short s = 0;
        short[] sArr3 = new short[this.SYS_T + 1];
        short[] sArr4 = new short[this.SYS_T + 1];
        short[] sArr5 = new short[this.SYS_T + 1];
        short s2 = 1;
        for (int i = 0; i < this.SYS_T + 1; i++) {
            sArr5[i] = 0;
            sArr4[i] = 0;
        }
        sArr4[0] = 1;
        sArr5[1] = 1;
        short s3 = 0;
        while (true) {
            short s4 = s3;
            if (s4 >= 2 * this.SYS_T) {
                break;
            }
            int iGf_mul_ext = 0;
            for (int i2 = 0; i2 <= min(s4, this.SYS_T); i2++) {
                iGf_mul_ext ^= this.gf.gf_mul_ext(sArr4[i2], sArr2[s4 - i2]);
            }
            short sGf_reduce = this.gf.gf_reduce(iGf_mul_ext);
            short s5 = (short) (((short) (((short) (((short) (sGf_reduce - 1)) >> 15)) & 1)) - 1);
            short s6 = (short) (((short) (((short) (((short) (((short) (s4 - (2 * s))) >> 15)) & 1)) - 1)) & s5);
            for (int i3 = 0; i3 <= this.SYS_T; i3++) {
                sArr3[i3] = sArr4[i3];
            }
            short sGf_frac = this.gf.gf_frac(s2, sGf_reduce);
            for (int i4 = 0; i4 <= this.SYS_T; i4++) {
                int i5 = i4;
                sArr4[i5] = (short) (sArr4[i5] ^ (this.gf.gf_mul(sGf_frac, sArr5[i4]) & s5));
            }
            s = (short) ((s & (s6 ^ (-1))) | (((s4 + 1) - s) & s6));
            for (int i6 = this.SYS_T - 1; i6 >= 0; i6--) {
                sArr5[i6 + 1] = (short) ((sArr5[i6] & (s6 ^ (-1))) | (sArr3[i6] & s6));
            }
            sArr5[0] = 0;
            s2 = (short) ((s2 & (s6 ^ (-1))) | (sGf_reduce & s6));
            s3 = (short) (s4 + 1);
        }
        for (int i7 = 0; i7 <= this.SYS_T; i7++) {
            sArr[i7] = sArr4[this.SYS_T - i7];
        }
    }

    private void synd(short[] sArr, short[] sArr2, short[] sArr3, byte[] bArr) {
        short s = (short) (bArr[0] & 1);
        short s2 = sArr3[0];
        short sGf_inv = (short) (this.gf.gf_inv(this.gf.gf_sq(eval(sArr2, s2))) & (-s));
        sArr[0] = sGf_inv;
        for (int i = 1; i < 2 * this.SYS_T; i++) {
            sGf_inv = this.gf.gf_mul(sGf_inv, s2);
            sArr[i] = sGf_inv;
        }
        for (int i2 = 1; i2 < this.SYS_N; i2++) {
            short s3 = (short) ((bArr[i2 / 8] >> (i2 % 8)) & 1);
            short s4 = sArr3[i2];
            short sGf_mul = this.gf.gf_mul(this.gf.gf_inv(this.gf.gf_sq(eval(sArr2, s4))), s3);
            sArr[0] = (short) (sArr[0] ^ sGf_mul);
            for (int i3 = 1; i3 < 2 * this.SYS_T; i3++) {
                sGf_mul = this.gf.gf_mul(sGf_mul, s4);
                int i4 = i3;
                sArr[i4] = (short) (sArr[i4] ^ sGf_mul);
            }
        }
    }

    private int mov_columns(byte[][] bArr, short[] sArr, long[] jArr) {
        long jLoad8;
        long[] jArr2 = new long[64];
        long[] jArr3 = new long[32];
        byte[] bArr2 = new byte[9];
        int i = this.PK_NROWS - 32;
        int i2 = i / 8;
        int i3 = i % 8;
        if (this.usePadding) {
            for (int i4 = 0; i4 < 32; i4++) {
                for (int i5 = 0; i5 < 9; i5++) {
                    bArr2[i5] = bArr[i + i4][i2 + i5];
                }
                for (int i6 = 0; i6 < 8; i6++) {
                    bArr2[i6] = (byte) (((bArr2[i6] & 255) >> i3) | (bArr2[i6 + 1] << (8 - i3)));
                }
                jArr2[i4] = Utils.load8(bArr2, 0);
            }
        } else {
            for (int i7 = 0; i7 < 32; i7++) {
                jArr2[i7] = Utils.load8(bArr[i + i7], i2);
            }
        }
        jArr[0] = 0;
        for (int i8 = 0; i8 < 32; i8++) {
            long j = jArr2[i8];
            for (int i9 = i8 + 1; i9 < 32; i9++) {
                j |= jArr2[i9];
            }
            if (j == 0) {
                return -1;
            }
            int iCtz = ctz(j);
            jArr3[i8] = iCtz;
            jArr[0] = jArr[0] | (1 << ((int) jArr3[i8]));
            for (int i10 = i8 + 1; i10 < 32; i10++) {
                int i11 = i8;
                jArr2[i11] = jArr2[i11] ^ (jArr2[i10] & (((jArr2[i8] >> iCtz) & 1) - 1));
            }
            for (int i12 = i8 + 1; i12 < 32; i12++) {
                int i13 = i12;
                jArr2[i13] = jArr2[i13] ^ (jArr2[i8] & (-((jArr2[i12] >> iCtz) & 1)));
            }
        }
        for (int i14 = 0; i14 < 32; i14++) {
            for (int i15 = i14 + 1; i15 < 64; i15++) {
                long jSame_mask64 = ((long) (sArr[i + i14] ^ sArr[i + i15])) & same_mask64((short) i15, (short) jArr3[i14]);
                int i16 = i + i14;
                sArr[i16] = (short) (((long) sArr[i16]) ^ jSame_mask64);
                int i17 = i + i15;
                sArr[i17] = (short) (((long) sArr[i17]) ^ jSame_mask64);
            }
        }
        for (int i18 = 0; i18 < this.PK_NROWS; i18++) {
            if (this.usePadding) {
                for (int i19 = 0; i19 < 9; i19++) {
                    bArr2[i19] = bArr[i18][i2 + i19];
                }
                for (int i20 = 0; i20 < 8; i20++) {
                    bArr2[i20] = (byte) (((bArr2[i20] & 255) >> i3) | (bArr2[i20 + 1] << (8 - i3)));
                }
                jLoad8 = Utils.load8(bArr2, 0);
            } else {
                jLoad8 = Utils.load8(bArr[i18], i2);
            }
            for (int i21 = 0; i21 < 32; i21++) {
                long j2 = ((jLoad8 >> i21) ^ (jLoad8 >> ((int) jArr3[i21]))) & 1;
                jLoad8 = (jLoad8 ^ (j2 << ((int) jArr3[i21]))) ^ (j2 << i21);
            }
            if (this.usePadding) {
                Utils.store8(bArr2, 0, jLoad8);
                bArr[i18][i2 + 8] = (byte) ((((bArr[i18][i2 + 8] & 255) >>> i3) << i3) | ((bArr2[7] & 255) >>> (8 - i3)));
                bArr[i18][i2 + 0] = (byte) (((bArr2[0] & 255) << i3) | (((bArr[i18][i2] & 255) << (8 - i3)) >>> (8 - i3)));
                for (int i22 = 7; i22 >= 1; i22--) {
                    bArr[i18][i2 + i22] = (byte) (((bArr2[i22] & 255) << i3) | ((bArr2[i22 - 1] & 255) >>> (8 - i3)));
                }
            } else {
                Utils.store8(bArr[i18], i2, jLoad8);
            }
        }
        return 0;
    }

    private static int ctz(long j) {
        long j2 = 72340172838076673L;
        long j3 = 0;
        long j4 = j ^ (-1);
        for (int i = 0; i < 8; i++) {
            j2 &= j4 >>> i;
            j3 += j2;
        }
        long j5 = j3 & 578721382704613384L;
        long j6 = j5 | (j5 >>> 1);
        long j7 = j6 | (j6 >>> 2);
        long j8 = j3;
        long j9 = j3 >>> 8;
        long j10 = j8 + (j9 & j7);
        for (int i2 = 2; i2 < 8; i2++) {
            j7 &= j7 >>> 8;
            j9 >>>= 8;
            j10 += j9 & j7;
        }
        return ((int) j10) & 255;
    }

    private static long same_mask64(short s, short s2) {
        return -((((long) (s ^ s2)) - 1) >>> 63);
    }

    private static byte same_mask32(short s, short s2) {
        return (byte) ((-(((s ^ s2) - 1) >>> 31)) & 255);
    }

    private static void layer(short[] sArr, byte[] bArr, int i, int i2, int i3) {
        int i4 = 1 << i2;
        int i5 = 0;
        int i6 = 0;
        while (true) {
            int i7 = i6;
            if (i7 >= i3) {
                return;
            }
            for (int i8 = 0; i8 < i4; i8++) {
                int i9 = (sArr[i7 + i8] ^ sArr[(i7 + i8) + i4]) & (-((bArr[i + (i5 >> 3)] >> (i5 & 7)) & 1));
                int i10 = i7 + i8;
                sArr[i10] = (short) (sArr[i10] ^ i9);
                int i11 = i7 + i8 + i4;
                sArr[i11] = (short) (sArr[i11] ^ i9);
                i5++;
            }
            i6 = i7 + (i4 * 2);
        }
    }

    private static void controlbitsfrompermutation(byte[] bArr, short[] sArr, long j, long j2) {
        short s;
        int[] iArr = new int[(int) (2 * j2)];
        short[] sArr2 = new short[(int) j2];
        do {
            for (int i = 0; i < (((((2 * j) - 1) * j2) / 2) + 7) / 8; i++) {
                bArr[i] = 0;
            }
            cbrecursion(bArr, 0L, 1L, sArr, 0, j, j2, iArr);
            for (int i2 = 0; i2 < j2; i2++) {
                sArr2[i2] = (short) i2;
            }
            int i3 = 0;
            for (int i4 = 0; i4 < j; i4++) {
                layer(sArr2, bArr, i3, i4, (int) j2);
                i3 = (int) (((long) i3) + (j2 >> 4));
            }
            for (int i5 = (int) (j - 2); i5 >= 0; i5--) {
                layer(sArr2, bArr, i3, i5, (int) j2);
                i3 = (int) (((long) i3) + (j2 >> 4));
            }
            s = 0;
            for (int i6 = 0; i6 < j2; i6++) {
                s = (short) (s | (sArr[i6] ^ sArr2[i6]));
            }
        } while (s != 0);
    }

    static short get_q_short(int[] iArr, int i) {
        int i2 = i / 2;
        return i % 2 == 0 ? (short) iArr[i2] : (short) ((iArr[i2] & (-65536)) >> 16);
    }

    static void cbrecursion(byte[] bArr, long j, long j2, short[] sArr, int i, long j3, long j4, int[] iArr) {
        if (j3 == 1) {
            int i2 = (int) (j >> 3);
            bArr[i2] = (byte) (bArr[i2] ^ (get_q_short(iArr, i) << ((int) (j & 7))));
            return;
        }
        if (sArr == null) {
            long j5 = 0;
            while (true) {
                long j6 = j5;
                if (j6 >= j4) {
                    break;
                }
                iArr[(int) j6] = ((get_q_short(iArr, (int) (((long) i) + j6)) ^ 1) << 16) | get_q_short(iArr, (int) (((long) i) + (j6 ^ 1)));
                j5 = j6 + 1;
            }
        } else {
            long j7 = 0;
            while (true) {
                long j8 = j7;
                if (j8 >= j4) {
                    break;
                }
                iArr[(int) j8] = ((sArr[(int) j8] ^ 1) << 16) | sArr[(int) (j8 ^ 1)];
                j7 = j8 + 1;
            }
        }
        sort32(iArr, 0, (int) j4);
        long j9 = 0;
        while (true) {
            long j10 = j9;
            if (j10 >= j4) {
                break;
            }
            int i3 = iArr[(int) j10] & 65535;
            int i4 = i3;
            if (j10 < i4) {
                i4 = (int) j10;
            }
            iArr[(int) (j4 + j10)] = (i3 << 16) | i4;
            j9 = j10 + 1;
        }
        long j11 = 0;
        while (true) {
            long j12 = j11;
            if (j12 >= j4) {
                break;
            }
            iArr[(int) j12] = (int) (((long) (iArr[(int) j12] << 16)) | j12);
            j11 = j12 + 1;
        }
        sort32(iArr, 0, (int) j4);
        long j13 = 0;
        while (true) {
            long j14 = j13;
            if (j14 >= j4) {
                break;
            }
            iArr[(int) j14] = (iArr[(int) j14] << 16) + (iArr[(int) (j4 + j14)] >> 16);
            j13 = j14 + 1;
        }
        sort32(iArr, 0, (int) j4);
        if (j3 > 10) {
            long j15 = 0;
            while (true) {
                long j16 = j15;
                if (j16 >= j4) {
                    break;
                }
                iArr[(int) (j4 + j16)] = (iArr[(int) j16] << 16) | (iArr[(int) (j4 + j16)] & 65535);
                j15 = j16 + 1;
            }
            long j17 = 1;
            while (true) {
                long j18 = j17;
                if (j18 >= j3 - 1) {
                    break;
                }
                long j19 = 0;
                while (true) {
                    long j20 = j19;
                    if (j20 >= j4) {
                        break;
                    }
                    iArr[(int) j20] = (int) (((long) (iArr[(int) (j4 + j20)] & (-65536))) | j20);
                    j19 = j20 + 1;
                }
                sort32(iArr, 0, (int) j4);
                long j21 = 0;
                while (true) {
                    long j22 = j21;
                    if (j22 >= j4) {
                        break;
                    }
                    iArr[(int) j22] = (iArr[(int) j22] << 16) | (iArr[(int) (j4 + j22)] & 65535);
                    j21 = j22 + 1;
                }
                if (j18 < j3 - 2) {
                    long j23 = 0;
                    while (true) {
                        long j24 = j23;
                        if (j24 >= j4) {
                            break;
                        }
                        iArr[(int) (j4 + j24)] = (iArr[(int) j24] & (-65536)) | (iArr[(int) (j4 + j24)] >> 16);
                        j23 = j24 + 1;
                    }
                    sort32(iArr, (int) j4, (int) (j4 * 2));
                    long j25 = 0;
                    while (true) {
                        long j26 = j25;
                        if (j26 >= j4) {
                            break;
                        }
                        iArr[(int) (j4 + j26)] = (iArr[(int) (j4 + j26)] << 16) | (iArr[(int) j26] & 65535);
                        j25 = j26 + 1;
                    }
                }
                sort32(iArr, 0, (int) j4);
                long j27 = 0;
                while (true) {
                    long j28 = j27;
                    if (j28 < j4) {
                        int i5 = (iArr[(int) (j4 + j28)] & (-65536)) | (iArr[(int) j28] & 65535);
                        if (i5 < iArr[(int) (j4 + j28)]) {
                            iArr[(int) (j4 + j28)] = i5;
                        }
                        j27 = j28 + 1;
                    }
                }
                j17 = j18 + 1;
            }
            long j29 = 0;
            while (true) {
                long j30 = j29;
                if (j30 >= j4) {
                    break;
                }
                int i6 = (int) (j4 + j30);
                iArr[i6] = iArr[i6] & 65535;
                j29 = j30 + 1;
            }
        } else {
            long j31 = 0;
            while (true) {
                long j32 = j31;
                if (j32 >= j4) {
                    break;
                }
                iArr[(int) (j4 + j32)] = ((iArr[(int) j32] & 65535) << 10) | (iArr[(int) (j4 + j32)] & WinUser.CF_GDIOBJLAST);
                j31 = j32 + 1;
            }
            long j33 = 1;
            while (true) {
                long j34 = j33;
                if (j34 >= j3 - 1) {
                    break;
                }
                long j35 = 0;
                while (true) {
                    long j36 = j35;
                    if (j36 >= j4) {
                        break;
                    }
                    iArr[(int) j36] = (int) (((long) ((iArr[(int) (j4 + j36)] & (-1024)) << 6)) | j36);
                    j35 = j36 + 1;
                }
                sort32(iArr, 0, (int) j4);
                long j37 = 0;
                while (true) {
                    long j38 = j37;
                    if (j38 >= j4) {
                        break;
                    }
                    iArr[(int) j38] = (iArr[(int) j38] << 20) | iArr[(int) (j4 + j38)];
                    j37 = j38 + 1;
                }
                sort32(iArr, 0, (int) j4);
                long j39 = 0;
                while (true) {
                    long j40 = j39;
                    if (j40 < j4) {
                        int i7 = iArr[(int) j40] & WinNT.NLS_VALID_LOCALE_MASK;
                        int i8 = (iArr[(int) j40] & 1047552) | (iArr[(int) (j4 + j40)] & WinUser.CF_GDIOBJLAST);
                        if (i7 < i8) {
                            i8 = i7;
                        }
                        iArr[(int) (j4 + j40)] = i8;
                        j39 = j40 + 1;
                    }
                }
                j33 = j34 + 1;
            }
            long j41 = 0;
            while (true) {
                long j42 = j41;
                if (j42 >= j4) {
                    break;
                }
                int i9 = (int) (j4 + j42);
                iArr[i9] = iArr[i9] & WinUser.CF_GDIOBJLAST;
                j41 = j42 + 1;
            }
        }
        if (sArr == null) {
            long j43 = 0;
            while (true) {
                long j44 = j43;
                if (j44 >= j4) {
                    break;
                }
                iArr[(int) j44] = (int) (((long) (get_q_short(iArr, (int) (((long) i) + j44)) << 16)) + j44);
                j43 = j44 + 1;
            }
        } else {
            long j45 = 0;
            while (true) {
                long j46 = j45;
                if (j46 >= j4) {
                    break;
                }
                iArr[(int) j46] = (int) (((long) (sArr[(int) j46] << 16)) + j46);
                j45 = j46 + 1;
            }
        }
        sort32(iArr, 0, (int) j4);
        long j47 = 0;
        while (true) {
            long j48 = j47;
            if (j48 >= j4 / 2) {
                break;
            }
            long j49 = 2 * j48;
            int i10 = iArr[(int) (j4 + j49)] & 1;
            int i11 = (int) (j49 + ((long) i10));
            int i12 = (int) (j >> 3);
            bArr[i12] = (byte) (bArr[i12] ^ (i10 << ((int) (j & 7))));
            j += j2;
            iArr[(int) (j4 + j49)] = (iArr[(int) j49] << 16) | i11;
            iArr[(int) (j4 + j49 + 1)] = (iArr[(int) (j49 + 1)] << 16) | (i11 ^ 1);
            j47 = j48 + 1;
        }
        sort32(iArr, (int) j4, (int) (j4 * 2));
        long j50 = j + (((2 * j3) - 3) * j2 * (j4 / 2));
        long j51 = 0;
        while (true) {
            long j52 = j51;
            if (j52 >= j4 / 2) {
                break;
            }
            long j53 = 2 * j52;
            int i13 = iArr[(int) (j4 + j53)] & 1;
            int i14 = (int) (j53 + ((long) i13));
            int i15 = (int) (j50 >> 3);
            bArr[i15] = (byte) (bArr[i15] ^ (i13 << ((int) (j50 & 7))));
            j50 += j2;
            iArr[(int) j53] = (i14 << 16) | (iArr[(int) (j4 + j53)] & 65535);
            iArr[(int) (j53 + 1)] = ((i14 ^ 1) << 16) | (iArr[(int) (j4 + j53 + 1)] & 65535);
            j51 = j52 + 1;
        }
        sort32(iArr, 0, (int) j4);
        long j54 = j50 - ((((2 * j3) - 2) * j2) * (j4 / 2));
        short[] sArr2 = new short[((int) j4) * 4];
        long j55 = 0;
        while (true) {
            long j56 = j55;
            if (j56 >= j4 * 2) {
                break;
            }
            sArr2[(int) ((j56 * 2) + 0)] = (short) iArr[(int) j56];
            sArr2[(int) ((j56 * 2) + 1)] = (short) ((iArr[(int) j56] & (-65536)) >> 16);
            j55 = j56 + 1;
        }
        long j57 = 0;
        while (true) {
            long j58 = j57;
            if (j58 >= j4 / 2) {
                break;
            }
            sArr2[(int) j58] = (short) ((iArr[(int) (2 * j58)] & 65535) >>> 1);
            sArr2[(int) (j58 + (j4 / 2))] = (short) ((iArr[(int) ((2 * j58) + 1)] & 65535) >>> 1);
            j57 = j58 + 1;
        }
        long j59 = 0;
        while (true) {
            long j60 = j59;
            if (j60 >= j4 / 2) {
                cbrecursion(bArr, j54, j2 * 2, null, ((int) (j4 + (j4 / 4))) * 2, j3 - 1, j4 / 2, iArr);
                cbrecursion(bArr, j54 + j2, j2 * 2, null, (int) (((j4 + (j4 / 4)) * 2) + (j4 / 2)), j3 - 1, j4 / 2, iArr);
                return;
            } else {
                iArr[(int) (j4 + (j4 / 4) + j60)] = (sArr2[(int) ((j60 * 2) + 1)] << 16) | sArr2[(int) (j60 * 2)];
                j59 = j60 + 1;
            }
        }
    }

    private int pk_gen(byte[] bArr, byte[] bArr2, int[] iArr, short[] sArr, long[] jArr) {
        short[] sArr2 = new short[this.SYS_T + 1];
        sArr2[this.SYS_T] = 1;
        for (int i = 0; i < this.SYS_T; i++) {
            sArr2[i] = Utils.load_gf(bArr2, 40 + (i * 2), this.GFMASK);
        }
        long[] jArr2 = new long[1 << this.GFBITS];
        for (int i2 = 0; i2 < (1 << this.GFBITS); i2++) {
            jArr2[i2] = iArr[i2];
            int i3 = i2;
            jArr2[i3] = jArr2[i3] << 31;
            int i4 = i2;
            jArr2[i4] = jArr2[i4] | ((long) i2);
            int i5 = i2;
            jArr2[i5] = jArr2[i5] & Long.MAX_VALUE;
        }
        sort64(jArr2, 0, jArr2.length);
        for (int i6 = 1; i6 < (1 << this.GFBITS); i6++) {
            if ((jArr2[i6 - 1] >> 31) == (jArr2[i6] >> 31)) {
                return -1;
            }
        }
        short[] sArr3 = new short[this.SYS_N];
        for (int i7 = 0; i7 < (1 << this.GFBITS); i7++) {
            sArr[i7] = (short) (jArr2[i7] & ((long) this.GFMASK));
        }
        for (int i8 = 0; i8 < this.SYS_N; i8++) {
            sArr3[i8] = Utils.bitrev(sArr[i8], this.GFBITS);
        }
        short[] sArr4 = new short[this.SYS_N];
        root(sArr4, sArr2, sArr3);
        for (int i9 = 0; i9 < this.SYS_N; i9++) {
            sArr4[i9] = this.gf.gf_inv(sArr4[i9]);
        }
        byte[][] bArr3 = new byte[this.PK_NROWS][this.SYS_N / 8];
        for (int i10 = 0; i10 < this.PK_NROWS; i10++) {
            for (int i11 = 0; i11 < this.SYS_N / 8; i11++) {
                bArr3[i10][i11] = 0;
            }
        }
        int i12 = 0;
        while (i12 < this.SYS_T) {
            for (int i13 = 0; i13 < this.SYS_N; i13 += 8) {
                for (int i14 = 0; i14 < this.GFBITS; i14++) {
                    bArr3[(i12 * this.GFBITS) + i14][i13 / 8] = (byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) (((byte) ((sArr4[i13 + 7] >>> i14) & 1)) << 1)) | ((sArr4[i13 + 6] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 5] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 4] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 3] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 2] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 1] >>> i14) & 1))) << 1)) | ((sArr4[i13 + 0] >>> i14) & 1));
                }
            }
            for (int i15 = 0; i15 < this.SYS_N; i15++) {
                sArr4[i15] = this.gf.gf_mul(sArr4[i15], sArr3[i15]);
            }
            i12++;
        }
        for (int i16 = 0; i16 < this.PK_NROWS; i16++) {
            i12 = i16 >>> 3;
            int i17 = i16 & 7;
            if (this.usePivots && i16 == this.PK_NROWS - 32 && mov_columns(bArr3, sArr, jArr) != 0) {
                return -1;
            }
            for (int i18 = i16 + 1; i18 < this.PK_NROWS; i18++) {
                byte b = (byte) (-((byte) (((byte) (((byte) (bArr3[i16][i12] ^ bArr3[i18][i12])) >> i17)) & 1)));
                for (int i19 = 0; i19 < this.SYS_N / 8; i19++) {
                    byte[] bArr4 = bArr3[i16];
                    int i20 = i19;
                    bArr4[i20] = (byte) (bArr4[i20] ^ (bArr3[i18][i19] & b));
                }
            }
            if (((bArr3[i16][i12] >> i17) & 1) == 0) {
                return -1;
            }
            for (int i21 = 0; i21 < this.PK_NROWS; i21++) {
                if (i21 != i16) {
                    byte b2 = (byte) (-((byte) (((byte) (bArr3[i21][i12] >> i17)) & 1)));
                    for (int i22 = 0; i22 < this.SYS_N / 8; i22++) {
                        byte[] bArr5 = bArr3[i21];
                        int i23 = i22;
                        bArr5[i23] = (byte) (bArr5[i23] ^ (bArr3[i16][i22] & b2));
                    }
                }
            }
        }
        if (bArr == null) {
            return 0;
        }
        if (!this.usePadding) {
            int i24 = ((this.SYS_N - this.PK_NROWS) + 7) / 8;
            for (int i25 = 0; i25 < this.PK_NROWS; i25++) {
                System.arraycopy(bArr3[i25], this.PK_NROWS / 8, bArr, i24 * i25, i24);
            }
            return 0;
        }
        int i26 = 0;
        int i27 = this.PK_NROWS % 8;
        if (i27 == 0) {
            System.arraycopy(bArr3[i12], (this.PK_NROWS - 1) / 8, bArr, 0, this.SYS_N / 8);
            int i28 = 0 + (this.SYS_N / 8);
            return 0;
        }
        for (int i29 = 0; i29 < this.PK_NROWS; i29++) {
            int i30 = (this.PK_NROWS - 1) / 8;
            while (i30 < (this.SYS_N / 8) - 1) {
                int i31 = i26;
                i26++;
                bArr[i31] = (byte) (((bArr3[i29][i30] & 255) >>> i27) | (bArr3[i29][i30 + 1] << (8 - i27)));
                i30++;
            }
            int i32 = i26;
            i26++;
            bArr[i32] = (byte) ((bArr3[i29][i30] & 255) >>> i27);
        }
        return 0;
    }

    private short eval(short[] sArr, short s) {
        short sGf_mul = sArr[this.SYS_T];
        for (int i = this.SYS_T - 1; i >= 0; i--) {
            sGf_mul = (short) (this.gf.gf_mul(sGf_mul, s) ^ sArr[i]);
        }
        return sGf_mul;
    }

    private void root(short[] sArr, short[] sArr2, short[] sArr3) {
        for (int i = 0; i < this.SYS_N; i++) {
            sArr[i] = eval(sArr2, sArr3[i]);
        }
    }

    private int generate_irr_poly(short[] sArr) {
        short[][] sArr2 = new short[this.SYS_T + 1][this.SYS_T];
        sArr2[0][0] = 1;
        System.arraycopy(sArr, 0, sArr2[1], 0, this.SYS_T);
        int[] iArr = new int[(this.SYS_T * 2) - 1];
        int i = 2;
        while (i < this.SYS_T) {
            this.gf.gf_sqr_poly(this.SYS_T, this.poly, sArr2[i], sArr2[i >>> 1], iArr);
            this.gf.gf_mul_poly(this.SYS_T, this.poly, sArr2[i + 1], sArr2[i], sArr, iArr);
            i += 2;
        }
        if (i == this.SYS_T) {
            this.gf.gf_sqr_poly(this.SYS_T, this.poly, sArr2[i], sArr2[i >>> 1], iArr);
        }
        for (int i2 = 0; i2 < this.SYS_T; i2++) {
            for (int i3 = i2 + 1; i3 < this.SYS_T; i3++) {
                short sGf_iszero = this.gf.gf_iszero(sArr2[i2][i2]);
                for (int i4 = i2; i4 < this.SYS_T + 1; i4++) {
                    short[] sArr3 = sArr2[i4];
                    int i5 = i2;
                    sArr3[i5] = (short) (sArr3[i5] ^ ((short) (sArr2[i4][i3] & sGf_iszero)));
                }
            }
            if (sArr2[i2][i2] == 0) {
                return -1;
            }
            short sGf_inv = this.gf.gf_inv(sArr2[i2][i2]);
            for (int i6 = i2; i6 < this.SYS_T + 1; i6++) {
                sArr2[i6][i2] = this.gf.gf_mul(sArr2[i6][i2], sGf_inv);
            }
            for (int i7 = 0; i7 < this.SYS_T; i7++) {
                if (i7 != i2) {
                    short s = sArr2[i2][i7];
                    for (int i8 = i2; i8 <= this.SYS_T; i8++) {
                        short[] sArr4 = sArr2[i8];
                        int i9 = i7;
                        sArr4[i9] = (short) (sArr4[i9] ^ this.gf.gf_mul(sArr2[i8][i2], s));
                    }
                }
            }
        }
        System.arraycopy(sArr2[this.SYS_T], 0, sArr, 0, this.SYS_T);
        return 0;
    }

    int check_pk_padding(byte[] bArr) {
        byte b = 0;
        for (int i = 0; i < this.PK_NROWS; i++) {
            b = (byte) (b | bArr[((i * this.PK_ROW_BYTES) + this.PK_ROW_BYTES) - 1]);
        }
        return ((byte) ((((byte) (((byte) ((b & 255) >>> (this.PK_NCOLS % 8))) - 1)) & 255) >>> 7)) - 1;
    }

    int check_c_padding(byte[] bArr) {
        return ((byte) ((((byte) (((byte) ((bArr[this.SYND_BYTES - 1] & 255) >>> (this.PK_NROWS % 8))) - 1)) & 255) >>> 7)) - 1;
    }

    public int getDefaultSessionKeySize() {
        return this.defaultKeySize;
    }

    private static void sort32(int[] iArr, int i, int i2) {
        int i3;
        int i4 = i2 - i;
        if (i4 < 2) {
            return;
        }
        int i5 = 1;
        while (true) {
            i3 = i5;
            if (i3 >= i4 - i3) {
                break;
            } else {
                i5 = i3 + i3;
            }
        }
        int i6 = i3;
        while (true) {
            int i7 = i6;
            if (i7 <= 0) {
                return;
            }
            for (int i8 = 0; i8 < i4 - i7; i8++) {
                if ((i8 & i7) == 0) {
                    int i9 = iArr[(i + i8) + i7] ^ iArr[i + i8];
                    int i10 = iArr[(i + i8) + i7] - iArr[i + i8];
                    int i11 = ((i10 ^ (i9 & (i10 ^ iArr[(i + i8) + i7]))) >> 31) & i9;
                    int i12 = i + i8;
                    iArr[i12] = iArr[i12] ^ i11;
                    int i13 = i + i8 + i7;
                    iArr[i13] = iArr[i13] ^ i11;
                }
            }
            int i14 = 0;
            int i15 = i3;
            while (true) {
                int i16 = i15;
                if (i16 > i7) {
                    while (i14 < i4 - i16) {
                        if ((i14 & i7) == 0) {
                            int i17 = iArr[i + i14 + i7];
                            int i18 = i16;
                            while (true) {
                                int i19 = i18;
                                if (i19 <= i7) {
                                    break;
                                }
                                int i20 = iArr[(i + i14) + i19] ^ i17;
                                int i21 = iArr[(i + i14) + i19] - i17;
                                int i22 = ((i21 ^ (i20 & (i21 ^ iArr[(i + i14) + i19]))) >> 31) & i20;
                                i17 ^= i22;
                                int i23 = i + i14 + i19;
                                iArr[i23] = iArr[i23] ^ i22;
                                i18 = i19 >>> 1;
                            }
                            iArr[i + i14 + i7] = i17;
                        }
                        i14++;
                    }
                    i15 = i16 >>> 1;
                }
            }
            i6 = i7 >>> 1;
        }
    }

    private static void sort64(long[] jArr, int i, int i2) {
        int i3;
        int i4 = i2 - i;
        if (i4 < 2) {
            return;
        }
        int i5 = 1;
        while (true) {
            i3 = i5;
            if (i3 >= i4 - i3) {
                break;
            } else {
                i5 = i3 + i3;
            }
        }
        int i6 = i3;
        while (true) {
            int i7 = i6;
            if (i7 <= 0) {
                return;
            }
            for (int i8 = 0; i8 < i4 - i7; i8++) {
                if ((i8 & i7) == 0) {
                    long j = (-((jArr[(i + i8) + i7] - jArr[i + i8]) >>> 63)) & (jArr[i + i8] ^ jArr[(i + i8) + i7]);
                    int i9 = i + i8;
                    jArr[i9] = jArr[i9] ^ j;
                    int i10 = i + i8 + i7;
                    jArr[i10] = jArr[i10] ^ j;
                }
            }
            int i11 = 0;
            int i12 = i3;
            while (true) {
                int i13 = i12;
                if (i13 > i7) {
                    while (i11 < i4 - i13) {
                        if ((i11 & i7) == 0) {
                            long j2 = jArr[i + i11 + i7];
                            int i14 = i13;
                            while (true) {
                                int i15 = i14;
                                if (i15 <= i7) {
                                    break;
                                }
                                long j3 = (-((jArr[(i + i11) + i15] - j2) >>> 63)) & (j2 ^ jArr[(i + i11) + i15]);
                                j2 ^= j3;
                                int i16 = i + i11 + i15;
                                jArr[i16] = jArr[i16] ^ j3;
                                i14 = i15 >>> 1;
                            }
                            jArr[i + i11 + i7] = j2;
                        }
                        i11++;
                    }
                    i12 = i13 >>> 1;
                }
            }
            i6 = i7 >>> 1;
        }
    }
}
