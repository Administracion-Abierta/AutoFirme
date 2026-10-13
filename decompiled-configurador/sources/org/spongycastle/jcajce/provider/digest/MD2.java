package org.spongycastle.jcajce.provider.digest;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.digests.MD2Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/MD2.class */
public class MD2 {
    private MD2() {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/MD2$Digest.class */
    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new MD2Digest());
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Digest d = (Digest) super.clone();
            d.digest = new MD2Digest((MD2Digest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/MD2$HashMac.class */
    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new MD2Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/MD2$KeyGenerator.class */
    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACMD2", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/digest/MD2$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = MD2.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.MD2", PREFIX + "$Digest");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md2, MessageDigestAlgorithms.MD2);
            addHMACAlgorithm(provider, MessageDigestAlgorithms.MD2, PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
        }
    }
}
