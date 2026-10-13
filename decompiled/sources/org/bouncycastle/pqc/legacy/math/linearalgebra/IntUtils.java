package org.bouncycastle.pqc.legacy.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/IntUtils.class */
public final class IntUtils {
    private IntUtils() {
    }

    public static boolean equals(int[] iArr, int[] iArr2) {
        if (iArr.length != iArr2.length) {
            return false;
        }
        boolean z = true;
        for (int length = iArr.length - 1; length >= 0; length--) {
            z &= iArr[length] == iArr2[length];
        }
        return z;
    }

    public static int[] clone(int[] iArr) {
        int[] iArr2 = new int[iArr.length];
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        return iArr2;
    }

    public static void fill(int[] iArr, int i) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            iArr[length] = i;
        }
    }

    public static void quicksort(int[] iArr) {
        quicksort(iArr, 0, iArr.length - 1);
    }

    public static void quicksort(int[] iArr, int i, int i2) {
        if (i2 > i) {
            int iPartition = partition(iArr, i, i2, i2);
            quicksort(iArr, i, iPartition - 1);
            quicksort(iArr, iPartition + 1, i2);
        }
    }

    private static int partition(int[] iArr, int i, int i2, int i3) {
        int i4 = iArr[i3];
        iArr[i3] = iArr[i2];
        iArr[i2] = i4;
        int i5 = i;
        for (int i6 = i; i6 < i2; i6++) {
            if (iArr[i6] <= i4) {
                int i7 = iArr[i5];
                iArr[i5] = iArr[i6];
                iArr[i6] = i7;
                i5++;
            }
        }
        int i8 = iArr[i5];
        iArr[i5] = iArr[i2];
        iArr[i2] = i8;
        return i5;
    }

    public static int[] subArray(int[] iArr, int i, int i2) {
        int[] iArr2 = new int[i2 - i];
        System.arraycopy(iArr, i, iArr2, 0, i2 - i);
        return iArr2;
    }

    public static String toString(int[] iArr) {
        String str = "";
        for (int i : iArr) {
            str = str + i + " ";
        }
        return str;
    }

    public static String toHexString(int[] iArr) {
        return ByteUtils.toHexString(BigEndianConversions.toByteArray(iArr));
    }
}
