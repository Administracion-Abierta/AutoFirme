package org.bouncycastle.crypto.prng;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/ReversedWindowGenerator.class */
public class ReversedWindowGenerator implements RandomGenerator {
    private final RandomGenerator generator;
    private byte[] window;
    private int windowCount;

    public ReversedWindowGenerator(RandomGenerator randomGenerator, int i) {
        if (randomGenerator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        if (i < 2) {
            throw new IllegalArgumentException("windowSize must be at least 2");
        }
        this.generator = randomGenerator;
        this.window = new byte[i];
    }

    @Override // org.bouncycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(byte[] bArr) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(bArr);
        }
    }

    @Override // org.bouncycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(long j) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(j);
        }
    }

    @Override // org.bouncycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bArr) {
        doNextBytes(bArr, 0, bArr.length);
    }

    @Override // org.bouncycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bArr, int i, int i2) {
        doNextBytes(bArr, i, i2);
    }

    private void doNextBytes(byte[] bArr, int i, int i2) {
        synchronized (this) {
            int i3 = 0;
            while (i3 < i2) {
                if (this.windowCount < 1) {
                    this.generator.nextBytes(this.window, 0, this.window.length);
                    this.windowCount = this.window.length;
                }
                int i4 = i3;
                i3++;
                int i5 = i + i4;
                byte[] bArr2 = this.window;
                int i6 = this.windowCount - 1;
                this.windowCount = i6;
                bArr[i5] = bArr2[i6];
            }
        }
    }
}
