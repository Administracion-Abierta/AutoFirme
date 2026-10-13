package org.apache.xml.security.stax.securityEvent;

import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/EncryptedElementSecurityEvent.class */
public class EncryptedElementSecurityEvent extends AbstractSecuredElementSecurityEvent {
    public EncryptedElementSecurityEvent(InboundSecurityToken inboundSecurityToken, boolean encrypted, List<XMLSecurityConstants.ContentType> protectionOrder) {
        super(SecurityEventConstants.EncryptedElement, inboundSecurityToken, protectionOrder, false, encrypted);
    }
}
