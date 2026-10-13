package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.WinUser;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/HC128Engine.class */
public class HC128Engine implements StreamCipher {
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private int[] p = new int[512];
    private int[] q = new int[512];
    private int cnt = 0;
    private byte[] buf = new byte[4];
    private int idx = 0;

    private static int f1(int i) {
        return (rotateRight(i, 7) ^ rotateRight(i, 18)) ^ (i >>> 3);
    }

    private static int f2(int i) {
        return (rotateRight(i, 17) ^ rotateRight(i, 19)) ^ (i >>> 10);
    }

    private int g1(int i, int i2, int i3) {
        return (rotateRight(i, 10) ^ rotateRight(i3, 23)) + rotateRight(i2, 8);
    }

    private int g2(int i, int i2, int i3) {
        return (rotateLeft(i, 10) ^ rotateLeft(i3, 23)) + rotateLeft(i2, 8);
    }

    private static int rotateLeft(int i, int i2) {
        return (i << i2) | (i >>> (-i2));
    }

    private static int rotateRight(int i, int i2) {
        return (i >>> i2) | (i << (-i2));
    }

    private int h1(int i) {
        return this.q[i & 255] + this.q[((i >> 16) & 255) + 256];
    }

    private int h2(int i) {
        return this.p[i & 255] + this.p[((i >> 16) & 255) + 256];
    }

    private static int mod1024(int i) {
        return i & WinUser.CF_GDIOBJLAST;
    }

    private static int mod512(int i) {
        return i & 511;
    }

    private static int dim(int i, int i2) {
        return mod512(i - i2);
    }

    private int step() {
        int iH2;
        int iMod512 = mod512(this.cnt);
        if (this.cnt < 512) {
            int[] iArr = this.p;
            iArr[iMod512] = iArr[iMod512] + g1(this.p[dim(iMod512, 3)], this.p[dim(iMod512, 10)], this.p[dim(iMod512, 511)]);
            iH2 = h1(this.p[dim(iMod512, 12)]) ^ this.p[iMod512];
        } else {
            int[] iArr2 = this.q;
            iArr2[iMod512] = iArr2[iMod512] + g2(this.q[dim(iMod512, 3)], this.q[dim(iMod512, 10)], this.q[dim(iMod512, 511)]);
            iH2 = h2(this.q[dim(iMod512, 12)]) ^ this.q[iMod512];
        }
        this.cnt = mod1024(this.cnt + 1);
        return iH2;
    }

    private void init() {
        if (this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128 bits long");
        }
        if (this.iv.length != 16) {
            throw new IllegalArgumentException("The IV must be 128 bits long");
        }
        this.idx = 0;
        this.cnt = 0;
        int[] iArr = new int[1280];
        for (int i = 0; i < 16; i++) {
            int i2 = i >> 2;
            iArr[i2] = iArr[i2] | ((this.key[i] & 255) << (8 * (i & 3)));
        }
        System.arraycopy(iArr, 0, iArr, 4, 4);
        for (int i3 = 0; i3 < this.iv.length && i3 < 16; i3++) {
            int i4 = (i3 >> 2) + 8;
            iArr[i4] = iArr[i4] | ((this.iv[i3] & 255) << (8 * (i3 & 3)));
        }
        System.arraycopy(iArr, 8, iArr, 12, 4);
        for (int i5 = 16; i5 < 1280; i5++) {
            iArr[i5] = f2(iArr[i5 - 2]) + iArr[i5 - 7] + f1(iArr[i5 - 15]) + iArr[i5 - 16] + i5;
        }
        System.arraycopy(iArr, 256, this.p, 0, 512);
        System.arraycopy(iArr, 768, this.q, 0, 512);
        for (int i6 = 0; i6 < 512; i6++) {
            this.p[i6] = step();
        }
        for (int i7 = 0; i7 < 512; i7++) {
            this.q[i7] = step();
        }
        this.cnt = 0;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "HC-128";
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("no IV passed");
        }
        this.iv = ((ParametersWithIV) cipherParameters).getIV();
        CipherParameters parameters = ((ParametersWithIV) cipherParameters).getParameters();
        if (!(parameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + cipherParameters.getClass().getName());
        }
        this.key = ((KeyParameter) parameters).getKey();
        init();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(z)));
        this.initialised = true;
    }

    private byte getByte() {
        if (this.idx == 0) {
            int iStep = step();
            this.buf[0] = (byte) (iStep & 255);
            int i = iStep >> 8;
            this.buf[1] = (byte) (i & 255);
            int i2 = i >> 8;
            this.buf[2] = (byte) (i2 & 255);
            this.buf[3] = (byte) ((i2 >> 8) & 255);
        }
        byte b = this.buf[this.idx];
        this.idx = (this.idx + 1) & 3;
        return b;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i3 + i2 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i4 = 0; i4 < i2; i4++) {
            bArr2[i3 + i4] = (byte) (bArr[i + i4] ^ getByte());
        }
        return i2;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void reset() {
        init();
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public byte returnByte(byte b) {
        return (byte) (b ^ getByte());
    }
}
