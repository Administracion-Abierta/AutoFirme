package org.bouncycastle.jcajce.provider.drbg;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/IncrementalEntropySourceProvider.class */
class IncrementalEntropySourceProvider implements EntropySourceProvider {
    private final SecureRandom random;
    private final boolean predictionResistant;

    public IncrementalEntropySourceProvider(SecureRandom secureRandom, boolean z) {
        this.random = secureRandom;
        this.predictionResistant = z;
    }

    @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
    public EntropySource get(final int i) {
        return new IncrementalEntropySource() { // from class: org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySourceProvider.1
            final int numBytes;

            {
                this.numBytes = (i + 7) / 8;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return IncrementalEntropySourceProvider.this.predictionResistant;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                try {
                    return getEntropy(0L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override // org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource
            public byte[] getEntropy(long j) throws InterruptedException {
                byte[] bArr = new byte[this.numBytes];
                for (int i2 = 0; i2 < this.numBytes / 8; i2++) {
                    IncrementalEntropySourceProvider.sleep(j);
                    byte[] bArrGenerateSeed = IncrementalEntropySourceProvider.this.random.generateSeed(8);
                    System.arraycopy(bArrGenerateSeed, 0, bArr, i2 * 8, bArrGenerateSeed.length);
                }
                int i3 = this.numBytes - ((this.numBytes / 8) * 8);
                if (i3 != 0) {
                    IncrementalEntropySourceProvider.sleep(j);
                    byte[] bArrGenerateSeed2 = IncrementalEntropySourceProvider.this.random.generateSeed(i3);
                    System.arraycopy(bArrGenerateSeed2, 0, bArr, bArr.length - bArrGenerateSeed2.length, bArrGenerateSeed2.length);
                }
                return bArr;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public int entropySize() {
                return i;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sleep(long j) throws InterruptedException {
        if (j != 0) {
            Thread.sleep(j);
        }
    }
}
