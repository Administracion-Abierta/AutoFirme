package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/BufferedBlockCipher.class */
public class BufferedBlockCipher {
    protected byte[] buf;
    protected int bufOff;
    protected boolean forEncryption;
    protected BlockCipher cipher;
    protected boolean partialBlockOkay;
    protected boolean pgpCFB;

    protected BufferedBlockCipher() {
    }

    public BufferedBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
        String name = cipher.getAlgorithmName();
        int idx = name.indexOf(47) + 1;
        this.pgpCFB = idx > 0 && name.startsWith("PGP", idx);
        if (this.pgpCFB || (cipher instanceof StreamCipher)) {
            this.partialBlockOkay = true;
        } else {
            this.partialBlockOkay = idx > 0 && name.startsWith("OpenPGP", idx);
        }
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        reset();
        this.cipher.init(forEncryption, params);
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    public int getUpdateOutputSize(int len) {
        int leftOver;
        int total = len + this.bufOff;
        if (this.pgpCFB && this.forEncryption) {
            leftOver = (total % this.buf.length) - (this.cipher.getBlockSize() + 2);
        } else {
            leftOver = total % this.buf.length;
        }
        return total - leftOver;
    }

    public int getOutputSize(int length) {
        return length + this.bufOff;
    }

    public int processByte(byte in, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        int resultLen = 0;
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
        }
        return resultLen;
    }

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
        if (this.bufOff == this.buf.length) {
            resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
            this.bufOff = 0;
        }
        return resultLen;
    }

    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
        try {
            int resultLen = 0;
            if (outOff + this.bufOff > out.length) {
                throw new OutputLengthException("output buffer too short for doFinal()");
            }
            if (this.bufOff != 0) {
                if (!this.partialBlockOkay) {
                    throw new DataLengthException("data not block size aligned");
                }
                this.cipher.processBlock(this.buf, 0, this.buf, 0);
                resultLen = this.bufOff;
                this.bufOff = 0;
                System.arraycopy(this.buf, 0, out, outOff, resultLen);
            }
            int i = resultLen;
            reset();
            return i;
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
