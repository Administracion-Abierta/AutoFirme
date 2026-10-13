package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.StreamBlockCipher;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/KCTRBlockCipher.class */
public class KCTRBlockCipher extends StreamBlockCipher {
    private byte[] iv;
    private byte[] ofbV;
    private byte[] ofbOutV;
    private int byteCount;
    private boolean initialised;
    private BlockCipher engine;

    public KCTRBlockCipher(BlockCipher engine) {
        super(engine);
        this.engine = engine;
        this.iv = new byte[engine.getBlockSize()];
        this.ofbV = new byte[engine.getBlockSize()];
        this.ofbOutV = new byte[engine.getBlockSize()];
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.initialised = true;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] iv = ivParam.getIV();
            int diff = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte) 0);
            System.arraycopy(iv, 0, this.iv, diff, iv.length);
            CipherParameters params2 = ivParam.getParameters();
            if (params2 != null) {
                this.engine.init(true, params2);
            }
            reset();
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed");
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCTR";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.engine.getBlockSize();
    }

    @Override // org.spongycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte b) {
        if (this.byteCount == 0) {
            incrementCounterAt(0);
            checkCounter();
            this.engine.processBlock(this.ofbV, 0, this.ofbOutV, 0);
            byte[] bArr = this.ofbOutV;
            int i = this.byteCount;
            this.byteCount = i + 1;
            return (byte) (bArr[i] ^ b);
        }
        byte[] bArr2 = this.ofbOutV;
        int i2 = this.byteCount;
        this.byteCount = i2 + 1;
        byte rv = (byte) (bArr2[i2] ^ b);
        if (this.byteCount == this.ofbV.length) {
            this.byteCount = 0;
        }
        return rv;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (in.length - inOff < getBlockSize()) {
            throw new DataLengthException("input buffer too short");
        }
        if (out.length - outOff < getBlockSize()) {
            throw new OutputLengthException("output buffer too short");
        }
        processBytes(in, inOff, getBlockSize(), out, outOff);
        return getBlockSize();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
        if (this.initialised) {
            this.engine.processBlock(this.iv, 0, this.ofbV, 0);
        }
        this.engine.reset();
        this.byteCount = 0;
    }

    private void incrementCounterAt(int pos) {
        int i = pos;
        while (i < this.ofbV.length) {
            byte[] bArr = this.ofbV;
            int i2 = i;
            i++;
            byte b = (byte) (bArr[i2] + 1);
            bArr[i2] = b;
            if (b != 0) {
                return;
            }
        }
    }

    private void checkCounter() {
    }
}
