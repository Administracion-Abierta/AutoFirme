package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/securityToken/X509SubjectNameSecurityToken.class */
public class X509SubjectNameSecurityToken extends X509SecurityToken {
    private String subjectName;

    protected X509SubjectNameSecurityToken(SecurityTokenConstants.TokenType tokenType, InboundSecurityContext inboundSecurityContext, String id) {
        super(tokenType, inboundSecurityContext, id, SecurityTokenConstants.KeyIdentifier_X509SubjectName, false);
    }

    public String getSubjectName() {
        return this.subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
