package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/DESedeEngine.class */
public class DESedeEngine extends DESBase implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey1 = null;
    private int[] workingKey2 = null;
    private int[] workingKey3 = null;
    private boolean forEncryption;

    public DESedeEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), bitsOfSecurity()));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DESede init - " + cipherParameters.getClass().getName());
        }
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        if (key.length != 24 && key.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
        }
        this.forEncryption = z;
        byte[] bArr = new byte[8];
        System.arraycopy(key, 0, bArr, 0, bArr.length);
        this.workingKey1 = generateWorkingKey(z, bArr);
        byte[] bArr2 = new byte[8];
        System.arraycopy(key, 8, bArr2, 0, bArr2.length);
        this.workingKey2 = generateWorkingKey(!z, bArr2);
        if (key.length == 24) {
            byte[] bArr3 = new byte[8];
            System.arraycopy(key, 16, bArr3, 0, bArr3.length);
            this.workingKey3 = generateWorkingKey(z, bArr3);
        } else {
            this.workingKey3 = this.workingKey1;
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), bitsOfSecurity(), cipherParameters, Utils.getPurpose(this.forEncryption)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.workingKey1 == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (i + 8 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + 8 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        byte[] bArr3 = new byte[8];
        if (this.forEncryption) {
            desFunc(this.workingKey1, bArr, i, bArr3, 0);
            desFunc(this.workingKey2, bArr3, 0, bArr3, 0);
            desFunc(this.workingKey3, bArr3, 0, bArr2, i2);
            return 8;
        }
        desFunc(this.workingKey3, bArr, i, bArr3, 0);
        desFunc(this.workingKey2, bArr3, 0, bArr3, 0);
        desFunc(this.workingKey1, bArr3, 0, bArr2, i2);
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private int bitsOfSecurity() {
        return (this.workingKey1 == null || this.workingKey1 != this.workingKey3) ? 112 : 80;
    }
}
