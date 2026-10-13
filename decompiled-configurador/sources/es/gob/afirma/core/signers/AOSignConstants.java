package es.gob.afirma.core.signers;

import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import java.util.Locale;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOSignConstants.class */
public final class AOSignConstants {
    public static final String SIGN_FORMAT_AUTO = "auto";
    public static final String SIGN_FORMAT_CMS = "CMS/PKCS#7";
    public static final String SIGN_FORMAT_CADES_ASIC_S = "CAdES-ASiC-S";
    public static final String SIGN_FORMAT_CADES_ASIC_S_TRI = "CAdES-ASiC-S-tri";
    public static final String SIGN_FORMAT_CADES = "CAdES";
    public static final String SIGN_FORMAT_CADES_TRI = "CAdEStri";
    public static final String SIGN_FORMAT_PKCS1 = "NONE";
    public static final String SIGN_FORMAT_PKCS1_ALT1 = "PKCS1";
    public static final String SIGN_FORMAT_PKCS1_ALT2 = "PKCS#1";
    public static final String SIGN_FORMAT_PKCS1_TRI = "NONEtri";
    public static final String SIGN_FORMAT_XADES_ASIC_S = "XAdES-ASiC-S";
    public static final String SIGN_FORMAT_XADES_ASIC_S_TRI = "XAdES-ASiC-S-tri";
    public static final String SIGN_FORMAT_XADES_DETACHED = "XAdES Detached";
    public static final String SIGN_FORMAT_XADES_EXTERNALLY_DETACHED = "XAdES Externally Detached";
    public static final String SIGN_FORMAT_XADES_ENVELOPED = "XAdES Enveloped";
    public static final String SIGN_FORMAT_XADES_ENVELOPING = "XAdES Enveloping";
    public static final String SIGN_FORMAT_XADES = "XAdES";
    public static final String SIGN_FORMAT_XADES_TRI = "XAdEStri";
    public static final String SIGN_FORMAT_XMLDSIG_DETACHED = "XMLDSig Detached";
    public static final String SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED = "XMLDSig Externally Detached";
    public static final String SIGN_FORMAT_XMLDSIG_ENVELOPED = "XMLDSig Enveloped";
    public static final String SIGN_FORMAT_XMLDSIG_ENVELOPING = "XMLDSig Enveloping";
    public static final String SIGN_FORMAT_XMLDSIG = "XMLDSig";
    public static final String SIGN_FORMAT_OOXML = "OOXML (Office Open XML)";
    public static final String SIGN_FORMAT_OOXML_ALT1 = "OOXML";
    public static final String SIGN_FORMAT_ODF = "ODF (Open Document Format)";
    public static final String SIGN_FORMAT_ODF_ALT1 = "ODF";
    public static final String SIGN_FORMAT_PDF = "Adobe PDF";
    public static final String SIGN_FORMAT_PDF_TRI = "Adobe PDF TriPhase";
    public static final String SIGN_FORMAT_PADES = "PAdES";
    public static final String SIGN_FORMAT_PADES_TRI = "PAdEStri";
    public static final String SIGN_FORMAT_SOAP = "SOAP";
    public static final String SIGN_FORMAT_FACTURAE = "FacturaE";
    public static final String SIGN_FORMAT_FACTURAE_TRI = "FacturaEtri";
    public static final String SIGN_FORMAT_FACTURAE_ALT1 = "Factura-e";
    public static final String DEFAULT_SIGN_FORMAT = "CAdES";
    public static final String MASSIVE_OPERATION_SIGN = "FIRMAR";
    public static final String MASSIVE_OPERATION_COSIGN = "COFIRMAR";
    public static final String MASSIVE_OPERATION_COUNTERSIGN_TREE = "CONTRAFIRMAR_ARBOL";
    public static final String MASSIVE_OPERATION_COUNTERSIGN_LEAFS = "CONTRAFIRMAR_HOJAS";
    public static final String DEFAULT_MASSIVE_OPERATION = "FIRMAR";
    public static final String SIGN_PROFILE_ADVANCED = "advanced";
    public static final String SIGN_PROFILE_BASELINE = "baseline";
    public static final String DEFAULT_SIGN_PROFILE = "advanced";
    public static final String CMS_CONTENTTYPE_DATA = "Data";
    public static final String CMS_CONTENTTYPE_SIGNEDDATA = "SignedData";
    public static final String CMS_CONTENTTYPE_DIGESTEDDATA = "DigestedData";
    public static final String CMS_CONTENTTYPE_COMPRESSEDDATA = "CompressedData";
    public static final String CMS_CONTENTTYPE_ENCRYPTEDDATA = "EncryptedData";
    public static final String CMS_CONTENTTYPE_ENVELOPEDDATA = "EnvelopedData";
    public static final String CMS_CONTENTTYPE_SIGNEDANDENVELOPEDDATA = "SignedAndEnvelopedData";
    public static final String CMS_CONTENTTYPE_AUTHENTICATEDDATA = "AuthenticatedData";
    public static final String CMS_CONTENTTYPE_AUTHENVELOPEDDATA = "AuthEnvelopedData";
    public static final String DEFAULT_CMS_CONTENTTYPE = "EnvelopedData";
    public static final String DEFAULT_OID_TO_SIGN = "1.3.6.1.4.1.1466.115.121.1.40";
    public static final String PADES_SUBFILTER_BASIC = "adbe.pkcs7.detached";
    public static final String PADES_SUBFILTER_BES = "ETSI.CAdES.detached";
    public static final String DIGEST_ALGORITHM_SHA1 = "SHA1";
    public static final String DIGEST_ALGORITHM_SHA256 = "SHA256";
    public static final String DIGEST_ALGORITHM_SHA384 = "SHA384";
    public static final String DIGEST_ALGORITHM_SHA512 = "SHA512";
    public static final String SIGN_ALGORITHM_SHA1WITHRSA = "SHA1withRSA";
    public static final String SIGN_ALGORITHM_SHA256WITHRSA = "SHA256withRSA";
    public static final String SIGN_ALGORITHM_SHA384WITHRSA = "SHA384withRSA";
    public static final String SIGN_ALGORITHM_SHA512WITHRSA = "SHA512withRSA";
    public static final String SIGN_ALGORITHM_NONEWITHRSA = "NONEwithRSA";
    public static final String SIGN_ALGORITHM_SHA1WITHDSA = "SHA1withDSA";
    public static final String SIGN_ALGORITHM_SHA1WITHECDSA = "SHA1withECDSA";
    public static final String SIGN_ALGORITHM_SHA224WITHECDSA = "SHA224withECDSA";
    public static final String SIGN_ALGORITHM_SHA256WITHECDSA = "SHA256withECDSA";
    public static final String SIGN_ALGORITHM_SHA384WITHECDSA = "SHA384withECDSA";
    public static final String SIGN_ALGORITHM_SHA512WITHECDSA = "SHA512withECDSA";
    public static final String SIGN_ALGORITHM_NONEWITHECDSA = "NONEwithECDSA";
    public static final String DEFAULT_SIGN_ALGO = "SHA512withRSA";
    public static final String SIGN_MODE_EXPLICIT = "explicit";
    public static final String SIGN_MODE_IMPLICIT = "implicit";
    public static final String DEFAULT_SIGN_MODE = "explicit";

