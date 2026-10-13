package org.spongycastle.crypto;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/KeyGenerationParameters.class */
public class KeyGenerationParameters {
    private SecureRandom random;
    private int strength;

    public KeyGenerationParameters(SecureRandom random, int strength) {
        this.random = random;
        this.strength = strength;
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public int getStrength() {
        return this.strength;
    }
}
