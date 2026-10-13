package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/CramerShoupParametersGenerator.class */
public class CramerShoupParametersGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private int size;
    private int certainty;
    private SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/CramerShoupParametersGenerator$ParametersHelper.class */
    private static class ParametersHelper {
        private static final BigInteger TWO = BigInteger.valueOf(2);

        private ParametersHelper() {
        }

        static BigInteger[] generateSafePrimes(int i, int i2, SecureRandom secureRandom) {
            BigInteger bigIntegerCreateRandomPrime;
            BigInteger bigIntegerAdd;
            int i3 = i - 1;
            while (true) {
                bigIntegerCreateRandomPrime = BigIntegers.createRandomPrime(i3, 2, secureRandom);
                bigIntegerAdd = bigIntegerCreateRandomPrime.shiftLeft(1).add(CramerShoupParametersGenerator.ONE);
                if (bigIntegerAdd.isProbablePrime(i2) && (i2 <= 2 || bigIntegerCreateRandomPrime.isProbablePrime(i2))) {
                    break;
                }
            }
            return new BigInteger[]{bigIntegerAdd, bigIntegerCreateRandomPrime};
        }

        static BigInteger selectGenerator(BigInteger bigInteger, SecureRandom secureRandom) {
            BigInteger bigIntegerModPow;
            BigInteger bigIntegerSubtract = bigInteger.subtract(TWO);
            do {
                bigIntegerModPow = BigIntegers.createRandomInRange(TWO, bigIntegerSubtract, secureRandom).modPow(TWO, bigInteger);
            } while (bigIntegerModPow.equals(CramerShoupParametersGenerator.ONE));
            return bigIntegerModPow;
        }
    }

    public void init(int i, int i2, SecureRandom secureRandom) {
        this.size = i;
        this.certainty = i2;
        this.random = secureRandom;
    }

    public CramerShoupParameters generateParameters() {
        BigInteger bigInteger = ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random)[1];
        BigInteger bigIntegerSelectGenerator = ParametersHelper.selectGenerator(bigInteger, this.random);
        BigInteger bigIntegerSelectGenerator2 = ParametersHelper.selectGenerator(bigInteger, this.random);
        while (true) {
            BigInteger bigInteger2 = bigIntegerSelectGenerator2;
            if (!bigIntegerSelectGenerator.equals(bigInteger2)) {
                return new CramerShoupParameters(bigInteger, bigIntegerSelectGenerator, bigInteger2, SHA256Digest.newInstance());
            }
            bigIntegerSelectGenerator2 = ParametersHelper.selectGenerator(bigInteger, this.random);
        }
    }

    public CramerShoupParameters generateParameters(DHParameters dHParameters) {
        BigInteger p = dHParameters.getP();
        BigInteger g = dHParameters.getG();
        BigInteger bigIntegerSelectGenerator = ParametersHelper.selectGenerator(p, this.random);
        while (true) {
            BigInteger bigInteger = bigIntegerSelectGenerator;
            if (!g.equals(bigInteger)) {
                return new CramerShoupParameters(p, g, bigInteger, SHA256Digest.newInstance());
            }
            bigIntegerSelectGenerator = ParametersHelper.selectGenerator(p, this.random);
        }
    }
}
