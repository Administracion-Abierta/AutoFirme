package org.apache.xml.security.stax.securityToken;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityToken/SecurityTokenProvider.class */
public interface SecurityTokenProvider<T> {
    T getSecurityToken() throws XMLSecurityException;

    String getId();
}
