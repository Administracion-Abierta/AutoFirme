package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/TEAEngine.class */
public class TEAEngine implements BlockCipher {
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private static final int d_sum = -957401312;
    private int _a;
    private int _b;
    private int _c;
    private int _d;
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "TEA";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to TEA init - " + params.getClass().getName());
        }
        this._forEncryption = forEncryption;
        this._initialised = true;
        KeyParameter p = (KeyParameter) params;
        setKey(p.getKey());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (!this._initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key size must be 128 bits.");
        }
        this._a = bytesToInt(key, 0);
        this._b = bytesToInt(key, 4);
        this._c = bytesToInt(key, 8);
        this._d = bytesToInt(key, 12);
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = bytesToInt(in, inOff);
        int v1 = bytesToInt(in, inOff + 4);
        int sum = 0;
        for (int i = 0; i != 32; i++) {
            sum -= 1640531527;
            v0 += (((v1 << 4) + this._a) ^ (v1 + sum)) ^ ((v1 >>> 5) + this._b);
            v1 += (((v0 << 4) + this._c) ^ (v0 + sum)) ^ ((v0 >>> 5) + this._d);
        }
        unpackInt(v0, out, outOff);
        unpackInt(v1, out, outOff + 4);
        return 8;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = bytesToInt(in, inOff);
        int v1 = bytesToInt(in, inOff + 4);
        int sum = d_sum;
        for (int i = 0; i != 32; i++) {
            v1 -= (((v0 << 4) + this._c) ^ (v0 + sum)) ^ ((v0 >>> 5) + this._d);
            v0 -= (((v1 << 4) + this._a) ^ (v1 + sum)) ^ ((v1 >>> 5) + this._b);
            sum += 1640531527;
        }
        unpackInt(v0, out, outOff);
        unpackInt(v1, out, outOff + 4);
        return 8;
    }

    private int bytesToInt(byte[] in, int inOff) {
        int inOff2 = inOff + 1;
        int i = in[inOff] << 24;
        int inOff3 = inOff2 + 1;
        return i | ((in[inOff2] & 255) << 16) | ((in[inOff3] & 255) << 8) | (in[inOff3 + 1] & 255);
    }

    private void unpackInt(int v, byte[] out, int outOff) {
        int outOff2 = outOff + 1;
        out[outOff] = (byte) (v >>> 24);
        int outOff3 = outOff2 + 1;
        out[outOff2] = (byte) (v >>> 16);
        out[outOff3] = (byte) (v >>> 8);
        out[outOff3 + 1] = (byte) v;
    }
}
