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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/HC256Engine.class */
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
        int i;
        int i2 = this.cnt & WinUser.CF_GDIOBJLAST;
        if (this.cnt < 1024) {
            int i3 = this.p[(i2 - 3) & WinUser.CF_GDIOBJLAST];
            int i4 = this.p[(i2 - WinUser.CF_GDIOBJLAST) & WinUser.CF_GDIOBJLAST];
            int[] iArr = this.p;
            iArr[i2] = iArr[i2] + this.p[(i2 - 10) & WinUser.CF_GDIOBJLAST] + (rotateRight(i3, 10) ^ rotateRight(i4, 23)) + this.q[(i3 ^ i4) & WinUser.CF_GDIOBJLAST];
            int i5 = this.p[(i2 - 12) & WinUser.CF_GDIOBJLAST];
            i = (((this.q[i5 & 255] + this.q[((i5 >> 8) & 255) + 256]) + this.q[((i5 >> 16) & 255) + 512]) + this.q[((i5 >> 24) & 255) + 768]) ^ this.p[i2];
        } else {
            int i6 = this.q[(i2 - 3) & WinUser.CF_GDIOBJLAST];
            int i7 = this.q[(i2 - WinUser.CF_GDIOBJLAST) & WinUser.CF_GDIOBJLAST];
            int[] iArr2 = this.q;
            iArr2[i2] = iArr2[i2] + this.q[(i2 - 10) & WinUser.CF_GDIOBJLAST] + (rotateRight(i6, 10) ^ rotateRight(i7, 23)) + this.p[(i6 ^ i7) & WinUser.CF_GDIOBJLAST];
            int i8 = this.q[(i2 - 12) & WinUser.CF_GDIOBJLAST];
            i = (((this.p[i8 & 255] + this.p[((i8 >> 8) & 255) + 256]) + this.p[((i8 >> 16) & 255) + 512]) + this.p[((i8 >> 24) & 255) + 768]) ^ this.q[i2];
        }
        this.cnt = (this.cnt + 1) & 2047;
        return i;
    }

    private void init() {
        if (this.key.length != 32 && this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128/256 bits long");
        }
        if (this.iv.length < 16) {
            throw new IllegalArgumentException("The IV must be at least 128 bits long");
        }
        if (this.key.length != 32) {
            byte[] bArr = new byte[32];
            System.arraycopy(this.key, 0, bArr, 0, this.key.length);
            System.arraycopy(this.key, 0, bArr, 16, this.key.length);
            this.key = bArr;
        }
        if (this.iv.length < 32) {
            byte[] bArr2 = new byte[32];
            System.arraycopy(this.iv, 0, bArr2, 0, this.iv.length);
            System.arraycopy(this.iv, 0, bArr2, this.iv.length, bArr2.length - this.iv.length);
            this.iv = bArr2;
        }
        this.idx = 0;
        this.cnt = 0;
        int[] iArr = new int[2560];
        for (int i = 0; i < 32; i++) {
            int i2 = i >> 2;
            iArr[i2] = iArr[i2] | ((this.key[i] & 255) << (8 * (i & 3)));
        }
        for (int i3 = 0; i3 < 32; i3++) {
            int i4 = (i3 >> 2) + 8;
            iArr[i4] = iArr[i4] | ((this.iv[i3] & 255) << (8 * (i3 & 3)));
        }
        for (int i5 = 16; i5 < 2560; i5++) {
            int i6 = iArr[i5 - 2];
            int i7 = iArr[i5 - 15];
            iArr[i5] = ((rotateRight(i6, 17) ^ rotateRight(i6, 19)) ^ (i6 >>> 10)) + iArr[i5 - 7] + ((rotateRight(i7, 7) ^ rotateRight(i7, 18)) ^ (i7 >>> 3)) + iArr[i5 - 16] + i5;
        }
        System.arraycopy(iArr, 512, this.p, 0, 1024);
        System.arraycopy(iArr, 1536, this.q, 0, 1024);
        for (int i8 = 0; i8 < 4096; i8++) {
            step();
        }
        this.cnt = 0;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "HC-256";
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        CipherParameters parameters = cipherParameters;
        if (cipherParameters instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV) cipherParameters).getIV();
            parameters = ((ParametersWithIV) cipherParameters).getParameters();
        } else {
            this.iv = new byte[0];
        }
        if (!(parameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + cipherParameters.getClass().getName());
        }
        this.key = ((KeyParameter) parameters).getKey();
        init();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), this.key.length * 8, cipherParameters, Utils.getPurpose(z)));
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

    private static int rotateRight(int i, int i2) {
        return (i >>> i2) | (i << (-i2));
    }
}
