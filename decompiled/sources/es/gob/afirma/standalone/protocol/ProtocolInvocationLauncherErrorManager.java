package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ProtocolInvocationLauncherErrorManager.class */
final class ProtocolInvocationLauncherErrorManager {
    static final String ERROR_CANNOT_READ_DATA = "SAF_00";
    static final String ERROR_NULL_URI = "SAF_01";
    static final String ERROR_UNSUPPORTED_PROTOCOL = "SAF_02";
    static final String ERROR_PARAMS = "SAF_03";
    static final String ERROR_UNSUPPORTED_OPERATION = "SAF_04";
    static final String ERROR_CANNOT_SAVE_DATA = "SAF_05";
    static final String ERROR_UNSUPPORTED_FORMAT = "SAF_06";
    static final String ERROR_CANNOT_FIND_KEYSTORE = "SAF_07";
    static final String ERROR_CANNOT_ACCESS_KEYSTORE = "SAF_08";
    static final String ERROR_SIGNATURE_FAILED = "SAF_09";
    static final String ERROR_NO_CERTIFICATES_SYSTEM = "SAF_10";
    static final String ERROR_SENDING_RESULT = "SAF_11";
    static final String ERROR_ENCRIPTING_DATA = "SAF_12";
    static final String ERROR_LOCAL_ACCESS_BLOCKED = "SAF_13";
    static final String ERROR_OBSOLETE_APP = "SAF_14";
    static final String ERROR_DECRYPTING_DATA = "SAF_15";
    static final String ERROR_RECOVERING_DATA = "SAF_16";
    static final String ERROR_UNKNOWN_SIGNER = "SAF_17";
    static final String ERROR_DECODING_CERTIFICATE = "SAF_18";
    static final String ERROR_NO_CERTIFICATES_KEYSTORE = "SAF_19";
    static final String ERROR_LOCAL_BATCH_SIGN = "SAF_20";
    static final String ERROR_UNSUPPORTED_PROCEDURE = "SAF_21";
    static final String ERROR_UNSOPPORTED_WEB_PROCEDURE = "SAF_22";
    static final String ERROR_INVALID_POLICY = "SAF_23";
    static final String ERROR_RECOVERING_LOG = "SAF_24";
    static final String ERROR_CANNOT_LOAD_DATA = "SAF_25";
    static final String ERROR_CONTACT_BATCH_SERVICE = "SAF_26";
    static final String ERROR_BATCH_SIGNATURE = "SAF_27";
    static final String ERROR_INVALID_PDF = "SAF_28";
    static final String ERROR_INVALID_XML = "SAF_29";
    static final String ERROR_INVALID_DATA = "SAF_30";
    static final String ERROR_NO_SIGN_DATA = "SAF_31";
    static final String ERROR_FACE_ALREADY_SIGNED = "SAF_32";
    static final String ERROR_PDF_WRONG_PASSWORD = "SAF_33";
    static final String ERROR_PDF_UNREG_SIGN = "SAF_34";
    static final String ERROR_PDF_CERTIFIED = "SAF_35";
    static final String ERROR_CANNOT_FIND_SSL_KEYSTORE = "SAF_36";
    static final String ERROR_CANNOT_ACCESS_SSL_KEYSTORE = "SAF_37";
    static final String ERROR_INVALID_FACTURAE = "SAF_38";
    static final String ERROR_INVALID_SIGNATURE = "SAF_39";
    static final String ERROR_RECOVER_SERVER_DOCUMENT = "SAF_40";
    static final String ERROR_MINIMUM_VERSION_NON_SATISTIED = "SAF_41";
    static final String ERROR_POSTPROCESSING_DATA = "SAF_42";
    static final String ERROR_VISIBLE_SIGNATURE = "SAF_43";
    static final String ERROR_SIGN_WITHOUT_DATA = "SAF_44";
    static final String ERROR_CANNOT_OPEN_SOCKET = "SAF_45";
    static final String ERROR_INVALID_SESSION_ID = "SAF_46";
    static final String ERROR_EXTERNAL_REQUEST_TO_SOCKET = "SAF_47";
    static final String ERROR_PDF_SHADOW_ATTACK = "SAF_48";
    static final String ERROR_SIGNING_LTS_SIGNATURE = "SAF_49";
    static final String ERROR_CONFIRMATION_NEEDED = "SAF_50";
    static final String ERROR_INCOMPATIBLE_KEY_TYPE = "SAF_51";
    static final String ERROR_LOCKED_KEYSTORE = "SAF_52";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final boolean HEADLESS = Boolean.getBoolean("es.gob.afirma.protocolinvocation.HeadLess");
    private static final Dictionary<String, String> ERRORS = new Hashtable();

