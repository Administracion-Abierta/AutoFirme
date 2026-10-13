package org.bouncycastle.jcajce.provider.util;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/util/DigestFactory.class */
public class DigestFactory {
    private static Set md5 = new HashSet();
    private static Set sha1 = new HashSet();
    private static Set sha224 = new HashSet();
    private static Set sha256 = new HashSet();
    private static Set sha384 = new HashSet();
    private static Set sha512 = new HashSet();
    private static Set sha512_224 = new HashSet();
    private static Set sha512_256 = new HashSet();
    private static Set sha3_224 = new HashSet();
    private static Set sha3_256 = new HashSet();
    private static Set sha3_384 = new HashSet();
    private static Set sha3_512 = new HashSet();
    private static Set shake128 = new HashSet();
    private static Set shake256 = new HashSet();
    private static Map oids = new HashMap();

    public static Digest getDigest(String str) {
        String upperCase = Strings.toUpperCase(str);
        if (sha1.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA1();
        }
        if (md5.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createMD5();
        }
        if (sha224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA224();
        }
        if (sha256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA256();
        }
        if (sha384.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA384();
        }
        if (sha512.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512();
        }
        if (sha512_224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512_224();
        }
        if (sha512_256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA512_256();
        }
        if (sha3_224.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_224();
        }
        if (sha3_256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_256();
        }
        if (sha3_384.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_384();
        }
        if (sha3_512.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHA3_512();
        }
        if (shake128.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHAKE128();
        }
        if (shake256.contains(upperCase)) {
            return org.bouncycastle.crypto.util.DigestFactory.createSHAKE256();
        }
        return null;
    }

    public static boolean isSameDigest(String str, String str2) {
        return (sha1.contains(str) && sha1.contains(str2)) || (sha224.contains(str) && sha224.contains(str2)) || ((sha256.contains(str) && sha256.contains(str2)) || ((sha384.contains(str) && sha384.contains(str2)) || ((sha512.contains(str) && sha512.contains(str2)) || ((sha512_224.contains(str) && sha512_224.contains(str2)) || ((sha512_256.contains(str) && sha512_256.contains(str2)) || ((sha3_224.contains(str) && sha3_224.contains(str2)) || ((sha3_256.contains(str) && sha3_256.contains(str2)) || ((sha3_384.contains(str) && sha3_384.contains(str2)) || ((sha3_512.contains(str) && sha3_512.contains(str2)) || (md5.contains(str) && md5.contains(str2)))))))))));
    }

    public static ASN1ObjectIdentifier getOID(String str) {
        return (ASN1ObjectIdentifier) oids.get(str);
    }

