package org.bouncycastle.crypto.prng;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/RandomGenerator.class */
public interface RandomGenerator {
    void addSeedMaterial(byte[] bArr);

    void addSeedMaterial(long j);

    void nextBytes(byte[] bArr);

    void nextBytes(byte[] bArr, int i, int i2);
}
