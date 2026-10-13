package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/PGPCFBBlockCipher.class */
public class PGPCFBBlockCipher implements BlockCipher {
    private byte[] IV;
    private byte[] FR;
    private byte[] FRE;
    private byte[] tmp;
    private BlockCipher cipher;
    private int count;
    private int blockSize;
    private boolean forEncryption;
    private boolean inlineIv;

    public PGPCFBBlockCipher(BlockCipher cipher, boolean inlineIv) {
        this.cipher = cipher;
        this.inlineIv = inlineIv;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
        this.tmp = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        if (this.inlineIv) {
            return this.cipher.getAlgorithmName() + "/PGPCFBwithIV";
        }
        return this.cipher.getAlgorithmName() + "/PGPCFB";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (this.inlineIv) {
            return this.forEncryption ? encryptBlockWithIV(in, inOff, out, outOff) : decryptBlockWithIV(in, inOff, out, outOff);
        }
        return this.forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
        this.count = 0;
        for (int i = 0; i != this.FR.length; i++) {
            if (this.inlineIv) {
                this.FR[i] = 0;
            } else {
                this.FR[i] = this.IV[i];
            }
        }
        this.cipher.reset();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
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
            this.cipher.init(true, ivParam.getParameters());
            return;
        }
        reset();
        this.cipher.init(true, params);
    }

    private byte encryptByte(byte data, int blockOff) {
        return (byte) (this.FRE[blockOff] ^ data);
    }

    private int encryptBlockWithIV(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (this.count == 0) {
            if (outOff + (2 * this.blockSize) + 2 > out.length) {
                throw new OutputLengthException("output buffer too short");
            }
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 0; n < this.blockSize; n++) {
                out[outOff + n] = encryptByte(this.IV[n], n);
            }
            System.arraycopy(out, outOff, this.FR, 0, this.blockSize);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            out[outOff + this.blockSize] = encryptByte(this.IV[this.blockSize - 2], 0);
            out[outOff + this.blockSize + 1] = encryptByte(this.IV[this.blockSize - 1], 1);
            System.arraycopy(out, outOff + 2, this.FR, 0, this.blockSize);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n2 = 0; n2 < this.blockSize; n2++) {
                out[outOff + this.blockSize + 2 + n2] = encryptByte(in[inOff + n2], n2);
            }
            System.arraycopy(out, outOff + this.blockSize + 2, this.FR, 0, this.blockSize);
            this.count += (2 * this.blockSize) + 2;
            return (2 * this.blockSize) + 2;
        }
        if (this.count >= this.blockSize + 2) {
            if (outOff + this.blockSize > out.length) {
                throw new OutputLengthException("output buffer too short");
            }
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n3 = 0; n3 < this.blockSize; n3++) {
                out[outOff + n3] = encryptByte(in[inOff + n3], n3);
            }
            System.arraycopy(out, outOff, this.FR, 0, this.blockSize);
        }
        return this.blockSize;
    }

    private int decryptBlockWithIV(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count == 0) {
            for (int n = 0; n < this.blockSize; n++) {
                this.FR[n] = in[inOff + n];
            }
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            this.count += this.blockSize;
            return 0;
        }
        if (this.count == this.blockSize) {
            System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = this.tmp[0];
            this.FR[this.blockSize - 1] = this.tmp[1];
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n2 = 0; n2 < this.blockSize - 2; n2++) {
                out[outOff + n2] = encryptByte(this.tmp[n2 + 2], n2);
            }
            System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
            this.count += 2;
            return this.blockSize - 2;
        }
        if (this.count >= this.blockSize + 2) {
            System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
            out[outOff + 0] = encryptByte(this.tmp[0], this.blockSize - 2);
            out[outOff + 1] = encryptByte(this.tmp[1], this.blockSize - 1);
            System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n3 = 0; n3 < this.blockSize - 2; n3++) {
                out[outOff + n3 + 2] = encryptByte(this.tmp[n3 + 2], n3);
            }
            System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
        }
        return this.blockSize;
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int n = 0; n < this.blockSize; n++) {
            out[outOff + n] = encryptByte(in[inOff + n], n);
        }
        for (int n2 = 0; n2 < this.blockSize; n2++) {
            this.FR[n2] = out[outOff + n2];
        }
        return this.blockSize;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int n = 0; n < this.blockSize; n++) {
            out[outOff + n] = encryptByte(in[inOff + n], n);
        }
        for (int n2 = 0; n2 < this.blockSize; n2++) {
            this.FR[n2] = in[inOff + n2];
        }
        return this.blockSize;
    }
}
