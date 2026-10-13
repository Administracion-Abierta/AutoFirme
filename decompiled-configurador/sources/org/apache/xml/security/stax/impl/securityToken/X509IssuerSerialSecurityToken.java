package org.apache.xml.security.stax.impl.securityToken;

import java.math.BigInteger;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/securityToken/X509IssuerSerialSecurityToken.class */
public class X509IssuerSerialSecurityToken extends X509SecurityToken {
    private String issuerName;
    private BigInteger serialNumber;

    protected X509IssuerSerialSecurityToken(SecurityTokenConstants.TokenType tokenType, InboundSecurityContext inboundSecurityContext, String id) {
        super(tokenType, inboundSecurityContext, id, SecurityTokenConstants.KeyIdentifier_IssuerSerial, false);
    }

    public String getIssuerName() {
        return this.issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }
}
