package org.spongycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.spongycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.spongycastle.pqc.crypto.newhope.NHPublicKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/newhope/NHKeyPairGeneratorSpi.class */
public class NHKeyPairGeneratorSpi extends KeyPairGenerator {
    NHKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    public NHKeyPairGeneratorSpi() {
        super("NH");
        this.engine = new NHKeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        if (strength != 1024) {
            throw new IllegalArgumentException("strength must be 1024 bits");
        }
        this.engine.init(new KeyGenerationParameters(random, 1024));
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("parameter object not recognised");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.engine.init(new KeyGenerationParameters(this.random, 1024));
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        NHPublicKeyParameters pub = (NHPublicKeyParameters) pair.getPublic();
        NHPrivateKeyParameters priv = (NHPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCNHPublicKey(pub), new BCNHPrivateKey(priv));
    }
}
