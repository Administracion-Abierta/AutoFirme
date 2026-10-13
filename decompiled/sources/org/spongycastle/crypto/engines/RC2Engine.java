package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.RC2Parameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RC2Engine.class */
public class RC2Engine implements BlockCipher {
    private static byte[] piTable = {-39, 120, -7, -60, 25, -35, -75, -19, 40, -23, -3, 121, 74, -96, -40, -99, -58, 126, 55, -125, 43, 118, 83, -114, 98, 76, 100, -120, 68, -117, -5, -94, 23, -102, 89, -11, -121, -77, 79, 19, 97, 69, 109, -115, 9, -127, 125, 50, -67, -113, 64, -21, -122, -73, 123, 11, -16, -107, 33, 34, 92, 107, 78, -126, 84, -42, 101, -109, -50, 96, -78, 28, 115, 86, -64, 20, -89, -116, -15, -36, 18, 117, -54, 31, 59, -66, -28, -47, 66, 61, -44, 48, -93, 60, -74, 38, 111, -65, 14, -38, 70, 105, 7, 87, 39, -14, 29, -101, -68, -108, 67, 3, -8, 17, -57, -10, -112, -17, 62, -25, 6, -61, -43, 47, -56, 102, 30, -41, 8, -24, -22, -34, -128, 82, -18, -9, -124, -86, 114, -84, 53, 77, 106, 42, -106, 26, -46, 113, 90, 21, 73, 116, 75, -97, -48, 94, 4, 24, -92, -20, -62, -32, 65, 110, 15, 81, -53, -52, 36, -111, -81, 80, -95, -12, 112, 57, -103, 124, 58, -123, 35, -72, -76, 122, -4, 2, 54, 91, 37, 85, -105, 49, 45, 93, -6, -104, -29, -118, -110, -82, 5, -33, 41, 16, 103, 108, -70, -55, -45, 0, -26, -49, -31, -98, -88, 44, 99, 22, 1, 63, 88, -30, -119, -87, 13, 56, 52, 27, -85, 51, -1, -80, -69, 72, 12, 95, -71, -79, -51, 46, -59, -13, -37, 71, -27, -91, -100, 119, 10, -90, 32, 104, -2, 127, -63, -83};
    private static final int BLOCK_SIZE = 8;
    private int[] workingKey;
    private boolean encrypting;

