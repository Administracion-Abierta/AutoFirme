package org.bouncycastle.math.ec.rfc8032;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Codec.class */
abstract class Codec {
    Codec() {
    }

    static int decode16(byte[] bArr, int i) {
        return (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8);
    }

    static int decode24(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        return i2 | ((bArr[i3] & 255) << 8) | ((bArr[i3 + 1] & 255) << 16);
    }

    static int decode32(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    static void decode32(byte[] bArr, int i, int[] iArr, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i2 + i4] = decode32(bArr, i + (i4 * 4));
        }
    }

    static void encode24(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        bArr[i3 + 1] = (byte) (i >>> 16);
    }

    static void encode32(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 16);
        bArr[i4 + 1] = (byte) (i >>> 24);
    }

    static void encode32(int[] iArr, int i, int i2, byte[] bArr, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            encode32(iArr[i + i4], bArr, i3 + (i4 * 4));
        }
    }

    static void encode56(long j, byte[] bArr, int i) {
        encode32((int) j, bArr, i);
        encode24((int) (j >>> 32), bArr, i + 4);
    }
}
