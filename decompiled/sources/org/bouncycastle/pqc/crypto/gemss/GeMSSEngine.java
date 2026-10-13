package org.bouncycastle.pqc.crypto.gemss;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSEngine.class */
class GeMSSEngine {
    private SecureRandom random;
    final int HFEn;
    final int HFEv;
    final int HFEDELTA;
    final int NB_ITE;
    final int HFEDeg;
    final int HFEDegI;
    final int HFEDegJ;
    final int HFEnv;
    final int HFEm;
    final int HFEnq;
    final int HFEnr;
    int HFE_odd_degree;
    int NB_WORD_GFqn;
    int NB_WORD_GF2nv;
    int NB_MONOMIAL_VINEGAR;
    int NB_MONOMIAL_PK;
    final int HFEnvq;
    final int HFEnvr;
    int LTRIANGULAR_NV_SIZE;
    final int LTRIANGULAR_N_SIZE;
    final int SIZE_SEED_SK;
    final int NB_WORD_MUL;
    int NB_WORD_MMUL;
    int MQv_GFqn_SIZE;
    final boolean ENABLED_REMOVE_ODD_DEGREE;
    final int MATRIXnv_SIZE;
    final int HFEmq;
    final int HFEmr;
    int NB_WORD_GF2m;
    final int HFEvq;
    final int HFEvr;
    final int NB_WORD_GFqv;
    final int HFEmq8;
    final int HFEmr8;
    final int NB_BYTES_GFqm;
    final int ACCESS_last_equations8;
    final int NB_BYTES_EQUATION;
    final int HFENr8;
    final int NB_WORD_UNCOMP_EQ;
    final int HFENr8c;
    final int LOST_BITS;
    final int NB_WORD_GF2nvm;
    final int SIZE_SIGN_UNCOMPRESSED;
    final int SIZE_DIGEST;
    final int SIZE_DIGEST_UINT;
    final int HFEnvr8;
    final int NB_BYTES_GFqnv;
    final int VAL_BITS_M;
    final long MASK_GF2m;
    int NB_COEFS_HFEPOLY;
    int NB_UINT_HFEVPOLY;
    final int MATRIXn_SIZE;
    final long MASK_GF2n;
    final int NB_BYTES_GFqn;
    private int buffer;
    final int SIZE_ROW;
    final int ShakeBitStrength;
    final int Sha3BitStrength;
    SHA3Digest sha3Digest;
    final int MLv_GFqn_SIZE;
    int II;
    int POW_II;
    int KP;
    int KX;
    int HFEn_1rightmost;
    int HFEn1h_rightmost;
    Mul_GF2x mul;
    Rem_GF2n rem;
    Pointer Buffer_NB_WORD_MUL;
    Pointer Buffer_NB_WORD_GFqn;
    final int NB_BITS_UINT = 64;
    final int LEN_UNROLLED_64 = 4;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSEngine$FunctionParams.class */
    enum FunctionParams {
        NV,
        V,
        N,
        M
    }

    public GeMSSEngine(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9;
        this.HFEn = i2;
        this.HFEv = i3;
        this.HFEDELTA = i4;
        this.NB_ITE = i5;
        this.HFEDeg = i6;
        this.HFEDegI = i7;
        this.HFEDegJ = i8;
        this.NB_BYTES_GFqn = (i2 >>> 3) + ((i2 & 7) != 0 ? 1 : 0);
        this.SIZE_ROW = i7 + 1;
        this.HFEnv = i2 + i3;
        this.HFEnq = i2 >>> 6;
        this.HFEnr = i2 & 63;
        this.HFEnvq = this.HFEnv >>> 6;
        this.HFEnvr = this.HFEnv & 63;
        this.SIZE_SEED_SK = i >>> 3;
        this.NB_WORD_MUL = (((i2 - 1) << 1) >>> 6) + 1;
        switch (this.NB_WORD_MUL) {
            case 6:
                this.mul = new Mul_GF2x.Mul6();
                break;
            case 9:
                this.mul = new Mul_GF2x.Mul9();
                break;
            case 12:
                this.mul = new Mul_GF2x.Mul12();
                break;
            case 13:
                this.mul = new Mul_GF2x.Mul13();
                break;
            case 17:
                this.mul = new Mul_GF2x.Mul17();
                break;
        }
        int i10 = i2 & 63;
        int i11 = 64 - i10;
        this.HFEm = i2 - i4;
        this.HFEmq = this.HFEm >>> 6;
        this.HFEmr = this.HFEm & 63;
        this.HFEvq = i3 >>> 6;
        this.HFEvr = i3 & 63;
        this.NB_WORD_GFqv = this.HFEvr != 0 ? this.HFEvq + 1 : this.HFEvq;
        this.HFEmq8 = this.HFEm >>> 3;
        this.HFEmr8 = this.HFEm & 7;
        this.NB_BYTES_GFqm = this.HFEmq8 + (this.HFEmr8 != 0 ? 1 : 0);
        this.NB_WORD_UNCOMP_EQ = (((this.HFEnvq * (this.HFEnvq + 1)) >>> 1) * 64) + ((this.HFEnvq + 1) * this.HFEnvr);
        this.HFEnvr8 = this.HFEnv & 7;
        this.NB_BYTES_GFqnv = (this.HFEnv >>> 3) + (this.HFEnvr8 != 0 ? 1 : 0);
        this.VAL_BITS_M = Math.min(i4 + i3, 8 - this.HFEmr8);
        this.MASK_GF2m = GeMSSUtils.maskUINT(this.HFEmr);
        this.MASK_GF2n = GeMSSUtils.maskUINT(this.HFEnr);
        this.NB_WORD_GFqn = this.HFEnq + (this.HFEnr != 0 ? 1 : 0);
        this.LTRIANGULAR_N_SIZE = (((this.HFEnq * (this.HFEnq + 1)) >>> 1) * 64) + (this.NB_WORD_GFqn * this.HFEnr);
        this.MATRIXn_SIZE = i2 * this.NB_WORD_GFqn;
        this.NB_WORD_GF2nv = this.HFEnvq + (this.HFEnvr != 0 ? 1 : 0);
        this.MATRIXnv_SIZE = this.HFEnv * this.NB_WORD_GF2nv;
        this.LTRIANGULAR_NV_SIZE = (((this.HFEnvq * (this.HFEnvq + 1)) >>> 1) * 64) + (this.NB_WORD_GF2nv * this.HFEnvr);
        this.NB_MONOMIAL_VINEGAR = ((i3 * (i3 + 1)) >>> 1) + 1;
        this.NB_MONOMIAL_PK = ((this.HFEnv * (this.HFEnv + 1)) >>> 1) + 1;
        this.MQv_GFqn_SIZE = this.NB_MONOMIAL_VINEGAR * this.NB_WORD_GFqn;
        this.ACCESS_last_equations8 = this.NB_MONOMIAL_PK * this.HFEmq8;
        this.NB_BYTES_EQUATION = (this.NB_MONOMIAL_PK + 7) >>> 3;
        this.HFENr8 = this.NB_MONOMIAL_PK & 7;
        this.HFENr8c = (8 - this.HFENr8) & 7;
        this.LOST_BITS = (this.HFEmr8 - 1) * this.HFENr8c;
        this.NB_WORD_MMUL = (((i2 - 1) << 1) >>> 6) + 1;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        switch (i2) {
            case 174:
                i9 = 13;
                break;
            case 175:
                i9 = 16;
                break;
            case 177:
                i9 = 8;
                break;
            case 178:
                i9 = 31;
                break;
            case 265:
                i9 = 42;
                break;
            case 266:
                i9 = 47;
                break;
            case 268:
                i9 = 25;
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                i9 = 53;
                break;
            case TIFF.TAG_MAKE /* 271 */:
                i9 = 58;
                break;
            case 354:
                i9 = 99;
                break;
            case 358:
                i9 = 57;
                break;
            case 364:
                i9 = 9;
                break;
            case 366:
                i9 = 29;
                break;
            case WinError.ERROR_PROCESS_MODE_ALREADY_BACKGROUND /* 402 */:
                i9 = 171;
                break;
            case 537:
                i9 = 10;
                i13 = 2;
                i12 = 1;
                break;
            case WinError.ERROR_INVALID_UNWIND_TARGET /* 544 */:
                i9 = 128;
                i13 = 3;
                i12 = 1;
                break;
            default:
                throw new IllegalArgumentException("error: need to add support for HFEn=" + i2);
        }
        if (i13 != 0) {
            i14 = 64 - i12;
            i15 = 64 - i13;
        }
        int i16 = 64 - (i9 & 63);
        if ((i6 & 1) == 0) {
            this.ENABLED_REMOVE_ODD_DEGREE = true;
            this.HFE_odd_degree = (1 << i7) + 1;
            if ((i6 & 1) != 0) {
                throw new IllegalArgumentException("HFEDeg is odd, so to remove the leading term would decrease the degree.");
            }
            if (this.HFE_odd_degree > i6) {
                throw new IllegalArgumentException("It is useless to remove 0 term.");
            }
            if (this.HFE_odd_degree <= 1) {
                throw new IllegalArgumentException("The case where the term X^3 is removing is not implemented.");
            }
            this.NB_COEFS_HFEPOLY = 2 + i8 + ((i7 * (i7 - 1)) >>> 1) + i7;
        } else {
            this.ENABLED_REMOVE_ODD_DEGREE = false;
            this.NB_COEFS_HFEPOLY = 2 + i8 + ((i7 * (i7 + 1)) >>> 1);
        }
        this.NB_WORD_GF2m = this.HFEmq + (this.HFEmr != 0 ? 1 : 0);
        this.NB_WORD_GF2nvm = (this.NB_WORD_GF2nv - this.NB_WORD_GF2m) + (this.HFEmr != 0 ? 1 : 0);
        this.SIZE_SIGN_UNCOMPRESSED = this.NB_WORD_GF2nv + ((i5 - 1) * this.NB_WORD_GF2nvm);
        if (i <= 128) {
            this.SIZE_DIGEST = 32;
            this.SIZE_DIGEST_UINT = 4;
            this.ShakeBitStrength = 128;
            this.Sha3BitStrength = 256;
        } else if (i <= 192) {
            this.SIZE_DIGEST = 48;
            this.SIZE_DIGEST_UINT = 6;
            this.ShakeBitStrength = 256;
            this.Sha3BitStrength = 384;
        } else {
            this.SIZE_DIGEST = 64;
            this.SIZE_DIGEST_UINT = 8;
            this.ShakeBitStrength = 256;
            this.Sha3BitStrength = 512;
        }
        this.sha3Digest = new SHA3Digest(this.Sha3BitStrength);
        this.NB_UINT_HFEVPOLY = (this.NB_COEFS_HFEPOLY + (this.NB_MONOMIAL_VINEGAR - 1) + ((i7 + 1) * i3)) * this.NB_WORD_GFqn;
        this.MLv_GFqn_SIZE = (i3 + 1) * this.NB_WORD_GFqn;
        if (i6 <= 34 || (i2 > 196 && i6 < 256)) {
            if (i6 == 17) {
                this.II = 4;
            } else {
                this.II = 6;
            }
            this.POW_II = 1 << this.II;
            this.KP = (i6 >>> this.II) + (i6 % this.POW_II != 0 ? 1 : 0);
            this.KX = i6 - this.KP;
        }
        if (i13 != 0) {
            if (i2 == 544 && i9 == 128) {
                this.rem = new Rem_GF2n.REM544_PENTANOMIAL_K3_IS_128_GF2X(i12, i13, i10, i11, i14, i15, this.MASK_GF2n);
            } else {
                this.rem = new Rem_GF2n.REM544_PENTANOMIAL_GF2X(i12, i13, i9, i10, i11, i14, i15, i16, this.MASK_GF2n);
            }
        } else if (i2 > 256 && i2 < 289 && i9 > 32 && i9 < 64) {
            this.rem = new Rem_GF2n.REM288_SPECIALIZED_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
        } else if (i2 != 354) {
            if (i2 != 358) {
                if (i2 != 402) {
                    switch (this.NB_WORD_MUL) {
                        case 6:
                            this.rem = new Rem_GF2n.REM192_SPECIALIZED_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
                            break;
                        case 9:
                            this.rem = new Rem_GF2n.REM288_SPECIALIZED_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
                            break;
                        case 12:
                            this.rem = new Rem_GF2n.REM384_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
                            break;
                    }
                } else {
                    this.rem = new Rem_GF2n.REM402_SPECIALIZED_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
                }
            } else {
                this.rem = new Rem_GF2n.REM384_SPECIALIZED358_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
            }
        } else {
            this.rem = new Rem_GF2n.REM384_SPECIALIZED_TRINOMIAL_GF2X(i9, i10, i11, i16, this.MASK_GF2n);
        }
        this.Buffer_NB_WORD_MUL = new Pointer(this.NB_WORD_MUL);
        this.Buffer_NB_WORD_GFqn = new Pointer(this.NB_WORD_GFqn);
        this.HFEn_1rightmost = 31;
        int i17 = i2 - 1;
        while ((i17 >>> this.HFEn_1rightmost) == 0) {
            this.HFEn_1rightmost--;
        }
        int i18 = (i2 + 1) >>> 1;
        this.HFEn1h_rightmost = 31;
        while ((i18 >>> this.HFEn1h_rightmost) == 0) {
            this.HFEn1h_rightmost--;
        }
        this.HFEn1h_rightmost--;
    }

