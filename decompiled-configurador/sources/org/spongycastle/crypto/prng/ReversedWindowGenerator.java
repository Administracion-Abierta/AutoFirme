package org.spongycastle.crypto.prng;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/ReversedWindowGenerator.class */
public class ReversedWindowGenerator implements RandomGenerator {
    private final RandomGenerator generator;
    private byte[] window;
    private int windowCount;

    public ReversedWindowGenerator(RandomGenerator generator, int windowSize) {
        if (generator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        if (windowSize < 2) {
            throw new IllegalArgumentException("windowSize must be at least 2");
        }
        this.generator = generator;
        this.window = new byte[windowSize];
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(byte[] seed) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(long seed) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes) {
        doNextBytes(bytes, 0, bytes.length);
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes, int start, int len) {
        doNextBytes(bytes, start, len);
    }

    private void doNextBytes(byte[] bytes, int start, int len) {
        synchronized (this) {
            int done = 0;
            while (done < len) {
                if (this.windowCount < 1) {
                    this.generator.nextBytes(this.window, 0, this.window.length);
                    this.windowCount = this.window.length;
                }
                int i = done;
                done++;
                int i2 = start + i;
                byte[] bArr = this.window;
                int i3 = this.windowCount - 1;
                this.windowCount = i3;
                bytes[i2] = bArr[i3];
            }
        }
    }
}
