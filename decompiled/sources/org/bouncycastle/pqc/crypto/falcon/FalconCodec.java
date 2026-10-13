package org.bouncycastle.pqc.crypto.falcon;

import com.sun.jna.platform.win32.Advapi32;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconCodec.class */
class FalconCodec {
    final byte[] max_fg_bits = {0, 8, 8, 8, 8, 8, 7, 7, 6, 6, 5};
    final byte[] max_FG_bits = {0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
    final byte[] max_sig_bits = {0, 10, 11, 11, 12, 12, 12, 12, 12, 12, 12};

    FalconCodec() {
    }

    int modq_encode(byte[] bArr, int i, int i2, short[] sArr, int i3, int i4) {
        int i5 = 1 << i4;
        for (int i6 = 0; i6 < i5; i6++) {
            if ((sArr[i3 + i6] & 65535) >= 12289) {
                return 0;
            }
        }
        int i7 = ((i5 * 14) + 7) >> 3;
        if (bArr == null) {
            return i7;
        }
        if (i7 > i2) {
            return 0;
        }
        int i8 = i;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < i5; i11++) {
            i9 = (i9 << 14) | (sArr[i3 + i11] & 65535);
            i10 += 14;
            while (i10 >= 8) {
                i10 -= 8;
                int i12 = i8;
                i8++;
                bArr[i12] = (byte) (i9 >> i10);
            }
        }
        if (i10 > 0) {
            bArr[i8] = (byte) (i9 << (8 - i10));
        }
        return i7;
    }

    int modq_decode(short[] sArr, int i, int i2, byte[] bArr, int i3, int i4) {
        int i5 = 1 << i2;
        int i6 = ((i5 * 14) + 7) >> 3;
        if (i6 > i4) {
            return 0;
        }
        int i7 = i3;
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        while (i10 < i5) {
            int i11 = i7;
            i7++;
            i8 = (i8 << 8) | (bArr[i11] & 255);
            i9 += 8;
            if (i9 >= 14) {
                i9 -= 14;
                int i12 = (i8 >>> i9) & Advapi32.MAX_VALUE_NAME;
                if (i12 >= 12289) {
                    return 0;
                }
                sArr[i + i10] = (short) i12;
                i10++;
            }
        }
        if ((i8 & ((1 << i9) - 1)) != 0) {
            return 0;
        }
        return i6;
    }

    int trim_i16_encode(byte[] bArr, int i, int i2, short[] sArr, int i3, int i4, int i5) {
        int i6 = 1 << i4;
        int i7 = (1 << (i5 - 1)) - 1;
        int i8 = -i7;
        for (int i9 = 0; i9 < i6; i9++) {
            if (sArr[i3 + i9] < i8 || sArr[i3 + i9] > i7) {
                return 0;
            }
        }
        int i10 = ((i6 * i5) + 7) >> 3;
        if (bArr == null) {
            return i10;
        }
        if (i10 > i2) {
            return 0;
        }
        int i11 = i;
        int i12 = 0;
        int i13 = 0;
        int i14 = (1 << i5) - 1;
        for (int i15 = 0; i15 < i6; i15++) {
            i12 = (i12 << i5) | (sArr[i3 + i15] & 4095 & i14);
            i13 += i5;
            while (i13 >= 8) {
                i13 -= 8;
                int i16 = i11;
                i11++;
                bArr[i16] = (byte) (i12 >> i13);
            }
        }
        if (i13 > 0) {
            int i17 = i11;
            int i18 = i11 + 1;
            bArr[i17] = (byte) (i12 << (8 - i13));
        }
        return i10;
    }

    int trim_i16_decode(short[] sArr, int i, int i2, int i3, byte[] bArr, int i4, int i5) {
        int i6 = 1 << i2;
        int i7 = ((i6 * i3) + 7) >> 3;
        if (i7 > i5) {
            return 0;
        }
        int i8 = i4;
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        int i12 = (1 << i3) - 1;
        int i13 = 1 << (i3 - 1);
        while (i9 < i6) {
            int i14 = i8;
            i8++;
            i10 = (i10 << 8) | (bArr[i14] & 255);
            i11 += 8;
            while (i11 >= i3 && i9 < i6) {
                i11 -= i3;
                int i15 = (i10 >>> i11) & i12;
                int i16 = i15 | (-(i15 & i13));
                if (i16 == (-i13)) {
                    return 0;
                }
                sArr[i + i9] = (short) (i16 | (-(i16 & i13)));
                i9++;
            }
        }
        if ((i10 & ((1 << i11) - 1)) != 0) {
            return 0;
        }
        return i7;
    }

