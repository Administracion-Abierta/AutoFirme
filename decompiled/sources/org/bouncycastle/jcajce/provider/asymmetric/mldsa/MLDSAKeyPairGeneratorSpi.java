package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi.class */
public class MLDSAKeyPairGeneratorSpi extends KeyPairGenerator {
    private final MLDSAParameters mldsaParameters;
    MLDSAKeyGenerationParameters param;
    MLDSAKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$Hash.class */
    public static class Hash extends MLDSAKeyPairGeneratorSpi {
        public Hash() throws NoSuchAlgorithmException {
            super("HASH-ML-DSA");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA44.class */
    public static class MLDSA44 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA44() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_44);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA44withSHA512.class */
    public static class MLDSA44withSHA512 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA44withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_44_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA65.class */
    public static class MLDSA65 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA65() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_65);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA65withSHA512.class */
    public static class MLDSA65withSHA512 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA65withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_65_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA87.class */
    public static class MLDSA87 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA87() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_87);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$MLDSA87withSHA512.class */
    public static class MLDSA87withSHA512 extends MLDSAKeyPairGeneratorSpi {
        public MLDSA87withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_87_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyPairGeneratorSpi$Pure.class */
    public static class Pure extends MLDSAKeyPairGeneratorSpi {
        public Pure() throws NoSuchAlgorithmException {
            super("ML-DSA");
        }
    }

    public MLDSAKeyPairGeneratorSpi(String str) {
        super(str);
        this.engine = new MLDSAKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.mldsaParameters = null;
    }

    protected MLDSAKeyPairGeneratorSpi(MLDSAParameterSpec mLDSAParameterSpec) {
        super(Strings.toUpperCase(mLDSAParameterSpec.getName()));
        this.engine = new MLDSAKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.mldsaParameters = Utils.getParameters(mLDSAParameterSpec.getName());
        if (this.param == null) {
            this.param = new MLDSAKeyGenerationParameters(this.random, this.mldsaParameters);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        try {
            initialize(algorithmParameterSpec, new BCJcaJceHelper().createSecureRandom("DEFAULT"));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("unable to find DEFAULT DRBG");
        }
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String nameFromParams = getNameFromParams(algorithmParameterSpec);
        if (nameFromParams == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        MLDSAParameters parameters = Utils.getParameters(nameFromParams);
        if (parameters == null) {
            throw new InvalidAlgorithmParameterException("unknown parameter set name: " + nameFromParams);
        }
        this.param = new MLDSAKeyGenerationParameters(secureRandom, parameters);
        if (this.mldsaParameters != null && !parameters.getName().equals(this.mldsaParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + MLDSAParameterSpec.fromName(this.mldsaParameters.getName()).getName());
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            if (getAlgorithm().startsWith("HASH")) {
                this.param = new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87_with_sha512);
            } else {
                this.param = new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCMLDSAPublicKey((MLDSAPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCMLDSAPrivateKey((MLDSAPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        return algorithmParameterSpec instanceof MLDSAParameterSpec ? ((MLDSAParameterSpec) algorithmParameterSpec).getName() : Strings.toUpperCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }
}
