package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.RC5Parameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RC532Engine.class */
public class RC532Engine implements BlockCipher {
    private int _noRounds = 12;
    private int[] _S = null;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private boolean forEncryption;

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC5-32";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof RC5Parameters) {
            RC5Parameters p = (RC5Parameters) params;
            this._noRounds = p.getRounds();
            setKey(p.getKey());
        } else if (params instanceof KeyParameter) {
            setKey(((KeyParameter) params).getKey());
        } else {
            throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + params.getClass().getName());
        }
        this.forEncryption = forEncryption;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        return this.forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] key) {
        int iter;
        int[] L = new int[(key.length + 3) / 4];
        for (int i = 0; i != key.length; i++) {
            int i2 = i / 4;
            L[i2] = L[i2] + ((key[i] & 255) << (8 * (i % 4)));
        }
        this._S = new int[2 * (this._noRounds + 1)];
        this._S[0] = P32;
        for (int i3 = 1; i3 < this._S.length; i3++) {
            this._S[i3] = this._S[i3 - 1] + Q32;
        }
        if (L.length > this._S.length) {
            iter = 3 * L.length;
        } else {
            iter = 3 * this._S.length;
        }
        int A = 0;
        int B = 0;
        int i4 = 0;
        int j = 0;
        for (int k = 0; k < iter; k++) {
            int iRotateLeft = rotateLeft(this._S[i4] + A + B, 3);
            this._S[i4] = iRotateLeft;
            A = iRotateLeft;
            int iRotateLeft2 = rotateLeft(L[j] + A + B, A + B);
            L[j] = iRotateLeft2;
            B = iRotateLeft2;
            i4 = (i4 + 1) % this._S.length;
            j = (j + 1) % L.length;
        }
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int A = bytesToWord(in, inOff) + this._S[0];
        int B = bytesToWord(in, inOff + 4) + this._S[1];
        for (int i = 1; i <= this._noRounds; i++) {
            A = rotateLeft(A ^ B, B) + this._S[2 * i];
            B = rotateLeft(B ^ A, A) + this._S[(2 * i) + 1];
        }
        wordToBytes(A, out, outOff);
        wordToBytes(B, out, outOff + 4);
        return 8;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int A = bytesToWord(in, inOff);
        int B = bytesToWord(in, inOff + 4);
        for (int i = this._noRounds; i >= 1; i--) {
            B = rotateRight(B - this._S[(2 * i) + 1], A) ^ A;
            A = rotateRight(A - this._S[2 * i], B) ^ B;
        }
        wordToBytes(A - this._S[0], out, outOff);
        wordToBytes(B - this._S[1], out, outOff + 4);
        return 8;
    }

    private int rotateLeft(int x, int y) {
        return (x << (y & 31)) | (x >>> (32 - (y & 31)));
    }

    private int rotateRight(int x, int y) {
        return (x >>> (y & 31)) | (x << (32 - (y & 31)));
    }

    private int bytesToWord(byte[] src, int srcOff) {
        return (src[srcOff] & 255) | ((src[srcOff + 1] & 255) << 8) | ((src[srcOff + 2] & 255) << 16) | ((src[srcOff + 3] & 255) << 24);
    }

    private void wordToBytes(int word, byte[] dst, int dstOff) {
        dst[dstOff] = (byte) word;
        dst[dstOff + 1] = (byte) (word >> 8);
        dst[dstOff + 2] = (byte) (word >> 16);
        dst[dstOff + 3] = (byte) (word >> 24);
    }
}
