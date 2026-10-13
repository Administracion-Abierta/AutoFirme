package es.gob.afirma.signers.pades;

import es.gob.afirma.core.InvalidLibraryException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/InvalidITextException.class */
public final class InvalidITextException extends InvalidLibraryException {
    private static final long serialVersionUID = -322997692480101275L;

    public InvalidITextException(Throwable th) {
        super("El Cliente afirma necesita una version especifica propia de iText", th);
    }
}
