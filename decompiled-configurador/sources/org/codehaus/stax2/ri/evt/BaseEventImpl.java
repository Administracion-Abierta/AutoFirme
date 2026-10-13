package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEvent2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/BaseEventImpl.class */
public abstract class BaseEventImpl implements XMLEvent2 {

    @Deprecated
    protected final Location mLocation;

    public abstract int getEventType();

    public abstract void writeAsEncodedUnicode(Writer writer) throws XMLStreamException;

    @Override // org.codehaus.stax2.evt.XMLEvent2
    public abstract void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException;

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    protected BaseEventImpl(Location loc) {
        this.mLocation = loc;
    }

    public Characters asCharacters() {
        return (Characters) this;
    }

    public EndElement asEndElement() {
        return (EndElement) this;
    }

    public StartElement asStartElement() {
        return (StartElement) this;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public QName getSchemaType() {
        return null;
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isStartElement() {
        return false;
    }

    public String toString() {
        return "[Stax Event #" + getEventType() + "]";
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwFromIOE(IOException ioe) throws XMLStreamException {
        throw new XMLStreamException(ioe.getMessage(), ioe);
    }

    protected static boolean stringsWithNullsEqual(String s1, String s2) {
        if (s1 == null || s1.length() == 0) {
            return s2 == null || s2.length() == 0;
        }
        return s2 != null && s1.equals(s2);
    }

    protected static boolean iteratedEquals(Iterator<?> it1, Iterator<?> it2) {
        if (it1 == null || it2 == null) {
            return it1 == it2;
        }
        while (it1.hasNext()) {
            if (!it2.hasNext()) {
                return false;
            }
            Object o1 = it1.next();
            Object o2 = it2.next();
            if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    protected static int addHash(Iterator<?> it, int baseHash) {
        int hash = baseHash;
        if (it != null) {
            while (it.hasNext()) {
                hash ^= it.next().hashCode();
            }
        }
        return hash;
    }
}
