package org.apache.xml.security.keys.storage;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/storage/StorageResolverException.class */
public class StorageResolverException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public StorageResolverException() {
    }

    public StorageResolverException(Exception ex) {
        super(ex);
    }

    public StorageResolverException(String msgID) {
        super(msgID);
    }

    public StorageResolverException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public StorageResolverException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public StorageResolverException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public StorageResolverException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public StorageResolverException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
