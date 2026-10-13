package es.gob.afirma.signers.pkcs7;

import es.gob.afirma.core.AOInvalidFormatException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/pkcs7/ContainsNoDataException.class */
public class ContainsNoDataException extends AOInvalidFormatException {
    private static final long serialVersionUID = -114813694395801268L;

    public ContainsNoDataException(String str) {
        super(str);
    }

    public ContainsNoDataException(String str, Exception exc) {
        super(str, exc);
    }
}
