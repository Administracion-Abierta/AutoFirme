package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityToken/InboundSecurityToken.class */
public interface InboundSecurityToken extends SecurityToken {
    Key getSecretKey(String str, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String str2) throws XMLSecurityException;

    PublicKey getPublicKey(String str, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String str2) throws XMLSecurityException;

    void addWrappedToken(InboundSecurityToken inboundSecurityToken);

    void verify() throws XMLSecurityException;

    List<QName> getElementPath();

    XMLSecEvent getXMLSecEvent();

    boolean isIncludedInMessage();
}
