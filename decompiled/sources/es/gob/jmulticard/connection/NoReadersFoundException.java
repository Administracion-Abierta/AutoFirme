package es.gob.jmulticard.connection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/NoReadersFoundException.class */
public class NoReadersFoundException extends ApduConnectionException {
    private static final long serialVersionUID = -7828305035163301527L;

    public NoReadersFoundException() {
        super("No se detectaron lectores de tarjetas en el sistema");
    }

    public NoReadersFoundException(String msg) {
        super(msg);
    }

    public NoReadersFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
