package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/OutboundSecurityContextImpl.class */
public class OutboundSecurityContextImpl extends AbstractSecurityContextImpl implements OutboundSecurityContext {
    private final Map<String, SecurityTokenProvider<OutboundSecurityToken>> securityTokenProviders = new HashMap();

    @Override // org.apache.xml.security.stax.ext.OutboundSecurityContext
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override // org.apache.xml.security.stax.ext.OutboundSecurityContext
    public SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String id) {
        return this.securityTokenProviders.get(id);
    }

    @Override // org.apache.xml.security.stax.ext.OutboundSecurityContext
    public List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders() {
        return new ArrayList(this.securityTokenProviders.values());
    }
}
