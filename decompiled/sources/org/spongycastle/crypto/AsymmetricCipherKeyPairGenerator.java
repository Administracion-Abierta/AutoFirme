package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/AsymmetricCipherKeyPairGenerator.class */
public interface AsymmetricCipherKeyPairGenerator {
    void init(KeyGenerationParameters keyGenerationParameters);

    AsymmetricCipherKeyPair generateKeyPair();
}
