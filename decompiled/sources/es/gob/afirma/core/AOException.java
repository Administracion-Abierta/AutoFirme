package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/AOException.class */
public class AOException extends Exception {
    private static final long serialVersionUID = -662191654860389176L;

    public AOException(String str) {
        super(str);
    }

    public AOException(String str, Throwable th) {
        super(str, th);
    }

    public AOException(Throwable th) {
        super(th);
    }
}
