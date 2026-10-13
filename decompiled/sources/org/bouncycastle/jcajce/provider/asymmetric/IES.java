package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/IES.class */
public class IES {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ies.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/IES$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.IES", "org.bouncycastle.jcajce.provider.asymmetric.ies.AlgorithmParametersSpi");
            configurableProvider.addAlgorithm("AlgorithmParameters.ECIES", "org.bouncycastle.jcajce.provider.asymmetric.ies.AlgorithmParametersSpi");
        }
    }
}
