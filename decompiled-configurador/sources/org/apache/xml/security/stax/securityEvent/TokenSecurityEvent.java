package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/TokenSecurityEvent.class */
public abstract class TokenSecurityEvent<T extends SecurityToken> extends SecurityEvent {
    private T securityToken;

    public TokenSecurityEvent(SecurityEventConstants.Event securityEventType) {
        super(securityEventType);
    }

    public T getSecurityToken() {
        return this.securityToken;
    }

    public void setSecurityToken(T securityToken) {
        this.securityToken = securityToken;
    }
}
