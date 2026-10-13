package es.gob.afirma.core.keystores;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/keystores/AuthenticationException.class */
public class AuthenticationException extends AOException {
    private static final long serialVersionUID = 7055233482951963862L;

    public AuthenticationException(String str) {
        super(str);
    }

    public AuthenticationException(Throwable th) {
        super(th);
    }

    public AuthenticationException(String str, Throwable th) {
        super(str, th);
    }
}
