package es.gob.afirma.keystores;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/JavaKeyStoreManager.class */
public final class JavaKeyStoreManager extends FileKeyStoreManager {
    @Override // es.gob.afirma.keystores.FileKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public /* bridge */ /* synthetic */ void refresh() throws IOException {
        super.refresh();
    }

    @Override // es.gob.afirma.keystores.FileKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public /* bridge */ /* synthetic */ KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        return super.getKeyEntry(str);
    }

    JavaKeyStoreManager() {
        setKeyStoreType(AOKeyStore.JAVA);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) throws AOKeyStoreManagerException, IOException {
        setKeyStore(init(inputStream, passwordCallback));
    }
}
