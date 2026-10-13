package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/resourceResolvers/ResolverSameDocument.class */
public class ResolverSameDocument implements ResourceResolver, ResourceResolverLookup {
    private String id;
    private boolean firstElementOccured = false;

    public ResolverSameDocument() {
    }

    public ResolverSameDocument(String uri) {
        this.id = XMLSecurityUtils.dropReferenceMarker(uri);
    }

    public String getId() {
        return this.id;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri != null) {
            if ((!uri.isEmpty() && uri.charAt(0) != '#') || uri.startsWith("#xpointer")) {
                return null;
            }
            return this;
        }
        return null;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverSameDocument(uri);
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean isSameDocumentReference() {
        return true;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return matches(xmlSecStartElement, XMLSecurityConstants.ATT_NULL_Id);
    }

    public boolean matches(XMLSecStartElement xmlSecStartElement, QName idAttributeNS) {
        if (this.id.isEmpty()) {
            if (this.firstElementOccured) {
                return false;
            }
            this.firstElementOccured = true;
            return true;
        }
        Attribute attribute = xmlSecStartElement.getAttributeByName(idAttributeNS);
        if (attribute != null && attribute.getValue().equals(this.id)) {
            return true;
        }
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        return null;
    }
}
