package org.apache.xml.security.exceptions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/exceptions/Base64DecodingException.class */
public class Base64DecodingException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public Base64DecodingException() {
    }

    public Base64DecodingException(String msgID) {
        super(msgID);
    }

    public Base64DecodingException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public Base64DecodingException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public Base64DecodingException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public Base64DecodingException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public Base64DecodingException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}
