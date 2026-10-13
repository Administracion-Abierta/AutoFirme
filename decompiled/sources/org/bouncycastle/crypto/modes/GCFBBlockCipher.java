package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/GCFBBlockCipher.class */
public class GCFBBlockCipher extends StreamBlockCipher {
    private static final byte[] C = {105, 0, 114, 34, 100, -55, 4, 35, -115, 58, -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 0, -19, 7, 18, -64, -122, -36, -62, -17, 76, -87, 43};
    private final CFBBlockCipher cfbEngine;
    private ParametersWithIV initParams;
    private KeyParameter key;
    private long counter;
    private boolean forEncryption;

    public GCFBBlockCipher(BlockCipher blockCipher) {
        super(blockCipher);
        this.counter = 0L;
        this.cfbEngine = new CFBBlockCipher(blockCipher, blockCipher.getBlockSize() * 8);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.counter = 0L;
        this.cfbEngine.init(z, cipherParameters);
        byte[] iv = null;
        this.forEncryption = z;
        if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            cipherParameters = parametersWithIV.getParameters();
            iv = parametersWithIV.getIV();
        }
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        if (cipherParameters instanceof ParametersWithSBox) {
            cipherParameters = ((ParametersWithSBox) cipherParameters).getParameters();
        }
        this.key = (KeyParameter) cipherParameters;
        if (this.key == null && this.initParams != null) {
            this.key = (KeyParameter) this.initParams.getParameters();
        }
        this.initParams = new ParametersWithIV(this.key, (iv != null || this.initParams == null) ? this.cfbEngine.getCurrentIV() : this.initParams.getIV());
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        String algorithmName = this.cfbEngine.getAlgorithmName();
        return algorithmName.substring(0, algorithmName.indexOf(47)) + "/G" + algorithmName.substring(algorithmName.indexOf(47) + 1);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cfbEngine.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        processBytes(bArr, i, this.cfbEngine.getBlockSize(), bArr2, i2);
        return this.cfbEngine.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte b) {
        if (this.counter > 0 && (this.counter & 1023) == 0) {
            BlockCipher underlyingCipher = this.cfbEngine.getUnderlyingCipher();
            underlyingCipher.init(false, this.key);
            byte[] bArr = new byte[32];
            int blockSize = underlyingCipher.getBlockSize();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= bArr.length) {
                    break;
                }
                underlyingCipher.processBlock(C, i2, bArr, i2);
                i = i2 + blockSize;
            }
            this.key = new KeyParameter(bArr);
            underlyingCipher.init(true, this.key);
            byte[] currentIV = this.cfbEngine.getCurrentIV();
            underlyingCipher.processBlock(currentIV, 0, currentIV, 0);
            this.cfbEngine.init(this.forEncryption, new ParametersWithIV(this.key, currentIV));
        }
        this.counter++;
        return this.cfbEngine.calculateByte(b);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
        this.counter = 0L;
        if (this.initParams == null) {
            this.cfbEngine.reset();
        } else {
            this.key = (KeyParameter) this.initParams.getParameters();
            this.cfbEngine.init(this.forEncryption, this.initParams);
        }
    }
}
