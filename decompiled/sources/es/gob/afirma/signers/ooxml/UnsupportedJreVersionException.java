package es.gob.afirma.signers.ooxml;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/UnsupportedJreVersionException.class */
public final class UnsupportedJreVersionException extends RuntimeException {
    private static final long serialVersionUID = -5356570249002228729L;

    UnsupportedJreVersionException() {
        super("Version de Java no soportada. Se necesita Java 7 o superior, y se ha encontrado Java " + System.getProperty("java.version"));
    }
}