    private AOSignConstants() {
    }

    public static String getDigestAlgorithmName(String str) {
        if (str == null) {
            throw new IllegalArgumentException("El nombre del algoritmo de huella digital no puede ser nulo");
        }
        String upperCase = str.toUpperCase(Locale.US);
        if ("SHA".equals(upperCase) || upperCase.equals("http://www.w3.org/2000/09/xmldsig#sha1".toUpperCase(Locale.US)) || "1.3.14.3.2.26".equals(upperCase) || upperCase.startsWith(DIGEST_ALGORITHM_SHA1) || upperCase.startsWith("SHA-1")) {
            return DIGEST_ALGORITHM_SHA1;
        }
        if (upperCase.equals("http://www.w3.org/2001/04/xmlenc#sha256".toUpperCase(Locale.US)) || AOAlgorithmID.OID_SHA256.equals(upperCase) || upperCase.startsWith("SHA256") || upperCase.startsWith("SHA-256")) {
            return "SHA-256";
        }
        if (upperCase.startsWith(DIGEST_ALGORITHM_SHA384) || "2.16.840.1.101.3.4.2.2".equals(upperCase) || upperCase.startsWith("SHA-384")) {
            return "SHA-384";
        }
        if (upperCase.equals("http://www.w3.org/2001/04/xmlenc#sha512".toUpperCase(Locale.US)) || "2.16.840.1.101.3.4.2.3".equals(upperCase) || upperCase.startsWith("SHA512") || upperCase.startsWith("SHA-512")) {
            return "SHA-512";
        }
        if (upperCase.equals("http://www.w3.org/2001/04/xmlenc#ripemd160".toUpperCase(Locale.US)) || upperCase.startsWith("RIPEMD160") || upperCase.startsWith("RIPEMD-160")) {
            return "RIPEMD160";
        }
        if (str.contains("with")) {
            return getDigestAlgorithmName(str.substring(0, str.indexOf("with")));
        }
        if (str.startsWith("http://www.w3.org/2001/04/xmldsig-more#") || str.startsWith("http://www.w3.org/2000/09/xmldsig#") || str.startsWith("http://www.w3.org/2009/xmldsig11#")) {
            return getDigestAlgorithmName(str.substring(str.lastIndexOf(45) + 1));
        }
        Logger.getLogger("es.gob.afirma").warning("Algoritmo de huella desconocido, no se normalizara su nombre: " + str);
        return str;
    }

