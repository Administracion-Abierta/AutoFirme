package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.math.raw.Interleave;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/GF13.class */
final class GF13 extends GF {
    GF13() {
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
            for (int i7 : iArr) {
                int i8 = (i5 - i) + i7;
                iArr2[i8] = iArr2[i8] ^ i6;
            }
        }
        for (int i9 = 0; i9 < i; i9++) {
            sArr[i9] = gf_reduce(iArr2[i9]);
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
            for (int i5 : iArr) {
                int i6 = (i3 - i) + i5;
                iArr2[i6] = iArr2[i6] ^ i4;
            }
        }
        for (int i7 = 0; i7 < i; i7++) {
            sArr[i7] = gf_reduce(iArr2[i7]);
        }
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_frac(short s, short s2) {
        short sGf_sqmul = gf_sqmul(s, s);
        short sGf_sq2mul = gf_sq2mul(sGf_sqmul, sGf_sqmul);
        return gf_sqmul(gf_sq2mul(gf_sq2(gf_sq2mul(gf_sq2(sGf_sq2mul), sGf_sq2mul)), sGf_sq2mul), s2);
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_inv(short s) {
        return gf_frac(s, (short) 1);
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_mul(short s, short s2) {
        int i = s * (s2 & 1);
        for (int i2 = 1; i2 < 13; i2++) {
            i ^= s * (s2 & (1 << i2));
        }
        return gf_reduce(i);
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected int gf_mul_ext(short s, short s2) {
        int i = s * (s2 & 1);
        for (int i2 = 1; i2 < 13; i2++) {
            i ^= s * (s2 & (1 << i2));
        }
        return i;
    }

    private int gf_mul_ext_par(short s, short s2, short s3, short s4) {
        int i = s * (s2 & 1);
        int i2 = s3 * (s4 & 1);
        for (int i3 = 1; i3 < 13; i3++) {
            i ^= s * (s2 & (1 << i3));
            i2 ^= s3 * (s4 & (1 << i3));
        }
        return i ^ i2;
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_reduce(int i) {
        int i2 = i >>> 13;
        int i3 = ((i2 << 4) ^ (i2 << 3)) ^ (i2 << 1);
        int i4 = i3 >>> 13;
        int i5 = i3 & 8191;
        return (short) (((((i & 8191) ^ i2) ^ i4) ^ i5) ^ (((i4 << 4) ^ (i4 << 3)) ^ (i4 << 1)));
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected short gf_sq(short s) {
        return gf_reduce(Interleave.expand16to32(s));
    }

    @Override // org.bouncycastle.pqc.crypto.cmce.GF
    protected int gf_sq_ext(short s) {
        return Interleave.expand16to32(s);
    }

    private short gf_sq2(short s) {
        return gf_reduce(Interleave.expand16to32(gf_reduce(Interleave.expand16to32(s))));
    }

    private short gf_sqmul(short s, short s2) {
        long j = s;
        long j2 = s2;
        long j3 = (j2 << 6) * (j & 64);
        long j4 = j ^ (j << 7);
        long j5 = (((((j3 ^ ((j2 << 0) * (j4 & 16385))) ^ ((j2 << 1) * (j4 & 32770))) ^ ((j2 << 2) * (j4 & 65540))) ^ ((j2 << 3) * (j4 & 131080))) ^ ((j2 << 4) * (j4 & 262160))) ^ ((j2 << 5) * (j4 & 524320));
        long j6 = j5 & 137371844608L;
        return gf_reduce(((int) (j5 ^ ((((j6 >>> 18) ^ (j6 >>> 20)) ^ (j6 >>> 24)) ^ (j6 >>> 26)))) & 67108863);
    }

    private short gf_sq2mul(short s, short s2) {
        long j = s;
        long j2 = s2;
        long j3 = (j2 << 18) * (j & 64);
        long j4 = j ^ (j << 21);
        long j5 = (((((j3 ^ ((j2 << 0) * (j4 & 268435457))) ^ ((j2 << 3) * (j4 & 536870914))) ^ ((j2 << 6) * (j4 & 1073741828))) ^ ((j2 << 9) * (j4 & 2147483656L))) ^ ((j2 << 12) * (j4 & 4294967312L))) ^ ((j2 << 15) * (j4 & 8589934624L));
        long j6 = j5 & 2305834213120671744L;
        long j7 = j5 ^ ((((j6 >>> 18) ^ (j6 >>> 20)) ^ (j6 >>> 24)) ^ (j6 >>> 26));
        long j8 = j7 & 8796025913344L;
        return gf_reduce(((int) (j7 ^ ((((j8 >>> 18) ^ (j8 >>> 20)) ^ (j8 >>> 24)) ^ (j8 >>> 26)))) & 67108863);
    }
}
