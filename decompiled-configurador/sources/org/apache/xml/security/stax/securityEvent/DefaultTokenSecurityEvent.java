package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/DefaultTokenSecurityEvent.class */
public class DefaultTokenSecurityEvent extends TokenSecurityEvent<SecurityToken> {
    public DefaultTokenSecurityEvent() {
        super(SecurityEventConstants.DefaultToken);
    }
}
