package es.gob.afirma.keystores.mozilla.shared;

import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.mozilla.MozillaUnifiedKeyStoreManager;
import es.gob.afirma.keystores.mozilla.NssKeyStoreManager;
import java.io.InputStream;
import java.util.Map;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/shared/SharedNssKeyStoreManager.class */
public final class SharedNssKeyStoreManager extends MozillaUnifiedKeyStoreManager {
    public SharedNssKeyStoreManager() {
        setKeyStoreType(AOKeyStore.SHARED_NSS);
    }

    @Override // es.gob.afirma.keystores.mozilla.MozillaUnifiedKeyStoreManager
    protected Map<String, String> getExternalStores() {
        return SharedNssUtil.getSharedNssPKCS11Modules((Boolean.getBoolean(MozillaUnifiedKeyStoreManager.INCLUDE_NATIVE_DNIE_P11) || Boolean.parseBoolean(System.getenv("AFIRMA_NSS_INCLUDE_NATIVE_DNIE_PKCS11"))) ? false : true, true);
    }

    @Override // es.gob.afirma.keystores.mozilla.MozillaUnifiedKeyStoreManager
    protected AOKeyStoreManager getNssKeyStoreManager() {
        return new NssKeyStoreManager(getParentComponent(), true);
    }

    @Override // es.gob.afirma.keystores.mozilla.MozillaUnifiedKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) {
        super.init(AOKeyStore.SHARED_NSS, inputStream, passwordCallback, objArr, z);
    }
}
