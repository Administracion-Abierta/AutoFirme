package org.apache.xml.security.stax.ext;

import java.util.List;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/OutboundSecurityContext.class */
public interface OutboundSecurityContext extends SecurityContext {
    void registerSecurityTokenProvider(String str, SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider);

    SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String str);

    List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders();
}
