package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RSAEngine.class */
public class RSAEngine implements AsymmetricBlockCipher {
    private RSACoreEngine core;

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        if (this.core == null) {
            this.core = new RSACoreEngine();
        }
        this.core.init(forEncryption, param);
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) {
        if (this.core == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        return this.core.convertOutput(this.core.processBlock(this.core.convertInput(in, inOff, inLen)));
    }
}
