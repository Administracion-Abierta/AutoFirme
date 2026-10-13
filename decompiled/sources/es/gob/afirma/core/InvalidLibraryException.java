package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/InvalidLibraryException.class */
public class InvalidLibraryException extends RuntimeException {
    private static final long serialVersionUID = -8418397871871426778L;

    public InvalidLibraryException(String str) {
        super(str);
    }

    public InvalidLibraryException(String str, Throwable th) {
        super(str, th);
    }
}
