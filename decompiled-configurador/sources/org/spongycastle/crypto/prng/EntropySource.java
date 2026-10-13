package org.spongycastle.crypto.prng;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/EntropySource.class */
public interface EntropySource {
    boolean isPredictionResistant();

    byte[] getEntropy();

    int entropySize();
}
