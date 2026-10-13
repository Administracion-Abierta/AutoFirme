package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/MissingLibraryException.class */
public class MissingLibraryException extends RuntimeException {
    private static final long serialVersionUID = -2323154676951325543L;

    public MissingLibraryException(String str, Exception exc) {
        super(str, exc);
    }
}
