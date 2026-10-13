package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/BufferedBlockCipher.class */
public class BufferedBlockCipher {
    protected byte[] buf;
    protected int bufOff;
    protected boolean forEncryption;
    protected BlockCipher cipher;
    protected MultiBlockCipher mbCipher;
    protected boolean partialBlockOkay;
    protected boolean pgpCFB;

    BufferedBlockCipher() {
    }

    public BufferedBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        if (blockCipher instanceof MultiBlockCipher) {
            this.mbCipher = (MultiBlockCipher) blockCipher;
            this.buf = new byte[this.mbCipher.getMultiBlockSize()];
        } else {
            this.mbCipher = null;
            this.buf = new byte[blockCipher.getBlockSize()];
        }
        this.bufOff = 0;
        String algorithmName = blockCipher.getAlgorithmName();
        int iIndexOf = algorithmName.indexOf(47) + 1;
        this.pgpCFB = iIndexOf > 0 && algorithmName.startsWith("PGP", iIndexOf);
        if (this.pgpCFB || (blockCipher instanceof StreamCipher)) {
            this.partialBlockOkay = true;
        } else {
            this.partialBlockOkay = iIndexOf > 0 && algorithmName.startsWith("OpenPGP", iIndexOf);
        }
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = z;
        reset();
        this.cipher.init(z, cipherParameters);
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    public int getUpdateOutputSize(int i) {
        int i2 = i + this.bufOff;
        int length = (this.pgpCFB && this.forEncryption) ? (i2 % this.buf.length) - (this.cipher.getBlockSize() + 2) : i2 % this.buf.length;
        return i2 - length;
    }

    public int getOutputSize(int i) {
        return (this.pgpCFB && this.forEncryption) ? i + this.bufOff + this.cipher.getBlockSize() + 2 : i + this.bufOff;
    }

    public int processByte(byte b, byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        int iProcessBlock = 0;
        byte[] bArr2 = this.buf;
        int i2 = this.bufOff;
        this.bufOff = i2 + 1;
        bArr2[i2] = b;
        if (this.bufOff == this.buf.length) {
            iProcessBlock = this.cipher.processBlock(this.buf, 0, bArr, i);
            this.bufOff = 0;
        }
        return iProcessBlock;
    }

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
            i += length;
            if (this.mbCipher != null) {
                int multiBlockSize = i2 / this.mbCipher.getMultiBlockSize();
                if (multiBlockSize > 0) {
                    iProcessBlock += this.mbCipher.processBlocks(bArr, i, multiBlockSize, bArr2, i3 + iProcessBlock);
                    int multiBlockSize2 = multiBlockSize * this.mbCipher.getMultiBlockSize();
                    i2 -= multiBlockSize2;
                    i += multiBlockSize2;
                }
            } else {
                while (i2 > this.buf.length) {
                    iProcessBlock += this.cipher.processBlock(bArr, i, bArr2, i3 + iProcessBlock);
                    i2 -= blockSize;
                    i += blockSize;
                }
            }
        }
        System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
        this.bufOff += i2;
        if (this.bufOff == this.buf.length) {
            iProcessBlock += this.cipher.processBlock(this.buf, 0, bArr2, i3 + iProcessBlock);
            this.bufOff = 0;
        }
        return iProcessBlock;
    }

    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException, DataLengthException {
        try {
            int i2 = 0;
            if (i + this.bufOff > bArr.length) {
                throw new OutputLengthException("output buffer too short for doFinal()");
            }
            if (this.bufOff != 0) {
                if (!this.partialBlockOkay) {
                    throw new DataLengthException("data not block size aligned");
                }
                this.cipher.processBlock(this.buf, 0, this.buf, 0);
                i2 = this.bufOff;
                this.bufOff = 0;
                System.arraycopy(this.buf, 0, bArr, i, i2);
            }
            int i3 = i2;
            reset();
            return i3;
        } catch (Throwable th) {
            reset();
            throw th;
        }
    }

    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}
