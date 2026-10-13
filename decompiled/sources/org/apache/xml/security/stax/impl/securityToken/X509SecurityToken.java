package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/securityToken/X509SecurityToken.class */
public class X509SecurityToken extends AbstractInboundSecurityToken {
    private final SecurityTokenConstants.TokenType tokenType;

    protected X509SecurityToken(SecurityTokenConstants.TokenType tokenType, InboundSecurityContext inboundSecurityContext, String id, SecurityTokenConstants.KeyIdentifier keyIdentifier, boolean includedInMessage) {
        super(inboundSecurityContext, id, keyIdentifier, includedInMessage);
        this.tokenType = tokenType;
    }

    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken, org.apache.xml.security.stax.securityToken.SecurityToken
    public boolean isAsymmetric() throws XMLSecurityException {
        return true;
    }

    @Override // org.apache.xml.security.stax.securityToken.SecurityToken
    public SecurityTokenConstants.TokenType getTokenType() {
        return this.tokenType;
    }
}