    static {
        md5.add(MessageDigestAlgorithms.MD5);
        md5.add(PKCSObjectIdentifiers.md5.getId());
        sha1.add(AOSignConstants.DIGEST_ALGORITHM_SHA1);
        sha1.add("SHA-1");
        sha1.add(OIWObjectIdentifiers.idSHA1.getId());
        sha224.add("SHA224");
        sha224.add("SHA-224");
        sha224.add(NISTObjectIdentifiers.id_sha224.getId());
        sha256.add("SHA256");
        sha256.add("SHA-256");
        sha256.add(NISTObjectIdentifiers.id_sha256.getId());
        sha384.add(AOSignConstants.DIGEST_ALGORITHM_SHA384);
        sha384.add("SHA-384");
        sha384.add(NISTObjectIdentifiers.id_sha384.getId());
        sha512.add("SHA512");
        sha512.add("SHA-512");
        sha512.add(NISTObjectIdentifiers.id_sha512.getId());
        sha512_224.add("SHA512(224)");
        sha512_224.add("SHA-512(224)");
        sha512_224.add(NISTObjectIdentifiers.id_sha512_224.getId());
        sha512_256.add("SHA512(256)");
        sha512_256.add("SHA-512(256)");
        sha512_256.add(NISTObjectIdentifiers.id_sha512_256.getId());
        sha3_224.add(MessageDigestAlgorithms.SHA3_224);
        sha3_224.add(NISTObjectIdentifiers.id_sha3_224.getId());
        sha3_256.add("SHA3-256");
        sha3_256.add(NISTObjectIdentifiers.id_sha3_256.getId());
        sha3_384.add(MessageDigestAlgorithms.SHA3_384);
        sha3_384.add(NISTObjectIdentifiers.id_sha3_384.getId());
        sha3_512.add(MessageDigestAlgorithms.SHA3_512);
        sha3_512.add(NISTObjectIdentifiers.id_sha3_512.getId());
        shake128.add("SHAKE128");
        shake128.add(NISTObjectIdentifiers.id_shake128.getId());
        shake256.add("SHAKE256");
        shake256.add(NISTObjectIdentifiers.id_shake256.getId());
        oids.put(MessageDigestAlgorithms.MD5, PKCSObjectIdentifiers.md5);
        oids.put(PKCSObjectIdentifiers.md5.getId(), PKCSObjectIdentifiers.md5);
        oids.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, OIWObjectIdentifiers.idSHA1);
        oids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        oids.put(OIWObjectIdentifiers.idSHA1.getId(), OIWObjectIdentifiers.idSHA1);
        oids.put("SHA224", NISTObjectIdentifiers.id_sha224);
        oids.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        oids.put(NISTObjectIdentifiers.id_sha224.getId(), NISTObjectIdentifiers.id_sha224);
        oids.put("SHA256", NISTObjectIdentifiers.id_sha256);
        oids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        oids.put(NISTObjectIdentifiers.id_sha256.getId(), NISTObjectIdentifiers.id_sha256);
        oids.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, NISTObjectIdentifiers.id_sha384);
        oids.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        oids.put(NISTObjectIdentifiers.id_sha384.getId(), NISTObjectIdentifiers.id_sha384);
        oids.put("SHA512", NISTObjectIdentifiers.id_sha512);
        oids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        oids.put(NISTObjectIdentifiers.id_sha512.getId(), NISTObjectIdentifiers.id_sha512);
        oids.put("SHA512(224)", NISTObjectIdentifiers.id_sha512_224);
        oids.put("SHA-512(224)", NISTObjectIdentifiers.id_sha512_224);
        oids.put(NISTObjectIdentifiers.id_sha512_224.getId(), NISTObjectIdentifiers.id_sha512_224);
        oids.put("SHA512(256)", NISTObjectIdentifiers.id_sha512_256);
        oids.put("SHA-512(256)", NISTObjectIdentifiers.id_sha512_256);
        oids.put(NISTObjectIdentifiers.id_sha512_256.getId(), NISTObjectIdentifiers.id_sha512_256);
        oids.put(MessageDigestAlgorithms.SHA3_224, NISTObjectIdentifiers.id_sha3_224);
        oids.put(NISTObjectIdentifiers.id_sha3_224.getId(), NISTObjectIdentifiers.id_sha3_224);
        oids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        oids.put(NISTObjectIdentifiers.id_sha3_256.getId(), NISTObjectIdentifiers.id_sha3_256);
        oids.put(MessageDigestAlgorithms.SHA3_384, NISTObjectIdentifiers.id_sha3_384);
        oids.put(NISTObjectIdentifiers.id_sha3_384.getId(), NISTObjectIdentifiers.id_sha3_384);
        oids.put(MessageDigestAlgorithms.SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        oids.put(NISTObjectIdentifiers.id_sha3_512.getId(), NISTObjectIdentifiers.id_sha3_512);
        oids.put("SHAKE128", NISTObjectIdentifiers.id_shake128);
        oids.put(NISTObjectIdentifiers.id_shake128.getId(), NISTObjectIdentifiers.id_shake128);
        oids.put("SHAKE256", NISTObjectIdentifiers.id_shake256);
        oids.put(NISTObjectIdentifiers.id_shake256.getId(), NISTObjectIdentifiers.id_shake256);
    }
}
