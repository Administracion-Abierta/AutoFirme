package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi.class */
public class RainbowKeyPairGeneratorSpi extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    private final RainbowParameters rainbowParameters;
    RainbowKeyGenerationParameters param;
    RainbowKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowIIIcircum.class */
    public static class RainbowIIIcircum extends RainbowKeyPairGeneratorSpi {
        public RainbowIIIcircum() {
            super(RainbowParameters.rainbowIIIcircumzenithal);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowIIIclassic.class */
    public static class RainbowIIIclassic extends RainbowKeyPairGeneratorSpi {
        public RainbowIIIclassic() {
            super(RainbowParameters.rainbowIIIclassic);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowIIIcomp.class */
    public static class RainbowIIIcomp extends RainbowKeyPairGeneratorSpi {
        public RainbowIIIcomp() {
            super(RainbowParameters.rainbowIIIcompressed);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowVcircum.class */
    public static class RainbowVcircum extends RainbowKeyPairGeneratorSpi {
        public RainbowVcircum() {
            super(RainbowParameters.rainbowVcircumzenithal);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowVclassic.class */
    public static class RainbowVclassic extends RainbowKeyPairGeneratorSpi {
        public RainbowVclassic() {
            super(RainbowParameters.rainbowVclassic);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/RainbowKeyPairGeneratorSpi$RainbowVcomp.class */
    public static class RainbowVcomp extends RainbowKeyPairGeneratorSpi {
        public RainbowVcomp() {
            super(RainbowParameters.rainbowVcompressed);
        }
    }

    public RainbowKeyPairGeneratorSpi() {
        super("RAINBOW");
        this.engine = new RainbowKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.rainbowParameters = null;
    }

    protected RainbowKeyPairGeneratorSpi(RainbowParameters rainbowParameters) {
        super(rainbowParameters.getName());
        this.engine = new RainbowKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.rainbowParameters = rainbowParameters;
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
        RainbowParameters rainbowParameters = (RainbowParameters) parameters.get(nameFromParams);
        this.param = new RainbowKeyGenerationParameters(secureRandom, rainbowParameters);
        if (this.rainbowParameters != null && !rainbowParameters.getName().equals(this.rainbowParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.rainbowParameters.getName()));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        return algorithmParameterSpec instanceof RainbowParameterSpec ? ((RainbowParameterSpec) algorithmParameterSpec).getName() : Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            if (this.rainbowParameters != null) {
                this.param = new RainbowKeyGenerationParameters(this.random, this.rainbowParameters);
            } else {
                this.param = new RainbowKeyGenerationParameters(this.random, RainbowParameters.rainbowIIIclassic);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCRainbowPublicKey((RainbowPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCRainbowPrivateKey((RainbowPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }

    static {
        parameters.put(RainbowParameterSpec.rainbowIIIclassic.getName(), RainbowParameters.rainbowIIIclassic);
        parameters.put(RainbowParameterSpec.rainbowIIIcircumzenithal.getName(), RainbowParameters.rainbowIIIcircumzenithal);
        parameters.put(RainbowParameterSpec.rainbowIIIcompressed.getName(), RainbowParameters.rainbowIIIcompressed);
        parameters.put(RainbowParameterSpec.rainbowVclassic.getName(), RainbowParameters.rainbowVclassic);
        parameters.put(RainbowParameterSpec.rainbowVcircumzenithal.getName(), RainbowParameters.rainbowVcircumzenithal);
        parameters.put(RainbowParameterSpec.rainbowVcompressed.getName(), RainbowParameters.rainbowVcompressed);
    }
}
