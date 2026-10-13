package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEvent2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/Stax2EventWriterImpl.class */
public class Stax2EventWriterImpl implements XMLEventWriter, XMLStreamConstants {
    protected final XMLStreamWriter2 _writer;

    public Stax2EventWriterImpl(XMLStreamWriter2 sw) {
        this._writer = sw;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void add(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
            case 1:
                StartElement se = event.asStartElement();
                QName n = se.getName();
                this._writer.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
                Iterator<?> it = se.getNamespaces();
                while (it.hasNext()) {
                    add((XMLEvent) it.next());
                }
                Iterator<?> it2 = se.getAttributes();
                while (it2.hasNext()) {
                    add((XMLEvent) it2.next());
                }
                return;
            case 2:
                this._writer.writeEndElement();
                return;
            case 3:
                ProcessingInstruction pi = (ProcessingInstruction) event;
                this._writer.writeProcessingInstruction(pi.getTarget(), pi.getData());
                return;
            case 4:
                Characters ch = event.asCharacters();
                String text = ch.getData();
                if (ch.isCData()) {
                    this._writer.writeCData(text);
                    return;
                } else {
                    this._writer.writeCharacters(text);
                    return;
                }
            case 5:
                this._writer.writeComment(((Comment) event).getText());
                return;
            case 6:
            case 14:
            case 15:
            default:
                if (event instanceof XMLEvent2) {
                    ((XMLEvent2) event).writeUsing(this._writer);
                    return;
                }
                throw new XMLStreamException("Don't know how to output event " + event);
            case 7:
                StartDocument sd = (StartDocument) event;
                if (!sd.encodingSet()) {
                    this._writer.writeStartDocument(sd.getVersion());
                    return;
                } else if (sd.standaloneSet()) {
                    this._writer.writeStartDocument(sd.getVersion(), sd.getCharacterEncodingScheme(), sd.isStandalone());
                    return;
                } else {
                    this._writer.writeStartDocument(sd.getCharacterEncodingScheme(), sd.getVersion());
                    return;
                }
            case 8:
                this._writer.writeEndDocument();
                return;
            case 9:
                this._writer.writeEntityRef(((EntityReference) event).getName());
                return;
            case 10:
                Attribute attr = (Attribute) event;
                QName name = attr.getName();
                this._writer.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
                return;
            case 11:
                this._writer.writeDTD(((DTD) event).getDocumentTypeDeclaration());
                return;
            case 12:
                this._writer.writeCData(event.asCharacters().getData());
                return;
            case 13:
                Namespace ns = (Namespace) event;
                this._writer.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
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
        this._writer.close();
    }

    public void flush() throws XMLStreamException {
        this._writer.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this._writer.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this._writer.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this._writer.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext ctxt) throws XMLStreamException {
        this._writer.setNamespaceContext(ctxt);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this._writer.setPrefix(prefix, uri);
    }
}
