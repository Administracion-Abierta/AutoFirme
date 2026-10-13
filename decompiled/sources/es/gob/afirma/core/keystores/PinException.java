package es.gob.afirma.core.keystores;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/keystores/PinException.class */
public class PinException extends AuthenticationException {
    private static final long serialVersionUID = 2287275718431510008L;

    public PinException(String str) {
        super(str);
    }

    public PinException(Throwable th) {
        super(th);
    }

    public PinException(String str, Throwable th) {
        super(str, th);
    }
}
