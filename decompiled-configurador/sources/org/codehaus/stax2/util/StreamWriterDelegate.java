package org.codehaus.stax2.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/util/StreamWriterDelegate.class */
public class StreamWriterDelegate implements XMLStreamWriter {
    protected XMLStreamWriter mDelegate;

    public StreamWriterDelegate(XMLStreamWriter parentWriter) {
        this.mDelegate = parentWriter;
    }

    public void setParent(XMLStreamWriter parentWriter) {
        this.mDelegate = parentWriter;
    }

    public XMLStreamWriter getParent() {
        return this.mDelegate;
    }

    public void close() throws XMLStreamException {
        this.mDelegate.close();
    }

    public void flush() throws XMLStreamException {
        this.mDelegate.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mDelegate.getNamespaceContext();
    }

    public String getPrefix(String ns) throws XMLStreamException {
        return this.mDelegate.getPrefix(ns);
    }

    public Object getProperty(String pname) throws IllegalArgumentException {
        return this.mDelegate.getProperty(pname);
    }

    public void setDefaultNamespace(String ns) throws XMLStreamException {
        this.mDelegate.setDefaultNamespace(ns);
    }

    public void setNamespaceContext(NamespaceContext nc) throws XMLStreamException {
        this.mDelegate.setNamespaceContext(nc);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.mDelegate.setPrefix(prefix, uri);
    }

    public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1);
    }

    public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1, arg2);
    }

    public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1, arg2, arg3);
    }

    public void writeCData(String arg0) throws XMLStreamException {
        this.mDelegate.writeCData(arg0);
    }

    public void writeCharacters(String arg0) throws XMLStreamException {
        this.mDelegate.writeCharacters(arg0);
    }

    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
        this.mDelegate.writeCharacters(arg0, arg1, arg2);
    }

    public void writeComment(String arg0) throws XMLStreamException {
        this.mDelegate.writeComment(arg0);
    }

    public void writeDTD(String arg0) throws XMLStreamException {
        this.mDelegate.writeDTD(arg0);
    }

    public void writeDefaultNamespace(String arg0) throws XMLStreamException {
        this.mDelegate.writeDefaultNamespace(arg0);
    }

    public void writeEmptyElement(String arg0) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0);
    }

    public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0, arg1);
    }

    public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0, arg1, arg2);
    }

    public void writeEndDocument() throws XMLStreamException {
        this.mDelegate.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        this.mDelegate.writeEndElement();
    }

    public void writeEntityRef(String arg0) throws XMLStreamException {
        this.mDelegate.writeEntityRef(arg0);
    }

    public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeNamespace(arg0, arg1);
    }

    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(arg0);
    }

    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(arg0, arg1);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.mDelegate.writeStartDocument();
    }

    public void writeStartDocument(String arg0) throws XMLStreamException {
        this.mDelegate.writeStartDocument(arg0);
    }

    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeStartDocument(arg0, arg1);
    }

    public void writeStartElement(String arg0) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0);
    }

    public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0, arg1);
    }

    public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0, arg1, arg2);
    }
}
