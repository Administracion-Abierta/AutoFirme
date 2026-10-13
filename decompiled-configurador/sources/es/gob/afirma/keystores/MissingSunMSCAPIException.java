package es.gob.afirma.keystores;

import es.gob.afirma.core.MissingLibraryException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/MissingSunMSCAPIException.class */
public class MissingSunMSCAPIException extends MissingLibraryException {
    private static final long serialVersionUID = -338521022809698613L;

    public MissingSunMSCAPIException(Exception exc) {
        super("No esta instalada la biblioteca SunMSCAPI", exc);
    }
}
