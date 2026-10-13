package es.gob.afirma.standalone.configurator.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/PreferencesManager.class */
public final class PreferencesManager {
    private static final String UPDATED_SYSTEM_PREFERENCE_NODE = "/es/gob/afirma/standalone/ui/systempreferences";
    private static final String INTERNAL_SYSTEM_PREFERENCE_NODE = "/es/gob/afirma/standalone/ui/internalpreferences";
    private static Preferences SYSTEM_PREFERENCES;
    private static final Properties DEFAULT_PREFERENCES;
    private static Preferences REAL_SYSTEM_PREFERENCES;
    private static Preferences INTERNAL_PREFERENCES;
    private static Properties INTERNAL_PREFERENCES_DATA;
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    private static final String PREFIX_HTTPS = "https://";
    public static final String VALUE_MULTISIGN_COSIGN = "cosign";
    public static final String VALUE_MULTISIGN_COUNTERSIGN_LEAFS = "countersignLeafs";
    public static final String VALUE_MULTISIGN_COUNTERSIGN_TREE = "countersignTree";
    public static final String PREFERENCES_LOCALE = "default.locale";
    public static final String PREFERENCE_GENERAL_BLOCKED = "preferencesBlocked";
    public static final String PREFERENCE_GENERAL_CHECK_JAVA_VERSION = "checkJavaVersion";
    public static final String PREFERENCE_GENERAL_OMIT_ASKONCLOSE = "omitAskOnClose";
    public static final String PREFERENCE_GENERAL_UPDATECHECK = "checkForUpdates";
    public static final String PREFERENCE_GENERAL_ENABLED_JMULTICARD = "enabledJmulticard";
    public static final String PREFERENCE_GENERAL_VDI_OPTIMIZATION = "vdiOptimization";
    public static final String PREFERENCE_GENERAL_SIGNATURE_ALGORITHM = "signatureHashAlgorithm";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF = "defaultSignatureFormatPdf";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML = "defaultSignatureFormatOoxml";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE = "defaultSignatureFormatFacturae";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF = "defaultSignatureFormatOdf";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_XML = "defaultSignatureFormatXml";
    public static final String PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN = "defaultSignatureFormatBin";
    public static final String PREFERENCE_GENERAL_CONFIRMTOSIGN = "confirmToSign";
    public static final String PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES = "allowInvalidSignatures";
    public static final String PREFERENCE_GENERAL_MASSIVE_OVERWRITE = "massiveOverride";
    public static final String PREFERENCE_GENERAL_SECURE_CONNECTIONS = "secureConnections";
    public static final String PREFERENCE_GENERAL_SECURE_DOMAINS_LIST = "secureDomainsList";
    public static final String PREFERENCE_GENERAL_PROXY_SELECTED = "proxySelected";
    public static final String PREFERENCE_GENERAL_PROXY_TYPE = "proxyType";
    public static final String PREFERENCE_GENERAL_PROXY_HOST = "proxyHost";
    public static final String PREFERENCE_GENERAL_PROXY_PORT = "proxyPort";
    public static final String PREFERENCE_GENERAL_PROXY_USERNAME = "proxyUsername";
    public static final String PREFERENCE_GENERAL_PROXY_PASSWORD = "proxyPassword";
    public static final String PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS = "proxyExcludedUrls";
    public static final String PREFERENCE_XADES_POLICY_IDENTIFIER = "xadesPolicyIdentifier";
    public static final String PREFERENCE_XADES_POLICY_HASH = "xadesPolicyIdentifierHash";
    public static final String PREFERENCE_XADES_POLICY_HASH_ALGORITHM = "xadesPolicyIdentifierHashAlgorithm";
    public static final String PREFERENCE_XADES_POLICY_QUALIFIER = "xadesPolicyQualifier";
    public static final String PREFERENCE_XADES_SIGN_FORMAT = "xadesSignFormat";
    public static final String PREFERENCE_XADES_MULTISIGN = "xadesMultisign";
    public static final String PREFERENCE_XADES_SIGNATURE_PRODUCTION_CITY = "xadesSignatureProductionCity";
    public static final String PREFERENCE_XADES_SIGNATURE_PRODUCTION_PROVINCE = "xadesSignatureProductionProvince";
    public static final String PREFERENCE_XADES_SIGNATURE_PRODUCTION_POSTAL_CODE = "xadesSignatureProductionPostalCode";
    public static final String PREFERENCE_XADES_SIGNATURE_PRODUCTION_COUNTRY = "xadesSignatureProductionCountry";
    public static final String PREFERENCE_XADES_SIGNER_CLAIMED_ROLE = "xadesSignerClaimedRole";
    public static final String PREFERENCE_PADES_FORMAT = "padesBasicFormat";
    public static final String PREFERENCE_PADES_POLICY_IDENTIFIER = "padesPolicyIdentifier";
    public static final String PREFERENCE_PADES_POLICY_HASH = "padesPolicyIdentifierHash";
    public static final String PREFERENCE_PADES_POLICY_HASH_ALGORITHM = "padesPolicyIdentifierHashAlgorithm";
    public static final String PREFERENCE_PADES_POLICY_QUALIFIER = "padesPolicyQualifier";
    public static final String PREFERENCE_PADES_VISIBLE = "padesVisibleSignature";
    public static final String PREFERENCE_PADES_OBFUSCATE_CERT_INFO = "padesObfuscateCertInfo";
    public static final String PREFERENCE_PADES_STAMP = "padesVisibleStamp";
    public static final String PREFERENCE_PADES_CHECK_SHADOW_ATTACK = "allowShadowAttack";
    public static final String PREFERENCE_PADES_CHECK_ALLOW_CERTIFIED_PDF = "allowCertifiedPDF";
    public static final String PREFERENCE_PADES_DEFAULT_CERTIFICATION_LEVEL = "padesCertificationLevel";
    public static final String PREFERENCE_PADES_SIGN_REASON = "padesSignReason";
    public static final String PREFERENCE_PADES_SIGN_PRODUCTION_CITY = "padesSignProductionCity";
    public static final String PREFERENCE_PADES_SIGNER_CONTACT = "padesSignerContact";
    public static final String PREFERENCE_CADES_POLICY_IDENTIFIER = "cadesPolicyIdentifier";
    public static final String PREFERENCE_CADES_POLICY_HASH = "cadesPolicyIdentifierHash";
    public static final String PREFERENCE_CADES_POLICY_HASH_ALGORITHM = "cadesPolicyIdentifierHashAlgorithm";
    public static final String PREFERENCE_CADES_POLICY_QUALIFIER = "cadesPolicyQualifier";
    public static final String PREFERENCE_CADES_IMPLICIT = "cadesImplicitMode";
    public static final String PREFERENCE_CADES_MULTISIGN = "cadesMultisign";
    public static final String PREFERENCE_FACTURAE_POLICY = "facturaEPolicy";
    public static final String PREFERENCE_FACTURAE_POLICY_IDENTIFIER = "facturaePolicyIdentifier";
    public static final String PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH = "facturaePolicyIdentifierHash";
    public static final String PREFERENCE_FACTURAE_POLICY_IDENTIFIER_HASH_ALGORITHM = "facturaePolicyIdentifierHashAlgorithm";
    public static final String PREFERENCE_FACTURAE_POLICY_QUALIFIER = "facturaePolicyQualifier";
    public static final String PREFERENCE_FACTURAE_SIGNER_ROLE = "facturaeSignerRole";
    public static final String PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_CITY = "facturaeSignatureProductionCity";
    public static final String PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_PROVINCE = "facturaeSignatureProductionProvince";
    public static final String PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_POSTAL_CODE = "facturaeSignatureProductionPostalCode";
    public static final String PREFERENCE_FACTURAE_SIGNATURE_PRODUCTION_COUNTRY = "facturaeSignatureProductionCountry";
    public static final String PREFERENCE_PDF_SIGN_LAYER2TEXT = "pdfLayer2Text";
    public static final String PREFERENCE_PDF_SIGN_LAYER2FONTFAMILY = "pdfLayer2FontFamily";
    public static final String PREFERENCE_PDF_SIGN_LAYER2FONTSIZE = "pdfLayer2FontSize";
    public static final String PREFERENCE_PDF_SIGN_LAYER2FONTSTYLE = "pdfLayer2FontStyle";
    public static final String PREFERENCE_PDF_SIGN_LAYER2FONTCOLOR = "pdfLayer2FontColor";
    public static final String PREFERENCE_PDF_SIGN_SIGNATUREROTATION = "pdfSignatureRotation";
    public static final String PREFERENCE_PDF_SIGN_IMAGE = "pdfSignatureImage";
    public static final String PREFERENCE_KEYSTORE_DEFAULT_STORE = "defaultKeystore";
    public static final String VALUE_KEYSTORE_DEFAULT = "default";
    public static final String PREFERENCE_LOCAL_KEYSTORE_PATH = "defaultLocalKeystorePath";
    public static final String PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS = "useDefaultStoreInBrowserCalls";
    public static final String PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN = "hideDnieStartScreen";
    public static final String PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS = "useOnlySignatureCertificates";
    public static final String PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS = "showExpiredCerts";
    public static final String PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS = "useOnlyAliasCertificates";
    public static final String PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE = "skipAuthCertDnie";
    private static final String SYSTEM_PREFERENCE_CONFIG_FILE_SHA256 = "configFileSHA256";
    private static final String SYSTEM_PREFERENCE_CONFIGURATION_DATE = "configDate";
    private static final String CONFIGURATION_DATE_FORMAT = "YYYYMMdd";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String SYSTEM_PREFERENCE_CONFIG_FILE_URL = "configFileUrl";
    private static final String SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG = "allowUpdateConfig";
    private static final String[] SYSTEM_EXCLUSIVE_PREFERENCES = {SYSTEM_PREFERENCE_CONFIG_FILE_URL, SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG};
    private static final String PREFERENCE_NODE = "/es/gob/afirma/standalone/ui/preferences";
    private static final Preferences USER_PREFERENCES = Preferences.userRoot().node(PREFERENCE_NODE);

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/PreferencesManager$PreferencesSource.class */
    public enum PreferencesSource {
        USER,
        SYSTEM,
        SYSTEM_INTERNAL,
        DEFAULT
    }

