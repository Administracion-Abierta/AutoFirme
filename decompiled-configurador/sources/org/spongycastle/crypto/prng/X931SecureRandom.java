package org.spongycastle.crypto.prng;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/X931SecureRandom.class */
public class X931SecureRandom extends SecureRandom {
    private final boolean predictionResistant;
    private final SecureRandom randomSource;
    private final X931RNG drbg;

    X931SecureRandom(SecureRandom randomSource, X931RNG drbg, boolean predictionResistant) {
        this.randomSource = randomSource;
        this.drbg = drbg;
        this.predictionResistant = predictionResistant;
    }

    @Override // java.security.SecureRandom
    public void setSeed(byte[] seed) {
        synchronized (this) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void setSeed(long seed) {
        synchronized (this) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bytes) {
        synchronized (this) {
            if (this.drbg.generate(bytes, this.predictionResistant) < 0) {
                this.drbg.reseed();
                this.drbg.generate(bytes, this.predictionResistant);
            }
        }
    }

    @Override // java.security.SecureRandom
    public byte[] generateSeed(int numBytes) {
        return EntropyUtil.generateSeed(this.drbg.getEntropySource(), numBytes);
    }
}
