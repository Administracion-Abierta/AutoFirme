package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSABlindingParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RSABlindingEngine.class */
public class RSABlindingEngine implements AsymmetricBlockCipher {
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private BigInteger blindingFactor;
    private boolean forEncryption;

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        RSABlindingParameters p;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            p = (RSABlindingParameters) rParam.getParameters();
        } else {
            p = (RSABlindingParameters) param;
        }
        this.core.init(forEncryption, p.getPublicKey());
        this.forEncryption = forEncryption;
        this.key = p.getPublicKey();
        this.blindingFactor = p.getBlindingFactor();
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
        BigInteger msg;
        BigInteger msg2 = this.core.convertInput(in, inOff, inLen);
        if (this.forEncryption) {
            msg = blindMessage(msg2);
        } else {
            msg = unblindMessage(msg2);
        }
        return this.core.convertOutput(msg);
    }

    private BigInteger blindMessage(BigInteger msg) {
        BigInteger blindMsg = this.blindingFactor;
        return msg.multiply(blindMsg.modPow(this.key.getExponent(), this.key.getModulus())).mod(this.key.getModulus());
    }

    private BigInteger unblindMessage(BigInteger blindedMsg) {
        BigInteger m = this.key.getModulus();
        BigInteger blindFactorInverse = this.blindingFactor.modInverse(m);
        BigInteger msg = blindedMsg.multiply(blindFactorInverse);
        return msg.mod(m);
    }
}
