package org.spongycastle.jcajce.provider.symmetric;

import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.engines.XTEAEngine;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.spongycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/XTEA.class */
public final class XTEA {
    private XTEA() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/XTEA$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new XTEAEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/XTEA$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("XTEA", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/XTEA$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.spongycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "XTEA IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/XTEA$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = XTEA.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("Cipher.XTEA", PREFIX + "$ECB");
            provider.addAlgorithm("KeyGenerator.XTEA", PREFIX + "$KeyGen");
            provider.addAlgorithm("AlgorithmParameters.XTEA", PREFIX + "$AlgParams");
        }
    }
}
