package org.spongycastle.crypto.prng;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/ThreadedSeedGenerator.class */
public class ThreadedSeedGenerator {

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/ThreadedSeedGenerator$SeedGenerator.class */
    private class SeedGenerator implements Runnable {
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

        public byte[] generateSeed(int numbytes, boolean fast) {
            int end;
            Thread t = new Thread(this);
            byte[] result = new byte[numbytes];
            this.counter = 0;
            this.stop = false;
            int last = 0;
            t.start();
            if (fast) {
                end = numbytes;
            } else {
                end = numbytes * 8;
            }
            for (int i = 0; i < end; i++) {
                while (this.counter == last) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                    }
                }
                last = this.counter;
                if (fast) {
                    result[i] = (byte) (last & 255);
                } else {
                    int bytepos = i / 8;
                    result[bytepos] = (byte) ((result[bytepos] << 1) | (last & 1));
                }
            }
            this.stop = true;
            return result;
        }
    }

    public byte[] generateSeed(int numBytes, boolean fast) {
        SeedGenerator gen = new SeedGenerator();
        return gen.generateSeed(numBytes, fast);
    }
}
