package org.apache.xml.security.stax.impl.stax;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecEventBaseImpl.class */
public abstract class XMLSecEventBaseImpl implements XMLSecEvent {
    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
    protected XMLSecStartElement parentXMLSecStartELement;

    protected static <T> EmptyIterator<T> getEmptyIterator() {
        return EMPTY_ITERATOR;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public void setParentXMLSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXMLSecStartELement = xmlSecStartElement;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public XMLSecStartElement getParentXMLSecStartElement() {
        return this.parentXMLSecStartELement;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public int getDocumentLevel() {
        if (this.parentXMLSecStartELement != null) {
            return this.parentXMLSecStartELement.getDocumentLevel();
        }
        return 0;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public void getElementPath(List<QName> list) {
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getElementPath(list);
        }
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public List<QName> getElementPath() {
        List<QName> elementPath = new ArrayList<>();
        getElementPath(elementPath);
        return elementPath;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    public XMLSecStartElement getStartElementAtLevel(int level) {
        if (getDocumentLevel() < level) {
            return null;
        }
        return this.parentXMLSecStartELement.getStartElementAtLevel(level);
    }

    public Location getLocation() {
        return new LocationImpl();
    }

    public boolean isStartElement() {
        return false;
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asStartElement */
    public XMLSecStartElement mo1376asStartElement() {
        throw new ClassCastException();
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asEndElement */
    public XMLSecEndElement mo1375asEndElement() {
        throw new ClassCastException();
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asCharacters */
    public XMLSecCharacters mo1374asCharacters() {
        throw new ClassCastException();
    }

    public QName getSchemaType() {
        return null;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException("writeAsEncodedUnicode not implemented for " + getClass().getName());
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecEventBaseImpl$LocationImpl.class */
    static final class LocationImpl implements Location {
        LocationImpl() {
        }

        public int getLineNumber() {
            return 0;
        }

        public int getColumnNumber() {
            return 0;
        }

        public int getCharacterOffset() {
            return 0;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecEventBaseImpl$EmptyIterator.class */
    static final class EmptyIterator<E> implements Iterator<E> {
        private EmptyIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public E next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException();
        }
    }
}
