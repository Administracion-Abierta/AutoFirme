package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/XTEA.class */
public final class XTEA {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/XTEA$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "XTEA IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/XTEA$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new XTEAEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/XTEA$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("XTEA", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/XTEA$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = XTEA.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.XTEA", PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.XTEA", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.XTEA", PREFIX + "$AlgParams");
        }
    }

    private XTEA() {
    }
}
