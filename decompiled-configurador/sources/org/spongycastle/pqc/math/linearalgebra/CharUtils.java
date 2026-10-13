package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/CharUtils.class */
public final class CharUtils {
    private CharUtils() {
    }

    public static char[] clone(char[] array) {
        char[] result = new char[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    public static byte[] toByteArray(char[] chars) {
        byte[] result = new byte[chars.length];
        for (int i = chars.length - 1; i >= 0; i--) {
            result[i] = (byte) chars[i];
        }
        return result;
    }

    public static byte[] toByteArrayForPBE(char[] chars) {
        byte[] out = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            out[i] = (byte) chars[i];
        }
        int length = out.length * 2;
        byte[] ret = new byte[length + 2];
        for (int i2 = 0; i2 < out.length; i2++) {
            int j = i2 * 2;
            ret[j] = 0;
            ret[j + 1] = out[i2];
        }
        ret[length] = 0;
        ret[length + 1] = 0;
        return ret;
    }

    public static boolean equals(char[] left, char[] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= left[i] == right[i];
        }
        return result;
    }
}
