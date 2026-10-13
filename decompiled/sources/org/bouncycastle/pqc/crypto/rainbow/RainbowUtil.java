package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowUtil.class */
class RainbowUtil {
    RainbowUtil() {
    }

    public static short[] convertArray(byte[] bArr) {
        short[] sArr = new short[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            sArr[i] = (short) (bArr[i] & 255);
        }
        return sArr;
    }

    public static byte[] convertArray(short[] sArr) {
        byte[] bArr = new byte[sArr.length];
        for (int i = 0; i < sArr.length; i++) {
            bArr[i] = (byte) sArr[i];
        }
        return bArr;
    }

    public static boolean equals(short[] sArr, short[] sArr2) {
        if (sArr.length != sArr2.length) {
            return false;
        }
        boolean z = true;
        for (int length = sArr.length - 1; length >= 0; length--) {
            z &= sArr[length] == sArr2[length];
        }
        return z;
    }

    public static boolean equals(short[][] sArr, short[][] sArr2) {
        if (sArr.length != sArr2.length) {
            return false;
        }
        boolean zEquals = true;
        for (int length = sArr.length - 1; length >= 0; length--) {
            zEquals &= equals(sArr[length], sArr2[length]);
        }
        return zEquals;
    }

    public static boolean equals(short[][][] sArr, short[][][] sArr2) {
        if (sArr.length != sArr2.length) {
            return false;
        }
        boolean zEquals = true;
        for (int length = sArr.length - 1; length >= 0; length--) {
            zEquals &= equals(sArr[length], sArr2[length]);
        }
        return zEquals;
    }

    public static short[][] cloneArray(short[][] sArr) {
        short[][] sArr2 = new short[sArr.length][];
        for (int i = 0; i < sArr.length; i++) {
            sArr2[i] = Arrays.clone(sArr[i]);
        }
        return sArr2;
    }

    public static short[][][] cloneArray(short[][][] sArr) {
        short[][][] sArr2 = new short[sArr.length][sArr[0].length][];
        for (int i = 0; i < sArr.length; i++) {
            for (int i2 = 0; i2 < sArr[0].length; i2++) {
                sArr2[i][i2] = Arrays.clone(sArr[i][i2]);
            }
        }
        return sArr2;
    }

    public static byte[] hash(Digest digest, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i;
        int digestSize = digest.getDigestSize();
        digest.update(bArr, 0, bArr.length);
        digest.update(bArr2, 0, bArr2.length);
        if (bArr3.length == digestSize) {
            digest.doFinal(bArr3, 0);
            return bArr3;
        }
        byte[] bArr4 = new byte[digestSize];
        digest.doFinal(bArr4, 0);
        if (bArr3.length < digestSize) {
            System.arraycopy(bArr4, 0, bArr3, 0, bArr3.length);
            return bArr3;
        }
        System.arraycopy(bArr4, 0, bArr3, 0, bArr4.length);
        int length = bArr3.length - digestSize;
        int length2 = digestSize;
        while (true) {
            i = length2;
            if (length < bArr4.length) {
                break;
            }
            digest.update(bArr4, 0, bArr4.length);
            digest.doFinal(bArr4, 0);
            System.arraycopy(bArr4, 0, bArr3, i, bArr4.length);
            length -= bArr4.length;
            length2 = i + bArr4.length;
        }
        if (length > 0) {
            digest.update(bArr4, 0, bArr4.length);
            digest.doFinal(bArr4, 0);
            System.arraycopy(bArr4, 0, bArr3, i, length);
        }
        return bArr3;
    }

