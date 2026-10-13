package org.spongycastle.crypto.prng;

import java.security.SecureRandom;
import org.spongycastle.crypto.prng.drbg.SP80090DRBG;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/SP800SecureRandom.class */
public class SP800SecureRandom extends SecureRandom {
    private final DRBGProvider drbgProvider;
    private final boolean predictionResistant;
    private final SecureRandom randomSource;
    private final EntropySource entropySource;
    private SP80090DRBG drbg;

    SP800SecureRandom(SecureRandom randomSource, EntropySource entropySource, DRBGProvider drbgProvider, boolean predictionResistant) {
        this.randomSource = randomSource;
        this.entropySource = entropySource;
        this.drbgProvider = drbgProvider;
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
            if (this.drbg == null) {
                this.drbg = this.drbgProvider.get(this.entropySource);
            }
            if (this.drbg.generate(bytes, null, this.predictionResistant) < 0) {
                this.drbg.reseed(null);
                this.drbg.generate(bytes, null, this.predictionResistant);
            }
        }
    }

    @Override // java.security.SecureRandom
    public byte[] generateSeed(int numBytes) {
        return EntropyUtil.generateSeed(this.entropySource, numBytes);
    }

    public void reseed(byte[] additionalInput) {
        synchronized (this) {
            if (this.drbg == null) {
                this.drbg = this.drbgProvider.get(this.entropySource);
            }
            this.drbg.reseed(additionalInput);
        }
    }
}
