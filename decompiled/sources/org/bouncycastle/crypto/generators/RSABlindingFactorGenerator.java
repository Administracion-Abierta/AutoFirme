package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/RSABlindingFactorGenerator.class */
public class RSABlindingFactorGenerator {
    private static BigInteger TWO = BigInteger.valueOf(2);
    private RSAKeyParameters key;
    private SecureRandom random;

    public void init(CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.key = (RSAKeyParameters) parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        } else {
            this.key = (RSAKeyParameters) cipherParameters;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("generator requires RSA public key");
        }
    }

    public BigInteger generateBlindingFactor() {
        if (this.key == null) {
            throw new IllegalStateException("generator not initialised");
        }
        BigInteger modulus = this.key.getModulus();
        int iBitLength = modulus.bitLength() - 1;
        while (true) {
            BigInteger bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(iBitLength, this.random);
            if (bigIntegerCreateRandomBigInteger.compareTo(TWO) >= 0 && BigIntegers.modOddIsCoprime(modulus, bigIntegerCreateRandomBigInteger)) {
                return bigIntegerCreateRandomBigInteger;
            }
        }
    }
}
