package org.spongycastle.crypto.prng;

import org.spongycastle.crypto.BlockCipher;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/X931RNG.class */
public class X931RNG {
    private static final long BLOCK64_RESEED_MAX = 32768;
    private static final long BLOCK128_RESEED_MAX = 8388608;
    private static final int BLOCK64_MAX_BITS_REQUEST = 4096;
    private static final int BLOCK128_MAX_BITS_REQUEST = 262144;
    private final BlockCipher engine;
    private final EntropySource entropySource;
    private final byte[] DT;
    private final byte[] I;
    private final byte[] R;
    private byte[] V;
    private long reseedCounter = 1;

    public X931RNG(BlockCipher engine, byte[] dateTimeVector, EntropySource entropySource) {
        this.engine = engine;
        this.entropySource = entropySource;
        this.DT = new byte[engine.getBlockSize()];
        System.arraycopy(dateTimeVector, 0, this.DT, 0, this.DT.length);
        this.I = new byte[engine.getBlockSize()];
        this.R = new byte[engine.getBlockSize()];
    }

    int generate(byte[] output, boolean predictionResistant) {
        if (this.R.length == 8) {
            if (this.reseedCounter > BLOCK64_RESEED_MAX) {
                return -1;
            }
            if (isTooLarge(output, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        } else {
            if (this.reseedCounter > BLOCK128_RESEED_MAX) {
                return -1;
            }
            if (isTooLarge(output, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (predictionResistant || this.V == null) {
            this.V = this.entropySource.getEntropy();
            if (this.V.length != this.engine.getBlockSize()) {
                throw new IllegalStateException("Insufficient entropy returned");
            }
        }
        int m = output.length / this.R.length;
        for (int i = 0; i < m; i++) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            process(this.R, this.I, this.V);
            process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, output, i * this.R.length, this.R.length);
            increment(this.DT);
        }
        int bytesToCopy = output.length - (m * this.R.length);
        if (bytesToCopy > 0) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            process(this.R, this.I, this.V);
            process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, output, m * this.R.length, bytesToCopy);
            increment(this.DT);
        }
        this.reseedCounter++;
        return output.length;
    }

    void reseed() {
        this.V = this.entropySource.getEntropy();
        if (this.V.length != this.engine.getBlockSize()) {
            throw new IllegalStateException("Insufficient entropy returned");
        }
        this.reseedCounter = 1L;
    }

    EntropySource getEntropySource() {
        return this.entropySource;
    }

    private void process(byte[] res, byte[] a, byte[] b) {
        for (int i = 0; i != res.length; i++) {
            res[i] = (byte) (a[i] ^ b[i]);
        }
        this.engine.processBlock(res, 0, res, 0);
    }

    private void increment(byte[] val) {
        for (int i = val.length - 1; i >= 0; i--) {
            int i2 = i;
            byte b = (byte) (val[i2] + 1);
            val[i2] = b;
            if (b != 0) {
                return;
            }
        }
    }

    private static boolean isTooLarge(byte[] bytes, int maxBytes) {
        return bytes != null && bytes.length > maxBytes;
    }
}
