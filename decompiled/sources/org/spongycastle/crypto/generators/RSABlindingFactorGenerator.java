package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.params.RSAPrivateCrtKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/generators/RSABlindingFactorGenerator.class */
public class RSABlindingFactorGenerator {
    private static BigInteger ZERO = BigInteger.valueOf(0);
    private static BigInteger ONE = BigInteger.valueOf(1);
    private RSAKeyParameters key;
    private SecureRandom random;

    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.key = (RSAKeyParameters) rParam.getParameters();
            this.random = rParam.getRandom();
        } else {
            this.key = (RSAKeyParameters) param;
            this.random = new SecureRandom();
        }
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("generator requires RSA public key");
        }
    }

    public BigInteger generateBlindingFactor() {
        if (this.key == null) {
            throw new IllegalStateException("generator not initialised");
        }
        BigInteger m = this.key.getModulus();
        int length = m.bitLength() - 1;
        while (true) {
            BigInteger factor = new BigInteger(length, this.random);
            BigInteger gcd = factor.gcd(m);
            if (!factor.equals(ZERO) && !factor.equals(ONE) && gcd.equals(ONE)) {
                return factor;
            }
        }
    }
}
