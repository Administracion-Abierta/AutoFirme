package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/CAPIUnifiedKeyStoreManager.class */
public class CAPIUnifiedKeyStoreManager extends AggregatedKeyStoreManager {
    private static final String SYSTEM_PROPERTY_USERNAME = "user.name";
    private static final String ENVIRONMENT_PROPERTY_USERPROFILE = "USERPROFILE";
    private static final String TEMPORARY_PROFILE_NAME = "TEMP";
    private PasswordCallback passwordCallback = null;
    private Object[] configParams = null;
    private boolean initialized = false;

    public CAPIUnifiedKeyStoreManager() {
        setKeyStoreType(AOKeyStore.WINDOWS);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public final void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) {
        this.passwordCallback = passwordCallback;
        this.configParams = objArr != null ? (Object[]) objArr.clone() : null;
        removeAll();
        Object obj = (objArr == null || objArr.length <= 0) ? null : objArr[0];
        CAPIKeyStoreManager cAPIKeyStoreManager = null;
        boolean zIsTemporaryProfile = isTemporaryProfile();
        if (zIsTemporaryProfile) {
            LOGGER.info("Detectado perfil temporal. Se omitira la carga del almacen de Windows y se buscaran tarjetas conocidas insertadas");
        } else {
            cAPIKeyStoreManager = new CAPIKeyStoreManager();
            try {
                cAPIKeyStoreManager.init(aOKeyStore, inputStream, passwordCallback, objArr, z);
            } catch (Exception e) {
                LOGGER.severe("No se ha podido cargar el almacen de Windows, se continuara con los almacenes preferentes: " + e);
            }
            addKeyStoreManager(cAPIKeyStoreManager);
        }
        boolean zAddPreferredKeyStoreManagers = false;
        if (z || !this.initialized) {
            try {
                zAddPreferredKeyStoreManagers = KeyStoreUtilities.addPreferredKeyStoreManagers(this, obj);
            } catch (AOCancelledOperationException e2) {
                LOGGER.info("Se cancelo el uso del driver Java: " + e2);
                zAddPreferredKeyStoreManagers = true;
            }
        }
        if (!zAddPreferredKeyStoreManagers && zIsTemporaryProfile) {
            SmartCardUnifiedKeyStoreManager smartCardUnifiedKeyStoreManager = new SmartCardUnifiedKeyStoreManager();
            try {
                smartCardUnifiedKeyStoreManager.init(aOKeyStore, inputStream, passwordCallback, objArr, z);
            } catch (Exception e3) {
                LOGGER.severe("No se ha podido cargar el almacen unificado de tarjetas: " + e3);
            }
            addKeyStoreManager(smartCardUnifiedKeyStoreManager);
        }
        if (cAPIKeyStoreManager != null && !zAddPreferredKeyStoreManagers) {
            try {
                cAPIKeyStoreManager.refresh();
            } catch (IOException e4) {
                LOGGER.warning("Error al refrescar el almacen de claves de Windows: " + e4);
            }
        }
        this.initialized = true;
    }

    @Override // es.gob.afirma.keystores.AggregatedKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        init(AOKeyStore.WINDOWS, null, this.passwordCallback, this.configParams, true);
    }

    private static boolean isTemporaryProfile() {
        String property = System.getProperty(SYSTEM_PROPERTY_USERNAME);
        String str = System.getenv(ENVIRONMENT_PROPERTY_USERPROFILE);
        if (property == null || str == null) {
            LOGGER.warning("No se ha podido identificar el nombre de usuario o su directorio de perfil. No se comprobara si se trata de un perfil temporal");
            return false;
        }
        String name = new File(str).getName();
        return !property.equals(name) && (TEMPORARY_PROFILE_NAME.equals(name) || name.startsWith("TEMP."));
    }
}
