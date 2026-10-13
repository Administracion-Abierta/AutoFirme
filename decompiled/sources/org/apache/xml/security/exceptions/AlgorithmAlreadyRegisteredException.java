package org.apache.xml.security.exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/exceptions/AlgorithmAlreadyRegisteredException.class */
public class AlgorithmAlreadyRegisteredException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public AlgorithmAlreadyRegisteredException() {
    }

    public AlgorithmAlreadyRegisteredException(String msgID) {
        super(msgID);
    }

    public AlgorithmAlreadyRegisteredException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public AlgorithmAlreadyRegisteredException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public AlgorithmAlreadyRegisteredException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public AlgorithmAlreadyRegisteredException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public AlgorithmAlreadyRegisteredException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
