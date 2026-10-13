package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

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
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi.class */
public class SLHDSAKeyPairGeneratorSpi extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    SLHDSAKeyGenerationParameters param;
    SLHDSAKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Hash.class */
    public static class Hash extends SLHDSAKeyPairGeneratorSpi {
        public Hash() throws NoSuchAlgorithmException {
            super("HASH-SLH-DSA");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_128f.class */
    public static class HashSha2_128f extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_128f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_128s.class */
    public static class HashSha2_128s extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_128s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_192f.class */
    public static class HashSha2_192f extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_192f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_192s.class */
    public static class HashSha2_192s extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_192s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_256f.class */
    public static class HashSha2_256f extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_256f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashSha2_256s.class */
    public static class HashSha2_256s extends SLHDSAKeyPairGeneratorSpi {
        public HashSha2_256s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_128f.class */
    public static class HashShake_128f extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_128f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_128s.class */
    public static class HashShake_128s extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_128s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_192f.class */
    public static class HashShake_192f extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_192f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_192s.class */
    public static class HashShake_192s extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_192s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_256f.class */
    public static class HashShake_256f extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_256f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$HashShake_256s.class */
    public static class HashShake_256s extends SLHDSAKeyPairGeneratorSpi {
        public HashShake_256s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Pure.class */
    public static class Pure extends SLHDSAKeyPairGeneratorSpi {
        public Pure() throws NoSuchAlgorithmException {
            super("SLH-DSA");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_128f.class */
    public static class Sha2_128f extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_128f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_128s.class */
    public static class Sha2_128s extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_128s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_128s);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_192f.class */
    public static class Sha2_192f extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_192f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_192s.class */
    public static class Sha2_192s extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_192s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_192s);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_256f.class */
    public static class Sha2_256f extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_256f() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Sha2_256s.class */
    public static class Sha2_256s extends SLHDSAKeyPairGeneratorSpi {
        public Sha2_256s() {
            super(SLHDSAParameterSpec.slh_dsa_sha2_256s);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_128f.class */
    public static class Shake_128f extends SLHDSAKeyPairGeneratorSpi {
        public Shake_128f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_128s.class */
    public static class Shake_128s extends SLHDSAKeyPairGeneratorSpi {
        public Shake_128s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_128s);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_192f.class */
    public static class Shake_192f extends SLHDSAKeyPairGeneratorSpi {
        public Shake_192f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_192s.class */
    public static class Shake_192s extends SLHDSAKeyPairGeneratorSpi {
        public Shake_192s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_192s);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_256f.class */
    public static class Shake_256f extends SLHDSAKeyPairGeneratorSpi {
        public Shake_256f() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256f);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/slhdsa/SLHDSAKeyPairGeneratorSpi$Shake_256s.class */
    public static class Shake_256s extends SLHDSAKeyPairGeneratorSpi {
        public Shake_256s() {
            super(SLHDSAParameterSpec.slh_dsa_shake_256s);
        }
    }

    public SLHDSAKeyPairGeneratorSpi(String str) {
        super(str);
        this.engine = new SLHDSAKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
    }

    protected SLHDSAKeyPairGeneratorSpi(SLHDSAParameterSpec sLHDSAParameterSpec) {
        super("SLH-DSA-" + Strings.toUpperCase(sLHDSAParameterSpec.getName()));
        this.engine = new SLHDSAKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
        this.param = new SLHDSAKeyGenerationParameters(this.random, (SLHDSAParameters) parameters.get(sLHDSAParameterSpec.getName()));
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String nameFromParams = getNameFromParams(algorithmParameterSpec);
        if (nameFromParams == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        SLHDSAParameters sLHDSAParameters = (SLHDSAParameters) parameters.get(nameFromParams);
        if (sLHDSAParameters == null) {
            throw new InvalidAlgorithmParameterException("unknown parameter set name: " + nameFromParams);
        }
        this.param = new SLHDSAKeyGenerationParameters(secureRandom, sLHDSAParameters);
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            if (getAlgorithm().startsWith("HASH")) {
                this.param = new SLHDSAKeyGenerationParameters(this.random, SLHDSAParameters.sha2_128f_with_sha256);
            } else {
                this.param = new SLHDSAKeyGenerationParameters(this.random, SLHDSAParameters.sha2_128f);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCSLHDSAPublicKey((SLHDSAPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCSLHDSAPrivateKey((SLHDSAPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        return algorithmParameterSpec instanceof SLHDSAParameterSpec ? ((SLHDSAParameterSpec) algorithmParameterSpec).getName() : Strings.toUpperCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    static {
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128f.getName(), SLHDSAParameters.sha2_128f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128s.getName(), SLHDSAParameters.sha2_128s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192f.getName(), SLHDSAParameters.sha2_192f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192s.getName(), SLHDSAParameters.sha2_192s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256f.getName(), SLHDSAParameters.sha2_256f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256s.getName(), SLHDSAParameters.sha2_256s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128f.getName(), SLHDSAParameters.shake_128f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128s.getName(), SLHDSAParameters.shake_128s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192f.getName(), SLHDSAParameters.shake_192f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192s.getName(), SLHDSAParameters.shake_192s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256f.getName(), SLHDSAParameters.shake_256f);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256s.getName(), SLHDSAParameters.shake_256s);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256.getName(), SLHDSAParameters.sha2_128f_with_sha256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256.getName(), SLHDSAParameters.sha2_128s_with_sha256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512.getName(), SLHDSAParameters.sha2_192f_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512.getName(), SLHDSAParameters.sha2_192s_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512.getName(), SLHDSAParameters.sha2_256f_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512.getName(), SLHDSAParameters.sha2_256s_with_sha512);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128.getName(), SLHDSAParameters.shake_128f_with_shake128);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128.getName(), SLHDSAParameters.shake_128s_with_shake128);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256.getName(), SLHDSAParameters.shake_192f_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256.getName(), SLHDSAParameters.shake_192s_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256.getName(), SLHDSAParameters.shake_256f_with_shake256);
        parameters.put(SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256.getName(), SLHDSAParameters.shake_256s_with_shake256);
    }
}
