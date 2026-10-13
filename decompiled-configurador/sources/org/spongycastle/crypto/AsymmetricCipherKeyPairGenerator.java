package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/AsymmetricCipherKeyPairGenerator.class */
public interface AsymmetricCipherKeyPairGenerator {
    void init(KeyGenerationParameters keyGenerationParameters);

    AsymmetricCipherKeyPair generateKeyPair();
}
