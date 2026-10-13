package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/KeyPairGeneratorSpi.class */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    private static Hashtable params = new Hashtable();
    private static Object lock = new Object();
    DSAKeyGenerationParameters param;
    DSAKeyPairGenerator engine;
    int strength;
    SecureRandom random;
    boolean initialised;

    public KeyPairGeneratorSpi() {
        super("DSA");
        this.engine = new DSAKeyPairGenerator();
        this.strength = 2048;
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        if (i < 512 || i > 4096 || ((i < 1024 && i % 64 != 0) || (i >= 1024 && i % 1024 != 0))) {
            throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
        }
        DSAParameterSpec dSADefaultParameters = BouncyCastleProvider.CONFIGURATION.getDSADefaultParameters(i);
        if (dSADefaultParameters != null) {
            this.param = new DSAKeyGenerationParameters(secureRandom, new DSAParameters(dSADefaultParameters.getP(), dSADefaultParameters.getQ(), dSADefaultParameters.getG()));
            this.engine.init(this.param);
            this.initialised = true;
        } else {
            this.strength = i;
            this.random = secureRandom;
            this.initialised = false;
        }
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DSAParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec");
        }
        DSAParameterSpec dSAParameterSpec = (DSAParameterSpec) algorithmParameterSpec;
        this.param = new DSAKeyGenerationParameters(secureRandom, new DSAParameters(dSAParameterSpec.getP(), dSAParameterSpec.getQ(), dSAParameterSpec.getG()));
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        DSAParametersGenerator dSAParametersGenerator;
        if (!this.initialised) {
            Integer numValueOf = Integers.valueOf(this.strength);
            if (params.containsKey(numValueOf)) {
                this.param = (DSAKeyGenerationParameters) params.get(numValueOf);
            } else {
                synchronized (lock) {
                    if (params.containsKey(numValueOf)) {
                        this.param = (DSAKeyGenerationParameters) params.get(numValueOf);
                    } else {
                        int defaultCertainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
                        if (this.strength == 1024) {
                            dSAParametersGenerator = new DSAParametersGenerator();
                            if (Properties.isOverrideSet("org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                                dSAParametersGenerator.init(this.strength, defaultCertainty, this.random);
                            } else {
                                dSAParametersGenerator.init(new DSAParameterGenerationParameters(1024, 160, defaultCertainty, this.random));
                            }
                        } else if (this.strength > 1024) {
                            DSAParameterGenerationParameters dSAParameterGenerationParameters = new DSAParameterGenerationParameters(this.strength, 256, defaultCertainty, this.random);
                            dSAParametersGenerator = new DSAParametersGenerator(SHA256Digest.newInstance());
                            dSAParametersGenerator.init(dSAParameterGenerationParameters);
                        } else {
                            dSAParametersGenerator = new DSAParametersGenerator();
                            dSAParametersGenerator.init(this.strength, defaultCertainty, this.random);
                        }
                        this.param = new DSAKeyGenerationParameters(this.random, dSAParametersGenerator.generateParameters());
                        params.put(numValueOf, this.param);
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCDSAPublicKey((DSAPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCDSAPrivateKey((DSAPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }
}
