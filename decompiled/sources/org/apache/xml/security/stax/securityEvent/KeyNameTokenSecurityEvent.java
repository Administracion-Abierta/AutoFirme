package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/KeyNameTokenSecurityEvent.class */
public class KeyNameTokenSecurityEvent extends TokenSecurityEvent<SecurityToken> {
    public KeyNameTokenSecurityEvent() {
        super(SecurityEventConstants.KeyNameToken);
    }
}
