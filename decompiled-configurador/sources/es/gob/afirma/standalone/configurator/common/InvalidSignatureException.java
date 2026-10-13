package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.AOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/InvalidSignatureException.class */
public class InvalidSignatureException extends AOException {
    private static final long serialVersionUID = -3747038770621522703L;

    public InvalidSignatureException(String str) {
        super(str);
    }

    public InvalidSignatureException(Throwable th) {
        super(th);
    }

    public InvalidSignatureException(String str, Throwable th) {
        super(str, th);
    }
}
