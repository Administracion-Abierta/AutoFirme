package org.apache.xml.security.utils.resolver;

import org.apache.xml.security.signature.XMLSignatureInput;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/resolver/ResourceResolverSpi.class */
public abstract class ResourceResolverSpi {
    public abstract XMLSignatureInput engineResolveURI(ResourceResolverContext resourceResolverContext) throws ResourceResolverException;

    public abstract boolean engineCanResolveURI(ResourceResolverContext resourceResolverContext);
}
