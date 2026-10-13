package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecDTD;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecDTDImpl.class */
public class XMLSecDTDImpl extends XMLSecEventBaseImpl implements XMLSecDTD {
    private final String dtd;

    public XMLSecDTDImpl(String dtd, XMLSecStartElement parentXmlSecStartElement) {
        this.dtd = dtd;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public String getDocumentTypeDeclaration() {
        return this.dtd;
    }

    public Object getProcessedDTD() {
        return null;
    }

    public List getNotations() {
        return Collections.emptyList();
    }

    public List getEntities() {
        return Collections.emptyList();
    }

    public int getEventType() {
        return 11;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(getDocumentTypeDeclaration());
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
