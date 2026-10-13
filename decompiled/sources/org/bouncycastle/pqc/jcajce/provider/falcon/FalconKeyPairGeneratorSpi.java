package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/FalconKeyPairGeneratorSpi.class */
public class FalconKeyPairGeneratorSpi extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    private final FalconParameters falconParameters;
    FalconKeyGenerationParameters param;
    FalconKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/FalconKeyPairGeneratorSpi$Falcon1024.class */
    public static class Falcon1024 extends FalconKeyPairGeneratorSpi {
        public Falcon1024() {
            super(FalconParameters.falcon_1024);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/FalconKeyPairGeneratorSpi$Falcon512.class */
    public static class Falcon512 extends FalconKeyPairGeneratorSpi {
        public Falcon512() {
            super(FalconParameters.falcon_512);
        }
    }

    public FalconKeyPairGeneratorSpi() {
        super("FALCON");
        this.engine = new FalconKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.falconParameters = null;
    }

    protected FalconKeyPairGeneratorSpi(FalconParameters falconParameters) {
        super(falconParameters.getName());
        this.engine = new FalconKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.falconParameters = falconParameters;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String nameFromParams = getNameFromParams(algorithmParameterSpec);
        if (nameFromParams == null || !parameters.containsKey(nameFromParams)) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        FalconParameters falconParameters = (FalconParameters) parameters.get(nameFromParams);
        this.param = new FalconKeyGenerationParameters(secureRandom, falconParameters);
        if (this.falconParameters != null && !falconParameters.getName().equals(this.falconParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.falconParameters.getName()));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        return algorithmParameterSpec instanceof FalconParameterSpec ? ((FalconParameterSpec) algorithmParameterSpec).getName() : Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            if (this.falconParameters != null) {
                this.param = new FalconKeyGenerationParameters(this.random, this.falconParameters);
            } else {
                this.param = new FalconKeyGenerationParameters(this.random, FalconParameters.falcon_512);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCFalconPublicKey((FalconPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCFalconPrivateKey((FalconPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }

    static {
        parameters.put(FalconParameterSpec.falcon_512.getName(), FalconParameters.falcon_512);
        parameters.put(FalconParameterSpec.falcon_1024.getName(), FalconParameters.falcon_1024);
    }
}
