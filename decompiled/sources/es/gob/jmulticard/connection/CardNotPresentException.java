package es.gob.jmulticard.connection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/CardNotPresentException.class */
public final class CardNotPresentException extends ApduConnectionException {
    private static final long serialVersionUID = 4766021408409413374L;

    public CardNotPresentException(Throwable cause) {
        super("No hay ninguna tarjeta insertada en el lector", cause);
    }

    public CardNotPresentException() {
        super("No hay ninguna tarjeta insertada en el lector");
    }
}
