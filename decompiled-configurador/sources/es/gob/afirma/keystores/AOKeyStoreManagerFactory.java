package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.callbacks.NullPasswordCallback;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStoreManagerFactory.class */
public final class AOKeyStoreManagerFactory {
    public static final String FORCE_STORE_RESET = "es.gob.afirma.keystores.ForceReset";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static AggregatedKeyStoreManager mozillaKeyStoreManager = null;

    private AOKeyStoreManagerFactory() {
    }

    public static AggregatedKeyStoreManager getAOKeyStoreManager(AOKeyStore aOKeyStore, String str, String str2, PasswordCallback passwordCallback, Object obj) throws AOKeystoreAlternativeException, IOException {
        boolean z;
        try {
            z = Boolean.getBoolean(FORCE_STORE_RESET);
        } catch (Exception e) {
            z = false;
            LOGGER.warning("No se ha podido leer la variable 'es.gob.afirma.keystores.ForceReset', se usara 'false' como valor por defecto: " + e);
        }
        if (z) {
            LOGGER.info("No se mantendran los almacenes de claves precargados (es.gob.afirma.keystores.ForceReset=true)");
        }
        if (AOKeyStore.PKCS12.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getPkcs12KeyStoreManager(str, passwordCallback, z, obj));
        }
        if (AOKeyStore.JAVA.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getJavaKeyStoreManager(str, passwordCallback, z, obj));
        }
        if (AOKeyStore.SINGLE.equals(aOKeyStore) || AOKeyStore.JAVACE.equals(aOKeyStore) || AOKeyStore.JCEKS.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getFileKeyStoreManager(aOKeyStore, str, passwordCallback, z, obj));
        }
        if (AOKeyStore.PKCS11.equals(aOKeyStore)) {
            AOKeyStoreManager pkcs11KeyStoreManager = getPkcs11KeyStoreManager(str, str2, passwordCallback, z, obj);
            AggregatedKeyStoreManager aggregatedKeyStoreManager = new AggregatedKeyStoreManager(pkcs11KeyStoreManager);
            aggregatedKeyStoreManager.setKeyStore(pkcs11KeyStoreManager.getKeyStore());
            return aggregatedKeyStoreManager;
        }
        if (Platform.getOS().equals(Platform.OS.WINDOWS) && AOKeyStore.WINDOWS.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getWindowsMyCapiKeyStoreManager(z));
        }
        if (Platform.getOS().equals(Platform.OS.WINDOWS) && (AOKeyStore.WINADDRESSBOOK.equals(aOKeyStore) || AOKeyStore.WINCA.equals(aOKeyStore))) {
            return new AggregatedKeyStoreManager(getWindowsAddressBookKeyStoreManager(aOKeyStore, z));
        }
        if (AOKeyStore.MOZ_UNI.equals(aOKeyStore)) {
            return getMozillaUnifiedKeyStoreManager(passwordCallback, z, obj);
        }
        if (Platform.getOS().equals(Platform.OS.LINUX) && AOKeyStore.SHARED_NSS.equals(aOKeyStore)) {
            return getSharedNssKeyStoreManager(passwordCallback, z, obj);
        }
        if (Platform.getOS().equals(Platform.OS.MACOSX) && AOKeyStore.APPLE.equals(aOKeyStore)) {
            return getMacOSXKeyStoreManager(aOKeyStore, str, z, obj);
        }
        if (AOKeyStore.DNIEJAVA.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getDnieJavaKeyStoreManager(passwordCallback, z, obj));
        }
        if (AOKeyStore.CERES_430.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getCeres430JavaKeyStoreManager(passwordCallback, z, obj));
        }
        if (AOKeyStore.CERES.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getCeresJavaKeyStoreManager(passwordCallback, z, obj));
        }
        if (AOKeyStore.SMARTCAFE.equals(aOKeyStore)) {
            return new AggregatedKeyStoreManager(getSmartCafeJavaKeyStoreManager(passwordCallback, z, obj));
        }
        throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(aOKeyStore), "La plataforma de navegador '" + aOKeyStore.getName() + "' mas sistema operativo '" + Platform.getOS() + "' no esta soportada");
    }

    private static AOKeyStoreManager addFileKeyStoreManager(AOKeyStoreManager aOKeyStoreManager, String[] strArr, String str, String str2, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        String absolutePath = null;
        if (str2 != null && !str2.isEmpty() && new File(str2).exists()) {
            absolutePath = str2;
        }
        if (absolutePath == null) {
            absolutePath = AOUIFactory.getLoadFiles(KeyStoreMessages.getString("AOKeyStoreManagerFactory.4") + " " + aOKeyStoreManager.getType().getName(), null, null, strArr, str, false, false, null, obj)[0].getAbsolutePath();
            if (absolutePath == null) {
                throw new AOCancelledOperationException("No se ha seleccionado el almacen de certificados");
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            Throwable th = null;
            try {
                aOKeyStoreManager.init(null, fileInputStream, passwordCallback, null, z);
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                return aOKeyStoreManager;
            } catch (Throwable th3) {
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th3;
            }
        } catch (AOException e) {
            throw new AOKeystoreAlternativeException(AOKeyStore.JAVA, "No se ha podido abrir el almacen de tipo " + aOKeyStoreManager.getType().getName() + " para el fichero " + str2, e);
        }
    }

    private static AOKeyStoreManager getPkcs12KeyStoreManager(String str, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        Pkcs12KeyStoreManager pkcs12KeyStoreManager = new Pkcs12KeyStoreManager();
        pkcs12KeyStoreManager.setKeyStoreFile(str);
        return addFileKeyStoreManager(pkcs12KeyStoreManager, new String[]{"pfx", "p12"}, KeyStoreMessages.getString("AOKeyStoreManagerFactory.0"), str, passwordCallback, z, obj);
    }

    private static AOKeyStoreManager getJavaKeyStoreManager(String str, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        JavaKeyStoreManager javaKeyStoreManager = new JavaKeyStoreManager();
        javaKeyStoreManager.setKeyStoreFile(str);
        return addFileKeyStoreManager(javaKeyStoreManager, new String[]{"jks"}, KeyStoreMessages.getString("AOKeyStoreManagerFactory.1"), str, passwordCallback, z, obj);
    }

    private static AOKeyStoreManager getSmartCafeJavaKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(AOKeyStore.SMARTCAFE, null, passwordCallback, new Object[]{obj}, z);
            aOKeyStoreManager.setPreferred(true);
            return aOKeyStoreManager;
        } catch (AOKeyStoreManagerException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.PKCS12), "Error al inicializar el modulo G&D SmartCafe 100% Java: " + e, e);
        }
    }

    private static AOKeyStoreManager getCeresJavaKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(AOKeyStore.CERES, null, passwordCallback, new Object[]{obj}, z);
            aOKeyStoreManager.setPreferred(true);
            return aOKeyStoreManager;
        } catch (AOKeyStoreManagerException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.PKCS12), "Error al inicializar el modulo CERES 100% Java: " + e, e);
        }
    }

    private static AOKeyStoreManager getCeres430JavaKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(AOKeyStore.CERES_430, null, passwordCallback, new Object[]{obj}, z);
            aOKeyStoreManager.setPreferred(true);
            return aOKeyStoreManager;
        } catch (AOKeyStoreManagerException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.PKCS12), "Error al inicializar el modulo CERES 430 100% Java: " + e, e);
        }
    }

    private static AOKeyStoreManager getDnieJavaKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(AOKeyStore.DNIEJAVA, null, passwordCallback, new Object[]{obj}, z);
            aOKeyStoreManager.setPreferred(true);
            return aOKeyStoreManager;
        } catch (AOKeyStoreManagerException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.PKCS12), "Error al inicializar el modulo DNIe 100% Java: " + e, e);
        }
    }

    private static AOKeyStoreManager getFileKeyStoreManager(AOKeyStore aOKeyStore, String str, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        String absolutePath;
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        if (str != null && !str.isEmpty() && new File(str).exists()) {
            absolutePath = str;
        } else {
            String string = null;
            String[] strArr = null;
            if (aOKeyStore == AOKeyStore.SINGLE) {
                strArr = new String[]{"cer", "p7b"};
                string = KeyStoreMessages.getString("AOKeyStoreManagerFactory.2");
            } else if (aOKeyStore == AOKeyStore.JCEKS || aOKeyStore == AOKeyStore.JAVACE) {
                strArr = new String[]{"jceks", "jks", "jce"};
                string = KeyStoreMessages.getString("AOKeyStoreManagerFactory.3");
            }
            absolutePath = AOUIFactory.getLoadFiles(KeyStoreMessages.getString("AOKeyStoreManagerFactory.4") + " " + aOKeyStore.getName(), null, null, strArr, string, false, false, null, obj)[0].getAbsolutePath();
            if (absolutePath == null) {
                throw new AOCancelledOperationException("No se ha seleccionado el almacen de certificados");
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            Throwable th = null;
            try {
                try {
                    aOKeyStoreManager.init(aOKeyStore, fileInputStream, passwordCallback, null, z);
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                    return aOKeyStoreManager;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileInputStream != null) {
                    if (th != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (AOException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(aOKeyStore), "No se ha podido abrir el almacen de tipo " + aOKeyStore.getName(), e);
        }
    }

    private static AOKeyStoreManager getPkcs11KeyStoreManager(String str, String str2, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        String[] strArr;
        String str3;
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        String absolutePath = null;
        if (str != null && !str.isEmpty()) {
            absolutePath = str;
        }
        if (absolutePath != null && !new File(absolutePath).isFile()) {
            throw new IOException("La biblioteca '" + absolutePath + "' no existe");
        }
        if (absolutePath == null) {
            String string = KeyStoreMessages.getString("AOKeyStoreManagerFactory.6");
            if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
                strArr = new String[]{"dll"};
                str3 = string + " (*.dll)";
            } else if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                strArr = new String[]{"so", "dylib"};
                str3 = string + " (*.dylib, *.so)";
            } else {
                strArr = new String[]{"so"};
                str3 = string + " (*.so)";
            }
            absolutePath = AOUIFactory.getLoadFiles(KeyStoreMessages.getString("AOKeyStoreManagerFactory.7"), null, null, strArr, str3, false, false, null, obj)[0].getAbsolutePath();
        }
        if (absolutePath == null) {
            throw new AOCancelledOperationException("No se ha seleccionado el controlador PKCS#11");
        }
        try {
            aOKeyStoreManager.init(AOKeyStore.PKCS11, null, passwordCallback, new String[]{absolutePath, str2}, z);
            return aOKeyStoreManager;
        } catch (AOException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.PKCS11), "Error al inicializar el modulo PKCS#11: " + e, e);
        }
    }

    private static AOKeyStoreManager getWindowsAddressBookKeyStoreManager(AOKeyStore aOKeyStore, boolean z) throws AOKeystoreAlternativeException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(aOKeyStore, null, NullPasswordCallback.getInstance(), null, z);
            return aOKeyStoreManager;
        } catch (AOException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(aOKeyStore), "Error al inicializar el almacen " + aOKeyStore.getName(), e);
        }
    }

    private static AOKeyStoreManager getWindowsMyCapiKeyStoreManager(boolean z) throws AOKeystoreAlternativeException, IOException {
        CAPIUnifiedKeyStoreManager cAPIUnifiedKeyStoreManager = new CAPIUnifiedKeyStoreManager();
        try {
            cAPIUnifiedKeyStoreManager.init(AOKeyStore.WINDOWS, null, null, null, z);
            return cAPIUnifiedKeyStoreManager;
        } catch (AOKeyStoreManagerException e) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.WINDOWS), "Error al obtener almacen WINDOWS: " + e, e);
        }
    }

    private static AggregatedKeyStoreManager getNssKeyStoreManager(String str, PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        try {
            AggregatedKeyStoreManager aggregatedKeyStoreManager = (AggregatedKeyStoreManager) Class.forName(str).getConstructor(new Class[0]).newInstance(new Object[0]);
            try {
                aggregatedKeyStoreManager.init(AOKeyStore.SHARED_NSS, null, passwordCallback, new Object[]{obj}, z);
                return aggregatedKeyStoreManager;
            } catch (AOException e) {
                throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.MOZ_UNI), "Error al inicializar el almacen NSS: " + e, e);
            }
        } catch (Exception e2) {
            throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(AOKeyStore.MOZ_UNI), "Error al obtener dinamicamente el almacen NSS: " + e2, e2);
        }
    }

    private static AggregatedKeyStoreManager getSharedNssKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        return getNssKeyStoreManager("es.gob.afirma.keystores.mozilla.shared.SharedNssKeyStoreManager", passwordCallback, z, obj);
    }

    private static AggregatedKeyStoreManager getMozillaUnifiedKeyStoreManager(PasswordCallback passwordCallback, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        AggregatedKeyStoreManager nssKeyStoreManager = mozillaKeyStoreManager;
        if (nssKeyStoreManager == null) {
            nssKeyStoreManager = getNssKeyStoreManager("es.gob.afirma.keystores.mozilla.MozillaUnifiedKeyStoreManager", passwordCallback, z, obj);
            if (!containsDnieJavaKeyStoreManager(nssKeyStoreManager)) {
                mozillaKeyStoreManager = nssKeyStoreManager;
            }
        }
        return nssKeyStoreManager;
    }

    private static boolean containsDnieJavaKeyStoreManager(AggregatedKeyStoreManager aggregatedKeyStoreManager) {
        for (AOKeyStoreManager aOKeyStoreManager : aggregatedKeyStoreManager.getKeyStoreManagers()) {
            if (aOKeyStoreManager.getType() != AOKeyStore.DNIEJAVA) {
                if ((aOKeyStoreManager instanceof AggregatedKeyStoreManager) && containsDnieJavaKeyStoreManager((AggregatedKeyStoreManager) aOKeyStoreManager)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    private static AggregatedKeyStoreManager getMacOSXKeyStoreManager(AOKeyStore aOKeyStore, String str, boolean z, Object obj) throws AOKeystoreAlternativeException, IOException {
        FileInputStream fileInputStream;
        AppleKeyStoreManager appleKeyStoreManager = new AppleKeyStoreManager();
        if (str != null) {
            try {
                if (str.isEmpty()) {
                    fileInputStream = null;
                } else {
                    fileInputStream = new FileInputStream(str);
                }
            } catch (AOException e) {
                throw new AOKeystoreAlternativeException(getAlternateKeyStoreType(aOKeyStore), "Error al inicializar el Llavero de Mac OS X", e);
            }
        } else {
            fileInputStream = null;
        }
        FileInputStream fileInputStream2 = fileInputStream;
        Throwable th = null;
        try {
            appleKeyStoreManager.init(aOKeyStore, fileInputStream2, NullPasswordCallback.getInstance(), null, z);
            if (fileInputStream2 != null) {
                if (0 != 0) {
                    try {
                        fileInputStream2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    fileInputStream2.close();
                }
            }
            AggregatedKeyStoreManager aggregatedKeyStoreManager = new AggregatedKeyStoreManager(appleKeyStoreManager);
            try {
                KeyStoreUtilities.addPreferredKeyStoreManagers(aggregatedKeyStoreManager, obj);
            } catch (AOCancelledOperationException e2) {
                LOGGER.info("Se cancelo el uso del driver Java: " + e2);
            }
            return aggregatedKeyStoreManager;
        } catch (Throwable th3) {
            if (fileInputStream2 != null) {
                if (0 != 0) {
                    try {
                        fileInputStream2.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileInputStream2.close();
                }
            }
            throw th3;
        }
    }

    private static AOKeyStore getAlternateKeyStoreType(AOKeyStore aOKeyStore) {
        if (AOKeyStore.PKCS12.equals(aOKeyStore)) {
            return null;
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS()) && !AOKeyStore.WINDOWS.equals(aOKeyStore)) {
            return AOKeyStore.WINDOWS;
        }
        if (Platform.OS.MACOSX.equals(Platform.getOS()) && !AOKeyStore.APPLE.equals(aOKeyStore)) {
            return AOKeyStore.APPLE;
        }
        return AOKeyStore.PKCS12;
    }
}
