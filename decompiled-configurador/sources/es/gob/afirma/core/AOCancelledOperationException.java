package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/AOCancelledOperationException.class */
public class AOCancelledOperationException extends RuntimeException {
    private static final long serialVersionUID = 4447842480432712246L;

    public AOCancelledOperationException() {
    }

    public AOCancelledOperationException(String str) {
        super(str);
    }

    public AOCancelledOperationException(String str, Exception exc) {
        super(str, exc);
    }
}
