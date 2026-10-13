package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RSABlindedEngine.class */
public class RSABlindedEngine implements AsymmetricBlockCipher {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private SecureRandom random;

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        SecureRandom random = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            random = parametersWithRandom.getRandom();
            cipherParameters = parametersWithRandom.getParameters();
        }
        this.core.init(z, cipherParameters);
        this.key = (RSAKeyParameters) cipherParameters;
        this.random = initSecureRandom(this.key instanceof RSAPrivateCrtKeyParameters, random);
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) {
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        return this.core.convertOutput(processInput(this.core.convertInput(bArr, i, i2)));
    }

    protected SecureRandom initSecureRandom(boolean z, SecureRandom secureRandom) {
        if (z) {
            return CryptoServicesRegistrar.getSecureRandom(secureRandom);
        }
        return null;
    }

    private BigInteger processInput(BigInteger bigInteger) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger publicExponent;
        if (!(this.key instanceof RSAPrivateCrtKeyParameters) || (publicExponent = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters) this.key).getPublicExponent()) == null) {
            return this.core.processBlock(bigInteger);
        }
        BigInteger modulus = rSAPrivateCrtKeyParameters.getModulus();
        BigInteger bigIntegerCreateRandomInRange = BigIntegers.createRandomInRange(ONE, modulus.subtract(ONE), this.random);
        return BigIntegers.modOddInverse(modulus, bigIntegerCreateRandomInRange).multiply(this.core.processBlock(bigIntegerCreateRandomInRange.modPow(publicExponent, modulus).multiply(bigInteger).mod(modulus))).mod(modulus);
    }
}
