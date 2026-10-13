package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoEngine.class */
class FrodoEngine {
    static final int nbar = 8;
    private static final int mbar = 8;
    private static final int len_seedA = 128;
    private static final int len_z = 128;
    private static final int len_chi = 16;
    private static final int len_seedA_bytes = 16;
    private static final int len_z_bytes = 16;
    private static final int len_chi_bytes = 2;
    private final int D;
    private final int q;
    private final int n;
    private final int B;
    private final int len_sk_bytes;
    private final int len_pk_bytes;
    private final int len_ct_bytes;
    private final short[] T_chi;
    private final int len_mu;
    private final int len_seedSE;
    private final int len_s;
    private final int len_k;
    private final int len_pkh;
    private final int len_ss;
    private final int len_mu_bytes;
    private final int len_seedSE_bytes;
    private final int len_s_bytes;
    private final int len_k_bytes;
    private final int len_pkh_bytes;
    private final int len_ss_bytes;
    private final Xof digest;
    private final FrodoMatrixGenerator gen;

    public int getCipherTextSize() {
        return this.len_ct_bytes;
    }

    public int getSessionKeySize() {
        return this.len_ss_bytes;
    }

    public int getPrivateKeySize() {
        return this.len_sk_bytes;
    }

    public int getPublicKeySize() {
        return this.len_pk_bytes;
    }

    public FrodoEngine(int i, int i2, int i3, short[] sArr, Xof xof, FrodoMatrixGenerator frodoMatrixGenerator) {
        this.n = i;
        this.D = i2;
        this.q = 1 << i2;
        this.B = i3;
        this.len_mu = i3 * 8 * 8;
        this.len_seedSE = this.len_mu;
        this.len_s = this.len_mu;
        this.len_k = this.len_mu;
        this.len_pkh = this.len_mu;
        this.len_ss = this.len_mu;
        this.len_mu_bytes = this.len_mu / 8;
        this.len_seedSE_bytes = this.len_seedSE / 8;
        this.len_s_bytes = this.len_s / 8;
        this.len_k_bytes = this.len_k / 8;
        this.len_pkh_bytes = this.len_pkh / 8;
        this.len_ss_bytes = this.len_ss / 8;
        this.len_ct_bytes = (((i2 * i) * 8) / 8) + (((i2 * 8) * 8) / 8);
        this.len_pk_bytes = 16 + (((i2 * i) * 8) / 8);
        this.len_sk_bytes = this.len_s_bytes + this.len_pk_bytes + (2 * i * 8) + this.len_pkh_bytes;
        this.T_chi = sArr;
        this.digest = xof;
        this.gen = frodoMatrixGenerator;
    }

    private short sample(short s) {
        short s2 = (short) ((s & 65535) >>> 1);
        short s3 = 0;
        for (int i = 0; i < this.T_chi.length; i++) {
            if (s2 > this.T_chi[i]) {
                s3 = (short) (s3 + 1);
            }
        }
        if ((s & 65535) % 2 == 1) {
            s3 = (short) ((s3 * (-1)) & 65535);
        }
        return s3;
    }

