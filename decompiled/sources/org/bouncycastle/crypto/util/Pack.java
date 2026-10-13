package org.bouncycastle.crypto.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/Pack.class */
public abstract class Pack {
    public static int bigEndianToInt(byte[] bArr, int i) {
        int i2 = bArr[i] << 24;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 16);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 8) | (bArr[i5 + 1] & 255);
    }

    public static void bigEndianToInt(byte[] bArr, int i, int[] iArr) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = bigEndianToInt(bArr, i);
            i += 4;
        }
    }

    public static byte[] intToBigEndian(int i) {
        byte[] bArr = new byte[4];
        intToBigEndian(i, bArr, 0);
        return bArr;
    }

    public static void intToBigEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >>> 24);
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 16);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 8);
        bArr[i4 + 1] = (byte) i;
    }

    public static byte[] intToBigEndian(int[] iArr) {
        byte[] bArr = new byte[4 * iArr.length];
        intToBigEndian(iArr, bArr, 0);
        return bArr;
    }

    public static void intToBigEndian(int[] iArr, byte[] bArr, int i) {
        for (int i2 : iArr) {
            intToBigEndian(i2, bArr, i);
            i += 4;
        }
    }

    public static long bigEndianToLong(byte[] bArr, int i) {
        return ((((long) bigEndianToInt(bArr, i)) & 4294967295L) << 32) | (((long) bigEndianToInt(bArr, i + 4)) & 4294967295L);
    }

    public static void bigEndianToLong(byte[] bArr, int i, long[] jArr) {
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr[i2] = bigEndianToLong(bArr, i);
            i += 8;
        }
    }

    public static byte[] longToBigEndian(long j) {
        byte[] bArr = new byte[8];
        longToBigEndian(j, bArr, 0);
        return bArr;
    }

    public static void longToBigEndian(long j, byte[] bArr, int i) {
        intToBigEndian((int) (j >>> 32), bArr, i);
        intToBigEndian((int) (j & 4294967295L), bArr, i + 4);
    }

    public static byte[] longToBigEndian(long[] jArr) {
        byte[] bArr = new byte[8 * jArr.length];
        longToBigEndian(jArr, bArr, 0);
        return bArr;
    }

    public static void longToBigEndian(long[] jArr, byte[] bArr, int i) {
        for (long j : jArr) {
            longToBigEndian(j, bArr, i);
            i += 8;
        }
    }

    public static int littleEndianToInt(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    public static void littleEndianToInt(byte[] bArr, int i, int[] iArr) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = littleEndianToInt(bArr, i);
            i += 4;
        }
    }

    public static void littleEndianToInt(byte[] bArr, int i, int[] iArr, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i2 + i4] = littleEndianToInt(bArr, i);
            i += 4;
        }
    }

    public static byte[] intToLittleEndian(int i) {
        byte[] bArr = new byte[4];
        intToLittleEndian(i, bArr, 0);
        return bArr;
    }

    public static void intToLittleEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 16);
        bArr[i4 + 1] = (byte) (i >>> 24);
    }

    public static byte[] intToLittleEndian(int[] iArr) {
        byte[] bArr = new byte[4 * iArr.length];
        intToLittleEndian(iArr, bArr, 0);
        return bArr;
    }

    public static void intToLittleEndian(int[] iArr, byte[] bArr, int i) {
        for (int i2 : iArr) {
            intToLittleEndian(i2, bArr, i);
            i += 4;
        }
    }

    public static long littleEndianToLong(byte[] bArr, int i) {
        return ((((long) littleEndianToInt(bArr, i + 4)) & 4294967295L) << 32) | (((long) littleEndianToInt(bArr, i)) & 4294967295L);
    }

    public static void littleEndianToLong(byte[] bArr, int i, long[] jArr) {
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr[i2] = littleEndianToLong(bArr, i);
            i += 8;
        }
    }

    public static byte[] longToLittleEndian(long j) {
        byte[] bArr = new byte[8];
        longToLittleEndian(j, bArr, 0);
        return bArr;
    }

    public static void longToLittleEndian(long j, byte[] bArr, int i) {
        intToLittleEndian((int) (j & 4294967295L), bArr, i);
        intToLittleEndian((int) (j >>> 32), bArr, i + 4);
    }

    public static byte[] longToLittleEndian(long[] jArr) {
        byte[] bArr = new byte[8 * jArr.length];
        longToLittleEndian(jArr, bArr, 0);
        return bArr;
    }

    public static void longToLittleEndian(long[] jArr, byte[] bArr, int i) {
        for (long j : jArr) {
            longToLittleEndian(j, bArr, i);
            i += 8;
        }
    }
}
