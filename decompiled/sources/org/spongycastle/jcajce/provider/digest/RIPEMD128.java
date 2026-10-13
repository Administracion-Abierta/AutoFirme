package org.spongycastle.jcajce.provider.digest;

import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.digests.RIPEMD128Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/RIPEMD128.class */
public class RIPEMD128 {
    private RIPEMD128() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/RIPEMD128$Digest.class */
    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new RIPEMD128Digest());
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Digest d = (Digest) super.clone();
            d.digest = new RIPEMD128Digest((RIPEMD128Digest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/RIPEMD128$HashMac.class */
    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new RIPEMD128Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/RIPEMD128$KeyGenerator.class */
    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACRIPEMD128", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/RIPEMD128$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = RIPEMD128.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.RIPEMD128", PREFIX + "$Digest");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
            addHMACAlgorithm(provider, "RIPEMD128", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
        }
    }
}
