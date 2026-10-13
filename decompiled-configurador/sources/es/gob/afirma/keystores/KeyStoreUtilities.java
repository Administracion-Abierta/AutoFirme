package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.jmulticard.CancelledOperationException;
import es.gob.jmulticard.jse.provider.BadPasswordProviderException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/KeyStoreUtilities.class */
public final class KeyStoreUtilities {
    public static final String DISABLE_DNIE_NATIVE_DRIVER_ENV = "AFIRMA_NSS_DISABLE_DNIE_NATIVE_DRIVER";
    public static final String DISABLE_DNIE_NATIVE_DRIVER = "es.gob.afirma.keystores.mozilla.disableDnieNativeDriver";
    public static final String DISABLE_CERES_NATIVE_DRIVER_ENV = "AFIRMA_NSS_DISABLE_CERES_NATIVE_DRIVER";
    public static final String DISABLE_CERES_NATIVE_DRIVER = "es.gob.afirma.keystores.mozilla.disableCeresNativeDriver";
    public static final String ENABLE_GYDSC_NATIVE_DRIVER_ENV = "AFIRMA_NSS_ENABLE_GYDSC_NATIVE_DRIVER";
    public static final String ENABLE_GYDSC_NATIVE_DRIVER = "es.gob.afirma.keystores.mozilla.enableGYDSCNativeDriver";
    private static final int ALIAS_MAX_LENGTH = 120;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] FNMT_PKCS11_LIBS_WITHOUT_SHA1 = {"DNIe_P11_priv.dll", "DNIe_P11_pub.dll", "FNMT_P11.dll", "FNMT_P11_x64.dll", "UsrPkcs11.dll", "UsrPubPkcs11.dll", "TIF_P11.dll"};
    private static final String PIN_ERROR_LOCKED = "CKR_PIN_LOCKED";
    private static final String PIN_ERROR_WRONG_LENGTH = "CKR_PIN_LEN_RANGE";
    private static final String PIN_ERROR_INCORRECT = "CKR_PIN_INCORRECT";

    private KeyStoreUtilities() {
    }

    public static String createPKCS11ConfigFile(String str, String str2, Integer num) {
        StringBuilder sb = new StringBuilder("library=");
        sb.append(getLibraryPath(str));
        sb.append("\r\n").append("name=").append(str2 != null ? str2 : "AFIRMA-PKCS11").append("\r\nshowInfo=true\r\n");
        if (num != null) {
            sb.append("slot=").append(num).append("\r\n");
        }
        for (String str3 : FNMT_PKCS11_LIBS_WITHOUT_SHA1) {
            if (str3.equalsIgnoreCase(new File(str).getName())) {
                sb.append("disabledMechanisms={ CKM_SHA1_RSA_PKCS }\r\n");
                break;
            }
        }
        LOGGER.info("Creada configuracion PKCS#11:\r\n" + sb.toString());
        return sb.toString();
    }

    private static String getLibraryPath(String str) {
        File file = new File(str);
        if (!file.isFile() && !file.isAbsolute()) {
            boolean z = false;
            String[] strArrSplit = System.getProperty("java.library.path").split(File.pathSeparator);
            for (int i = 0; i < strArrSplit.length && !z; i++) {
                File file2 = new File(strArrSplit[i], str);
                if (file2.isFile()) {
                    file = file2;
                    z = true;
                }
            }
        }
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.contains(")") || absolutePath.contains("(")) {
            absolutePath = getWindowsShortName(absolutePath);
        }
        LOGGER.info(LoggerUtil.getCleanUserHomePath(String.format("La ruta de la libreria '%s' se interpretara como '%s'", str, absolutePath)));
        return absolutePath;
    }

    /* JADX WARN: Code duplicated, block: B:102:0x0194 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:36:0x0111 A[Catch: Exception -> 0x014a, TryCatch #0 {Exception -> 0x014a, blocks: (B:34:0x0106, B:36:0x0111), top: B:86:0x0106 }] */
    /* JADX WARN: Code duplicated, block: B:86:0x0106 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static Map<String, String> getAliasesByFriendlyName(String[] strArr, KeyStoreManager keyStoreManager, boolean z, boolean z2, List<? extends CertificateFilter> list) {
        String[] strArr2 = (String[]) strArr.clone();
        Hashtable hashtable = new Hashtable(strArr2.length);
        for (String str : strArr2) {
            hashtable.put(str, str);
        }
        if (keyStoreManager != null) {
            for (String str2 : (String[]) hashtable.keySet().toArray(new String[hashtable.size()])) {
                try {
                    X509Certificate certificate = keyStoreManager.getCertificate(str2);
                    if (certificate == null) {
                        LOGGER.warning("El KeyStore no permite extraer el certificado publico para el alias especificado");
                    } else if (!z2) {
                        try {
                            certificate.checkValidity();
                            if (z) {
                                try {
                                    if (!keyStoreManager.isKeyEntry(str2)) {
                                        hashtable.remove(str2);
                                        LOGGER.info(String.format("Se ha ocultado el certificado con numero de serie '%s' (emitido por '%s') por no soportar operaciones de clave privada", AOUtil.hexify(certificate.getSerialNumber().toByteArray(), false), AOUtil.getCN(certificate.getIssuerX500Principal().toString())));
                                    }
                                } catch (Exception e) {
                                    hashtable.remove(str2);
                                    LOGGER.info(String.format("Se ha ocultado el certificado con numero de serie '%s' (emitido por '%s') por no poderse comprobar su clave privada: " + e, AOUtil.hexify(certificate.getSerialNumber().toByteArray(), false), AOUtil.getCN(certificate.getIssuerX500Principal().toString())));
                                }
                            }
                        } catch (Exception e2) {
                            LOGGER.info("Se ocultara el certificado por no estar vigente: " + e2);
                            hashtable.remove(str2);
                        }
                    } else if (z) {
                        if (!keyStoreManager.isKeyEntry(str2)) {
                            hashtable.remove(str2);
                            LOGGER.info(String.format("Se ha ocultado el certificado con numero de serie '%s' (emitido por '%s') por no soportar operaciones de clave privada", AOUtil.hexify(certificate.getSerialNumber().toByteArray(), false), AOUtil.getCN(certificate.getIssuerX500Principal().toString())));
                        }
                    }
                } catch (AOCancelledOperationException e3) {
                    throw e3;
                } catch (RuntimeException e4) {
                    if ((e4 instanceof CancelledOperationException) || (e4 instanceof BadPasswordProviderException)) {
                        throw e4;
                    }
                    LOGGER.warning("No se ha inicializado el KeyStore indicado: " + e4);
                }
            }
            if (list != null && list.size() > 0) {
                Hashtable hashtable2 = new Hashtable();
                Iterator<? extends CertificateFilter> it = list.iterator();
                while (it.hasNext()) {
                    for (String str3 : it.next().matches((String[]) hashtable.keySet().toArray(new String[hashtable.size()]), keyStoreManager)) {
                        hashtable2.put(str3, hashtable.get(str3));
                        hashtable.remove(str3);
                    }
                }
                if (LOGGER.isLoggable(Level.INFO)) {
                    for (String str4 : (String[]) hashtable.keySet().toArray(new String[0])) {
                        try {
                            X509Certificate certificate2 = keyStoreManager.getCertificate(str4);
                            LOGGER.info(String.format("Se ha ocultado el certificado con numero de serie '%s' (emitido por '%s') por no ajustarse a los filtros proporcionados", AOUtil.hexify(certificate2.getSerialNumber().toByteArray(), false), AOUtil.getCN(certificate2.getIssuerX500Principal().toString())));
                        } catch (Exception e5) {
                            LOGGER.info(String.format("Se ha ocultado el certificado con alias '%s' por no ajustarse a los filtros proporcionados: " + e5, str4));
                        }
                    }
                }
                hashtable.clear();
                hashtable.putAll(hashtable2);
            }
            for (String str5 : (String[]) hashtable.keySet().toArray(new String[0])) {
                String cn = AOUtil.getCN(keyStoreManager.getCertificate(str5));
                if (cn != null) {
                    hashtable.put(str5, cn);
                } else {
                    hashtable.put(str5, str5.trim());
                }
            }
        } else {
            for (String str6 : (String[]) hashtable.keySet().toArray(new String[hashtable.size()])) {
                String str7 = (String) hashtable.get(str6);
                if (str7.length() > ALIAS_MAX_LENGTH) {
                    String cn2 = AOUtil.getCN(str7);
                    if (cn2 != null) {
                        hashtable.put(str6, cn2);
                    } else {
                        hashtable.put(str6, str7.substring(0, ALIAS_MAX_LENGTH - "...".length()) + "...");
                    }
                } else {
                    hashtable.put(str6, str7.trim());
                }
            }
        }
        return hashtable;
    }

    public static String getWindowsShortName(String str) {
        File canonicalFile;
        if (str == null || !Platform.OS.WINDOWS.equals(Platform.getOS())) {
            return str;
        }
        try {
            canonicalFile = new File(str.replace("\"", "").replace("'", "")).getCanonicalFile();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "La ruta de fichero a acortar no es valida", (Throwable) e);
            canonicalFile = null;
        }
        if (canonicalFile == null || !canonicalFile.exists()) {
            return str;
        }
        try {
            InputStream inputStream = new ProcessBuilder("cmd.exe", "/c", "for %f in (\"" + canonicalFile.getAbsolutePath() + "\") do @echo %~sf").start().getInputStream();
            Throwable th = null;
            try {
                try {
                    String strTrim = new String(AOUtil.getDataFromInputStream(inputStream)).trim();
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    return strTrim;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e2) {
            LOGGER.warning("No se ha podido obtener el nombre corto de " + LoggerUtil.getCleanUserHomePath(str) + ": " + e2);
            return str;
        }
    }

    public static boolean addPreferredKeyStoreManagers(AggregatedKeyStoreManager aggregatedKeyStoreManager, Object obj) {
        if (!Boolean.getBoolean(DISABLE_DNIE_NATIVE_DRIVER) && !Boolean.parseBoolean(System.getenv(DISABLE_DNIE_NATIVE_DRIVER_ENV))) {
            try {
                aggregatedKeyStoreManager.addKeyStoreManager(getDnieKeyStoreManager(obj));
                return true;
            } catch (AOCancelledOperationException e) {
                throw e;
            } catch (Exception e2) {
                LOGGER.info("No se ha encontrado un DNIe: " + e2);
            } catch (NoClassDefFoundError e3) {
                LOGGER.info("No se puede inicializar el almacen de tipo DNIe: " + e3);
            }
        }
        if (!Boolean.getBoolean(DISABLE_CERES_NATIVE_DRIVER) && !Boolean.parseBoolean(System.getenv(DISABLE_CERES_NATIVE_DRIVER_ENV))) {
            try {
                aggregatedKeyStoreManager.addKeyStoreManager(getCeres430KeyStoreManager(obj));
                return true;
            } catch (AOCancelledOperationException e4) {
                throw e4;
            } catch (Exception e5) {
                LOGGER.info("No se ha encontrado una tarjeta CERES 4.30 o superior: " + e5);
                try {
                    aggregatedKeyStoreManager.addKeyStoreManager(getCeresKeyStoreManager(obj));
                    return true;
                } catch (AOCancelledOperationException e6) {
                    throw e6;
                } catch (Exception e7) {
                    LOGGER.info("No se ha encontrado una tarjeta CERES: " + e7);
                } catch (NoClassDefFoundError e8) {
                    LOGGER.info("No se puede inicializar el almacen de tipo CERES (otras): " + e8);
                }
            } catch (NoClassDefFoundError e9) {
                LOGGER.info("No se puede inicializar el almacen de tipo CERES 4.30 o superior: " + e9);
                aggregatedKeyStoreManager.addKeyStoreManager(getCeresKeyStoreManager(obj));
                return true;
            }
        }
        if (Boolean.getBoolean(ENABLE_GYDSC_NATIVE_DRIVER) || Boolean.parseBoolean(System.getenv(ENABLE_GYDSC_NATIVE_DRIVER_ENV))) {
            try {
                aggregatedKeyStoreManager.addKeyStoreManager(getSmartCafeKeyStoreManager(obj));
                return true;
            } catch (AOCancelledOperationException e10) {
                throw e10;
            } catch (Exception e11) {
                LOGGER.info("No se ha encontrado una tarjeta G&D SmartCafe: " + e11);
                return false;
            } catch (NoClassDefFoundError e12) {
                LOGGER.info("No se puede inicializar el almacen de tipo G&D SmartCafe: " + e12);
                return false;
            }
        }
        return false;
    }

    private static AOKeyStoreManager getDnieKeyStoreManager(Object obj) throws AOKeystoreAlternativeException, IOException {
        AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.DNIEJAVA, null, null, null, obj);
        LOGGER.info("El DNIe 100% Java ha podido inicializarse, se anadiran sus entradas");
        aOKeyStoreManager.setPreferred(true);
        return aOKeyStoreManager;
    }

    private static AOKeyStoreManager getCeres430KeyStoreManager(Object obj) throws AOKeystoreAlternativeException, IOException {
        AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.CERES_430, null, null, null, obj);
        LOGGER.info("La tarjeta CERES 430 ha podido inicializarse, se anadiran sus entradas");
        aOKeyStoreManager.setPreferred(true);
        return aOKeyStoreManager;
    }

    private static AOKeyStoreManager getCeresKeyStoreManager(Object obj) throws AOKeystoreAlternativeException, IOException {
        AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.CERES, null, null, null, obj);
        LOGGER.info("La tarjeta CERES ha podido inicializarse, se anadiran sus entradas");
        aOKeyStoreManager.setPreferred(true);
        return aOKeyStoreManager;
    }

    private static AOKeyStoreManager getSmartCafeKeyStoreManager(Object obj) throws AOKeystoreAlternativeException, IOException {
        AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.SMARTCAFE, null, null, null, obj);
        LOGGER.info("La tarjeta SmartCafe ha podido inicializarse, se anadiran sus entradas");
        aOKeyStoreManager.setPreferred(true);
        return aOKeyStoreManager;
    }

    public static KeyStore getKeyStoreWithPasswordCallbackHandler(AOKeyStore aOKeyStore, PasswordCallback passwordCallback, Provider provider, Object obj) throws KeyStoreException {
        PasswordCallbackHandler passwordCallbackHandler = new PasswordCallbackHandler(obj, passwordCallback);
        try {
            return KeyStore.Builder.newInstance(aOKeyStore.getProviderName(), provider, new KeyStore.CallbackHandlerProtection(passwordCallbackHandler)).getKeyStore();
        } catch (KeyStoreException e) {
            if (passwordCallbackHandler.isCancelled()) {
                LOGGER.warning("Se ha detectado la cancelacion del dialogo de PIN");
                throw new AOCancelledOperationException("Se cancelo el dialogo de insercion de PIN");
            }
            if (e.getCause() != null && e.getCause().getCause() != null && (e.getCause().getCause() instanceof LoginException)) {
                Throwable cause = e.getCause().getCause().getCause();
                boolean z = false;
                String string = KeyStoreMessages.getString("KeyStoreUtilities.5");
                if (cause != null) {
                    if (PIN_ERROR_LOCKED.equals(cause.getMessage())) {
                        string = KeyStoreMessages.getString("KeyStoreUtilities.7");
                        z = true;
                    } else if (PIN_ERROR_WRONG_LENGTH.equals(cause.getMessage())) {
                        string = KeyStoreMessages.getString("KeyStoreUtilities.8");
                    } else if (PIN_ERROR_INCORRECT.equals(cause.getMessage())) {
                        string = KeyStoreMessages.getString("KeyStoreUtilities.5");
                    }
                }
                AOUIFactory.showMessageDialog(obj, string, KeyStoreMessages.getString("KeyStoreUtilities.6"), AOUIFactory.ERROR_MESSAGE);
                if (!z) {
                    return getKeyStoreWithPasswordCallbackHandler(aOKeyStore, passwordCallback, provider, obj);
                }
            }
            throw e;
        }
    }

    public static String searchPathForFile(String[] strArr) {
        if (strArr == null || strArr.length < 1) {
            return null;
        }
        if (new File(strArr[0]).exists()) {
            return strArr[0];
        }
        StringTokenizer stringTokenizer = new StringTokenizer(Platform.getJavaLibraryPath(), File.pathSeparator);
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (!strNextToken.endsWith(File.separator)) {
                strNextToken = strNextToken + File.separator;
            }
            for (String str : strArr) {
                File file = new File(strNextToken, str);
                if (file.exists() && !file.isDirectory()) {
                    return strNextToken + str;
                }
            }
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/KeyStoreUtilities$PasswordCallbackHandler.class */
    public static class PasswordCallbackHandler implements CallbackHandler {
        private final Object parentComponent;
        private final PasswordCallback pssCallBack;
        private boolean cancelled = false;

        public PasswordCallbackHandler(Object obj, PasswordCallback passwordCallback) {
            this.parentComponent = obj;
            this.pssCallBack = passwordCallback;
        }

        @Override // javax.security.auth.callback.CallbackHandler
        public void handle(Callback[] callbackArr) throws UnsupportedCallbackException {
            for (Callback callback : callbackArr) {
                if (callback instanceof PasswordCallback) {
                    try {
                        ((PasswordCallback) callback).setPassword(this.pssCallBack.getPassword());
                    } catch (AOCancelledOperationException e) {
                        this.cancelled = true;
                    }
                } else if (callback instanceof TextOutputCallback) {
                    TextOutputCallback textOutputCallback = (TextOutputCallback) callback;
                    switch (textOutputCallback.getMessageType()) {
                        case 0:
                            KeyStoreUtilities.LOGGER.info("Informacion del dispositivo criptografico: " + textOutputCallback.getMessage());
                            AOUIFactory.showMessageDialog(this.parentComponent, textOutputCallback.getMessage(), KeyStoreMessages.getString("KeyStoreUtilities.0"), AOUIFactory.INFORMATION_MESSAGE);
                            break;
                        case 1:
                            KeyStoreUtilities.LOGGER.warning("Informacion del dispositivo criptografico: " + textOutputCallback.getMessage());
                            AOUIFactory.showMessageDialog(this.parentComponent, textOutputCallback.getMessage(), KeyStoreMessages.getString("KeyStoreUtilities.2"), AOUIFactory.WARNING_MESSAGE);
                            break;
                        case 2:
                            KeyStoreUtilities.LOGGER.severe("Informacion del dispositivo criptografico: " + textOutputCallback.getMessage());
                            AOUIFactory.showMessageDialog(this.parentComponent, textOutputCallback.getMessage(), KeyStoreMessages.getString("KeyStoreUtilities.1"), AOUIFactory.ERROR_MESSAGE);
                            break;
                        default:
                            KeyStoreUtilities.LOGGER.warning("Recibida informacion del dispositivo criptografico en un formato desconocido: " + textOutputCallback.getMessageType());
                            break;
                    }
                } else {
                    if (callback instanceof NameCallback) {
                        Object objShowInputDialog = AOUIFactory.showInputDialog(this.parentComponent, KeyStoreMessages.getString("KeyStoreUtilities.3"), KeyStoreMessages.getString("KeyStoreUtilities.4"), AOUIFactory.WARNING_MESSAGE, null, null, null);
                        if (objShowInputDialog != null) {
                            ((NameCallback) callback).setName(objShowInputDialog.toString());
                        }
                        throw new UnsupportedCallbackException(callback, "No se soporta la solicitud de nombre de usuario para dispositivos criptograficos");
                    }
                    throw new UnsupportedCallbackException(callback, "Recibido tipo de callback desconocido: " + callback.getClass().getName());
                }
            }
        }

        public boolean isCancelled() {
            return this.cancelled;
        }
    }
}
