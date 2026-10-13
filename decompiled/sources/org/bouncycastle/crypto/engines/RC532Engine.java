package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC5Parameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RC532Engine.class */
public class RC532Engine implements BlockCipher {
    private int _noRounds = 12;
    private int[] _S = null;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private boolean forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC5-32";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        byte[] key;
        if (cipherParameters instanceof RC5Parameters) {
            RC5Parameters rC5Parameters = (RC5Parameters) cipherParameters;
            this._noRounds = rC5Parameters.getRounds();
            key = rC5Parameters.getKey();
            setKey(key);
        } else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + cipherParameters.getClass().getName());
            }
            key = ((KeyParameter) cipherParameters).getKey();
            setKey(key);
        }
        this.forEncryption = z;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), key.length * 8, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        return this.forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void setKey(byte[] bArr) {
        int[] iArr = new int[(bArr.length + 3) / 4];
        for (int i = 0; i != bArr.length; i++) {
            int i2 = i / 4;
            iArr[i2] = iArr[i2] + ((bArr[i] & 255) << (8 * (i % 4)));
        }
        this._S = new int[2 * (this._noRounds + 1)];
        this._S[0] = P32;
        for (int i3 = 1; i3 < this._S.length; i3++) {
            this._S[i3] = this._S[i3 - 1] + Q32;
        }
        int length = iArr.length > this._S.length ? 3 * iArr.length : 3 * this._S.length;
        int i4 = 0;
        int i5 = 0;
        int length2 = 0;
        int length3 = 0;
        for (int i6 = 0; i6 < length; i6++) {
            int iRotateLeft = rotateLeft(this._S[length2] + i4 + i5, 3);
            this._S[length2] = iRotateLeft;
            i4 = iRotateLeft;
            int iRotateLeft2 = rotateLeft(iArr[length3] + i4 + i5, i4 + i5);
            iArr[length3] = iRotateLeft2;
            i5 = iRotateLeft2;
            length2 = (length2 + 1) % this._S.length;
            length3 = (length3 + 1) % iArr.length;
        }
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToWord = bytesToWord(bArr, i) + this._S[0];
        int iBytesToWord2 = bytesToWord(bArr, i + 4) + this._S[1];
        for (int i3 = 1; i3 <= this._noRounds; i3++) {
            iBytesToWord = rotateLeft(iBytesToWord ^ iBytesToWord2, iBytesToWord2) + this._S[2 * i3];
            iBytesToWord2 = rotateLeft(iBytesToWord2 ^ iBytesToWord, iBytesToWord) + this._S[(2 * i3) + 1];
        }
        wordToBytes(iBytesToWord, bArr2, i2);
        wordToBytes(iBytesToWord2, bArr2, i2 + 4);
        return 8;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBytesToWord = bytesToWord(bArr, i);
        int iBytesToWord2 = bytesToWord(bArr, i + 4);
        for (int i3 = this._noRounds; i3 >= 1; i3--) {
            iBytesToWord2 = rotateRight(iBytesToWord2 - this._S[(2 * i3) + 1], iBytesToWord) ^ iBytesToWord;
            iBytesToWord = rotateRight(iBytesToWord - this._S[2 * i3], iBytesToWord2) ^ iBytesToWord2;
        }
        wordToBytes(iBytesToWord - this._S[0], bArr2, i2);
        wordToBytes(iBytesToWord2 - this._S[1], bArr2, i2 + 4);
        return 8;
    }

    private int rotateLeft(int i, int i2) {
        return (i << (i2 & 31)) | (i >>> (32 - (i2 & 31)));
    }

    private int rotateRight(int i, int i2) {
        return (i >>> (i2 & 31)) | (i << (32 - (i2 & 31)));
    }

    private int bytesToWord(byte[] bArr, int i) {
        return (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16) | ((bArr[i + 3] & 255) << 24);
    }

    private void wordToBytes(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        bArr[i2 + 1] = (byte) (i >> 8);
        bArr[i2 + 2] = (byte) (i >> 16);
        bArr[i2 + 3] = (byte) (i >> 24);
    }
}
