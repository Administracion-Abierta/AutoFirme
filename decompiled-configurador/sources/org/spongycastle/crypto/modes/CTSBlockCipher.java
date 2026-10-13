package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.StreamBlockCipher;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/CTSBlockCipher.class */
public class CTSBlockCipher extends BufferedBlockCipher {
    private int blockSize;

    public CTSBlockCipher(BlockCipher cipher) {
        if (cipher instanceof StreamBlockCipher) {
            throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
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
    public int getOutputSize(int len) {
        return len + this.bufOff;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
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
            System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            this.bufOff = blockSize;
            len -= gapLen;
            int i = inOff;
            int i2 = gapLen;
            while (true) {
                inOff = i + i2;
                if (len <= blockSize) {
                    break;
                }
                System.arraycopy(in, inOff, this.buf, this.bufOff, blockSize);
                resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
                System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
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
        if (this.bufOff + outOff > out.length) {
            throw new OutputLengthException("output buffer to small in doFinal");
        }
        int blockSize = this.cipher.getBlockSize();
        int len = this.bufOff - blockSize;
        byte[] block = new byte[blockSize];
        if (this.forEncryption) {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            this.cipher.processBlock(this.buf, 0, block, 0);
            if (this.bufOff > blockSize) {
                for (int i = this.bufOff; i != this.buf.length; i++) {
                    this.buf[i] = block[i - blockSize];
                }
                for (int i2 = blockSize; i2 != this.bufOff; i2++) {
                    byte[] bArr = this.buf;
                    int i3 = i2;
                    bArr[i3] = (byte) (bArr[i3] ^ block[i2 - blockSize]);
                }
                if (this.cipher instanceof CBCBlockCipher) {
                    BlockCipher c = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                    c.processBlock(this.buf, blockSize, out, outOff);
                } else {
                    this.cipher.processBlock(this.buf, blockSize, out, outOff);
                }
                System.arraycopy(block, 0, out, outOff + blockSize, len);
            } else {
                System.arraycopy(block, 0, out, outOff, blockSize);
            }
        } else {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            byte[] lastBlock = new byte[blockSize];
            if (this.bufOff > blockSize) {
                if (this.cipher instanceof CBCBlockCipher) {
                    BlockCipher c2 = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                    c2.processBlock(this.buf, 0, block, 0);
                } else {
                    this.cipher.processBlock(this.buf, 0, block, 0);
                }
                for (int i4 = blockSize; i4 != this.bufOff; i4++) {
                    lastBlock[i4 - blockSize] = (byte) (block[i4 - blockSize] ^ this.buf[i4]);
                }
                System.arraycopy(this.buf, blockSize, block, 0, len);
                this.cipher.processBlock(block, 0, out, outOff);
                System.arraycopy(lastBlock, 0, out, outOff + blockSize, len);
            } else {
                this.cipher.processBlock(this.buf, 0, block, 0);
                System.arraycopy(block, 0, out, outOff, blockSize);
            }
        }
        int offset = this.bufOff;
        reset();
        return offset;
    }
}
