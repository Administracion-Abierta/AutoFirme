package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.math.raw.Interleave;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/GF12.class */
final class GF12 extends GF {
    GF12() {
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected void gf_mul_poly(int i, int[] iArr, short[] sArr, short[] sArr2, short[] sArr3, int[] iArr2) {
        iArr2[0] = gf_mul_ext(sArr2[0], sArr3[0]);
        for (int i2 = 1; i2 < i; i2++) {
            iArr2[(i2 + i2) - 1] = 0;
            short s = sArr2[i2];
            short s2 = sArr3[i2];
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = i2 + i3;
                iArr2[i4] = iArr2[i4] ^ gf_mul_ext_par(s, sArr3[i3], sArr2[i3], s2);
            }
            iArr2[i2 + i2] = gf_mul_ext(s, s2);
        }
        for (int i5 = (i - 1) * 2; i5 >= i; i5--) {
            int i6 = iArr2[i5];
            for (int i7 = 0; i7 < iArr.length - 1; i7++) {
                int i8 = (i5 - i) + iArr[i7];
                iArr2[i8] = iArr2[i8] ^ i6;
            }
            int i9 = i5 - i;
            iArr2[i9] = iArr2[i9] ^ (i6 << 1);
        }
        for (int i10 = 0; i10 < i; i10++) {
            sArr[i10] = gf_reduce(iArr2[i10]);
        }
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected void gf_sqr_poly(int i, int[] iArr, short[] sArr, short[] sArr2, int[] iArr2) {
        iArr2[0] = gf_sq_ext(sArr2[0]);
        for (int i2 = 1; i2 < i; i2++) {
            iArr2[(i2 + i2) - 1] = 0;
            iArr2[i2 + i2] = gf_sq_ext(sArr2[i2]);
        }
        for (int i3 = (i - 1) * 2; i3 >= i; i3--) {
            int i4 = iArr2[i3];
            for (int i5 = 0; i5 < iArr.length - 1; i5++) {
                int i6 = (i3 - i) + iArr[i5];
                iArr2[i6] = iArr2[i6] ^ i4;
            }
            int i7 = i3 - i;
            iArr2[i7] = iArr2[i7] ^ (i4 << 1);
        }
        for (int i8 = 0; i8 < i; i8++) {
            sArr[i8] = gf_reduce(iArr2[i8]);
        }
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_frac(short s, short s2) {
        return gf_mul(gf_inv(s), s2);
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_inv(short s) {
        short sGf_mul = gf_mul(gf_sq(s), s);
        short sGf_mul2 = gf_mul(gf_sq(gf_sq(sGf_mul)), sGf_mul);
        return gf_sq(gf_mul(gf_sq(gf_mul(gf_sq(gf_sq(gf_mul(gf_sq(gf_sq(gf_sq(gf_sq(sGf_mul2)))), sGf_mul2))), sGf_mul)), s));
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_mul(short s, short s2) {
        int i = s * (s2 & 1);
        for (int i2 = 1; i2 < 12; i2++) {
            i ^= s * (s2 & (1 << i2));
        }
        return gf_reduce(i);
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected int gf_mul_ext(short s, short s2) {
        int i = s * (s2 & 1);
        for (int i2 = 1; i2 < 12; i2++) {
            i ^= s * (s2 & (1 << i2));
        }
        return i;
    }

    private int gf_mul_ext_par(short s, short s2, short s3, short s4) {
        int i = s * (s2 & 1);
        int i2 = s3 * (s4 & 1);
        for (int i3 = 1; i3 < 12; i3++) {
            i ^= s * (s2 & (1 << i3));
            i2 ^= s3 * (s4 & (1 << i3));
        }
        return i ^ i2;
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_reduce(int i) {
        int i2 = (i & 14680064) >>> 18;
        return (short) (((((i & 4095) ^ (i >>> 12)) ^ ((i & 2093056) >>> 9)) ^ i2) ^ (i >>> 21));
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_sq(short s) {
        return gf_reduce(Interleave.expand16to32(s));
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected int gf_sq_ext(short s) {
        return Interleave.expand16to32(s);
    }
}
