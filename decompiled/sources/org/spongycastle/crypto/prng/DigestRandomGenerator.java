package org.spongycastle.crypto.prng;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/DigestRandomGenerator.class */
public class DigestRandomGenerator implements RandomGenerator {
    private static long CYCLE_COUNT = 10;
    private Digest digest;
    private byte[] state;
    private byte[] seed;
    private long seedCounter = 1;
    private long stateCounter = 1;

    public DigestRandomGenerator(Digest digest) {
        this.digest = digest;
        this.seed = new byte[digest.getDigestSize()];
        this.state = new byte[digest.getDigestSize()];
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(byte[] inSeed) {
        synchronized (this) {
            digestUpdate(inSeed);
            digestUpdate(this.seed);
            digestDoFinal(this.seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(long rSeed) {
        synchronized (this) {
            digestAddCounter(rSeed);
            digestUpdate(this.seed);
            digestDoFinal(this.seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes) {
        nextBytes(bytes, 0, bytes.length);
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes, int start, int len) {
        synchronized (this) {
            int stateOff = 0;
            generateState();
            int end = start + len;
            for (int i = start; i != end; i++) {
                if (stateOff == this.state.length) {
                    generateState();
                    stateOff = 0;
                }
                int i2 = stateOff;
                stateOff++;
                bytes[i] = this.state[i2];
            }
        }
    }

    private void cycleSeed() {
        digestUpdate(this.seed);
        long j = this.seedCounter;
        this.seedCounter = j + 1;
        digestAddCounter(j);
        digestDoFinal(this.seed);
    }

    private void generateState() {
        long j = this.stateCounter;
        this.stateCounter = j + 1;
        digestAddCounter(j);
        digestUpdate(this.state);
        digestUpdate(this.seed);
        digestDoFinal(this.state);
        if (this.stateCounter % CYCLE_COUNT == 0) {
            cycleSeed();
        }
    }

    private void digestAddCounter(long seed) {
        for (int i = 0; i != 8; i++) {
            this.digest.update((byte) seed);
            seed >>>= 8;
        }
    }

    private void digestUpdate(byte[] inSeed) {
        this.digest.update(inSeed, 0, inSeed.length);
    }

    private void digestDoFinal(byte[] result) {
        this.digest.doFinal(result, 0);
    }
}
