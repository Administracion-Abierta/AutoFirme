package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import java.io.IOException;
import java.io.InputStream;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/SmartCardUnifiedKeyStoreManager.class */
public class SmartCardUnifiedKeyStoreManager extends AggregatedKeyStoreManager {
    private PasswordCallback passwordCallback = null;
    private Object[] configParams = null;
    private boolean preferredKsAdded = false;
    private boolean initialized = false;

    public SmartCardUnifiedKeyStoreManager() {
        setKeyStoreType(AOKeyStore.KNOWN_SMARTCARDS);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public final void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) {
        this.passwordCallback = passwordCallback;
        this.configParams = objArr != null ? (Object[]) objArr.clone() : null;
        Object obj = (objArr == null || objArr.length <= 0) ? null : objArr[0];
        boolean z2 = false;
        removeAll();
        if (z || !this.initialized) {
            try {
                this.preferredKsAdded = KeyStoreUtilities.addPreferredKeyStoreManagers(this, obj);
            } catch (AOCancelledOperationException e) {
                LOGGER.info("Se cancelo el uso del driver Java: " + e);
                this.preferredKsAdded = false;
                z2 = true;
            }
        }
        if (!this.preferredKsAdded) {
            boolean z3 = false;
            for (KnownSmartCardsPkcs11 knownSmartCardsPkcs11 : KnownSmartCardsPkcs11.values()) {
                LOGGER.warning("Intentamos cargar: " + knownSmartCardsPkcs11.getDescription());
                if (!z2 || !knownSmartCardsPkcs11.isPreferred()) {
                    for (String str : knownSmartCardsPkcs11.getPkcs11Names()) {
                        LOGGER.warning("Probamos con la biblioteca: " + str);
                        try {
                            z3 = true;
                            addKeyStoreManager(initPkcs11(knownSmartCardsPkcs11.getDescription(), obj, z, str));
                            LOGGER.info("El almacen externo '" + knownSmartCardsPkcs11.getDescription() + "' ha podido inicializarse, se anadiran sus entradas");
                            break;
                        } catch (AOCancelledOperationException e2) {
                            LOGGER.warning("Se cancelo el acceso al almacen externo  '" + knownSmartCardsPkcs11.getDescription() + "', se continuara con el siguiente: " + e2);
                        } catch (Exception e3) {
                            LOGGER.warning("Fallo el acceso al almacen externo  '" + knownSmartCardsPkcs11.getDescription() + "', se reintentara: " + e3);
                            if (Platform.OS.LINUX.equals(Platform.getOS())) {
                                try {
                                    initPkcs11(knownSmartCardsPkcs11.getDescription(), obj, z, str);
                                } catch (AOCancelledOperationException e4) {
                                    LOGGER.warning("Se cancelo el acceso al almacen externo  '" + knownSmartCardsPkcs11.getDescription() + "', se continuara con el siguiente: " + e4);
                                } catch (Exception e5) {
                                    LOGGER.warning("Fallo el acceso al almacen externo  '" + knownSmartCardsPkcs11.getDescription() + "', se continuara con el siguiente: " + e5);
                                }
                            }
                        }
                    }
                    if (z3) {
                        break;
                    }
                }
            }
        }
        this.initialized = true;
    }

    private static AOKeyStoreManager initPkcs11(String str, Object obj, boolean z, String str2) throws AOKeyStoreManagerException, IOException {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        aOKeyStoreManager.init(AOKeyStore.PKCS11, null, new UIPasswordCallback(KeyStoreMessages.getString("SmartCardUnifiedKeyStoreManager.0", str.toString()), obj), new String[]{str2, str.toString()}, z);
        return aOKeyStoreManager;
    }

    @Override // es.gob.afirma.keystores.AggregatedKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        init(AOKeyStore.KNOWN_SMARTCARDS, null, this.passwordCallback, this.configParams, true);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/SmartCardUnifiedKeyStoreManager$KnownSmartCardsPkcs11.class */
    private enum KnownSmartCardsPkcs11 {
        CERES("FNMT-CERES", new String[]{"%SYSTEMROOT%/System32/FNMT_P11_x64.dll", "%SYSTEMROOT%/System32/FNMT_P11.dll", "%SYSTEMROOT%/SysWOW64/FNMT_P11.dll"}, true),
        CARDOS("CardOS", new String[]{"%SYSTEMROOT%/SysWOW64/siecap11.dll", "%SYSTEMROOT%/System32/siecap11.dll"}, false),
        TUI("TUI", new String[]{"%PROGRAMFILES%/umu/Third-Party/Gemalto/Classic Client/BIN/gclib.dll", "%PROGRAMFILES(x86)%/umu/Third-Party/Gemalto/Classic Client/BIN/gclib.dll", "%PROGRAMFILES%/umu/UMU-Crypto/lib/libumupkcs11.dll", "%PROGRAMFILES(x86)%/umu/UMU-Crypto/lib/libumupkcs11.dll", "%PROGRAMFILES%/Gemalto/IDGo 800 PKCS#11/IDPrimePKCS1164.dll", "%PROGRAMFILES%/Gemalto/IDGo 800 PKCS#11/IDPrimePKCS11.dll", "%PROGRAMFILES(x86)%/Gemalto/IDGo 800 PKCS#11/IDPrimePKCS1164.dll", "%PROGRAMFILES(x86)%/Gemalto/IDGo 800 PKCS#11/IDPrimePKCS11.dll"}, false),
        SAFESIGN("SafeSign", new String[]{"%SYSTEMROOT%/SysWOW64/aetpkss1.dll", "%SYSTEMROOT%/System32/aetpkss1.dll"}, false);

        private static final String[] ENV_PROPERTIES = {"%PROGRAMFILES%", "%PROGRAMFILES(x86)%", "%SYSTEMROOT%"};
        private String description;
        private String[] pkcs11Names;
        private boolean preferredKs;

        KnownSmartCardsPkcs11(String str, String[] strArr, boolean z) {
            this.description = str;
            this.pkcs11Names = strArr != null ? (String[]) strArr.clone() : null;
            this.preferredKs = z;
        }

        public String getDescription() {
            return this.description;
        }

        public String[] getPkcs11Names() {
            String[] strArr = new String[this.pkcs11Names.length];
            for (int i = 0; i < this.pkcs11Names.length; i++) {
                strArr[i] = replaceEnvProperties(this.pkcs11Names[i]);
            }
            return strArr;
        }

        private static String replaceEnvProperties(String str) {
            String strReplace = str;
            for (String str2 : ENV_PROPERTIES) {
                if (strReplace.contains(str2)) {
                    strReplace = strReplace.replace(str2, System.getenv(str2.substring(1, str2.length() - 1)));
                }
            }
            return strReplace;
        }

        public boolean isPreferred() {
            return this.preferredKs;
        }
    }
}