    void genSecretMQS_gf2_opt(Pointer pointer, Pointer pointer2) {
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer((this.HFEDegI + 1) * (this.HFEv + 1) * this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer(pointer2, this.MQv_GFqn_SIZE);
        for (int i = 0; i <= this.HFEDegI; i++) {
            for (int i2 = 0; i2 <= this.HFEv; i2++) {
                pointer4.copyFrom(((i2 * (this.HFEDegI + 1)) + i) * this.NB_WORD_GFqn, pointer5, 0, this.NB_WORD_GFqn);
                pointer5.move(this.NB_WORD_GFqn);
            }
            pointer5.move(i * this.NB_WORD_GFqn);
        }
        Pointer pointer6 = new Pointer(this.SIZE_ROW * (this.HFEn - 1) * this.NB_WORD_GFqn);
        for (int i3 = 1; i3 < this.HFEn; i3++) {
            pointer6.set(i3 >>> 6, 1 << (i3 & 63));
            for (int i4 = 0; i4 < this.HFEDegI; i4++) {
                sqr_gf2n(pointer6, this.NB_WORD_GFqn, pointer6, 0);
                pointer6.move(this.NB_WORD_GFqn);
            }
            pointer6.move(this.NB_WORD_GFqn);
        }
        pointer6.indexReset();
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        pointer2.move(this.MQv_GFqn_SIZE);
        pointer.move(this.NB_WORD_GFqn);
        Pointer pointer7 = new Pointer(this.HFEDegI * this.HFEn * this.NB_WORD_GFqn);
        special_buffer(pointer7, pointer2, pointer6);
        Pointer pointer8 = new Pointer(pointer7);
        Pointer pointer9 = new Pointer(pointer7);
        pointer.copyFrom(pointer9, this.NB_WORD_GFqn);
        pointer9.move(this.NB_WORD_GFqn);
        pointer.setXorMatrix_NoMove(pointer9, this.NB_WORD_GFqn, this.HFEDegI - 1);
        pointer5.changeIndex(pointer4);
        pointer.setXorMatrix(pointer5, this.NB_WORD_GFqn, this.HFEDegI + 1);
        Pointer pointer10 = new Pointer(pointer6, this.NB_WORD_GFqn);
        int i5 = 1;
        while (i5 < this.HFEn) {
            dotProduct_gf2n(pointer, pointer10, pointer8, this.HFEDegI);
            pointer10.move(this.SIZE_ROW * this.NB_WORD_GFqn);
            pointer.setXorMatrix(pointer9, this.NB_WORD_GFqn, this.HFEDegI);
            i5++;
        }
        while (i5 < this.HFEnv) {
            pointer.copyFrom(pointer5, this.NB_WORD_GFqn);
            pointer5.move(this.NB_WORD_GFqn);
            pointer.setXorMatrix(pointer5, this.NB_WORD_GFqn, this.HFEDegI);
            i5++;
        }
        Pointer pointer11 = new Pointer(pointer6, this.NB_WORD_GFqn);
        Pointer pointer12 = new Pointer(this.NB_WORD_MUL);
        for (int i6 = 1; i6 < this.HFEn; i6++) {
            pointer8.move(this.HFEDegI * this.NB_WORD_GFqn);
            pointer10.changeIndex(pointer11);
            pointer9.changeIndex(pointer8);
            this.mul.mul_gf2x(this.Buffer_NB_WORD_MUL, pointer4, new Pointer(pointer10, -this.NB_WORD_GFqn));
            for (int i7 = 1; i7 <= this.HFEDegI; i7++) {
                pointer3.setRangeFromXor(0, pointer9, 0, pointer4, i7 * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
                mul_xorrange(this.Buffer_NB_WORD_MUL, pointer3, pointer10);
                pointer9.move(this.NB_WORD_GFqn);
                pointer10.move(this.NB_WORD_GFqn);
            }
            pointer10.move(this.NB_WORD_GFqn);
            rem_gf2n(pointer, 0, this.Buffer_NB_WORD_MUL);
            pointer.move(this.NB_WORD_GFqn);
            int i8 = i6 + 1;
            while (i8 < this.HFEn) {
                int index = pointer10.getIndex();
                int index2 = pointer8.getIndex();
                int index3 = pointer11.getIndex();
                int index4 = pointer9.getIndex();
                mul_move(pointer12, pointer10, pointer8);
                for_mul_xorrange_move(pointer12, pointer10, pointer8, this.HFEDegI - 1);
                for_mul_xorrange_move(pointer12, pointer11, pointer9, this.HFEDegI);
                rem_gf2n(pointer, 0, pointer12);
                pointer10.changeIndex(index + (this.SIZE_ROW * this.NB_WORD_GFqn));
                pointer8.changeIndex(index2);
                pointer11.changeIndex(index3);
                pointer9.changeIndex(index4 + (this.HFEDegI * this.NB_WORD_GFqn));
                pointer.move(this.NB_WORD_GFqn);
                i8++;
            }
            pointer5.changeIndex(pointer4);
            pointer11.move(-this.NB_WORD_GFqn);
            while (i8 < this.HFEnv) {
                pointer5.move((this.HFEDegI + 1) * this.NB_WORD_GFqn);
                dotProduct_gf2n(pointer, pointer11, pointer5, this.HFEDegI + 1);
                pointer.move(this.NB_WORD_GFqn);
                i8++;
            }
            pointer11.move(this.NB_WORD_GFqn + (this.SIZE_ROW * this.NB_WORD_GFqn));
        }
        pointer2.move(this.NB_WORD_GFqn - this.MQv_GFqn_SIZE);
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn * (this.NB_MONOMIAL_VINEGAR - 1));
        pointer.indexReset();
        pointer2.indexReset();
    }

    private void special_buffer(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        int index = pointer2.getIndex();
        pointer2.move((this.NB_WORD_GFqn * (this.HFEv + 1)) << 1);
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        pointer.move(this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer(pointer2, this.NB_WORD_GFqn * (this.HFEv + 2));
        int i = 2;
        while (i < this.SIZE_ROW - 1) {
            copy_move_matrix_move(pointer, pointer4, i - 1);
            i++;
        }
        if (this.ENABLED_REMOVE_ODD_DEGREE) {
            while (i < this.SIZE_ROW - 1) {
                copy_move_matrix_move(pointer, pointer4, i - 2);
                i++;
            }
        }
        pointer.set1_gf2n(0, this.NB_WORD_GFqn);
        pointer.setXorMatrix(pointer4, this.NB_WORD_GFqn, this.HFEDegJ);
        for (int i2 = 0; i2 < this.HFEn - 1; i2++) {
            mul_gf2n(pointer, pointer3, pointer2);
            pointer.move(this.NB_WORD_GFqn);
            pointer4.changeIndex(pointer2, this.NB_WORD_GFqn * (this.HFEv + 2));
            int i3 = 2;
            while (i3 < this.HFEDegI) {
                dotproduct_move_move(pointer, pointer4, pointer3, i3);
                i3++;
            }
            if (this.ENABLED_REMOVE_ODD_DEGREE) {
                pointer3.move(this.NB_WORD_GFqn);
                while (i3 < this.SIZE_ROW - 1) {
                    dotproduct_move_move(pointer, pointer4, pointer3, i3 - 1);
                    i3++;
                }
                pointer3.move(-this.NB_WORD_GFqn);
            }
            if (this.HFEDegJ == 0) {
                pointer.copyFrom(pointer3, this.NB_WORD_GFqn);
                pointer.move(this.NB_WORD_GFqn);
                pointer3.move(this.SIZE_ROW * this.NB_WORD_GFqn);
            } else {
                dotProduct_gf2n(pointer, pointer3, pointer4, this.HFEDegJ);
                pointer3.move(this.HFEDegJ * this.NB_WORD_GFqn);
                pointer.setXorRange_SelfMove(pointer3, this.NB_WORD_GFqn);
                pointer3.move((this.SIZE_ROW - this.HFEDegJ) * this.NB_WORD_GFqn);
            }
        }
        pointer.indexReset();
        pointer2.changeIndex(index);
        pointer3.indexReset();
    }

    private void copy_move_matrix_move(Pointer pointer, Pointer pointer2, int i) {
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        pointer2.move(this.NB_WORD_GFqn);
        pointer.setXorMatrix(pointer2, this.NB_WORD_GFqn, i);
        pointer2.move(this.NB_WORD_GFqn * (this.HFEv + 1));
    }

    private void dotproduct_move_move(Pointer pointer, Pointer pointer2, Pointer pointer3, int i) {
        dotProduct_gf2n(pointer, pointer3, pointer2, i);
        pointer.move(this.NB_WORD_GFqn);
        pointer2.move((i + this.HFEv + 1) * this.NB_WORD_GFqn);
    }

    private void dotProduct_gf2n(Pointer pointer, Pointer pointer2, Pointer pointer3, int i) {
        Pointer pointer4 = new Pointer(this.NB_WORD_MUL);
        int index = pointer2.getIndex();
        int index2 = pointer3.getIndex();
        mul_move(pointer4, pointer2, pointer3);
        for_mul_xorrange_move(pointer4, pointer2, pointer3, i - 1);
        rem_gf2n(pointer, 0, pointer4);
        pointer2.changeIndex(index);
        pointer3.changeIndex(index2);
    }

    void mul_gf2n(Pointer pointer, Pointer pointer2, int i, Pointer pointer3) {
        int index = pointer2.getIndex();
        pointer2.move(i);
        this.mul.mul_gf2x(this.Buffer_NB_WORD_MUL, pointer2, pointer3);
        pointer2.changeIndex(index);
        rem_gf2n(pointer, 0, this.Buffer_NB_WORD_MUL);
    }

    void mul_gf2n(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        this.mul.mul_gf2x(this.Buffer_NB_WORD_MUL, pointer2, pointer3);
        rem_gf2n(pointer, 0, this.Buffer_NB_WORD_MUL);
    }

    void for_mul_xorrange_move(Pointer pointer, Pointer pointer2, Pointer pointer3, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            this.mul.mul_gf2x_xor(pointer, pointer2, pointer3);
            pointer2.move(this.NB_WORD_GFqn);
            pointer3.move(this.NB_WORD_GFqn);
        }
    }

    void mul_move(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        this.mul.mul_gf2x(pointer, pointer2, pointer3);
        pointer2.move(this.NB_WORD_GFqn);
        pointer3.move(this.NB_WORD_GFqn);
    }

    public void mul_xorrange(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        this.mul.mul_gf2x_xor(pointer, pointer2, pointer3);
    }

    public void mul_rem_xorrange(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        this.mul.mul_gf2x(this.Buffer_NB_WORD_MUL, pointer2, pointer3);
        this.rem.rem_gf2n_xor(pointer.array, pointer.cp, this.Buffer_NB_WORD_MUL.array);
    }

    public void mul_rem_xorrange(Pointer pointer, Pointer pointer2, Pointer pointer3, int i) {
        int index = pointer3.getIndex();
        pointer3.move(i);
        this.mul.mul_gf2x(this.Buffer_NB_WORD_MUL, pointer2, pointer3);
        this.rem.rem_gf2n_xor(pointer.array, pointer.cp, this.Buffer_NB_WORD_MUL.array);
        pointer3.changeIndex(index);
    }

    private void rem_gf2n(Pointer pointer, int i, Pointer pointer2) {
        this.rem.rem_gf2n(pointer.array, i + pointer.getIndex(), pointer2.array);
    }

    private void sqr_gf2n(Pointer pointer, int i, Pointer pointer2, int i2) {
        this.mul.sqr_gf2x(this.Buffer_NB_WORD_MUL.array, pointer2.array, i2 + pointer2.cp);
        rem_gf2n(pointer, i, this.Buffer_NB_WORD_MUL);
    }

    private void sqr_gf2n(Pointer pointer, Pointer pointer2) {
        this.mul.sqr_gf2x(this.Buffer_NB_WORD_MUL.array, pointer2.array, pointer2.cp);
        this.rem.rem_gf2n(pointer.array, pointer.cp, this.Buffer_NB_WORD_MUL.array);
    }

    void cleanLowerMatrix(Pointer pointer, FunctionParams functionParams) {
        int i;
        int i2;
        switch (functionParams.ordinal()) {
            case 0:
                i = this.HFEnvq;
                i2 = this.HFEnvr;
                break;
            case 2:
                i = this.HFEnq;
                i2 = this.HFEnr;
                break;
            default:
                throw new IllegalArgumentException("");
        }
        Pointer pointer2 = new Pointer(pointer);
        int i3 = 1;
        while (i3 <= i) {
            for_and_xor_shift_incre_move(pointer2, i3, 64);
            pointer2.moveIncremental();
            i3++;
        }
        for_and_xor_shift_incre_move(pointer2, i3, i2);
    }

    private void for_and_xor_shift_incre_move(Pointer pointer, int i, int i2) {
        long j = 0;
        for (int i3 = 0; i3 < i2; i3++) {
            pointer.setAnd(j);
            pointer.setXor(1 << i3);
            j = (j << 1) + 1;
            pointer.move(i);
        }
    }

    void invMatrixLU_gf2(Pointer pointer, Pointer pointer2, Pointer pointer3, FunctionParams functionParams) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        Pointer pointer4 = new Pointer(pointer2);
        Pointer pointer5 = new Pointer(pointer2);
        Pointer pointer6 = new Pointer(pointer3);
        switch (functionParams.ordinal()) {
            case 0:
                i = this.HFEnvq;
                i2 = this.HFEnv - 1;
                i3 = this.NB_WORD_GF2nv;
                i4 = this.HFEnvr;
                i5 = this.LTRIANGULAR_NV_SIZE;
                break;
            case 2:
                pointer.setRangeClear(0, this.MATRIXn_SIZE);
                i = this.HFEnq;
                i2 = this.HFEn - 1;
                i3 = this.NB_WORD_GFqn;
                i4 = this.HFEnr;
                i5 = this.LTRIANGULAR_N_SIZE;
                break;
            default:
                throw new IllegalArgumentException("Invalid Input");
        }
        Pointer pointer7 = new Pointer(pointer);
        Pointer pointer8 = new Pointer(pointer);
        int iLoop_xor_loop_move_xorandmask_move = 0;
        int i6 = 0;
        while (i6 < i) {
            iLoop_xor_loop_move_xorandmask_move = loop_xor_loop_move_xorandmask_move(pointer7, pointer8, pointer4, pointer5, iLoop_xor_loop_move_xorandmask_move, i6, 64, i2, i3);
            pointer5.moveIncremental();
            i6++;
        }
        if (i4 > 1) {
            loop_xor_loop_move_xorandmask_move(pointer7, pointer8, pointer4, pointer5, iLoop_xor_loop_move_xorandmask_move, i6, i4 - 1, i2, i3);
            pointer7.setXor(i6, 1 << (i4 - 1));
            pointer7.move(i3);
        } else if (i4 == 1) {
            pointer7.set(i6, 1L);
            pointer7.move(i3);
        }
        pointer6.move(i5);
        for (int i7 = i2; i7 > 0; i7--) {
            pointer6.move((-1) - (i7 >>> 6));
            pointer7.move(-i3);
            pointer8.changeIndex(pointer);
            for (int i8 = 0; i8 < i7; i8++) {
                pointer8.setXorRangeAndMask(pointer7, i3, -((pointer6.get(i8 >>> 6) >>> (i8 & 63)) & 1));
                pointer8.move(i3);
            }
        }
    }

