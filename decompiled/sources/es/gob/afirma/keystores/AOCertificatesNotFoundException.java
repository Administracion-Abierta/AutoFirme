package es.gob.afirma.keystores;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOCertificatesNotFoundException.class */
public final class AOCertificatesNotFoundException extends AOException {
    private static final long serialVersionUID = -6996346324337434742L;

    public AOCertificatesNotFoundException() {
        super("No hay certificados en el almacen");
    }

    public AOCertificatesNotFoundException(String str) {
        super(str);
    }

    public AOCertificatesNotFoundException(String str, Throwable th) {
        super(str, th);
    }
}
