package es.gob.jmulticard.card;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/BadPinException.class */
public class BadPinException extends PinException {
    private static final long serialVersionUID = 9827614003517666L;
    private final int retries;

    public BadPinException(int retriesLeft) {
        super("PIN incorrecto, intentos restantes: " + Integer.toString(retriesLeft));
        this.retries = retriesLeft;
    }

    public BadPinException(String msg) {
        super(msg);
        this.retries = -1;
    }

    public int getRemainingRetries() {
        return this.retries;
    }
}
