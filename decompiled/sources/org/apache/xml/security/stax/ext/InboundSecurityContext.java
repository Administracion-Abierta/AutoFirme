package org.apache.xml.security.stax.ext;

import java.util.List;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/InboundSecurityContext.class */
public interface InboundSecurityContext extends SecurityContext {
    void registerSecurityTokenProvider(String str, SecurityTokenProvider<? extends InboundSecurityToken> securityTokenProvider);

    SecurityTokenProvider<? extends InboundSecurityToken> getSecurityTokenProvider(String str);

    List<SecurityTokenProvider<? extends InboundSecurityToken>> getRegisteredSecurityTokenProviders();
}