    static {
        Preferences preferencesNode;
        Preferences preferencesNode2;
        try {
            preferencesNode = Preferences.systemRoot().nodeExists(PREFERENCE_NODE) ? Preferences.systemRoot().node(PREFERENCE_NODE) : null;
            preferencesNode2 = (preferencesNode != null && isAutommaticUpdateConfigAllowed() && Preferences.userRoot().nodeExists(UPDATED_SYSTEM_PREFERENCE_NODE)) ? Preferences.userRoot().node(UPDATED_SYSTEM_PREFERENCE_NODE) : null;
        } catch (Exception e) {
            LOGGER.warning("No se pueden cargar las preferencias establecidas a nivel de sistema: " + e);
            preferencesNode = null;
            preferencesNode2 = null;
        }
        if (preferencesNode2 != null) {
            SYSTEM_PREFERENCES = preferencesNode2;
            REAL_SYSTEM_PREFERENCES = preferencesNode;
        } else {
            SYSTEM_PREFERENCES = preferencesNode;
            REAL_SYSTEM_PREFERENCES = SYSTEM_PREFERENCES;
        }
        INTERNAL_PREFERENCES = Preferences.systemRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE);
        try {
            INTERNAL_PREFERENCES_DATA = loadInternalPreferencesData();
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error al cargar las propiedades internas de configuracion del sistema", (Throwable) e2);
            INTERNAL_PREFERENCES_DATA = null;
        }
        DEFAULT_PREFERENCES = new Properties();
        try {
            DEFAULT_PREFERENCES.load(PreferencesManager.class.getResourceAsStream("/properties/preferences.properties"));
        } catch (Exception e3) {
            LOGGER.severe("No han podido cargarse los valores por defecto del fichero de configuracion de preferencias, se usaran los valores por defecto: " + e3);
        }
    }

    private PreferencesManager() {
    }

    private static boolean isSystemConfigPreference(String str) {
        for (String str2 : SYSTEM_EXCLUSIVE_PREFERENCES) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    private static Properties loadInternalPreferencesData() throws BackingStoreException {
        Properties properties = null;
        Preferences preferencesSystemRoot = Preferences.systemRoot();
        if (preferencesSystemRoot.nodeExists(INTERNAL_SYSTEM_PREFERENCE_NODE)) {
            properties = new Properties();
            Preferences preferencesNode = preferencesSystemRoot.node(INTERNAL_SYSTEM_PREFERENCE_NODE);
            try {
                for (String str : preferencesNode.keys()) {
                    properties.setProperty(str, preferencesNode.get(str, null));
                }
                Preferences preferencesUserRoot = Preferences.userRoot();
                if (preferencesUserRoot.nodeExists(INTERNAL_SYSTEM_PREFERENCE_NODE)) {
                    Preferences preferencesNode2 = preferencesUserRoot.node(INTERNAL_SYSTEM_PREFERENCE_NODE);
                    for (String str2 : preferencesNode2.keys()) {
                        if (!isSystemConfigPreference(str2)) {
                            properties.setProperty(str2, preferencesNode2.get(str2, null));
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return properties;
    }

    public static String get(String str) {
        String str2 = USER_PREFERENCES.get(str, null);
        String str3 = SYSTEM_PREFERENCES != null ? SYSTEM_PREFERENCES.get(str, null) : null;
        String property = DEFAULT_PREFERENCES.getProperty(str);
        if (str2 != null) {
            return str2;
        }
        return str3 != null ? str3 : property;
    }

    public static String get(String str, PreferencesSource preferencesSource) {
        if (preferencesSource == null) {
            return get(str);
        }
        switch (preferencesSource) {
            case USER:
                return USER_PREFERENCES.get(str, null);
            case SYSTEM:
                if (SYSTEM_PREFERENCES != null) {
                    return SYSTEM_PREFERENCES.get(str, null);
                }
                return null;
            case SYSTEM_INTERNAL:
                if (INTERNAL_PREFERENCES_DATA != null) {
                    return INTERNAL_PREFERENCES_DATA.getProperty(str, null);
                }
                return null;
            case DEFAULT:
            default:
                return DEFAULT_PREFERENCES.getProperty(str);
        }
    }

    public static boolean getBoolean(String str) {
        boolean z = Boolean.parseBoolean(DEFAULT_PREFERENCES.getProperty(str));
        return USER_PREFERENCES.getBoolean(str, SYSTEM_PREFERENCES != null ? SYSTEM_PREFERENCES.getBoolean(str, z) : z);
    }

    public static boolean getBoolean(String str, PreferencesSource preferencesSource) {
        if (preferencesSource == null) {
            return getBoolean(str);
        }
        switch (preferencesSource) {
            case USER:
                return USER_PREFERENCES.getBoolean(str, false);
            case SYSTEM:
                if (SYSTEM_PREFERENCES != null) {
                    return SYSTEM_PREFERENCES.getBoolean(str, false);
                }
                return false;
            case SYSTEM_INTERNAL:
                if (INTERNAL_PREFERENCES_DATA != null) {
                    return Boolean.parseBoolean(INTERNAL_PREFERENCES_DATA.getProperty(str, Boolean.FALSE.toString()));
                }
                return false;
            case DEFAULT:
            default:
                return Boolean.parseBoolean(DEFAULT_PREFERENCES.getProperty(str));
        }
    }

    public static void put(String str, String str2) {
        String property = DEFAULT_PREFERENCES.getProperty(str);
        if (str2.equals(SYSTEM_PREFERENCES != null ? SYSTEM_PREFERENCES.get(str, property) : property)) {
            USER_PREFERENCES.remove(str);
        } else if (!str2.equals(get(str))) {
            USER_PREFERENCES.put(str, str2);
        }
    }

    public static void putBoolean(String str, boolean z) {
        boolean z2;
        if (SYSTEM_PREFERENCES != null) {
            z2 = SYSTEM_PREFERENCES.getBoolean(str, Boolean.parseBoolean(DEFAULT_PREFERENCES.getProperty(str)));
        } else {
            z2 = Boolean.parseBoolean(DEFAULT_PREFERENCES.getProperty(str));
        }
        if (z == z2) {
            USER_PREFERENCES.remove(str);
        } else if (z != getBoolean(str)) {
            USER_PREFERENCES.putBoolean(str, z);
        }
    }

    public static void putSystemPref(String str, String str2) {
        if (SYSTEM_PREFERENCES == null) {
            createSystemPrefs();
        }
        try {
            SYSTEM_PREFERENCES.put(str, str2);
        } catch (Exception e) {
            unlockSystemPreferences();
            SYSTEM_PREFERENCES.put(str, str2);
        }
    }

    private static void createSystemPrefs() {
        try {
            SYSTEM_PREFERENCES = Preferences.systemRoot().node(PREFERENCE_NODE);
            REAL_SYSTEM_PREFERENCES = SYSTEM_PREFERENCES;
        } catch (Exception e) {
            SYSTEM_PREFERENCES = Preferences.userRoot().node(UPDATED_SYSTEM_PREFERENCE_NODE);
            REAL_SYSTEM_PREFERENCES = null;
        }
    }

    private static void unlockSystemPreferences() {
        if (SYSTEM_PREFERENCES != null && !SYSTEM_PREFERENCES.isUserNode()) {
            REAL_SYSTEM_PREFERENCES = SYSTEM_PREFERENCES;
        }
        SYSTEM_PREFERENCES = Preferences.userRoot().node(UPDATED_SYSTEM_PREFERENCE_NODE);
        INTERNAL_PREFERENCES = Preferences.userRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE);
    }

    public static void putBooleanSystemPref(String str, boolean z) {
        if (SYSTEM_PREFERENCES == null) {
            createSystemPrefs();
        }
        try {
            SYSTEM_PREFERENCES.putBoolean(str, z);
        } catch (Exception e) {
            unlockSystemPreferences();
            SYSTEM_PREFERENCES.putBoolean(str, z);
        }
    }

    public static void remove(String str) {
        USER_PREFERENCES.remove(str);
    }

    public static void removeSystemPrefs(String str) {
        if (SYSTEM_PREFERENCES != null) {
            SYSTEM_PREFERENCES.remove(str);
        }
    }

    public static void clearAll() throws BackingStoreException {
        USER_PREFERENCES.clear();
    }

    public static void clearAllSystemPrefs() throws Exception {
        if (SYSTEM_PREFERENCES != null) {
            try {
                SYSTEM_PREFERENCES.clear();
            } catch (Exception e) {
                if (!SYSTEM_PREFERENCES.isUserNode()) {
                    unlockSystemPreferences();
                    clearAllSystemPrefs();
                    return;
                }
                throw e;
            }
        }
    }

    public static void flush() throws BackingStoreException {
        USER_PREFERENCES.flush();
    }

    public static void flushSystemPrefs() throws BackingStoreException {
        if (SYSTEM_PREFERENCES == null) {
            return;
        }
        try {
            SYSTEM_PREFERENCES.flush();
        } catch (Exception e) {
            if (SYSTEM_PREFERENCES.isUserNode()) {
                LOGGER.warning("No se pudieron guardar las preferencias en el apartado de configuracion actualizada del usuario: " + e);
                return;
            }
            HashMap map = new HashMap();
            for (String str : SYSTEM_PREFERENCES.childrenNames()) {
                map.put(str, SYSTEM_PREFERENCES.get(str, null));
            }
            unlockSystemPreferences();
            for (String str2 : (String[]) map.keySet().toArray(new String[0])) {
                SYSTEM_PREFERENCES.put(str2, (String) map.get(str2));
            }
        }
    }

    public static boolean isProtectedPreference(String str) {
        return str.equals(PREFERENCE_GENERAL_UPDATECHECK) || str.equals(PREFERENCE_GENERAL_ENABLED_JMULTICARD) || str.equals(PREFERENCE_GENERAL_SIGNATURE_ALGORITHM) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_PDF) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_OOXML) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_FACTURAE) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_ODF) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_XML) || str.equals(PREFERENCE_GENERAL_DEFAULT_FORMAT_BIN) || str.equals(PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES) || str.equals(PREFERENCE_GENERAL_SECURE_CONNECTIONS) || str.equals(PREFERENCE_XADES_POLICY_IDENTIFIER) || str.equals(PREFERENCE_XADES_POLICY_HASH) || str.equals(PREFERENCE_XADES_POLICY_HASH_ALGORITHM) || str.equals(PREFERENCE_XADES_POLICY_QUALIFIER) || str.equals(PREFERENCE_XADES_SIGN_FORMAT) || str.equals(PREFERENCE_PADES_FORMAT) || str.equals(PREFERENCE_PADES_POLICY_IDENTIFIER) || str.equals(PREFERENCE_PADES_POLICY_HASH) || str.equals(PREFERENCE_PADES_POLICY_HASH_ALGORITHM) || str.equals(PREFERENCE_PADES_POLICY_QUALIFIER) || str.equals("allowShadowAttack") || str.equals(PREFERENCE_CADES_POLICY_IDENTIFIER) || str.equals(PREFERENCE_CADES_POLICY_HASH) || str.equals(PREFERENCE_CADES_POLICY_HASH_ALGORITHM) || str.equals(PREFERENCE_CADES_POLICY_QUALIFIER) || str.equals(PREFERENCE_CADES_IMPLICIT) || str.equals(PREFERENCE_FACTURAE_POLICY) || str.equals(PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS);
    }

    public static Map<String, Object> getPrefsToExport() {
        HashMap map = new HashMap();
        try {
            if (SYSTEM_PREFERENCES != null) {
                String[] strArrKeys = SYSTEM_PREFERENCES.keys();
                for (int i = 0; i < strArrKeys.length; i++) {
                    String str = SYSTEM_PREFERENCES.get(strArrKeys[i], null);
                    if (str != null && (str.equals(TRUE_VALUE) || str.equals(FALSE_VALUE))) {
                        map.put(strArrKeys[i], Boolean.valueOf(str));
                    } else {
                        map.put(strArrKeys[i], str);
                    }
                }
            }
            String[] strArrKeys2 = USER_PREFERENCES.keys();
            for (int i2 = 0; i2 < strArrKeys2.length; i2++) {
                String str2 = USER_PREFERENCES.get(strArrKeys2[i2], null);
                if (str2 != null && (str2.equals(TRUE_VALUE) || str2.equals(FALSE_VALUE))) {
                    map.put(strArrKeys2[i2], Boolean.valueOf(str2));
                } else {
                    map.put(strArrKeys2[i2], str2);
                }
            }
        } catch (BackingStoreException e) {
            LOGGER.severe("Error al obtener preferencias configuradas por el usuario" + e);
        }
        return map;
    }

    static boolean isNewConfigFile(ConfigDataInfo configDataInfo) {
        String str = get(SYSTEM_PREFERENCE_CONFIG_FILE_SHA256, PreferencesSource.SYSTEM_INTERNAL);
        return str == null || !str.equals(configDataInfo.getHash());
    }

    static void setConfigFileInfo(ConfigDataInfo configDataInfo) {
        setConfigFileInfo(null, false, configDataInfo);
    }

    public static void setConfigFileInfo(String str, boolean z, ConfigDataInfo configDataInfo) {
        if (str != null) {
            try {
                INTERNAL_PREFERENCES.put(SYSTEM_PREFERENCE_CONFIG_FILE_URL, str);
            } catch (Exception e) {
                if (!INTERNAL_PREFERENCES.isUserNode()) {
                    INTERNAL_PREFERENCES = Preferences.userRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE);
                    setConfigFileInfo(str, z, configDataInfo);
                    return;
                } else {
                    LOGGER.log(Level.WARNING, "No se ha podido establecer la informacion de configuracion interna del sistema", (Throwable) e);
                    return;
                }
            }
        }
        if (z && hasHttpsSchema(str)) {
            INTERNAL_PREFERENCES.putBoolean(SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG, true);
        } else {
            INTERNAL_PREFERENCES.remove(SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG);
        }
        INTERNAL_PREFERENCES.put(SYSTEM_PREFERENCE_CONFIG_FILE_SHA256, configDataInfo.getHash());
        loadInternalPreferencesData();
    }

    static void setConfigCheckDate() {
        try {
            INTERNAL_PREFERENCES.put(SYSTEM_PREFERENCE_CONFIGURATION_DATE, new SimpleDateFormat(CONFIGURATION_DATE_FORMAT).format(new Date()));
            loadInternalPreferencesData();
        } catch (Exception e) {
            if (!INTERNAL_PREFERENCES.isUserNode()) {
                INTERNAL_PREFERENCES = Preferences.userRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE);
                setConfigCheckDate();
            } else {
                LOGGER.log(Level.WARNING, "No se ha podido establecer la fecha de configuracion interna del sistema", (Throwable) e);
            }
        }
    }

    private static boolean isAutommaticUpdateConfigAllowed() {
        try {
            return Preferences.systemRoot().nodeExists(INTERNAL_SYSTEM_PREFERENCE_NODE) && Preferences.systemRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE).getBoolean(SYSTEM_PREFERENCE_ALLOW_UPDATE_CONFIG, false);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido comprobar si esta activa la actualizacion automatica de la configuracion", (Throwable) e);
            return false;
        }
    }

    static boolean needCheckConfigUpdates() {
        if (!isAutommaticUpdateConfigAllowed() || !hasHttpsSchema(getConfigFileUrl())) {
            return false;
        }
        String str = get(SYSTEM_PREFERENCE_CONFIGURATION_DATE, PreferencesSource.SYSTEM_INTERNAL);
        return str == null || new SimpleDateFormat(CONFIGURATION_DATE_FORMAT).format(new Date()).compareTo(str) > 0;
    }

    private static boolean hasHttpsSchema(String str) {
        return str != null && str.toLowerCase(Locale.US).startsWith(PREFIX_HTTPS);
    }

    static String getConfigFileUrl() {
        return get(SYSTEM_PREFERENCE_CONFIG_FILE_URL, PreferencesSource.SYSTEM_INTERNAL);
    }

    public static void removeSystemPrefs() throws BackingStoreException {
        if (REAL_SYSTEM_PREFERENCES != null) {
            Preferences preferencesParent = REAL_SYSTEM_PREFERENCES.parent();
            REAL_SYSTEM_PREFERENCES.removeNode();
            removeEmptyTree(preferencesParent);
        }
        if (Preferences.systemRoot().nodeExists(INTERNAL_SYSTEM_PREFERENCE_NODE)) {
            Preferences preferencesParent2 = Preferences.systemRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE).parent();
            Preferences.systemRoot().node(INTERNAL_SYSTEM_PREFERENCE_NODE).removeNode();
            removeEmptyTree(preferencesParent2);
        }
    }

    private static void removeEmptyTree(Preferences preferences) throws BackingStoreException {
        Preferences preferences2 = preferences;
        while (true) {
            Preferences preferences3 = preferences2;
            if (!preferences3.name().isEmpty() && preferences3.childrenNames().length == 0 && preferences3.keys().length == 0) {
                Preferences preferencesParent = preferences3.parent();
                preferences3.removeNode();
                preferences2 = preferencesParent;
            } else {
                return;
            }
        }
    }
}
