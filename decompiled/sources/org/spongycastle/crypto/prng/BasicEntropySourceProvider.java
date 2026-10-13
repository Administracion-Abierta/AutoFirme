package org.spongycastle.crypto.prng;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/BasicEntropySourceProvider.class */
public class BasicEntropySourceProvider implements EntropySourceProvider {
    private final SecureRandom _sr;
    private final boolean _predictionResistant;

    public BasicEntropySourceProvider(SecureRandom random, boolean isPredictionResistant) {
        this._sr = random;
        this._predictionResistant = isPredictionResistant;
    }

    @Override // org.spongycastle.crypto.prng.EntropySourceProvider
    public EntropySource get(final int bitsRequired) {
        return new EntropySource() { // from class: org.spongycastle.crypto.prng.BasicEntropySourceProvider.1
            @Override // org.spongycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return BasicEntropySourceProvider.this._predictionResistant;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                if (!(BasicEntropySourceProvider.this._sr instanceof SP800SecureRandom) && !(BasicEntropySourceProvider.this._sr instanceof X931SecureRandom)) {
                    return BasicEntropySourceProvider.this._sr.generateSeed((bitsRequired + 7) / 8);
                }
                byte[] rv = new byte[(bitsRequired + 7) / 8];
                BasicEntropySourceProvider.this._sr.nextBytes(rv);
                return rv;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public int entropySize() {
                return bitsRequired;
            }
        };
    }
}
