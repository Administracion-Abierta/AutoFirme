package org.spongycastle.jcajce.provider.symmetric;

import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.engines.HC128Engine;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.spongycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/HC128.class */
public final class HC128 {
    private HC128() {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/HC128$Base.class */
    public static class Base extends BaseStreamCipher {
        public Base() {
            super(new HC128Engine(), 16);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/HC128$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("HC128", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/HC128$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = HC128.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("Cipher.HC128", PREFIX + "$Base");
            provider.addAlgorithm("KeyGenerator.HC128", PREFIX + "$KeyGen");
        }
    }
}
