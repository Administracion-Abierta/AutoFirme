package es.gob.afirma.core.keystores;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/keystores/CertificateContext.class */
public class CertificateContext {
    private final KeyStoreManager ksm;
    private final String alias;

    public CertificateContext(KeyStoreManager keyStoreManager, String str) {
        this.ksm = keyStoreManager;
        this.alias = str;
    }

    public KeyStoreManager getKeyStoreManager() {
        return this.ksm;
    }

    public String getAlias() {
        return this.alias;
    }
}
