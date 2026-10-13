package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grain128.class */
public final class Grain128 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grain128$Base.class */
    public static class Base extends BaseStreamCipher {
        public Base() {
            super(new Grain128Engine(), 12);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grain128$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("Grain128", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Grain128$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = Grain128.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.Grain128", PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.Grain128", PREFIX + "$KeyGen");
        }
    }

    private Grain128() {
    }
}
