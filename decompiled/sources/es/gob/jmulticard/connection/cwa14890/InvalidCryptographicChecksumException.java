package es.gob.jmulticard.connection.cwa14890;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/InvalidCryptographicChecksumException.class */
public final class InvalidCryptographicChecksumException extends SecurityException {
    private static final long serialVersionUID = -366110067889217051L;

    public InvalidCryptographicChecksumException() {
        super("Checksum criptografico invalido (APDU respuesta = 6688)");
    }
}
