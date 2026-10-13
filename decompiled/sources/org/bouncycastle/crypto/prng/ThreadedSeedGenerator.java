package org.bouncycastle.crypto.prng;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/ThreadedSeedGenerator.class */
public class ThreadedSeedGenerator {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/ThreadedSeedGenerator$SeedGenerator.class */
    private static class SeedGenerator implements Runnable {
        private volatile int counter;
        private volatile boolean stop;

        private SeedGenerator() {
            this.counter = 0;
            this.stop = false;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (!this.stop) {
                this.counter++;
            }
        }

        public byte[] generateSeed(int i, boolean z) {
            Thread thread = new Thread(this);
            byte[] bArr = new byte[i];
            this.counter = 0;
            this.stop = false;
            int i2 = 0;
            thread.start();
            int i3 = z ? i : i * 8;
            for (int i4 = 0; i4 < i3; i4++) {
                while (this.counter == i2) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                    }
                }
                i2 = this.counter;
                if (z) {
                    bArr[i4] = (byte) (i2 & 255);
                } else {
                    int i5 = i4 / 8;
                    bArr[i5] = (byte) ((bArr[i5] << 1) | (i2 & 1));
                }
            }
            this.stop = true;
            return bArr;
        }
    }

    public byte[] generateSeed(int i, boolean z) {
        return new SeedGenerator().generateSeed(i, z);
    }
}
