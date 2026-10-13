package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconFFT.class */
class FalconFFT {
    FPREngine fpr = new FPREngine();

    FalconFFT() {
    }

    ComplexNumberWrapper FPC_ADD(FalconFPR falconFPR, FalconFPR falconFPR2, FalconFPR falconFPR3, FalconFPR falconFPR4) {
        return new ComplexNumberWrapper(this.fpr.fpr_add(falconFPR, falconFPR3), this.fpr.fpr_add(falconFPR2, falconFPR4));
    }

    ComplexNumberWrapper FPC_SUB(FalconFPR falconFPR, FalconFPR falconFPR2, FalconFPR falconFPR3, FalconFPR falconFPR4) {
        return new ComplexNumberWrapper(this.fpr.fpr_sub(falconFPR, falconFPR3), this.fpr.fpr_sub(falconFPR2, falconFPR4));
    }

    ComplexNumberWrapper FPC_MUL(FalconFPR falconFPR, FalconFPR falconFPR2, FalconFPR falconFPR3, FalconFPR falconFPR4) {
        return new ComplexNumberWrapper(this.fpr.fpr_sub(this.fpr.fpr_mul(falconFPR, falconFPR3), this.fpr.fpr_mul(falconFPR2, falconFPR4)), this.fpr.fpr_add(this.fpr.fpr_mul(falconFPR, falconFPR4), this.fpr.fpr_mul(falconFPR2, falconFPR3)));
    }

    ComplexNumberWrapper FPC_SQR(FalconFPR falconFPR, FalconFPR falconFPR2) {
        return new ComplexNumberWrapper(this.fpr.fpr_sub(this.fpr.fpr_sqr(falconFPR), this.fpr.fpr_sqr(falconFPR2)), this.fpr.fpr_double(this.fpr.fpr_mul(falconFPR, falconFPR2)));
    }

    ComplexNumberWrapper FPC_INV(FalconFPR falconFPR, FalconFPR falconFPR2) {
        FalconFPR falconFPRFpr_inv = this.fpr.fpr_inv(this.fpr.fpr_add(this.fpr.fpr_sqr(falconFPR), this.fpr.fpr_sqr(falconFPR2)));
        return new ComplexNumberWrapper(this.fpr.fpr_mul(falconFPR, falconFPRFpr_inv), this.fpr.fpr_mul(this.fpr.fpr_neg(falconFPR2), falconFPRFpr_inv));
    }

    ComplexNumberWrapper FPC_DIV(FalconFPR falconFPR, FalconFPR falconFPR2, FalconFPR falconFPR3, FalconFPR falconFPR4) {
        FalconFPR falconFPRFpr_inv = this.fpr.fpr_inv(this.fpr.fpr_add(this.fpr.fpr_sqr(falconFPR3), this.fpr.fpr_sqr(falconFPR4)));
        FalconFPR falconFPRFpr_mul = this.fpr.fpr_mul(falconFPR3, falconFPRFpr_inv);
        FalconFPR falconFPRFpr_mul2 = this.fpr.fpr_mul(this.fpr.fpr_neg(falconFPR4), falconFPRFpr_inv);
        return new ComplexNumberWrapper(this.fpr.fpr_sub(this.fpr.fpr_mul(falconFPR, falconFPRFpr_mul), this.fpr.fpr_mul(falconFPR2, falconFPRFpr_mul2)), this.fpr.fpr_add(this.fpr.fpr_mul(falconFPR, falconFPRFpr_mul2), this.fpr.fpr_mul(falconFPR2, falconFPRFpr_mul)));
    }

