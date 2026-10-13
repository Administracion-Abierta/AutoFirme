package es.gob.afirma.keystores.mozilla;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Level;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/NssKeyStoreManager.class */
public final class NssKeyStoreManager extends AOKeyStoreManager {
    private static Provider nssProvider = null;
    private final boolean useSharedNss;
    private final Object parentComponent;

    public NssKeyStoreManager(Object obj, boolean z) {
        setKeyStoreType(AOKeyStore.MOZ_UNI);
        this.parentComponent = obj;
        this.useSharedNss = z;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) {
        char[] password;
        Provider nssProvider2 = getNssProvider(this.useSharedNss, z);
        KeyStore keyStore = null;
        if (nssProvider2 != null) {
            try {
                keyStore = KeyStore.getInstance("PKCS11", nssProvider2);
            } catch (Exception e) {
                LOGGER.warning("No se ha podido obtener el KeyStore PKCS#11 NSS del proveedor SunPKCS11: " + e);
                keyStore = null;
            }
        }
        if (keyStore != null) {
            try {
                keyStore.load(null, new char[0]);
            } catch (Exception e2) {
                LOGGER.info("No se ha podido abrir el almacen sin contrasena, se intentara proporcionando una : " + e2);
                KeyStore keyStore2 = keyStore;
                try {
                    if (passwordCallback != null) {
                        password = passwordCallback.getPassword();
                    } else {
                        password = new UIPasswordCallback(FirefoxKeyStoreMessages.getString("MozillaUnifiedKeyStoreManager.0"), this.parentComponent).getPassword();
                    }
                    keyStore2.load(null, password);
                } catch (AOCancelledOperationException e3) {
                    throw e3;
                } catch (Exception e4) {
                    LOGGER.warning("No se ha podido abrir el almacen PKCS#11 NSS del proveedor SunPKCS11: " + e4);
                    keyStore = null;
                }
            }
        }
        if (keyStore != null) {
            setKeyStore(keyStore);
        }
    }

    private static Provider getNssProvider(boolean z, boolean z2) {
        if (nssProvider != null) {
            if (z2) {
                Security.removeProvider(nssProvider.getName());
            } else {
                return (Provider) nssProvider.clone();
            }
        }
        try {
            nssProvider = MozillaKeyStoreUtilities.loadNSS(z);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo el proveedor NSS: " + e, (Throwable) e);
            nssProvider = null;
        }
        if (nssProvider != null) {
            return (Provider) nssProvider.clone();
        }
        return null;
    }
}
