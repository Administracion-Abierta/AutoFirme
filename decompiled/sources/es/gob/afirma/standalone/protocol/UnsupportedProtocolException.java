package es.gob.afirma.standalone.protocol;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/UnsupportedProtocolException.class */
public class UnsupportedProtocolException extends Exception {
    private static final long serialVersionUID = -7429271911165454430L;
    private static final int DEFAULT_VERSION = 1;
    private final boolean newVersionNeeded;
    private final int version;

    UnsupportedProtocolException() {
        this(1, true);
    }

    UnsupportedProtocolException(int i, boolean z) {
        super("Version del protocolo no soportada");
        this.version = i;
        this.newVersionNeeded = z;
    }

    public boolean isNewVersionNeeded() {
        return this.newVersionNeeded;
    }

    public int getVersion() {
        return this.version;
    }
}