    private int loop_xor_loop_move_xorandmask_move(Pointer pointer, Pointer pointer2, Pointer pointer3, Pointer pointer4, int i, int i2, int i3, int i4, int i5) {
        int i6 = 0;
        while (i6 < i3) {
            pointer.setXor(i2, 1 << i6);
            pointer2.changeIndex(pointer);
            pointer3.changeIndex(pointer4);
            for (int i7 = i; i7 < i4; i7++) {
                pointer2.move(i5);
                pointer3.move((i7 >>> 6) + 1);
                pointer2.setXorRangeAndMask(pointer, i2 + 1, -((pointer3.get() >>> i6) & 1));
            }
            pointer.move(i5);
            pointer4.move(i2 + 1);
            i6++;
            i++;
        }
        return i;
    }

    void vecMatProduct(Pointer pointer, Pointer pointer2, Pointer pointer3, FunctionParams functionParams) {
        int i;
        int i2;
        int i3;
        long j;
        int i4;
        int i5 = 0;
        Pointer pointer4 = new Pointer(pointer3);
        switch (functionParams) {
            case NV:
                pointer.setRangeClear(0, this.NB_WORD_GF2nv);
                i = this.HFEnvq;
                i2 = this.NB_WORD_GF2nv;
                i3 = this.NB_WORD_GF2nv;
                break;
            case V:
                pointer.setRangeClear(0, this.NB_WORD_GFqn);
                i2 = this.NB_WORD_GFqn;
                i3 = this.NB_WORD_GFqn;
                i = this.HFEvq;
                break;
            case N:
                pointer.setRangeClear(0, this.NB_WORD_GFqn);
                i2 = this.NB_WORD_GFqn;
                i3 = this.NB_WORD_GFqn;
                i = this.HFEnq;
                break;
            case M:
                pointer.setRangeClear(0, this.NB_WORD_GF2m);
                i = this.HFEnq;
                i2 = this.NB_WORD_GF2m;
                i3 = this.NB_WORD_GFqn;
                break;
            default:
                throw new IllegalArgumentException("Invalid input for vecMatProduct");
        }
        for (int i6 = 0; i6 < i; i6++) {
            long j2 = pointer2.get(i6);
            while (i5 < 64) {
                pointer.setXorRangeAndMask(pointer4, i2, -(j2 & 1));
                pointer4.move(i3);
                j2 >>>= 1;
                i5++;
            }
            i5 = 0;
        }
        switch (functionParams) {
            case NV:
                if (this.HFEnvr == 0) {
                    return;
                }
                j = pointer2.get(this.HFEnvq);
                i4 = this.HFEnvr;
                break;
            case V:
                if (this.HFEvr == 0) {
                    return;
                }
                j = pointer2.get(this.HFEvq);
                i4 = this.HFEvr;
                break;
            case N:
            case M:
                j = pointer2.get(this.HFEnq);
                i4 = this.HFEnr;
                break;
            default:
                throw new IllegalArgumentException("Invalid input for vecMatProduct");
        }
        while (i5 < i4) {
            pointer.setXorRangeAndMask(pointer4, i2, -(j & 1));
            pointer4.move(i3);
            j >>>= 1;
            i5++;
        }
        if (functionParams != FunctionParams.M || this.HFEmr == 0) {
            return;
        }
        pointer.setAnd(this.NB_WORD_GF2m - 1, this.MASK_GF2m);
    }

    private long convMQ_uncompressL_gf2(Pointer pointer, PointerUnion pointerUnion) {
        PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
        int iFor_setpk2_end_move_plus = for_setpk2_end_move_plus(pointer, pointerUnion2, this.HFEnvq);
        if (this.HFEnvr != 0) {
            setPk2Value(pointer, pointerUnion2, iFor_setpk2_end_move_plus, this.HFEnvq, this.HFEnvr + 1);
        }
        return pointerUnion.get() & 1;
    }

    private int setPk2Value(Pointer pointer, PointerUnion pointerUnion, int i, int i2, int i3) {
        for (int i4 = 1; i4 < i3; i4++) {
            if ((i & 63) != 0) {
                pointer.setRangePointerUnion(pointerUnion, i2, i & 63);
                pointer.set(i2, pointerUnion.get(i2) >>> (i & 63));
                if ((i & 63) + i4 > 64) {
                    pointer.setXor(i2, pointerUnion.get(i2 + 1) << (64 - (i & 63)));
                }
                if ((i & 63) + i4 >= 64) {
                    pointerUnion.moveIncremental();
                }
            } else {
                pointer.setRangePointerUnion(pointerUnion, i2 + 1);
            }
            pointerUnion.move(i2);
            pointer.setAnd(i2, (1 << i4) - 1);
            pointer.move(i2 + 1);
            i += (i2 << 6) + i4;
        }
        return i;
    }

    private void setPk2_endValue(Pointer pointer, PointerUnion pointerUnion, int i, int i2) {
        if ((i & 63) != 0) {
            pointer.setRangePointerUnion(pointerUnion, i2 + 1, i & 63);
        } else {
            pointer.setRangePointerUnion(pointerUnion, i2 + 1);
        }
    }

    private long convMQ_last_uncompressL_gf2(Pointer pointer, PointerUnion pointerUnion) {
        PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
        int i = this.HFEnv - 1;
        int i2 = i >>> 6;
        int i3 = i & 63;
        int iFor_setpk2_end_move_plus = for_setpk2_end_move_plus(pointer, pointerUnion2, i2);
        if (i3 != 0) {
            iFor_setpk2_end_move_plus = setPk2Value(pointer, pointerUnion2, iFor_setpk2_end_move_plus, i2, i3 + 1);
        }
        int i4 = this.HFEnv - this.LOST_BITS;
        int i5 = i4 >>> 6;
        int i6 = i4 & 63;
        if (i6 != 0) {
            if ((iFor_setpk2_end_move_plus & 63) != 0) {
                if (((((this.NB_MONOMIAL_PK - this.LOST_BITS) + 7) >>> 3) & 7) != 0) {
                    int i7 = (this.HFEnv - ((64 - (((this.NB_MONOMIAL_PK - this.LOST_BITS) - this.HFEnvr) & 63)) & 63)) >>> 6;
                    pointer.setRangePointerUnion_Check(pointerUnion2, i7, iFor_setpk2_end_move_plus);
                    pointer.set(i7, pointerUnion2.getWithCheck(i7) >>> (iFor_setpk2_end_move_plus & 63));
                    if (i7 < i5) {
                        long withCheck = pointerUnion2.getWithCheck(i7 + 1);
                        pointer.setXor(i7, withCheck << (64 - (iFor_setpk2_end_move_plus & 63)));
                        pointer.set(i7 + 1, withCheck >>> (iFor_setpk2_end_move_plus & 63));
                    } else if ((iFor_setpk2_end_move_plus & 63) + i6 > 64) {
                        pointer.setXor(i7, pointerUnion2.getWithCheck(i7 + 1) << (64 - (iFor_setpk2_end_move_plus & 63)));
                    }
                } else {
                    pointer.setRangePointerUnion(pointerUnion2, i5, iFor_setpk2_end_move_plus & 63);
                    pointer.set(i5, pointerUnion2.get(i5) >>> (iFor_setpk2_end_move_plus & 63));
                    if ((iFor_setpk2_end_move_plus & 63) + i6 > 64) {
                        pointer.setXor(i5, pointerUnion2.get(i5 + 1) << (64 - (iFor_setpk2_end_move_plus & 63)));
                    }
                }
            } else if (((((this.NB_MONOMIAL_PK - this.LOST_BITS) + 7) >>> 3) & 7) != 0) {
                pointer.setRangePointerUnion(pointerUnion2, i5);
                pointer.set(i5, pointerUnion2.getWithCheck(i5));
            } else {
                pointer.setRangePointerUnion(pointerUnion2, i5 + 1);
            }
        } else if (i5 != 0) {
            if ((iFor_setpk2_end_move_plus & 63) == 0) {
                pointer.setRangePointerUnion(pointerUnion2, i5);
            } else if (((((this.NB_MONOMIAL_PK - this.LOST_BITS) + 7) >>> 3) & 7) != 0) {
                pointer.setRangePointerUnion(pointerUnion2, i5 - 1, iFor_setpk2_end_move_plus & 63);
                int i8 = i5 - 1;
                pointer.set(i8, pointerUnion2.get(i8) >>> (iFor_setpk2_end_move_plus & 63));
                pointer.setXor(i8, pointerUnion2.getWithCheck(i8 + 1) << (64 - (iFor_setpk2_end_move_plus & 63)));
            } else {
                pointer.setRangePointerUnion(pointerUnion2, i5, iFor_setpk2_end_move_plus & 63);
            }
        }
        return pointerUnion.get() & 1;
    }

    private int for_setpk2_end_move_plus(Pointer pointer, PointerUnion pointerUnion, int i) {
        int i2 = 1;
        for (int i3 = 0; i3 < i; i3++) {
            int pk2Value = setPk2Value(pointer, pointerUnion, i2, i3, 64);
            setPk2_endValue(pointer, pointerUnion, pk2Value, i3);
            pointerUnion.move(i3 + 1);
            pointer.move(i3 + 1);
            i2 = pk2Value + ((i3 + 1) << 6);
        }
        return i2;
    }

    public int sign_openHFE_huncomp_pk(byte[] bArr, int i, byte[] bArr2, PointerUnion pointerUnion, PointerUnion pointerUnion2) {
        Pointer pointer = new Pointer(this.SIZE_SIGN_UNCOMPRESSED);
        Pointer pointer2 = new Pointer(this.NB_WORD_GF2nv);
        Pointer pointer3 = new Pointer(this.NB_WORD_GF2nv);
        Pointer pointer4 = new Pointer(pointer2);
        Pointer pointer5 = new Pointer(pointer3);
        byte[] bArr3 = new byte[64];
        Pointer pointer6 = new Pointer(this.NB_ITE * this.SIZE_DIGEST_UINT);
        long j = pointerUnion2.get();
        pointerUnion2.move(1);
        uncompress_signHFE(pointer, bArr2);
        getSHA3Hash(pointer6, 0, 64, bArr, 0, i, bArr3);
        int i2 = 1;
        while (i2 < this.NB_ITE) {
            getSHA3Hash(pointer6, i2 * this.SIZE_DIGEST_UINT, 64, bArr3, 0, this.SIZE_DIGEST, bArr3);
            pointer6.setAnd(((this.SIZE_DIGEST_UINT * (i2 - 1)) + this.NB_WORD_GF2m) - 1, this.MASK_GF2m);
            i2++;
        }
        pointer6.setAnd(((this.SIZE_DIGEST_UINT * (i2 - 1)) + this.NB_WORD_GF2m) - 1, this.MASK_GF2m);
        evalMQShybrid8_uncomp_nocst_gf2_m(pointer4, pointer, pointerUnion, pointerUnion2);
        pointer4.setXor(this.HFEmq, j);
        for (int i3 = this.NB_ITE - 1; i3 > 0; i3--) {
            pointer4.setXorRange(pointer6, i3 * this.SIZE_DIGEST_UINT, this.NB_WORD_GF2m);
            int i4 = this.NB_WORD_GF2nv + (((this.NB_ITE - 1) - i3) * this.NB_WORD_GF2nvm);
            pointer4.setAnd(this.NB_WORD_GF2m - 1, this.MASK_GF2m);
            pointer4.setXor(this.NB_WORD_GF2m - 1, pointer.get(i4));
            if (this.NB_WORD_GF2nvm != 1) {
                pointer4.copyFrom(this.NB_WORD_GF2m, pointer, i4 + 1, this.NB_WORD_GF2nvm - 1);
            }
            evalMQShybrid8_uncomp_nocst_gf2_m(pointer5, pointer4, pointerUnion, pointerUnion2);
            pointer5.setXor(this.HFEmq, j);
            pointer5.swap(pointer4);
        }
        return pointer4.isEqual_nocst_gf2(pointer6, this.NB_WORD_GF2m);
    }

    private void getSHA3Hash(Pointer pointer, int i, int i2, byte[] bArr, int i3, int i4, byte[] bArr2) {
        this.sha3Digest.update(bArr, i3, i4);
        this.sha3Digest.doFinal(bArr2, 0);
        pointer.fill(i, bArr2, 0, i2);
    }

    private void evalMQShybrid8_uncomp_nocst_gf2_m(Pointer pointer, Pointer pointer2, PointerUnion pointerUnion, PointerUnion pointerUnion2) {
        PointerUnion pointerUnion3 = new PointerUnion(pointerUnion2);
        evalMQSnocst8_quo_gf2(pointer, pointer2, pointerUnion);
        if (this.HFEmr < 8) {
            pointer.set(this.HFEmq, 0L);
        }
        for (int i = this.HFEmr - this.HFEmr8; i < this.HFEmr; i++) {
            pointer.setXor(this.HFEmq, evalMQnocst_unrolled_no_simd_gf2(pointer2, pointerUnion3) << i);
            pointerUnion3.move(this.NB_WORD_UNCOMP_EQ);
        }
    }

