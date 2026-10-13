package org.spongycastle.pqc.jcajce.provider.xmss;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Xof;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.digests.SHAKEDigest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/xmss/DigestUtil.class */
class DigestUtil {
    DigestUtil() {
    }

    static Digest getDigest(ASN1ObjectIdentifier oid) {
        if (oid.equals(NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_sha512)) {
            return new SHA512Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_shake128)) {
            return new SHAKEDigest(128);
        }
        if (oid.equals(NISTObjectIdentifiers.id_shake256)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + oid);
    }

    public static byte[] getDigestResult(Digest digest) {
        byte[] hash = new byte[getDigestSize(digest)];
        if (digest instanceof Xof) {
            ((Xof) digest).doFinal(hash, 0, hash.length);
        } else {
            digest.doFinal(hash, 0);
        }
        return hash;
    }

    public static int getDigestSize(Digest digest) {
        if (digest instanceof Xof) {
            return digest.getDigestSize() * 2;
        }
        return digest.getDigestSize();
    }

    public static String getXMSSDigestName(ASN1ObjectIdentifier treeDigest) {
        if (treeDigest.equals(NISTObjectIdentifiers.id_sha256)) {
            return "SHA256";
        }
        if (treeDigest.equals(NISTObjectIdentifiers.id_sha512)) {
            return "SHA512";
        }
        if (treeDigest.equals(NISTObjectIdentifiers.id_shake128)) {
            return "SHAKE128";
        }
        if (treeDigest.equals(NISTObjectIdentifiers.id_shake256)) {
            return "SHAKE256";
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + treeDigest);
    }
}
