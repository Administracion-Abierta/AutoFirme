package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.CramerShoupParameters;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/CramerShoupParametersGenerator.class */
public class CramerShoupParametersGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private int size;
    private int certainty;
    private SecureRandom random;

    public void init(int size, int certainty, SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }

    public CramerShoupParameters generateParameters() {
        BigInteger[] safePrimes = ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        BigInteger q = safePrimes[1];
        BigInteger g1 = ParametersHelper.selectGenerator(q, this.random);
        BigInteger bigIntegerSelectGenerator = ParametersHelper.selectGenerator(q, this.random);
        while (true) {
            BigInteger g2 = bigIntegerSelectGenerator;
            if (g1.equals(g2)) {
                bigIntegerSelectGenerator = ParametersHelper.selectGenerator(q, this.random);
            } else {
                return new CramerShoupParameters(q, g1, g2, new SHA256Digest());
            }
        }
    }

    public CramerShoupParameters generateParameters(DHParameters dhParams) {
        BigInteger p = dhParams.getP();
        BigInteger g1 = dhParams.getG();
        BigInteger bigIntegerSelectGenerator = ParametersHelper.selectGenerator(p, this.random);
        while (true) {
            BigInteger g2 = bigIntegerSelectGenerator;
            if (g1.equals(g2)) {
                bigIntegerSelectGenerator = ParametersHelper.selectGenerator(p, this.random);
            } else {
                return new CramerShoupParameters(p, g1, g2, new SHA256Digest());
            }
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/CramerShoupParametersGenerator$ParametersHelper.class */
    private static class ParametersHelper {
        private static final BigInteger TWO = BigInteger.valueOf(2);

        private ParametersHelper() {
        }

        static BigInteger[] generateSafePrimes(int size, int certainty, SecureRandom random) {
            BigInteger q;
            BigInteger p;
            int qLength = size - 1;
            while (true) {
                q = new BigInteger(qLength, 2, random);
                p = q.shiftLeft(1).add(CramerShoupParametersGenerator.ONE);
                if (p.isProbablePrime(certainty) && (certainty <= 2 || q.isProbablePrime(certainty))) {
                    break;
                }
            }
            return new BigInteger[]{p, q};
        }

        static BigInteger selectGenerator(BigInteger p, SecureRandom random) {
            BigInteger g;
            BigInteger pMinusTwo = p.subtract(TWO);
            do {
                BigInteger h = BigIntegers.createRandomInRange(TWO, pMinusTwo, random);
                g = h.modPow(TWO, p);
            } while (g.equals(CramerShoupParametersGenerator.ONE));
            return g;
        }
    }
}
