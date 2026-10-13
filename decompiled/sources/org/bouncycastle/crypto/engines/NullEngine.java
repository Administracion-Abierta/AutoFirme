package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/NullEngine.class */
public class NullEngine implements BlockCipher {
    private boolean initialised;
    protected static final int DEFAULT_BLOCK_SIZE = 1;
    private final int blockSize;

    public NullEngine() {
        this(1);
    }

    public NullEngine(int i) {
        this.blockSize = i;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.initialised = true;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Null";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException("Null engine not initialised");
        }
        if (i + this.blockSize > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + this.blockSize > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i3 = 0; i3 < this.blockSize; i3++) {
            bArr2[i2 + i3] = bArr[i + i3];
        }
        return this.blockSize;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }
}
