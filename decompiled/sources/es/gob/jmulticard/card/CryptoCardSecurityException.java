package es.gob.jmulticard.card;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/CryptoCardSecurityException.class */
public final class CryptoCardSecurityException extends CardException {
    private static final long serialVersionUID = -3133117372570125570L;

    public CryptoCardSecurityException(String msg) {
        super(msg);
    }

    public CryptoCardSecurityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
