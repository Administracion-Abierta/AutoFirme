package org.apache.xml.security.stax.securityEvent;

import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/ContentEncryptedElementSecurityEvent.class */
public class ContentEncryptedElementSecurityEvent extends AbstractSecuredElementSecurityEvent {
    public ContentEncryptedElementSecurityEvent(InboundSecurityToken inboundSecurityToken, boolean encrypted, List<XMLSecurityConstants.ContentType> protectionOrder) {
        super(SecurityEventConstants.ContentEncrypted, inboundSecurityToken, protectionOrder, false, encrypted);
    }
}
