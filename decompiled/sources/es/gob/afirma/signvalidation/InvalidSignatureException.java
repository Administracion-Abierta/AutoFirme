package es.gob.afirma.signvalidation;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/InvalidSignatureException.class */
public class InvalidSignatureException extends AOException {
    private static final long serialVersionUID = -6420658430794700293L;

    public InvalidSignatureException(String str) {
        super(str);
    }

    public InvalidSignatureException(Throwable th) {
        super(th);
    }

    public InvalidSignatureException(String str, Throwable th) {
        super(str, th);
    }
}
