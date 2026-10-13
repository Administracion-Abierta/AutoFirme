package org.bouncycastle.pqc.legacy.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/BigEndianConversions.class */
public final class BigEndianConversions {
    private BigEndianConversions() {
    }

    public static byte[] I2OSP(int i) {
        return new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i};
    }

    public static byte[] I2OSP(int i, int i2) throws ArithmeticException {
        if (i < 0) {
            return null;
        }
        int iCeilLog256 = IntegerFunctions.ceilLog256(i);
        if (iCeilLog256 > i2) {
            throw new ArithmeticException("Cannot encode given integer into specified number of octets.");
        }
        byte[] bArr = new byte[i2];
        for (int i3 = i2 - 1; i3 >= i2 - iCeilLog256; i3--) {
            bArr[i3] = (byte) (i >>> (8 * ((i2 - 1) - i3)));
        }
        return bArr;
    }

    public static void I2OSP(int i, byte[] bArr, int i2) {
        int i3 = i2 + 1;
        bArr[i2] = (byte) (i >>> 24);
        int i4 = i3 + 1;
        bArr[i3] = (byte) (i >>> 16);
        bArr[i4] = (byte) (i >>> 8);
        bArr[i4 + 1] = (byte) i;
    }

    public static byte[] I2OSP(long j) {
        return new byte[]{(byte) (j >>> 56), (byte) (j >>> 48), (byte) (j >>> 40), (byte) (j >>> 32), (byte) (j >>> 24), (byte) (j >>> 16), (byte) (j >>> 8), (byte) j};
    }

    public static void I2OSP(long j, byte[] bArr, int i) {
        int i2 = i + 1;
        bArr[i] = (byte) (j >>> 56);
        int i3 = i2 + 1;
        bArr[i2] = (byte) (j >>> 48);
        int i4 = i3 + 1;
        bArr[i3] = (byte) (j >>> 40);
        int i5 = i4 + 1;
        bArr[i4] = (byte) (j >>> 32);
        int i6 = i5 + 1;
        bArr[i5] = (byte) (j >>> 24);
        int i7 = i6 + 1;
        bArr[i6] = (byte) (j >>> 16);
        bArr[i7] = (byte) (j >>> 8);
        bArr[i7 + 1] = (byte) j;
    }

    public static void I2OSP(int i, byte[] bArr, int i2, int i3) {
        for (int i4 = i3 - 1; i4 >= 0; i4--) {
            bArr[i2 + i4] = (byte) (i >>> (8 * ((i3 - 1) - i4)));
        }
    }

    public static int OS2IP(byte[] bArr) {
        if (bArr.length > 4) {
            throw new ArithmeticException("invalid input length");
        }
        if (bArr.length == 0) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < bArr.length; i++) {
            length |= (bArr[i] & 255) << (8 * ((bArr.length - 1) - i));
        }
        return length;
    }

    public static int OS2IP(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = (bArr[i] & 255) << 24;
        int i4 = i2 + 1;
        return i3 | ((bArr[i2] & 255) << 16) | ((bArr[i4] & 255) << 8) | (bArr[i4 + 1] & 255);
    }

    public static int OS2IP(byte[] bArr, int i, int i2) {
        if (bArr.length == 0 || bArr.length < (i + i2) - 1) {
            return 0;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            i3 |= (bArr[i + i4] & 255) << (8 * ((i2 - i4) - 1));
        }
        return i3;
    }

    public static long OS2LIP(byte[] bArr, int i) {
        int i2 = i + 1;
        long j = (((long) bArr[i]) & 255) << 56;
        int i3 = i2 + 1;
        long j2 = j | ((((long) bArr[i2]) & 255) << 48);
        int i4 = i3 + 1;
        long j3 = j2 | ((((long) bArr[i3]) & 255) << 40);
        int i5 = i4 + 1;
        long j4 = j3 | ((((long) bArr[i4]) & 255) << 32);
        int i6 = i5 + 1;
        long j5 = j4 | ((((long) bArr[i5]) & 255) << 24);
        int i7 = i6 + 1;
        return j5 | ((long) ((bArr[i6] & 255) << 16)) | ((long) ((bArr[i7] & 255) << 8)) | ((long) (bArr[i7 + 1] & 255));
    }

    public static byte[] toByteArray(int[] iArr) {
        byte[] bArr = new byte[iArr.length << 2];
        for (int i = 0; i < iArr.length; i++) {
            I2OSP(iArr[i], bArr, i << 2);
        }
        return bArr;
    }

    public static byte[] toByteArray(int[] iArr, int i) {
        int length = iArr.length;
        byte[] bArr = new byte[i];
        int i2 = 0;
        int i3 = 0;
        while (i3 <= length - 2) {
            I2OSP(iArr[i3], bArr, i2);
            i3++;
            i2 += 4;
        }
        I2OSP(iArr[length - 1], bArr, i2, i - i2);
        return bArr;
    }

    public static int[] toIntArray(byte[] bArr) {
        int length = (bArr.length + 3) / 4;
        int length2 = bArr.length & 3;
        int[] iArr = new int[length];
        int i = 0;
        int i2 = 0;
        while (i2 <= length - 2) {
            iArr[i2] = OS2IP(bArr, i);
            i2++;
            i += 4;
        }
        if (length2 != 0) {
            iArr[length - 1] = OS2IP(bArr, i, length2);
        } else {
            iArr[length - 1] = OS2IP(bArr, i);
        }
        return iArr;
    }
}
