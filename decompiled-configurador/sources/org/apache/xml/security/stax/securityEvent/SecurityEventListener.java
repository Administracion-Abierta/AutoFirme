package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/SecurityEventListener.class */
public interface SecurityEventListener {
    void registerSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException;
}
