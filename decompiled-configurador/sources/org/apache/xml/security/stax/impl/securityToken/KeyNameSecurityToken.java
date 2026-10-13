package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/securityToken/KeyNameSecurityToken.class */
public class KeyNameSecurityToken extends AbstractInboundSecurityToken {
    private String keyName;

    public KeyNameSecurityToken(String keyName, InboundSecurityContext inboundSecurityContext) {
        super(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_KeyName, false);
        this.keyName = keyName;
    }

    @Override // org.apache.xml.security.stax.securityToken.SecurityToken
    public SecurityTokenConstants.TokenType getTokenType() {
        return SecurityTokenConstants.KeyNameToken;
    }

    public String getKeyName() {
        return this.keyName;
    }
}
