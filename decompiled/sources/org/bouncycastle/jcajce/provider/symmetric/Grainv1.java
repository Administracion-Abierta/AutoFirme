package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.Grainv1Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grainv1.class */
public final class Grainv1 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grainv1$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "Grainv1 IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grainv1$Base.class */
    public static class Base extends BaseStreamCipher {
        public Base() {
            super(new Grainv1Engine(), 8);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grainv1$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("Grainv1", 80, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grainv1$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = Grainv1.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.Grainv1", PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.Grainv1", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.Grainv1", PREFIX + "$AlgParams");
        }
    }

    private Grainv1() {
    }
}
