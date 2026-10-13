package org.apache.xml.security.stax.securityEvent;

import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityEvent/SignedElementSecurityEvent.class */
public class SignedElementSecurityEvent extends AbstractSecuredElementSecurityEvent {
    public SignedElementSecurityEvent(InboundSecurityToken inboundSecurityToken, boolean signed, List<XMLSecurityConstants.ContentType> protectionOrder) {
        super(SecurityEventConstants.SignedElement, inboundSecurityToken, protectionOrder, signed, false);
    }
}
