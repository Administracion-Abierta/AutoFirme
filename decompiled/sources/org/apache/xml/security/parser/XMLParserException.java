package org.apache.xml.security.parser;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/parser/XMLParserException.class */
public class XMLParserException extends XMLSecurityException {
    private static final long serialVersionUID = 1;

    public XMLParserException() {
    }

    public XMLParserException(String msgID) {
        super(msgID);
    }

    public XMLParserException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public XMLParserException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    public XMLParserException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }
}
