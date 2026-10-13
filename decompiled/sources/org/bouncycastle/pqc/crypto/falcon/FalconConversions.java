package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconConversions.class */
class FalconConversions {
    FalconConversions() {
    }

    byte[] int_to_bytes(int i) {
        return new byte[]{(byte) (i >>> 0), (byte) (i >>> 8), (byte) (i >>> 16), (byte) (i >>> 24)};
    }

    int bytes_to_int(byte[] bArr, int i) {
        return (toUnsignedInt(bArr[i + 0]) << 0) | (toUnsignedInt(bArr[i + 1]) << 8) | (toUnsignedInt(bArr[i + 2]) << 16) | (toUnsignedInt(bArr[i + 3]) << 24);
    }

    int[] bytes_to_int_array(byte[] bArr, int i, int i2) {
        int[] iArr = new int[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            iArr[i3] = bytes_to_int(bArr, i + (4 * i3));
        }
        return iArr;
    }

    byte[] long_to_bytes(long j) {
        return new byte[]{(byte) (j >>> 0), (byte) (j >>> 8), (byte) (j >>> 16), (byte) (j >>> 24), (byte) (j >>> 32), (byte) (j >>> 40), (byte) (j >>> 48), (byte) (j >>> 56)};
    }

    long bytes_to_long(byte[] bArr, int i) {
        return (toUnsignedLong(bArr[i + 0]) << 0) | (toUnsignedLong(bArr[i + 1]) << 8) | (toUnsignedLong(bArr[i + 2]) << 16) | (toUnsignedLong(bArr[i + 3]) << 24) | (toUnsignedLong(bArr[i + 4]) << 32) | (toUnsignedLong(bArr[i + 5]) << 40) | (toUnsignedLong(bArr[i + 6]) << 48) | (toUnsignedLong(bArr[i + 7]) << 56);
    }

    private int toUnsignedInt(byte b) {
        return b & 255;
    }

    private long toUnsignedLong(byte b) {
        return ((long) b) & 255;
    }
}
