package es.gob.afirma.core;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/InvalidOSException.class */
public final class InvalidOSException extends RuntimeException {
    private static final long serialVersionUID = -6174267665607129065L;
    private final String os;

    public InvalidOSException(String str) {
        super("Se esperaba el sistema operativo: " + str);
        this.os = str;
    }

    public String getExpectedOS() {
        return this.os;
    }
}
