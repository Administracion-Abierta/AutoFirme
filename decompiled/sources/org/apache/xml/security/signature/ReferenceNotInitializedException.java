package org.apache.xml.security.signature;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/ReferenceNotInitializedException.class */
public class ReferenceNotInitializedException extends XMLSignatureException {
    private static final long serialVersionUID = 1;

    public ReferenceNotInitializedException() {
    }

    public ReferenceNotInitializedException(Exception ex) {
        super(ex);
    }

    public ReferenceNotInitializedException(String msgID) {
        super(msgID);
    }

    public ReferenceNotInitializedException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public ReferenceNotInitializedException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public ReferenceNotInitializedException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public ReferenceNotInitializedException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public ReferenceNotInitializedException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
