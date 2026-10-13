package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/Permute.class */
class Permute {
    private static final int CHACHA_ROUNDS = 12;

    Permute() {
    }

    protected static int rotl(int i, int i2) {
        return (i << i2) | (i >>> (-i2));
    }

    public static void permute(int i, int[] iArr) {
        if (iArr.length != 16) {
            throw new IllegalArgumentException();
        }
        if (i % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int iRotl = iArr[4];
        int iRotl2 = iArr[5];
        int iRotl3 = iArr[6];
        int iRotl4 = iArr[7];
        int i6 = iArr[8];
        int i7 = iArr[9];
        int i8 = iArr[10];
        int i9 = iArr[11];
        int iRotl5 = iArr[12];
        int iRotl6 = iArr[13];
        int iRotl7 = iArr[14];
        int iRotl8 = iArr[15];
        for (int i10 = i; i10 > 0; i10 -= 2) {
            int i11 = i2 + iRotl;
            int iRotl9 = rotl(iRotl5 ^ i11, 16);
            int i12 = i6 + iRotl9;
            int iRotl10 = rotl(iRotl ^ i12, 12);
            int i13 = i11 + iRotl10;
            int iRotl11 = rotl(iRotl9 ^ i13, 8);
            int i14 = i12 + iRotl11;
            int iRotl12 = rotl(iRotl10 ^ i14, 7);
            int i15 = i3 + iRotl2;
            int iRotl13 = rotl(iRotl6 ^ i15, 16);
            int i16 = i7 + iRotl13;
            int iRotl14 = rotl(iRotl2 ^ i16, 12);
            int i17 = i15 + iRotl14;
            int iRotl15 = rotl(iRotl13 ^ i17, 8);
            int i18 = i16 + iRotl15;
            int iRotl16 = rotl(iRotl14 ^ i18, 7);
            int i19 = i4 + iRotl3;
            int iRotl17 = rotl(iRotl7 ^ i19, 16);
            int i20 = i8 + iRotl17;
            int iRotl18 = rotl(iRotl3 ^ i20, 12);
            int i21 = i19 + iRotl18;
            int iRotl19 = rotl(iRotl17 ^ i21, 8);
            int i22 = i20 + iRotl19;
            int iRotl20 = rotl(iRotl18 ^ i22, 7);
            int i23 = i5 + iRotl4;
            int iRotl21 = rotl(iRotl8 ^ i23, 16);
            int i24 = i9 + iRotl21;
            int iRotl22 = rotl(iRotl4 ^ i24, 12);
            int i25 = i23 + iRotl22;
            int iRotl23 = rotl(iRotl21 ^ i25, 8);
            int i26 = i24 + iRotl23;
            int iRotl24 = rotl(iRotl22 ^ i26, 7);
            int i27 = i13 + iRotl16;
            int iRotl25 = rotl(iRotl23 ^ i27, 16);
            int i28 = i22 + iRotl25;
            int iRotl26 = rotl(iRotl16 ^ i28, 12);
            i2 = i27 + iRotl26;
            iRotl8 = rotl(iRotl25 ^ i2, 8);
            i8 = i28 + iRotl8;
            iRotl2 = rotl(iRotl26 ^ i8, 7);
            int i29 = i17 + iRotl20;
            int iRotl27 = rotl(iRotl11 ^ i29, 16);
            int i30 = i26 + iRotl27;
            int iRotl28 = rotl(iRotl20 ^ i30, 12);
            i3 = i29 + iRotl28;
            iRotl5 = rotl(iRotl27 ^ i3, 8);
            i9 = i30 + iRotl5;
            iRotl3 = rotl(iRotl28 ^ i9, 7);
            int i31 = i21 + iRotl24;
            int iRotl29 = rotl(iRotl15 ^ i31, 16);
            int i32 = i14 + iRotl29;
            int iRotl30 = rotl(iRotl24 ^ i32, 12);
            i4 = i31 + iRotl30;
            iRotl6 = rotl(iRotl29 ^ i4, 8);
            i6 = i32 + iRotl6;
            iRotl4 = rotl(iRotl30 ^ i6, 7);
            int i33 = i25 + iRotl12;
            int iRotl31 = rotl(iRotl19 ^ i33, 16);
            int i34 = i18 + iRotl31;
            int iRotl32 = rotl(iRotl12 ^ i34, 12);
            i5 = i33 + iRotl32;
            iRotl7 = rotl(iRotl31 ^ i5, 8);
            i7 = i34 + iRotl7;
            iRotl = rotl(iRotl32 ^ i7, 7);
        }
        iArr[0] = i2;
        iArr[1] = i3;
        iArr[2] = i4;
        iArr[3] = i5;
        iArr[4] = iRotl;
        iArr[5] = iRotl2;
        iArr[6] = iRotl3;
        iArr[7] = iRotl4;
        iArr[8] = i6;
        iArr[9] = i7;
        iArr[10] = i8;
        iArr[11] = i9;
        iArr[12] = iRotl5;
        iArr[13] = iRotl6;
        iArr[14] = iRotl7;
        iArr[15] = iRotl8;
    }

    void chacha_permute(byte[] bArr, byte[] bArr2) {
        int[] iArr = new int[16];
        for (int i = 0; i < 16; i++) {
            iArr[i] = Pack.littleEndianToInt(bArr2, 4 * i);
        }
        permute(12, iArr);
        for (int i2 = 0; i2 < 16; i2++) {
            Pack.intToLittleEndian(iArr[i2], bArr, 4 * i2);
        }
    }
}
