package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Poly1305.class */
public class Poly1305 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Poly1305$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("Poly1305", 256, new Poly1305KeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Poly1305$Mac.class */
    public static class Mac extends BaseMac {
        public Mac() {
            super(new org.bouncycastle.crypto.macs.Poly1305());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Poly1305$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = Poly1305.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Mac.POLY1305", PREFIX + "$Mac");
            configurableProvider.addAlgorithm("KeyGenerator.POLY1305", PREFIX + "$KeyGen");
        }
    }

    private Poly1305() {
    }
}
