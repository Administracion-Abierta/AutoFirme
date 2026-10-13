package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/ByteUtils.class */
public final class ByteUtils {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private ByteUtils() {
    }

    public static boolean equals(byte[] left, byte[] right) {
        if (left == null) {
            return right == null;
        }
        if (right == null || left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= left[i] == right[i];
        }
        return result;
    }

    public static boolean equals(byte[][] left, byte[][] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= equals(left[i], right[i]);
        }
        return result;
    }

    public static boolean equals(byte[][][] left, byte[][][] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            if (left[i].length != right[i].length) {
                return false;
            }
            for (int j = left[i].length - 1; j >= 0; j--) {
                result &= equals(left[i][j], right[i][j]);
            }
        }
        return result;
    }

    public static int deepHashCode(byte[] array) {
        int result = 1;
        for (byte b : array) {
            result = (31 * result) + b;
        }
        return result;
    }

    public static int deepHashCode(byte[][] array) {
        int result = 1;
        for (byte[] bArr : array) {
            result = (31 * result) + deepHashCode(bArr);
        }
        return result;
    }

    public static int deepHashCode(byte[][][] array) {
        int result = 1;
        for (byte[][] bArr : array) {
            result = (31 * result) + deepHashCode(bArr);
        }
        return result;
    }

    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        byte[] result = new byte[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    public static byte[] fromHexString(String s) {
        char[] rawChars = s.toUpperCase().toCharArray();
        int hexChars = 0;
        for (int i = 0; i < rawChars.length; i++) {
            if ((rawChars[i] >= '0' && rawChars[i] <= '9') || (rawChars[i] >= 'A' && rawChars[i] <= 'F')) {
                hexChars++;
            }
        }
        byte[] byteString = new byte[(hexChars + 1) >> 1];
        int pos = hexChars & 1;
        for (int i2 = 0; i2 < rawChars.length; i2++) {
            if (rawChars[i2] >= '0' && rawChars[i2] <= '9') {
                int i3 = pos >> 1;
                byteString[i3] = (byte) (byteString[i3] << 4);
                int i4 = pos >> 1;
                byteString[i4] = (byte) (byteString[i4] | (rawChars[i2] - '0'));
            } else {
                if (rawChars[i2] >= 'A' && rawChars[i2] <= 'F') {
                    int i5 = pos >> 1;
                    byteString[i5] = (byte) (byteString[i5] << 4);
                    int i6 = pos >> 1;
                    byteString[i6] = (byte) (byteString[i6] | ((rawChars[i2] - 'A') + 10));
                }
            }
            pos++;
        }
        return byteString;
    }

    public static String toHexString(byte[] input) {
        String result = "";
        for (int i = 0; i < input.length; i++) {
            result = (result + HEX_CHARS[(input[i] >>> 4) & 15]) + HEX_CHARS[input[i] & 15];
        }
        return result;
    }

    public static String toHexString(byte[] input, String prefix, String seperator) {
        String result = new String(prefix);
        for (int i = 0; i < input.length; i++) {
            result = (result + HEX_CHARS[(input[i] >>> 4) & 15]) + HEX_CHARS[input[i] & 15];
            if (i < input.length - 1) {
                result = result + seperator;
            }
        }
        return result;
    }

    public static String toBinaryString(byte[] input) {
        String result = "";
        for (int i = 0; i < input.length; i++) {
            byte b = input[i];
            for (int ii = 0; ii < 8; ii++) {
                int b2 = (b >>> ii) & 1;
                result = result + b2;
            }
            if (i != input.length - 1) {
                result = result + " ";
            }
        }
        return result;
    }

    public static byte[] xor(byte[] x1, byte[] x2) {
        byte[] out = new byte[x1.length];
        for (int i = x1.length - 1; i >= 0; i--) {
            out[i] = (byte) (x1[i] ^ x2[i]);
        }
        return out;
    }

    public static byte[] concatenate(byte[] x1, byte[] x2) {
        byte[] result = new byte[x1.length + x2.length];
        System.arraycopy(x1, 0, result, 0, x1.length);
        System.arraycopy(x2, 0, result, x1.length, x2.length);
        return result;
    }

    public static byte[] concatenate(byte[][] array) {
        int rowLength = array[0].length;
        byte[] result = new byte[array.length * rowLength];
        int index = 0;
        for (byte[] bArr : array) {
            System.arraycopy(bArr, 0, result, index, rowLength);
            index += rowLength;
        }
        return result;
    }

    public static byte[][] split(byte[] input, int index) throws ArrayIndexOutOfBoundsException {
        if (index > input.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        byte[][] result = {new byte[index], new byte[input.length - index]};
        System.arraycopy(input, 0, result[0], 0, index);
        System.arraycopy(input, index, result[1], 0, input.length - index);
        return result;
    }

    public static byte[] subArray(byte[] input, int start, int end) {
        byte[] result = new byte[end - start];
        System.arraycopy(input, start, result, 0, end - start);
        return result;
    }

    public static byte[] subArray(byte[] input, int start) {
        return subArray(input, start, input.length);
    }

    public static char[] toCharArray(byte[] input) {
        char[] result = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (char) input[i];
        }
        return result;
    }
}
