package org.bouncycastle.pqc.crypto.newhope;

import android.R;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/Poly.class */
class Poly {
    Poly() {
    }

    static void add(short[] sArr, short[] sArr2, short[] sArr3) {
        for (int i = 0; i < 1024; i++) {
            sArr3[i] = Reduce.barrett((short) (sArr[i] + sArr2[i]));
        }
    }

    static void fromBytes(short[] sArr, byte[] bArr) {
        for (int i = 0; i < 256; i++) {
            int i2 = 7 * i;
            int i3 = bArr[i2 + 0] & 255;
            int i4 = bArr[i2 + 1] & 255;
            int i5 = bArr[i2 + 2] & 255;
            int i6 = bArr[i2 + 3] & 255;
            int i7 = bArr[i2 + 4] & 255;
            int i8 = bArr[i2 + 5] & 255;
            int i9 = bArr[i2 + 6] & 255;
            int i10 = 4 * i;
            sArr[i10 + 0] = (short) (i3 | ((i4 & 63) << 8));
            sArr[i10 + 1] = (short) ((i4 >>> 6) | (i5 << 2) | ((i6 & 15) << 10));
            sArr[i10 + 2] = (short) ((i6 >>> 4) | (i7 << 4) | ((i8 & 3) << 12));
            sArr[i10 + 3] = (short) ((i8 >>> 2) | (i9 << 6));
        }
    }

    static void fromNTT(short[] sArr) {
        NTT.bitReverse(sArr);
        NTT.core(sArr, Precomp.OMEGAS_INV_MONTGOMERY);
        NTT.mulCoefficients(sArr, Precomp.PSIS_INV_MONTGOMERY);
    }

    static void getNoise(short[] sArr, byte[] bArr, byte b) {
        byte[] bArr2 = new byte[8];
        bArr2[0] = b;
        byte[] bArr3 = new byte[4096];
        ChaCha20.process(bArr, bArr2, bArr3, 0, bArr3.length);
        for (int i = 0; i < 1024; i++) {
            int iBigEndianToInt = Pack.bigEndianToInt(bArr3, i * 4);
            int i2 = 0;
            for (int i3 = 0; i3 < 8; i3++) {
                i2 += (iBigEndianToInt >> i3) & R.attr.cacheColorHint;
            }
            sArr[i] = (short) (((((i2 >>> 24) + (i2 >>> 0)) & 255) + 12289) - (((i2 >>> 16) + (i2 >>> 8)) & 255));
        }
    }

    static void pointWise(short[] sArr, short[] sArr2, short[] sArr3) {
        for (int i = 0; i < 1024; i++) {
            sArr3[i] = Reduce.montgomery((sArr[i] & 65535) * (Reduce.montgomery(3186 * (sArr2[i] & 65535)) & 65535));
        }
    }

    static void toBytes(byte[] bArr, short[] sArr) {
        for (int i = 0; i < 256; i++) {
            int i2 = 4 * i;
            short sNormalize = normalize(sArr[i2 + 0]);
            short sNormalize2 = normalize(sArr[i2 + 1]);
            short sNormalize3 = normalize(sArr[i2 + 2]);
            short sNormalize4 = normalize(sArr[i2 + 3]);
            int i3 = 7 * i;
            bArr[i3 + 0] = (byte) sNormalize;
            bArr[i3 + 1] = (byte) ((sNormalize >> 8) | (sNormalize2 << 6));
            bArr[i3 + 2] = (byte) (sNormalize2 >> 2);
            bArr[i3 + 3] = (byte) ((sNormalize2 >> 10) | (sNormalize3 << 4));
            bArr[i3 + 4] = (byte) (sNormalize3 >> 4);
            bArr[i3 + 5] = (byte) ((sNormalize3 >> 12) | (sNormalize4 << 2));
            bArr[i3 + 6] = (byte) (sNormalize4 >> 6);
        }
    }

    static void toNTT(short[] sArr) {
        NTT.mulCoefficients(sArr, Precomp.PSIS_BITREV_MONTGOMERY);
        NTT.core(sArr, Precomp.OMEGAS_MONTGOMERY);
    }

    static void uniform(short[] sArr, byte[] bArr) {
        SHAKEDigest sHAKEDigest = new SHAKEDigest(128);
        sHAKEDigest.update(bArr, 0, bArr.length);
        int i = 0;
        while (true) {
            byte[] bArr2 = new byte[256];
            sHAKEDigest.doOutput(bArr2, 0, bArr2.length);
            for (int i2 = 0; i2 < bArr2.length; i2 += 2) {
                int i3 = (bArr2[i2] & 255) | ((bArr2[i2 + 1] & 255) << 8);
                if (i3 < 61445) {
                    int i4 = i;
                    i++;
                    sArr[i4] = (short) i3;
                    if (i == 1024) {
                        return;
                    }
                }
            }
        }
    }

    private static short normalize(short s) {
        short sBarrett = Reduce.barrett(s);
        int i = sBarrett - 12289;
        return (short) (i ^ ((sBarrett ^ i) & (i >> 31)));
    }
}
