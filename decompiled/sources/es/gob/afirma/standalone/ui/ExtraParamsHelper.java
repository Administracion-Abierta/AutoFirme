package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ExtraParamsHelper.class */
final class ExtraParamsHelper {
    private ExtraParamsHelper() {
    }

    static Properties loadExtraParamsForSigner(AOSigner aOSigner) {
        Properties propertiesLoadCAdESExtraParams;
        if (aOSigner instanceof AOFacturaESigner) {
            propertiesLoadCAdESExtraParams = loadFacturaEExtraParams();
        } else if (aOSigner instanceof AOXAdESSigner) {
            propertiesLoadCAdESExtraParams = loadXAdESExtraParams();
        } else if (aOSigner instanceof AOPDFSigner) {
            propertiesLoadCAdESExtraParams = loadPAdESExtraParams();
        } else {
            propertiesLoadCAdESExtraParams = loadCAdESExtraParams();
        }
        return propertiesLoadCAdESExtraParams;
    }

    private static Properties loadFacturaEExtraParams() {
        Properties properties = new Properties();
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER);
        if (str != null && !str.trim().isEmpty()) {
            properties.put("policyIdentifier", str);
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH);
            if (str2 != null && !str2.trim().isEmpty()) {
                properties.put("policyIdentifierHash", str2);
            }
            String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH_ALGORITHM);
            if (str3 != null && !str3.trim().isEmpty()) {
                properties.put("policyIdentifierHashAlgorithm", str3);
            }
        }
        String str4 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY);
        if (str4 != null && !str4.trim().isEmpty()) {
            properties.put("signatureProductionCity", str4);
        }
        String str5 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE);
        if (str5 != null && !str5.trim().isEmpty()) {
            properties.put(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE, str5);
        }
        String str6 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE);
        if (str6 != null && !str6.trim().isEmpty()) {
            properties.put("signatureProductionPostalCode", str6);
        }
        String str7 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY);
        if (str7 != null && !str7.trim().isEmpty()) {
            properties.put("signatureProductionCountry", str7);
        }
        String str8 = PreferencesManager.get(PreferencesManager.PREFERENCE_FACTURAE_SIGNER_ROLE);
        if (str8 != null && !str8.trim().isEmpty()) {
            properties.put("signerClaimedRoles", str8);
        }
        return properties;
    }

    private static Properties loadXAdESExtraParams() {
        Properties properties = new Properties();
        properties.put(XAdESExtraParams.IGNORE_STYLE_SHEETS, "false");
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_IDENTIFIER);
        if (str != null && !str.trim().isEmpty()) {
            properties.put("policyIdentifier", str);
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH);
            if (str2 != null && !str2.trim().isEmpty()) {
                properties.put("policyIdentifierHash", str2);
            }
            String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_HASH_ALGORITHM);
            if (str3 != null && !str3.trim().isEmpty()) {
                properties.put("policyIdentifierHashAlgorithm", str3);
            }
            String str4 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_POLICY_QUALIFIER);
            if (str4 != null && !str4.trim().isEmpty()) {
                properties.put("policyQualifier", str4);
            }
        }
        String str5 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNER_CLAIMED_ROLE);
        if (str5 != null && !str5.trim().isEmpty()) {
            properties.put("signerClaimedRoles", str5);
        }
        String str6 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY);
        if (str6 != null && !str6.trim().isEmpty()) {
            properties.put("signatureProductionCity", str6);
        }
        String str7 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE);
        if (str7 != null && !str7.trim().isEmpty()) {
            properties.put(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE, str7);
        }
        String str8 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE);
        if (str8 != null && !str8.trim().isEmpty()) {
            properties.put("signatureProductionPostalCode", str8);
        }
        String str9 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY);
        if (str9 != null && !str9.trim().isEmpty()) {
            properties.put("signatureProductionCountry", str9);
        }
        String str10 = PreferencesManager.get(PreferencesManager.PREFERENCE_XADES_SIGN_FORMAT);
        if (str10 != null && !str10.trim().isEmpty()) {
            properties.put(XAdESExtraParams.FORMAT, str10);
        }
        return properties;
    }

    private static Properties loadPAdESExtraParams() {
        Properties properties = new Properties();
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_IDENTIFIER);
        if (str != null && !str.trim().isEmpty()) {
            properties.put("policyIdentifier", str);
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH);
            if (str2 != null && !str2.trim().isEmpty()) {
                properties.put("policyIdentifierHash", str2);
            }
            String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_HASH_ALGORITHM);
            if (str3 != null && !str3.trim().isEmpty()) {
                properties.put("policyIdentifierHashAlgorithm", str3);
            }
            String str4 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_POLICY_QUALIFIER);
            if (str4 != null && !str4.trim().isEmpty()) {
                properties.put("policyQualifier", str4);
            }
        }
        String str5 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_REASON);
        if (str5 != null && !str5.trim().isEmpty()) {
            properties.put(PdfExtraParams.SIGN_REASON, str5);
        }
        String str6 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGN_PRODUCTION_CITY);
        if (str6 != null && !str6.trim().isEmpty()) {
            properties.put("signatureProductionCity", str6);
        }
        String str7 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_SIGNER_CONTACT);
        if (str7 != null && !str7.trim().isEmpty()) {
            properties.put(PdfExtraParams.SIGNER_CONTACT, str7);
        }
        String str8 = PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_FORMAT);
        if (str8 != null && !str8.trim().isEmpty()) {
            properties.put(PdfExtraParams.SIGNATURE_SUBFILTER, str8);
        }
        properties.put(PdfExtraParams.OBFUSCATE_CERT_DATA, Boolean.toString(Boolean.parseBoolean(PreferencesManager.get(PreferencesManager.PREFERENCE_PADES_OBFUSCATE_CERT_INFO))));
        return properties;
    }

    private static Properties loadCAdESExtraParams() {
        Properties properties = new Properties();
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_IDENTIFIER);
        if (str != null && !str.trim().isEmpty()) {
            properties.put("policyIdentifier", str);
            String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH);
            if (str2 != null && !str2.trim().isEmpty()) {
                properties.put("policyIdentifierHash", str2);
            }
            String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_HASH_ALGORITHM);
            if (str3 != null && !str3.trim().isEmpty()) {
                properties.put(CAdESExtraParams.POLICY_IDENTIFIER_HASH_ALGORITHM, str3);
            }
            String str4 = PreferencesManager.get(PreferencesManager.PREFERENCE_CADES_POLICY_QUALIFIER);
            if (str4 != null && !str4.trim().isEmpty()) {
                properties.put("policyQualifier", str4);
            }
        }
        properties.put(CAdESExtraParams.MODE, PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_CADES_IMPLICIT) ? AOSignConstants.SIGN_MODE_IMPLICIT : "explicit");
        return properties;
    }

    public static void addParamToCertifiedPdf(Properties properties) {
        properties.setProperty(PdfExtraParams.ALLOW_SIGNING_CERTIFIED_PDFS, Boolean.TRUE.toString());
    }

    public static void addParamToUnregisteredPdf(Properties properties) {
        properties.setProperty(PdfExtraParams.ALLOW_COSIGNING_UNREGISTERED_SIGNATURES, Boolean.TRUE.toString());
    }
}
