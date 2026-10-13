package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOInvalidFormatException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/EFacturaAlreadySignedException.class */
public final class EFacturaAlreadySignedException extends AOInvalidFormatException {
    private static final long serialVersionUID = -2721087059930025508L;

    public EFacturaAlreadySignedException() {
        super("La factura ya tiene una firma electronica y no admite firmas adicionales");
    }
}
