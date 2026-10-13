package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/X509TokenSecurityEvent.class */
public class X509TokenSecurityEvent extends TokenSecurityEvent<SecurityToken> {
    public X509TokenSecurityEvent() {
        super(SecurityEventConstants.X509Token);
    }
}
