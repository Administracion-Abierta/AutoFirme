package es.gob.jmulticard.card;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/PasswordCallbackNotFoundException.class */
public final class PasswordCallbackNotFoundException extends PinException {
    private static final long serialVersionUID = 3347342217520582788L;

    public PasswordCallbackNotFoundException(String msg) {
        super(msg);
    }

    public PasswordCallbackNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
