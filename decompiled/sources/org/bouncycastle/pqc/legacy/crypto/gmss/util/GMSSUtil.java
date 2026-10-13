package org.bouncycastle.pqc.legacy.crypto.gmss.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/util/GMSSUtil.class */
public class GMSSUtil {
    public byte[] intToBytesLittleEndian(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)};
    }

    public int bytesToIntLittleEndian(byte[] bArr) {
        return (bArr[0] & 255) | ((bArr[1] & 255) << 8) | ((bArr[2] & 255) << 16) | ((bArr[3] & 255) << 24);
    }

    public int bytesToIntLittleEndian(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = bArr[i] & 255;
        int i4 = i2 + 1;
        return i3 | ((bArr[i2] & 255) << 8) | ((bArr[i4] & 255) << 16) | ((bArr[i4 + 1] & 255) << 24);
    }

    public byte[] concatenateArray(byte[][] bArr) {
        byte[] bArr2 = new byte[bArr.length * bArr[0].length];
        int length = 0;
        for (int i = 0; i < bArr.length; i++) {
            System.arraycopy(bArr[i], 0, bArr2, length, bArr[i].length);
            length += bArr[i].length;
        }
        return bArr2;
    }

    public void printArray(String str, byte[][] bArr) {
        System.out.println(str);
        int i = 0;
        for (byte[] bArr2 : bArr) {
            for (int i2 = 0; i2 < bArr[0].length; i2++) {
                System.out.println(i + "; " + ((int) bArr2[i2]));
                i++;
            }
        }
    }

    public void printArray(String str, byte[] bArr) {
        System.out.println(str);
        int i = 0;
        for (byte b : bArr) {
            System.out.println(i + "; " + ((int) b));
            i++;
        }
    }

    public boolean testPowerOfTwo(int i) {
        int i2;
        int i3 = 1;
        while (true) {
            i2 = i3;
            if (i2 >= i) {
                break;
            }
            i3 = i2 << 1;
        }
        return i == i2;
    }

    public int getLog(int i) {
        int i2 = 1;
        int i3 = 2;
        while (i3 < i) {
            i3 <<= 1;
            i2++;
        }
        return i2;
    }
}
