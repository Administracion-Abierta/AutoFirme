package es.gob.afirma.core.keystores;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/keystores/LockedKeyStoreException.class */
public class LockedKeyStoreException extends AuthenticationException {
    private static final long serialVersionUID = -1662659418498317574L;

    public LockedKeyStoreException(String str) {
        super(str);
    }

    public LockedKeyStoreException(Throwable th) {
        super(th);
    }

    public LockedKeyStoreException(String str, Throwable th) {
        super(str, th);
    }
}