    public static String composeSignatureAlgorithmName(String str, String str2) {
        String str3;
        if (str == null) {
            throw new IllegalArgumentException("El nombre del algoritmo no puede ser nulo");
        }
        if (str2 == null) {
            throw new IllegalArgumentException("El tipo de clave de certificado no puede ser nulo");
        }
        String strReplace = getDigestAlgorithmName(str).replace("-", "");
        if ("RSA".equals(str2)) {
            str3 = "withRSA";
        } else if ("DSA".equals(str2)) {
            str3 = "withDSA";
        } else if (str2.startsWith("EC")) {
            str3 = "withECDSA";
        } else {
            throw new IllegalArgumentException("Tipo de clave de firma no soportado: " + str2);
        }
        return strReplace + str3;
    }

    public static boolean isSHA2SignatureAlgorithm(String str) {
        return SIGN_ALGORITHM_SHA256WITHRSA.equals(str) || SIGN_ALGORITHM_SHA384WITHRSA.equals(str) || "SHA512withRSA".equals(str) || SIGN_ALGORITHM_SHA256WITHECDSA.equals(str) || SIGN_ALGORITHM_SHA384WITHECDSA.equals(str) || SIGN_ALGORITHM_SHA512WITHECDSA.equals(str);
    }

    public static boolean isSHA1SignatureAlgorithm(String str) {
        return SIGN_ALGORITHM_SHA1WITHRSA.equals(str) || SIGN_ALGORITHM_SHA1WITHDSA.equals(str) || SIGN_ALGORITHM_SHA1WITHECDSA.equals(str);
    }

    public static boolean isDSAorECDSASignatureAlgorithm(String str) {
        return str != null && (str.endsWith("withECDSA") || str.endsWith("withECDSAinP1363Format") || str.endsWith("withDSA") || str.endsWith("withDSAinP1363Format"));
    }
}
