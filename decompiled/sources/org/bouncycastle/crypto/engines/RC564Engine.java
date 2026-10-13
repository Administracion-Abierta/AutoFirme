package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RC5Parameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RC564Engine.class */
public class RC564Engine implements BlockCipher {
    private static final int wordSize = 64;
    private static final int bytesPerWord = 8;
    private int _noRounds = 12;
    private long[] _S = null;
    private static final long P64 = -5196783011329398165L;
    private static final long Q64 = -7046029254386353131L;
    private boolean forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC5-64";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof RC5Parameters)) {
            throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + cipherParameters.getClass().getName());
        }
        RC5Parameters rC5Parameters = (RC5Parameters) cipherParameters;
        this.forEncryption = z;
        this._noRounds = rC5Parameters.getRounds();
        byte[] key = rC5Parameters.getKey();
        setKey(key);
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
        long[] jArr = new long[(bArr.length + 7) / 8];
        for (int i = 0; i != bArr.length; i++) {
            int i2 = i / 8;
            jArr[i2] = jArr[i2] + (((long) (bArr[i] & 255)) << (8 * (i % 8)));
        }
        this._S = new long[2 * (this._noRounds + 1)];
        this._S[0] = -5196783011329398165L;
        for (int i3 = 1; i3 < this._S.length; i3++) {
            this._S[i3] = this._S[i3 - 1] + Q64;
        }
        int length = jArr.length > this._S.length ? 3 * jArr.length : 3 * this._S.length;
        long j = 0;
        long j2 = 0;
        int length2 = 0;
        int length3 = 0;
        for (int i4 = 0; i4 < length; i4++) {
            long jRotateLeft = rotateLeft(this._S[length2] + j + j2, 3L);
            this._S[length2] = jRotateLeft;
            j = jRotateLeft;
            long jRotateLeft2 = rotateLeft(jArr[length3] + j + j2, j + j2);
            jArr[length3] = jRotateLeft2;
            j2 = jRotateLeft2;
            length2 = (length2 + 1) % this._S.length;
            length3 = (length3 + 1) % jArr.length;
        }
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        long jBytesToWord = bytesToWord(bArr, i) + this._S[0];
        long jBytesToWord2 = bytesToWord(bArr, i + 8) + this._S[1];
        for (int i3 = 1; i3 <= this._noRounds; i3++) {
            jBytesToWord = rotateLeft(jBytesToWord ^ jBytesToWord2, jBytesToWord2) + this._S[2 * i3];
            jBytesToWord2 = rotateLeft(jBytesToWord2 ^ jBytesToWord, jBytesToWord) + this._S[(2 * i3) + 1];
        }
        wordToBytes(jBytesToWord, bArr2, i2);
        wordToBytes(jBytesToWord2, bArr2, i2 + 8);
        return 16;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        long jBytesToWord = bytesToWord(bArr, i);
        long jBytesToWord2 = bytesToWord(bArr, i + 8);
        for (int i3 = this._noRounds; i3 >= 1; i3--) {
            jBytesToWord2 = rotateRight(jBytesToWord2 - this._S[(2 * i3) + 1], jBytesToWord) ^ jBytesToWord;
            jBytesToWord = rotateRight(jBytesToWord - this._S[2 * i3], jBytesToWord2) ^ jBytesToWord2;
        }
        wordToBytes(jBytesToWord - this._S[0], bArr2, i2);
        wordToBytes(jBytesToWord2 - this._S[1], bArr2, i2 + 8);
        return 16;
    }

    private long rotateLeft(long j, long j2) {
        return (j << ((int) (j2 & 63))) | (j >>> ((int) (64 - (j2 & 63))));
    }

    private long rotateRight(long j, long j2) {
        return (j >>> ((int) (j2 & 63))) | (j << ((int) (64 - (j2 & 63))));
    }

    private long bytesToWord(byte[] bArr, int i) {
        long j = 0;
        for (int i2 = 7; i2 >= 0; i2--) {
            j = (j << 8) + ((long) (bArr[i2 + i] & 255));
        }
        return j;
    }

    private void wordToBytes(long j, byte[] bArr, int i) {
        for (int i2 = 0; i2 < 8; i2++) {
            bArr[i2 + i] = (byte) j;
            j >>>= 8;
        }
    }
}
