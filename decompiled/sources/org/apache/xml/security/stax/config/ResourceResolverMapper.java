package org.apache.xml.security.stax.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.configuration.ResolverType;
import org.apache.xml.security.configuration.ResourceResolversType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/config/ResourceResolverMapper.class */
public final class ResourceResolverMapper {
    private static List<ResourceResolverLookup> resourceResolvers;

    private ResourceResolverMapper() {
    }

    protected static synchronized void init(ResourceResolversType resourceResolversType, Class<?> callingClass) throws Exception {
        List<ResolverType> handlerList = resourceResolversType.getResolver();
        resourceResolvers = new ArrayList(handlerList.size() + 1);
        for (int i = 0; i < handlerList.size(); i++) {
            ResolverType uriResolverType = handlerList.get(i);
            resourceResolvers.add((ResourceResolverLookup) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(uriResolverType.getJAVACLASS(), callingClass)));
        }
    }

    public static ResourceResolver getResourceResolver(String uri, String baseURI) throws XMLSecurityException {
        for (ResourceResolverLookup resourceResolver : resourceResolvers) {
            ResourceResolverLookup rr = resourceResolver.canResolve(uri, baseURI);
            if (rr != null) {
                return rr.newInstance(uri, baseURI);
            }
        }
        throw new XMLSecurityException("utils.resolver.noClass", new Object[]{uri, baseURI});
    }
}
