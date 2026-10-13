package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOInvalidFormatException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/InvalidEFacturaDataException.class */
public class InvalidEFacturaDataException extends AOInvalidFormatException {
    private static final long serialVersionUID = -7465457150119381288L;

    public InvalidEFacturaDataException() {
        super("Los datos proporcionados no son una factura electronica");
    }
}
