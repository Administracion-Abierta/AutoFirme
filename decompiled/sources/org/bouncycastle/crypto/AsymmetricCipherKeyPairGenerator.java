package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/AsymmetricCipherKeyPairGenerator.class */
public interface AsymmetricCipherKeyPairGenerator {
    void init(KeyGenerationParameters keyGenerationParameters);

    AsymmetricCipherKeyPair generateKeyPair();
}
