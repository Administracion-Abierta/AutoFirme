package org.apache.xml.security.encryption;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/XMLEncryptionException.class */
public class XMLEncryptionException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public XMLEncryptionException() {
    }

    public XMLEncryptionException(Exception ex) {
        super(ex);
    }

    public XMLEncryptionException(String msgID) {
        super(msgID);
    }

    public XMLEncryptionException(String msgID, Object... exArgs) {
        super(msgID, exArgs);
    }

    public XMLEncryptionException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public XMLEncryptionException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public XMLEncryptionException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public XMLEncryptionException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
