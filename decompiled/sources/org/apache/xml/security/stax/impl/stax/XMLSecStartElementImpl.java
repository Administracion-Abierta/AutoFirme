package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecStartElementImpl.class */
public class XMLSecStartElementImpl extends XMLSecEventBaseImpl implements XMLSecStartElement {
    private final QName elementName;
    private XMLSecNamespace elementNamespace;
    private List<XMLSecAttribute> attributes;
    private List<XMLSecNamespace> namespaces;

    public XMLSecStartElementImpl(QName elementName, List<XMLSecAttribute> attributes, List<XMLSecNamespace> namespaces, XMLSecStartElement parentXmlSecStartElement) {
        this.attributes = Collections.emptyList();
        this.namespaces = Collections.emptyList();
        this.elementName = elementName;
        setParentXMLSecStartElement(parentXmlSecStartElement);
        if (attributes != null) {
            this.attributes = attributes;
        }
        if (namespaces != null) {
            this.namespaces = namespaces;
        }
    }

    public XMLSecStartElementImpl(QName elementName, Collection<XMLSecAttribute> attributes, Collection<XMLSecNamespace> namespaces) {
        this.attributes = Collections.emptyList();
        this.namespaces = Collections.emptyList();
        this.elementName = elementName;
        if (attributes != null && !attributes.isEmpty()) {
            this.attributes = new ArrayList(attributes);
        }
        if (namespaces != null && !namespaces.isEmpty()) {
            this.namespaces = new ArrayList(namespaces);
        }
    }

    public QName getName() {
        return this.elementName;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public XMLSecNamespace getElementNamespace() {
        if (this.elementNamespace == null) {
            this.elementNamespace = XMLSecNamespaceImpl.getInstance(this.elementName.getPrefix(), this.elementName.getNamespaceURI());
        }
        return this.elementNamespace;
    }

    public Iterator<Attribute> getAttributes() {
        if (this.attributes.isEmpty()) {
            return getEmptyIterator();
        }
        return this.attributes.iterator();
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public void getAttributesFromCurrentScope(List<XMLSecAttribute> comparableAttributeList) {
        comparableAttributeList.addAll(this.attributes);
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getAttributesFromCurrentScope(comparableAttributeList);
        }
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public List<XMLSecAttribute> getOnElementDeclaredAttributes() {
        return this.attributes;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public void addAttribute(XMLSecAttribute xmlSecAttribute) {
        if (this.attributes == Collections.emptyList()) {
            this.attributes = new ArrayList(1);
        }
        this.attributes.add(xmlSecAttribute);
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public int getDocumentLevel() {
        return super.getDocumentLevel() + 1;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public void getElementPath(List<QName> list) {
        super.getElementPath(list);
        list.add(getName());
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public XMLSecStartElement getStartElementAtLevel(int level) {
        int thisLevel = getDocumentLevel();
        if (thisLevel < level) {
            return null;
        }
        if (thisLevel == level) {
            return this;
        }
        return this.parentXMLSecStartELement.getStartElementAtLevel(level);
    }

    public Iterator<Namespace> getNamespaces() {
        if (this.namespaces.isEmpty()) {
            return getEmptyIterator();
        }
        return this.namespaces.iterator();
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public void getNamespacesFromCurrentScope(List<XMLSecNamespace> comparableNamespaceList) {
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getNamespacesFromCurrentScope(comparableNamespaceList);
        }
        comparableNamespaceList.addAll(this.namespaces);
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public List<XMLSecNamespace> getOnElementDeclaredNamespaces() {
        return this.namespaces;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartElement
    public void addNamespace(XMLSecNamespace xmlSecNamespace) {
        if (this.namespaces == Collections.emptyList()) {
            this.namespaces = new ArrayList(1);
        }
        this.namespaces.add(xmlSecNamespace);
    }

    public Attribute getAttributeByName(QName name) {
        for (XMLSecAttribute comparableAttribute : this.attributes) {
            if (name.equals(comparableAttribute.getName())) {
                return comparableAttribute;
            }
        }
        return null;
    }

    public NamespaceContext getNamespaceContext() {
        return new NamespaceContext() { // from class: org.apache.xml.security.stax.impl.stax.XMLSecStartElementImpl.1
            @Override // javax.xml.namespace.NamespaceContext
            public String getNamespaceURI(String prefix) {
                for (XMLSecNamespace comparableNamespace : XMLSecStartElementImpl.this.namespaces) {
                    if (prefix.equals(comparableNamespace.getPrefix())) {
                        return comparableNamespace.getNamespaceURI();
                    }
                }
                if (XMLSecStartElementImpl.this.parentXMLSecStartELement != null) {
                    return XMLSecStartElementImpl.this.parentXMLSecStartELement.getNamespaceURI(prefix);
                }
                return null;
            }

            @Override // javax.xml.namespace.NamespaceContext
            public String getPrefix(String namespaceURI) {
                for (XMLSecNamespace comparableNamespace : XMLSecStartElementImpl.this.namespaces) {
                    if (namespaceURI.equals(comparableNamespace.getNamespaceURI())) {
                        return comparableNamespace.getPrefix();
                    }
                }
                if (XMLSecStartElementImpl.this.parentXMLSecStartELement != null) {
                    return XMLSecStartElementImpl.this.parentXMLSecStartELement.getNamespaceContext().getPrefix(namespaceURI);
                }
                return null;
            }

            @Override // javax.xml.namespace.NamespaceContext
            public Iterator getPrefixes(String namespaceURI) {
                Set<String> prefixes = new HashSet<>();
                List<XMLSecNamespace> xmlSecNamespaces = new ArrayList<>();
                XMLSecStartElementImpl.this.getNamespacesFromCurrentScope(xmlSecNamespaces);
                for (XMLSecNamespace xmlSecNamespace : xmlSecNamespaces) {
                    if (namespaceURI.equals(xmlSecNamespace.getNamespaceURI())) {
                        prefixes.add(xmlSecNamespace.getPrefix());
                    }
                }
                return prefixes.iterator();
            }
        };
    }

    public String getNamespaceURI(String prefix) {
        for (XMLSecNamespace comparableNamespace : this.namespaces) {
            if (prefix.equals(comparableNamespace.getPrefix())) {
                return comparableNamespace.getNamespaceURI();
            }
        }
        if (this.parentXMLSecStartELement != null) {
            return this.parentXMLSecStartELement.getNamespaceURI(prefix);
        }
        return null;
    }

    public int getEventType() {
        return 1;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isStartElement() {
        return true;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asStartElement */
    public XMLSecStartElement mo1376asStartElement() {
        return this;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(60);
            String prefix = getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(getName().getLocalPart());
            for (Namespace xmlSecNamespace : this.namespaces) {
                writer.write(" xmlns");
                String nsPrefix = xmlSecNamespace.getPrefix();
                if (nsPrefix != null && !nsPrefix.isEmpty()) {
                    writer.write(58);
                    writer.write(nsPrefix);
                }
                writer.write("=\"");
                writer.write(xmlSecNamespace.getValue());
                writer.write(34);
            }
            for (Attribute xmlSecAttribute : this.attributes) {
                writer.write(32);
                String attrPrefix = xmlSecAttribute.getName().getPrefix();
                if (attrPrefix != null && !attrPrefix.isEmpty()) {
                    writer.write(attrPrefix);
                    writer.write(58);
                }
                writer.write(xmlSecAttribute.getName().getLocalPart());
                writer.write("=\"");
                writer.write(xmlSecAttribute.getValue());
                writer.write(34);
            }
            writer.write(62);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
