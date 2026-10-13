package org.apache.xml.security.stax.impl;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.codehaus.stax2.XMLStreamProperties;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/XMLSecurityStreamReader.class */
public class XMLSecurityStreamReader implements XMLStreamReader {
    private final InputProcessorChain inputProcessorChain;
    private XMLSecEvent currentXMLSecEvent;
    private final boolean skipDocumentEvents;
    private String version;
    private boolean standalone;
    private boolean standaloneSet;
    private String characterEncodingScheme;
    private static final String ERR_STATE_NOT_ELEM = "Current state not START_ELEMENT or END_ELEMENT";
    private static final String ERR_STATE_NOT_STELEM = "Current state not START_ELEMENT";
    private static final String ERR_STATE_NOT_PI = "Current state not PROCESSING_INSTRUCTION";
    private static final int MASK_GET_TEXT = 6768;

    public XMLSecurityStreamReader(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties) {
        this.inputProcessorChain = inputProcessorChain;
        this.skipDocumentEvents = securityProperties.isSkipDocumentEvents();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (XMLStreamProperties.XSP_NAMESPACE_AWARE.equals(name)) {
            return true;
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int next() throws XMLStreamException {
        try {
            this.inputProcessorChain.reset();
            this.currentXMLSecEvent = this.inputProcessorChain.processEvent();
            int eventType = this.currentXMLSecEvent.getEventType();
            if (eventType == 7) {
                StartDocument startDocument = this.currentXMLSecEvent;
                this.version = startDocument.getVersion();
                if (startDocument.encodingSet()) {
                    this.characterEncodingScheme = startDocument.getCharacterEncodingScheme();
                }
                this.standalone = startDocument.isStandalone();
                this.standaloneSet = startDocument.standaloneSet();
                if (this.skipDocumentEvents) {
                    this.currentXMLSecEvent = this.inputProcessorChain.processEvent();
                    eventType = this.currentXMLSecEvent.getEventType();
                }
            }
            return eventType;
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    private XMLSecEvent getCurrentEvent() {
        return this.currentXMLSecEvent;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != type) {
            throw new XMLStreamException("Event type mismatch");
        }
        if (localName != null) {
            if (xmlSecEvent.getEventType() != 1 && xmlSecEvent.getEventType() != 2 && xmlSecEvent.getEventType() != 9) {
                throw new XMLStreamException("Expected non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + xmlSecEvent.getEventType() + ")");
            }
            String n = getLocalName();
            if (!n.equals(localName)) {
                throw new XMLStreamException("Expected local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (namespaceURI != null) {
            if (xmlSecEvent.getEventType() != 1 && xmlSecEvent.getEventType() != 2) {
                throw new XMLStreamException("Expected non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + xmlSecEvent.getEventType() + ")");
            }
            String uri = getNamespaceURI();
            if (namespaceURI.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    throw new XMLStreamException("Expected empty namespace, instead have '" + uri + "'.");
                }
            } else if (!namespaceURI.equals(uri)) {
                throw new XMLStreamException("Expected namespace '" + namespaceURI + "'; have '" + uri + "'.");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public String getElementText() throws XMLStreamException {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new XMLStreamException("Not positioned on a start element");
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int type = next();
            switch (type) {
                case 2:
                    return stringBuilder.toString();
                case 3:
                case 5:
                    break;
                case 4:
                case 6:
                case 9:
                case 12:
                    stringBuilder.append(getText());
                    break;
                case 7:
                case 8:
                case 10:
                case 11:
                default:
                    throw new XMLStreamException("Expected a text token, got " + type + ".");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int nextTag() throws XMLStreamException {
        while (true) {
            int next = next();
            switch (next) {
                case 1:
                case 2:
                    return next;
                case 3:
                case 5:
                case 6:
                    break;
                case 4:
                case 12:
                    if (!isWhiteSpace()) {
                        throw new XMLStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                    }
                    break;
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    throw new XMLStreamException("Received event " + next + ", instead of START_ELEMENT or END_ELEMENT.");
            }
        }
    }

    public boolean hasNext() throws XMLStreamException {
        return this.currentXMLSecEvent == null || this.currentXMLSecEvent.getEventType() != 8;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void close() throws XMLStreamException {
        try {
            this.inputProcessorChain.reset();
            this.inputProcessorChain.doFinal();
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    public String getNamespaceURI(String prefix) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getNamespaceURI(prefix);
            case 2:
                XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo1375asEndElement().getParentXMLSecStartElement();
                if (xmlSecStartElement != null) {
                    return xmlSecStartElement.getNamespaceURI(prefix);
                }
                return null;
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public boolean isStartElement() {
        return getCurrentEvent().isStartElement();
    }

    public boolean isEndElement() {
        return getCurrentEvent().isEndElement();
    }

    public boolean isCharacters() {
        return getCurrentEvent().isCharacters();
    }

    public boolean isWhiteSpace() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return xmlSecEvent.isCharacters() && xmlSecEvent.mo1374asCharacters().isWhiteSpace();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        Attribute attribute = xmlSecEvent.mo1376asStartElement().getAttributeByName(new QName(namespaceURI, localName));
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    public int getAttributeCount() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().size();
    }

    public QName getAttributeName(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getName();
    }

    public String getAttributeNamespace(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getAttributeNamespace().getNamespaceURI();
    }

    public String getAttributeLocalName(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getName().getLocalPart();
    }

    public String getAttributePrefix(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getName().getPrefix();
    }

    public String getAttributeType(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getDTDType();
    }

    public String getAttributeValue(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).getValue();
    }

    public boolean isAttributeSpecified(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredAttributes().get(index).isSpecified();
    }

    public int getNamespaceCount() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredNamespaces().size();
            case 2:
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.mo1375asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    namespaceIterator.next();
                    count++;
                }
                return count;
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getNamespacePrefix(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredNamespaces().get(index).getPrefix();
            case 2:
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.mo1375asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    Namespace namespace = namespaceIterator.next();
                    if (count == index) {
                        return namespace.getPrefix();
                    }
                    count++;
                }
                throw new ArrayIndexOutOfBoundsException(index);
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getNamespaceURI(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getOnElementDeclaredNamespaces().get(index).getNamespaceURI();
    }

    public NamespaceContext getNamespaceContext() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.mo1376asStartElement().getNamespaceContext();
    }

    public int getEventType() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent == null) {
            try {
                return next();
            } catch (XMLStreamException e) {
                throw new IllegalStateException((Throwable) e);
            }
        }
        if (xmlSecEvent.isCharacters() && xmlSecEvent.mo1374asCharacters().isIgnorableWhiteSpace()) {
            return 6;
        }
        return xmlSecEvent.getEventType();
    }

    public String getText() {
        EntityReference currentEvent = getCurrentEvent();
        switch (currentEvent.getEventType()) {
            case 4:
            case 6:
            case 12:
                return currentEvent.mo1374asCharacters().getData();
            case 5:
                return ((Comment) currentEvent).getText();
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not TEXT");
            case 9:
                return currentEvent.getDeclaration().getReplacementText();
            case 11:
                return ((DTD) currentEvent).getDocumentTypeDeclaration();
        }
    }

    public char[] getTextCharacters() {
        EntityReference currentEvent = getCurrentEvent();
        switch (currentEvent.getEventType()) {
            case 4:
            case 6:
            case 12:
                return currentEvent.mo1374asCharacters().getText();
            case 5:
                return ((Comment) currentEvent).getText().toCharArray();
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not TEXT");
            case 9:
                return currentEvent.getDeclaration().getReplacementText().toCharArray();
            case 11:
                return ((DTD) currentEvent).getDocumentTypeDeclaration().toCharArray();
        }
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        EntityReference currentEvent = getCurrentEvent();
        switch (currentEvent.getEventType()) {
            case 4:
            case 6:
            case 12:
                currentEvent.mo1374asCharacters().getData().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            case 5:
                ((Comment) currentEvent).getText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not TEXT");
            case 9:
                currentEvent.getDeclaration().getReplacementText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            case 11:
                ((DTD) currentEvent).getDocumentTypeDeclaration().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
        }
    }

    public int getTextStart() {
        return 0;
    }

    public int getTextLength() {
        EntityReference currentEvent = getCurrentEvent();
        switch (currentEvent.getEventType()) {
            case 4:
            case 6:
            case 12:
                return currentEvent.mo1374asCharacters().getData().length();
            case 5:
                return ((Comment) currentEvent).getText().length();
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not TEXT");
            case 9:
                return currentEvent.getDeclaration().getReplacementText().length();
            case 11:
                return ((DTD) currentEvent).getDocumentTypeDeclaration().length();
        }
    }

    public String getEncoding() {
        return this.inputProcessorChain.getDocumentContext().getEncoding();
    }

    public boolean hasText() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return ((1 << xmlSecEvent.getEventType()) & MASK_GET_TEXT) != 0;
    }

    public Location getLocation() {
        return new Location() { // from class: org.apache.xml.security.stax.impl.XMLSecurityStreamReader.1
            public int getLineNumber() {
                return -1;
            }

            public int getColumnNumber() {
                return -1;
            }

            public int getCharacterOffset() {
                return -1;
            }

            public String getPublicId() {
                return null;
            }

            public String getSystemId() {
                return null;
            }
        };
    }

    public QName getName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getName();
            case 2:
                return xmlSecEvent.mo1375asEndElement().getName();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getLocalName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getName().getLocalPart();
            case 2:
                return xmlSecEvent.mo1375asEndElement().getName().getLocalPart();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public boolean hasName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return xmlSecEvent.getEventType() == 1 || xmlSecEvent.getEventType() == 2;
    }

    public String getNamespaceURI() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getName().getNamespaceURI();
            case 2:
                return xmlSecEvent.mo1375asEndElement().getName().getNamespaceURI();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getPrefix() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1:
                return xmlSecEvent.mo1376asStartElement().getName().getPrefix();
            case 2:
                return xmlSecEvent.mo1375asEndElement().getName().getPrefix();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isStandalone() {
        return this.standalone;
    }

    public boolean standaloneSet() {
        return this.standaloneSet;
    }

    public String getCharacterEncodingScheme() {
        return this.characterEncodingScheme;
    }

    public String getPITarget() {
        ProcessingInstruction currentEvent = getCurrentEvent();
        if (currentEvent.getEventType() != 3) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return currentEvent.getTarget();
    }

    public String getPIData() {
        ProcessingInstruction currentEvent = getCurrentEvent();
        if (currentEvent.getEventType() != 3) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return currentEvent.getData();
    }
}
