package org.spongycastle.jcajce.util;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.AlgorithmParameters;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/util/JcaJceUtils.class */
public class JcaJceUtils {
    private JcaJceUtils() {
    }

    public static ASN1Encodable extractParameters(AlgorithmParameters params) throws IOException {
        ASN1Encodable asn1Params;
        try {
            asn1Params = ASN1Primitive.fromByteArray(params.getEncoded("ASN.1"));
        } catch (Exception e) {
            asn1Params = ASN1Primitive.fromByteArray(params.getEncoded());
        }
        return asn1Params;
    }

    public static void loadParameters(AlgorithmParameters params, ASN1Encodable sParams) throws IOException {
        try {
            params.init(sParams.toASN1Primitive().getEncoded(), "ASN.1");
        } catch (Exception e) {
            params.init(sParams.toASN1Primitive().getEncoded());
        }
    }

    public static String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        if (PKCSObjectIdentifiers.md5.equals(digestAlgOID)) {
            return MessageDigestAlgorithms.MD5;
        }
        if (OIWObjectIdentifiers.idSHA1.equals(digestAlgOID)) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA1;
        }
        if (NISTObjectIdentifiers.id_sha224.equals(digestAlgOID)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals(digestAlgOID)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals(digestAlgOID)) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA384;
        }
        if (NISTObjectIdentifiers.id_sha512.equals(digestAlgOID)) {
            return "SHA512";
        }
        if (TeleTrusTObjectIdentifiers.ripemd128.equals(digestAlgOID)) {
            return "RIPEMD128";
        }
        if (TeleTrusTObjectIdentifiers.ripemd160.equals(digestAlgOID)) {
            return "RIPEMD160";
        }
        if (TeleTrusTObjectIdentifiers.ripemd256.equals(digestAlgOID)) {
            return "RIPEMD256";
        }
        if (CryptoProObjectIdentifiers.gostR3411.equals(digestAlgOID)) {
            return "GOST3411";
        }
        return digestAlgOID.getId();
    }
}
