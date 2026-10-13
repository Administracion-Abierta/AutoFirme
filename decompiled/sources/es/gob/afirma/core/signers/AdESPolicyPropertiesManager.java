package es.gob.afirma.core.signers;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/AdESPolicyPropertiesManager.class */
public final class AdESPolicyPropertiesManager {
    static final String POLICY_ID_AGE = "FirmaAGE";
    static final String POLICY_ID_AGE_1_8 = "FirmaAGE18";
    static final String PROPERTY_POLICY_IDENTIFIER = "policyIdentifier";
    static final String PROPERTY_POLICY_HASH_ALGORITHM = "policyIdentifierHashAlgorithm";
    static final String PROPERTY_POLICY_HASH = "policyIdentifierHash";
    static final String PROPERTY_POLICY_QUALIFIER = "policyQualifier";
    static final String PROPERTY_POLICY_DESCRIPTION = "policyDescription";
    static final String FORMAT_CADES = "CAdES";
    static final String FORMAT_XADES = "XAdES";
    static final String FORMAT_PADES = "PAdES";
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String BUNDLE_NAME = "policy";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private AdESPolicyPropertiesManager() {
    }

    static void setProperties(Properties properties, String str, String str2) {
        if (!isSupportedConfiguration(str, str2)) {
            return;
        }
        String property = getProperty(str, "policyIdentifier", str2);
        if (property != null) {
            setProperty(properties, "policyIdentifier", property);
        }
        String property2 = getProperty(str, "policyIdentifierHashAlgorithm", str2);
        if (property2 != null) {
            setProperty(properties, "policyIdentifierHashAlgorithm", property2);
        }
        String property3 = getProperty(str, "policyIdentifierHash", str2);
        if (property3 != null) {
            setProperty(properties, "policyIdentifierHash", property3);
        }
        String property4 = getProperty(str, "policyQualifier", str2);
        if (property4 != null) {
            setProperty(properties, "policyQualifier", property4);
        }
        String property5 = getProperty(str, "policyDescription", str2);
        if (property5 != null) {
            setProperty(properties, "policyDescription", property5);
        }
    }

    private static boolean isSupportedConfiguration(String str, String str2) {
        String property;
        String property2 = getProperty(str, "policyIdentifierHash", str2);
        if ((property2 == null || property2.trim().isEmpty()) && (property = getProperty(str, "policyIdentifier", str2)) != null) {
            if (!property.toLowerCase().startsWith(HTTP_PREFIX) || !property.toLowerCase().startsWith(HTTPS_PREFIX)) {
                return false;
            }
            return true;
        }
        return true;
    }

    private static String getProperty(String str, String str2, String str3) {
        if (str2 == null) {
            throw new IllegalArgumentException("No se ha indicado la propiedad de la politica de firma");
        }
        if (str != null) {
            if (str3 != null) {
                String str4 = str + "." + str2 + "." + str3;
                if (RESOURCE_BUNDLE.containsKey(str4)) {
                    return RESOURCE_BUNDLE.getString(str4);
                }
            }
            String str5 = str + "." + str2;
            if (RESOURCE_BUNDLE.containsKey(str5)) {
                return RESOURCE_BUNDLE.getString(str5);
            }
        }
        if (str3 != null) {
            String str6 = str2 + "." + str3;
            if (RESOURCE_BUNDLE.containsKey(str6)) {
                return RESOURCE_BUNDLE.getString(str6);
            }
        }
        if (RESOURCE_BUNDLE.containsKey(str2)) {
            return RESOURCE_BUNDLE.getString(str2);
        }
        return null;
    }

    private static void setProperty(Properties properties, String str, String str2) {
        if (properties.containsKey(str)) {
            LOGGER.warning("La siguiente propiedad se ignora en favor del valor derivado de la politica establecida: " + str);
        }
        properties.setProperty(str, str2);
    }

    public static boolean isAgePolicyConfigurated(String str) {
        if (str == null) {
            return false;
        }
        Enumeration<String> keys = RESOURCE_BUNDLE.getKeys();
        while (keys.hasMoreElements()) {
            String strNextElement = keys.nextElement();
            if (strNextElement.endsWith(".policyIdentifier") && RESOURCE_BUNDLE.getString(strNextElement).equals(str)) {
                return true;
            }
        }
        return false;
    }
}
