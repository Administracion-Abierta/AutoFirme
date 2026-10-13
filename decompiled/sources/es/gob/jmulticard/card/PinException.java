package es.gob.jmulticard.card;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/PinException.class */
public class PinException extends CardException {
    private static final long serialVersionUID = 9827614003517666L;

    public PinException(String msg) {
        super(msg);
    }

    public PinException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PinException() {
    }
}
