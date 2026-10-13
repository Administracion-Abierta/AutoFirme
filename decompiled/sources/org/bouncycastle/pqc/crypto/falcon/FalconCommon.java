package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconCommon.class */
class FalconCommon {
    static final int[] l2bound = {0, 101498, 208714, 428865, 892039, 1852696, 3842630, 7959734, 16468416, 34034726, 70265242};

    FalconCommon() {
    }

    void hash_to_point_vartime(SHAKE256 shake256, short[] sArr, int i, int i2) {
        int i3 = 1 << i2;
        while (i3 > 0) {
            byte[] bArr = new byte[2];
            shake256.inner_shake256_extract(bArr, 0, 2);
            int i4 = ((bArr[0] & 255) << 8) | (bArr[1] & 255);
            if (i4 < 61445) {
                while (i4 >= 12289) {
                    i4 -= 12289;
                }
                int i5 = i;
                i++;
                sArr[i5] = (short) i4;
                i3--;
            }
        }
    }

    void hash_to_point_ct(SHAKE256 shake256, short[] sArr, int i, int i2, short[] sArr2, int i3) {
        char c;
        int i4;
        short s;
        char c2;
        int i5;
        short s2;
        short[] sArr3 = new short[63];
        int i6 = 1 << i2;
        int i7 = i6 << 1;
        short s3 = new short[]{0, 65, 67, 71, 77, 86, 100, 122, 154, 205, 287}[i2];
        int i8 = i6 + s3;
        for (int i9 = 0; i9 < i8; i9++) {
            byte[] bArr = new byte[2];
            shake256.inner_shake256_extract(bArr, 0, bArr.length);
            int i10 = ((bArr[0] & 255) << 8) | (bArr[1] & 255);
            int i11 = i10 - (24578 & (((i10 - 24578) >>> 31) - 1));
            int i12 = i11 - (24578 & (((i11 - 24578) >>> 31) - 1));
            int i13 = (i12 - (12289 & (((i12 - 12289) >>> 31) - 1))) | (((i10 - 61445) >>> 31) - 1);
            if (i9 < i6) {
                sArr[i + i9] = (short) i13;
            } else if (i9 < i7) {
                sArr2[(i3 + i9) - i6] = (short) i13;
            } else {
                sArr3[i9 - i7] = (short) i13;
            }
        }
        int i14 = 1;
        while (true) {
            int i15 = i14;
            if (i15 > s3) {
                return;
            }
            int i16 = 0;
            for (int i17 = 0; i17 < i8; i17++) {
                if (i17 < i6) {
                    c = 1;
                    i4 = i + i17;
                    s = sArr[i4];
                } else if (i17 < i7) {
                    c = 2;
                    i4 = (i3 + i17) - i6;
                    s = sArr2[i4];
                } else {
                    c = 3;
                    i4 = i17 - i7;
                    s = sArr3[i4];
                }
                int i18 = i17 - i16;
                int i19 = (s >>> 15) - 1;
                i16 -= i19;
                if (i17 >= i15) {
                    if (i17 - i15 < i6) {
                        c2 = 1;
                        i5 = (i + i17) - i15;
                        s2 = sArr[i5];
                    } else if (i17 - i15 < i7) {
                        c2 = 2;
                        i5 = (i3 + (i17 - i15)) - i6;
                        s2 = sArr2[i5];
                    } else {
                        c2 = 3;
                        i5 = (i17 - i15) - i7;
                        s2 = sArr3[i5];
                    }
                    int i20 = i19 & (-(((i18 & i15) + 511) >> 9));
                    if (c == 1) {
                        sArr[i4] = (short) (s ^ (i20 & (s ^ s2)));
                    } else if (c == 2) {
                        sArr2[i4] = (short) (s ^ (i20 & (s ^ s2)));
                    } else {
                        sArr3[i4] = (short) (s ^ (i20 & (s ^ s2)));
                    }
                    if (c2 == 1) {
                        sArr[i5] = (short) (s2 ^ (i20 & (s ^ s2)));
                    } else if (c2 == 2) {
                        sArr2[i5] = (short) (s2 ^ (i20 & (s ^ s2)));
                    } else {
                        sArr3[i5] = (short) (s2 ^ (i20 & (s ^ s2)));
                    }
                }
            }
            i14 = i15 << 1;
        }
    }

    int is_short(short[] sArr, int i, short[] sArr2, int i2, int i3) {
        int i4 = 1 << i3;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < i4; i7++) {
            short s = sArr[i + i7];
            int i8 = i5 + (s * s);
            int i9 = i6 | i8;
            short s2 = sArr2[i2 + i7];
            i5 = i8 + (s2 * s2);
            i6 = i9 | i5;
        }
        return (((long) (i5 | (-(i6 >>> 31)))) & 4294967295L) <= ((long) l2bound[i3]) ? 1 : 0;
    }

    int is_short_half(int i, short[] sArr, int i2, int i3) {
        int i4 = 1 << i3;
        int i5 = -(i >>> 31);
        for (int i6 = 0; i6 < i4; i6++) {
            short s = sArr[i2 + i6];
            i += s * s;
            i5 |= i;
        }
        return (((long) (i | (-(i5 >>> 31)))) & 4294967295L) <= ((long) l2bound[i3]) ? 1 : 0;
    }
}
