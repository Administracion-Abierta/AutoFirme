package org.apache.xml.security.stax.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/AbstractOutputProcessor.class */
public abstract class AbstractOutputProcessor implements OutputProcessor {
    protected XMLSecurityProperties securityProperties;
    protected XMLSecurityConstants.Action action;
    protected int actionOrder = -1;
    private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
    private Set<Class<? extends OutputProcessor>> beforeProcessors;
    private Set<Class<? extends OutputProcessor>> afterProcessors;

    protected AbstractOutputProcessor() throws XMLSecurityException {
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        this.securityProperties = xmlSecurityProperties;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void setAction(XMLSecurityConstants.Action action, int actionOrder) {
        this.action = action;
        this.actionOrder = actionOrder;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        outputProcessorChain.addProcessor(this);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public XMLSecurityConstants.Phase getPhase() {
        return this.phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void addBeforeProcessor(Class<? extends OutputProcessor> processor) {
        if (this.beforeProcessors == null) {
            this.beforeProcessors = new HashSet();
        }
        this.beforeProcessors.add(processor);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public Set<Class<? extends OutputProcessor>> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void addAfterProcessor(Class<? extends OutputProcessor> processor) {
        if (this.afterProcessors == null) {
            this.afterProcessors = new HashSet();
        }
        this.afterProcessors.add(processor);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public Set<Class<? extends OutputProcessor>> getAfterProcessors() {
        if (this.afterProcessors == null) {
            return Collections.emptySet();
        }
        return this.afterProcessors;
    }

    public XMLSecurityProperties getSecurityProperties() {
        return this.securityProperties;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public XMLSecurityConstants.Action getAction() {
        return this.action;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public int getActionOrder() {
        return this.actionOrder;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        outputProcessorChain.doFinal();
    }

    public XMLSecStartElement addAttributes(XMLSecStartElement xmlSecStartElement, List<XMLSecAttribute> attributeList) throws XMLStreamException {
        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < attributeList.size(); i++) {
            XMLSecAttribute xmlSecAttribute = attributeList.get(i);
            xmlSecStartElement.addAttribute(xmlSecAttribute);
            QName attributeName = xmlSecAttribute.getName();
            if (attributeName.getNamespaceURI() != null && attributeName.getNamespaceURI().length() != 0 && !declaredNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) {
                xmlSecStartElement.addNamespace(xmlSecAttribute.getAttributeNamespace());
            }
        }
        return xmlSecStartElement;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void createStartElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element, List<XMLSecNamespace> namespaces, List<XMLSecAttribute> attributes) throws XMLSecurityException, XMLStreamException {
        XMLSecStartElement xmlSecStartElement = XMLSecEventFactory.createXmlSecStartElement(element, attributes, namespaces);
        outputAsEvent(outputProcessorChain, xmlSecStartElement);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLSecStartElement createStartElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element, boolean outputLocalNs, List<XMLSecAttribute> attributes) throws XMLSecurityException, XMLStreamException {
        List<XMLSecNamespace> comparableNamespaces = Collections.emptyList();
        if (outputLocalNs) {
            comparableNamespaces = new ArrayList<>(2);
            comparableNamespaces.add(XMLSecEventFactory.createXMLSecNamespace(element.getPrefix(), element.getNamespaceURI()));
        }
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                XMLSecAttribute xmlSecAttribute = attributes.get(i);
                QName attributeName = xmlSecAttribute.getName();
                String attributeNamePrefix = attributeName.getPrefix();
                if ((attributeNamePrefix == null || !attributeNamePrefix.isEmpty()) && !comparableNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) {
                    if (comparableNamespaces == Collections.emptyList()) {
                        comparableNamespaces = new ArrayList<>(1);
                    }
                    comparableNamespaces.add(xmlSecAttribute.getAttributeNamespace());
                }
            }
        }
        XMLSecStartElement xmlSecStartElement = XMLSecEventFactory.createXmlSecStartElement(element, attributes, comparableNamespaces);
        outputAsEvent(outputProcessorChain, xmlSecStartElement);
        return xmlSecStartElement;
    }

    public XMLSecEndElement createEndElement(QName element) {
        return XMLSecEventFactory.createXmlSecEndElement(element);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void createEndElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element) throws XMLSecurityException, XMLStreamException {
        outputAsEvent(outputProcessorChain, createEndElement(element));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, String characters) throws XMLSecurityException, XMLStreamException {
        outputAsEvent(outputProcessorChain, createCharacters(characters));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, char[] text) throws XMLSecurityException, XMLStreamException {
        outputAsEvent(outputProcessorChain, createCharacters(text));
    }

    public XMLSecCharacters createCharacters(String characters) {
        return XMLSecEventFactory.createXmlSecCharacters(characters);
    }

    public XMLSecCharacters createCharacters(char[] text) {
        return XMLSecEventFactory.createXmlSecCharacters(text);
    }

    public XMLSecAttribute createAttribute(QName attribute, String attributeValue) {
        return XMLSecEventFactory.createXMLSecAttribute(attribute, attributeValue);
    }

    public XMLSecNamespace createNamespace(String prefix, String uri) {
        return XMLSecEventFactory.createXMLSecNamespace(prefix, uri);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void outputAsEvent(OutputProcessorChain outputProcessorChain, XMLSecEvent xmlSecEvent) throws XMLSecurityException, XMLStreamException {
        outputProcessorChain.reset();
        outputProcessorChain.processEvent(xmlSecEvent);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain, String dynamicParts) {
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(dynamicParts);
        return securePartMatches(xmlSecStartElement, dynamicSecureParts);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, Map<Object, SecurePart> secureParts) {
        Attribute attribute;
        SecurePart securePart = null;
        if (secureParts != null) {
            securePart = secureParts.get(xmlSecStartElement.getName());
            if (securePart == null && (attribute = xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS())) != null) {
                securePart = secureParts.get(attribute.getValue());
            }
        }
        return securePart;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void outputDOMElement(Element element, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        NamedNodeMap namedNodeMap = element.getAttributes();
        List<XMLSecAttribute> attributes = new ArrayList<>(namedNodeMap.getLength());
        List<XMLSecNamespace> namespaces = new ArrayList<>(namedNodeMap.getLength());
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Attr attribute = (Attr) namedNodeMap.item(i);
            if (attribute.getPrefix() == null) {
                attributes.add(createAttribute(new QName(attribute.getNamespaceURI(), attribute.getLocalName()), attribute.getValue()));
            } else if (CanonicalizerBase.XMLNS.equals(attribute.getPrefix()) || CanonicalizerBase.XMLNS.equals(attribute.getLocalName())) {
                namespaces.add(createNamespace(attribute.getLocalName(), attribute.getValue()));
            } else {
                attributes.add(createAttribute(new QName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix()), attribute.getValue()));
            }
        }
        QName elementName = new QName(element.getNamespaceURI(), element.getLocalName(), element.getPrefix());
        createStartElementAndOutputAsEvent(outputProcessorChain, elementName, namespaces, attributes);
        Node firstChild = element.getFirstChild();
        while (true) {
            Node childNode = firstChild;
            if (childNode != null) {
                if (1 == childNode.getNodeType()) {
                    outputDOMElement((Element) childNode, outputProcessorChain);
                } else if (3 == childNode.getNodeType()) {
                    createCharactersAndOutputAsEvent(outputProcessorChain, ((Text) childNode).getData());
                }
                firstChild = childNode.getNextSibling();
            } else {
                createEndElementAndOutputAsEvent(outputProcessorChain, elementName);
                return;
            }
        }
    }
}