    private int[] generateWorkingKey(byte[] key, int bits) {
        int[] xKey = new int[128];
        for (int i = 0; i != key.length; i++) {
            xKey[i] = key[i] & 255;
        }
        int len = key.length;
        if (len < 128) {
            int index = 0;
            int x = xKey[len - 1];
            do {
                int i2 = index;
                index++;
                x = piTable[(x + xKey[i2]) & 255] & 255;
                int i3 = len;
                len++;
                xKey[i3] = x;
            } while (len < 128);
        }
        int len2 = (bits + 7) >> 3;
        int x2 = piTable[xKey[128 - len2] & (255 >> (7 & (-bits)))] & 255;
        xKey[128 - len2] = x2;
        for (int i4 = (128 - len2) - 1; i4 >= 0; i4--) {
            x2 = piTable[x2 ^ xKey[i4 + len2]] & 255;
            xKey[i4] = x2;
        }
        int[] newKey = new int[64];
        for (int i5 = 0; i5 != newKey.length; i5++) {
            newKey[i5] = xKey[2 * i5] + (xKey[(2 * i5) + 1] << 8);
        }
        return newKey;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) {
        this.encrypting = encrypting;
        if (params instanceof RC2Parameters) {
            RC2Parameters param = (RC2Parameters) params;
            this.workingKey = generateWorkingKey(param.getKey(), param.getEffectiveKeyBits());
        } else {
            if (params instanceof KeyParameter) {
                byte[] key = ((KeyParameter) params).getKey();
                this.workingKey = generateWorkingKey(key, key.length * 8);
                return;
            }
            throw new IllegalArgumentException("invalid parameter passed to RC2 init - " + params.getClass().getName());
        }
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC2";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public final int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("RC2 engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            encryptBlock(in, inOff, out, outOff);
            return 8;
        }
        decryptBlock(in, inOff, out, outOff);
        return 8;
    }

    private int rotateWordLeft(int x, int y) {
        int x2 = x & 65535;
        return (x2 << y) | (x2 >> (16 - y));
    }

    private void encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int x76 = ((in[inOff + 7] & 255) << 8) + (in[inOff + 6] & 255);
        int x54 = ((in[inOff + 5] & 255) << 8) + (in[inOff + 4] & 255);
        int x32 = ((in[inOff + 3] & 255) << 8) + (in[inOff + 2] & 255);
        int x10 = ((in[inOff + 1] & 255) << 8) + (in[inOff + 0] & 255);
        for (int i = 0; i <= 16; i += 4) {
            x10 = rotateWordLeft(x10 + (x32 & (x76 ^ (-1))) + (x54 & x76) + this.workingKey[i], 1);
            x32 = rotateWordLeft(x32 + (x54 & (x10 ^ (-1))) + (x76 & x10) + this.workingKey[i + 1], 2);
            x54 = rotateWordLeft(x54 + (x76 & (x32 ^ (-1))) + (x10 & x32) + this.workingKey[i + 2], 3);
            x76 = rotateWordLeft(x76 + (x10 & (x54 ^ (-1))) + (x32 & x54) + this.workingKey[i + 3], 5);
        }
        int x11 = x10 + this.workingKey[x76 & 63];
        int x33 = x32 + this.workingKey[x11 & 63];
        int x55 = x54 + this.workingKey[x33 & 63];
        int x77 = x76 + this.workingKey[x55 & 63];
        for (int i2 = 20; i2 <= 40; i2 += 4) {
            x11 = rotateWordLeft(x11 + (x33 & (x77 ^ (-1))) + (x55 & x77) + this.workingKey[i2], 1);
            x33 = rotateWordLeft(x33 + (x55 & (x11 ^ (-1))) + (x77 & x11) + this.workingKey[i2 + 1], 2);
            x55 = rotateWordLeft(x55 + (x77 & (x33 ^ (-1))) + (x11 & x33) + this.workingKey[i2 + 2], 3);
            x77 = rotateWordLeft(x77 + (x11 & (x55 ^ (-1))) + (x33 & x55) + this.workingKey[i2 + 3], 5);
        }
        int x12 = x11 + this.workingKey[x77 & 63];
        int x34 = x33 + this.workingKey[x12 & 63];
        int x56 = x55 + this.workingKey[x34 & 63];
        int x78 = x77 + this.workingKey[x56 & 63];
        for (int i3 = 44; i3 < 64; i3 += 4) {
            x12 = rotateWordLeft(x12 + (x34 & (x78 ^ (-1))) + (x56 & x78) + this.workingKey[i3], 1);
            x34 = rotateWordLeft(x34 + (x56 & (x12 ^ (-1))) + (x78 & x12) + this.workingKey[i3 + 1], 2);
            x56 = rotateWordLeft(x56 + (x78 & (x34 ^ (-1))) + (x12 & x34) + this.workingKey[i3 + 2], 3);
            x78 = rotateWordLeft(x78 + (x12 & (x56 ^ (-1))) + (x34 & x56) + this.workingKey[i3 + 3], 5);
        }
        out[outOff + 0] = (byte) x12;
        out[outOff + 1] = (byte) (x12 >> 8);
        out[outOff + 2] = (byte) x34;
        out[outOff + 3] = (byte) (x34 >> 8);
        out[outOff + 4] = (byte) x56;
        out[outOff + 5] = (byte) (x56 >> 8);
        out[outOff + 6] = (byte) x78;
        out[outOff + 7] = (byte) (x78 >> 8);
    }

    private void decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int x76 = ((in[inOff + 7] & 255) << 8) + (in[inOff + 6] & 255);
        int x54 = ((in[inOff + 5] & 255) << 8) + (in[inOff + 4] & 255);
        int x32 = ((in[inOff + 3] & 255) << 8) + (in[inOff + 2] & 255);
        int x10 = ((in[inOff + 1] & 255) << 8) + (in[inOff + 0] & 255);
        for (int i = 60; i >= 44; i -= 4) {
            x76 = rotateWordLeft(x76, 11) - (((x10 & (x54 ^ (-1))) + (x32 & x54)) + this.workingKey[i + 3]);
            x54 = rotateWordLeft(x54, 13) - (((x76 & (x32 ^ (-1))) + (x10 & x32)) + this.workingKey[i + 2]);
            x32 = rotateWordLeft(x32, 14) - (((x54 & (x10 ^ (-1))) + (x76 & x10)) + this.workingKey[i + 1]);
            x10 = rotateWordLeft(x10, 15) - (((x32 & (x76 ^ (-1))) + (x54 & x76)) + this.workingKey[i]);
        }
        int x77 = x76 - this.workingKey[x54 & 63];
        int x55 = x54 - this.workingKey[x32 & 63];
        int x33 = x32 - this.workingKey[x10 & 63];
        int x11 = x10 - this.workingKey[x77 & 63];
        for (int i2 = 40; i2 >= 20; i2 -= 4) {
            x77 = rotateWordLeft(x77, 11) - (((x11 & (x55 ^ (-1))) + (x33 & x55)) + this.workingKey[i2 + 3]);
            x55 = rotateWordLeft(x55, 13) - (((x77 & (x33 ^ (-1))) + (x11 & x33)) + this.workingKey[i2 + 2]);
            x33 = rotateWordLeft(x33, 14) - (((x55 & (x11 ^ (-1))) + (x77 & x11)) + this.workingKey[i2 + 1]);
            x11 = rotateWordLeft(x11, 15) - (((x33 & (x77 ^ (-1))) + (x55 & x77)) + this.workingKey[i2]);
        }
        int x78 = x77 - this.workingKey[x55 & 63];
        int x56 = x55 - this.workingKey[x33 & 63];
        int x34 = x33 - this.workingKey[x11 & 63];
        int x12 = x11 - this.workingKey[x78 & 63];
        for (int i3 = 16; i3 >= 0; i3 -= 4) {
            x78 = rotateWordLeft(x78, 11) - (((x12 & (x56 ^ (-1))) + (x34 & x56)) + this.workingKey[i3 + 3]);
            x56 = rotateWordLeft(x56, 13) - (((x78 & (x34 ^ (-1))) + (x12 & x34)) + this.workingKey[i3 + 2]);
            x34 = rotateWordLeft(x34, 14) - (((x56 & (x12 ^ (-1))) + (x78 & x12)) + this.workingKey[i3 + 1]);
            x12 = rotateWordLeft(x12, 15) - (((x34 & (x78 ^ (-1))) + (x56 & x78)) + this.workingKey[i3]);
        }
        out[outOff + 0] = (byte) x12;
        out[outOff + 1] = (byte) (x12 >> 8);
        out[outOff + 2] = (byte) x34;
        out[outOff + 3] = (byte) (x34 >> 8);
        out[outOff + 4] = (byte) x56;
        out[outOff + 5] = (byte) (x56 >> 8);
        out[outOff + 6] = (byte) x78;
        out[outOff + 7] = (byte) (x78 >> 8);
    }
}
