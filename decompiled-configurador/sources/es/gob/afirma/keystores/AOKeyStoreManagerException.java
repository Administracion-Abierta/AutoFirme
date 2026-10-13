package es.gob.afirma.keystores;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStoreManagerException.class */
public final class AOKeyStoreManagerException extends AOException {
    private static final long serialVersionUID = 2896862509190263027L;

    public AOKeyStoreManagerException(String str) {
        super(str);
    }

    public AOKeyStoreManagerException(String str, Exception exc) {
        super(str, exc);
    }

    public AOKeyStoreManagerException(Exception exc) {
        super(exc);
    }
}
