package es.gob.afirma.signers.pades;

import es.gob.afirma.core.AOFormatFileException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/InvalidPdfException.class */
public final class InvalidPdfException extends AOFormatFileException {
    private static final long serialVersionUID = 674827105543544636L;

    public InvalidPdfException(Throwable th) {
        super("El fichero no es un PDF o es un PDF no soportado", th);
    }

    public InvalidPdfException(String str) {
        super(str);
    }
}
