package org.apache.xml.security.stax.securityToken;

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/securityToken/SecurityTokenFactory.class */
public abstract class SecurityTokenFactory {
    private static SecurityTokenFactory instance;

    public abstract InboundSecurityToken getSecurityToken(KeyInfoType keyInfoType, SecurityTokenConstants.KeyUsage keyUsage, XMLSecurityProperties xMLSecurityProperties, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException;

    public static synchronized SecurityTokenFactory getInstance() throws XMLSecurityException {
        if (instance == null) {
            String stf = ConfigurationProperties.getProperty("securityTokenFactory");
            if (stf == null) {
                throw new XMLSecurityException("algorithm.ClassDoesNotExist", new Object[]{"null"});
            }
            Class<?> callingClass = ConfigurationProperties.getCallingClass();
            if (callingClass == null) {
                callingClass = SecurityTokenFactory.class;
            }
            try {
                instance = (SecurityTokenFactory) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(stf, callingClass));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new XMLSecurityException(e, "algorithm.ClassDoesNotExist", new Object[]{stf});
            }
        }
        return instance;
    }
}
