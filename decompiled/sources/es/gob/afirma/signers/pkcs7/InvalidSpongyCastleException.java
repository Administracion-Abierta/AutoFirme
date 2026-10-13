package es.gob.afirma.signers.pkcs7;

import es.gob.afirma.core.InvalidLibraryException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/InvalidSpongyCastleException.class */
public class InvalidSpongyCastleException extends InvalidLibraryException {
    private static final long serialVersionUID = -322997692480101275L;
    private final String exp;
    private final String fnd;

    public InvalidSpongyCastleException(String str, String str2, Throwable th) {
        super("Se necesitaba SpongyCastle version " + str + ", pero se encontro la version " + str2, th);
        this.exp = str;
        this.fnd = str2;
    }

    public String getExpectedVersion() {
        return this.exp;
    }

    public String getFoundVersion() {
        return this.fnd;
    }
}
