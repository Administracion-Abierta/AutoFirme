package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/SamplerZ.class */
class SamplerZ {
    FPREngine fpr = new FPREngine();

    SamplerZ() {
    }

    int sample(SamplerCtx samplerCtx, FalconFPR falconFPR, FalconFPR falconFPR2) {
        return sampler(samplerCtx, falconFPR, falconFPR2);
    }

    int gaussian0_sampler(FalconRNG falconRNG) {
        int[] iArr = {10745844, 3068844, 3741698, 5559083, 1580863, 8248194, 2260429, 13669192, 2736639, 708981, 4421575, 10046180, 169348, 7122675, 4136815, 30538, 13063405, 7650655, 4132, 14505003, 7826148, 417, 16768101, 11363290, 31, 8444042, 8086568, 1, 12844466, 265321, 0, 1232676, 13644283, 0, 38047, 9111839, 0, 870, 6138264, 0, 14, 12545723, 0, 0, 3104126, 0, 0, 28824, 0, 0, 198, 0, 0, 1};
        long jPrng_get_u64 = falconRNG.prng_get_u64();
        int i = ((int) jPrng_get_u64) & 16777215;
        int i2 = ((int) (jPrng_get_u64 >>> 24)) & 16777215;
        int iPrng_get_u8 = ((int) (jPrng_get_u64 >>> 48)) | ((falconRNG.prng_get_u8() & 255) << 16);
        int i3 = 0;
        for (int i4 = 0; i4 < iArr.length; i4 += 3) {
            int i5 = iArr[i4 + 2];
            i3 += ((iPrng_get_u8 - iArr[i4 + 0]) - (((i2 - iArr[i4 + 1]) - ((i - i5) >>> 31)) >>> 31)) >>> 31;
        }
        return i3;
    }

    int BerExp(FalconRNG falconRNG, FalconFPR falconFPR, FalconFPR falconFPR2) {
        int iPrng_get_u8;
        int iFpr_trunc = (int) this.fpr.fpr_trunc(this.fpr.fpr_mul(falconFPR, this.fpr.fpr_inv_log2));
        long jFpr_expm_p63 = ((this.fpr.fpr_expm_p63(this.fpr.fpr_sub(falconFPR, this.fpr.fpr_mul(this.fpr.fpr_of(iFpr_trunc), this.fpr.fpr_log2)), falconFPR2) << 1) - 1) >>> (iFpr_trunc ^ ((iFpr_trunc ^ 63) & (-((63 - iFpr_trunc) >>> 31))));
        int i = 64;
        do {
            i -= 8;
            iPrng_get_u8 = (falconRNG.prng_get_u8() & 255) - (((int) (jFpr_expm_p63 >>> i)) & 255);
            if (iPrng_get_u8 != 0) {
                break;
            }
        } while (i > 0);
        return iPrng_get_u8 >>> 31;
    }

    int sampler(SamplerCtx samplerCtx, FalconFPR falconFPR, FalconFPR falconFPR2) {
        int iGaussian0_sampler;
        int i;
        int iFpr_floor = (int) this.fpr.fpr_floor(falconFPR);
        FalconFPR falconFPRFpr_sub = this.fpr.fpr_sub(falconFPR, this.fpr.fpr_of(iFpr_floor));
        FalconFPR falconFPRFpr_half = this.fpr.fpr_half(this.fpr.fpr_sqr(falconFPR2));
        FalconFPR falconFPRFpr_mul = this.fpr.fpr_mul(falconFPR2, samplerCtx.sigma_min);
        do {
            iGaussian0_sampler = gaussian0_sampler(samplerCtx.p);
            int iPrng_get_u8 = samplerCtx.p.prng_get_u8() & 255 & 1;
            i = iPrng_get_u8 + (((iPrng_get_u8 << 1) - 1) * iGaussian0_sampler);
        } while (BerExp(samplerCtx.p, this.fpr.fpr_sub(this.fpr.fpr_mul(this.fpr.fpr_sqr(this.fpr.fpr_sub(this.fpr.fpr_of(i), falconFPRFpr_sub)), falconFPRFpr_half), this.fpr.fpr_mul(this.fpr.fpr_of(iGaussian0_sampler * iGaussian0_sampler), this.fpr.fpr_inv_2sqrsigma0)), falconFPRFpr_mul) == 0);
        return iFpr_floor + i;
    }
}
