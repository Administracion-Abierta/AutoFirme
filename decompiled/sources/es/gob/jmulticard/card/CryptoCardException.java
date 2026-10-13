package es.gob.jmulticard.card;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/CryptoCardException.class */
public class CryptoCardException extends CardException {
    private static final long serialVersionUID = -3133117372570125570L;

    public CryptoCardException() {
    }

    public CryptoCardException(String msg) {
        super(msg);
    }

    public CryptoCardException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
