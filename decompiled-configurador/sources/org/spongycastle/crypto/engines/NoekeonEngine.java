package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/NoekeonEngine.class */
public class NoekeonEngine implements BlockCipher {
    private static final int genericSize = 16;
    private static final int[] nullVector = {0, 0, 0, 0};
    private static final int[] roundConstants = {128, 27, 54, CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256, 216, CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384, 77, CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA, 47, 94, 188, 99, 198, CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA, 53, CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256, 212};
    private int[] state = new int[4];
    private int[] subKeys = new int[4];
    private int[] decryptKeys = new int[4];
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Noekeon";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + params.getClass().getName());
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
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] key) {
        this.subKeys[0] = bytesToIntBig(key, 0);
        this.subKeys[1] = bytesToIntBig(key, 4);
        this.subKeys[2] = bytesToIntBig(key, 8);
        this.subKeys[3] = bytesToIntBig(key, 12);
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        this.state[0] = bytesToIntBig(in, inOff);
        this.state[1] = bytesToIntBig(in, inOff + 4);
        this.state[2] = bytesToIntBig(in, inOff + 8);
        this.state[3] = bytesToIntBig(in, inOff + 12);
        int i = 0;
        while (i < 16) {
            int[] iArr = this.state;
            iArr[0] = iArr[0] ^ roundConstants[i];
            theta(this.state, this.subKeys);
            pi1(this.state);
            gamma(this.state);
            pi2(this.state);
            i++;
        }
        int[] iArr2 = this.state;
        iArr2[0] = iArr2[0] ^ roundConstants[i];
        theta(this.state, this.subKeys);
        intToBytesBig(this.state[0], out, outOff);
        intToBytesBig(this.state[1], out, outOff + 4);
        intToBytesBig(this.state[2], out, outOff + 8);
        intToBytesBig(this.state[3], out, outOff + 12);
        return 16;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        this.state[0] = bytesToIntBig(in, inOff);
        this.state[1] = bytesToIntBig(in, inOff + 4);
        this.state[2] = bytesToIntBig(in, inOff + 8);
        this.state[3] = bytesToIntBig(in, inOff + 12);
        System.arraycopy(this.subKeys, 0, this.decryptKeys, 0, this.subKeys.length);
        theta(this.decryptKeys, nullVector);
        int i = 16;
        while (i > 0) {
            theta(this.state, this.decryptKeys);
            int[] iArr = this.state;
            iArr[0] = iArr[0] ^ roundConstants[i];
            pi1(this.state);
            gamma(this.state);
            pi2(this.state);
            i--;
        }
        theta(this.state, this.decryptKeys);
        int[] iArr2 = this.state;
        iArr2[0] = iArr2[0] ^ roundConstants[i];
        intToBytesBig(this.state[0], out, outOff);
        intToBytesBig(this.state[1], out, outOff + 4);
        intToBytesBig(this.state[2], out, outOff + 8);
        intToBytesBig(this.state[3], out, outOff + 12);
        return 16;
    }

    private void gamma(int[] a) {
        a[1] = a[1] ^ ((a[3] ^ (-1)) & (a[2] ^ (-1)));
        a[0] = a[0] ^ (a[2] & a[1]);
        int tmp = a[3];
        a[3] = a[0];
        a[0] = tmp;
        a[2] = a[2] ^ ((a[0] ^ a[1]) ^ a[3]);
        a[1] = a[1] ^ ((a[3] ^ (-1)) & (a[2] ^ (-1)));
        a[0] = a[0] ^ (a[2] & a[1]);
    }

    private void theta(int[] a, int[] k) {
        int tmp = a[0] ^ a[2];
        int tmp2 = tmp ^ (rotl(tmp, 8) ^ rotl(tmp, 24));
        a[1] = a[1] ^ tmp2;
        a[3] = a[3] ^ tmp2;
        for (int i = 0; i < 4; i++) {
            int i2 = i;
            a[i2] = a[i2] ^ k[i];
        }
        int tmp3 = a[1] ^ a[3];
        int tmp4 = tmp3 ^ (rotl(tmp3, 8) ^ rotl(tmp3, 24));
        a[0] = a[0] ^ tmp4;
        a[2] = a[2] ^ tmp4;
    }

    private void pi1(int[] a) {
        a[1] = rotl(a[1], 1);
        a[2] = rotl(a[2], 5);
        a[3] = rotl(a[3], 2);
    }

    private void pi2(int[] a) {
        a[1] = rotl(a[1], 31);
        a[2] = rotl(a[2], 27);
        a[3] = rotl(a[3], 30);
    }

    private int bytesToIntBig(byte[] in, int off) {
        int off2 = off + 1;
        int i = in[off] << 24;
        int off3 = off2 + 1;
        return i | ((in[off2] & 255) << 16) | ((in[off3] & 255) << 8) | (in[off3 + 1] & 255);
    }

    private void intToBytesBig(int x, byte[] out, int off) {
        int off2 = off + 1;
        out[off] = (byte) (x >>> 24);
        int off3 = off2 + 1;
        out[off2] = (byte) (x >>> 16);
        out[off3] = (byte) (x >>> 8);
        out[off3 + 1] = (byte) x;
    }

    private int rotl(int x, int y) {
        return (x << y) | (x >>> (32 - y));
    }
}
