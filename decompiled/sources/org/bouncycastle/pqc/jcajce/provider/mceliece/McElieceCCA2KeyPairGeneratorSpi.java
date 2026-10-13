package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2Parameters;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2PublicKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/mceliece/McElieceCCA2KeyPairGeneratorSpi.class */
public class McElieceCCA2KeyPairGeneratorSpi extends KeyPairGenerator {
    private McElieceCCA2KeyPairGenerator kpg;

    public McElieceCCA2KeyPairGeneratorSpi() {
        super("McEliece-CCA2");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        McElieceCCA2KeyGenParameterSpec mcElieceCCA2KeyGenParameterSpec = (McElieceCCA2KeyGenParameterSpec) algorithmParameterSpec;
        this.kpg.init(new McElieceCCA2KeyGenerationParameters(secureRandom, new McElieceCCA2Parameters(mcElieceCCA2KeyGenParameterSpec.getM(), mcElieceCCA2KeyGenParameterSpec.getT(), mcElieceCCA2KeyGenParameterSpec.getDigest())));
    }

    @Override // java.security.KeyPairGenerator
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        McElieceCCA2KeyGenParameterSpec mcElieceCCA2KeyGenParameterSpec = (McElieceCCA2KeyGenParameterSpec) algorithmParameterSpec;
        this.kpg.init(new McElieceCCA2KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), new McElieceCCA2Parameters(mcElieceCCA2KeyGenParameterSpec.getM(), mcElieceCCA2KeyGenParameterSpec.getT(), mcElieceCCA2KeyGenParameterSpec.getDigest())));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        this.kpg.init(new McElieceCCA2KeyGenerationParameters(secureRandom, new McElieceCCA2Parameters()));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.kpg.generateKeyPair();
        return new KeyPair(new BCMcElieceCCA2PublicKey((McElieceCCA2PublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCMcElieceCCA2PrivateKey((McElieceCCA2PrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }
}