    public static byte[] hash(Digest digest, byte[] bArr, int i) {
        int i2;
        int digestSize = digest.getDigestSize();
        digest.update(bArr, 0, bArr.length);
        byte[] bArr2 = new byte[digestSize];
        digest.doFinal(bArr2, 0);
        if (i == digestSize) {
            return bArr2;
        }
        if (i < digestSize) {
            return Arrays.copyOf(bArr2, i);
        }
        byte[] bArrCopyOf = Arrays.copyOf(bArr2, digestSize);
        int i3 = i;
        while (true) {
            i2 = i3 - digestSize;
            if (i2 < digestSize) {
                break;
            }
            digest.update(bArr2, 0, digestSize);
            bArr2 = new byte[digestSize];
            digest.doFinal(bArr2, 0);
            bArrCopyOf = Arrays.concatenate(bArrCopyOf, bArr2);
            i3 = i2;
        }
        if (i2 > 0) {
            digest.update(bArr2, 0, digestSize);
            byte[] bArr3 = new byte[digestSize];
            digest.doFinal(bArr3, 0);
            int length = bArrCopyOf.length;
            bArrCopyOf = Arrays.copyOf(bArrCopyOf, length + i2);
            System.arraycopy(bArr3, 0, bArrCopyOf, length, i2);
        }
        return bArrCopyOf;
    }

    public static short[][] generate_random_2d(SecureRandom secureRandom, int i, int i2) {
        byte[] bArr = new byte[i * i2];
        secureRandom.nextBytes(bArr);
        short[][] sArr = new short[i][i2];
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                sArr[i4][i3] = (short) (bArr[(i3 * i) + i4] & 255);
            }
        }
        return sArr;
    }

    public static short[][][] generate_random(SecureRandom secureRandom, int i, int i2, int i3, boolean z) {
        byte[] bArr = new byte[z ? i * ((i2 * (i2 + 1)) / 2) : i * i2 * i3];
        secureRandom.nextBytes(bArr);
        int i4 = 0;
        short[][][] sArr = new short[i][i2][i3];
        for (int i5 = 0; i5 < i2; i5++) {
            for (int i6 = 0; i6 < i3; i6++) {
                for (int i7 = 0; i7 < i; i7++) {
                    if (!z || i5 <= i6) {
                        int i8 = i4;
                        i4++;
                        sArr[i7][i5][i6] = (short) (bArr[i8] & 255);
                    }
                }
            }
        }
        return sArr;
    }

    public static byte[] getEncoded(short[][] sArr) {
        int length = sArr.length;
        int length2 = sArr[0].length;
        byte[] bArr = new byte[length * length2];
        for (int i = 0; i < length2; i++) {
            for (int i2 = 0; i2 < length; i2++) {
                bArr[(i * length) + i2] = (byte) sArr[i2][i];
            }
        }
        return bArr;
    }

    public static byte[] getEncoded(short[][][] sArr, boolean z) {
        int length = sArr.length;
        int length2 = sArr[0].length;
        int length3 = sArr[0][0].length;
        byte[] bArr = new byte[z ? length * ((length2 * (length2 + 1)) / 2) : length * length2 * length3];
        int i = 0;
        for (int i2 = 0; i2 < length2; i2++) {
            for (int i3 = 0; i3 < length3; i3++) {
                for (short[][] sArr2 : sArr) {
                    if (!z || i2 <= i3) {
                        bArr[i] = (byte) sArr2[i2][i3];
                        i++;
                    }
                }
            }
        }
        return bArr;
    }

    public static int loadEncoded(short[][] sArr, byte[] bArr, int i) {
        int length = sArr.length;
        int length2 = sArr[0].length;
        for (int i2 = 0; i2 < length2; i2++) {
            for (int i3 = 0; i3 < length; i3++) {
                sArr[i3][i2] = (short) (bArr[i + (i2 * length) + i3] & 255);
            }
        }
        return length * length2;
    }

    public static int loadEncoded(short[][][] sArr, byte[] bArr, int i, boolean z) {
        int length = sArr[0].length;
        int length2 = sArr[0][0].length;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            for (int i4 = 0; i4 < length2; i4++) {
                for (short[][] sArr2 : sArr) {
                    if (!z || i3 <= i4) {
                        int i5 = i2;
                        i2++;
                        sArr2[i3][i4] = (short) (bArr[i + i5] & 255);
                    }
                }
            }
        }
        return i2;
    }
}
