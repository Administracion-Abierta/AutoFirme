package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/PaddedBlockCipher.class */
public class PaddedBlockCipher extends BufferedBlockCipher {
    public PaddedBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            if (this.forEncryption) {
                return total + this.buf.length;
            }
            return total;
        }
        return (total - leftOver) + this.buf.length;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            return total - this.buf.length;
        }
        return total - leftOver;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        return resultLen;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = getBlockSize();
        int length = getUpdateOutputSize(len);
        if (length > 0 && outOff + length > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen = 0 + this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
            len -= gapLen;
            int i = inOff;
            int i2 = gapLen;
            while (true) {
                inOff = i + i2;
                if (len <= this.buf.length) {
                    break;
                }
                resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
                len -= blockSize;
                i = inOff;
                i2 = blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
        return resultLen;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
        int resultLen;
        int blockSize = this.cipher.getBlockSize();
        int resultLen2 = 0;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if (outOff + (2 * blockSize) > out.length) {
                    throw new OutputLengthException("output buffer too short");
                }
                resultLen2 = this.cipher.processBlock(this.buf, 0, out, outOff);
                this.bufOff = 0;
            }
            byte code = (byte) (blockSize - this.bufOff);
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = code;
                this.bufOff++;
            }
            resultLen = resultLen2 + this.cipher.processBlock(this.buf, 0, out, outOff + resultLen2);
        } else if (this.bufOff == blockSize) {
            int resultLen3 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            int count = this.buf[blockSize - 1] & 255;
            if (count < 0 || count > blockSize) {
                throw new InvalidCipherTextException("pad block corrupted");
            }
            resultLen = resultLen3 - count;
            System.arraycopy(this.buf, 0, out, outOff, resultLen);
        } else {
            throw new DataLengthException("last block incomplete in decryption");
        }
        reset();
        return resultLen;
    }
}
