package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.spongycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/McElieceCCA2KeyPairGeneratorSpi.class */
public class McElieceCCA2KeyPairGeneratorSpi extends KeyPairGenerator {
    private McElieceCCA2KeyPairGenerator kpg;

    public McElieceCCA2KeyPairGeneratorSpi() {
        super("McEliece-CCA2");
    }

    @Override // java.security.KeyPairGenerator
    public void initialize(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        super.initialize(params);
        McElieceCCA2KeyGenParameterSpec ecc = (McElieceCCA2KeyGenParameterSpec) params;
        McElieceCCA2KeyGenerationParameters mccca2KGParams = new McElieceCCA2KeyGenerationParameters(new SecureRandom(), new McElieceCCA2Parameters(ecc.getM(), ecc.getT(), ecc.getDigest()));
        this.kpg.init(mccca2KGParams);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int keySize, SecureRandom random) {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        McElieceCCA2KeyGenerationParameters mccca2KGParams = new McElieceCCA2KeyGenerationParameters(random, new McElieceCCA2Parameters());
        this.kpg.init(mccca2KGParams);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        AsymmetricCipherKeyPair generateKeyPair = this.kpg.generateKeyPair();
        McElieceCCA2PrivateKeyParameters sk = (McElieceCCA2PrivateKeyParameters) generateKeyPair.getPrivate();
        McElieceCCA2PublicKeyParameters pk = (McElieceCCA2PublicKeyParameters) generateKeyPair.getPublic();
        return new KeyPair(new BCMcElieceCCA2PublicKey(pk), new BCMcElieceCCA2PrivateKey(sk));
    }
}
