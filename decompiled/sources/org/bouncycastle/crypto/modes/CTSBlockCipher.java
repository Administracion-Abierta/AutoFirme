package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/CTSBlockCipher.class */
public class CTSBlockCipher extends DefaultBufferedBlockCipher {
    private int blockSize;

    public CTSBlockCipher(BlockCipher blockCipher) {
        if (blockCipher instanceof StreamBlockCipher) {
            throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
        }
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int i) {
        int i2 = i + this.bufOff;
        int length = i2 % this.buf.length;
        return length == 0 ? i2 - this.buf.length : i2 - length;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int i) {
        return i + this.bufOff;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int processByte(byte b, byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        int iProcessBlock = 0;
        if (this.bufOff == this.buf.length) {
            iProcessBlock = this.cipher.processBlock(this.buf, 0, bArr, i);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
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
            System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            this.bufOff = blockSize;
            i2 -= length;
            int i4 = i;
            int i5 = length;
            while (true) {
                i = i4 + i5;
                if (i2 <= blockSize) {
                    break;
                }
                System.arraycopy(bArr, i, this.buf, this.bufOff, blockSize);
                iProcessBlock += this.cipher.processBlock(this.buf, 0, bArr2, i3 + iProcessBlock);
                System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
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
        if (this.bufOff + i > bArr.length) {
            throw new OutputLengthException("output buffer to small in doFinal");
        }
        int blockSize = this.cipher.getBlockSize();
        int i2 = this.bufOff - blockSize;
        byte[] bArr2 = new byte[blockSize];
        if (this.forEncryption) {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            this.cipher.processBlock(this.buf, 0, bArr2, 0);
            if (this.bufOff > blockSize) {
                for (int i3 = this.bufOff; i3 != this.buf.length; i3++) {
                    this.buf[i3] = bArr2[i3 - blockSize];
                }
                for (int i4 = blockSize; i4 != this.bufOff; i4++) {
                    byte[] bArr3 = this.buf;
                    int i5 = i4;
                    bArr3[i5] = (byte) (bArr3[i5] ^ bArr2[i4 - blockSize]);
                }
                if (this.cipher instanceof CBCModeCipher) {
                    ((CBCModeCipher) this.cipher).getUnderlyingCipher().processBlock(this.buf, blockSize, bArr, i);
                } else {
                    this.cipher.processBlock(this.buf, blockSize, bArr, i);
                }
                System.arraycopy(bArr2, 0, bArr, i + blockSize, i2);
            } else {
                System.arraycopy(bArr2, 0, bArr, i, blockSize);
            }
        } else {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            byte[] bArr4 = new byte[blockSize];
            if (this.bufOff > blockSize) {
                if (this.cipher instanceof CBCModeCipher) {
                    ((CBCModeCipher) this.cipher).getUnderlyingCipher().processBlock(this.buf, 0, bArr2, 0);
                } else {
                    this.cipher.processBlock(this.buf, 0, bArr2, 0);
                }
                for (int i6 = blockSize; i6 != this.bufOff; i6++) {
                    bArr4[i6 - blockSize] = (byte) (bArr2[i6 - blockSize] ^ this.buf[i6]);
                }
                System.arraycopy(this.buf, blockSize, bArr2, 0, i2);
                this.cipher.processBlock(bArr2, 0, bArr, i);
                System.arraycopy(bArr4, 0, bArr, i + blockSize, i2);
            } else {
                this.cipher.processBlock(this.buf, 0, bArr2, 0);
                System.arraycopy(bArr2, 0, bArr, i, blockSize);
            }
        }
        int i7 = this.bufOff;
        reset();
        return i7;
    }
}
