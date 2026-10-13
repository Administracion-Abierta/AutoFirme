package es.gob.afirma.signers.xml;

import es.gob.afirma.core.AOFormatFileException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/InvalidXMLException.class */
public final class InvalidXMLException extends AOFormatFileException {
    private static final long serialVersionUID = -8682543966969351726L;

    public InvalidXMLException(Throwable th) {
        super("Los datos proporcionados no son un XML apto para su firma con los parametros indicados: " + th, th);
    }

    public InvalidXMLException(String str) {
        super(str);
    }

    public InvalidXMLException(String str, Throwable th) {
        super(str, th);
    }
}
