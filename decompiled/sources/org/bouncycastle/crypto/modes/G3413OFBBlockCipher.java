package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/G3413OFBBlockCipher.class */
public class G3413OFBBlockCipher extends StreamBlockCipher {
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private byte[] Y;
    private BlockCipher cipher;
    private int byteCount;
    private boolean initialized;

    public G3413OFBBlockCipher(BlockCipher blockCipher) {
        super(blockCipher);
        this.initialized = false;
        this.blockSize = blockCipher.getBlockSize();
        this.cipher = blockCipher;
        this.Y = new byte[this.blockSize];
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            byte[] iv = parametersWithIV.getIV();
            if (iv.length < this.blockSize) {
                throw new IllegalArgumentException("Parameter m must blockSize <= m");
            }
            this.m = iv.length;
            initArrays();
            this.R_init = Arrays.clone(iv);
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(true, parametersWithIV.getParameters());
            }
        } else {
            setupDefaultParams();
            initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (cipherParameters != null) {
                this.cipher.init(true, cipherParameters);
            }
        }
        this.initialized = true;
    }

    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }

    private void setupDefaultParams() {
        this.m = 2 * this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OFB";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        processBytes(bArr, i, this.blockSize, bArr2, i2);
        return this.blockSize;
    }

    @Override // org.bouncycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte b) {
        if (this.byteCount == 0) {
            generateY();
        }
        byte b2 = (byte) (this.Y[this.byteCount] ^ b);
        this.byteCount++;
        if (this.byteCount == getBlockSize()) {
            this.byteCount = 0;
            generateR();
        }
        return b2;
    }

    private void generateY() {
        this.cipher.processBlock(GOST3413CipherUtil.MSB(this.R, this.blockSize), 0, this.Y, 0);
    }

    private void generateR() {
        byte[] bArrLSB = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(bArrLSB, 0, this.R, 0, bArrLSB.length);
        System.arraycopy(this.Y, 0, this.R, bArrLSB.length, this.m - bArrLSB.length);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            Arrays.clear(this.Y);
            this.byteCount = 0;
            this.cipher.reset();
        }
    }
}
