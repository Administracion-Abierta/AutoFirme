package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityDeclaration;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecEntityDeclarationImpl.class */
public class XMLSecEntityDeclarationImpl extends XMLSecEventBaseImpl implements XMLSecEntityDeclaration {
    private String name;

    public XMLSecEntityDeclarationImpl(String name) {
        this.name = name;
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getNotationName() {
        return null;
    }

    public String getReplacementText() {
        return null;
    }

    public String getBaseURI() {
        return null;
    }

    public int getEventType() {
        return 15;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isEntityReference() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(getName());
            writer.write(" \"");
            String replacementText = getReplacementText();
            if (replacementText != null) {
                writer.write(replacementText);
            }
            writer.write("\">");
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
