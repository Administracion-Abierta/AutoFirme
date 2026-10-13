package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/IntUtils.class */
public final class IntUtils {
    private IntUtils() {
    }

    public static boolean equals(int[] left, int[] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= left[i] == right[i];
        }
        return result;
    }

    public static int[] clone(int[] array) {
        int[] result = new int[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    public static void fill(int[] array, int value) {
        for (int i = array.length - 1; i >= 0; i--) {
            array[i] = value;
        }
    }

    public static void quicksort(int[] source) {
        quicksort(source, 0, source.length - 1);
    }

    public static void quicksort(int[] source, int left, int right) {
        if (right > left) {
            int index = partition(source, left, right, right);
            quicksort(source, left, index - 1);
            quicksort(source, index + 1, right);
        }
    }

    private static int partition(int[] source, int left, int right, int pivotIndex) {
        int pivot = source[pivotIndex];
        source[pivotIndex] = source[right];
        source[right] = pivot;
        int index = left;
        for (int i = left; i < right; i++) {
            if (source[i] <= pivot) {
                int tmp = source[index];
                source[index] = source[i];
                source[i] = tmp;
                index++;
            }
        }
        int tmp2 = source[index];
        source[index] = source[right];
        source[right] = tmp2;
        return index;
    }

    public static int[] subArray(int[] input, int start, int end) {
        int[] result = new int[end - start];
        System.arraycopy(input, start, result, 0, end - start);
        return result;
    }

    public static String toString(int[] input) {
        String result = "";
        for (int i : input) {
            result = result + i + " ";
        }
        return result;
    }

    public static String toHexString(int[] input) {
        return ByteUtils.toHexString(BigEndianConversions.toByteArray(input));
    }
}
