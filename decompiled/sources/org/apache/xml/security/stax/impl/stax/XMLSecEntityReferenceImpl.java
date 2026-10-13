package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityReference;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecEntityReferenceImpl.class */
public class XMLSecEntityReferenceImpl extends XMLSecEventBaseImpl implements XMLSecEntityReference {
    private final String name;
    private final EntityDeclaration entityDeclaration;

    public XMLSecEntityReferenceImpl(String name, EntityDeclaration entityDeclaration, XMLSecStartElement parentXmlSecStartElement) {
        this.name = name;
        this.entityDeclaration = entityDeclaration;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public EntityDeclaration getDeclaration() {
        return this.entityDeclaration;
    }

    public String getName() {
        return this.name;
    }

    public int getEventType() {
        return 9;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isEntityReference() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(getName());
            writer.write(59);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
