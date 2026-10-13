package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/InboundSecurityContextImpl.class */
public class InboundSecurityContextImpl extends AbstractSecurityContextImpl implements InboundSecurityContext {
    private static final Boolean allowMD5Algorithm = Boolean.valueOf(ConfigurationProperties.getProperty("AllowMD5Algorithm"));
    private final Map<String, SecurityTokenProvider<? extends InboundSecurityToken>> securityTokenProviders = new HashMap();

    @Override // org.apache.xml.security.stax.impl.AbstractSecurityContextImpl
    protected void forwardSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        if (!allowMD5Algorithm.booleanValue() && SecurityEventConstants.AlgorithmSuite.equals(securityEvent.getSecurityEventType())) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = (AlgorithmSuiteSecurityEvent) securityEvent;
            if (algorithmSuiteSecurityEvent.getAlgorithmURI().contains("md5") || algorithmSuiteSecurityEvent.getAlgorithmURI().contains(MessageDigestAlgorithms.MD5)) {
                throw new XMLSecurityException("secureProcessing.AllowMD5Algorithm");
            }
        }
        super.forwardSecurityEvent(securityEvent);
    }

    @Override // org.apache.xml.security.stax.ext.InboundSecurityContext
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider<? extends InboundSecurityToken> securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override // org.apache.xml.security.stax.ext.InboundSecurityContext
    public SecurityTokenProvider<? extends InboundSecurityToken> getSecurityTokenProvider(String id) {
        return this.securityTokenProviders.get(id);
    }

    @Override // org.apache.xml.security.stax.ext.InboundSecurityContext
    public List<SecurityTokenProvider<? extends InboundSecurityToken>> getRegisteredSecurityTokenProviders() {
        return new ArrayList(this.securityTokenProviders.values());
    }
}
