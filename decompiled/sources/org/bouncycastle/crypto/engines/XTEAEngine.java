package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/XTEAEngine.class */
public class XTEAEngine implements BlockCipher {
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private int[] _S = new int[4];
    private int[] _sum0 = new int[32];
    private int[] _sum1 = new int[32];
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "XTEA";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to TEA init - " + cipherParameters.getClass().getName());
        }
        this._forEncryption = z;
        this._initialised = true;
        setKey(((KeyParameter) cipherParameters).getKey());
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (!this._initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (i + 8 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + 8 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] bArr) {
        if (bArr.length != 16) {
            throw new IllegalArgumentException("Key size must be 128 bits.");
        }
        int i = 0;
        int i2 = 0;
        while (i2 < 4) {
            this._S[i2] = bytesToInt(bArr, i);
            i2++;
            i += 4;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < 32; i4++) {
            this._sum0[i4] = i3 + this._S[i3 & 3];
            i3 -= 1640531527;
            this._sum1[i4] = i3 + this._S[(i3 >>> 11) & 3];
        }
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToInt = bytesToInt(bArr, i);
        int iBytesToInt2 = bytesToInt(bArr, i + 4);
        for (int i3 = 0; i3 < 32; i3++) {
            iBytesToInt += (((iBytesToInt2 << 4) ^ (iBytesToInt2 >>> 5)) + iBytesToInt2) ^ this._sum0[i3];
            iBytesToInt2 += (((iBytesToInt << 4) ^ (iBytesToInt >>> 5)) + iBytesToInt) ^ this._sum1[i3];
        }
        unpackInt(iBytesToInt, bArr2, i2);
        unpackInt(iBytesToInt2, bArr2, i2 + 4);
        return 8;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToInt = bytesToInt(bArr, i);
        int iBytesToInt2 = bytesToInt(bArr, i + 4);
        for (int i3 = 31; i3 >= 0; i3--) {
            iBytesToInt2 -= (((iBytesToInt << 4) ^ (iBytesToInt >>> 5)) + iBytesToInt) ^ this._sum1[i3];
            iBytesToInt -= (((iBytesToInt2 << 4) ^ (iBytesToInt2 >>> 5)) + iBytesToInt2) ^ this._sum0[i3];
        }
        unpackInt(iBytesToInt, bArr2, i2);
        unpackInt(iBytesToInt2, bArr2, i2 + 4);
        return 8;
    }

    private int bytesToInt(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = bArr[i] << 24;
        int i4 = i2 + 1;
        return i3 | ((bArr[i2] & 255) << 16) | ((bArr[i4] & 255) << 8) | (bArr[i4 + 1] & 255);
    }

    private void unpackInt(int i, byte[] bArr, int i2) {
        int i3 = i2 + 1;
        bArr[i2] = (byte) (i >>> 24);
        int i4 = i3 + 1;
        bArr[i3] = (byte) (i >>> 16);
        bArr[i4] = (byte) (i >>> 8);
        bArr[i4 + 1] = (byte) i;
    }
}
