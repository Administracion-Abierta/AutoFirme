package org.spongycastle.jcajce.provider.digest;

import es.gob.afirma.core.signers.AOSignConstants;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.macs.OldHMac;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384.class */
public class SHA384 {
    private SHA384() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384$Digest.class */
    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA384Digest());
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            Digest d = (Digest) super.clone();
            d.digest = new SHA384Digest((SHA384Digest) this.digest);
            return d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384$HashMac.class */
    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA384Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384$KeyGenerator.class */
    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA384", 384, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384$OldSHA384.class */
    public static class OldSHA384 extends BaseMac {
        public OldSHA384() {
            super(new OldHMac(new SHA384Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/digest/SHA384$Mappings.class */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA384.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.SHA-384", PREFIX + "$Digest");
            provider.addAlgorithm("Alg.Alias.MessageDigest.SHA384", "SHA-384");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha384, "SHA-384");
            provider.addAlgorithm("Mac.OLDHMACSHA384", PREFIX + "$OldSHA384");
            provider.addAlgorithm("Mac.PBEWITHHMACSHA384", PREFIX + "$HashMac");
            addHMACAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
            addHMACAlias(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, PKCSObjectIdentifiers.id_hmacWithSHA384);
        }
    }
}
