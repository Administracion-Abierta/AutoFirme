package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/BufferedAsymmetricBlockCipher.class */
public class BufferedAsymmetricBlockCipher {
    protected byte[] buf;
    protected int bufOff;
    private final AsymmetricBlockCipher cipher;

    public BufferedAsymmetricBlockCipher(AsymmetricBlockCipher cipher) {
        this.cipher = cipher;
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public int getBufferPosition() {
        return this.bufOff;
    }

    public void init(boolean forEncryption, CipherParameters params) {
        reset();
        this.cipher.init(forEncryption, params);
        this.buf = new byte[this.cipher.getInputBlockSize() + (forEncryption ? 1 : 0)];
        this.bufOff = 0;
    }

    public int getInputBlockSize() {
        return this.cipher.getInputBlockSize();
    }

    public int getOutputBlockSize() {
        return this.cipher.getOutputBlockSize();
    }

    public void processByte(byte in) {
        if (this.bufOff >= this.buf.length) {
            throw new DataLengthException("attempt to process message too long for cipher");
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
    }

    public void processBytes(byte[] in, int inOff, int len) {
        if (len == 0) {
            return;
        }
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        if (this.bufOff + len > this.buf.length) {
            throw new DataLengthException("attempt to process message too long for cipher");
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    public byte[] doFinal() throws InvalidCipherTextException {
        byte[] out = this.cipher.processBlock(this.buf, 0, this.bufOff);
        reset();
        return out;
    }

    public void reset() {
        if (this.buf != null) {
            for (int i = 0; i < this.buf.length; i++) {
                this.buf[i] = 0;
            }
        }
        this.bufOff = 0;
    }
}
