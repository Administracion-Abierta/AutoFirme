package org.apache.xml.security.signature;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/InvalidSignatureValueException.class */
public class InvalidSignatureValueException extends XMLSignatureException {
    private static final long serialVersionUID = 1;

    public InvalidSignatureValueException() {
    }

    public InvalidSignatureValueException(String msgID) {
        super(msgID);
    }

    public InvalidSignatureValueException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public InvalidSignatureValueException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public InvalidSignatureValueException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public InvalidSignatureValueException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public InvalidSignatureValueException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
