package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/NISTCTSBlockCipher.class */
public class NISTCTSBlockCipher extends BufferedBlockCipher {
    public static final int CS1 = 1;
    public static final int CS2 = 2;
    public static final int CS3 = 3;
    private final int type;
    private final int blockSize;

    public NISTCTSBlockCipher(int type, BlockCipher cipher) {
        this.type = type;
        this.cipher = new CBCBlockCipher(cipher);
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
                throw new DataLengthException("need at least one block of input for NISTCTS");
            }
            if (this.bufOff > blockSize) {
                byte[] lastBlock = new byte[blockSize];
                if (this.type == 2 || this.type == 3) {
                    this.cipher.processBlock(this.buf, 0, block, 0);
                    System.arraycopy(this.buf, blockSize, lastBlock, 0, len);
                    this.cipher.processBlock(lastBlock, 0, lastBlock, 0);
                    if (this.type == 2 && len == blockSize) {
                        System.arraycopy(block, 0, out, outOff, blockSize);
                        System.arraycopy(lastBlock, 0, out, outOff + blockSize, len);
                    } else {
                        System.arraycopy(lastBlock, 0, out, outOff, blockSize);
                        System.arraycopy(block, 0, out, outOff + blockSize, len);
                    }
                } else {
                    System.arraycopy(this.buf, 0, block, 0, blockSize);
                    this.cipher.processBlock(block, 0, block, 0);
                    System.arraycopy(block, 0, out, outOff, len);
                    System.arraycopy(this.buf, this.bufOff - len, lastBlock, 0, len);
                    this.cipher.processBlock(lastBlock, 0, lastBlock, 0);
                    System.arraycopy(lastBlock, 0, out, outOff + len, blockSize);
                }
            } else {
                this.cipher.processBlock(this.buf, 0, block, 0);
                System.arraycopy(block, 0, out, outOff, blockSize);
            }
        } else {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            byte[] lastBlock2 = new byte[blockSize];
            if (this.bufOff > blockSize) {
                if (this.type == 3 || (this.type == 2 && (this.buf.length - this.bufOff) % blockSize != 0)) {
                    if (this.cipher instanceof CBCBlockCipher) {
                        BlockCipher c = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                        c.processBlock(this.buf, 0, block, 0);
                    } else {
                        this.cipher.processBlock(this.buf, 0, block, 0);
                    }
                    for (int i = blockSize; i != this.bufOff; i++) {
                        lastBlock2[i - blockSize] = (byte) (block[i - blockSize] ^ this.buf[i]);
                    }
                    System.arraycopy(this.buf, blockSize, block, 0, len);
                    this.cipher.processBlock(block, 0, out, outOff);
                    System.arraycopy(lastBlock2, 0, out, outOff + blockSize, len);
                } else {
                    BlockCipher c2 = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                    c2.processBlock(this.buf, this.bufOff - blockSize, lastBlock2, 0);
                    System.arraycopy(this.buf, 0, block, 0, blockSize);
                    if (len != blockSize) {
                        System.arraycopy(lastBlock2, len, block, len, blockSize - len);
                    }
                    this.cipher.processBlock(block, 0, block, 0);
                    System.arraycopy(block, 0, out, outOff, blockSize);
                    for (int i2 = 0; i2 != len; i2++) {
                        int i3 = i2;
                        lastBlock2[i3] = (byte) (lastBlock2[i3] ^ this.buf[i2]);
                    }
                    System.arraycopy(lastBlock2, 0, out, outOff + blockSize, len);
                }
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
