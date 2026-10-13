package org.spongycastle.jcajce.provider.digest;

import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.digests.KeccakDigest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak.class */
public class Keccak {
    private Keccak() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$DigestKeccak.class */
    public static class DigestKeccak extends BCMessageDigest implements Cloneable {
        public DigestKeccak(int size) {
            super(new KeccakDigest(size));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest d = (BCMessageDigest) super.clone();
            d.digest = new KeccakDigest((KeccakDigest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Digest224.class */
    public static class Digest224 extends DigestKeccak {
        public Digest224() {
            super(224);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Digest256.class */
    public static class Digest256 extends DigestKeccak {
        public Digest256() {
            super(256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Digest288.class */
    public static class Digest288 extends DigestKeccak {
        public Digest288() {
            super(288);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Digest384.class */
    public static class Digest384 extends DigestKeccak {
        public Digest384() {
            super(384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Digest512.class */
    public static class Digest512 extends DigestKeccak {
        public Digest512() {
            super(512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$HashMac224.class */
    public static class HashMac224 extends BaseMac {
        public HashMac224() {
            super(new HMac(new KeccakDigest(224)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$HashMac256.class */
    public static class HashMac256 extends BaseMac {
        public HashMac256() {
            super(new HMac(new KeccakDigest(256)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$HashMac288.class */
    public static class HashMac288 extends BaseMac {
        public HashMac288() {
            super(new HMac(new KeccakDigest(288)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$HashMac384.class */
    public static class HashMac384 extends BaseMac {
        public HashMac384() {
            super(new HMac(new KeccakDigest(384)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$HashMac512.class */
    public static class HashMac512 extends BaseMac {
        public HashMac512() {
            super(new HMac(new KeccakDigest(512)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$KeyGenerator224.class */
    public static class KeyGenerator224 extends BaseKeyGenerator {
        public KeyGenerator224() {
            super("HMACKECCAK224", 224, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$KeyGenerator256.class */
    public static class KeyGenerator256 extends BaseKeyGenerator {
        public KeyGenerator256() {
            super("HMACKECCAK256", 256, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$KeyGenerator288.class */
    public static class KeyGenerator288 extends BaseKeyGenerator {
        public KeyGenerator288() {
            super("HMACKECCAK288", 288, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$KeyGenerator384.class */
    public static class KeyGenerator384 extends BaseKeyGenerator {
        public KeyGenerator384() {
            super("HMACKECCAK384", 384, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$KeyGenerator512.class */
    public static class KeyGenerator512 extends BaseKeyGenerator {
        public KeyGenerator512() {
            super("HMACKECCAK512", 512, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Keccak$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = Keccak.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.KECCAK-224", PREFIX + "$Digest224");
            provider.addAlgorithm("MessageDigest.KECCAK-288", PREFIX + "$Digest288");
            provider.addAlgorithm("MessageDigest.KECCAK-256", PREFIX + "$Digest256");
            provider.addAlgorithm("MessageDigest.KECCAK-384", PREFIX + "$Digest384");
            provider.addAlgorithm("MessageDigest.KECCAK-512", PREFIX + "$Digest512");
            addHMACAlgorithm(provider, "KECCAK224", PREFIX + "$HashMac224", PREFIX + "$KeyGenerator224");
            addHMACAlgorithm(provider, "KECCAK256", PREFIX + "$HashMac256", PREFIX + "$KeyGenerator256");
            addHMACAlgorithm(provider, "KECCAK288", PREFIX + "$HashMac288", PREFIX + "$KeyGenerator288");
            addHMACAlgorithm(provider, "KECCAK384", PREFIX + "$HashMac384", PREFIX + "$KeyGenerator384");
            addHMACAlgorithm(provider, "KECCAK512", PREFIX + "$HashMac512", PREFIX + "$KeyGenerator512");
        }
    }
}
