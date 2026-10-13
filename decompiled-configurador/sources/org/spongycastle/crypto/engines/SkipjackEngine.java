package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/SkipjackEngine.class */
public class SkipjackEngine implements BlockCipher {
    static final int BLOCK_SIZE = 8;
    static short[] ftable = {163, 215, 9, 131, 248, 72, 246, 244, 179, 33, 21, 120, 153, 177, 175, 249, 231, 45, 77, 138, 206, 76, 202, 46, 82, 149, 217, 30, 78, 56, 68, 40, 10, 223, 2, 160, 23, 241, 96, 104, 18, 183, 122, 195, 233, 250, 61, 83, 150, 132, 107, 186, 242, 99, 154, 25, 124, 174, 229, 245, 247, 22, 106, 162, 57, 182, 123, 15, 193, 147, 129, 27, 238, 180, 26, 234, 208, 145, 47, 184, 85, 185, 218, 133, 63, 65, 191, 224, 90, 88, 128, 95, 102, 11, 216, 144, 53, 213, 192, 167, 51, 6, 101, 105, 69, 0, 148, 86, 109, 152, 155, 118, 151, 252, 178, 194, 176, 254, 219, 32, 225, 235, 214, 228, 221, 71, 74, 29, 66, 237, 158, 110, 73, 60, 205, 67, 39, 210, 7, 212, 222, 199, 103, 24, 137, 203, 48, 31, 141, 198, 143, 170, 200, 116, 220, 201, 93, 92, 49, 164, 112, 136, 97, 44, 159, 13, 43, 135, 80, 130, 84, 100, 38, 125, 3, 64, 52, 75, 28, 115, 209, 196, 253, 59, 204, 251, 127, 171, 230, 62, 91, 165, 173, 4, 35, 156, 20, 81, 34, 240, 41, 121, 113, 126, 255, 140, 14, 226, 12, 239, 188, 114, 117, 111, 55, 161, 236, 211, 142, 98, 139, 134, 16, 232, 8, 119, 17, 190, 146, 79, 36, 197, 50, 54, 157, 207, 243, 166, 187, 172, 94, 108, 169, 19, 87, 37, 181, 227, 189, 168, 58, 1, 5, 89, 42, 70};
    private int[] key0;
    private int[] key1;
    private int[] key2;
    private int[] key3;
    private boolean encrypting;

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to SKIPJACK init - " + params.getClass().getName());
        }
        byte[] keyBytes = ((KeyParameter) params).getKey();
        this.encrypting = encrypting;
        this.key0 = new int[32];
        this.key1 = new int[32];
        this.key2 = new int[32];
        this.key3 = new int[32];
        for (int i = 0; i < 32; i++) {
            this.key0[i] = keyBytes[(i * 4) % 10] & 255;
            this.key1[i] = keyBytes[((i * 4) + 1) % 10] & 255;
            this.key2[i] = keyBytes[((i * 4) + 2) % 10] & 255;
            this.key3[i] = keyBytes[((i * 4) + 3) % 10] & 255;
        }
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "SKIPJACK";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.key1 == null) {
            throw new IllegalStateException("SKIPJACK engine not initialised");
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

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private int g(int k, int w) {
        int g1 = (w >> 8) & 255;
        int g2 = w & 255;
        int g3 = ftable[g2 ^ this.key0[k]] ^ g1;
        int g4 = ftable[g3 ^ this.key1[k]] ^ g2;
        int g5 = ftable[g4 ^ this.key2[k]] ^ g3;
        int g6 = ftable[g5 ^ this.key3[k]] ^ g4;
        return (g5 << 8) + g6;
    }

    public int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int w1 = (in[inOff + 0] << 8) + (in[inOff + 1] & 255);
        int w2 = (in[inOff + 2] << 8) + (in[inOff + 3] & 255);
        int w3 = (in[inOff + 4] << 8) + (in[inOff + 5] & 255);
        int w4 = (in[inOff + 6] << 8) + (in[inOff + 7] & 255);
        int k = 0;
        for (int t = 0; t < 2; t++) {
            for (int i = 0; i < 8; i++) {
                int tmp = w4;
                w4 = w3;
                w3 = w2;
                w2 = g(k, w1);
                w1 = (w2 ^ tmp) ^ (k + 1);
                k++;
            }
            for (int i2 = 0; i2 < 8; i2++) {
                int tmp2 = w4;
                w4 = w3;
                w3 = (w1 ^ w2) ^ (k + 1);
                w2 = g(k, w1);
                w1 = tmp2;
                k++;
            }
        }
        out[outOff + 0] = (byte) (w1 >> 8);
        out[outOff + 1] = (byte) w1;
        out[outOff + 2] = (byte) (w2 >> 8);
        out[outOff + 3] = (byte) w2;
        out[outOff + 4] = (byte) (w3 >> 8);
        out[outOff + 5] = (byte) w3;
        out[outOff + 6] = (byte) (w4 >> 8);
        out[outOff + 7] = (byte) w4;
        return 8;
    }

    private int h(int k, int w) {
        int h1 = w & 255;
        int h2 = (w >> 8) & 255;
        int h3 = ftable[h2 ^ this.key3[k]] ^ h1;
        int h4 = ftable[h3 ^ this.key2[k]] ^ h2;
        int h5 = ftable[h4 ^ this.key1[k]] ^ h3;
        int h6 = ftable[h5 ^ this.key0[k]] ^ h4;
        return (h6 << 8) + h5;
    }

    public int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int w2 = (in[inOff + 0] << 8) + (in[inOff + 1] & 255);
        int w1 = (in[inOff + 2] << 8) + (in[inOff + 3] & 255);
        int w4 = (in[inOff + 4] << 8) + (in[inOff + 5] & 255);
        int w3 = (in[inOff + 6] << 8) + (in[inOff + 7] & 255);
        int k = 31;
        for (int t = 0; t < 2; t++) {
            for (int i = 0; i < 8; i++) {
                int tmp = w4;
                w4 = w3;
                w3 = w2;
                w2 = h(k, w1);
                w1 = (w2 ^ tmp) ^ (k + 1);
                k--;
            }
            for (int i2 = 0; i2 < 8; i2++) {
                int tmp2 = w4;
                w4 = w3;
                w3 = (w1 ^ w2) ^ (k + 1);
                w2 = h(k, w1);
                w1 = tmp2;
                k--;
            }
        }
        out[outOff + 0] = (byte) (w2 >> 8);
        out[outOff + 1] = (byte) w2;
        out[outOff + 2] = (byte) (w1 >> 8);
        out[outOff + 3] = (byte) w1;
        out[outOff + 4] = (byte) (w4 >> 8);
        out[outOff + 5] = (byte) w4;
        out[outOff + 6] = (byte) (w3 >> 8);
        out[outOff + 7] = (byte) w3;
        return 8;
    }
}
