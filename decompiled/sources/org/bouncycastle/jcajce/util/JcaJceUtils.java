package org.bouncycastle.jcajce.util;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.AlgorithmParameters;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/util/JcaJceUtils.class */
public class JcaJceUtils {
    private JcaJceUtils() {
    }

    public static ASN1Encodable extractParameters(AlgorithmParameters algorithmParameters) throws IOException {
        ASN1Primitive aSN1PrimitiveFromByteArray;
        try {
            aSN1PrimitiveFromByteArray = ASN1Primitive.fromByteArray(algorithmParameters.getEncoded("ASN.1"));
        } catch (Exception e) {
            aSN1PrimitiveFromByteArray = ASN1Primitive.fromByteArray(algorithmParameters.getEncoded());
        }
        return aSN1PrimitiveFromByteArray;
    }

    public static void loadParameters(AlgorithmParameters algorithmParameters, ASN1Encodable aSN1Encodable) throws IOException {
        try {
            algorithmParameters.init(aSN1Encodable.toASN1Primitive().getEncoded(), "ASN.1");
        } catch (Exception e) {
            algorithmParameters.init(aSN1Encodable.toASN1Primitive().getEncoded());
        }
    }

    public static String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.md5.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return MessageDigestAlgorithms.MD5;
        }
        if (OIWObjectIdentifiers.idSHA1.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA1;
        }
        if (NISTObjectIdentifiers.id_sha224.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA384;
        }
        if (NISTObjectIdentifiers.id_sha512.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "SHA512";
        }
        if (TeleTrusTObjectIdentifiers.ripemd128.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "RIPEMD128";
        }
        if (TeleTrusTObjectIdentifiers.ripemd160.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "RIPEMD160";
        }
        if (TeleTrusTObjectIdentifiers.ripemd256.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return "RIPEMD256";
        }
        return CryptoProObjectIdentifiers.gostR3411.equals((ASN1Primitive) aSN1ObjectIdentifier) ? "GOST3411" : aSN1ObjectIdentifier.getId();
    }
}
