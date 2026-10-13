package org.apache.xml.security.transforms;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/TransformationException.class */
public class TransformationException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public TransformationException() {
    }

    public TransformationException(Exception ex) {
        super(ex);
    }

    public TransformationException(String msgID) {
        super(msgID);
    }

    public TransformationException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public TransformationException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public TransformationException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public TransformationException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public TransformationException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
