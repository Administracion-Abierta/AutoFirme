package es.gob.jmulticard.jse.provider;

import java.security.SignatureException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/SignatureAuthException.class */
public final class SignatureAuthException extends SignatureException {
    private static final long serialVersionUID = 6467790733377018931L;

    public SignatureAuthException(Throwable cause) {
        super(cause);
    }
}
