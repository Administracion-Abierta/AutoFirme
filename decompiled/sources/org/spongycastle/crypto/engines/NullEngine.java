package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/NullEngine.class */
public class NullEngine implements BlockCipher {
    private boolean initialised;
    protected static final int DEFAULT_BLOCK_SIZE = 1;
    private final int blockSize;

    public NullEngine() {
        this(1);
    }

    public NullEngine(int blockSize) {
        this.blockSize = blockSize;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.initialised = true;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Null";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException("Null engine not initialised");
        }
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < this.blockSize; i++) {
            out[outOff + i] = in[inOff + i];
        }
        return this.blockSize;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }
}
