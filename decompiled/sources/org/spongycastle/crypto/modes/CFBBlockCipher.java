package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.StreamBlockCipher;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/CFBBlockCipher.class */
public class CFBBlockCipher extends StreamBlockCipher {
    private byte[] IV;
    private byte[] cfbV;
    private byte[] cfbOutV;
    private byte[] inBuf;
    private int blockSize;
    private BlockCipher cipher;
    private boolean encrypting;
    private int byteCount;

    public CFBBlockCipher(BlockCipher cipher, int bitBlockSize) {
        super(cipher);
        this.cipher = null;
        this.cipher = cipher;
        this.blockSize = bitBlockSize / 8;
        this.IV = new byte[cipher.getBlockSize()];
        this.cfbV = new byte[cipher.getBlockSize()];
        this.cfbOutV = new byte[cipher.getBlockSize()];
        this.inBuf = new byte[this.blockSize];
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        this.encrypting = encrypting;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] iv = ivParam.getIV();
            if (iv.length < this.IV.length) {
                System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
                for (int i = 0; i < this.IV.length - iv.length; i++) {
                    this.IV[i] = 0;
                }
            } else {
                System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
            }
            reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
                return;
            }
            return;
        }
        reset();
        if (params != null) {
            this.cipher.init(true, params);
        }
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CFB" + (this.blockSize * 8);
    }

    @Override // org.spongycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte in) throws IllegalStateException, DataLengthException {
        return this.encrypting ? encryptByte(in) : decryptByte(in);
    }

    private byte encryptByte(byte in) {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
        }
        byte rv = (byte) (this.cfbOutV[this.byteCount] ^ in);
        byte[] bArr = this.inBuf;
        int i = this.byteCount;
        this.byteCount = i + 1;
        bArr[i] = rv;
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
        }
        return rv;
    }

    private byte decryptByte(byte in) {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
        }
        this.inBuf[this.byteCount] = in;
        byte[] bArr = this.cfbOutV;
        int i = this.byteCount;
        this.byteCount = i + 1;
        byte rv = (byte) (bArr[i] ^ in);
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
        }
        return rv;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    public int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    public int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    public byte[] getCurrentIV() {
        return Arrays.clone(this.cfbV);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
        System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
        Arrays.fill(this.inBuf, (byte) 0);
        this.byteCount = 0;
        this.cipher.reset();
    }
}
