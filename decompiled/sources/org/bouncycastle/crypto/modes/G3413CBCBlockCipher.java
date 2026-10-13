package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/G3413CBCBlockCipher.class */
public class G3413CBCBlockCipher implements BlockCipher {
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private BlockCipher cipher;
    private boolean initialized = false;
    private boolean forEncryption;

    public G3413CBCBlockCipher(BlockCipher blockCipher) {
        this.blockSize = blockCipher.getBlockSize();
        this.cipher = blockCipher;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = z;
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
                this.cipher.init(z, parametersWithIV.getParameters());
            }
        } else {
            setupDefaultParams();
            initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (cipherParameters != null) {
                this.cipher.init(z, cipherParameters);
            }
        }
        this.initialized = true;
    }

    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }

    private void setupDefaultParams() {
        this.m = this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        return this.forEncryption ? encrypt(bArr, i, bArr2, i2) : decrypt(bArr, i, bArr2, i2);
    }

    private int encrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        byte[] bArrSum = GOST3413CipherUtil.sum(GOST3413CipherUtil.copyFromInput(bArr, this.blockSize, i), GOST3413CipherUtil.MSB(this.R, this.blockSize));
        byte[] bArr3 = new byte[bArrSum.length];
        this.cipher.processBlock(bArrSum, 0, bArr3, 0);
        System.arraycopy(bArr3, 0, bArr2, i2, bArr3.length);
        if (bArr2.length > i2 + bArrSum.length) {
            generateR(bArr3);
        }
        return bArr3.length;
    }

    private int decrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        byte[] bArrMSB = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        byte[] bArrCopyFromInput = GOST3413CipherUtil.copyFromInput(bArr, this.blockSize, i);
        byte[] bArr3 = new byte[bArrCopyFromInput.length];
        this.cipher.processBlock(bArrCopyFromInput, 0, bArr3, 0);
        byte[] bArrSum = GOST3413CipherUtil.sum(bArr3, bArrMSB);
        System.arraycopy(bArrSum, 0, bArr2, i2, bArrSum.length);
        if (bArr2.length > i2 + bArrSum.length) {
            generateR(bArrCopyFromInput);
        }
        return bArrSum.length;
    }

    private void generateR(byte[] bArr) {
        byte[] bArrLSB = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(bArrLSB, 0, this.R, 0, bArrLSB.length);
        System.arraycopy(bArr, 0, this.R, bArrLSB.length, this.m - bArrLSB.length);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            this.cipher.reset();
        }
    }
}