    int trim_i8_encode(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4, int i5) {
        int i6 = 1 << i4;
        int i7 = (1 << (i5 - 1)) - 1;
        int i8 = -i7;
        for (int i9 = 0; i9 < i6; i9++) {
            if (bArr2[i3 + i9] < i8 || bArr2[i3 + i9] > i7) {
                return 0;
            }
        }
        int i10 = ((i6 * i5) + 7) >> 3;
        if (bArr == null) {
            return i10;
        }
        if (i10 > i2) {
            return 0;
        }
        int i11 = i;
        int i12 = 0;
        int i13 = 0;
        int i14 = (1 << i5) - 1;
        for (int i15 = 0; i15 < i6; i15++) {
            i12 = (i12 << i5) | (bArr2[i3 + i15] & 65535 & i14);
            i13 += i5;
            while (i13 >= 8) {
                i13 -= 8;
                int i16 = i11;
                i11++;
                bArr[i16] = (byte) (i12 >>> i13);
            }
        }
        if (i13 > 0) {
            int i17 = i11;
            int i18 = i11 + 1;
            bArr[i17] = (byte) (i12 << (8 - i13));
        }
        return i10;
    }

    int trim_i8_decode(byte[] bArr, int i, int i2, int i3, byte[] bArr2, int i4, int i5) {
        int i6 = 1 << i2;
        int i7 = ((i6 * i3) + 7) >> 3;
        if (i7 > i5) {
            return 0;
        }
        int i8 = i4;
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        int i12 = (1 << i3) - 1;
        int i13 = 1 << (i3 - 1);
        while (i9 < i6) {
            int i14 = i8;
            i8++;
            i10 = (i10 << 8) | (bArr2[i14] & 255);
            i11 += 8;
            while (i11 >= i3 && i9 < i6) {
                i11 -= i3;
                int i15 = (i10 >>> i11) & i12;
                int i16 = i15 | (-(i15 & i13));
                if (i16 == (-i13)) {
                    return 0;
                }
                bArr[i + i9] = (byte) i16;
                i9++;
            }
        }
        if ((i10 & ((1 << i11) - 1)) != 0) {
            return 0;
        }
        return i7;
    }

    int comp_encode(byte[] bArr, int i, int i2, short[] sArr, int i3, int i4) {
        int i5 = 1 << i4;
        for (int i6 = 0; i6 < i5; i6++) {
            if (sArr[i3 + i6] < -2047 || sArr[i3 + i6] > 2047) {
                return 0;
            }
        }
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = 0; i10 < i5; i10++) {
            int i11 = i7 << 1;
            int i12 = sArr[i3 + i10];
            if (i12 < 0) {
                i12 = -i12;
                i11 |= 1;
            }
            int i13 = i12;
            int i14 = (i11 << 7) | (i13 & 127);
            int i15 = i13 >>> 7;
            i7 = (i14 << (i15 + 1)) | 1;
            i8 = i8 + 8 + i15 + 1;
            while (i8 >= 8) {
                i8 -= 8;
                if (bArr != null) {
                    if (i9 >= i2) {
                        return 0;
                    }
                    bArr[i + i9] = (byte) (i7 >>> i8);
                }
                i9++;
            }
        }
        if (i8 > 0) {
            if (bArr != null) {
                if (i9 >= i2) {
                    return 0;
                }
                bArr[i + i9] = (byte) (i7 << (8 - i8));
            }
            i9++;
        }
        return i9;
    }

    int comp_decode(short[] sArr, int i, int i2, byte[] bArr, int i3, int i4) {
        int i5 = 1 << i2;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 0; i9 < i5; i9++) {
            if (i8 >= i4) {
                return 0;
            }
            i6 = (i6 << 8) | (bArr[i3 + i8] & 255);
            i8++;
            int i10 = i6 >>> i7;
            int i11 = i10 & 128;
            int i12 = i10 & 127;
            do {
                if (i7 == 0) {
                    if (i8 >= i4) {
                        return 0;
                    }
                    i6 = (i6 << 8) | (bArr[i3 + i8] & 255);
                    i8++;
                    i7 = 8;
                }
                i7--;
                if (((i6 >>> i7) & 1) == 0) {
                    i12 += 128;
                } else {
                    if (i11 != 0 && i12 == 0) {
                        return 0;
                    }
                    sArr[i + i9] = (short) (i11 != 0 ? -i12 : i12);
                }
            } while (i12 <= 2047);
            return 0;
        }
        if ((i6 & ((1 << i7) - 1)) != 0) {
            return 0;
        }
        return i8;
    }
}
