package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/bike/BIKEUtils.class */
class BIKEUtils {
    BIKEUtils() {
    }

    static int getHammingWeight(byte[] bArr) {
        int i = 0;
        for (byte b : bArr) {
            i += b;
        }
        return i;
    }

    static void fromBitArrayToByteArray(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3 = 0;
        int i4 = 0;
        long j = i2;
        while (i3 < j) {
            if (i3 + 8 >= i2) {
                int i5 = bArr2[i + i3];
                for (int i6 = (i2 - i3) - 1; i6 >= 1; i6--) {
                    i5 |= bArr2[(i + i3) + i6] << i6;
                }
                bArr[i4] = (byte) i5;
            } else {
                int i7 = bArr2[i + i3];
                for (int i8 = 7; i8 >= 1; i8--) {
                    i7 |= bArr2[(i + i3) + i8] << i8;
                }
                bArr[i4] = (byte) i7;
            }
            i3 += 8;
            i4++;
        }
    }

    static void generateRandomByteArray(byte[] bArr, int i, int i2, Xof xof) {
        byte[] bArr2 = new byte[4];
        for (int i3 = i2 - 1; i3 >= 0; i3--) {
            xof.doOutput(bArr2, 0, 4);
            int iLittleEndianToInt = ((int) (((((long) Pack.littleEndianToInt(bArr2, 0)) & 4294967295L) * ((long) (i - i3))) >> 32)) + i3;
            if (CHECK_BIT(bArr, iLittleEndianToInt) != 0) {
                iLittleEndianToInt = i3;
            }
            SET_BIT(bArr, iLittleEndianToInt);
        }
    }

    protected static int CHECK_BIT(byte[] bArr, int i) {
        return (bArr[i / 8] >>> (i % 8)) & 1;
    }

    protected static void SET_BIT(byte[] bArr, int i) {
        int i2 = i / 8;
        bArr[i2] = (byte) (((long) bArr[i2]) | (1 << (i % 8)));
    }
}
