package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyPairGenerator;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/DilithiumKeyPairGeneratorSpi.class */
public class DilithiumKeyPairGeneratorSpi extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    private final DilithiumParameters dilithiumParameters;
    DilithiumKeyGenerationParameters param;
    DilithiumKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/DilithiumKeyPairGeneratorSpi$Base2.class */
    public static class Base2 extends DilithiumKeyPairGeneratorSpi {
        public Base2() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/DilithiumKeyPairGeneratorSpi$Base3.class */
    public static class Base3 extends DilithiumKeyPairGeneratorSpi {
        public Base3() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium3);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/DilithiumKeyPairGeneratorSpi$Base5.class */
    public static class Base5 extends DilithiumKeyPairGeneratorSpi {
        public Base5() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium5);
        }
    }

    public DilithiumKeyPairGeneratorSpi() {
        super("DILITHIUM");
        this.engine = new DilithiumKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.dilithiumParameters = null;
    }

    protected DilithiumKeyPairGeneratorSpi(DilithiumParameters dilithiumParameters) {
        super(Strings.toUpperCase(dilithiumParameters.getName()));
        this.engine = new DilithiumKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.dilithiumParameters = dilithiumParameters;
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
        DilithiumParameters dilithiumParameters = (DilithiumParameters) parameters.get(nameFromParams);
        this.param = new DilithiumKeyGenerationParameters(secureRandom, dilithiumParameters);
        if (this.dilithiumParameters != null && !dilithiumParameters.getName().equals(this.dilithiumParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.dilithiumParameters.getName()));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        return algorithmParameterSpec instanceof DilithiumParameterSpec ? ((DilithiumParameterSpec) algorithmParameterSpec).getName() : Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            if (this.dilithiumParameters != null) {
                this.param = new DilithiumKeyGenerationParameters(this.random, this.dilithiumParameters);
            } else {
                this.param = new DilithiumKeyGenerationParameters(this.random, DilithiumParameters.dilithium3);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCDilithiumPublicKey((DilithiumPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCDilithiumPrivateKey((DilithiumPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }

    static {
        parameters.put(DilithiumParameterSpec.dilithium2.getName(), DilithiumParameters.dilithium2);
        parameters.put(DilithiumParameterSpec.dilithium3.getName(), DilithiumParameters.dilithium3);
        parameters.put(DilithiumParameterSpec.dilithium5.getName(), DilithiumParameters.dilithium5);
    }
}
