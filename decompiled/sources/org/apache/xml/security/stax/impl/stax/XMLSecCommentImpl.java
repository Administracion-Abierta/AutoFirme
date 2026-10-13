package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecComment;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecCommentImpl.class */
public class XMLSecCommentImpl extends XMLSecEventBaseImpl implements XMLSecComment {
    private final String text;

    public XMLSecCommentImpl(String text, XMLSecStartElement parentXmlSecStartElement) {
        this.text = text;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public String getText() {
        return this.text;
    }

    public int getEventType() {
        return 5;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(getText());
            writer.write("-->");
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
