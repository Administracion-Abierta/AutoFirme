package es.gob.afirma.core.keystores;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/keystores/KeyStoreManager.class */
public interface KeyStoreManager {
    void deactivateEntry(String str);

    boolean isKeyEntry(String str) throws KeyStoreException;

    String[] getAliases();

    X509Certificate getCertificate(String str);

    X509Certificate[] getCertificateChain(String str);

    KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException;

    void refresh() throws IOException;

    void setEntryPasswordCallBack(PasswordCallback passwordCallback);

    void setParentComponent(Object obj);
}
