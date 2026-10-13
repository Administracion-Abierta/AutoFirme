package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/Utils.class */
class Utils {
    Utils() {
    }

    static void resizeArray(long[] jArr, int i, long[] jArr2, int i2, int i3, int i4) {
        if (i >= i2) {
            System.arraycopy(jArr2, 0, jArr, 0, (i2 + 7) / 8);
            return;
        }
        int i5 = i % 64 != 0 ? 64 - (i % 64) : 0;
        System.arraycopy(jArr2, 0, jArr, 0, i3);
        for (int i6 = 0; i6 < i5; i6++) {
            int i7 = i4 - 1;
            jArr[i7] = jArr[i7] & (9223372036854775807 >> i6);
        }
    }

    static void fromByte16ArrayToLongArray(long[] jArr, int[] iArr) {
        for (int i = 0; i != iArr.length; i += 4) {
            jArr[i / 4] = ((long) iArr[i]) & 65535;
            int i2 = i / 4;
            jArr[i2] = jArr[i2] | (((long) iArr[i + 1]) << 16);
            int i3 = i / 4;
            jArr[i3] = jArr[i3] | (((long) iArr[i + 2]) << 32);
            int i4 = i / 4;
            jArr[i4] = jArr[i4] | (((long) iArr[i + 3]) << 48);
        }
    }

    static void fromByteArrayToByte16Array(int[] iArr, byte[] bArr) {
        byte[] bArr2 = bArr;
        if (bArr.length % 2 != 0) {
            bArr2 = new byte[((bArr.length + 1) / 2) * 2];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        }
        int i = 0;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = Pack.littleEndianToShort(bArr2, i) & 65535;
            i += 2;
        }
    }

    static void fromLongArrayToByteArray(byte[] bArr, long[] jArr) {
        int length = bArr.length / 8;
        for (int i = 0; i != length; i++) {
            Pack.longToLittleEndian(jArr[i], bArr, i * 8);
        }
        if (bArr.length % 8 != 0) {
            int i2 = length * 8;
            int i3 = 0;
            while (i2 < bArr.length) {
                int i4 = i2;
                i2++;
                int i5 = i3;
                i3++;
                bArr[i4] = (byte) (jArr[length] >>> (i5 * 8));
            }
        }
    }

    static long bitMask(long j, long j2) {
        return (1 << ((int) (j % j2))) - 1;
    }

    static void fromByteArrayToLongArray(long[] jArr, byte[] bArr) {
        byte[] bArr2 = bArr;
        if (bArr.length % 8 != 0) {
            bArr2 = new byte[((bArr.length + 7) / 8) * 8];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        }
        int i = 0;
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr[i2] = Pack.littleEndianToLong(bArr2, i);
            i += 8;
        }
    }

    static void fromByte32ArrayToLongArray(long[] jArr, int[] iArr) {
        for (int i = 0; i != iArr.length; i += 2) {
            jArr[i / 2] = ((long) iArr[i]) & 4294967295L;
            int i2 = i / 2;
            jArr[i2] = jArr[i2] | (((long) iArr[i + 1]) << 32);
        }
    }

    static void fromLongArrayToByte32Array(int[] iArr, long[] jArr) {
        for (int i = 0; i != jArr.length; i++) {
            iArr[2 * i] = (int) jArr[i];
            iArr[(2 * i) + 1] = (int) (jArr[i] >> 32);
        }
    }

    static void copyBytes(int[] iArr, int i, int[] iArr2, int i2, int i3) {
        System.arraycopy(iArr, i, iArr2, i2, i3 / 2);
    }

    static int getByteSizeFromBitSize(int i) {
        return (i + 7) / 8;
    }

    static int getByte64SizeFromBitSize(int i) {
        return (i + 63) / 64;
    }

    static int toUnsigned8bits(int i) {
        return i & 255;
    }

    static int toUnsigned16Bits(int i) {
        return i & 65535;
    }

    static void xorLongToByte16Array(int[] iArr, long j, int i) {
        int i2 = i + 0;
        iArr[i2] = iArr[i2] ^ (((int) j) & 65535);
        int i3 = i + 1;
        iArr[i3] = iArr[i3] ^ (((int) (j >>> 16)) & 65535);
        int i4 = i + 2;
        iArr[i4] = iArr[i4] ^ (((int) (j >>> 32)) & 65535);
        int i5 = i + 3;
        iArr[i5] = iArr[i5] ^ (((int) (j >>> 48)) & 65535);
    }
}
