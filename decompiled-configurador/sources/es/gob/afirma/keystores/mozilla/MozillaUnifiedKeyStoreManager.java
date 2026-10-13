package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerException;
import es.gob.afirma.keystores.AggregatedKeyStoreManager;
import es.gob.afirma.keystores.KeyStoreUtilities;
import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/MozillaUnifiedKeyStoreManager.class */
public class MozillaUnifiedKeyStoreManager extends AggregatedKeyStoreManager {
    private static final String ONLY_PKCS11 = "es.gob.afirma.keystores.mozilla.LoadSscdOnly";
    private static final String ONLY_PKCS11_ENV = "AFIRMA_NSS_LOAD_SSCD_ONLY";
    public static final String INCLUDE_NATIVE_DNIE_P11 = "es.gob.afirma.keystores.mozilla.IncludeNativeDniePkcs11";
    protected static final String INCLUDE_NATIVE_DNIE_P11_ENV = "AFIRMA_NSS_INCLUDE_NATIVE_DNIE_PKCS11";
    private PasswordCallback passwordCallback = null;
    private Object[] configParams = null;
    private boolean initialized = false;
    private boolean preferredKsAdded = false;

    public MozillaUnifiedKeyStoreManager() {
        setKeyStoreType(AOKeyStore.MOZ_UNI);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) {
        LOGGER.info("Inicializamos el almacen de tipo: " + aOKeyStore);
        this.passwordCallback = passwordCallback;
        this.configParams = objArr != null ? (Object[]) objArr.clone() : null;
        removeAll();
        Object obj = (this.configParams == null || this.configParams.length <= 0) ? null : this.configParams[0];
        if (!Boolean.getBoolean(ONLY_PKCS11) && !Boolean.parseBoolean(System.getenv(ONLY_PKCS11_ENV))) {
            AOKeyStoreManager nssKeyStoreManager = getNssKeyStoreManager();
            try {
                nssKeyStoreManager.init(aOKeyStore, inputStream, passwordCallback, this.configParams, z);
            } catch (Exception e) {
                LOGGER.severe("No se ha podido cargar NSS, se continuara con los almacenes externos: " + e);
            }
            setKeyStore(nssKeyStoreManager.getKeyStore());
            addKeyStoreManager(nssKeyStoreManager);
        }
        boolean z2 = false;
        if (z || !this.initialized) {
            try {
                this.preferredKsAdded = KeyStoreUtilities.addPreferredKeyStoreManagers(this, obj);
            } catch (AOCancelledOperationException e2) {
                LOGGER.info("Se cancelo el uso del driver Java: " + e2);
                this.preferredKsAdded = false;
                z2 = true;
            }
        }
        if (!this.preferredKsAdded) {
            Map<String, String> externalStores = getExternalStores(z2);
            if (externalStores.size() > 0) {
                StringBuilder sb = new StringBuilder("Encontrados los siguientes modulos PKCS#11 externos instalados en Mozilla / Firefox: ");
                for (String str : externalStores.keySet()) {
                    sb.append("'");
                    sb.append(externalStores.get(str));
                    sb.append("' ");
                }
                LOGGER.info(sb.toString());
            } else {
                LOGGER.info("No se han encontrado modulos PKCS#11 externos instalados en Firefox");
            }
            for (String str2 : externalStores.keySet()) {
                AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
                try {
                    internalInitStore(aOKeyStoreManager, str2, obj, z, externalStores.get(str2));
                } catch (AOCancelledOperationException e3) {
                    LOGGER.warning("Se cancelo el acceso al almacen externo  '" + str2 + "', se continuara con el siguiente: " + e3);
                } catch (Exception e4) {
                    if (Platform.OS.LINUX.equals(Platform.getOS())) {
                        try {
                            internalInitStore(aOKeyStoreManager, str2, obj, z, externalStores.get(str2));
                        } catch (AOCancelledOperationException e5) {
                            LOGGER.warning("Se cancelo el acceso al almacen externo  '" + str2 + "', se continuara con el siguiente: " + e5);
                        } catch (Exception e6) {
                            LOGGER.warning("No se ha podido inicializar el PKCS#11 '" + str2 + "' tras haberlo intentado dos veces: " + e4 + ", " + e6);
                        }
                    } else {
                        LOGGER.warning("No se ha podido inicializar el PKCS#11 '" + str2 + "': " + e4);
                    }
                }
                addKeyStoreManager(aOKeyStoreManager);
                LOGGER.info("El almacen externo '" + str2 + "' ha podido inicializarse, se anadiran sus entradas y se detiene la carga del resto de almacenes");
            }
        }
        if (lacksKeyStores()) {
            LOGGER.warning("No se ha podido inicializar ningun almacen, interno o externo, de Mozilla, ni los almacenes preferentes");
        }
        setKeyStoreType(aOKeyStore);
        this.initialized = true;
    }

    private static void internalInitStore(AOKeyStoreManager aOKeyStoreManager, String str, Object obj, boolean z, String str2) throws AOKeyStoreManagerException, IOException {
        aOKeyStoreManager.init(AOKeyStore.PKCS11, null, new UIPasswordCallback(FirefoxKeyStoreMessages.getString("MozillaUnifiedKeyStoreManager.1") + " " + str, obj), new String[]{str2, str.toString()}, z);
    }

    @Override // es.gob.afirma.keystores.AggregatedKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        init(AOKeyStore.MOZ_UNI, null, this.passwordCallback, this.configParams, true);
    }

    protected Map<String, String> getExternalStores() {
        return MozillaKeyStoreUtilities.getMozillaPKCS11Modules(!Platform.OS.LINUX.equals(Platform.getOS()) || Boolean.getBoolean(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER) || Boolean.parseBoolean(System.getenv(KeyStoreUtilities.DISABLE_DNIE_NATIVE_DRIVER_ENV)), true);
    }

    protected Map<String, String> getExternalStores(boolean z) {
        return MozillaKeyStoreUtilities.getMozillaPKCS11Modules(z, true);
    }

    protected AOKeyStoreManager getNssKeyStoreManager() {
        return new NssKeyStoreManager(getParentComponent(), false);
    }
}
