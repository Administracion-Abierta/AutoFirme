package org.bouncycastle.pqc.crypto;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/DigestUtils.class */
public class DigestUtils {
    static final Map digestOids = new HashMap();

    public static ASN1ObjectIdentifier getDigestOid(String str) {
        if (digestOids.containsKey(str)) {
            return (ASN1ObjectIdentifier) digestOids.get(str);
        }
        throw new IllegalArgumentException("unrecognised digest algorithm: " + str);
    }

    static {
        digestOids.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        digestOids.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        digestOids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        digestOids.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        digestOids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        digestOids.put(MessageDigestAlgorithms.SHA_512_224, NISTObjectIdentifiers.id_sha512_224);
        digestOids.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
        digestOids.put(MessageDigestAlgorithms.SHA3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(MessageDigestAlgorithms.SHA3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(MessageDigestAlgorithms.SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put("SHAKE128", NISTObjectIdentifiers.id_shake128);
        digestOids.put("SHAKE256", NISTObjectIdentifiers.id_shake256);
    }
}
