package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/HC128Engine.class */
public class HC128Engine implements StreamCipher {
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private int[] p = new int[512];
    private int[] q = new int[512];
    private int cnt = 0;
    private byte[] buf = new byte[4];
    private int idx = 0;

    private static int f1(int x) {
        return (rotateRight(x, 7) ^ rotateRight(x, 18)) ^ (x >>> 3);
    }

    private static int f2(int x) {
        return (rotateRight(x, 17) ^ rotateRight(x, 19)) ^ (x >>> 10);
    }

    private int g1(int x, int y, int z) {
        return (rotateRight(x, 10) ^ rotateRight(z, 23)) + rotateRight(y, 8);
    }

    private int g2(int x, int y, int z) {
        return (rotateLeft(x, 10) ^ rotateLeft(z, 23)) + rotateLeft(y, 8);
    }

    private static int rotateLeft(int x, int bits) {
        return (x << bits) | (x >>> (-bits));
    }

    private static int rotateRight(int x, int bits) {
        return (x >>> bits) | (x << (-bits));
    }

    private int h1(int x) {
        return this.q[x & 255] + this.q[((x >> 16) & 255) + 256];
    }

    private int h2(int x) {
        return this.p[x & 255] + this.p[((x >> 16) & 255) + 256];
    }

    private static int mod1024(int x) {
        return x & 1023;
    }

    private static int mod512(int x) {
        return x & 511;
    }

    private static int dim(int x, int y) {
        return mod512(x - y);
    }

    private int step() {
        int ret;
        int j = mod512(this.cnt);
        if (this.cnt < 512) {
            int[] iArr = this.p;
            iArr[j] = iArr[j] + g1(this.p[dim(j, 3)], this.p[dim(j, 10)], this.p[dim(j, 511)]);
            ret = h1(this.p[dim(j, 12)]) ^ this.p[j];
        } else {
            int[] iArr2 = this.q;
            iArr2[j] = iArr2[j] + g2(this.q[dim(j, 3)], this.q[dim(j, 10)], this.q[dim(j, 511)]);
            ret = h2(this.q[dim(j, 12)]) ^ this.q[j];
        }
        this.cnt = mod1024(this.cnt + 1);
        return ret;
    }

    private void init() {
        if (this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128 bits long");
        }
        this.idx = 0;
        this.cnt = 0;
        int[] w = new int[1280];
        for (int i = 0; i < 16; i++) {
            int i2 = i >> 2;
            w[i2] = w[i2] | ((this.key[i] & 255) << (8 * (i & 3)));
        }
        System.arraycopy(w, 0, w, 4, 4);
        for (int i3 = 0; i3 < this.iv.length && i3 < 16; i3++) {
            int i4 = (i3 >> 2) + 8;
            w[i4] = w[i4] | ((this.iv[i3] & 255) << (8 * (i3 & 3)));
        }
        System.arraycopy(w, 8, w, 12, 4);
        for (int i5 = 16; i5 < 1280; i5++) {
            w[i5] = f2(w[i5 - 2]) + w[i5 - 7] + f1(w[i5 - 15]) + w[i5 - 16] + i5;
        }
        System.arraycopy(w, 256, this.p, 0, 512);
        System.arraycopy(w, 768, this.q, 0, 512);
        for (int i6 = 0; i6 < 512; i6++) {
            this.p[i6] = step();
        }
        for (int i7 = 0; i7 < 512; i7++) {
            this.q[i7] = step();
        }
        this.cnt = 0;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "HC-128";
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
        throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + params.getClass().getName());
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
}
