package org.apache.xml.security.exceptions;

import java.text.MessageFormat;
import org.apache.xml.security.utils.I18n;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/exceptions/XMLSecurityException.class */
public class XMLSecurityException extends Exception {
    private static final long serialVersionUID = 1;
    protected String msgID;

    public XMLSecurityException() {
        super("Missing message string");
        this.msgID = null;
    }

    public XMLSecurityException(String msgID) {
        super(I18n.getExceptionMessage(msgID));
        this.msgID = msgID;
    }

    public XMLSecurityException(String msgID, Object[] exArgs) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));
        this.msgID = msgID;
    }

    public XMLSecurityException(Exception originalException) {
        super(originalException.getMessage(), originalException);
    }

    public XMLSecurityException(Exception originalException, String msgID) {
        super(I18n.getExceptionMessage(msgID, originalException), originalException);
        this.msgID = msgID;
    }

    @Deprecated
    public XMLSecurityException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public XMLSecurityException(Exception originalException, String msgID, Object[] exArgs) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs), originalException);
        this.msgID = msgID;
    }

    @Deprecated
    public XMLSecurityException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }

    public String getMsgID() {
        if (this.msgID == null) {
            return "Missing message ID";
        }
        return this.msgID;
    }

    @Override // java.lang.Throwable
    public String toString() {
        String message;
        String s = getClass().getName();
        String message2 = super.getLocalizedMessage();
        if (message2 != null) {
            message = s + ": " + message2;
        } else {
            message = s;
        }
        if (super.getCause() != null) {
            message = message + "\nOriginal Exception was " + super.getCause().toString();
        }
        return message;
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        synchronized (System.err) {
            super.printStackTrace(System.err);
        }
    }

    public Exception getOriginalException() {
        if (getCause() instanceof Exception) {
            return (Exception) getCause();
        }
        return null;
    }
}
