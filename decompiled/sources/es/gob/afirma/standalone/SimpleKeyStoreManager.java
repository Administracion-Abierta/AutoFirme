package es.gob.afirma.standalone;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerException;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AOKeystoreAlternativeException;
import es.gob.afirma.keystores.SmartCardLockedException;
import es.gob.afirma.keystores.mozilla.MozillaKeyStoreUtilities;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Component;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/SimpleKeyStoreManager.class */
public final class SimpleKeyStoreManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private SimpleKeyStoreManager() {
    }

    /* JADX WARN: Code duplicated, block: B:42:0x014e  */
    static AOKeyStoreManager getKeyStore(boolean z, boolean z2, Component component) throws NoDnieFoundException, AOKeyStoreManagerException {
        AOKeyStore defaultKeyStoreType;
        Throwable cause;
        Throwable cause2;
        if (z) {
            JMulticardUtilities.configureJMulticard(true);
            LOGGER.info("Cargando certificados del DNIe");
            try {
                return getKeyStoreManager(AOKeyStore.DNIEJAVA, component);
            } catch (SmartCardLockedException e) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.9"), SimpleAfirmaMessages.getString("SimpleKeyStoreManager.10"), 2, e);
                if (z2) {
                    throw new NoDnieFoundException();
                }
                JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
                defaultKeyStoreType = getDefaultKeyStoreType();
                LOGGER.info("Cargando almacen por defecto: " + defaultKeyStoreType.getName());
                return getKeyStoreManager(defaultKeyStoreType, component);
            } catch (Exception e2) {
                String name = e2.getClass().getName();
                Throwable cause3 = e2.getCause();
                if (cause3 != null && (cause = cause3.getCause()) != null && (cause2 = cause.getCause()) != null) {
                    name = cause2.getClass().getName();
                }
                switch (name) {
                    case "es.gob.jmulticard.apdu.connection.CardNotPresentException":
                        if (0 == AOUIFactory.showConfirmDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.1"), SimpleAfirmaMessages.getString("SimpleKeyStoreManager.2"), 0, 2)) {
                            return getKeyStore(true, z2, component);
                        }
                        break;
                    case "es.gob.jmulticard.card.InvalidCardException":
                        if (0 == AOUIFactory.showConfirmDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.3"), SimpleAfirmaMessages.getString("SimpleKeyStoreManager.4"), 0, 2)) {
                            return getKeyStore(true, z2, component);
                        }
                        break;
                    case "es.gob.jmulticard.card.dnie.BurnedDnieCardException":
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.5"), SimpleAfirmaMessages.getString("SimpleKeyStoreManager.6"), 2, e2);
                        break;
                    default:
                        Logger.getLogger("es.gob.afirma").severe("No se ha podido inicializar el controlador 100% Java del DNIe: " + e2);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.7"), SimpleAfirmaMessages.getString("SimpleKeyStoreManager.8"), 0, e2);
                        break;
                }
                if (z2) {
                    throw new NoDnieFoundException();
                }
                JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
                defaultKeyStoreType = getDefaultKeyStoreType();
                LOGGER.info("Cargando almacen por defecto: " + defaultKeyStoreType.getName());
                return getKeyStoreManager(defaultKeyStoreType, component);
            }
        }
        JMulticardUtilities.configureJMulticard(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
        defaultKeyStoreType = getDefaultKeyStoreType();
        LOGGER.info("Cargando almacen por defecto: " + defaultKeyStoreType.getName());
        try {
            return getKeyStoreManager(defaultKeyStoreType, component);
        } catch (AOCancelledOperationException e3) {
            AOUIFactory.showMessageDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.13"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2);
            return loadSystemAOKSManager(component);
        } catch (IOException e4) {
            if (e4.getCause() != null && e4.getCause().getCause() != null && (e4.getCause().getCause() instanceof UnrecoverableKeyException)) {
                AOUIFactory.showMessageDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.12"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0);
                while (true) {
                    try {
                        return getKeyStoreManager(defaultKeyStoreType, component);
                    } catch (AOCancelledOperationException e5) {
                        AOUIFactory.showMessageDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.13"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 2);
                        return loadSystemAOKSManager(component);
                    } catch (IOException e6) {
                        if (e4.getCause() != null && e4.getCause().getCause() != null && (e4.getCause().getCause() instanceof UnrecoverableKeyException)) {
                            AOUIFactory.showMessageDialog(component, SimpleAfirmaMessages.getString("SimpleKeyStoreManager.12"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0);
                        }
                    } catch (Exception e7) {
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.11", defaultKeyStoreType.getName()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e7);
                        return loadSystemAOKSManager(component);
                    }
                }
            } else {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.11", defaultKeyStoreType.getName()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e4);
                return loadSystemAOKSManager(component);
            }
        } catch (Exception e8) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleKeyStoreManager.11", defaultKeyStoreType.getName()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e8);
            return loadSystemAOKSManager(component);
        }
    }

    private static AOKeyStoreManager getKeyStoreManager(AOKeyStore aOKeyStore, Component component) throws AOKeystoreAlternativeException, IOException {
        String str = null;
        if (AOKeyStore.PKCS12.equals(aOKeyStore) || AOKeyStore.PKCS11.equals(aOKeyStore)) {
            str = PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH);
        }
        return AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, str, null, aOKeyStore.getStorePasswordCallback(component), component);
    }

    public static boolean isFirefoxAvailable() {
        try {
            return (MozillaKeyStoreUtilities.getMozillaUserProfileDirectory() == null || MozillaKeyStoreUtilities.getSystemNSSLibDir() == null) ? false : true;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido obtener el directorio de NSS del usuario: " + e);
            return false;
        }
    }

    public static AOKeyStore getKeyStore(String str) {
        if (str == null) {
            return null;
        }
        for (AOKeyStore aOKeyStore : AOKeyStore.values()) {
            if (aOKeyStore.getName().equalsIgnoreCase(str.trim())) {
                return aOKeyStore;
            }
        }
        if (KeyStorePreferencesManager.getUserSmartCardsRegistered().containsKey(str) || KeyStorePreferencesManager.getSystemSmartCardsRegistered().containsKey(str)) {
            AOKeyStore aOKeyStore2 = AOKeyStore.PKCS11;
            aOKeyStore2.setName(str);
            return aOKeyStore2;
        }
        try {
            return AOKeyStore.valueOf(str);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("Almacen de claves no reconocido (" + str + "): " + e);
            return null;
        }
    }

    public static AOKeyStore getDefaultKeyStoreType() {
        AOKeyStore validKeyStoreType = null;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
        if (str != null && !PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(str)) {
            validKeyStoreType = getValidKeyStoreType(str);
        }
        return validKeyStoreType != null ? validKeyStoreType : AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS());
    }

    private static AOKeyStore getValidKeyStoreType(String str) {
        AOKeyStore keyStore = null;
        if (str != null && !str.isEmpty()) {
            keyStore = getKeyStore(str);
            if (keyStore != null) {
                Platform.OS os = Platform.getOS();
                if (AOKeyStore.MOZ_UNI.equals(keyStore)) {
                    if (isFirefoxAvailable()) {
                        return keyStore;
                    }
                    return AOKeyStore.getDefaultKeyStoreTypeByOs(os);
                }
                if (AOKeyStore.WINDOWS.equals(keyStore)) {
                    if (Platform.OS.WINDOWS.equals(os)) {
                        return keyStore;
                    }
                    return AOKeyStore.getDefaultKeyStoreTypeByOs(os);
                }
                if (AOKeyStore.APPLE.equals(keyStore)) {
                    if (Platform.OS.MACOSX.equals(os)) {
                        return keyStore;
                    }
                    return AOKeyStore.getDefaultKeyStoreTypeByOs(os);
                }
                return keyStore;
            }
        }
        return keyStore;
    }

    public static AOKeyStore getLastSelectedKeystore() {
        return getValidKeyStoreType(KeyStorePreferencesManager.getLastSelectedKeystore());
    }

    private static AOKeyStoreManager loadSystemAOKSManager(Component component) throws AOKeyStoreManagerException {
        try {
            return getKeyStoreManager(AOKeyStore.getDefaultKeyStoreTypeByOs(Platform.getOS()), component);
        } catch (Exception e) {
            throw new AOKeyStoreManagerException("No se ha podido incializar el almacen del sistema", e);
        }
    }
}
