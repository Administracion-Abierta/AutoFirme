package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/Falcon.class */
public class Falcon {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.falcon.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/Falcon$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.FALCON", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi");
            addKeyFactoryAlgorithm(configurableProvider, "FALCON-512", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi$Falcon512", BCObjectIdentifiers.falcon_512, new FalconKeyFactorySpi.Falcon512());
            addKeyFactoryAlgorithm(configurableProvider, "FALCON-1024", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi$Falcon1024", BCObjectIdentifiers.falcon_1024, new FalconKeyFactorySpi.Falcon1024());
            configurableProvider.addAlgorithm("KeyPairGenerator.FALCON", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi");
            addKeyPairGeneratorAlgorithm(configurableProvider, "FALCON-512", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi$Falcon512", BCObjectIdentifiers.falcon_512);
            addKeyPairGeneratorAlgorithm(configurableProvider, "FALCON-1024", "org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi$Falcon1024", BCObjectIdentifiers.falcon_1024);
            addSignatureAlgorithm(configurableProvider, "FALCON", "org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Base", BCObjectIdentifiers.falcon);
            addSignatureAlgorithm(configurableProvider, "FALCON-512", "org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Falcon512", BCObjectIdentifiers.falcon_512);
            addSignatureAlgorithm(configurableProvider, "FALCON-1024", "org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Falcon1024", BCObjectIdentifiers.falcon_1024);
        }
    }
}
