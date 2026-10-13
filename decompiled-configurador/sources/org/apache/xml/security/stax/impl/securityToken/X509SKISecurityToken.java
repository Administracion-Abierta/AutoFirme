package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/securityToken/X509SKISecurityToken.class */
public class X509SKISecurityToken extends X509SecurityToken {
    private byte[] skiBytes;

    protected X509SKISecurityToken(SecurityTokenConstants.TokenType tokenType, InboundSecurityContext inboundSecurityContext, String id) {
        super(tokenType, inboundSecurityContext, id, SecurityTokenConstants.KeyIdentifier_SkiKeyIdentifier, false);
    }

    public byte[] getSkiBytes() {
        return this.skiBytes;
    }

    public void setSkiBytes(byte[] skiBytes) {
        this.skiBytes = skiBytes;
    }
}
