package es.gob.jmulticard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/HexUtils.class */
public final class HexUtils {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private HexUtils() {
    }

    public static boolean arrayEquals(byte[] v, byte[] w) {
        return arrayEquals(v, 0, v.length, w, 0, w.length);
    }

    public static boolean arrayEquals(byte[] v, int vOffset, int vLen, byte[] w, int wOffset, int wLen) {
        if (vLen != wLen || v.length < vOffset + vLen || w.length < wOffset + wLen) {
            return false;
        }
        for (int i = 0; i < vLen; i++) {
            if (v[i + vOffset] != w[i + wOffset]) {
                return false;
            }
        }
        return true;
    }

    public static short getShort(byte[] data, int offset) {
        return (short) getUnsignedInt(data, offset);
    }

    public static int getUnsignedInt(byte[] data, int offset) {
        if (data.length - offset < 2) {
            return data[offset] & 255;
        }
        return ((data[offset] & 255) << 8) | (data[offset + 1] & 255);
    }

    public static String hexify(byte[] abyte, boolean separator) {
        if (abyte == null) {
            return "null";
        }
        StringBuilder stringbuilder = new StringBuilder(256);
        int i = 0;
        for (byte element : abyte) {
            if (separator && i > 0) {
                stringbuilder.append('-');
            }
            stringbuilder.append(HEX_CHARS[(element >> 4) & 15]);
            stringbuilder.append(HEX_CHARS[element & 15]);
            i++;
            if (i == 16) {
                if (separator) {
                    stringbuilder.append('\n');
                }
                i = 0;
            }
        }
        return stringbuilder.toString();
    }

    public static byte[] subArray(byte[] src, int srcPos, int length) {
        if (length == 0 || src.length < srcPos + length) {
            return null;
        }
        byte[] temp = new byte[length];
        System.arraycopy(src, srcPos, temp, 0, length);
        return temp;
    }

    public static byte[] xor(byte[] v, byte[] w) {
        byte[] xored = new BigInteger(1, v).xor(new BigInteger(1, w)).toByteArray();
        byte[] trimmedXor = new byte[v.length];
        if (xored.length >= trimmedXor.length) {
            System.arraycopy(xored, xored.length - trimmedXor.length, trimmedXor, 0, trimmedXor.length);
        } else {
            System.arraycopy(xored, 0, trimmedXor, trimmedXor.length - xored.length, xored.length);
        }
        return trimmedXor;
    }

    public static byte[] concatenateByteArrays(byte[]... arrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] array : arrays) {
            try {
                baos.write(array);
            } catch (IOException e) {
                throw new IllegalStateException("Error construyendo el campo de datos", e);
            }
        }
        return baos.toByteArray();
    }

    public static byte[] charArrayToByteArray(char[] inCharArray) {
        if (inCharArray == null) {
            JmcLogger.warning("Se ha pedido convertir un array de caracteres nulo, se devolvera otro vacio de octetos");
            return new byte[0];
        }
        if (inCharArray.length < 1) {
            JmcLogger.warning("El array de caracteres proporcionado esta vacio, se devolvera otro vacio de octetos");
            return new byte[0];
        }
        byte[] ret = new byte[inCharArray.length];
        for (int i = 0; i < inCharArray.length; i++) {
            ret[i] = (byte) inCharArray[i];
        }
        return ret;
    }
}
