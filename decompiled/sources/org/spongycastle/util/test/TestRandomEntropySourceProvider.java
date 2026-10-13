package org.spongycastle.util.test;

import java.security.SecureRandom;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.crypto.prng.EntropySourceProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/test/TestRandomEntropySourceProvider.class */
public class TestRandomEntropySourceProvider implements EntropySourceProvider {
    private final SecureRandom _sr = new SecureRandom();
    private final boolean _predictionResistant;

    public TestRandomEntropySourceProvider(boolean isPredictionResistant) {
        this._predictionResistant = isPredictionResistant;
    }

    @Override // org.spongycastle.crypto.prng.EntropySourceProvider
    public EntropySource get(final int bitsRequired) {
        return new EntropySource() { // from class: org.spongycastle.util.test.TestRandomEntropySourceProvider.1
            @Override // org.spongycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return TestRandomEntropySourceProvider.this._predictionResistant;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                byte[] rv = new byte[(bitsRequired + 7) / 8];
                TestRandomEntropySourceProvider.this._sr.nextBytes(rv);
                return rv;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public int entropySize() {
                return bitsRequired;
            }
        };
    }
}