    private void uncompress_signHFE(Pointer pointer, byte[] bArr) {
        PointerUnion pointerUnion = new PointerUnion(pointer);
        int i = (1 << this.HFEnvr8) - 1;
        pointerUnion.fillBytes(0, bArr, 0, this.NB_BYTES_GFqnv);
        if (this.HFEnvr8 != 0) {
            pointerUnion.setAndByte(this.NB_BYTES_GFqnv - 1, i);
        }
        int i2 = this.HFEnv;
        pointerUnion.moveNextBytes((this.NB_WORD_GF2nv << 3) + (this.HFEmq8 & 7));
        for (int i3 = 1; i3 < this.NB_ITE; i3++) {
            int iMin = Math.min(this.HFEDELTA + this.HFEv, (8 - (i2 & 7)) & 7);
            if ((i2 & 7) != 0) {
                pointerUnion.setXorByte(((bArr[i2 >>> 3] & 255) >>> (i2 & 7)) << this.HFEmr8);
                int i4 = iMin - this.VAL_BITS_M;
                if (i4 >= 0) {
                    pointerUnion.moveNextByte();
                }
                if (i4 > 0) {
                    int i5 = i2 + this.VAL_BITS_M;
                    pointerUnion.setXorByte((bArr[i5 >>> 3] & 255) >>> (i5 & 7));
                    i2 = i5 + i4;
                } else {
                    i2 += iMin;
                }
            }
            int i6 = (this.HFEDELTA + this.HFEv) - iMin;
            int i7 = (this.HFEm + iMin) & 7;
            if (i7 != 0) {
                for (int i8 = 0; i8 < ((i6 - 1) >>> 3); i8++) {
                    pointerUnion.setXorByte((bArr[i2 >>> 3] & 255) << i7);
                    pointerUnion.moveNextByte();
                    pointerUnion.setXorByte((bArr[i2 >>> 3] & 255) >>> (8 - i7));
                    i2 += 8;
                }
                pointerUnion.setXorByte((bArr[i2 >>> 3] & 255) << i7);
                pointerUnion.moveNextByte();
                int i9 = ((i6 + 7) & 7) + 1;
                if (i9 > 8 - i7) {
                    pointerUnion.setByte((bArr[i2 >>> 3] & 255) >>> (8 - i7));
                    pointerUnion.moveNextByte();
                }
                i2 += i9;
            } else {
                for (int i10 = 0; i10 < ((i6 + 7) >>> 3); i10++) {
                    pointerUnion.setByte(bArr[i2 >>> 3]);
                    i2 += 8;
                    pointerUnion.moveNextByte();
                }
                i2 -= (8 - (i6 & 7)) & 7;
            }
            if (this.HFEnvr8 != 0) {
                pointerUnion.setAndByte(-1, i);
            }
            pointerUnion.moveNextBytes(((8 - (this.NB_BYTES_GFqnv & 7)) & 7) + (this.HFEmq8 & 7));
        }
    }

    private void evalMQSnocst8_quo_gf2(Pointer pointer, Pointer pointer2, PointerUnion pointerUnion) {
        int i = this.HFEnv;
        int i2 = (this.HFEm >>> 3) != 0 ? (this.HFEm >>> 3) << 3 : this.HFEm;
        int i3 = (i2 & 7) != 0 ? (i2 >>> 3) + 1 : i2 >>> 3;
        int i4 = (i3 >>> 3) + ((i3 & 7) != 0 ? 1 : 0);
        PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
        System.arraycopy(pointerUnion2.getArray(), 0, pointer.getArray(), pointer.getIndex(), i4);
        pointerUnion2.moveNextBytes(i3);
        for (int i5 = 0; i5 < this.HFEnvq; i5++) {
            long j = pointer2.get(i5);
            int i6 = 0;
            while (i6 < 64) {
                if ((j & 1) != 0) {
                    pointer.setXorRange(0, pointerUnion2, 0, i4);
                    pointerUnion2.moveNextBytes(i3);
                    LOOPJR_UNROLLED_64(pointer, pointerUnion2, i6 + 1, 64, j >>> 1, i3, i4);
                    for (int i7 = i5 + 1; i7 < this.HFEnvq; i7++) {
                        LOOPJR_UNROLLED_64(pointer, pointerUnion2, 0, 64, pointer2.get(i7), i3, i4);
                    }
                    if (this.HFEnvr != 0) {
                        choose_LOOPJR(pointer, pointerUnion2, 0, pointer2.get(this.HFEnvq), i3, i4);
                    }
                } else {
                    pointerUnion2.moveNextBytes(i * i3);
                }
                j >>>= 1;
                i6++;
                i--;
            }
        }
        if (this.HFEnvr != 0) {
            long j2 = pointer2.get(this.HFEnvq);
            int i8 = 0;
            while (i8 < this.HFEnvr) {
                if ((j2 & 1) != 0) {
                    pointer.setXorRange(0, pointerUnion2, 0, i4);
                    pointerUnion2.moveNextBytes(i3);
                    choose_LOOPJR(pointer, pointerUnion2, i8 + 1, j2 >>> 1, i3, i4);
                } else {
                    pointerUnion2.moveNextBytes(i * i3);
                }
                j2 >>>= 1;
                i8++;
                i--;
            }
        }
        if ((i2 & 63) != 0) {
            pointer.setAnd(i4 - 1, (1 << (i2 & 63)) - 1);
        }
    }

    private void choose_LOOPJR(Pointer pointer, PointerUnion pointerUnion, int i, long j, int i2, int i3) {
        if (this.HFEnvr < 8) {
            LOOPJR_NOCST_64(pointer, pointerUnion, i, this.HFEnvr, j, i2, i3);
        } else {
            LOOPJR_UNROLLED_64(pointer, pointerUnion, i, this.HFEnvr, j, i2, i3);
        }
    }

    private void LOOPJR_UNROLLED_64(Pointer pointer, PointerUnion pointerUnion, int i, int i2, long j, int i3, int i4) {
        int i5 = i;
        while (i5 < (i2 - 4) + 1) {
            j = LOOPJR_NOCST_64(pointer, pointerUnion, 0, 4, j, i3, i4);
            i5 += 4;
        }
        LOOPJR_NOCST_64(pointer, pointerUnion, i5, i2, j, i3, i4);
    }

    private long LOOPJR_NOCST_64(Pointer pointer, PointerUnion pointerUnion, int i, int i2, long j, int i3, int i4) {
        for (int i5 = i; i5 < i2; i5++) {
            if ((j & 1) != 0) {
                pointer.setXorRange(0, pointerUnion, 0, i4);
            }
            pointerUnion.moveNextBytes(i3);
            j >>>= 1;
        }
        return j;
    }

    private long evalMQnocst_unrolled_no_simd_gf2(Pointer pointer, PointerUnion pointerUnion) {
        long dotProduct = 0;
        PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
        long j = pointer.get();
        for (int i = 0; i < 64; i++) {
            if (((j >>> i) & 1) != 0) {
                dotProduct ^= pointerUnion2.get(i) & j;
            }
        }
        pointerUnion2.move(64);
        for (int i2 = 1; i2 < this.NB_WORD_GF2nv; i2++) {
            int i3 = (this.NB_WORD_GF2nv != i2 + 1 || this.HFEnvr == 0) ? 64 : this.HFEnvr;
            long j2 = pointer.get(i2);
            for (int i4 = 0; i4 < i3; i4++) {
                if (((j2 >>> i4) & 1) != 0) {
                    dotProduct ^= pointerUnion2.getDotProduct(0, pointer, 0, i2 + 1);
                }
                pointerUnion2.move(i2 + 1);
            }
        }
        return GeMSSUtils.XORBITS_UINT(dotProduct);
    }

    public void signHFE_FeistelPatarin(SecureRandom secureRandom, byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        this.random = secureRandom;
        Pointer pointer = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer2 = new Pointer(this.SIZE_DIGEST_UINT);
        Pointer pointer3 = new Pointer(new Pointer(this.SIZE_DIGEST_UINT));
        int i3 = this.HFEv & 7;
        int i4 = (this.HFEv >>> 3) + (i3 != 0 ? 1 : 0);
        long jMaskUINT = GeMSSUtils.maskUINT(this.HFEvr);
        long j = 0;
        SecretKeyHFE secretKeyHFE = new SecretKeyHFE(this);
        Pointer pointer4 = new Pointer(this.NB_WORD_GFqv);
        Pointer[] pointerArr = new Pointer[this.HFEDegI + 1];
        precSignHFE(secretKeyHFE, pointerArr, bArr3);
        Pointer pointer5 = new Pointer(secretKeyHFE.F_struct.poly);
        Pointer pointer6 = new Pointer(pointer2);
        byte[] bArr4 = new byte[this.Sha3BitStrength >>> 3];
        getSHA3Hash(pointer6, 0, bArr4.length, bArr2, i, i2, bArr4);
        Pointer pointer7 = new Pointer(this.SIZE_SIGN_UNCOMPRESSED);
        Pointer pointer8 = new Pointer(this.NB_WORD_GF2nv);
        PointerUnion pointerUnion = new PointerUnion(pointer8);
        for (int i5 = 1; i5 <= this.NB_ITE; i5++) {
            pointer8.setRangeFromXor(pointer7, pointer6, this.NB_WORD_GF2m);
            if (this.HFEmr8 != 0) {
                pointer8.setAnd(this.NB_WORD_GF2m - 1, this.MASK_GF2m);
                j = pointerUnion.getByte(this.HFEmq8);
            }
            do {
                if (this.HFEmr8 != 0) {
                    pointerUnion.fillRandomBytes(this.HFEmq8, secureRandom, (this.NB_BYTES_GFqn - this.NB_BYTES_GFqm) + 1);
                    pointerUnion.setAndThenXorByte(this.HFEmq8, -(1 << this.HFEmr8), j);
                } else {
                    pointerUnion.fillRandomBytes(this.NB_BYTES_GFqm, secureRandom, this.NB_BYTES_GFqn - this.NB_BYTES_GFqm);
                }
                if ((this.HFEn & 7) != 0) {
                    pointer8.setAnd(this.NB_WORD_GFqn - 1, this.MASK_GF2n);
                }
                vecMatProduct(pointer, pointer8, secretKeyHFE.T, FunctionParams.N);
                pointer4.fillRandom(0, secureRandom, i4);
                if (i3 != 0) {
                    pointer4.setAnd(this.NB_WORD_GFqv - 1, jMaskUINT);
                }
                evalMQSv_unrolled_gf2(pointer5, pointer4, secretKeyHFE.F_HFEv);
                for (int i6 = 0; i6 <= this.HFEDegI; i6++) {
                    vecMatProduct(this.Buffer_NB_WORD_GFqn, pointer4, new Pointer(pointerArr[i6], this.NB_WORD_GFqn), FunctionParams.V);
                    pointer5.setRangeFromXor(this.NB_WORD_GFqn * (((i6 * (i6 + 1)) >>> 1) + 1), pointerArr[i6], 0, this.Buffer_NB_WORD_GFqn, 0, this.NB_WORD_GFqn);
                }
            } while (chooseRootHFE_gf2nx(pointer8, secretKeyHFE.F_struct, pointer) == 0);
            pointer8.setXor(this.NB_WORD_GFqn - 1, pointer4.get() << this.HFEnr);
            pointer8.setRangeRotate(this.NB_WORD_GFqn, pointer4, 0, this.NB_WORD_GFqv - 1, 64 - this.HFEnr);
            if (this.NB_WORD_GFqn + this.NB_WORD_GFqv == this.NB_WORD_GF2nv) {
                pointer8.set((this.NB_WORD_GFqn + this.NB_WORD_GFqv) - 1, pointer4.get(this.NB_WORD_GFqv - 1) >>> (64 - this.HFEnr));
            }
            vecMatProduct(pointer7, pointer8, secretKeyHFE.S, FunctionParams.NV);
            if (i5 != this.NB_ITE) {
                int i7 = this.NB_WORD_GF2nv + (((this.NB_ITE - 1) - i5) * this.NB_WORD_GF2nvm);
                pointer7.copyFrom(i7, pointer7, this.NB_WORD_GF2nv - this.NB_WORD_GF2nvm, this.NB_WORD_GF2nvm);
                if (this.HFEmr != 0) {
                    pointer7.setAnd(i7, this.MASK_GF2m ^ (-1));
                }
                byte[] bytes = pointer6.toBytes(this.SIZE_DIGEST);
                getSHA3Hash(pointer3, 0, this.SIZE_DIGEST, bytes, 0, bytes.length, bytes);
                pointer3.swap(pointer6);
            }
        }
        if (this.NB_ITE == 1) {
            System.arraycopy(pointer7.toBytes(pointer7.getLength() << 3), 0, bArr, 0, this.NB_BYTES_GFqnv);
        } else {
            compress_signHFE(bArr, pointer7);
        }
    }

    private void precSignHFE(SecretKeyHFE secretKeyHFE, Pointer[] pointerArr, byte[] bArr) {
        precSignHFESeed(secretKeyHFE, bArr);
        initListDifferences_gf2nx(secretKeyHFE.F_struct.L);
        Pointer pointer = new Pointer(secretKeyHFE.F_HFEv);
        Pointer pointer2 = new Pointer(this.NB_COEFS_HFEPOLY * this.NB_WORD_GFqn);
        pointerArr[0] = new Pointer(pointer, this.MQv_GFqn_SIZE);
        pointer.changeIndex(pointerArr[0], this.MLv_GFqn_SIZE);
        Pointer pointer3 = new Pointer(pointer2, 2 * this.NB_WORD_GFqn);
        int i = 0;
        while (i < this.HFEDegI) {
            int i2 = i - (((1 << i) + 1 <= this.HFE_odd_degree || !this.ENABLED_REMOVE_ODD_DEGREE) ? 0 : 1);
            pointer3.copyFrom(pointer, i2 * this.NB_WORD_GFqn);
            pointer.move(i2 * this.NB_WORD_GFqn);
            pointer3.move(i2 * this.NB_WORD_GFqn);
            pointerArr[i + 1] = new Pointer(pointer);
            pointer.move(this.MLv_GFqn_SIZE);
            pointer3.move(this.NB_WORD_GFqn);
            i++;
        }
        if (this.HFEDegJ != 0) {
            pointer3.copyFrom(pointer, (this.HFEDegJ - ((1 << i) + 1 <= this.HFE_odd_degree ? 0 : 1)) * this.NB_WORD_GFqn);
        }
        secretKeyHFE.F_struct.poly = new Pointer(pointer2);
    }

