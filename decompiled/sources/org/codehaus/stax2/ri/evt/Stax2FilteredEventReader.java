package org.codehaus.stax2.ri.evt;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLEventReader2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/Stax2FilteredEventReader.class */
public class Stax2FilteredEventReader implements XMLEventReader2, XMLStreamConstants {
    final XMLEventReader2 mReader;
    final EventFilter mFilter;

    public Stax2FilteredEventReader(XMLEventReader2 r, EventFilter f) {
        this.mReader = r;
        this.mFilter = f;
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
        try {
            return peek() != null;
        } catch (XMLStreamException sex) {
            throw new RuntimeException((Throwable) sex);
        }
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent evt;
        do {
            evt = this.mReader.nextEvent();
            if (evt == null) {
                break;
            }
        } while (!this.mFilter.accept(evt));
        return evt;
    }

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException sex) {
            throw new RuntimeException((Throwable) sex);
        }
    }

    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent evt;
        do {
            evt = this.mReader.nextTag();
            if (evt == null) {
                break;
            }
        } while (!this.mFilter.accept(evt));
        return evt;
    }

    public XMLEvent peek() throws XMLStreamException {
        XMLEvent evt;
        while (true) {
            evt = this.mReader.peek();
            if (evt == null || this.mFilter.accept(evt)) {
                break;
            }
            this.mReader.nextEvent();
        }
        return evt;
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
        return this.mReader.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.XMLEventReader2
    public boolean setProperty(String name, Object value) {
        return this.mReader.setProperty(name, value);
    }
}
