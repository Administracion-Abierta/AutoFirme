package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityToken/OutboundSecurityToken.class */
public interface OutboundSecurityToken extends SecurityToken {
    OutputProcessor getProcessor();

    Key getSecretKey(String str) throws XMLSecurityException;

    void addWrappedToken(OutboundSecurityToken outboundSecurityToken);

    Element getCustomTokenReference();
}
