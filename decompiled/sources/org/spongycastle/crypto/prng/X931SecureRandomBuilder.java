package org.spongycastle.crypto.prng;

import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/X931SecureRandomBuilder.class */
public class X931SecureRandomBuilder {
    private SecureRandom random;
    private EntropySourceProvider entropySourceProvider;
    private byte[] dateTimeVector;

    public X931SecureRandomBuilder() {
        this(new SecureRandom(), false);
    }

    public X931SecureRandomBuilder(SecureRandom entropySource, boolean predictionResistant) {
        this.random = entropySource;
        this.entropySourceProvider = new BasicEntropySourceProvider(this.random, predictionResistant);
    }

    public X931SecureRandomBuilder(EntropySourceProvider entropySourceProvider) {
        this.random = null;
        this.entropySourceProvider = entropySourceProvider;
    }

    public X931SecureRandomBuilder setDateTimeVector(byte[] dateTimeVector) {
        this.dateTimeVector = dateTimeVector;
        return this;
    }

    public X931SecureRandom build(BlockCipher engine, KeyParameter key, boolean predictionResistant) {
        if (this.dateTimeVector == null) {
            this.dateTimeVector = new byte[engine.getBlockSize()];
            Pack.longToBigEndian(System.currentTimeMillis(), this.dateTimeVector, 0);
        }
        engine.init(true, key);
        return new X931SecureRandom(this.random, new X931RNG(engine, this.dateTimeVector, this.entropySourceProvider.get(engine.getBlockSize() * 8)), predictionResistant);
    }
}