    void FFT(FalconFPR[] falconFPRArr, int i, int i2) {
        int i3 = (1 << i2) >> 1;
        int i4 = i3;
        int i5 = 1;
        int i6 = 2;
        while (true) {
            int i7 = i6;
            if (i5 >= i2) {
                return;
            }
            int i8 = i4 >> 1;
            int i9 = i7 >> 1;
            int i10 = 0;
            int i11 = 0;
            while (true) {
                int i12 = i11;
                if (i10 < i9) {
                    int i13 = i12 + i8;
                    FalconFPR falconFPR = this.fpr.fpr_gm_tab[((i7 + i10) << 1) + 0];
                    FalconFPR falconFPR2 = this.fpr.fpr_gm_tab[((i7 + i10) << 1) + 1];
                    for (int i14 = i12; i14 < i13; i14++) {
                        FalconFPR falconFPR3 = falconFPRArr[i + i14];
                        FalconFPR falconFPR4 = falconFPRArr[i + i14 + i3];
                        ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPRArr[i + i14 + i8], falconFPRArr[i + i14 + i8 + i3], falconFPR, falconFPR2);
                        FalconFPR falconFPR5 = complexNumberWrapperFPC_MUL.re;
                        FalconFPR falconFPR6 = complexNumberWrapperFPC_MUL.im;
                        ComplexNumberWrapper complexNumberWrapperFPC_ADD = FPC_ADD(falconFPR3, falconFPR4, falconFPR5, falconFPR6);
                        falconFPRArr[i + i14] = complexNumberWrapperFPC_ADD.re;
                        falconFPRArr[i + i14 + i3] = complexNumberWrapperFPC_ADD.im;
                        ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR3, falconFPR4, falconFPR5, falconFPR6);
                        falconFPRArr[i + i14 + i8] = complexNumberWrapperFPC_SUB.re;
                        falconFPRArr[i + i14 + i8 + i3] = complexNumberWrapperFPC_SUB.im;
                    }
                    i10++;
                    i11 = i12 + i4;
                }
            }
            i4 = i8;
            i5++;
            i6 = i7 << 1;
        }
    }

    void iFFT(FalconFPR[] falconFPRArr, int i, int i2) {
        int i3 = 1 << i2;
        int i4 = 1;
        int i5 = i3;
        int i6 = i3 >> 1;
        for (int i7 = i2; i7 > 1; i7--) {
            int i8 = i5 >> 1;
            int i9 = i4 << 1;
            int i10 = 0;
            int i11 = 0;
            while (true) {
                int i12 = i11;
                if (i12 < i6) {
                    int i13 = i12 + i4;
                    FalconFPR falconFPR = this.fpr.fpr_gm_tab[((i8 + i10) << 1) + 0];
                    FalconFPR falconFPRFpr_neg = this.fpr.fpr_neg(this.fpr.fpr_gm_tab[((i8 + i10) << 1) + 1]);
                    for (int i14 = i12; i14 < i13; i14++) {
                        FalconFPR falconFPR2 = falconFPRArr[i + i14];
                        FalconFPR falconFPR3 = falconFPRArr[i + i14 + i6];
                        FalconFPR falconFPR4 = falconFPRArr[i + i14 + i4];
                        FalconFPR falconFPR5 = falconFPRArr[i + i14 + i4 + i6];
                        ComplexNumberWrapper complexNumberWrapperFPC_ADD = FPC_ADD(falconFPR2, falconFPR3, falconFPR4, falconFPR5);
                        falconFPRArr[i + i14] = complexNumberWrapperFPC_ADD.re;
                        falconFPRArr[i + i14 + i6] = complexNumberWrapperFPC_ADD.im;
                        ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR2, falconFPR3, falconFPR4, falconFPR5);
                        ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(complexNumberWrapperFPC_SUB.re, complexNumberWrapperFPC_SUB.im, falconFPR, falconFPRFpr_neg);
                        falconFPRArr[i + i14 + i4] = complexNumberWrapperFPC_MUL.re;
                        falconFPRArr[i + i14 + i4 + i6] = complexNumberWrapperFPC_MUL.im;
                    }
                    i10++;
                    i11 = i12 + i9;
                }
            }
            i4 = i9;
            i5 = i8;
        }
        if (i2 > 0) {
            FalconFPR falconFPR6 = this.fpr.fpr_p2_tab[i2];
            for (int i15 = 0; i15 < i3; i15++) {
                falconFPRArr[i + i15] = this.fpr.fpr_mul(falconFPRArr[i + i15], falconFPR6);
            }
        }
    }

    void poly_add(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = 1 << i3;
        for (int i5 = 0; i5 < i4; i5++) {
            falconFPRArr[i + i5] = this.fpr.fpr_add(falconFPRArr[i + i5], falconFPRArr2[i2 + i5]);
        }
    }

    void poly_sub(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = 1 << i3;
        for (int i5 = 0; i5 < i4; i5++) {
            falconFPRArr[i + i5] = this.fpr.fpr_sub(falconFPRArr[i + i5], falconFPRArr2[i2 + i5]);
        }
    }

    void poly_neg(FalconFPR[] falconFPRArr, int i, int i2) {
        int i3 = 1 << i2;
        for (int i4 = 0; i4 < i3; i4++) {
            falconFPRArr[i + i4] = this.fpr.fpr_neg(falconFPRArr[i + i4]);
        }
    }

    void poly_adj_fft(FalconFPR[] falconFPRArr, int i, int i2) {
        int i3 = 1 << i2;
        for (int i4 = i3 >> 1; i4 < i3; i4++) {
            falconFPRArr[i + i4] = this.fpr.fpr_neg(falconFPRArr[i + i4]);
        }
    }

    void poly_mul_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = (1 << i3) >> 1;
        for (int i5 = 0; i5 < i4; i5++) {
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPRArr[i + i5], falconFPRArr[i + i5 + i4], falconFPRArr2[i2 + i5], falconFPRArr2[i2 + i5 + i4]);
            falconFPRArr[i + i5] = complexNumberWrapperFPC_MUL.re;
            falconFPRArr[i + i5 + i4] = complexNumberWrapperFPC_MUL.im;
        }
    }

    void poly_muladj_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = (1 << i3) >> 1;
        for (int i5 = 0; i5 < i4; i5++) {
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPRArr[i + i5], falconFPRArr[i + i5 + i4], falconFPRArr2[i2 + i5], this.fpr.fpr_neg(falconFPRArr2[i2 + i5 + i4]));
            falconFPRArr[i + i5] = complexNumberWrapperFPC_MUL.re;
            falconFPRArr[i + i5 + i4] = complexNumberWrapperFPC_MUL.im;
        }
    }

    void poly_mulselfadj_fft(FalconFPR[] falconFPRArr, int i, int i2) {
        int i3 = (1 << i2) >> 1;
        for (int i4 = 0; i4 < i3; i4++) {
            falconFPRArr[i + i4] = this.fpr.fpr_add(this.fpr.fpr_sqr(falconFPRArr[i + i4]), this.fpr.fpr_sqr(falconFPRArr[i + i4 + i3]));
            falconFPRArr[i + i4 + i3] = this.fpr.fpr_zero;
        }
    }

    void poly_mulconst(FalconFPR[] falconFPRArr, int i, FalconFPR falconFPR, int i2) {
        int i3 = 1 << i2;
        for (int i4 = 0; i4 < i3; i4++) {
            falconFPRArr[i + i4] = this.fpr.fpr_mul(falconFPRArr[i + i4], falconFPR);
        }
    }

    void poly_div_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = (1 << i3) >> 1;
        for (int i5 = 0; i5 < i4; i5++) {
            ComplexNumberWrapper complexNumberWrapperFPC_DIV = FPC_DIV(falconFPRArr[i + i5], falconFPRArr[i + i5 + i4], falconFPRArr2[i2 + i5], falconFPRArr2[i2 + i5 + i4]);
            falconFPRArr[i + i5] = complexNumberWrapperFPC_DIV.re;
            falconFPRArr[i + i5 + i4] = complexNumberWrapperFPC_DIV.im;
        }
    }

    void poly_invnorm2_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, int i4) {
        int i5 = (1 << i4) >> 1;
        for (int i6 = 0; i6 < i5; i6++) {
            falconFPRArr[i + i6] = this.fpr.fpr_inv(this.fpr.fpr_add(this.fpr.fpr_add(this.fpr.fpr_sqr(falconFPRArr2[i2 + i6]), this.fpr.fpr_sqr(falconFPRArr2[i2 + i6 + i5])), this.fpr.fpr_add(this.fpr.fpr_sqr(falconFPRArr3[i3 + i6]), this.fpr.fpr_sqr(falconFPRArr3[i3 + i6 + i5]))));
        }
    }

    void poly_add_muladj_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, FalconFPR[] falconFPRArr4, int i4, FalconFPR[] falconFPRArr5, int i5, int i6) {
        int i7 = (1 << i6) >> 1;
        for (int i8 = 0; i8 < i7; i8++) {
            FalconFPR falconFPR = falconFPRArr2[i2 + i8];
            FalconFPR falconFPR2 = falconFPRArr2[i2 + i8 + i7];
            FalconFPR falconFPR3 = falconFPRArr3[i3 + i8];
            FalconFPR falconFPR4 = falconFPRArr3[i3 + i8 + i7];
            FalconFPR falconFPR5 = falconFPRArr4[i4 + i8];
            FalconFPR falconFPR6 = falconFPRArr4[i4 + i8 + i7];
            FalconFPR falconFPR7 = falconFPRArr5[i5 + i8];
            FalconFPR falconFPR8 = falconFPRArr5[i5 + i8 + i7];
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPR, falconFPR2, falconFPR5, this.fpr.fpr_neg(falconFPR6));
            FalconFPR falconFPR9 = complexNumberWrapperFPC_MUL.re;
            FalconFPR falconFPR10 = complexNumberWrapperFPC_MUL.im;
            ComplexNumberWrapper complexNumberWrapperFPC_MUL2 = FPC_MUL(falconFPR3, falconFPR4, falconFPR7, this.fpr.fpr_neg(falconFPR8));
            FalconFPR falconFPR11 = complexNumberWrapperFPC_MUL2.re;
            FalconFPR falconFPR12 = complexNumberWrapperFPC_MUL2.im;
            falconFPRArr[i + i8] = this.fpr.fpr_add(falconFPR9, falconFPR11);
            falconFPRArr[i + i8 + i7] = this.fpr.fpr_add(falconFPR10, falconFPR12);
        }
    }

    void poly_mul_autoadj_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = (1 << i3) >> 1;
        for (int i5 = 0; i5 < i4; i5++) {
            falconFPRArr[i + i5] = this.fpr.fpr_mul(falconFPRArr[i + i5], falconFPRArr2[i2 + i5]);
            falconFPRArr[i + i5 + i4] = this.fpr.fpr_mul(falconFPRArr[i + i5 + i4], falconFPRArr2[i2 + i5]);
        }
    }

    void poly_div_autoadj_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, int i3) {
        int i4 = (1 << i3) >> 1;
        for (int i5 = 0; i5 < i4; i5++) {
            FalconFPR falconFPRFpr_inv = this.fpr.fpr_inv(falconFPRArr2[i2 + i5]);
            falconFPRArr[i + i5] = this.fpr.fpr_mul(falconFPRArr[i + i5], falconFPRFpr_inv);
            falconFPRArr[i + i5 + i4] = this.fpr.fpr_mul(falconFPRArr[i + i5 + i4], falconFPRFpr_inv);
        }
    }

    void poly_LDL_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, int i4) {
        int i5 = (1 << i4) >> 1;
        for (int i6 = 0; i6 < i5; i6++) {
            FalconFPR falconFPR = falconFPRArr[i + i6];
            FalconFPR falconFPR2 = falconFPRArr[i + i6 + i5];
            FalconFPR falconFPR3 = falconFPRArr2[i2 + i6];
            FalconFPR falconFPR4 = falconFPRArr2[i2 + i6 + i5];
            FalconFPR falconFPR5 = falconFPRArr3[i3 + i6];
            FalconFPR falconFPR6 = falconFPRArr3[i3 + i6 + i5];
            ComplexNumberWrapper complexNumberWrapperFPC_DIV = FPC_DIV(falconFPR3, falconFPR4, falconFPR, falconFPR2);
            FalconFPR falconFPR7 = complexNumberWrapperFPC_DIV.re;
            FalconFPR falconFPR8 = complexNumberWrapperFPC_DIV.im;
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPR7, falconFPR8, falconFPR3, this.fpr.fpr_neg(falconFPR4));
            ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR5, falconFPR6, complexNumberWrapperFPC_MUL.re, complexNumberWrapperFPC_MUL.im);
            falconFPRArr3[i3 + i6] = complexNumberWrapperFPC_SUB.re;
            falconFPRArr3[i3 + i6 + i5] = complexNumberWrapperFPC_SUB.im;
            falconFPRArr2[i2 + i6] = falconFPR7;
            falconFPRArr2[i2 + i6 + i5] = this.fpr.fpr_neg(falconFPR8);
        }
    }

    void poly_LDLmv_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, FalconFPR[] falconFPRArr4, int i4, FalconFPR[] falconFPRArr5, int i5, int i6) {
        int i7 = (1 << i6) >> 1;
        for (int i8 = 0; i8 < i7; i8++) {
            FalconFPR falconFPR = falconFPRArr3[i3 + i8];
            FalconFPR falconFPR2 = falconFPRArr3[i3 + i8 + i7];
            FalconFPR falconFPR3 = falconFPRArr4[i4 + i8];
            FalconFPR falconFPR4 = falconFPRArr4[i4 + i8 + i7];
            FalconFPR falconFPR5 = falconFPRArr5[i5 + i8];
            FalconFPR falconFPR6 = falconFPRArr5[i5 + i8 + i7];
            ComplexNumberWrapper complexNumberWrapperFPC_DIV = FPC_DIV(falconFPR3, falconFPR4, falconFPR, falconFPR2);
            FalconFPR falconFPR7 = complexNumberWrapperFPC_DIV.re;
            FalconFPR falconFPR8 = complexNumberWrapperFPC_DIV.im;
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPR7, falconFPR8, falconFPR3, this.fpr.fpr_neg(falconFPR4));
            ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR5, falconFPR6, complexNumberWrapperFPC_MUL.re, complexNumberWrapperFPC_MUL.im);
            falconFPRArr[i + i8] = complexNumberWrapperFPC_SUB.re;
            falconFPRArr[i + i8 + i7] = complexNumberWrapperFPC_SUB.im;
            falconFPRArr2[i2 + i8] = falconFPR7;
            falconFPRArr2[i2 + i8 + i7] = this.fpr.fpr_neg(falconFPR8);
        }
    }

    void poly_split_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, int i4) {
        int i5 = (1 << i4) >> 1;
        int i6 = i5 >> 1;
        falconFPRArr[i + 0] = falconFPRArr3[i3 + 0];
        falconFPRArr2[i2 + 0] = falconFPRArr3[i3 + i5];
        for (int i7 = 0; i7 < i6; i7++) {
            FalconFPR falconFPR = falconFPRArr3[i3 + (i7 << 1) + 0];
            FalconFPR falconFPR2 = falconFPRArr3[i3 + (i7 << 1) + 0 + i5];
            FalconFPR falconFPR3 = falconFPRArr3[i3 + (i7 << 1) + 1];
            FalconFPR falconFPR4 = falconFPRArr3[i3 + (i7 << 1) + 1 + i5];
            ComplexNumberWrapper complexNumberWrapperFPC_ADD = FPC_ADD(falconFPR, falconFPR2, falconFPR3, falconFPR4);
            FalconFPR falconFPR5 = complexNumberWrapperFPC_ADD.re;
            FalconFPR falconFPR6 = complexNumberWrapperFPC_ADD.im;
            falconFPRArr[i + i7] = this.fpr.fpr_half(falconFPR5);
            falconFPRArr[i + i7 + i6] = this.fpr.fpr_half(falconFPR6);
            ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR, falconFPR2, falconFPR3, falconFPR4);
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(complexNumberWrapperFPC_SUB.re, complexNumberWrapperFPC_SUB.im, this.fpr.fpr_gm_tab[((i7 + i5) << 1) + 0], this.fpr.fpr_neg(this.fpr.fpr_gm_tab[((i7 + i5) << 1) + 1]));
            FalconFPR falconFPR7 = complexNumberWrapperFPC_MUL.re;
            FalconFPR falconFPR8 = complexNumberWrapperFPC_MUL.im;
            falconFPRArr2[i2 + i7] = this.fpr.fpr_half(falconFPR7);
            falconFPRArr2[i2 + i7 + i6] = this.fpr.fpr_half(falconFPR8);
        }
    }

    void poly_merge_fft(FalconFPR[] falconFPRArr, int i, FalconFPR[] falconFPRArr2, int i2, FalconFPR[] falconFPRArr3, int i3, int i4) {
        int i5 = (1 << i4) >> 1;
        int i6 = i5 >> 1;
        falconFPRArr[i + 0] = falconFPRArr2[i2 + 0];
        falconFPRArr[i + i5] = falconFPRArr3[i3 + 0];
        for (int i7 = 0; i7 < i6; i7++) {
            FalconFPR falconFPR = falconFPRArr2[i2 + i7];
            FalconFPR falconFPR2 = falconFPRArr2[i2 + i7 + i6];
            ComplexNumberWrapper complexNumberWrapperFPC_MUL = FPC_MUL(falconFPRArr3[i3 + i7], falconFPRArr3[i3 + i7 + i6], this.fpr.fpr_gm_tab[((i7 + i5) << 1) + 0], this.fpr.fpr_gm_tab[((i7 + i5) << 1) + 1]);
            FalconFPR falconFPR3 = complexNumberWrapperFPC_MUL.re;
            FalconFPR falconFPR4 = complexNumberWrapperFPC_MUL.im;
            ComplexNumberWrapper complexNumberWrapperFPC_ADD = FPC_ADD(falconFPR, falconFPR2, falconFPR3, falconFPR4);
            FalconFPR falconFPR5 = complexNumberWrapperFPC_ADD.re;
            FalconFPR falconFPR6 = complexNumberWrapperFPC_ADD.im;
            falconFPRArr[i + (i7 << 1) + 0] = falconFPR5;
            falconFPRArr[i + (i7 << 1) + 0 + i5] = falconFPR6;
            ComplexNumberWrapper complexNumberWrapperFPC_SUB = FPC_SUB(falconFPR, falconFPR2, falconFPR3, falconFPR4);
            FalconFPR falconFPR7 = complexNumberWrapperFPC_SUB.re;
            FalconFPR falconFPR8 = complexNumberWrapperFPC_SUB.im;
            falconFPRArr[i + (i7 << 1) + 1] = falconFPR7;
            falconFPRArr[i + (i7 << 1) + 1 + i5] = falconFPR8;
        }
    }
}
