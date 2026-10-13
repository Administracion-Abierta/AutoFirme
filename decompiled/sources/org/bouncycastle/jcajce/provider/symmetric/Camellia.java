package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.CamelliaWrapEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia.class */
public final class Camellia {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$AlgParamGen.class */
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for Camellia parameter generation.");
        }

        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters algorithmParametersCreateParametersInstance = createParametersInstance("Camellia");
                algorithmParametersCreateParametersInstance.init(new IvParameterSpec(bArr));
                return algorithmParametersCreateParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "Camellia IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$CBC.class */
    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new CamelliaEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$CBC128.class */
    public static class CBC128 extends BaseBlockCipher {
        public CBC128() {
            super(128, new CBCBlockCipher(new CamelliaEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$CBC192.class */
    public static class CBC192 extends BaseBlockCipher {
        public CBC192() {
            super(192, new CBCBlockCipher(new CamelliaEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$CBC256.class */
    public static class CBC256 extends BaseBlockCipher {
        public CBC256() {
            super(256, new CBCBlockCipher(new CamelliaEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new BlockCipherProvider() { // from class: org.bouncycastle.jcajce.provider.symmetric.Camellia.ECB.1
                @Override // org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider
                public BlockCipher get() {
                    return new CamelliaEngine();
                }
            });
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$GMAC.class */
    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new CamelliaEngine())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$KeyFactory.class */
    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("Camellia", null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("Camellia", i, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$KeyGen128.class */
    public static class KeyGen128 extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$KeyGen192.class */
    public static class KeyGen192 extends KeyGen {
        public KeyGen192() {
            super(192);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$KeyGen256.class */
    public static class KeyGen256 extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Mappings.class */
    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = Camellia.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.CAMELLIA", PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.CAMELLIA", PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Cipher.CAMELLIA", PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia128_cbc, PREFIX + "$CBC128");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia192_cbc, PREFIX + "$CBC192");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia256_cbc, PREFIX + "$CBC256");
            configurableProvider.addAlgorithm("Cipher.CAMELLIARFC3211WRAP", PREFIX + "$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.CAMELLIAWRAP", PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia128_wrap, PREFIX + "$Wrap128");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia192_wrap, PREFIX + "$Wrap192");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia256_wrap, PREFIX + "$Wrap256");
            configurableProvider.addAlgorithm("SecretKeyFactory.CAMELLIA", PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("KeyGenerator.CAMELLIA", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia128_wrap, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia192_wrap, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia256_wrap, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia128_cbc, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia192_cbc, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia256_cbc, PREFIX + "$KeyGen256");
            addGMacAlgorithm(configurableProvider, "CAMELLIA", PREFIX + "$GMAC", PREFIX + "$KeyGen");
            addPoly1305Algorithm(configurableProvider, "CAMELLIA", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Poly1305.class */
    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new CamelliaEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Poly1305KeyGen.class */
    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-Camellia", 256, new Poly1305KeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$RFC3211Wrap.class */
    public static class RFC3211Wrap extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new CamelliaEngine()), 16);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Wrap.class */
    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new CamelliaWrapEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Wrap128.class */
    public static class Wrap128 extends BaseWrapCipher {
        public Wrap128() {
            super(128, new CamelliaWrapEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Wrap192.class */
    public static class Wrap192 extends BaseWrapCipher {
        public Wrap192() {
            super(192, new CamelliaWrapEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Camellia$Wrap256.class */
    public static class Wrap256 extends BaseWrapCipher {
        public Wrap256() {
            super(256, new CamelliaWrapEngine());
        }
    }

    private Camellia() {
    }
}
