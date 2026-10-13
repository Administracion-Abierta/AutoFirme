package org.spongycastle.jcajce.provider.digest;

import org.spongycastle.asn1.misc.MiscObjectIdentifiers;
import org.spongycastle.crypto.digests.Blake2bDigest;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b.class */
public class Blake2b {
    private Blake2b() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b$Blake2b512.class */
    public static class Blake2b512 extends BCMessageDigest implements Cloneable {
        public Blake2b512() {
            super(new Blake2bDigest(512));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Blake2b512 d = (Blake2b512) super.clone();
            d.digest = new Blake2bDigest((Blake2bDigest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b$Blake2b384.class */
    public static class Blake2b384 extends BCMessageDigest implements Cloneable {
        public Blake2b384() {
            super(new Blake2bDigest(384));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Blake2b384 d = (Blake2b384) super.clone();
            d.digest = new Blake2bDigest((Blake2bDigest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b$Blake2b256.class */
    public static class Blake2b256 extends BCMessageDigest implements Cloneable {
        public Blake2b256() {
            super(new Blake2bDigest(256));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Blake2b256 d = (Blake2b256) super.clone();
            d.digest = new Blake2bDigest((Blake2bDigest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b$Blake2b160.class */
    public static class Blake2b160 extends BCMessageDigest implements Cloneable {
        public Blake2b160() {
            super(new Blake2bDigest(160));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Blake2b160 d = (Blake2b160) super.clone();
            d.digest = new Blake2bDigest((Blake2bDigest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/Blake2b$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = Blake2b.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.BLAKE2B-512", PREFIX + "$Blake2b512");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b512, "BLAKE2B-512");
            provider.addAlgorithm("MessageDigest.BLAKE2B-384", PREFIX + "$Blake2b384");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b384, "BLAKE2B-384");
            provider.addAlgorithm("MessageDigest.BLAKE2B-256", PREFIX + "$Blake2b256");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b256, "BLAKE2B-256");
            provider.addAlgorithm("MessageDigest.BLAKE2B-160", PREFIX + "$Blake2b160");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b160, "BLAKE2B-160");
        }
    }
}
