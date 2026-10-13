package es.gob.afirma.keystores;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeystoreAlternativeException.class */
public final class AOKeystoreAlternativeException extends Exception {
    private static final long serialVersionUID = -1536411480952188376L;
    private final AOKeyStore alternativeKs;

    public AOKeystoreAlternativeException(AOKeyStore aOKeyStore, String str, Exception exc) {
        super(str, exc);
        this.alternativeKs = aOKeyStore;
    }

    public AOKeystoreAlternativeException(AOKeyStore aOKeyStore, String str) {
        super(str);
        this.alternativeKs = aOKeyStore;
    }

    public AOKeyStore getAlternativeKsm() {
        return this.alternativeKs;
    }
}
