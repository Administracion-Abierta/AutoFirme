package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.params.ElGamalParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/ElGamalParametersGenerator.class */
public class ElGamalParametersGenerator {
    private int size;
    private int certainty;
    private SecureRandom random;

    public void init(int size, int certainty, SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }

    public ElGamalParameters generateParameters() {
        BigInteger[] safePrimes = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        BigInteger p = safePrimes[0];
        BigInteger q = safePrimes[1];
        BigInteger g = DHParametersHelper.selectGenerator(p, q, this.random);
        return new ElGamalParameters(p, g);
    }
}
