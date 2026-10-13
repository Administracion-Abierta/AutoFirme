package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/XMLSecurityStreamWriter.class */
public class XMLSecurityStreamWriter implements XMLStreamWriter {
    private final OutputProcessorChain outputProcessorChain;
    private Element elementStack;
    private Element openStartElement;
    private NSContext namespaceContext = new NSContext(null);
    private boolean endDocumentWritten = false;
    private boolean haveToWriteEndElement = false;
    private SecurePart signEntireRequestPart;
    private SecurePart encryptEntireRequestPart;

    public XMLSecurityStreamWriter(OutputProcessorChain outputProcessorChain) {
        this.outputProcessorChain = outputProcessorChain;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void chainProcessEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        try {
            this.outputProcessorChain.reset();
            this.outputProcessorChain.processEvent(xmlSecEvent);
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        } catch (XMLStreamException e2) {
            String msg = e2.getMessage();
            if (msg != null && msg.contains("Trying to declare prefix xmlns (illegal as per NS 1.1 #4)")) {
                throw new XMLStreamException("If you hit this exception this most probably meansyou are using the javax.xml.transform.stax.StAXResult. Don't use it. It is buggy as hell.", e2);
            }
            throw e2;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void outputOpenStartElement() throws XMLStreamException {
        if (this.openStartElement != null) {
            chainProcessEvent(XMLSecEventFactory.createXmlSecStartElement(this.openStartElement.getQName(), (List<XMLSecAttribute>) this.openStartElement.getAttributes(), (List<XMLSecNamespace>) this.openStartElement.getNamespaces()));
            this.openStartElement = null;
        }
        if (this.haveToWriteEndElement) {
            this.haveToWriteEndElement = false;
            writeEndElement();
        }
    }

    private String getNamespacePrefix(String namespaceURI) {
        if (this.elementStack == null) {
            return this.namespaceContext.getPrefix(namespaceURI);
        }
        return this.elementStack.getNamespaceContext().getPrefix(namespaceURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String localName) throws XMLStreamException {
        writeStartElement("", localName, "");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(getNamespacePrefix(namespaceURI), localName, namespaceURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        Element element;
        outputOpenStartElement();
        if (this.elementStack == null) {
            element = new Element(this.elementStack, this.namespaceContext, namespaceURI, localName, prefix);
            if (this.signEntireRequestPart != null) {
                this.signEntireRequestPart.setName(new QName(namespaceURI, localName, prefix));
                this.outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.SIGNATURE_PARTS, this.signEntireRequestPart.getName(), this.signEntireRequestPart);
            }
            if (this.encryptEntireRequestPart != null) {
                this.encryptEntireRequestPart.setName(new QName(namespaceURI, localName, prefix));
                this.outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.ENCRYPTION_PARTS, this.encryptEntireRequestPart.getName(), this.encryptEntireRequestPart);
            }
        } else {
            element = new Element(this.elementStack, namespaceURI, localName, prefix);
        }
        this.elementStack = element;
        this.openStartElement = element;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEmptyElement(String localName) throws XMLStreamException {
        writeEmptyElement("", localName, "");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        writeEmptyElement(getNamespacePrefix(namespaceURI), localName, namespaceURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        writeStartElement(prefix, localName, namespaceURI);
        this.openStartElement.setEmptyElement(true);
        this.haveToWriteEndElement = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEndElement() throws XMLStreamException {
        outputOpenStartElement();
        Element element = this.elementStack;
        this.elementStack = this.elementStack.getParentElement();
        chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(element.getQName()));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEndDocument() throws XMLStreamException {
        if (!this.endDocumentWritten) {
            outputOpenStartElement();
            while (this.elementStack != null) {
                Element element = this.elementStack;
                this.elementStack = element.getParentElement();
                chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(element.getQName()));
            }
            chainProcessEvent(XMLSecEventFactory.createXMLSecEndDocument());
            this.endDocumentWritten = true;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void close() throws XMLStreamException {
        try {
            writeEndDocument();
            this.outputProcessorChain.reset();
            this.outputProcessorChain.doFinal();
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    public void flush() throws XMLStreamException {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        writeAttribute("", "", localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        writeAttribute(getNamespacePrefix(namespaceURI), namespaceURI, localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        this.openStartElement.addAttribute(XMLSecEventFactory.createXMLSecAttribute(new QName(namespaceURI, localName, prefix), value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        this.openStartElement.addNamespace(XMLSecEventFactory.createXMLSecNamespace(prefix, namespaceURI));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        if (this.openStartElement.getElementPrefix().equals("")) {
            this.openStartElement.setElementNamespace(namespaceURI);
            this.openStartElement.setElementPrefix("");
        }
        this.openStartElement.addNamespace(XMLSecEventFactory.createXMLSecNamespace("", namespaceURI));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeComment(String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecComment(data));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        writeProcessingInstruction(target, "");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecProcessingInstruction(target, data));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCData(String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecCData(data));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeDTD(String dtd) throws XMLStreamException {
        if (this.elementStack != null) {
            throw new XMLStreamException("Not in proLOG");
        }
        chainProcessEvent(XMLSecEventFactory.createXMLSecDTD(dtd));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeEntityRef(String name) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecEntityReference(name, XMLSecEventFactory.createXmlSecEntityDeclaration(name)));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument() throws XMLStreamException {
        writeStartDocument(null, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument(null, version);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        chainProcessEvent(XMLSecEventFactory.createXmlSecStartDocument(null, encoding, null, version));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(String text) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text, start, len));
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return getNamespacePrefix(uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (this.elementStack == null) {
            this.namespaceContext.add(prefix, uri);
        } else {
            this.elementStack.getNamespaceContext().add(prefix, uri);
        }
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        if (this.elementStack == null) {
            this.namespaceContext.add("", uri);
        } else {
            this.elementStack.getNamespaceContext().add("", uri);
        }
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        this.namespaceContext = new NSContext(context);
    }

    public NamespaceContext getNamespaceContext() {
        if (this.elementStack == null) {
            return this.namespaceContext;
        }
        return this.elementStack.getNamespaceContext();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Properties not supported");
    }

    public SecurePart getSignEntireRequestPart() {
        return this.signEntireRequestPart;
    }

    public void setSignEntireRequestPart(SecurePart signEntireRequestPart) {
        this.signEntireRequestPart = signEntireRequestPart;
    }

    public SecurePart getEncryptEntireRequestPart() {
        return this.encryptEntireRequestPart;
    }

    public void setEncryptEntireRequestPart(SecurePart encryptEntireRequestPart) {
        this.encryptEntireRequestPart = encryptEntireRequestPart;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/XMLSecurityStreamWriter$Element.class */
    private static class Element {
        private Element parentElement;
        private QName qName;
        private String elementName;
        private String elementNamespace;
        private String elementPrefix;
        private boolean emptyElement;
        private List<XMLSecNamespace> namespaces;
        private List<XMLSecAttribute> attributes;
        private NSContext namespaceContext;

        public Element(Element parentElement, String elementNamespace, String elementName, String elementPrefix) {
            this(parentElement, null, elementNamespace, elementName, elementPrefix);
        }

        public Element(Element parentElement, NSContext namespaceContext, String elementNamespace, String elementName, String elementPrefix) {
            this.namespaces = Collections.emptyList();
            this.attributes = Collections.emptyList();
            this.parentElement = parentElement;
            this.namespaceContext = namespaceContext;
            this.elementName = elementName;
            setElementNamespace(elementNamespace);
            setElementPrefix(elementPrefix);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Element getParentElement() {
            return this.parentElement;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEmptyElement(boolean emptyElement) {
            this.emptyElement = emptyElement;
        }

        private String getElementName() {
            return this.elementName;
        }

        private String getElementNamespace() {
            return this.elementNamespace;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setElementNamespace(String elementNamespace) {
            if (elementNamespace == null) {
                this.elementNamespace = "";
            } else {
                this.elementNamespace = elementNamespace;
            }
            this.qName = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getElementPrefix() {
            return this.elementPrefix;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setElementPrefix(String elementPrefix) {
            if (elementPrefix == null) {
                this.elementPrefix = "";
            } else {
                this.elementPrefix = elementPrefix;
            }
            this.qName = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<XMLSecNamespace> getNamespaces() {
            return this.namespaces;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addNamespace(XMLSecNamespace namespace) {
            if (this.namespaces == Collections.emptyList()) {
                this.namespaces = new ArrayList(1);
            }
            this.namespaces.add(namespace);
            getNamespaceContext().add(namespace.getPrefix(), namespace.getNamespaceURI());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<XMLSecAttribute> getAttributes() {
            return this.attributes;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAttribute(XMLSecAttribute attribute) {
            if (this.attributes == Collections.emptyList()) {
                this.attributes = new ArrayList(1);
            }
            this.attributes.add(attribute);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public NSContext getNamespaceContext() {
            if (this.namespaceContext == null) {
                if (this.emptyElement && this.parentElement != null) {
                    this.namespaceContext = this.parentElement.getNamespaceContext();
                } else if (this.parentElement != null) {
                    this.namespaceContext = new NSContext(this.parentElement.getNamespaceContext());
                } else {
                    this.namespaceContext = new NSContext(null);
                }
            }
            return this.namespaceContext;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public QName getQName() {
            if (this.qName == null) {
                this.qName = new QName(getElementNamespace(), getElementName(), getElementPrefix());
            }
            return this.qName;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/XMLSecurityStreamWriter$NSContext.class */
    private static class NSContext implements NamespaceContext {
        private NamespaceContext parentNamespaceContext;
        private List<String> prefixNsList = Collections.emptyList();

        NSContext(NamespaceContext parentNamespaceContext) {
            this.parentNamespaceContext = parentNamespaceContext;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getNamespaceURI(String prefix) {
            for (int i = 0; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (s.equals(prefix)) {
                    return this.prefixNsList.get(i + 1);
                }
            }
            if (this.parentNamespaceContext != null) {
                return this.parentNamespaceContext.getNamespaceURI(prefix);
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getPrefix(String namespaceURI) {
            for (int i = 1; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (s.equals(namespaceURI)) {
                    return this.prefixNsList.get(i - 1);
                }
            }
            if (this.parentNamespaceContext != null) {
                return this.parentNamespaceContext.getPrefix(namespaceURI);
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public Iterator getPrefixes(String namespaceURI) {
            List<String> prefixes = new ArrayList<>(1);
            for (int i = 1; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (s.equals(namespaceURI)) {
                    prefixes.add(this.prefixNsList.get(i - 1));
                }
            }
            if (this.parentNamespaceContext != null) {
                Iterator<String> parentPrefixes = this.parentNamespaceContext.getPrefixes(namespaceURI);
                while (parentPrefixes.hasNext()) {
                    prefixes.add(parentPrefixes.next());
                }
            }
            return prefixes.iterator();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void add(String prefix, String namespace) {
            if (this.prefixNsList == Collections.emptyList()) {
                this.prefixNsList = new ArrayList(1);
            }
            this.prefixNsList.add(prefix);
            this.prefixNsList.add(namespace);
        }
    }
}
