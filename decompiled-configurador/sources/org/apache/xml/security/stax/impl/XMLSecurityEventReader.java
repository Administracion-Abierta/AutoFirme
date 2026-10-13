package org.apache.xml.security.stax.impl;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/XMLSecurityEventReader.class */
public class XMLSecurityEventReader implements XMLEventReader {
    private final Iterator<XMLSecEvent> xmlSecEventIterator;
    private XMLEvent xmlSecEvent;

    public XMLSecurityEventReader(Deque<XMLSecEvent> xmlSecEvents, int fromIndex) {
        this.xmlSecEventIterator = xmlSecEvents.descendingIterator();
        int curIdx = 0;
        while (true) {
            int i = curIdx;
            curIdx++;
            if (i < fromIndex) {
                this.xmlSecEventIterator.next();
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.xmlSecEvent != null) {
            XMLEvent currentXMLEvent = this.xmlSecEvent;
            this.xmlSecEvent = null;
            return currentXMLEvent;
        }
        try {
            XMLEvent currentXMLEvent2 = this.xmlSecEventIterator.next();
            return currentXMLEvent2;
        } catch (NoSuchElementException e) {
            throw new XMLStreamException(e);
        }
    }

    public boolean hasNext() {
        if (this.xmlSecEvent != null) {
            return true;
        }
        return this.xmlSecEventIterator.hasNext();
    }

    public XMLEvent peek() throws XMLStreamException {
        if (this.xmlSecEvent != null) {
            return this.xmlSecEvent;
        }
        try {
            this.xmlSecEvent = this.xmlSecEventIterator.next();
            return this.xmlSecEvent;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public String getElementText() throws XMLStreamException {
        throw new XMLStreamException(new UnsupportedOperationException());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent nextTag() throws XMLStreamException {
        throw new XMLStreamException(new UnsupportedOperationException());
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException(new UnsupportedOperationException());
    }

    public void close() throws XMLStreamException {
    }

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
