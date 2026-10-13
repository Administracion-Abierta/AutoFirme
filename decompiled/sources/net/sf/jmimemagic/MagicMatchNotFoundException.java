package net.sf.jmimemagic;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicMatchNotFoundException.class */
public class MagicMatchNotFoundException extends Exception {
    private static final long serialVersionUID = -2939562214801201909L;

    public MagicMatchNotFoundException() {
    }

    public MagicMatchNotFoundException(String message) {
        super(message);
    }

    public MagicMatchNotFoundException(Throwable cause) {
        super(cause);
    }

    public MagicMatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
