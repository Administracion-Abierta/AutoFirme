package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.EphemeralKeyPair;
import org.spongycastle.crypto.KeyEncoder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/generators/EphemeralKeyPairGenerator.class */
public class EphemeralKeyPairGenerator {
    private AsymmetricCipherKeyPairGenerator gen;
    private KeyEncoder keyEncoder;

    public EphemeralKeyPairGenerator(AsymmetricCipherKeyPairGenerator gen, KeyEncoder keyEncoder) {
        this.gen = gen;
        this.keyEncoder = keyEncoder;
    }

    public EphemeralKeyPair generate() {
        AsymmetricCipherKeyPair eph = this.gen.generateKeyPair();
        return new EphemeralKeyPair(eph, this.keyEncoder);
    }
}
