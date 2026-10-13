package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/HC256Engine.class */
public class HC256Engine implements StreamCipher {
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private int[] p = new int[1024];
    private int[] q = new int[1024];
    private int cnt = 0;
    private byte[] buf = new byte[4];
    private int idx = 0;

    private int step() {
        int ret;
        int j = this.cnt & 1023;
        if (this.cnt < 1024) {
            int x = this.p[(j - 3) & 1023];
            int y = this.p[(j - 1023) & 1023];
            int[] iArr = this.p;
            iArr[j] = iArr[j] + this.p[(j - 10) & 1023] + (rotateRight(x, 10) ^ rotateRight(y, 23)) + this.q[(x ^ y) & 1023];
            int x2 = this.p[(j - 12) & 1023];
            ret = (((this.q[x2 & 255] + this.q[((x2 >> 8) & 255) + 256]) + this.q[((x2 >> 16) & 255) + 512]) + this.q[((x2 >> 24) & 255) + 768]) ^ this.p[j];
        } else {
            int x3 = this.q[(j - 3) & 1023];
            int y2 = this.q[(j - 1023) & 1023];
            int[] iArr2 = this.q;
            iArr2[j] = iArr2[j] + this.q[(j - 10) & 1023] + (rotateRight(x3, 10) ^ rotateRight(y2, 23)) + this.p[(x3 ^ y2) & 1023];
            int x4 = this.q[(j - 12) & 1023];
            ret = (((this.p[x4 & 255] + this.p[((x4 >> 8) & 255) + 256]) + this.p[((x4 >> 16) & 255) + 512]) + this.p[((x4 >> 24) & 255) + 768]) ^ this.q[j];
        }
        this.cnt = (this.cnt + 1) & 2047;
        return ret;
    }

    private void init() {
        if (this.key.length != 32 && this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128/256 bits long");
        }
        if (this.iv.length < 16) {
            throw new IllegalArgumentException("The IV must be at least 128 bits long");
        }
        if (this.key.length != 32) {
            byte[] k = new byte[32];
            System.arraycopy(this.key, 0, k, 0, this.key.length);
            System.arraycopy(this.key, 0, k, 16, this.key.length);
            this.key = k;
        }
        if (this.iv.length < 32) {
            byte[] newIV = new byte[32];
            System.arraycopy(this.iv, 0, newIV, 0, this.iv.length);
            System.arraycopy(this.iv, 0, newIV, this.iv.length, newIV.length - this.iv.length);
            this.iv = newIV;
        }
        this.idx = 0;
        this.cnt = 0;
        int[] w = new int[2560];
        for (int i = 0; i < 32; i++) {
            int i2 = i >> 2;
            w[i2] = w[i2] | ((this.key[i] & 255) << (8 * (i & 3)));
        }
        for (int i3 = 0; i3 < 32; i3++) {
            int i4 = (i3 >> 2) + 8;
            w[i4] = w[i4] | ((this.iv[i3] & 255) << (8 * (i3 & 3)));
        }
        for (int i5 = 16; i5 < 2560; i5++) {
            int x = w[i5 - 2];
            int y = w[i5 - 15];
            w[i5] = ((rotateRight(x, 17) ^ rotateRight(x, 19)) ^ (x >>> 10)) + w[i5 - 7] + ((rotateRight(y, 7) ^ rotateRight(y, 18)) ^ (y >>> 3)) + w[i5 - 16] + i5;
        }
        System.arraycopy(w, 512, this.p, 0, 1024);
        System.arraycopy(w, 1536, this.q, 0, 1024);
        for (int i6 = 0; i6 < 4096; i6++) {
            step();
        }
        this.cnt = 0;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "HC-256";
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters keyParam = params;
        if (params instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV) params).getIV();
            keyParam = ((ParametersWithIV) params).getParameters();
        } else {
            this.iv = new byte[0];
        }
        if (keyParam instanceof KeyParameter) {
            this.key = ((KeyParameter) keyParam).getKey();
            init();
            this.initialised = true;
            return;
        }
        throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + params.getClass().getName());
    }

    private byte getByte() {
        if (this.idx == 0) {
            int step = step();
            this.buf[0] = (byte) (step & 255);
            int step2 = step >> 8;
            this.buf[1] = (byte) (step2 & 255);
            int step3 = step2 >> 8;
            this.buf[2] = (byte) (step3 & 255);
            this.buf[3] = (byte) ((step3 >> 8) & 255);
        }
        byte ret = this.buf[this.idx];
        this.idx = (this.idx + 1) & 3;
        return ret;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; i++) {
            out[outOff + i] = (byte) (in[inOff + i] ^ getByte());
        }
        return len;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void reset() {
        init();
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public byte returnByte(byte in) {
        return (byte) (in ^ getByte());
    }

    private static int rotateRight(int x, int bits) {
        return (x >>> bits) | (x << (-bits));
    }
}
