package org.spongycastle.pqc.crypto.rainbow.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/rainbow/util/RainbowUtil.class */
public class RainbowUtil {
    public static int[] convertArraytoInt(byte[] in) {
        int[] out = new int[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] & 255;
        }
        return out;
    }

    public static short[] convertArray(byte[] in) {
        short[] out = new short[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (short) (in[i] & 255);
        }
        return out;
    }

    public static short[][] convertArray(byte[][] in) {
        short[][] out = new short[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                out[i][j] = (short) (in[i][j] & 255);
            }
        }
        return out;
    }

    public static short[][][] convertArray(byte[][][] in) {
        short[][][] out = new short[in.length][in[0].length][in[0][0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                for (int k = 0; k < in[0][0].length; k++) {
                    out[i][j][k] = (short) (in[i][j][k] & 255);
                }
            }
        }
        return out;
    }

    public static byte[] convertIntArray(int[] in) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) in[i];
        }
        return out;
    }

    public static byte[] convertArray(short[] in) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) in[i];
        }
        return out;
    }

    public static byte[][] convertArray(short[][] in) {
        byte[][] out = new byte[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                out[i][j] = (byte) in[i][j];
            }
        }
        return out;
    }

    public static byte[][][] convertArray(short[][][] in) {
        byte[][][] out = new byte[in.length][in[0].length][in[0][0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                for (int k = 0; k < in[0][0].length; k++) {
                    out[i][j][k] = (byte) in[i][j][k];
                }
            }
        }
        return out;
    }

    public static boolean equals(short[] left, short[] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= left[i] == right[i];
        }
        return result;
    }

    public static boolean equals(short[][] left, short[][] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= equals(left[i], right[i]);
        }
        return result;
    }

    public static boolean equals(short[][][] left, short[][][] right) {
        if (left.length != right.length) {
            return false;
        }
        boolean result = true;
        for (int i = left.length - 1; i >= 0; i--) {
            result &= equals(left[i], right[i]);
        }
        return result;
    }
}