    private void precSignHFESeed(SecretKeyHFE secretKeyHFE, byte[] bArr) {
        int i = this.NB_UINT_HFEVPOLY + ((this.LTRIANGULAR_NV_SIZE + this.LTRIANGULAR_N_SIZE) << 1);
        secretKeyHFE.sk_uncomp = new Pointer(i + this.MATRIXnv_SIZE + this.MATRIXn_SIZE);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(this.ShakeBitStrength);
        sHAKEDigest.update(bArr, 0, this.SIZE_SEED_SK);
        byte[] bArr2 = new byte[i << 3];
        sHAKEDigest.doFinal(bArr2, 0, bArr2.length);
        secretKeyHFE.sk_uncomp.fill(0, bArr2, 0, bArr2.length);
        secretKeyHFE.S = new Pointer(secretKeyHFE.sk_uncomp, i);
        secretKeyHFE.T = new Pointer(secretKeyHFE.S, this.MATRIXnv_SIZE);
        secretKeyHFE.F_HFEv = new Pointer(secretKeyHFE.sk_uncomp);
        cleanMonicHFEv_gf2nx(secretKeyHFE.F_HFEv);
        Pointer pointer = new Pointer(secretKeyHFE.sk_uncomp, this.NB_UINT_HFEVPOLY);
        Pointer pointer2 = new Pointer(pointer, this.LTRIANGULAR_NV_SIZE);
        cleanLowerMatrix(pointer, FunctionParams.NV);
        cleanLowerMatrix(pointer2, FunctionParams.NV);
        mulMatricesLU_gf2(secretKeyHFE.S, pointer, pointer2, FunctionParams.NV);
        pointer.move(this.LTRIANGULAR_NV_SIZE << 1);
        pointer2.changeIndex(pointer, this.LTRIANGULAR_N_SIZE);
        cleanLowerMatrix(pointer, FunctionParams.N);
        cleanLowerMatrix(pointer2, FunctionParams.N);
        mulMatricesLU_gf2(secretKeyHFE.T, pointer, pointer2, FunctionParams.N);
    }

    void cleanMonicHFEv_gf2nx(Pointer pointer) {
        int i = this.NB_WORD_GFqn - 1;
        while (true) {
            int i2 = i;
            if (i2 >= this.NB_UINT_HFEVPOLY) {
                return;
            }
            pointer.setAnd(i2, this.MASK_GF2n);
            i = i2 + this.NB_WORD_GFqn;
        }
    }

    private void mulMatricesLU_gf2(Pointer pointer, Pointer pointer2, Pointer pointer3, FunctionParams functionParams) {
        int i;
        int i2;
        boolean z;
        int index = pointer.getIndex();
        switch (functionParams.ordinal()) {
            case 0:
                i = this.HFEnvq;
                i2 = this.HFEnvr;
                z = this.HFEnvr != 0;
                break;
            case 2:
                i = this.HFEnq;
                i2 = this.HFEnr;
                z = true;
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter for MULMATRICESLU_GF2");
        }
        Pointer pointer4 = new Pointer(pointer2);
        int i3 = 1;
        while (i3 <= i) {
            LOOPIR(pointer, pointer4, pointer3, 64, i, i2, i3, z);
            i3++;
        }
        LOOPIR(pointer, pointer4, pointer3, i2, i, i2, i3, z);
        pointer.changeIndex(index);
    }

    private void LOOPIR(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2, int i3, int i4, boolean z) {
        for (int i5 = 0; i5 < i; i5++) {
            Pointer pointer4 = new Pointer(pointer3);
            int i6 = 1;
            while (i6 <= i2) {
                LOOPJR(pointer, pointer2, pointer4, 64, i4, i6);
                i6++;
            }
            if (z) {
                LOOPJR(pointer, pointer2, pointer4, i3, i4, i6);
            }
            pointer2.move(i4);
        }
    }

    private void LOOPJR(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2, int i3) {
        int iMin = Math.min(i2, i3);
        pointer.set(0L);
        for (int i4 = 0; i4 < i; i4++) {
            pointer.setXor(GeMSSUtils.XORBITS_UINT(pointer2.getDotProduct(0, pointer3, 0, iMin)) << i4);
            pointer3.move(i3);
        }
        pointer.moveIncremental();
    }

    private int setArrayL(int[] iArr, int i, int i2, int i3) {
        for (int i4 = i2; i4 < i3; i4++) {
            int i5 = i;
            i++;
            iArr[i5] = this.NB_WORD_GFqn << i4;
        }
        return i;
    }

    private void initListDifferences_gf2nx(int[] iArr) {
        int arrayL;
        int i = 2;
        iArr[1] = this.NB_WORD_GFqn;
        int i2 = 0;
        while (i2 < this.HFEDegI) {
            if (!this.ENABLED_REMOVE_ODD_DEGREE || (1 << i2) + 1 <= this.HFE_odd_degree) {
                iArr[i] = this.NB_WORD_GFqn;
                arrayL = setArrayL(iArr, i + 1, 0, i2);
            } else {
                if (i2 != 0) {
                    int i3 = i;
                    i++;
                    iArr[i3] = this.NB_WORD_GFqn << 1;
                }
                arrayL = setArrayL(iArr, i, 1, i2);
            }
            i = arrayL;
            i2++;
        }
        if (this.HFEDegJ != 0) {
            if (!this.ENABLED_REMOVE_ODD_DEGREE || (1 << i2) + 1 <= this.HFE_odd_degree) {
                iArr[i] = this.NB_WORD_GFqn;
                setArrayL(iArr, i + 1, 0, this.HFEDegJ - 1);
            } else {
                iArr[i] = this.NB_WORD_GFqn << 1;
                setArrayL(iArr, i + 1, 1, this.HFEDegJ - 1);
            }
        }
    }

    void evalMQSv_unrolled_gf2(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        Pointer pointer4 = new Pointer(this.HFEv);
        int i = this.HFEv >>> 6;
        int i2 = this.HFEv & 63;
        int i3 = (this.HFEn >>> 6) + ((this.HFEn & 63) != 0 ? 1 : 0);
        int index = pointer3.getIndex();
        Pointer pointer5 = new Pointer(i3);
        int i4 = 0;
        int range_xi = 0;
        while (i4 < i) {
            range_xi = pointer4.setRange_xi(pointer2.get(i4), range_xi, 64);
            i4++;
        }
        if (i2 != 0) {
            pointer4.setRange_xi(pointer2.get(i4), range_xi, i2);
        }
        pointer.copyFrom(pointer3, i3);
        pointer3.move(i3);
        for (int i5 = 0; i5 < this.HFEv; i5++) {
            pointer5.copyFrom(pointer3, i3);
            pointer3.move(i3);
            int i6 = i5 + 1;
            while (i6 < this.HFEv - 3) {
                pointer5.setXorRangeAndMaskMove(pointer3, i3, pointer4.get(i6));
                pointer5.setXorRangeAndMaskMove(pointer3, i3, pointer4.get(i6 + 1));
                pointer5.setXorRangeAndMaskMove(pointer3, i3, pointer4.get(i6 + 2));
                pointer5.setXorRangeAndMaskMove(pointer3, i3, pointer4.get(i6 + 3));
                i6 += 4;
            }
            while (i6 < this.HFEv) {
                pointer5.setXorRangeAndMaskMove(pointer3, i3, pointer4.get(i6));
                i6++;
            }
            pointer.setXorRangeAndMask(pointer5, i3, pointer4.get(i5));
        }
        pointer3.changeIndex(index);
    }

