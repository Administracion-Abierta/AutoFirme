package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/PaddedBlockCipher.class */
public class PaddedBlockCipher extends DefaultBufferedBlockCipher {
    public PaddedBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        this.buf = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int i) {
        int i2 = i + this.bufOff;
        int length = i2 % this.buf.length;
        if (length == 0) {
            return this.forEncryption ? i2 + this.buf.length : i2;
        }
        return (i2 - length) + this.buf.length;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int i) {
        int i2 = i + this.bufOff;
        int length = i2 % this.buf.length;
        return length == 0 ? i2 - this.buf.length : i2 - length;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int processByte(byte b, byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        int iProcessBlock = 0;
        if (this.bufOff == this.buf.length) {
            iProcessBlock = this.cipher.processBlock(this.buf, 0, bArr, i);
            this.bufOff = 0;
        }
        byte[] bArr2 = this.buf;
        int i2 = this.bufOff;
        this.bufOff = i2 + 1;
        bArr2[i2] = b;
        return iProcessBlock;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalStateException, DataLengthException {
        if (i2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = getBlockSize();
        int updateOutputSize = getUpdateOutputSize(i2);
        if (updateOutputSize > 0 && i3 + updateOutputSize > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int iProcessBlock = 0;
        int length = this.buf.length - this.bufOff;
        if (i2 > length) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, length);
            iProcessBlock = 0 + this.cipher.processBlock(this.buf, 0, bArr2, i3);
            this.bufOff = 0;
            i2 -= length;
            int i4 = i;
            int i5 = length;
            while (true) {
                i = i4 + i5;
                if (i2 <= this.buf.length) {
                    break;
                }
                iProcessBlock += this.cipher.processBlock(bArr, i, bArr2, i3 + iProcessBlock);
                i2 -= blockSize;
                i4 = i;
                i5 = blockSize;
            }
        }
        System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
        this.bufOff += i2;
        return iProcessBlock;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException, DataLengthException {
        int iProcessBlock;
        int blockSize = this.cipher.getBlockSize();
        int iProcessBlock2 = 0;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if (i + (2 * blockSize) > bArr.length) {
                    throw new OutputLengthException("output buffer too short");
                }
                iProcessBlock2 = this.cipher.processBlock(this.buf, 0, bArr, i);
                this.bufOff = 0;
            }
            byte b = (byte) (blockSize - this.bufOff);
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = b;
                this.bufOff++;
            }
            iProcessBlock = iProcessBlock2 + this.cipher.processBlock(this.buf, 0, bArr, i + iProcessBlock2);
        } else {
            if (this.bufOff != blockSize) {
                throw new DataLengthException("last block incomplete in decryption");
            }
            int iProcessBlock3 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            int i2 = this.buf[blockSize - 1] & 255;
            if (i2 > blockSize) {
                throw new InvalidCipherTextException("pad block corrupted");
            }
            iProcessBlock = iProcessBlock3 - i2;
            System.arraycopy(this.buf, 0, bArr, i, iProcessBlock);
        }
        reset();
        return iProcessBlock;
    }
}
