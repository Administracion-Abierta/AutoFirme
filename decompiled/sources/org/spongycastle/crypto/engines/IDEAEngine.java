package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/IDEAEngine.class */
public class IDEAEngine implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey = null;
    private static final int MASK = 65535;
    private static final int BASE = 65537;

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.workingKey = generateWorkingKey(forEncryption, ((KeyParameter) params).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "IDEA";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("IDEA engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        ideaFunc(this.workingKey, in, inOff, out, outOff);
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private int bytesToWord(byte[] in, int inOff) {
        return ((in[inOff] << 8) & 65280) + (in[inOff + 1] & 255);
    }

    private void wordToBytes(int word, byte[] out, int outOff) {
        out[outOff] = (byte) (word >>> 8);
        out[outOff + 1] = (byte) word;
    }

    private int mul(int x, int y) {
        int x2;
        if (x == 0) {
            x2 = BASE - y;
        } else if (y == 0) {
            x2 = BASE - x;
        } else {
            int p = x * y;
            int y2 = p & 65535;
            int x3 = p >>> 16;
            x2 = (y2 - x3) + (y2 < x3 ? 1 : 0);
        }
        return x2 & 65535;
    }

    private void ideaFunc(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int keyOff = 0;
        int x0 = bytesToWord(in, inOff);
        int x1 = bytesToWord(in, inOff + 2);
        int x2 = bytesToWord(in, inOff + 4);
        int x3 = bytesToWord(in, inOff + 6);
        for (int round = 0; round < 8; round++) {
            int i = keyOff;
            int keyOff2 = keyOff + 1;
            int x4 = mul(x0, workingKey[i]);
            int keyOff3 = keyOff2 + 1;
            int x5 = (x1 + workingKey[keyOff2]) & 65535;
            int keyOff4 = keyOff3 + 1;
            int x6 = (x2 + workingKey[keyOff3]) & 65535;
            int keyOff5 = keyOff4 + 1;
            int x7 = mul(x3, workingKey[keyOff4]);
            int x8 = x6 ^ x4;
            int x9 = x5 ^ x7;
            int keyOff6 = keyOff5 + 1;
            int x10 = mul(x8, workingKey[keyOff5]);
            keyOff = keyOff6 + 1;
            int x11 = mul((x9 + x10) & 65535, workingKey[keyOff6]);
            int x12 = (x10 + x11) & 65535;
            x0 = x4 ^ x11;
            x3 = x7 ^ x12;
            x1 = x11 ^ x6;
            x2 = x12 ^ x5;
        }
        int i2 = keyOff;
        int keyOff7 = keyOff + 1;
        wordToBytes(mul(x0, workingKey[i2]), out, outOff);
        int keyOff8 = keyOff7 + 1;
        wordToBytes(x2 + workingKey[keyOff7], out, outOff + 2);
        wordToBytes(x1 + workingKey[keyOff8], out, outOff + 4);
        wordToBytes(mul(x3, workingKey[keyOff8 + 1]), out, outOff + 6);
    }

    private int[] expandKey(byte[] uKey) {
        int[] key = new int[52];
        if (uKey.length < 16) {
            byte[] tmp = new byte[16];
            System.arraycopy(uKey, 0, tmp, tmp.length - uKey.length, uKey.length);
            uKey = tmp;
        }
        for (int i = 0; i < 8; i++) {
            key[i] = bytesToWord(uKey, i * 2);
        }
        for (int i2 = 8; i2 < 52; i2++) {
            if ((i2 & 7) < 6) {
                key[i2] = (((key[i2 - 7] & 127) << 9) | (key[i2 - 6] >> 7)) & 65535;
            } else if ((i2 & 7) == 6) {
                key[i2] = (((key[i2 - 7] & 127) << 9) | (key[i2 - 14] >> 7)) & 65535;
            } else {
                key[i2] = (((key[i2 - 15] & 127) << 9) | (key[i2 - 14] >> 7)) & 65535;
            }
        }
        return key;
    }

    private int mulInv(int x) {
        if (x < 2) {
            return x;
        }
        int t0 = 1;
        int t1 = BASE / x;
        int y = BASE % x;
        while (y != 1) {
            int q = x / y;
            x %= y;
            t0 = (t0 + (t1 * q)) & 65535;
            if (x == 1) {
                return t0;
            }
            int q2 = y / x;
            y %= x;
            t1 = (t1 + (t0 * q2)) & 65535;
        }
        return (1 - t1) & 65535;
    }

    int addInv(int x) {
        return (0 - x) & 65535;
    }

    private int[] invertKey(int[] inKey) {
        int[] key = new int[52];
        int inOff = 0 + 1;
        int t1 = mulInv(inKey[0]);
        int inOff2 = inOff + 1;
        int t2 = addInv(inKey[inOff]);
        int inOff3 = inOff2 + 1;
        int t3 = addInv(inKey[inOff2]);
        int inOff4 = inOff3 + 1;
        int t4 = mulInv(inKey[inOff3]);
        int p = 52 - 1;
        key[p] = t4;
        int p2 = p - 1;
        key[p2] = t3;
        int p3 = p2 - 1;
        key[p3] = t2;
        int p4 = p3 - 1;
        key[p4] = t1;
        for (int round = 1; round < 8; round++) {
            int i = inOff4;
            int inOff5 = inOff4 + 1;
            int t5 = inKey[i];
            int inOff6 = inOff5 + 1;
            int t6 = inKey[inOff5];
            int p5 = p4 - 1;
            key[p5] = t6;
            int p6 = p5 - 1;
            key[p6] = t5;
            int inOff7 = inOff6 + 1;
            int t7 = mulInv(inKey[inOff6]);
            int inOff8 = inOff7 + 1;
            int t8 = addInv(inKey[inOff7]);
            int inOff9 = inOff8 + 1;
            int t9 = addInv(inKey[inOff8]);
            inOff4 = inOff9 + 1;
            int t10 = mulInv(inKey[inOff9]);
            int p7 = p6 - 1;
            key[p7] = t10;
            int p8 = p7 - 1;
            key[p8] = t8;
            int p9 = p8 - 1;
            key[p9] = t9;
            p4 = p9 - 1;
            key[p4] = t7;
        }
        int i2 = inOff4;
        int inOff10 = inOff4 + 1;
        int t11 = inKey[i2];
        int inOff11 = inOff10 + 1;
        int t12 = inKey[inOff10];
        int p10 = p4 - 1;
        key[p10] = t12;
        int p11 = p10 - 1;
        key[p11] = t11;
        int inOff12 = inOff11 + 1;
        int t13 = mulInv(inKey[inOff11]);
        int inOff13 = inOff12 + 1;
        int t14 = addInv(inKey[inOff12]);
        int t15 = addInv(inKey[inOff13]);
        int t16 = mulInv(inKey[inOff13 + 1]);
        int p12 = p11 - 1;
        key[p12] = t16;
        int p13 = p12 - 1;
        key[p13] = t15;
        int p14 = p13 - 1;
        key[p14] = t14;
        key[p14 - 1] = t13;
        return key;
    }

    private int[] generateWorkingKey(boolean forEncryption, byte[] userKey) {
        if (forEncryption) {
            return expandKey(userKey);
        }
        return invertKey(expandKey(userKey));
    }
}
