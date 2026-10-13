package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RC6Engine.class */
public class RC6Engine implements BlockCipher {
    private static final int wordSize = 32;
    private static final int bytesPerWord = 4;
    private static final int _noRounds = 20;
    private int[] _S = null;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private static final int LGW = 5;
    private boolean forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC6";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to RC6 init - " + cipherParameters.getClass().getName());
        }
        this.forEncryption = z;
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        setKey(key);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), key.length * 8, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int blockSize = getBlockSize();
        if (this._S == null) {
            throw new IllegalStateException("RC6 engine not initialised");
        }
        if (i + blockSize > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + blockSize > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this.forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] bArr) {
        if ((bArr.length + 3) / 4 == 0) {
        }
        int[] iArr = new int[((bArr.length + 4) - 1) / 4];
        for (int length = bArr.length - 1; length >= 0; length--) {
            iArr[length / 4] = (iArr[length / 4] << 8) + (bArr[length] & 255);
        }
        this._S = new int[44];
        this._S[0] = P32;
        for (int i = 1; i < this._S.length; i++) {
            this._S[i] = this._S[i - 1] + Q32;
        }
        int length2 = iArr.length > this._S.length ? 3 * iArr.length : 3 * this._S.length;
        int i2 = 0;
        int i3 = 0;
        int length3 = 0;
        int length4 = 0;
        for (int i4 = 0; i4 < length2; i4++) {
            int iRotateLeft = rotateLeft(this._S[length3] + i2 + i3, 3);
            this._S[length3] = iRotateLeft;
            i2 = iRotateLeft;
            int iRotateLeft2 = rotateLeft(iArr[length4] + i2 + i3, i2 + i3);
            iArr[length4] = iRotateLeft2;
            i3 = iRotateLeft2;
            length3 = (length3 + 1) % this._S.length;
            length4 = (length4 + 1) % iArr.length;
        }
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToWord = bytesToWord(bArr, i);
        int iBytesToWord2 = bytesToWord(bArr, i + 4);
        int iBytesToWord3 = bytesToWord(bArr, i + 8);
        int iBytesToWord4 = bytesToWord(bArr, i + 12);
        int iRotateLeft = iBytesToWord2 + this._S[0];
        int i3 = iBytesToWord4 + this._S[1];
        for (int i4 = 1; i4 <= 20; i4++) {
            int iRotateLeft2 = rotateLeft(iRotateLeft * ((2 * iRotateLeft) + 1), 5);
            int iRotateLeft3 = rotateLeft(i3 * ((2 * i3) + 1), 5);
            int iRotateLeft4 = rotateLeft(iBytesToWord ^ iRotateLeft2, iRotateLeft3) + this._S[2 * i4];
            iBytesToWord = iRotateLeft;
            iRotateLeft = rotateLeft(iBytesToWord3 ^ iRotateLeft3, iRotateLeft2) + this._S[(2 * i4) + 1];
            iBytesToWord3 = i3;
            i3 = iRotateLeft4;
        }
        int i5 = iBytesToWord + this._S[42];
        int i6 = iBytesToWord3 + this._S[43];
        wordToBytes(i5, bArr2, i2);
        wordToBytes(iRotateLeft, bArr2, i2 + 4);
        wordToBytes(i6, bArr2, i2 + 8);
        wordToBytes(i3, bArr2, i2 + 12);
        return 16;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToWord = bytesToWord(bArr, i);
        int iBytesToWord2 = bytesToWord(bArr, i + 4);
        int iBytesToWord3 = bytesToWord(bArr, i + 8);
        int iBytesToWord4 = bytesToWord(bArr, i + 12);
        int iRotateRight = iBytesToWord3 - this._S[43];
        int iRotateRight2 = iBytesToWord - this._S[42];
        for (int i3 = 20; i3 >= 1; i3--) {
            int i4 = iBytesToWord4;
            iBytesToWord4 = iRotateRight;
            int i5 = iBytesToWord2;
            iBytesToWord2 = iRotateRight2;
            int iRotateLeft = rotateLeft(iBytesToWord2 * ((2 * iBytesToWord2) + 1), 5);
            int iRotateLeft2 = rotateLeft(iBytesToWord4 * ((2 * iBytesToWord4) + 1), 5);
            iRotateRight = rotateRight(i5 - this._S[(2 * i3) + 1], iRotateLeft) ^ iRotateLeft2;
            iRotateRight2 = rotateRight(i4 - this._S[2 * i3], iRotateLeft2) ^ iRotateLeft;
        }
        int i6 = iBytesToWord4 - this._S[1];
        int i7 = iBytesToWord2 - this._S[0];
        wordToBytes(iRotateRight2, bArr2, i2);
        wordToBytes(i7, bArr2, i2 + 4);
        wordToBytes(iRotateRight, bArr2, i2 + 8);
        wordToBytes(i6, bArr2, i2 + 12);
        return 16;
    }

    private int rotateLeft(int i, int i2) {
        return (i << i2) | (i >>> (-i2));
    }

    private int rotateRight(int i, int i2) {
        return (i >>> i2) | (i << (-i2));
    }

    private int bytesToWord(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 3; i3 >= 0; i3--) {
            i2 = (i2 << 8) + (bArr[i3 + i] & 255);
        }
        return i2;
    }

    private void wordToBytes(int i, byte[] bArr, int i2) {
        for (int i3 = 0; i3 < 4; i3++) {
            bArr[i3 + i2] = (byte) i;
            i >>>= 8;
        }
    }
}
