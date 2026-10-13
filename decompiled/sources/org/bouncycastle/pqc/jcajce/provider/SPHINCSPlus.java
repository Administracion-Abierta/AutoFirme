package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/SPHINCSPlus.class */
public class SPHINCSPlus {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.sphincsplus.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/SPHINCSPlus$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.SPHINCS+", "SPHINCSPLUS");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.SPHINCS+", "SPHINCSPLUS");
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-128S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_128s", BCObjectIdentifiers.sphincsPlus_sha2_128s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-128F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_128f", BCObjectIdentifiers.sphincsPlus_sha2_128f);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-192S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_192s", BCObjectIdentifiers.sphincsPlus_sha2_192s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-192F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_192f", BCObjectIdentifiers.sphincsPlus_sha2_192f);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-256S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_256s", BCObjectIdentifiers.sphincsPlus_sha2_256s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-256F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_256f", BCObjectIdentifiers.sphincsPlus_sha2_256f);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-128S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_128s", BCObjectIdentifiers.sphincsPlus_shake_128s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-128F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_128f", BCObjectIdentifiers.sphincsPlus_shake_128f);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-192S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_192s", BCObjectIdentifiers.sphincsPlus_shake_192s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-192F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_192f", BCObjectIdentifiers.sphincsPlus_shake_192f);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-256S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_256s", BCObjectIdentifiers.sphincsPlus_shake_256s);
            addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-256F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_256f", BCObjectIdentifiers.sphincsPlus_shake_256f);
            addSignatureAlgorithm(configurableProvider, "SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SignatureSpi$Direct", BCObjectIdentifiers.sphincsPlus);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256s_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256f_r3);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple);
            addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SPHINCS+", "SPHINCSPLUS");
            SPHINCSPlusKeyFactorySpi sPHINCSPlusKeyFactorySpi = new SPHINCSPlusKeyFactorySpi();
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
        }
    }
}
