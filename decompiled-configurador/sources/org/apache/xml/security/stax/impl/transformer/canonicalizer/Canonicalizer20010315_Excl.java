package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/canonicalizer/Canonicalizer20010315_Excl.class */
public abstract class Canonicalizer20010315_Excl extends CanonicalizerBase {
    public static final String INCLUSIVE_NAMESPACES_PREFIX_LIST = "inclusiveNamespacePrefixList";
    public static final String PROPAGATE_DEFAULT_NAMESPACE = "propagateDefaultNamespace";
    protected List<String> inclusiveNamespaces;
    protected boolean propagateDefaultNamespace;

    public Canonicalizer20010315_Excl(boolean includeComments) {
        super(includeComments);
        this.propagateDefaultNamespace = false;
    }

    @Override // org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase, org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
        this.inclusiveNamespaces = getPrefixList((List) properties.get(INCLUSIVE_NAMESPACES_PREFIX_LIST));
        Boolean propagateDfltNs = (Boolean) properties.get(PROPAGATE_DEFAULT_NAMESPACE);
        if (propagateDfltNs != null) {
            this.propagateDefaultNamespace = propagateDfltNs.booleanValue();
        }
    }

    protected static List<String> getPrefixList(List<String> inclusiveNamespaces) {
        if (inclusiveNamespaces == null || inclusiveNamespaces.isEmpty()) {
            return null;
        }
        List<String> prefixes = new ArrayList<>(inclusiveNamespaces.size());
        for (int i = 0; i < inclusiveNamespaces.size(); i++) {
            String s = inclusiveNamespaces.get(i).intern();
            if ("#default".equals(s)) {
                prefixes.add("");
            } else {
                prefixes.add(s);
            }
        }
        return prefixes;
    }

    @Override // org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase
    protected List<XMLSecNamespace> getCurrentUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, CanonicalizerBase.C14NStack<XMLSecEvent> outputStack) {
        XMLSecNamespace resultNamespace;
        List<XMLSecNamespace> utilizedNamespaces = Collections.emptyList();
        XMLSecNamespace elementNamespace = xmlSecStartElement.getElementNamespace();
        XMLSecNamespace found = (XMLSecNamespace) outputStack.containsOnStack(elementNamespace);
        if (found == null || found.getNamespaceURI() == null || !found.getNamespaceURI().equals(elementNamespace.getNamespaceURI())) {
            utilizedNamespaces = new ArrayList(2);
            utilizedNamespaces.add(elementNamespace);
            outputStack.peek().add(elementNamespace);
        }
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < comparableAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = comparableAttributes.get(i);
            XMLSecNamespace attributeNamespace = comparableAttribute.getAttributeNamespace();
            if (!org.apache.xml.security.c14n.implementations.CanonicalizerBase.XML.equals(attributeNamespace.getPrefix()) && attributeNamespace.getNamespaceURI() != null && !attributeNamespace.getNamespaceURI().isEmpty() && ((resultNamespace = (XMLSecNamespace) outputStack.containsOnStack(attributeNamespace)) == null || resultNamespace.getNamespaceURI() == null || !resultNamespace.getNamespaceURI().equals(attributeNamespace.getNamespaceURI()))) {
                if (utilizedNamespaces == Collections.emptyList()) {
                    utilizedNamespaces = new ArrayList(2);
                }
                utilizedNamespaces.add(attributeNamespace);
                outputStack.peek().add(attributeNamespace);
            }
        }
        if (this.inclusiveNamespaces != null) {
            for (int i2 = 0; i2 < this.inclusiveNamespaces.size(); i2++) {
                String prefix = this.inclusiveNamespaces.get(i2);
                String ns = xmlSecStartElement.getNamespaceURI(prefix);
                if (ns == null && prefix.isEmpty()) {
                    ns = "";
                } else {
                    if (ns == null) {
                    }
                }
                XMLSecNamespace comparableNamespace = XMLSecEventFactory.createXMLSecNamespace(prefix, ns);
                XMLSecNamespace resultNamespace2 = (XMLSecNamespace) outputStack.containsOnStack(comparableNamespace);
                if (resultNamespace2 == null || resultNamespace2.getNamespaceURI() == null || !resultNamespace2.getNamespaceURI().equals(comparableNamespace.getNamespaceURI()) || (this.firstCall && this.propagateDefaultNamespace && !utilizedNamespaces.contains(comparableNamespace))) {
                    if (utilizedNamespaces == Collections.emptyList()) {
                        utilizedNamespaces = new ArrayList(2);
                    }
                    utilizedNamespaces.add(comparableNamespace);
                    outputStack.peek().add(comparableNamespace);
                }
            }
        }
        return utilizedNamespaces;
    }

    @Override // org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase
    protected List<XMLSecNamespace> getInitialUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, CanonicalizerBase.C14NStack<XMLSecEvent> outputStack) {
        return getCurrentUtilizedNamespaces(xmlSecStartElement, outputStack);
    }

    @Override // org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase
    protected List<XMLSecAttribute> getInitialUtilizedAttributes(XMLSecStartElement xmlSecStartElement, CanonicalizerBase.C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < comparableAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = comparableAttributes.get(i);
            if (utilizedAttributes == Collections.emptyList()) {
                utilizedAttributes = new ArrayList(2);
            }
            utilizedAttributes.add(comparableAttribute);
        }
        return utilizedAttributes;
    }
}
