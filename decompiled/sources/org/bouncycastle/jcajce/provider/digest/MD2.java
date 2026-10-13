package org.bouncycastle.jcajce.provider.digest;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/MD2.class */
public class MD2 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/MD2$Digest.class */
    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new MD2Digest());
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new MD2Digest((MD2Digest) this.digest);
            return digest;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/MD2$HashMac.class */
    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new MD2Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/MD2$KeyGenerator.class */
    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACMD2", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/MD2$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = MD2.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.MD2", PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md2, MessageDigestAlgorithms.MD2);
            addHMACAlgorithm(configurableProvider, MessageDigestAlgorithms.MD2, PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
        }
    }

    private MD2() {
    }
}
