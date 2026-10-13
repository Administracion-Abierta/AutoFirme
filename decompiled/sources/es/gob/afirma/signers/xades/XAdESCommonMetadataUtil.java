package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifierImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceV2;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceV2Impl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2Impl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithExplicitPolicy;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.ietf.jgss.Oid;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/XAdESCommonMetadataUtil.class */
final class XAdESCommonMetadataUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private XAdESCommonMetadataUtil() {
    }

    static void addCommonMetadata(XAdESBase xAdESBase, Properties properties) throws AOException {
        if (xAdESBase instanceof XadesWithExplicitPolicy) {
            try {
                SignaturePolicyIdentifier policy = getPolicy(properties);
                if (policy != null) {
                    ((XadesWithExplicitPolicy) xAdESBase).setSignaturePolicyIdentifier(policy);
                }
            } catch (NoSuchAlgorithmException e) {
                throw new AOException("El algoritmo indicado para la politica (" + properties.getProperty("policyIdentifierHashAlgorithm") + ") no esta soportado: " + e, e);
            }
        }
        if (xAdESBase instanceof XadesWithBasicAttributes) {
            SignatureProductionPlace signatureProductionPlace = getSignatureProductionPlace(properties);
            if (signatureProductionPlace != null) {
                ((XadesWithBasicAttributes) xAdESBase).setSignatureProductionPlace(signatureProductionPlace);
            }
            SignerRole signerRole = parseSignerRole(properties);
            if (signerRole != null) {
                ((XadesWithBasicAttributes) xAdESBase).setSignerRole(signerRole);
            }
        }
        if (xAdESBase instanceof XadesWithBaselineAttributes) {
            SignatureProductionPlaceV2 signatureProductionPlaceV2 = getSignatureProductionPlaceV2(properties);
            if (signatureProductionPlaceV2 != null) {
                ((XadesWithBaselineAttributes) xAdESBase).setSignatureProductionPlaceV2(signatureProductionPlaceV2);
            }
            SignerRoleV2 signerRoleV2 = parseSignerRoleV2(properties);
            if (signerRoleV2 != null) {
                ((XadesWithBaselineAttributes) xAdESBase).setSignerRoleV2(signerRoleV2);
            }
        }
    }

    private static SignerRole parseSignerRole(Properties properties) {
        if (properties == null) {
            return null;
        }
        SignerRoleImpl signerRoleImpl = null;
        String[] claimedRoles = parseClaimedRoles(properties);
        if (claimedRoles != null) {
            signerRoleImpl = new SignerRoleImpl();
            for (String str : claimedRoles) {
                signerRoleImpl.addClaimedRole(str);
            }
        }
        return signerRoleImpl;
    }

    private static SignerRoleV2 parseSignerRoleV2(Properties properties) {
        if (properties == null) {
            return null;
        }
        SignerRoleV2Impl signerRoleV2Impl = null;
        String[] claimedRoles = parseClaimedRoles(properties);
        if (claimedRoles != null) {
            signerRoleV2Impl = new SignerRoleV2Impl();
            for (String str : claimedRoles) {
                signerRoleV2Impl.addClaimedRole(str);
            }
        }
        return signerRoleV2Impl;
    }

    private static String[] parseClaimedRoles(Properties properties) {
        String[] strArr = null;
        String property = properties.getProperty("signerClaimedRoles");
        if (property != null) {
            String[] strArrSplit = property.split(Pattern.quote("|"));
            ArrayList arrayList = new ArrayList();
            for (String str : strArrSplit) {
                if (!str.trim().isEmpty()) {
                    arrayList.add(str);
                }
            }
            strArr = (String[]) arrayList.toArray(new String[0]);
        }
        return strArr;
    }

    private static SignatureProductionPlace getSignatureProductionPlace(Properties properties) {
        if (properties == null) {
            return null;
        }
        return getSignatureProductionPlace(properties.getProperty("signatureProductionCity"), properties.getProperty(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE), properties.getProperty("signatureProductionPostalCode"), properties.getProperty("signatureProductionCountry"));
    }

    private static SignatureProductionPlace getSignatureProductionPlace(String str, String str2, String str3, String str4) {
        if (str == null && str2 == null && str3 == null && str4 == null) {
            return null;
        }
        return new SignatureProductionPlaceImpl(str, str2, str3, str4);
    }

    private static SignatureProductionPlaceV2 getSignatureProductionPlaceV2(Properties properties) {
        if (properties == null) {
            return null;
        }
        return getSignatureProductionPlaceV2(properties.getProperty("signatureProductionCity"), properties.getProperty(XAdESExtraParams.SIGNATURE_PRODUCCTION_STREET_ADDRESS), properties.getProperty(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE), properties.getProperty("signatureProductionPostalCode"), properties.getProperty("signatureProductionCountry"));
    }

    private static SignatureProductionPlaceV2 getSignatureProductionPlaceV2(String str, String str2, String str3, String str4, String str5) {
        if (str == null && str2 == null && str3 == null && str4 == null && str5 == null) {
            return null;
        }
        return new SignatureProductionPlaceV2Impl(str, str2, str3, str4, str5);
    }

    private static SignaturePolicyIdentifier getPolicy(Properties properties) throws NoSuchAlgorithmException {
        if (properties == null) {
            return null;
        }
        return getPolicy(properties.getProperty("policyIdentifier"), properties.getProperty("policyIdentifierHash"), properties.getProperty("policyIdentifierHashAlgorithm"), properties.getProperty(XAdESExtraParams.POLICY_DESCRIPTION), properties.getProperty("policyQualifier"));
    }

    private static SignaturePolicyIdentifier getPolicy(String str, String str2, String str3, String str4, String str5) throws NoSuchAlgorithmException {
        String str6;
        if (str == null) {
            return null;
        }
        try {
            new Oid(str);
            if (str.toLowerCase().startsWith("urn:oid:")) {
                str6 = str;
            } else {
                LOGGER.warning("Se proporciono directamente un OID como identificador de politica (" + str + "), se tranformara en URN con el prefijo 'urn:oid:'");
                str6 = "urn:oid:" + str;
            }
        } catch (Exception e) {
            str6 = str;
        }
        String digestMethodByCommonName = str3 != null ? XAdESUtil.getDigestMethodByCommonName(str3) : null;
        SignaturePolicyIdentifierImpl signaturePolicyIdentifierImpl = new SignaturePolicyIdentifierImpl(false);
        try {
            signaturePolicyIdentifierImpl.setIdentifier(str6, digestMethodByCommonName != null ? str2 : null, digestMethodByCommonName);
            signaturePolicyIdentifierImpl.setDescription(str4 != null ? str4 : "");
            if (str5 != null) {
                signaturePolicyIdentifierImpl.setQualifier(str5);
            }
            return signaturePolicyIdentifierImpl;
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido acceder al identificador ('" + str6 + "') de la politica de firma, no se anadira este campo: " + e2);
            return null;
        }
    }
}
