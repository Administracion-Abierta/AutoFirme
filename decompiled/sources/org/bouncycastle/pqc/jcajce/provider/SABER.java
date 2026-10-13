package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/SABER.class */
public class SABER {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.saber.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/SABER$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyGeneratorSpi");
            SABERKeyFactorySpi sABERKeyFactorySpi = new SABERKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_saber, "SABER");
            registerOid(configurableProvider, BCObjectIdentifiers.pqc_kem_saber, "SABER", sABERKeyFactorySpi);
            registerOidAlgorithmParameters(configurableProvider, BCObjectIdentifiers.pqc_kem_saber, "SABER");
        }
    }
}
