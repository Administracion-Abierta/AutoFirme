package org.codehaus.stax2.ri.evt;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLEventReader2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/Stax2EventReaderAdapter.class */
public class Stax2EventReaderAdapter implements XMLEventReader2 {
    protected final XMLEventReader mReader;

    protected Stax2EventReaderAdapter(XMLEventReader er) {
        this.mReader = er;
    }

    public static XMLEventReader2 wrapIfNecessary(XMLEventReader er) {
        if (er instanceof XMLEventReader2) {
            return (XMLEventReader2) er;
        }
        return new Stax2EventReaderAdapter(er);
    }

    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    public String getElementText() throws XMLStreamException {
        return this.mReader.getElementText();
    }

    public Object getProperty(String name) {
        return this.mReader.getProperty(name);
    }

    public boolean hasNext() {
        return this.mReader.hasNext();
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        return this.mReader.nextEvent();
    }

    public Object next() {
        return this.mReader.next();
    }

    public XMLEvent nextTag() throws XMLStreamException {
        return this.mReader.nextTag();
    }

    public XMLEvent peek() throws XMLStreamException {
        return this.mReader.peek();
    }

    public void remove() {
        this.mReader.remove();
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean hasNextEvent() throws XMLStreamException {
        return peek() != null;
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean isPropertySupported(String name) {
        try {
            this.mReader.getProperty(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean setProperty(String name, Object value) {
        return false;
    }
}
