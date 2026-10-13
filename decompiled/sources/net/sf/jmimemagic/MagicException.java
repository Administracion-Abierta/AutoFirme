package net.sf.jmimemagic;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicException.class */
public class MagicException extends Exception {
    private static final long serialVersionUID = 438818233944415255L;

    public MagicException() {
    }

    public MagicException(String message) {
        super(message);
    }

    public MagicException(Throwable cause) {
        super(cause);
    }

    public MagicException(String message, Throwable cause) {
        super(message, cause);
    }
}
