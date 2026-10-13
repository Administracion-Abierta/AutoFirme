package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/BigIntUtils.class */
public final class BigIntUtils {
    private BigIntUtils() {
    }

    public static boolean equals(BigInteger[] bigIntegerArr, BigInteger[] bigIntegerArr2) {
        int iCompareTo = 0;
        if (bigIntegerArr.length != bigIntegerArr2.length) {
            return false;
        }
        for (int i = 0; i < bigIntegerArr.length; i++) {
            iCompareTo |= bigIntegerArr[i].compareTo(bigIntegerArr2[i]);
        }
        return iCompareTo == 0;
    }

    public static void fill(BigInteger[] bigIntegerArr, BigInteger bigInteger) {
        for (int length = bigIntegerArr.length - 1; length >= 0; length--) {
            bigIntegerArr[length] = bigInteger;
        }
    }

    public static BigInteger[] subArray(BigInteger[] bigIntegerArr, int i, int i2) {
        BigInteger[] bigIntegerArr2 = new BigInteger[i2 - i];
        System.arraycopy(bigIntegerArr, i, bigIntegerArr2, 0, i2 - i);
        return bigIntegerArr2;
    }

    public static int[] toIntArray(BigInteger[] bigIntegerArr) {
        int[] iArr = new int[bigIntegerArr.length];
        for (int i = 0; i < bigIntegerArr.length; i++) {
            iArr[i] = bigIntegerArr[i].intValue();
        }
        return iArr;
    }

    public static int[] toIntArrayModQ(int i, BigInteger[] bigIntegerArr) {
        BigInteger bigIntegerValueOf = BigInteger.valueOf(i);
        int[] iArr = new int[bigIntegerArr.length];
        for (int i2 = 0; i2 < bigIntegerArr.length; i2++) {
            iArr[i2] = bigIntegerArr[i2].mod(bigIntegerValueOf).intValue();
        }
        return iArr;
    }

    public static byte[] toMinimalByteArray(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == 1 || (bigInteger.bitLength() & 7) != 0) {
            return byteArray;
        }
        byte[] bArr = new byte[bigInteger.bitLength() >> 3];
        System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
        return bArr;
    }
}
