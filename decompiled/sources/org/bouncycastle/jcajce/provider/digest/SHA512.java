package org.bouncycastle.jcajce.provider.digest;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512.class */
public class SHA512 {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$Digest.class */
    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA512Digest());
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SHA512Digest((SHA512Digest) this.digest);
            return digest;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$DigestT.class */
    public static class DigestT extends BCMessageDigest implements Cloneable {
        public DigestT(int i) {
            super(new SHA512tDigest(i));
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            DigestT digestT = (DigestT) super.clone();
            digestT.digest = new SHA512tDigest((SHA512tDigest) this.digest);
            return digestT;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$DigestT224.class */
    public static class DigestT224 extends DigestT {
        public DigestT224() {
            super(224);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$DigestT256.class */
    public static class DigestT256 extends DigestT {
        public DigestT256() {
            super(256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$HashMac.class */
    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA512Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$HashMacT224.class */
    public static class HashMacT224 extends BaseMac {
        public HashMacT224() {
            super(new HMac(new SHA512tDigest(224)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$HashMacT256.class */
    public static class HashMacT256 extends BaseMac {
        public HashMacT256() {
            super(new HMac(new SHA512tDigest(256)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyFactory.class */
    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("HmacSHA512", null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyFactory224.class */
    public static class KeyFactory224 extends BaseSecretKeyFactory {
        public KeyFactory224() {
            super("HmacSHA512/224", null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyFactory256.class */
    public static class KeyFactory256 extends BaseSecretKeyFactory {
        public KeyFactory256() {
            super("HmacSHA512/256", null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyGenerator.class */
    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA512", 512, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyGeneratorT224.class */
    public static class KeyGeneratorT224 extends BaseKeyGenerator {
        public KeyGeneratorT224() {
            super("HMACSHA512/224", 224, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$KeyGeneratorT256.class */
    public static class KeyGeneratorT256 extends BaseKeyGenerator {
        public KeyGeneratorT256() {
            super("HMACSHA512/256", 256, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA512.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.SHA-512", PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512", "SHA-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512, "SHA-512");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/224", PREFIX + "$DigestT224");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512/224", MessageDigestAlgorithms.SHA_512_224);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512224", MessageDigestAlgorithms.SHA_512_224);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA-512(224)", MessageDigestAlgorithms.SHA_512_224);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512(224)", MessageDigestAlgorithms.SHA_512_224);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_224, MessageDigestAlgorithms.SHA_512_224);
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/256", PREFIX + "$DigestT256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512/256", "SHA-512/256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512256", "SHA-512/256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA-512(256)", "SHA-512/256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512(256)", "SHA-512/256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_256, "SHA-512/256");
            configurableProvider.addAlgorithm("Mac.OLDHMACSHA512", PREFIX + "$OldSHA512");
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA512", PREFIX + "$HashMac");
            addHMACAlgorithm(configurableProvider, "SHA512", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
            addHMACAlias(configurableProvider, "SHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
            addHMACAlgorithm(configurableProvider, "SHA512/224", PREFIX + "$HashMacT224", PREFIX + "$KeyGeneratorT224");
            addHMACAlgorithm(configurableProvider, "SHA512/256", PREFIX + "$HashMacT256", PREFIX + "$KeyGeneratorT256");
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA512", PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512, "HMACSHA512");
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA512/224", PREFIX + "$KeyFactory224");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.HMACSHA512(224)", "HMACSHA512/224");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512_224, "HMACSHA512/224");
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA512/256", PREFIX + "$KeyFactory256");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.HMACSHA512(256)", "HMACSHA512/256");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_hmacWithSHA512_256, "HMACSHA512/256");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/digest/SHA512$OldSHA512.class */
    public static class OldSHA512 extends BaseMac {
        public OldSHA512() {
            super(new OldHMac(new SHA512Digest()));
        }
    }

    private SHA512() {
    }
}
