package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/OpenPGPCFBBlockCipher.class */
public class OpenPGPCFBBlockCipher implements BlockCipher {
    private byte[] IV;
    private byte[] FR;
    private byte[] FRE;
    private BlockCipher cipher;
    private int count;
    private int blockSize;
    private boolean forEncryption;

    public OpenPGPCFBBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        return this.forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
        this.count = 0;
        System.arraycopy(this.IV, 0, this.FR, 0, this.FR.length);
        this.cipher.reset();
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = z;
        reset();
        this.cipher.init(true, cipherParameters);
    }

    private byte encryptByte(byte b, int i) {
        return (byte) (this.FRE[i] ^ b);
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        if (i + this.blockSize > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + this.blockSize > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            byte[] bArr3 = this.FR;
            int i3 = this.blockSize - 2;
            byte bEncryptByte = encryptByte(bArr[i], this.blockSize - 2);
            bArr2[i2] = bEncryptByte;
            bArr3[i3] = bEncryptByte;
            byte[] bArr4 = this.FR;
            int i4 = this.blockSize - 1;
            byte bEncryptByte2 = encryptByte(bArr[i + 1], this.blockSize - 1);
            bArr2[i2 + 1] = bEncryptByte2;
            bArr4[i4] = bEncryptByte2;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i5 = 2; i5 < this.blockSize; i5++) {
                byte bEncryptByte3 = encryptByte(bArr[i + i5], i5 - 2);
                bArr2[i2 + i5] = bEncryptByte3;
                this.FR[i5 - 2] = bEncryptByte3;
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i6 = 0; i6 < this.blockSize; i6++) {
                byte bEncryptByte4 = encryptByte(bArr[i + i6], i6);
                bArr2[i2 + i6] = bEncryptByte4;
                this.FR[i6] = bEncryptByte4;
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            bArr2[i2] = encryptByte(bArr[i], 0);
            bArr2[i2 + 1] = encryptByte(bArr[i + 1], 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            System.arraycopy(bArr2, i2, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i7 = 2; i7 < this.blockSize; i7++) {
                byte bEncryptByte5 = encryptByte(bArr[i + i7], i7 - 2);
                bArr2[i2 + i7] = bEncryptByte5;
                this.FR[i7 - 2] = bEncryptByte5;
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        if (i + this.blockSize > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + this.blockSize > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            byte b = bArr[i];
            this.FR[this.blockSize - 2] = b;
            bArr2[i2] = encryptByte(b, this.blockSize - 2);
            byte b2 = bArr[i + 1];
            this.FR[this.blockSize - 1] = b2;
            bArr2[i2 + 1] = encryptByte(b2, this.blockSize - 1);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i3 = 2; i3 < this.blockSize; i3++) {
                byte b3 = bArr[i + i3];
                this.FR[i3 - 2] = b3;
                bArr2[i2 + i3] = encryptByte(b3, i3 - 2);
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i4 = 0; i4 < this.blockSize; i4++) {
                this.FR[i4] = bArr[i + i4];
                bArr2[i2 + i4] = encryptByte(bArr[i + i4], i4);
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            byte b4 = bArr[i];
            byte b5 = bArr[i + 1];
            bArr2[i2] = encryptByte(b4, 0);
            bArr2[i2 + 1] = encryptByte(b5, 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = b4;
            this.FR[this.blockSize - 1] = b5;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i5 = 2; i5 < this.blockSize; i5++) {
                byte b6 = bArr[i + i5];
                this.FR[i5 - 2] = b6;
                bArr2[i2 + i5] = encryptByte(b6, i5 - 2);
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }
}
