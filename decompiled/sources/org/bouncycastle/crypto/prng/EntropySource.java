package org.bouncycastle.crypto.prng;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/EntropySource.class */
public interface EntropySource {
    boolean isPredictionResistant();

    byte[] getEntropy();

    int entropySize();
}
