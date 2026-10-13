package org.apache.xml.security.stax.ext;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/UncheckedXMLSecurityException.class */
public class UncheckedXMLSecurityException extends RuntimeException {
    private static final long serialVersionUID = 3440022764012033141L;

    public UncheckedXMLSecurityException(String message) {
        super(message);
    }

    public UncheckedXMLSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedXMLSecurityException(Throwable cause) {
        super(cause);
    }
}
