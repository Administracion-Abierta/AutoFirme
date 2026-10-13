package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.InputStream;
import java.net.URI;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/resourceResolvers/ResolverFilesystem.class */
public class ResolverFilesystem implements ResourceResolver, ResourceResolverLookup {
    private String uri;
    private String baseURI;

    public ResolverFilesystem() {
    }

    public ResolverFilesystem(String uri, String baseURI) {
        this.uri = uri;
        this.baseURI = baseURI;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("file:") || (baseURI != null && baseURI.startsWith("file:"))) {
            return this;
        }
        return null;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverFilesystem(uri, baseURI);
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean isSameDocumentReference() {
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        URI tmp;
        try {
            if (this.baseURI == null || this.baseURI.length() == 0) {
                tmp = new URI(this.uri);
            } else {
                tmp = new URI(this.baseURI).resolve(this.uri);
            }
            if (tmp.getFragment() != null) {
                tmp = new URI(tmp.getScheme(), tmp.getSchemeSpecificPart(), null);
            }
            return tmp.toURL().openStream();
        } catch (Exception e) {
            throw new XMLSecurityException(e);
        }
    }
}
