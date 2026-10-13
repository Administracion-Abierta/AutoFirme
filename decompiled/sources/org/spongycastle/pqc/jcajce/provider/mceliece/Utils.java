package org.spongycastle.pqc.jcajce.provider.mceliece;

import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/Utils.class */
class Utils {
    Utils() {
    }

    static AlgorithmIdentifier getDigAlgId(String digestName) {
        if (digestName.equals("SHA-1")) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        }
        if (digestName.equals("SHA-224")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE);
        }
        if (digestName.equals("SHA-256")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        }
        if (digestName.equals("SHA-384")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE);
        }
        if (digestName.equals("SHA-512")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE);
        }
        throw new IllegalArgumentException("unrecognised digest algorithm: " + digestName);
    }

    static Digest getDigest(AlgorithmIdentifier digest) {
        if (digest.getAlgorithm().equals(OIWObjectIdentifiers.idSHA1)) {
            return DigestFactory.createSHA1();
        }
        if (digest.getAlgorithm().equals(NISTObjectIdentifiers.id_sha224)) {
            return DigestFactory.createSHA224();
        }
        if (digest.getAlgorithm().equals(NISTObjectIdentifiers.id_sha256)) {
            return DigestFactory.createSHA256();
        }
        if (digest.getAlgorithm().equals(NISTObjectIdentifiers.id_sha384)) {
            return DigestFactory.createSHA384();
        }
        if (digest.getAlgorithm().equals(NISTObjectIdentifiers.id_sha512)) {
            return DigestFactory.createSHA512();
        }
        throw new IllegalArgumentException("unrecognised OID in digest algorithm identifier: " + digest.getAlgorithm());
    }
}
