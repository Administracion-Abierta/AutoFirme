package es.gob.afirma.keystores;

import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/Pkcs12KeyStoreManager.class */
public final class Pkcs12KeyStoreManager extends FileKeyStoreManager {
    private PasswordCallback passwordCallBack = null;

    @Override // es.gob.afirma.keystores.FileKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public /* bridge */ /* synthetic */ void refresh() throws IOException {
        super.refresh();
    }

    @Override // es.gob.afirma.keystores.FileKeyStoreManager, es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public /* bridge */ /* synthetic */ KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        return super.getKeyEntry(str);
    }

    Pkcs12KeyStoreManager() {
        setKeyStoreType(AOKeyStore.PKCS12);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) throws AOKeyStoreManagerException, IOException {
        String string;
        this.passwordCallBack = passwordCallback;
        if (this.passwordCallBack instanceof UIPasswordCallback) {
            String keyStoreFile = getKeyStoreFile();
            if (keyStoreFile != null) {
                string = KeyStoreMessages.getString("AOKeyStore.15", new File(keyStoreFile).getName());
            } else {
                string = KeyStoreMessages.getString("AOKeyStore.1");
            }
            ((UIPasswordCallback) this.passwordCallBack).setPrompt(string);
        }
        setKeyStore(init(inputStream, this.passwordCallBack));
    }
}
