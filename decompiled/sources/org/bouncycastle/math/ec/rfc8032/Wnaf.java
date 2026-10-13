package org.bouncycastle.math.ec.rfc8032;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Wnaf.class */
abstract class Wnaf {
    Wnaf() {
    }

    static void getSignedVar(int[] iArr, int i, byte[] bArr) {
        int[] iArr2 = new int[iArr.length * 2];
        int i2 = iArr[iArr.length - 1] >> 31;
        int length = iArr.length;
        int length2 = iArr2.length;
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            int i3 = iArr[length];
            int i4 = length2 - 1;
            iArr2[i4] = (i3 >>> 16) | (i2 << 16);
            length2 = i4 - 1;
            i2 = i3;
            iArr2[length2] = i3;
        }
        int i5 = 32 - i;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i8 < iArr2.length) {
            int i9 = iArr2[i8];
            while (i6 < 16) {
                int i10 = i9 >>> i6;
                if ((i10 & 1) == i7) {
                    i6++;
                } else {
                    int i11 = (i10 | 1) << i5;
                    i7 = i11 >>> 31;
                    bArr[(i8 << 4) + i6] = (byte) (i11 >> i5);
                    i6 += i;
                }
            }
            i8++;
            i6 -= 16;
        }
    }
}