    ProtocolInvocationLauncherErrorManager() {
    }

    static {
        ERRORS.put(ERROR_CANNOT_READ_DATA, ProtocolMessages.getString("ProtocolLauncher.0"));
        ERRORS.put(ERROR_NULL_URI, ProtocolMessages.getString("ProtocolLauncher.1"));
        ERRORS.put(ERROR_UNSUPPORTED_PROTOCOL, ProtocolMessages.getString("ProtocolLauncher.2"));
        ERRORS.put(ERROR_PARAMS, ProtocolMessages.getString("ProtocolLauncher.3"));
        ERRORS.put(ERROR_UNSUPPORTED_OPERATION, ProtocolMessages.getString("ProtocolLauncher.4"));
        ERRORS.put(ERROR_CANNOT_SAVE_DATA, ProtocolMessages.getString("ProtocolLauncher.5"));
        ERRORS.put(ERROR_UNSUPPORTED_FORMAT, ProtocolMessages.getString("ProtocolLauncher.6"));
        ERRORS.put(ERROR_CANNOT_FIND_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.7"));
        ERRORS.put(ERROR_CANNOT_ACCESS_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.8"));
        ERRORS.put(ERROR_SIGNATURE_FAILED, ProtocolMessages.getString("ProtocolLauncher.9"));
        ERRORS.put(ERROR_NO_CERTIFICATES_SYSTEM, ProtocolMessages.getString("ProtocolLauncher.10"));
        ERRORS.put(ERROR_SENDING_RESULT, ProtocolMessages.getString("ProtocolLauncher.11"));
        ERRORS.put(ERROR_ENCRIPTING_DATA, ProtocolMessages.getString("ProtocolLauncher.12"));
        ERRORS.put(ERROR_LOCAL_ACCESS_BLOCKED, ProtocolMessages.getString("ProtocolLauncher.13"));
        ERRORS.put(ERROR_OBSOLETE_APP, ProtocolMessages.getString("ProtocolLauncher.14"));
        ERRORS.put(ERROR_DECRYPTING_DATA, ProtocolMessages.getString("ProtocolLauncher.15"));
        ERRORS.put(ERROR_RECOVERING_DATA, ProtocolMessages.getString("ProtocolLauncher.16"));
        ERRORS.put(ERROR_UNKNOWN_SIGNER, ProtocolMessages.getString("ProtocolLauncher.17"));
        ERRORS.put(ERROR_DECODING_CERTIFICATE, ProtocolMessages.getString("ProtocolLauncher.18"));
        ERRORS.put(ERROR_NO_CERTIFICATES_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.19"));
        ERRORS.put(ERROR_LOCAL_BATCH_SIGN, ProtocolMessages.getString("ProtocolLauncher.20"));
        ERRORS.put(ERROR_UNSUPPORTED_PROCEDURE, ProtocolMessages.getString("ProtocolLauncher.21"));
        ERRORS.put(ERROR_UNSOPPORTED_WEB_PROCEDURE, ProtocolMessages.getString("ProtocolLauncher.22"));
        ERRORS.put(ERROR_INVALID_POLICY, ProtocolMessages.getString("ProtocolLauncher.33"));
        ERRORS.put(ERROR_RECOVERING_LOG, ProtocolMessages.getString("ProtocolLauncher.34"));
        ERRORS.put(ERROR_CANNOT_LOAD_DATA, ProtocolMessages.getString("ProtocolLauncher.35"));
        ERRORS.put(ERROR_CONTACT_BATCH_SERVICE, ProtocolMessages.getString("ProtocolLauncher.36"));
        ERRORS.put(ERROR_BATCH_SIGNATURE, ProtocolMessages.getString("ProtocolLauncher.37"));
        ERRORS.put(ERROR_INVALID_PDF, ProtocolMessages.getString("ProtocolLauncher.38"));
        ERRORS.put(ERROR_INVALID_XML, ProtocolMessages.getString("ProtocolLauncher.39"));
        ERRORS.put(ERROR_INVALID_DATA, ProtocolMessages.getString("ProtocolLauncher.40"));
        ERRORS.put(ERROR_NO_SIGN_DATA, ProtocolMessages.getString("ProtocolLauncher.41"));
        ERRORS.put(ERROR_FACE_ALREADY_SIGNED, ProtocolMessages.getString("ProtocolLauncher.42"));
        ERRORS.put(ERROR_PDF_WRONG_PASSWORD, ProtocolMessages.getString("ProtocolLauncher.43"));
        ERRORS.put(ERROR_PDF_UNREG_SIGN, ProtocolMessages.getString("ProtocolLauncher.44"));
        ERRORS.put(ERROR_PDF_CERTIFIED, ProtocolMessages.getString("ProtocolLauncher.45"));
        ERRORS.put(ERROR_CANNOT_FIND_SSL_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.46"));
        ERRORS.put(ERROR_CANNOT_ACCESS_SSL_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.47"));
        ERRORS.put(ERROR_INVALID_FACTURAE, ProtocolMessages.getString("ProtocolLauncher.48"));
        ERRORS.put(ERROR_INVALID_SIGNATURE, ProtocolMessages.getString("ProtocolLauncher.49"));
        ERRORS.put(ERROR_RECOVER_SERVER_DOCUMENT, ProtocolMessages.getString("ProtocolLauncher.50"));
        ERRORS.put(ERROR_MINIMUM_VERSION_NON_SATISTIED, ProtocolMessages.getString("ProtocolLauncher.53"));
        ERRORS.put(ERROR_POSTPROCESSING_DATA, ProtocolMessages.getString("ProtocolLauncher.54"));
        ERRORS.put(ERROR_VISIBLE_SIGNATURE, ProtocolMessages.getString("ProtocolLauncher.55"));
        ERRORS.put(ERROR_SIGN_WITHOUT_DATA, ProtocolMessages.getString("ProtocolLauncher.56"));
        ERRORS.put(ERROR_CANNOT_OPEN_SOCKET, ProtocolMessages.getString("ProtocolLauncher.57"));
        ERRORS.put(ERROR_INVALID_SESSION_ID, ProtocolMessages.getString("ProtocolLauncher.58"));
        ERRORS.put(ERROR_EXTERNAL_REQUEST_TO_SOCKET, ProtocolMessages.getString("ProtocolLauncher.59"));
        ERRORS.put(ERROR_PDF_SHADOW_ATTACK, ProtocolMessages.getString("ProtocolLauncher.63"));
        ERRORS.put(ERROR_SIGNING_LTS_SIGNATURE, ProtocolMessages.getString("ProtocolLauncher.64"));
        ERRORS.put(ERROR_CONFIRMATION_NEEDED, ProtocolMessages.getString("ProtocolLauncher.65"));
        ERRORS.put(ERROR_INCOMPATIBLE_KEY_TYPE, ProtocolMessages.getString("ProtocolLauncher.66"));
        ERRORS.put(ERROR_LOCKED_KEYSTORE, ProtocolMessages.getString("ProtocolLauncher.67"));
    }

    static void showError(String str) {
        showError(str, null);
    }

    static void showError(String str, Throwable th) {
        showError(str, ERRORS.get(str), th);
    }

    static void showError(String str, String str2, Throwable th) {
        String str3 = ProtocolMessages.getString("ProtocolLauncher.28") + "\n(" + str + ": " + str2 + ")";
        if (!HEADLESS) {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                MacUtils.focusApplication();
            }
            AOUIFactory.showErrorMessage(str3, ProtocolMessages.getString("ProtocolLauncher.29"), AOUIFactory.ERROR_MESSAGE, th);
        }
        LOGGER.severe(str3);
    }

    static void showErrorDetail(String str, String str2) {
        showErrorDetail(str, ERRORS.get(str), str2);
    }

    static void showErrorDetail(String str, String str2, String str3) {
        showError(str, str2 + "\n" + str3, null);
    }

    static void showErrorDetail(String str, Throwable th) {
        showError(str, th);
    }

    static void showErrorDetail(String str, String str2, Throwable th) {
        showError(str, str2, th);
    }

    static String getErrorMessage(String str) {
        return str + ": " + ERRORS.get(str);
    }
}
