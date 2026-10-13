package org.bouncycastle.pqc.crypto.mlkem;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/CBD.class */
final class CBD {
    CBD() {
    }

    public static void mlkemCBD(Poly poly, byte[] bArr, int i) {
        switch (i) {
            case 3:
                for (int i2 = 0; i2 < 64; i2++) {
                    long jConvertByteTo24BitUnsignedInt = convertByteTo24BitUnsignedInt(bArr, 3 * i2);
                    long j = (jConvertByteTo24BitUnsignedInt & 2396745) + ((jConvertByteTo24BitUnsignedInt >> 1) & 2396745) + ((jConvertByteTo24BitUnsignedInt >> 2) & 2396745);
                    for (int i3 = 0; i3 < 4; i3++) {
                        poly.setCoeffIndex((4 * i2) + i3, (short) (((short) ((j >> ((6 * i3) + 0)) & 7)) - ((short) ((j >> ((6 * i3) + 3)) & 7))));
                    }
                }
                break;
            default:
                for (int i4 = 0; i4 < 32; i4++) {
                    long jConvertByteTo32BitUnsignedInt = convertByteTo32BitUnsignedInt(bArr, 4 * i4);
                    long j2 = (jConvertByteTo32BitUnsignedInt & 1431655765) + ((jConvertByteTo32BitUnsignedInt >> 1) & 1431655765);
                    for (int i5 = 0; i5 < 8; i5++) {
                        poly.setCoeffIndex((8 * i4) + i5, (short) (((short) ((j2 >> ((4 * i5) + 0)) & 3)) - ((short) ((j2 >> ((4 * i5) + i)) & 3))));
                    }
                }
                break;
        }
    }

    private static long convertByteTo32BitUnsignedInt(byte[] bArr, int i) {
        return ((long) (bArr[i] & 255)) | (((long) (bArr[i + 1] & 255)) << 8) | (((long) (bArr[i + 2] & 255)) << 16) | (((long) (bArr[i + 3] & 255)) << 24);
    }

    private static long convertByteTo24BitUnsignedInt(byte[] bArr, int i) {
        return ((long) (bArr[i] & 255)) | (((long) (bArr[i + 1] & 255)) << 8) | (((long) (bArr[i + 2] & 255)) << 16);
    }
}