    private int chooseRootHFE_gf2nx(Pointer pointer, SecretKeyHFE.complete_sparse_monic_gf2nx complete_sparse_monic_gf2nxVar, Pointer pointer2) {
        Pointer pointer3 = new Pointer(this.SIZE_DIGEST_UINT);
        Pointer pointer4 = new Pointer(((this.HFEDeg << 1) - 1) * this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer((this.HFEDeg + 1) * this.NB_WORD_GFqn);
        Pointer pointer6 = new Pointer(this.NB_WORD_GFqn);
        pointer6.setRangeFromXor(complete_sparse_monic_gf2nxVar.poly, pointer2, this.NB_WORD_GFqn);
        if (this.HFEDeg <= 34 || (this.HFEn > 196 && this.HFEDeg < 256)) {
            frobeniusMap_multisqr_HFE_gf2nx(pointer4, complete_sparse_monic_gf2nxVar, pointer6);
        } else {
            int i = 2 << this.HFEDegI;
            pointer4.set(i * this.NB_WORD_GFqn, 1L);
            divsqr_r_HFE_cstdeg_gf2nx(pointer4, i, i, this.HFEDeg, complete_sparse_monic_gf2nxVar, pointer6);
            for_sqr_divsqr(pointer4, this.HFEDegI + 1, this.HFEn, complete_sparse_monic_gf2nxVar, pointer6);
        }
        pointer4.setXor(this.NB_WORD_GFqn, 1L);
        int index = pointer5.getIndex();
        pointer5.copyFrom(complete_sparse_monic_gf2nxVar.poly, this.NB_WORD_GFqn);
        for_copy_move(pointer5, complete_sparse_monic_gf2nxVar);
        pointer5.changeIndex(index);
        pointer5.set(this.HFEDeg * this.NB_WORD_GFqn, 1L);
        pointer5.setXorRange(pointer2, this.NB_WORD_GFqn);
        int iGcd_gf2nx = gcd_gf2nx(pointer5, this.HFEDeg, pointer4, pointer4.getD_for_not0_or_plus(this.NB_WORD_GFqn, this.HFEDeg - 1));
        if (this.buffer != 0) {
            pointer4.swap(pointer5);
        }
        if (pointer4.is0_gf2n(0, this.NB_WORD_GFqn) == 0) {
            return 0;
        }
        convMonic_gf2nx(pointer5, iGcd_gf2nx);
        Pointer pointer7 = new Pointer(iGcd_gf2nx * this.NB_WORD_GFqn);
        findRootsSplit_gf2nx(pointer7, pointer5, iGcd_gf2nx);
        if (iGcd_gf2nx == 1) {
            pointer.copyFrom(pointer7, this.NB_WORD_GFqn);
        } else {
            fast_sort_gf2n(pointer7, iGcd_gf2nx);
            getSHA3Hash(pointer3, 0, this.Sha3BitStrength >>> 3, pointer2.toBytes(this.NB_BYTES_GFqn), 0, this.NB_BYTES_GFqn, new byte[this.Sha3BitStrength >>> 3]);
            pointer.copyFrom(0, pointer7, ((int) remainderUnsigned(pointer3.get(), iGcd_gf2nx)) * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
        }
        return iGcd_gf2nx;
    }

    private int gcd_gf2nx(Pointer pointer, int i, Pointer pointer2, int i2) {
        int iDiv_r_monic_gf2nx;
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        this.buffer = 0;
        while (i2 != 0) {
            if ((i2 << 1) > i) {
                iDiv_r_monic_gf2nx = div_r_gf2nx(pointer, i, pointer2, i2);
            } else {
                inv_gf2n(pointer3, pointer2, i2 * this.NB_WORD_GFqn);
                pointer2.set1_gf2n(i2 * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
                for_mul(pointer2, pointer3, i2 - 1);
                iDiv_r_monic_gf2nx = div_r_monic_gf2nx(pointer, i, pointer2, i2);
            }
            int i3 = iDiv_r_monic_gf2nx;
            Pointer pointer4 = pointer;
            pointer = pointer2;
            pointer2 = pointer4;
            i = i2;
            i2 = i3;
            this.buffer = 1 - this.buffer;
        }
        return i;
    }

    private void for_mul(Pointer pointer, Pointer pointer2, int i) {
        Pointer pointer3 = new Pointer(pointer, i * this.NB_WORD_GFqn);
        for (int i2 = i; i2 != -1; i2--) {
            mul_gf2n(pointer3, pointer3, pointer2);
            pointer3.move(-this.NB_WORD_GFqn);
        }
    }

    private void frobeniusMap_multisqr_HFE_gf2nx(Pointer pointer, SecretKeyHFE.complete_sparse_monic_gf2nx complete_sparse_monic_gf2nxVar, Pointer pointer2) {
        Pointer pointer3 = new Pointer();
        Pointer pointer4 = new Pointer(this.HFEDeg * this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer();
        Pointer pointer6 = new Pointer(((this.KX * this.HFEDeg) + this.POW_II) * this.NB_WORD_GFqn);
        int i = (this.POW_II * this.KP) - this.HFEDeg;
        Pointer pointer7 = new Pointer(pointer6, this.NB_WORD_GFqn * i);
        pointer7.copyFrom(pointer2, this.NB_WORD_GFqn);
        for_copy_move(pointer7, complete_sparse_monic_gf2nxVar);
        divsqr_r_HFE_cstdeg_gf2nx(pointer6, (i - 1) + this.HFEDeg, i - 1, 0, complete_sparse_monic_gf2nxVar, pointer2);
        for (int i2 = this.KP + 1; i2 < this.HFEDeg; i2++) {
            pointer7.changeIndex(pointer6, this.HFEDeg * this.NB_WORD_GFqn);
            pointer7.setRangeClear(0, this.POW_II * this.NB_WORD_GFqn);
            pointer7.copyFrom(this.POW_II * this.NB_WORD_GFqn, pointer6, 0, this.HFEDeg * this.NB_WORD_GFqn);
            pointer6.changeIndex(pointer7);
            divsqr_r_HFE_cstdeg_gf2nx(pointer6, (this.POW_II - 1) + this.HFEDeg, this.POW_II - 1, 0, complete_sparse_monic_gf2nxVar, pointer2);
        }
        pointer6.indexReset();
        pointer.copyFrom(0, pointer6, ((1 << this.HFEDegI) - this.KP) * this.HFEDeg * this.NB_WORD_GFqn, this.HFEDeg * this.NB_WORD_GFqn);
        for (int i3 = 0; i3 < ((this.HFEn - this.HFEDegI) - this.II) / this.II; i3++) {
            loop_sqr(pointer4, pointer);
            for (int i4 = 1; i4 < this.II; i4++) {
                loop_sqr(pointer4, pointer4);
            }
            pointer5.changeIndex(pointer4, this.KP * this.NB_WORD_GFqn);
            pointer7.changeIndex(pointer6);
            pointer3.changeIndex(pointer);
            for (int i5 = 0; i5 < this.HFEDeg; i5++) {
                mul_gf2n(pointer3, pointer7, pointer5);
                pointer3.move(this.NB_WORD_GFqn);
                pointer7.move(this.NB_WORD_GFqn);
            }
            for (int i6 = this.KP + 1; i6 < this.HFEDeg; i6++) {
                pointer5.move(this.NB_WORD_GFqn);
                pointer3.changeIndex(pointer);
                for (int i7 = 0; i7 < this.HFEDeg; i7++) {
                    mul_rem_xorrange(pointer3, pointer7, pointer5);
                    pointer3.move(this.NB_WORD_GFqn);
                    pointer7.move(this.NB_WORD_GFqn);
                }
            }
            for (int i8 = 0; i8 < this.KP; i8++) {
                pointer.setXorRange(i8 * this.POW_II * this.NB_WORD_GFqn, pointer4, i8 * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
            }
        }
        for_sqr_divsqr(pointer, 0, (this.HFEn - this.HFEDegI) % this.II, complete_sparse_monic_gf2nxVar, pointer2);
    }

    private void for_sqr_divsqr(Pointer pointer, int i, int i2, SecretKeyHFE.complete_sparse_monic_gf2nx complete_sparse_monic_gf2nxVar, Pointer pointer2) {
        for (int i3 = i; i3 < i2; i3++) {
            sqr_gf2nx(pointer, this.HFEDeg - 1);
            divsqr_r_HFE_cstdeg_gf2nx(pointer, (this.HFEDeg - 1) << 1, (this.HFEDeg - 1) << 1, this.HFEDeg, complete_sparse_monic_gf2nxVar, pointer2);
        }
    }

    private void loop_sqr(Pointer pointer, Pointer pointer2) {
        for (int i = 0; i < this.HFEDeg; i++) {
            sqr_gf2n(pointer, i * this.NB_WORD_GFqn, pointer2, i * this.NB_WORD_GFqn);
        }
    }

    private void for_copy_move(Pointer pointer, SecretKeyHFE.complete_sparse_monic_gf2nx complete_sparse_monic_gf2nxVar) {
        int i = 1;
        int i2 = this.NB_WORD_GFqn;
        while (true) {
            int i3 = i2;
            if (i >= this.NB_COEFS_HFEPOLY) {
                return;
            }
            pointer.move(complete_sparse_monic_gf2nxVar.L[i]);
            pointer.copyFrom(0, complete_sparse_monic_gf2nxVar.poly, i * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
            i++;
            i2 = i3 + this.NB_WORD_GFqn;
        }
    }

    private void divsqr_r_HFE_cstdeg_gf2nx(Pointer pointer, int i, int i2, int i3, SecretKeyHFE.complete_sparse_monic_gf2nx complete_sparse_monic_gf2nxVar, Pointer pointer2) {
        Pointer pointer3 = new Pointer(pointer, i * this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer();
        for (int i4 = i2; i4 >= i3; i4--) {
            pointer4.changeIndex(pointer3, (-this.HFEDeg) * this.NB_WORD_GFqn);
            mul_rem_xorrange(pointer4, pointer3, pointer2);
            for (int i5 = 1; i5 < this.NB_COEFS_HFEPOLY; i5++) {
                pointer4.move(complete_sparse_monic_gf2nxVar.L[i5]);
                mul_rem_xorrange(pointer4, pointer3, complete_sparse_monic_gf2nxVar.poly, i5 * this.NB_WORD_GFqn);
            }
            pointer3.move(-this.NB_WORD_GFqn);
        }
    }

    private void sqr_gf2nx(Pointer pointer, int i) {
        int i2 = this.NB_WORD_GFqn * i;
        int index = pointer.getIndex();
        pointer.move(i2);
        Pointer pointer2 = new Pointer(pointer, i2);
        for (int i3 = 0; i3 < i; i3++) {
            sqr_gf2n(pointer2, pointer);
            pointer.move(-this.NB_WORD_GFqn);
            pointer2.move(-this.NB_WORD_GFqn);
            pointer2.setRangeClear(0, this.NB_WORD_GFqn);
            pointer2.move(-this.NB_WORD_GFqn);
        }
        sqr_gf2n(pointer, pointer);
        pointer.changeIndex(index);
    }

    int div_r_gf2nx(Pointer pointer, int i, Pointer pointer2, int i2) {
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer(pointer);
        inv_gf2n(pointer4, pointer2, i2 * this.NB_WORD_GFqn);
        while (i >= i2) {
            i = pointer.searchDegree(i, i2, this.NB_WORD_GFqn);
            if (i < i2) {
                break;
            }
            pointer5.changeIndex((i - i2) * this.NB_WORD_GFqn);
            mul_gf2n(pointer3, pointer, i * this.NB_WORD_GFqn, pointer4);
            for_mul_rem_xor_move(pointer5, pointer3, pointer2, 0, i2);
            i--;
        }
        return pointer.searchDegree(i, 1, this.NB_WORD_GFqn);
    }

    private void div_q_monic_gf2nx(Pointer pointer, int i, Pointer pointer2, int i2) {
        Pointer pointer3 = new Pointer();
        Pointer pointer4 = new Pointer();
        while (i >= i2) {
            int iSearchDegree = pointer.searchDegree(i, i2, this.NB_WORD_GFqn);
            if (iSearchDegree < i2) {
                return;
            }
            pointer3.changeIndex(pointer, iSearchDegree * this.NB_WORD_GFqn);
            int iMax = Math.max(0, (i2 << 1) - iSearchDegree);
            pointer4.changeIndex(pointer, ((iSearchDegree - i2) + iMax) * this.NB_WORD_GFqn);
            for_mul_rem_xor_move(pointer4, pointer3, pointer2, iMax, i2);
            i = iSearchDegree - 1;
        }
    }

    private int div_r_monic_gf2nx(Pointer pointer, int i, Pointer pointer2, int i2) {
        Pointer pointer3 = new Pointer();
        Pointer pointer4 = new Pointer();
        while (i >= i2) {
            i = pointer.searchDegree(i, i2, this.NB_WORD_GFqn);
            if (i < i2) {
                break;
            }
            pointer3.changeIndex(pointer, i * this.NB_WORD_GFqn);
            pointer4.changeIndex(pointer3, (-i2) * this.NB_WORD_GFqn);
            for_mul_rem_xor_move(pointer4, pointer3, pointer2, 0, i2);
            i--;
        }
        if (i == -1) {
            i++;
        }
        return pointer.searchDegree(i, 1, this.NB_WORD_GFqn);
    }

    private void for_mul_rem_xor_move(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2) {
        int i3 = i;
        int i4 = i * this.NB_WORD_GFqn;
        while (true) {
            int i5 = i4;
            if (i3 >= i2) {
                return;
            }
            mul_rem_xorrange(pointer, pointer2, pointer3, i5);
            pointer.move(this.NB_WORD_GFqn);
            i3++;
            i4 = i5 + this.NB_WORD_GFqn;
        }
    }

    private void inv_gf2n(Pointer pointer, Pointer pointer2, int i) {
        int index = pointer2.getIndex();
        pointer2.move(i);
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        for (int i2 = this.HFEn_1rightmost - 1; i2 != -1; i2--) {
            int i3 = (this.HFEn - 1) >>> (i2 + 1);
            sqr_gf2n(pointer3, pointer);
            for (int i4 = 1; i4 < i3; i4++) {
                sqr_gf2n(pointer3, pointer3);
            }
            mul_gf2n(pointer, pointer, pointer3);
            if ((((this.HFEn - 1) >>> i2) & 1) != 0) {
                sqr_gf2n(pointer3, pointer);
                mul_gf2n(pointer, pointer2, pointer3);
            }
        }
        sqr_gf2n(pointer, pointer);
        pointer2.changeIndex(index);
    }

    private void convMonic_gf2nx(Pointer pointer, int i) {
        Pointer pointer2 = new Pointer(this.NB_WORD_GFqn);
        int index = pointer.getIndex();
        pointer.move(i * this.NB_WORD_GFqn);
        inv_gf2n(pointer2, pointer, 0);
        pointer.set1_gf2n(0, this.NB_WORD_GFqn);
        for (int i2 = i - 1; i2 != -1; i2--) {
            pointer.move(-this.NB_WORD_GFqn);
            mul_gf2n(pointer, pointer, pointer2);
        }
        pointer.changeIndex(index);
    }

    private void findRootsSplit_gf2nx(Pointer pointer, Pointer pointer2, int i) {
        int iGcd_gf2nx;
        int i2;
        if (i == 1) {
            pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
            return;
        }
        if ((this.HFEn & 1) != 0 && i == 2) {
            findRootsSplit2_HT_gf2nx(pointer, pointer2);
            return;
        }
        Pointer pointer3 = new Pointer(((i << 1) - 1) * this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer(i * this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer((i + 1) * this.NB_WORD_GFqn);
        Pointer pointer6 = new Pointer(this.NB_WORD_GFqn);
        while (true) {
            pointer3.setRangeClear(0, ((i << 1) - 1) * this.NB_WORD_GFqn);
            pointer4.setRangeClear(0, i * this.NB_WORD_GFqn);
            do {
                pointer4.fillRandom(this.NB_WORD_GFqn, this.random, this.NB_BYTES_GFqn);
                pointer4.setAnd((this.NB_WORD_GFqn << 1) - 1, this.MASK_GF2n);
            } while (pointer4.is0_gf2n(this.NB_WORD_GFqn, this.NB_WORD_GFqn) != 0);
            pointer5.copyFrom(pointer2, (i + 1) * this.NB_WORD_GFqn);
            traceMap_gf2nx(pointer4, pointer3, pointer5, i);
            iGcd_gf2nx = gcd_gf2nx(pointer5, i, pointer4, pointer4.searchDegree(i - 1, 1, this.NB_WORD_GFqn));
            i2 = this.buffer;
            if (iGcd_gf2nx != 0 && iGcd_gf2nx != i) {
                break;
            }
        }
        if (i2 != 0) {
            pointer4.swap(pointer5);
        }
        inv_gf2n(pointer6, pointer5, iGcd_gf2nx * this.NB_WORD_GFqn);
        pointer5.set1_gf2n(iGcd_gf2nx * this.NB_WORD_GFqn, this.NB_WORD_GFqn);
        for_mul(pointer5, pointer6, iGcd_gf2nx - 1);
        div_q_monic_gf2nx(pointer2, i, pointer5, iGcd_gf2nx);
        findRootsSplit_gf2nx(pointer, pointer5, iGcd_gf2nx);
        findRootsSplit_gf2nx(new Pointer(pointer, iGcd_gf2nx * this.NB_WORD_GFqn), new Pointer(pointer2, iGcd_gf2nx * this.NB_WORD_GFqn), i - iGcd_gf2nx);
    }

    void findRootsSplit2_HT_gf2nx(Pointer pointer, Pointer pointer2) {
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer(this.NB_WORD_GFqn);
        int index = pointer2.getIndex();
        sqr_gf2n(pointer3, 0, pointer2, this.NB_WORD_GFqn);
        inv_gf2n(pointer, pointer3, 0);
        mul_gf2n(pointer3, pointer2, pointer);
        findRootsSplit_x2_x_c_HT_gf2nx(pointer4, pointer3);
        pointer2.move(this.NB_WORD_GFqn);
        mul_gf2n(pointer, pointer4, pointer2);
        pointer.setRangeFromXor(this.NB_WORD_GFqn, pointer, 0, pointer2, 0, this.NB_WORD_GFqn);
        pointer2.changeIndex(index);
    }

    void findRootsSplit_x2_x_c_HT_gf2nx(Pointer pointer, Pointer pointer2) {
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        int i = (this.HFEn + 1) >>> 1;
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        int i2 = 1;
        for (int i3 = this.HFEn1h_rightmost; i3 != -1; i3--) {
            int i4 = i2 << 1;
            sqr_gf2n(pointer3, pointer);
            for (int i5 = 1; i5 < i4; i5++) {
                sqr_gf2n(pointer3, pointer3);
            }
            pointer.setXorRange(pointer3, this.NB_WORD_GFqn);
            i2 = i >>> i3;
            if ((i2 & 1) != 0) {
                sqr_gf2n(pointer3, pointer);
                sqr_gf2n(pointer, pointer3);
                pointer.setXorRange(pointer2, this.NB_WORD_GFqn);
            }
        }
    }

    private void traceMap_gf2nx(Pointer pointer, Pointer pointer2, Pointer pointer3, int i) {
        int i2 = 1;
        while ((1 << i2) < i) {
            sqr_gf2n(pointer, this.NB_WORD_GFqn << i2, pointer, this.NB_WORD_GFqn << (i2 - 1));
            i2++;
        }
        if (i2 >= this.HFEn) {
            return;
        }
        sqr_gf2n(pointer2, this.NB_WORD_GFqn << i2, pointer, this.NB_WORD_GFqn << (i2 - 1));
        div_r_monic_cst_gf2nx(pointer2, 1 << i2, pointer3, i);
        pointer.setXorRange(pointer2, i * this.NB_WORD_GFqn);
        while (true) {
            i2++;
            if (i2 >= this.HFEn) {
                return;
            }
            sqr_gf2nx(pointer2, i - 1);
            div_r_monic_cst_gf2nx(pointer2, (i - 1) << 1, pointer3, i);
            pointer.setXorRange(pointer2, i * this.NB_WORD_GFqn);
        }
    }

    private void div_r_monic_cst_gf2nx(Pointer pointer, int i, Pointer pointer2, int i2) {
        Pointer pointer3 = new Pointer();
        int index = pointer.getIndex();
        pointer.move(i * this.NB_WORD_GFqn);
        while (i >= i2) {
            pointer3.changeIndex(pointer, (-i2) * this.NB_WORD_GFqn);
            for_mul_rem_xor_move(pointer3, pointer, pointer2, 0, i2);
            pointer.move(-this.NB_WORD_GFqn);
            i--;
        }
        pointer.changeIndex(index);
    }

    void fast_sort_gf2n(Pointer pointer, int i) {
        Pointer pointer2 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer3 = new Pointer(this.NB_WORD_GFqn);
        Pointer pointer4 = new Pointer();
        Pointer pointer5 = new Pointer();
        int iHighest_One = GeMSSUtils.Highest_One(i - 1);
        int i2 = iHighest_One;
        while (true) {
            int i3 = i2;
            if (i3 <= 1) {
                break;
            }
            int i4 = i / (i3 << 1);
            int iMax = Math.max(0, (i - ((i3 << 1) * i4)) - i3);
            pointer4.changeIndex(pointer);
            pointer5.changeIndex(pointer, i3 * this.NB_WORD_GFqn);
            for (int i5 = 0; i5 < i4; i5++) {
                for_casct_move(pointer4, pointer5, pointer3, i3, 1);
                pointer4.move(i3 * this.NB_WORD_GFqn);
                pointer5.move(i3 * this.NB_WORD_GFqn);
            }
            for_casct_move(pointer4, pointer5, pointer3, iMax, 1);
            int i6 = 0;
            for (int i7 = iHighest_One; i7 > i3; i7 >>>= 1) {
                while (i6 < i - i7) {
                    if ((i6 & i3) == 0) {
                        pointer5.changeIndex(pointer, (i6 + i3) * this.NB_WORD_GFqn);
                        copy_for_casct(pointer2, pointer5, pointer, pointer4, pointer3, i7, i6);
                        pointer5.copyFrom(pointer2, this.NB_WORD_GFqn);
                    }
                    i6++;
                }
            }
            i2 = i3 >>> 1;
        }
        pointer4.changeIndex(pointer);
        pointer5.changeIndex(pointer, this.NB_WORD_GFqn);
        for_casct_move(pointer4, pointer5, pointer3, i - 1, 2);
        pointer5.changeIndex(pointer, this.NB_WORD_GFqn);
        int i8 = 0;
        for (int i9 = iHighest_One; i9 > 1; i9 >>>= 1) {
            while (i8 < i - i9) {
                copy_for_casct(pointer2, pointer5, pointer, pointer4, pointer3, i9, i8);
                pointer5.copyFrom(pointer2, this.NB_WORD_GFqn);
                pointer5.move(this.NB_WORD_GFqn << 1);
                i8 += 2;
            }
        }
    }

    private void copy_for_casct(Pointer pointer, Pointer pointer2, Pointer pointer3, Pointer pointer4, Pointer pointer5, int i, int i2) {
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        int i3 = i;
        while (true) {
            int i4 = i3;
            if (i4 <= 1) {
                return;
            }
            pointer4.changeIndex(pointer3, (i2 + i4) * this.NB_WORD_GFqn);
            CMP_AND_SWAP_CST_TIME(pointer, pointer4, pointer5);
            i3 = i4 >>> 1;
        }
    }

    private void for_casct_move(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2) {
        int i3 = this.NB_WORD_GFqn * i2;
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= i) {
                return;
            }
            CMP_AND_SWAP_CST_TIME(pointer, pointer2, pointer3);
            pointer.move(i3);
            pointer2.move(i3);
            i4 = i5 + i2;
        }
    }

    private void CMP_AND_SWAP_CST_TIME(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        long jORBITS_UINT = 0;
        long j = 0;
        for (int i = this.NB_WORD_GFqn - 1; i > 0; i--) {
            jORBITS_UINT |= GeMSSUtils.ORBITS_UINT(pointer2.get(i) ^ pointer.get(i));
            j += jORBITS_UINT;
        }
        long jCMP_LT_UINT = 0;
        for (int i2 = 0; i2 < this.NB_WORD_GFqn; i2++) {
            jCMP_LT_UINT |= (-GeMSSUtils.NORBITS_UINT(((long) i2) ^ j)) & GeMSSUtils.CMP_LT_UINT(pointer2.get(i2), pointer.get(i2));
        }
        pointer3.setRangeFromXorAndMask_xor(pointer, pointer2, -jCMP_LT_UINT, this.NB_WORD_GFqn);
    }

    public void compress_signHFE(byte[] bArr, Pointer pointer) {
        int i;
        byte[] bytes = pointer.toBytes(pointer.getLength() << 3);
        System.arraycopy(bytes, 0, bArr, 0, this.NB_BYTES_GFqnv);
        int i2 = this.HFEnv;
        int i3 = (this.NB_WORD_GF2nv << 3) + (this.HFEmq8 & 7);
        for (int i4 = 1; i4 < this.NB_ITE; i4++) {
            int iMin = Math.min(this.HFEDELTA + this.HFEv, (8 - (i2 & 7)) & 7);
            if ((i2 & 7) != 0) {
                if (this.HFEmr8 != 0) {
                    int i5 = i2 >>> 3;
                    bArr[i5] = (byte) (bArr[i5] ^ (((bytes[i3] & 255) >>> this.HFEmr8) << (i2 & 7)));
                    int i6 = iMin - this.VAL_BITS_M;
                    if (i6 >= 0) {
                        i3++;
                    }
                    if (i6 > 0) {
                        int i7 = i2 + this.VAL_BITS_M;
                        int i8 = i7 >>> 3;
                        bArr[i8] = (byte) (bArr[i8] ^ ((bytes[i3] & 255) << (i7 & 7)));
                        i2 = i7 + i6;
                    } else {
                        i2 += iMin;
                    }
                } else {
                    int i9 = i2 >>> 3;
                    bArr[i9] = (byte) (bArr[i9] ^ ((bytes[i3] & 255) << (i2 & 7)));
                    i2 += iMin;
                }
            }
            int i10 = (this.HFEDELTA + this.HFEv) - iMin;
            int i11 = (this.HFEm + iMin) & 7;
            if (i11 != 0) {
                for (int i12 = 0; i12 < ((i10 - 1) >>> 3); i12++) {
                    int i13 = (bytes[i3] & 255) >>> i11;
                    i3++;
                    bArr[i2 >>> 3] = (byte) (i13 ^ ((bytes[i3] & 255) << (8 - i11)));
                    i2 += 8;
                }
                int i14 = i3;
                i3++;
                bArr[i2 >>> 3] = (byte) ((bytes[i14] & 255) >>> i11);
                int i15 = ((i10 + 7) & 7) + 1;
                if (i15 > 8 - i11) {
                    int i16 = i2 >>> 3;
                    i3++;
                    bArr[i16] = (byte) (bArr[i16] ^ ((byte) ((bytes[i3] & 255) << (8 - i11))));
                }
                i = i2 + i15;
            } else {
                for (int i17 = 0; i17 < ((i10 + 7) >>> 3); i17++) {
                    int i18 = i3;
                    i3++;
                    bArr[i2 >>> 3] = bytes[i18];
                    i2 += 8;
                }
                i = i2 - ((8 - (i10 & 7)) & 7);
            }
            i2 = i;
            i3 += ((8 - (this.NB_BYTES_GFqnv & 7)) & 7) + (this.HFEmq8 & 7);
        }
    }

    void convMQS_one_to_last_mr8_equations_gf2(byte[] bArr, PointerUnion pointerUnion) {
        int i = 0;
        pointerUnion.moveNextBytes(this.HFEmq8);
        PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
        int i2 = this.NB_MONOMIAL_PK >>> 3;
        for (int i3 = 0; i3 < this.HFEmr8; i3++) {
            pointerUnion2.changeIndex(pointerUnion);
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = (pointerUnion2.getByte() >>> i3) & 1;
                pointerUnion2.moveNextBytes(this.NB_BYTES_GFqm);
                for (int i6 = 1; i6 < 8; i6++) {
                    i5 ^= ((pointerUnion2.getByte() >>> i3) & 1) << i6;
                    pointerUnion2.moveNextBytes(this.NB_BYTES_GFqm);
                }
                int i7 = i;
                i++;
                bArr[i7] = (byte) i5;
            }
            if (this.HFENr8 != 0) {
                long withCheck = (pointerUnion2.getWithCheck() >>> i3) & 1;
                pointerUnion2.moveNextBytes(this.NB_BYTES_GFqm);
                for (int i8 = 1; i8 < this.HFENr8; i8++) {
                    withCheck ^= ((pointerUnion2.getWithCheck() >>> i3) & 1) << i8;
                    pointerUnion2.moveNextBytes(this.NB_BYTES_GFqm);
                }
                int i9 = i;
                i++;
                bArr[i9] = (byte) withCheck;
            }
        }
    }

    void convMQ_UL_gf2(byte[] bArr, byte[] bArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            for_setPK(bArr, bArr2, this.ACCESS_last_equations8 + (i2 * this.NB_BYTES_EQUATION), i2 * this.NB_BYTES_EQUATION, this.HFEnv + 1);
        }
    }

    private int for_setPK(byte[] bArr, byte[] bArr2, int i, int i2, int i3) {
        bArr[i] = (byte) (bArr2[i2] & 3);
        int pk = 2;
        for (int i4 = 2; i4 < i3; i4++) {
            pk = setPK(bArr, bArr2, i4, i, i2, pk, this.HFEnv - 1, this.HFEnv - i4);
        }
        return pk;
    }

    private int setPK(byte[] bArr, byte[] bArr2, int i, int i2, int i3, int i4, int i5, int i6) {
        int i7 = i5;
        while (i7 >= i6) {
            int i8 = i2 + (i4 >>> 3);
            bArr[i8] = (byte) (bArr[i8] ^ (((bArr2[i3 + (i >>> 3)] >>> (i & 7)) & 1) << (i4 & 7)));
            i += i7;
            i7--;
            i4++;
        }
        this.buffer = i;
        return i4;
    }

    void convMQS_one_eq_to_hybrid_rep8_comp_gf2(byte[] bArr, PointerUnion pointerUnion, byte[] bArr2) {
        int bytesMove = 0;
        convMQ_UL_gf2(bArr, bArr2, this.HFEmr8);
        for (int i = 0; i < this.NB_MONOMIAL_PK; i++) {
            bytesMove = pointerUnion.toBytesMove(bArr, bytesMove, this.HFEmq8);
            if (this.HFEmr8 != 0) {
                pointerUnion.moveNextByte();
            }
        }
    }

    void convMQS_one_eq_to_hybrid_rep8_uncomp_gf2(byte[] bArr, PointerUnion pointerUnion, byte[] bArr2) {
        int i = this.HFEmr8 - 1;
        long j = 0;
        convMQ_UL_gf2(bArr, bArr2, i);
        int i2 = this.ACCESS_last_equations8 + (i * this.NB_BYTES_EQUATION);
        int i3 = i * this.NB_BYTES_EQUATION;
        int pk = setPK(bArr, bArr2, this.HFEnv, i2, i3, for_setPK(bArr, bArr2, i2, i3, this.HFEnv), this.HFEnv - 1, this.LOST_BITS);
        int i4 = this.LOST_BITS - 1;
        int i5 = this.buffer;
        while (i4 >= 0) {
            j ^= ((long) ((bArr2[i3 + (i5 >>> 3)] >>> (i5 & 7)) & 1)) << ((this.LOST_BITS - 1) - i4);
            i5 += i4;
            i4--;
            pk++;
        }
        int i6 = this.ACCESS_last_equations8 - 1;
        for (int i7 = 0; i7 < this.HFEmr8 - 1; i7++) {
            i6 += this.NB_BYTES_EQUATION;
            bArr[i6] = (byte) (bArr[i6] ^ (((byte) (j >>> (i7 * this.HFENr8c))) << this.HFENr8));
        }
        pointerUnion.indexReset();
        int bytesMove = 0;
        for (int i8 = 0; i8 < this.NB_MONOMIAL_PK; i8++) {
            bytesMove = pointerUnion.toBytesMove(bArr, bytesMove, this.HFEmq8);
            pointerUnion.moveNextByte();
        }
    }

    public int crypto_sign_open(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        PointerUnion pointerUnion = new PointerUnion(bArr);
        long j = 0;
        if (this.HFENr8 != 0 && this.HFEmr8 > 1) {
            PointerUnion pointerUnion2 = new PointerUnion(pointerUnion);
            pointerUnion2.moveNextBytes(this.ACCESS_last_equations8 - 1);
            for (int i = 0; i < this.HFEmr8 - 1; i++) {
                pointerUnion2.moveNextBytes(this.NB_BYTES_EQUATION);
                j ^= ((((long) pointerUnion2.getByte()) & 255) >>> this.HFENr8) << (i * this.HFENr8c);
            }
        }
        if (this.HFEmr8 == 0) {
            Pointer pointer = new Pointer(this.SIZE_SIGN_UNCOMPRESSED);
            Pointer pointer2 = new Pointer(new Pointer(this.NB_WORD_GF2nv));
            Pointer pointer3 = new Pointer(this.SIZE_DIGEST_UINT);
            pointer.fill(0, bArr3, 0, this.NB_BYTES_GFqnv);
            getSHA3Hash(pointer3, 0, 64, bArr2, 0, bArr2.length, new byte[64]);
            evalMQSnocst8_quo_gf2(pointer2, pointer, pointerUnion);
            return pointer2.isEqual_nocst_gf2(pointer3, this.NB_WORD_GF2m);
        }
        Pointer pointer4 = new Pointer(1 + (this.NB_WORD_UNCOMP_EQ * this.HFEmr8));
        long jConvMQ_uncompressL_gf2 = 0;
        PointerUnion pointerUnion3 = new PointerUnion(pointerUnion);
        int i2 = 0;
        while (i2 < this.HFEmr8 - 1) {
            pointerUnion3.setByteIndex(this.ACCESS_last_equations8 + (i2 * this.NB_BYTES_EQUATION));
            jConvMQ_uncompressL_gf2 ^= convMQ_uncompressL_gf2(new Pointer(pointer4, 1 + (i2 * this.NB_WORD_UNCOMP_EQ)), pointerUnion3) << i2;
            i2++;
        }
        pointerUnion3.setByteIndex(this.ACCESS_last_equations8 + (i2 * this.NB_BYTES_EQUATION));
        long jConvMQ_last_uncompressL_gf2 = jConvMQ_uncompressL_gf2 ^ (convMQ_last_uncompressL_gf2(new Pointer(pointer4, 1 + (i2 * this.NB_WORD_UNCOMP_EQ)), pointerUnion3) << i2);
        if (this.HFENr8 != 0) {
            if (this.HFEnvr == 0) {
                pointer4.setXor((i2 + 1) * this.NB_WORD_UNCOMP_EQ, j << (64 - this.LOST_BITS));
            } else if (this.HFEnvr > this.LOST_BITS) {
                pointer4.setXor((i2 + 1) * this.NB_WORD_UNCOMP_EQ, j << (this.HFEnvr - this.LOST_BITS));
            } else if (this.HFEnvr == this.LOST_BITS) {
                pointer4.set((i2 + 1) * this.NB_WORD_UNCOMP_EQ, j);
            } else {
                pointer4.setXor(((i2 + 1) * this.NB_WORD_UNCOMP_EQ) - 1, j << (64 - (this.LOST_BITS - this.HFEnvr)));
                pointer4.set((i2 + 1) * this.NB_WORD_UNCOMP_EQ, j >>> (this.LOST_BITS - this.HFEnvr));
            }
        }
        pointer4.set(jConvMQ_last_uncompressL_gf2 << (this.HFEmr - this.HFEmr8));
        return sign_openHFE_huncomp_pk(bArr2, bArr2.length, bArr3, pointerUnion, new PointerUnion(pointer4));
    }

    void changeVariablesMQS64_gf2(Pointer pointer, Pointer pointer2) {
        Pointer pointer3 = new Pointer();
        Pointer pointer4 = new Pointer(this.HFEnv * this.HFEnv * this.NB_WORD_GFqn);
        Pointer pointer5 = new Pointer(pointer, this.NB_WORD_GFqn);
        Pointer pointer6 = new Pointer(pointer4);
        Pointer pointer7 = new Pointer(pointer2);
        for (int i = 0; i < this.HFEnv; i++) {
            pointer3.changeIndex(pointer5);
            for (int i2 = 0; i2 < this.HFEnvq; i2++) {
                for (int i3 = 0; i3 < 64; i3++) {
                    LOOPKR(pointer3, pointer6, pointer7.get() >>> i3, i3, 64);
                    LOOPK_COMPLETE(pointer6, pointer7, pointer3, 1, this.HFEnvq - i2);
                }
                pointer7.moveIncremental();
            }
            if (this.HFEnvr != 0) {
                for (int i4 = 0; i4 < this.HFEnvr; i4++) {
                    LOOPKR(pointer3, pointer6, pointer7.get() >>> i4, i4, this.HFEnvr);
                    pointer6.move(this.NB_WORD_GFqn);
                }
                pointer7.moveIncremental();
            }
        }
        pointer5.changeIndex(pointer4);
        pointer6.changeIndex(pointer, this.NB_WORD_GFqn);
        Pointer pointer8 = new Pointer(pointer2);
        for (int i5 = 0; i5 < this.HFEnvq; i5++) {
            for (int i6 = 0; i6 < 64; i6++) {
                pointer7.changeIndex(pointer8);
                LOOPIR_INIT(pointer6, pointer3, pointer5, pointer7, i6, 64);
                for (int i7 = i5 + 1; i7 < this.HFEnvq; i7++) {
                    LOOPIR_INIT(pointer6, pointer3, pointer5, pointer7, 0, 64);
                }
                if (this.HFEnvr != 0) {
                    LOOPIR_INIT(pointer6, pointer3, pointer5, pointer7, 0, this.HFEnvr);
                }
                pointer5.changeIndex(pointer3);
                pointer8.move(this.NB_WORD_GF2nv);
            }
        }
        if (this.HFEnvr != 0) {
            for (int i8 = 0; i8 < this.HFEnvr; i8++) {
                pointer7.changeIndex(pointer8);
                pointer3.changeIndex(pointer5);
                LOOPIR_INIT(pointer6, pointer3, pointer5, pointer7, i8, this.HFEnvr);
                pointer5.changeIndex(pointer3);
                pointer8.move(this.NB_WORD_GF2nv);
            }
        }
        pointer5.changeIndex(pointer4);
        pointer6.changeIndex(pointer, this.NB_WORD_GFqn);
        pointer7.changeIndex(pointer2);
        for (int i9 = 0; i9 < this.HFEnvq; i9++) {
            for (int i10 = 0; i10 < 64; i10++) {
                pointer6.move(this.NB_WORD_GFqn);
                pointer5.move(this.HFEnv * this.NB_WORD_GFqn);
                pointer3.changeIndex(pointer5);
                LOOPIR_LOOPK_COMPLETE(pointer6, pointer7, pointer3, i10 + 1, 64);
                for (int i11 = i9 + 1; i11 < this.HFEnvq; i11++) {
                    LOOPIR_LOOPK_COMPLETE(pointer6, pointer7, pointer3, 0, 64);
                }
                if (this.HFEnvr != 0) {
                    LOOPIR_LOOPK_COMPLETE(pointer6, pointer7, pointer3, 0, this.HFEnvr);
                }
                pointer7.move(this.NB_WORD_GF2nv);
            }
        }
        if (this.HFEnvr != 0) {
            for (int i12 = 0; i12 < this.HFEnvr - 1; i12++) {
                pointer6.move(this.NB_WORD_GFqn);
                pointer5.move(this.HFEnv * this.NB_WORD_GFqn);
                pointer3.changeIndex(pointer5);
                LOOPIR_LOOPK_COMPLETE(pointer6, pointer7, pointer3, i12 + 1, this.HFEnvr);
                pointer7.move(this.NB_WORD_GF2nv);
            }
        }
        pointer.indexReset();
        pointer2.indexReset();
    }

    private void LOOPIR_INIT(Pointer pointer, Pointer pointer2, Pointer pointer3, Pointer pointer4, int i, int i2) {
        for (int i3 = i; i3 < i2; i3++) {
            pointer.setRangeClear(0, this.NB_WORD_GFqn);
            pointer2.changeIndex(pointer3);
            LOOPK_COMPLETE(pointer, pointer4, pointer2, 0, this.HFEnvq);
            pointer4.move(this.NB_WORD_GF2nv);
        }
    }

    private void LOOPIR_LOOPK_COMPLETE(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2) {
        for (int i3 = i; i3 < i2; i3++) {
            LOOPK_COMPLETE(pointer, pointer2, pointer3, 0, this.HFEnvq);
        }
    }

    private void LOOPK_COMPLETE(Pointer pointer, Pointer pointer2, Pointer pointer3, int i, int i2) {
        for (int i3 = i; i3 < i2; i3++) {
            LOOPKR(pointer3, pointer, pointer2.get(i3), 0, 64);
        }
        if (this.HFEnvr != 0) {
            LOOPKR(pointer3, pointer, pointer2.get(i2), 0, this.HFEnvr);
        }
        pointer.move(this.NB_WORD_GFqn);
    }

    private void LOOPKR(Pointer pointer, Pointer pointer2, long j, int i, int i2) {
        for (int i3 = i; i3 < i2; i3++) {
            pointer2.setXorRangeAndMaskMove(pointer, this.NB_WORD_GFqn, -(j & 1));
            j >>>= 1;
        }
    }

    int interpolateHFE_FS_ref(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        Pointer pointer4 = new Pointer(this.NB_WORD_GF2nv);
        Pointer pointer5 = new Pointer();
        Pointer pointer6 = new Pointer();
        Pointer pointer7 = new Pointer(this.HFEnv * this.NB_WORD_GFqn);
        pointer.copyFrom(pointer2, this.NB_WORD_GFqn);
        Pointer pointer8 = new Pointer(pointer3);
        Pointer pointer9 = new Pointer(pointer7);
        for (int i = 0; i < this.HFEnv; i++) {
            evalHFEv_gf2nx(pointer9, pointer2, pointer8);
            pointer9.move(this.NB_WORD_GFqn);
            pointer8.move(this.NB_WORD_GF2nv);
        }
        pointer8.changeIndex(pointer3);
        pointer9.changeIndex(pointer7);
        for (int i2 = 0; i2 < this.HFEnv; i2++) {
            pointer.move(this.NB_WORD_GFqn);
            pointer9.setXorRange(pointer2, this.NB_WORD_GFqn);
            pointer.copyFrom(pointer9, this.NB_WORD_GFqn);
            pointer5.changeIndex(pointer9);
            pointer6.changeIndex(pointer8);
            for (int i3 = i2 + 1; i3 < this.HFEnv; i3++) {
                pointer.move(this.NB_WORD_GFqn);
                pointer5.move(this.NB_WORD_GFqn);
                pointer6.move(this.NB_WORD_GF2nv);
                pointer4.setRangeFromXor(pointer8, pointer6, this.NB_WORD_GF2nv);
                evalHFEv_gf2nx(pointer, pointer2, pointer4);
                pointer.setXorRangeXor(0, pointer9, 0, pointer5, 0, this.NB_WORD_GFqn);
            }
            pointer9.move(this.NB_WORD_GFqn);
            pointer8.move(this.NB_WORD_GF2nv);
        }
        pointer.indexReset();
        return 0;
    }

    void evalHFEv_gf2nx(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        Pointer pointer4 = new Pointer(this.NB_WORD_MUL);
        Pointer pointer5 = new Pointer(this.NB_WORD_MUL);
        Pointer pointer6 = new Pointer((this.HFEDegI + 1) * this.NB_WORD_GFqn);
        Pointer pointer7 = new Pointer();
        int index = pointer2.getIndex();
        Pointer pointer8 = new Pointer(this.NB_WORD_GFqv);
        Pointer pointer9 = new Pointer(pointer6, this.NB_WORD_GFqn);
        pointer6.copyFrom(pointer3, this.NB_WORD_GFqn);
        pointer6.setAnd(this.NB_WORD_GFqn - 1, this.MASK_GF2n);
        for (int i = 1; i <= this.HFEDegI; i++) {
            sqr_gf2n(pointer9, 0, pointer9, -this.NB_WORD_GFqn);
            pointer9.move(this.NB_WORD_GFqn);
        }
        int i2 = this.NB_WORD_GFqn + this.NB_WORD_GFqv == this.NB_WORD_GF2nv ? this.NB_WORD_GFqv : this.NB_WORD_GFqv - 1;
        pointer8.setRangeRotate(0, pointer3, this.NB_WORD_GFqn - 1, i2, 64 - this.HFEnr);
        if (this.NB_WORD_GFqn + this.NB_WORD_GFqv != this.NB_WORD_GF2nv) {
            pointer8.set(i2, pointer3.get((this.NB_WORD_GFqn - 1) + i2) >>> this.HFEnr);
        }
        evalMQSv_unrolled_gf2(pointer4, pointer8, pointer2);
        pointer2.move(this.MQv_GFqn_SIZE);
        vmpv_xorrange_move(pointer5, pointer8, pointer2);
        pointer9.changeIndex(pointer6);
        mul_xorrange(pointer4, pointer9, pointer5);
        for (int i3 = 1; i3 < this.HFEDegI; i3++) {
            vmpv_xorrange_move(pointer5, pointer8, pointer2);
            pointer5.setRangeClear(this.NB_WORD_GFqn, this.NB_WORD_MMUL - this.NB_WORD_GFqn);
            pointer7.changeIndex(pointer9);
            for_mul_xorrange_move(pointer5, pointer2, pointer7, i3);
            rem_gf2n(pointer5, 0, pointer5);
            mul_xorrange(pointer4, pointer7, pointer5);
        }
        vmpv_xorrange_move(pointer5, pointer8, pointer2);
        pointer7.changeIndex(pointer9);
        if (this.HFEDegJ != 0) {
            pointer5.setRangeClear(this.NB_WORD_GFqn, this.NB_WORD_MMUL - this.NB_WORD_GFqn);
            for_mul_xorrange_move(pointer5, pointer2, pointer7, this.HFEDegJ);
            pointer5.setXorRange(pointer7, this.NB_WORD_GFqn);
            rem_gf2n(pointer5, 0, pointer5);
        } else {
            pointer5.setRangeFromXor(pointer5, pointer7, this.NB_WORD_GFqn);
        }
        pointer9.move(this.HFEDegI * this.NB_WORD_GFqn);
        mul_xorrange(pointer4, pointer9, pointer5);
        rem_gf2n(pointer, 0, pointer4);
        pointer2.changeIndex(index);
    }

    private void vmpv_xorrange_move(Pointer pointer, Pointer pointer2, Pointer pointer3) {
        vecMatProduct(pointer, pointer2, new Pointer(pointer3, this.NB_WORD_GFqn), FunctionParams.V);
        pointer.setXorRange(pointer3, this.NB_WORD_GFqn);
        pointer3.move(this.MLv_GFqn_SIZE);
    }

    private static long remainderUnsigned(long j, long j2) {
        return (j <= 0 || j2 <= 0) ? new BigInteger(1, Pack.longToBigEndian(j)).mod(new BigInteger(1, Pack.longToBigEndian(j2))).longValue() : j % j2;
    }
}
