package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/stax/XMLSecEndElementImpl.class */
public class XMLSecEndElementImpl extends XMLSecEventBaseImpl implements XMLSecEndElement {
    private final QName elementName;

    public XMLSecEndElementImpl(QName elementName, XMLSecStartElement parentXmlSecStartElement) {
        this.elementName = elementName;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public QName getName() {
        return this.elementName;
    }

    public Iterator getNamespaces() {
        return getEmptyIterator();
    }

    public int getEventType() {
        return 2;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isEndElement() {
        return true;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asEndElement */
    public XMLSecEndElement mo457asEndElement() {
        return this;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            String prefix = getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(getName().getPrefix());
                writer.write(58);
            }
            writer.write(getName().getLocalPart());
            writer.write(62);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
