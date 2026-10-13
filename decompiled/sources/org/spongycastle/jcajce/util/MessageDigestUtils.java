package org.spongycastle.jcajce.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.gm.GMObjectIdentifiers;
import org.spongycastle.asn1.gnu.GNUObjectIdentifiers;
import org.spongycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/util/MessageDigestUtils.class */
public class MessageDigestUtils {
    private static Map<ASN1ObjectIdentifier, String> digestOidMap = new HashMap();

    static {
        digestOidMap.put(PKCSObjectIdentifiers.md2, MessageDigestAlgorithms.MD2);
        digestOidMap.put(PKCSObjectIdentifiers.md4, "MD4");
        digestOidMap.put(PKCSObjectIdentifiers.md5, MessageDigestAlgorithms.MD5);
        digestOidMap.put(OIWObjectIdentifiers.idSHA1, "SHA-1");
        digestOidMap.put(NISTObjectIdentifiers.id_sha224, "SHA-224");
        digestOidMap.put(NISTObjectIdentifiers.id_sha256, "SHA-256");
        digestOidMap.put(NISTObjectIdentifiers.id_sha384, "SHA-384");
        digestOidMap.put(NISTObjectIdentifiers.id_sha512, "SHA-512");
        digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD-128");
        digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD-160");
        digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD-128");
        digestOidMap.put(ISOIECObjectIdentifiers.ripemd128, "RIPEMD-128");
        digestOidMap.put(ISOIECObjectIdentifiers.ripemd160, "RIPEMD-160");
        digestOidMap.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        digestOidMap.put(GNUObjectIdentifiers.Tiger_192, "Tiger");
        digestOidMap.put(ISOIECObjectIdentifiers.whirlpool, "Whirlpool");
        digestOidMap.put(NISTObjectIdentifiers.id_sha3_224, MessageDigestAlgorithms.SHA3_224);
        digestOidMap.put(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
        digestOidMap.put(NISTObjectIdentifiers.id_sha3_384, MessageDigestAlgorithms.SHA3_384);
        digestOidMap.put(NISTObjectIdentifiers.id_sha3_512, MessageDigestAlgorithms.SHA3_512);
        digestOidMap.put(GMObjectIdentifiers.sm3, "SM3");
    }

    public static String getDigestName(ASN1ObjectIdentifier digestAlgOID) {
        String name = digestOidMap.get(digestAlgOID);
        if (name != null) {
            return name;
        }
        return digestAlgOID.getId();
    }
}