    private short[] sample_matrix(short[] sArr, int i, int i2, int i3) {
        short[] sArr2 = new short[i2 * i3];
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i3; i5++) {
                sArr2[(i4 * i3) + i5] = sample(sArr[(i4 * i3) + i5 + i]);
            }
        }
        return sArr2;
    }

    private short[] matrix_transpose(short[] sArr, int i, int i2) {
        short[] sArr2 = new short[i * i2];
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                sArr2[(i3 * i) + i4] = sArr[(i4 * i2) + i3];
            }
        }
        return sArr2;
    }

    private short[] matrix_mul(short[] sArr, int i, int i2, short[] sArr2, int i3, int i4) {
        int i5 = this.q - 1;
        short[] sArr3 = new short[i * i4];
        for (int i6 = 0; i6 < i; i6++) {
            for (int i7 = 0; i7 < i4; i7++) {
                int i8 = 0;
                for (int i9 = 0; i9 < i2; i9++) {
                    i8 += sArr[(i6 * i2) + i9] * sArr2[(i9 * i4) + i7];
                }
                sArr3[(i6 * i4) + i7] = (short) (i8 & i5);
            }
        }
        return sArr3;
    }

    private short[] matrix_add(short[] sArr, short[] sArr2, int i, int i2) {
        int i3 = this.q - 1;
        short[] sArr3 = new short[i * i2];
        for (int i4 = 0; i4 < i; i4++) {
            for (int i5 = 0; i5 < i2; i5++) {
                sArr3[(i4 * i2) + i5] = (short) ((sArr[(i4 * i2) + i5] + sArr2[(i4 * i2) + i5]) & i3);
            }
        }
        return sArr3;
    }

    private byte[] pack(short[] sArr) {
        int length = sArr.length;
        byte[] bArr = new byte[(this.D * length) / 8];
        short s = 0;
        short s2 = 0;
        short s3 = 0;
        byte b = 0;
        while (s < bArr.length && (s2 < length || (s2 == length && b > 0))) {
            byte b2 = 0;
            while (b2 < 8) {
                int iMin = Math.min(8 - b2, (int) b);
                bArr[s] = (byte) (bArr[s] + (((byte) ((s3 >> (b - iMin)) & ((short) ((1 << iMin) - 1)))) << ((8 - b2) - iMin)));
                b2 = (byte) (b2 + iMin);
                b = (byte) (b - iMin);
                if (b == 0) {
                    if (s2 >= length) {
                        break;
                    }
                    s3 = sArr[s2];
                    b = (byte) this.D;
                    s2 = (short) (s2 + 1);
                }
            }
            if (b2 == 8) {
                s = (short) (s + 1);
            }
        }
        return bArr;
    }

    public void kem_keypair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        byte[] bArr3 = new byte[this.len_s_bytes + this.len_seedSE_bytes + 16];
        secureRandom.nextBytes(bArr3);
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr3, 0, this.len_s_bytes);
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr3, this.len_s_bytes, this.len_s_bytes + this.len_seedSE_bytes);
        byte[] bArrCopyOfRange3 = Arrays.copyOfRange(bArr3, this.len_s_bytes + this.len_seedSE_bytes, this.len_s_bytes + this.len_seedSE_bytes + 16);
        byte[] bArr4 = new byte[16];
        this.digest.update(bArrCopyOfRange3, 0, bArrCopyOfRange3.length);
        this.digest.doFinal(bArr4, 0, bArr4.length);
        short[] sArrGenMatrix = this.gen.genMatrix(bArr4);
        byte[] bArr5 = new byte[2 * this.n * 8 * 2];
        this.digest.update((byte) 95);
        this.digest.update(bArrCopyOfRange2, 0, bArrCopyOfRange2.length);
        this.digest.doFinal(bArr5, 0, bArr5.length);
        short[] sArr = new short[2 * this.n * 8];
        for (int i = 0; i < sArr.length; i++) {
            sArr[i] = Pack.littleEndianToShort(bArr5, i * 2);
        }
        short[] sArrSample_matrix = sample_matrix(sArr, 0, 8, this.n);
        System.arraycopy(Arrays.concatenate(bArr4, pack(matrix_add(matrix_mul(sArrGenMatrix, this.n, this.n, matrix_transpose(sArrSample_matrix, 8, this.n), this.n, 8), sample_matrix(sArr, this.n * 8, this.n, 8), this.n, 8))), 0, bArr, 0, this.len_pk_bytes);
        byte[] bArr6 = new byte[this.len_pkh_bytes];
        this.digest.update(bArr, 0, bArr.length);
        this.digest.doFinal(bArr6, 0, bArr6.length);
        System.arraycopy(Arrays.concatenate(bArrCopyOfRange, bArr), 0, bArr2, 0, this.len_s_bytes + this.len_pk_bytes);
        for (int i2 = 0; i2 < 8; i2++) {
            for (int i3 = 0; i3 < this.n; i3++) {
                System.arraycopy(Pack.shortToLittleEndian(sArrSample_matrix[(i2 * this.n) + i3]), 0, bArr2, this.len_s_bytes + this.len_pk_bytes + (i2 * this.n * 2) + (i3 * 2), 2);
            }
        }
        System.arraycopy(bArr6, 0, bArr2, this.len_sk_bytes - this.len_pkh_bytes, this.len_pkh_bytes);
    }

    private short[] unpack(byte[] bArr, int i, int i2) {
        short[] sArr = new short[i * i2];
        short s = 0;
        short s2 = 0;
        byte b = 0;
        byte b2 = 0;
        while (s < sArr.length && (s2 < bArr.length || (s2 == bArr.length && b2 > 0))) {
            byte b3 = 0;
            while (b3 < this.D) {
                int iMin = Math.min(this.D - b3, (int) b2);
                short s3 = (short) (((1 << iMin) - 1) & 65535);
                sArr[s] = (short) (((sArr[s] & 65535) + ((((byte) ((((b & 255) >>> ((b2 & 255) - iMin)) & (s3 & 65535)) & 255)) & 255) << ((this.D - (b3 & 255)) - iMin))) & 65535);
                b3 = (byte) (b3 + iMin);
                b2 = (byte) (b2 - iMin);
                b = (byte) (b & ((s3 << b2) ^ (-1)));
                if (b2 == 0) {
                    if (s2 >= bArr.length) {
                        break;
                    }
                    b = bArr[s2];
                    b2 = 8;
                    s2 = (short) (s2 + 1);
                }
            }
            if (b3 == this.D) {
                s = (short) (s + 1);
            }
        }
        return sArr;
    }

    private short[] encode(byte[] bArr) {
        int i = 0;
        int i2 = 0;
        short[] sArr = new short[64];
        for (int i3 = 0; i3 < 8; i3++) {
            for (int i4 = 0; i4 < 8; i4++) {
                int i5 = 0;
                for (int i6 = 0; i6 < this.B; i6++) {
                    i5 += ((bArr[i] >>> i2) & 1) << i6;
                    int i7 = i2 + 1;
                    i += i7 >>> 3;
                    i2 = i7 & 7;
                }
                sArr[(i3 * 8) + i4] = (short) (i5 * (this.q / (1 << this.B)));
            }
        }
        return sArr;
    }

    public void kem_enc(byte[] bArr, byte[] bArr2, byte[] bArr3, SecureRandom secureRandom) {
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr3, 0, 16);
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr3, 16, this.len_pk_bytes);
        byte[] bArr4 = new byte[this.len_mu_bytes];
        secureRandom.nextBytes(bArr4);
        byte[] bArr5 = new byte[this.len_pkh_bytes];
        this.digest.update(bArr3, 0, this.len_pk_bytes);
        this.digest.doFinal(bArr5, 0, this.len_pkh_bytes);
        byte[] bArr6 = new byte[this.len_seedSE + this.len_k];
        this.digest.update(bArr5, 0, this.len_pkh_bytes);
        this.digest.update(bArr4, 0, this.len_mu_bytes);
        this.digest.doFinal(bArr6, 0, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] bArrCopyOfRange3 = Arrays.copyOfRange(bArr6, 0, this.len_seedSE_bytes);
        byte[] bArrCopyOfRange4 = Arrays.copyOfRange(bArr6, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] bArr7 = new byte[((16 * this.n) + 64) * 2];
        this.digest.update((byte) -106);
        this.digest.update(bArrCopyOfRange3, 0, bArrCopyOfRange3.length);
        this.digest.doFinal(bArr7, 0, bArr7.length);
        short[] sArr = new short[bArr7.length / 2];
        for (int i = 0; i < sArr.length; i++) {
            sArr[i] = Pack.littleEndianToShort(bArr7, i * 2);
        }
        short[] sArrSample_matrix = sample_matrix(sArr, 0, 8, this.n);
        byte[] bArrPack = pack(matrix_add(matrix_mul(sArrSample_matrix, 8, this.n, this.gen.genMatrix(bArrCopyOfRange), this.n, this.n), sample_matrix(sArr, 8 * this.n, 8, this.n), 8, this.n));
        byte[] bArrPack2 = pack(matrix_add(matrix_add(matrix_mul(sArrSample_matrix, 8, this.n, unpack(bArrCopyOfRange2, this.n, 8), this.n, 8), sample_matrix(sArr, 16 * this.n, 8, 8), 8, 8), encode(bArr4), 8, 8));
        System.arraycopy(Arrays.concatenate(bArrPack, bArrPack2), 0, bArr, 0, this.len_ct_bytes);
        this.digest.update(bArrPack, 0, bArrPack.length);
        this.digest.update(bArrPack2, 0, bArrPack2.length);
        this.digest.update(bArrCopyOfRange4, 0, this.len_k_bytes);
        this.digest.doFinal(bArr2, 0, this.len_s_bytes);
    }

    private short[] matrix_sub(short[] sArr, short[] sArr2, int i, int i2) {
        int i3 = this.q - 1;
        short[] sArr3 = new short[i * i2];
        for (int i4 = 0; i4 < i; i4++) {
            for (int i5 = 0; i5 < i2; i5++) {
                sArr3[(i4 * i2) + i5] = (short) ((sArr[(i4 * i2) + i5] - sArr2[(i4 * i2) + i5]) & i3);
            }
        }
        return sArr3;
    }

    private byte[] decode(short[] sArr) {
        int i = 0;
        short s = (short) ((1 << this.B) - 1);
        short s2 = (short) ((1 << this.D) - 1);
        byte[] bArr = new byte[8 * this.B];
        for (int i2 = 0; i2 < 8; i2++) {
            long j = 0;
            for (int i3 = 0; i3 < 8; i3++) {
                j |= ((long) (((short) (((sArr[i] & s2) + (1 << ((this.D - this.B) - 1))) >> (this.D - this.B))) & s)) << (this.B * i3);
                i++;
            }
            for (int i4 = 0; i4 < this.B; i4++) {
                bArr[(i2 * this.B) + i4] = (byte) ((j >> (8 * i4)) & 255);
            }
        }
        return bArr;
    }

    private short ctverify(short[] sArr, short[] sArr2, short[] sArr3, short[] sArr4) {
        short s = 0;
        short s2 = 0;
        while (true) {
            short s3 = s2;
            if (s3 >= sArr.length) {
                break;
            }
            s = (short) (s | (sArr[s3] ^ sArr3[s3]));
            s2 = (short) (s3 + 1);
        }
        short s4 = 0;
        while (true) {
            short s5 = s4;
            if (s5 >= sArr2.length) {
                break;
            }
            s = (short) (s | (sArr2[s5] ^ sArr4[s5]));
            s4 = (short) (s5 + 1);
        }
        return s == 0 ? (short) 0 : (short) -1;
    }

    private byte[] ctselect(byte[] bArr, byte[] bArr2, short s) {
        byte[] bArr3 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr3[i] = (byte) (((s ^ (-1)) & bArr[i] & 255) | (s & bArr2[i] & 255));
        }
        return bArr3;
    }

    public void kem_dec(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i = ((8 * this.n) * this.D) / 8;
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr2, 0, 0 + i);
        int i2 = 0 + i;
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr2, i2, i2 + ((64 * this.D) / 8));
        int i3 = this.len_s_bytes;
        byte[] bArrCopyOfRange3 = Arrays.copyOfRange(bArr3, 0, 0 + i3);
        int i4 = 0 + i3;
        byte[] bArrCopyOfRange4 = Arrays.copyOfRange(bArr3, i4, i4 + 16);
        int i5 = i4 + 16;
        int i6 = ((this.D * this.n) * 8) / 8;
        byte[] bArrCopyOfRange5 = Arrays.copyOfRange(bArr3, i5, i5 + i6);
        int i7 = i5 + i6;
        int i8 = ((this.n * 8) * 16) / 8;
        byte[] bArrCopyOfRange6 = Arrays.copyOfRange(bArr3, i7, i7 + i8);
        short[] sArr = new short[8 * this.n];
        for (int i9 = 0; i9 < 8; i9++) {
            for (int i10 = 0; i10 < this.n; i10++) {
                sArr[(i9 * this.n) + i10] = Pack.littleEndianToShort(bArrCopyOfRange6, (i9 * this.n * 2) + (i10 * 2));
            }
        }
        short[] sArrMatrix_transpose = matrix_transpose(sArr, 8, this.n);
        int i11 = i7 + i8;
        byte[] bArrCopyOfRange7 = Arrays.copyOfRange(bArr3, i11, i11 + this.len_pkh_bytes);
        short[] sArrUnpack = unpack(bArrCopyOfRange, 8, this.n);
        short[] sArrUnpack2 = unpack(bArrCopyOfRange2, 8, 8);
        byte[] bArrDecode = decode(matrix_sub(sArrUnpack2, matrix_mul(sArrUnpack, 8, this.n, sArrMatrix_transpose, this.n, 8), 8, 8));
        byte[] bArr4 = new byte[this.len_seedSE_bytes + this.len_k_bytes];
        this.digest.update(bArrCopyOfRange7, 0, this.len_pkh_bytes);
        this.digest.update(bArrDecode, 0, this.len_mu_bytes);
        this.digest.doFinal(bArr4, 0, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] bArrCopyOfRange8 = Arrays.copyOfRange(bArr4, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] bArr5 = new byte[((16 * this.n) + 64) * 2];
        this.digest.update((byte) -106);
        this.digest.update(bArr4, 0, this.len_seedSE_bytes);
        this.digest.doFinal(bArr5, 0, bArr5.length);
        short[] sArr2 = new short[(16 * this.n) + 64];
        for (int i12 = 0; i12 < sArr2.length; i12++) {
            sArr2[i12] = Pack.littleEndianToShort(bArr5, i12 * 2);
        }
        short[] sArrSample_matrix = sample_matrix(sArr2, 0, 8, this.n);
        byte[] bArrCtselect = ctselect(bArrCopyOfRange8, bArrCopyOfRange3, ctverify(sArrUnpack, sArrUnpack2, matrix_add(matrix_mul(sArrSample_matrix, 8, this.n, this.gen.genMatrix(bArrCopyOfRange4), this.n, this.n), sample_matrix(sArr2, 8 * this.n, 8, this.n), 8, this.n), matrix_add(matrix_add(matrix_mul(sArrSample_matrix, 8, this.n, unpack(bArrCopyOfRange5, this.n, 8), this.n, 8), sample_matrix(sArr2, 16 * this.n, 8, 8), 8, 8), encode(bArrDecode), 8, 8)));
        this.digest.update(bArrCopyOfRange, 0, bArrCopyOfRange.length);
        this.digest.update(bArrCopyOfRange2, 0, bArrCopyOfRange2.length);
        this.digest.update(bArrCtselect, 0, bArrCtselect.length);
        this.digest.doFinal(bArr, 0, this.len_ss_bytes);
    }
}
