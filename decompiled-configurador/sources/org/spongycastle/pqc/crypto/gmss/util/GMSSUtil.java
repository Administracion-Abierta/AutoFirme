package org.spongycastle.pqc.crypto.gmss.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/util/GMSSUtil.class */
public class GMSSUtil {
    public byte[] intToBytesLittleEndian(int value) {
        byte[] bytes = {(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        return bytes;
    }

    public int bytesToIntLittleEndian(byte[] bytes) {
        return (bytes[0] & 255) | ((bytes[1] & 255) << 8) | ((bytes[2] & 255) << 16) | ((bytes[3] & 255) << 24);
    }

    public int bytesToIntLittleEndian(byte[] bytes, int offset) {
        int offset2 = offset + 1;
        int i = bytes[offset] & 255;
        int offset3 = offset2 + 1;
        return i | ((bytes[offset2] & 255) << 8) | ((bytes[offset3] & 255) << 16) | ((bytes[offset3 + 1] & 255) << 24);
    }

    public byte[] concatenateArray(byte[][] arraycp) {
        byte[] dest = new byte[arraycp.length * arraycp[0].length];
        int indx = 0;
        for (int i = 0; i < arraycp.length; i++) {
            System.arraycopy(arraycp[i], 0, dest, indx, arraycp[i].length);
            indx += arraycp[i].length;
        }
        return dest;
    }

    public void printArray(String text, byte[][] array) {
        System.out.println(text);
        int counter = 0;
        for (byte[] bArr : array) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.println(counter + "; " + ((int) bArr[j]));
                counter++;
            }
        }
    }

    public void printArray(String text, byte[] array) {
        System.out.println(text);
        int counter = 0;
        for (byte b : array) {
            System.out.println(counter + "; " + ((int) b));
            counter++;
        }
    }

    public boolean testPowerOfTwo(int testValue) {
        int a;
        int i = 1;
        while (true) {
            a = i;
            if (a >= testValue) {
                break;
            }
            i = a << 1;
        }
        if (testValue == a) {
            return true;
        }
        return false;
    }

    public int getLog(int intValue) {
        int log = 1;
        int i = 2;
        while (i < intValue) {
            i <<= 1;
            log++;
        }
        return log;
    }
}
