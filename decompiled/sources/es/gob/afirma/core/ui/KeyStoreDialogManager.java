package es.gob.afirma.core.ui;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.keystores.NameCertificateBean;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ui/KeyStoreDialogManager.class */
public interface KeyStoreDialogManager {
    public static final int KEYSTORE_ID_SYSTEM = 1;
    public static final int KEYSTORE_ID_MOZILLA = 2;
    public static final int KEYSTORE_ID_PKCS12 = 3;
    public static final int KEYSTORE_ID_DNIE = 4;
    public static final int KEYSTORE_ID_PKCS11 = 5;

    void refresh() throws IOException;

    NameCertificateBean[] getNameCertificates();

    void setKeyStoreManager(KeyStoreManager keyStoreManager);

    boolean changeKeyStoreManager(int i, Object obj);

    boolean changeKeyStoreManagerToPKCS11(Object obj, String str, String str2);

    int[] getAvailablesKeyStores();

    Object getKeyEntry(String str) throws AOException;

    String show() throws AOException;

    String getSelectedAlias();

    CertificateContext getSelectedCertificateContext();

    void allowOpenExternalStores(boolean z);

    boolean isExternalStoresOpeningAllowed();

    String getKeyStoreName();

    String getLibName();
}
