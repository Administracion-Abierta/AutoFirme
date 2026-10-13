package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Rainbow.class */
public class Rainbow {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.rainbow.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Rainbow$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.RAINBOW", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.RAINBOW", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi");
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-III-CLASSIC", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowIIIclassic", BCObjectIdentifiers.rainbow_III_classic);
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-III-CIRCUMZENITHAL", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowIIIcircum", BCObjectIdentifiers.rainbow_III_circumzenithal);
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-III-COMPRESSED", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowIIIcomp", BCObjectIdentifiers.rainbow_III_compressed);
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-V-CLASSIC", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowVclassic", BCObjectIdentifiers.rainbow_V_classic);
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-V-CIRCUMZENITHAL", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowVcircum", BCObjectIdentifiers.rainbow_V_circumzenithal);
            addKeyPairGeneratorAlgorithm(configurableProvider, "RAINBOW-V-COMPRESSED", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi$RainbowVcomp", BCObjectIdentifiers.rainbow_V_compressed);
            addSignatureAlgorithm(configurableProvider, "RAINBOW", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$Base", BCObjectIdentifiers.rainbow);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-III-CLASSIC", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowIIIclassic", BCObjectIdentifiers.rainbow_III_classic);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-III-CIRCUMZENITHAL", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowIIIcircum", BCObjectIdentifiers.rainbow_III_circumzenithal);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-III-COMPRESSED", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowIIIcomp", BCObjectIdentifiers.rainbow_III_compressed);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-V-CLASSIC", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowVclassic", BCObjectIdentifiers.rainbow_V_classic);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-V-CIRCUMZENITHAL", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowVcircum", BCObjectIdentifiers.rainbow_V_circumzenithal);
            addSignatureAlgorithm(configurableProvider, "RAINBOW-v-COMPRESSED", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$RainbowVcomp", BCObjectIdentifiers.rainbow_V_compressed);
            RainbowKeyFactorySpi rainbowKeyFactorySpi = new RainbowKeyFactorySpi();
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_III_classic, "RAINBOW", rainbowKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_III_circumzenithal, "RAINBOW", rainbowKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_III_compressed, "RAINBOW", rainbowKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_V_classic, "RAINBOW", rainbowKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_V_circumzenithal, "RAINBOW", rainbowKeyFactorySpi);
            registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.rainbow_V_compressed, "RAINBOW", rainbowKeyFactorySpi);
        }
    }
}
