package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.SecurityToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/EncryptedKeyTokenSecurityEvent.class */
public class EncryptedKeyTokenSecurityEvent extends TokenSecurityEvent<SecurityToken> {
    public EncryptedKeyTokenSecurityEvent() {
        super(SecurityEventConstants.EncryptedKeyToken);
    }
}
