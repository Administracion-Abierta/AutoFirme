package org.apache.xml.security.stax.impl;

import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/XMLSecurityEventWriter.class */
public class XMLSecurityEventWriter implements XMLEventWriter {
    private final XMLStreamWriter xmlStreamWriter;

    public XMLSecurityEventWriter(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void add(XMLEvent event) throws XMLStreamException {
        if (!(event instanceof XMLSecEvent)) {
            throw new IllegalArgumentException("XMLEvent must be an instance of XMLSecEvent");
        }
        ProcessingInstruction processingInstruction = (XMLSecEvent) event;
        switch (processingInstruction.getEventType()) {
            case 1:
                XMLSecStartElement xmlSecStartElement = processingInstruction.mo1376asStartElement();
                QName n = xmlSecStartElement.getName();
                this.xmlStreamWriter.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
                List<XMLSecNamespace> xmlSecNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
                for (XMLSecNamespace namespace : xmlSecNamespaces) {
                    add(namespace);
                }
                List<XMLSecAttribute> xmlSecAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
                for (XMLSecAttribute xmlSecAttribute : xmlSecAttributes) {
                    add(xmlSecAttribute);
                }
                return;
            case 2:
                this.xmlStreamWriter.writeEndElement();
                return;
            case 3:
                ProcessingInstruction pi = processingInstruction;
                this.xmlStreamWriter.writeProcessingInstruction(pi.getTarget(), pi.getData());
                return;
            case 4:
                XMLSecCharacters characters = processingInstruction.mo1374asCharacters();
                if (!characters.isCData()) {
                    char[] text = characters.getText();
                    this.xmlStreamWriter.writeCharacters(text, 0, text.length);
                    return;
                } else {
                    this.xmlStreamWriter.writeCData(characters.getData());
                    return;
                }
            case 5:
                this.xmlStreamWriter.writeComment(((Comment) processingInstruction).getText());
                return;
            case 6:
            case 14:
            case 15:
            default:
                throw new XMLStreamException("Illegal event");
            case 7:
                StartDocument startDocument = (StartDocument) processingInstruction;
                if (!startDocument.encodingSet()) {
                    this.xmlStreamWriter.writeStartDocument(startDocument.getVersion());
                    return;
                } else {
                    this.xmlStreamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                    return;
                }
            case 8:
                this.xmlStreamWriter.writeEndDocument();
                return;
            case 9:
                this.xmlStreamWriter.writeEntityRef(((EntityReference) processingInstruction).getName());
                return;
            case 10:
                Attribute attribute = (Attribute) processingInstruction;
                QName name = attribute.getName();
                this.xmlStreamWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                return;
            case 11:
                this.xmlStreamWriter.writeDTD(((DTD) processingInstruction).getDocumentTypeDeclaration());
                return;
            case 12:
                this.xmlStreamWriter.writeCData(processingInstruction.mo1374asCharacters().getData());
                return;
            case 13:
                Namespace ns = (Namespace) processingInstruction;
                this.xmlStreamWriter.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
                return;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    public void close() throws XMLStreamException {
        this.xmlStreamWriter.close();
    }

    public void flush() throws XMLStreamException {
        this.xmlStreamWriter.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.xmlStreamWriter.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.xmlStreamWriter.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.xmlStreamWriter.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this.xmlStreamWriter.setNamespaceContext(namespaceContext);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.xmlStreamWriter.setPrefix(prefix, uri);
    }
}
