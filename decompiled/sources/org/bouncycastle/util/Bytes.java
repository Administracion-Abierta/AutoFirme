package org.bouncycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Bytes.class */
public class Bytes {
    public static final int BYTES = 1;
    public static final int SIZE = 8;

    public static void xor(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        for (int i2 = 0; i2 < i; i2++) {
            bArr3[i2] = (byte) (bArr[i2] ^ bArr2[i2]);
        }
    }

    public static void xor(int i, byte[] bArr, int i2, byte[] bArr2, int i3, byte[] bArr3, int i4) {
        for (int i5 = 0; i5 < i; i5++) {
            bArr3[i4 + i5] = (byte) (bArr[i2 + i5] ^ bArr2[i3 + i5]);
        }
    }

    public static void xorTo(int i, byte[] bArr, byte[] bArr2) {
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = i2;
            bArr2[i3] = (byte) (bArr2[i3] ^ bArr[i2]);
        }
    }

    public static void xorTo(int i, byte[] bArr, int i2, byte[] bArr2, int i3) {
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i3 + i4;
            bArr2[i5] = (byte) (bArr2[i5] ^ bArr[i2 + i4]);
        }
    }
}
